package ru.melonhell.bulletphysics.nms;

import lombok.experimental.UtilityClass;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import ru.melonhell.bulletphysics.nms.wrappers.AABBWrapper;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

@UtilityClass
public class NmsWrapUtils_v1_19_2 {
    public AABB unwrap(AABBWrapper aabb) {
        return new AABB(aabb.getMinX(), aabb.getMinY(), aabb.getMinZ(), aabb.getMaxX(), aabb.getMaxY(), aabb.getMaxZ(), true);
    }

    public AABBWrapper wrap(AABB aabb) {
        return new AABBWrapper(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public FluidState unwrap(FluidStateWrapper fluidState) {
        return (FluidState) fluidState.getHandle();
    }

    public FluidStateWrapper wrap(FluidState fluidState) {
        return new FluidStateWrapper(fluidState, fluidState.isEmpty());
    }
}
