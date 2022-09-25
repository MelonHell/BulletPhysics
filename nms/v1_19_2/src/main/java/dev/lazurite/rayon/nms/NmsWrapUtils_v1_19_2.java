package dev.lazurite.rayon.nms;

import com.mojang.math.Quaternion;
import dev.lazurite.rayon.nms.wrappers.AABBWrapper;
import dev.lazurite.rayon.nms.wrappers.FluidStateWrapper;
import dev.lazurite.rayon.nms.wrappers.QuaternionWrapper;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

@UtilityClass
public class NmsWrapUtils_v1_19_2 {
    public AABB unwrap(AABBWrapper aabb) {
        return new AABB(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ(), aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ(), true);
    }

    public AABBWrapper wrap(AABB aabb) {
        return new AABBWrapper(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public Quaternion unwrap(QuaternionWrapper quaternion) {
        return new Quaternion(quaternion.getI(), quaternion.getJ(), quaternion.getK(), quaternion.getR());
    }

    public QuaternionWrapper wrap(Quaternion quaternion) {
        return new QuaternionWrapper(quaternion.i(), quaternion.j(), quaternion.k(), quaternion.r());
    }

    public FluidState unwrap(FluidStateWrapper fluidState) {
        return (FluidState) fluidState.getHandle();
    }

    public FluidStateWrapper wrap(FluidState fluidState) {
        return new FluidStateWrapper(fluidState, fluidState.isEmpty());
    }
}
