package ru.melonhell.bulletphysics.nms;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import ru.melonhell.bulletphysics.nms.wrappers.FluidStateWrapper;

@UtilityClass
public class NmsWrapUtils_v1_19_2 {
    public AABB convert(BoundingBox boundingBox) {
        Vector3f min = boundingBox.getMin(null);
        Vector3f max = boundingBox.getMax(null);
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public BoundingBox convert(AABB aabb) {
        return new BoundingBox(new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ), new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));
    }

    public FluidState unwrap(FluidStateWrapper fluidState) {
        return (FluidState) fluidState.getHandle();
    }

    public FluidStateWrapper wrap(FluidState fluidState) {
        return new FluidStateWrapper(fluidState, fluidState.isEmpty());
    }
}
