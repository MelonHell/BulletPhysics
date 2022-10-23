package ru.melonhell.bulletphysics.bullet.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.springframework.stereotype.Component;
import ru.melonhell.bulletphysics.bullet.collision.body.element.PhysicsElement;
import ru.melonhell.bulletphysics.bullet.collision.body.shape.Triangle;
import ru.melonhell.bulletphysics.bullet.collision.space.MinecraftSpace;
import ru.melonhell.bulletphysics.bullet.collision.space.cache.data.FluidColumn;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;
import ru.melonhell.bulletphysics.utils.math.Convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO This class is messy af.
 */
@Component
public class PressureGenerator {
    public final float STOPPING_TIME = 2; // number of time steps to apply slamming force on fluid collision

    // Fluid-Related Constants
    public final float WATER_DENSITY = 1000f;       // kg/m^3

    // Gas-Related Constants
    public final float AIR_DENSITY = 1.225f;           // kg/m^3
    public final float GAS_CONSTANT = 8.3144598f;      // J/(mol·K)
    public final float MOLAR_MASS_OF_AIR = 0.0289644f; // kg/mol
    public final float SEA_LEVEL_PRESSURE = 101_325f;  // Pa
    public final float TEMPERATURE = 300;              // K
    public final int SEA_LEVEL = 62;                   // m

    public void step(MinecraftSpace space) {
        final var chunkCache = space.getBlockCache();
        final var timeStep = space.getAccuracy();
        final var gravity = space.getGravity(new Vector3f());

        final var location = new Vector3f();
        final var linearVelocity = new Vector3f();
        final var angularVelocity = new Vector3f();
        final var rotation = new Quaternion();

        for (var elementRigidBodyData : space.getPhysicsElements()) {
            PhysicsRigidBody rigidBody = elementRigidBodyData.getRigidBody();
            if (!rigidBody.isActive() || (elementRigidBodyData.getBuoyancyType() == PhysicsElement.BuoyancyType.NONE && elementRigidBodyData.getDragType() == PhysicsElement.DragType.NONE)) {
                elementRigidBodyData.getSleepTimer().reset();
                continue;
            }

            rigidBody.getPhysicsLocation(location);
            rigidBody.getLinearVelocity(linearVelocity);
            rigidBody.getAngularVelocity(angularVelocity);
            rigidBody.getPhysicsRotation(rotation);

            if (linearVelocity.length() < rigidBody.getLinearSleepingThreshold() && angularVelocity.length() < rigidBody.getAngularSleepingThreshold()) {
                if (elementRigidBodyData.getSleepTimer().get() > PhysicsElement.SLEEP_TIME_IN_SECONDS) {
                    rigidBody.setDeactivationTime(2.0f);
                    continue;
                }
            } else {
                elementRigidBodyData.getSleepTimer().reset();
            }

            final var mass = rigidBody.getMass();
            final var density = mass / elementRigidBodyData.getMinecraftShape().getVolume();
            final var dragCoefficient = elementRigidBodyData.getDragCoefficient();

            final var triangles = elementRigidBodyData.getMinecraftShape().getTriangles(rotation);
            final var crossSectionalAreas = new HashMap<Triangle, Float>();
            final var submergedTriangles = new ArrayList<Triangle>();
            float totalCrossSectionalArea = 0.0f;

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();
                final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                if (Math.signum(netVelocity.dot(area)) == 1) {
                    final var crossSectionalArea = netVelocity.normalize().dot(area);
                    crossSectionalAreas.put(triangle, crossSectionalArea);
                    totalCrossSectionalArea += crossSectionalArea;
                }

                final var blockPos = new BlockPos(
                        (int) (location.x + centroid.x),
                        (int) (location.y + centroid.y),
                        (int) (location.z + centroid.z));

                final var posRelativeToBlockCenter = new Vector3f(centroid).add(location).subtract(Convert.toBullet(blockPos));

                FluidColumn fluidColumn = chunkCache.getFluidColumn(blockPos);
                if (fluidColumn != null) {
                    final var waterHeight = fluidColumn.getTop().block().getY() + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y;

                    if (waterHeight > 0.0f) {
                        submergedTriangles.add(triangle);
                    }
                }
            }

            final var totalArea = totalCrossSectionalArea;
            final var addedMassAdjustment = density < 50 ? getAddedMassForceAdjustment(submergedTriangles, mass) : 1.0f;

            for (var triangle : triangles) {
                final var centroid = triangle.getCentroid();
                final var area = triangle.getArea();

                final var blockPos = new BlockPos(
                        (int) (location.x + centroid.x),
                        (int) (location.y + centroid.y),
                        (int) (location.z + centroid.z));

                if (submergedTriangles.contains(triangle)) {
                    final var posRelativeToBlockCenter = new Vector3f(centroid).add(location).subtract(Convert.toBullet(blockPos));

                    FluidColumn fluidColumn = chunkCache.getFluidColumn(blockPos);
                    var waterHeight = 0.0f;
                    if (fluidColumn != null) {
                        waterHeight = (float) fluidColumn.getTop().block().getY() + fluidColumn.getTopHeight(posRelativeToBlockCenter) - location.y - centroid.y;
                    }

                    FluidColumn fluidColumn1 = chunkCache.getFluidColumn(new BlockPos((int) location.x, (int) location.y, (int) location.z));
                    if (fluidColumn1 != null) {
                        final var flowForce = new Vector3f(fluidColumn1.getFlow());

                        if (Float.isFinite(flowForce.lengthSquared()) && flowForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(flowForce, centroid);
                        }
                    }

                    /* Do water buoyancy */
                    if (elementRigidBodyData.isWaterBuoyancyEnabled()) {
                        /* Check to make sure the triangle centroid is actually submerged */
                        final var pressure = gravity.y * WATER_DENSITY * waterHeight;
                        final var buoyantForce = new Vector3f(area).multLocal(pressure);

                        if (Float.isFinite(buoyantForce.lengthSquared()) && buoyantForce.lengthSquared() > 0.0f) {
                            rigidBody.applyForce(buoyantForce.multLocal(addedMassAdjustment), centroid);
                        }
                    }

                    /* Do water drag */
                    if (elementRigidBodyData.isWaterDragEnabled()) {
                        final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                        final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                        if (Math.signum(netVelocity.dot(area)) == 1) {
                            final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * WATER_DENSITY * netVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce)));

                            /* This stopping force is how we prevent objects from entering orbit upon touching water :( */
                            final var stoppingForce = new Vector3f(netVelocity).multLocal(-1.0f * rigidBody.getMass() * crossSectionalAreas.get(triangle) / totalArea).divideLocal(timeStep);

                            /* So if the stopping force is smaller, we apply that instead. */
                            if (dragForce.length() < stoppingForce.length()) {
                                rigidBody.applyForce(dragForce.multLocal(addedMassAdjustment), centroid);
                            } else {
                                rigidBody.applyForce(stoppingForce.divideLocal(STOPPING_TIME), centroid);
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
                    if (elementRigidBodyData.isAirDragEnabled()) {
                        /* air_density_at_sea_level * e^(gravity * molar_mass_of_air * sea_level / (gas_constant * temperature)) */
                        /* 1.2 * e^(-9.8 * 0.0289644 * 62 / (8.3144598 * 300) */
                        final var airDensity = (float) (AIR_DENSITY * Math.exp(MOLAR_MASS_OF_AIR * gravity.y * (SEA_LEVEL - location.y - centroid.y) / (GAS_CONSTANT * TEMPERATURE)));

                        final var tangentialVelocity = new Vector3f(angularVelocity).cross(centroid); // angular velocity converted to linear parallel to edge of circle (tangential)
                        final var netVelocity = new Vector3f(tangentialVelocity).addLocal(linearVelocity); // total linear + tangential velocity

                        if (Math.signum(netVelocity.dot(area)) == 1) {
                            final var dragForce = new Vector3f(area).multLocal(-0.5f * dragCoefficient * airDensity * netVelocity.lengthSquared());
                            dragForce.multLocal(-1.0f * Math.signum(netVelocity.dot(dragForce))); // make sure all the vectors are facing the same way

                            if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                                rigidBody.applyForce(dragForce, centroid);
                            }
                        }
                    }
                }
            }

            /* Do (simple) air drag */
            if (elementRigidBodyData.getDragType() == PhysicsElement.DragType.SIMPLE) {
                final var box = rigidBody.getCollisionShape().boundingBox(new Vector3f(), new Quaternion(), new BoundingBox());
                final var area = box.getExtent(new Vector3f()).lengthSquared();
                final var dragForce = new Vector3f(linearVelocity.normalize()).multLocal(-0.5f * area * dragCoefficient * AIR_DENSITY * linearVelocity.lengthSquared());

                if (Float.isFinite(dragForce.lengthSquared()) && dragForce.lengthSquared() > 0.0f) {
                    rigidBody.applyCentralForce(dragForce);
                }
            }
        }
    }

    public float getAddedMassForceAdjustment(List<Triangle> triangles, float mass) {
        final var n = triangles.size();
        final var sum = triangles.stream().mapToDouble(triangle -> triangle.getArea().length() * triangle.getCentroid().length()).sum();
        final var addedMass = WATER_DENSITY / (6 * n) * sum;
        return (float) (mass / (mass + addedMass));
    }
}