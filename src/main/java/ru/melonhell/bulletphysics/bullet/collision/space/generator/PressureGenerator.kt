package ru.melonhell.bulletphysics.bullet.collision.space.generator

import com.jme3.bounding.BoundingBox
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import org.springframework.stereotype.Component
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos
import ru.melonhell.bulletphysics.utils.math.Convert.toBullet
import kotlin.math.exp
import kotlin.math.sign

/**
 * TODO This class is messy af.
 */
@Component
class PressureGenerator {
    companion object{
        const val STOPPING_TIME = 2f // number of time steps to apply slamming force on fluid collision

        // Fluid-Related Constants
        const val WATER_DENSITY = 1000f // kg/m^3

        // Gas-Related Constants
        const val AIR_DENSITY = 1.225f // kg/m^3
        const val GAS_CONSTANT = 8.31446f // J/(mol·K)
        const val MOLAR_MASS_OF_AIR = 0.0289644f // kg/mol
        const val SEA_LEVEL_PRESSURE = 101325f // Pa
        const val TEMPERATURE = 300f // K
        const val SEA_LEVEL = 62 // m
    }

    fun step(space: MinecraftSpace) {
        val blockCache = space.blockCache
        val fluidCache = space.fluidCache
        val timeStep = space.accuracy
        val gravity = space.getGravity(Vector3f())
        val location = Vector3f()
        val linearVelocity = Vector3f()
        val angularVelocity = Vector3f()
        val rotation = Quaternion()
        for (elementRigidBodyData in space.physicsElements) {
            val rigidBody = elementRigidBodyData.rigidBody
            if (!rigidBody.isActive || elementRigidBodyData.buoyancyType == PhysicsElement.BuoyancyType.NONE && elementRigidBodyData.dragType == PhysicsElement.DragType.NONE) {
                elementRigidBodyData.sleepTimer.reset()
                continue
            }
            rigidBody.getPhysicsLocation(location)
            rigidBody.getLinearVelocity(linearVelocity)
            rigidBody.getAngularVelocity(angularVelocity)
            rigidBody.getPhysicsRotation(rotation)
            if (linearVelocity.length() < rigidBody.linearSleepingThreshold && angularVelocity.length() < rigidBody.angularSleepingThreshold) {
                if (elementRigidBodyData.sleepTimer.get() > PhysicsElement.SLEEP_TIME_IN_SECONDS) {
                    rigidBody.deactivationTime = 2.0f
                    continue
                }
            } else {
                elementRigidBodyData.sleepTimer.reset()
            }
            val mass = rigidBody.mass
            val density = mass / elementRigidBodyData.minecraftShape.volume
            val dragCoefficient = elementRigidBodyData.dragCoefficient
            val triangles = elementRigidBodyData.minecraftShape.getTriangles(rotation)
            val crossSectionalAreas = HashMap<Triangle, Float>()
            val submergedTriangles = ArrayList<Triangle>()
            var totalCrossSectionalArea = 0.0f
            for (triangle in triangles) {
                val centroid = triangle.centroid
                val area = triangle.area
                val tangentialVelocity =
                    Vector3f(angularVelocity).cross(centroid) // angular velocity converted to linear parallel to edge of circle (tangential)
                val netVelocity =
                    Vector3f(tangentialVelocity).addLocal(linearVelocity) // total linear + tangential velocity
                if (sign(netVelocity.dot(area)) == 1f) {
                    val crossSectionalArea = netVelocity.normalize().dot(area)
                    crossSectionalAreas[triangle] = crossSectionalArea
                    totalCrossSectionalArea += crossSectionalArea
                }
                val blockPos = BlockPos(
                    (location.x + centroid.x).toInt(),
                    (location.y + centroid.y).toInt(),
                    (location.z + centroid.z).toInt()
                )
                val posRelativeToBlockCenter = Vector3f(centroid).add(location).subtract(toBullet(blockPos))
                val fluidColumn = fluidCache.getFluidColumn(blockPos)
                if (fluidColumn != null) {
                    val waterHeight =
                        fluidColumn.top.block.y + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y
                    if (waterHeight > 0.0f) {
                        submergedTriangles.add(triangle)
                    }
                }
            }
            val totalArea = totalCrossSectionalArea
            val addedMassAdjustment = if (density < 50) getAddedMassForceAdjustment(submergedTriangles, mass) else 1.0f
            for (triangle in triangles) {
                val centroid = triangle.centroid
                val area = triangle.area
                val blockPos = BlockPos(
                    (location.x + centroid.x).toInt(),
                    (location.y + centroid.y).toInt(),
                    (location.z + centroid.z).toInt()
                )
                if (submergedTriangles.contains(triangle)) {
                    val posRelativeToBlockCenter = Vector3f(centroid).add(location).subtract(toBullet(blockPos))
                    val fluidColumn = fluidCache.getFluidColumn(blockPos)
                    var waterHeight = 0.0f
                    if (fluidColumn != null) {
                        waterHeight =
                            fluidColumn.top.block.y.toFloat() + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y
                    }
                    val fluidColumn1 =
                        fluidCache.getFluidColumn(BlockPos(location.x.toInt(), location.y.toInt(), location.z.toInt()))
                    if (fluidColumn1 != null) {
                        val flowForce = Vector3f(fluidColumn1.flow)
                        if (java.lang.Float.isFinite(flowForce.lengthSquared()) && flowForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(flowForce, centroid)
                        }
                    }

                    /* Do water buoyancy */
                    if (elementRigidBodyData.isWaterBuoyancyEnabled) {
                        /* Check to make sure the triangle centroid is actually submerged */
                        val pressure = gravity.y * WATER_DENSITY * waterHeight
                        val buoyantForce = Vector3f(area).multLocal(pressure)
                        if (java.lang.Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(buoyantForce.multLocal(addedMassAdjustment), centroid)
                        }
                    }

                    /* Do water drag */if (elementRigidBodyData.isWaterDragEnabled) {
                        val tangentialVelocity =
                            Vector3f(angularVelocity).cross(centroid) // angular velocity converted to linear parallel to edge of circle (tangential)
                        val netVelocity =
                            Vector3f(tangentialVelocity).addLocal(linearVelocity) // total linear + tangential velocity
                        if (sign(netVelocity.dot(area)) == 1f) {
                            val dragForce =
                                Vector3f(area).multLocal(-0.5f * dragCoefficient * WATER_DENSITY * netVelocity.lengthSquared())
                            dragForce.multLocal(-1.0f * sign(netVelocity.dot(dragForce)))

                            /* This stopping force is how we prevent objects from entering orbit upon touching water :( */
                            val stoppingForce =
                                Vector3f(netVelocity).multLocal(-1.0f * rigidBody.mass * crossSectionalAreas[triangle]!! / totalArea)
                                    .divideLocal(timeStep)

                            /* So if the stopping force is smaller, we apply that instead. */if (dragForce.length() < stoppingForce.length()) {
                                rigidBody.applyForce(dragForce.multLocal(addedMassAdjustment), centroid)
                            } else {
                                rigidBody.applyForce(stoppingForce.divideLocal(STOPPING_TIME), centroid)
                            }
                        }
                    }
                } else {
                    // TODO this is rly borky
//                            if (rigidBody.isAirBuoyancyEnabled()) {
//                                final var pressure = (float) (SEA_LEVEL_PRESSURE * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));
//                                final var buoyantForce = new Vector3f(area).multLocal(pressure);
//
//                                if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
//                                    rigidBody.applyForce(buoyantForce, centroid);
//                                }
//                            }

                    /* Do (complex) air drag */
                    if (elementRigidBodyData.isAirDragEnabled) {
                        /* air_density_at_sea_level * e^(gravity * molar_mass_of_air * sea_level / (gas_constant * temperature)) */
                        /* 1.2 * e^(-9.8 * 0.0289644 * 62 / (8.3144598 * 300) */
                        val airDensity =
                            (AIR_DENSITY * exp((MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)).toDouble())).toFloat()
                        val tangentialVelocity =
                            Vector3f(angularVelocity).cross(centroid) // angular velocity converted to linear parallel to edge of circle (tangential)
                        val netVelocity =
                            Vector3f(tangentialVelocity).addLocal(linearVelocity) // total linear + tangential velocity
                        if (sign(netVelocity.dot(area)) == 1f) {
                            val dragForce =
                                Vector3f(area).multLocal(-0.5f * dragCoefficient * airDensity * netVelocity.lengthSquared())
                            dragForce.multLocal(-1.0f * sign(netVelocity.dot(dragForce))) // make sure all the vectors are facing the same way
                            if (java.lang.Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                                rigidBody.applyForce(dragForce, centroid)
                            }
                        }
                    }
                }
            }

            /* Do (simple) air drag */if (elementRigidBodyData.dragType == PhysicsElement.DragType.SIMPLE) {
                val box = rigidBody.collisionShape.boundingBox(Vector3f(), Quaternion(), BoundingBox())
                val area = box.getExtent(Vector3f()).lengthSquared()
                val dragForce =
                    Vector3f(linearVelocity.normalize()).multLocal(-0.5f * area * dragCoefficient * AIR_DENSITY * linearVelocity.lengthSquared())
                if (java.lang.Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                    rigidBody.applyCentralForce(dragForce)
                }
            }
        }
    }

    fun getAddedMassForceAdjustment(triangles: List<Triangle>, mass: Float): Float {
        val n = triangles.size
        val sum = triangles.stream()
            .mapToDouble { triangle: Triangle -> (triangle.area.length() * triangle.centroid.length()).toDouble() }
            .sum()
        val addedMass = WATER_DENSITY / (6 * n) * sum
        return (mass / (mass + addedMass)).toFloat()
    }
}