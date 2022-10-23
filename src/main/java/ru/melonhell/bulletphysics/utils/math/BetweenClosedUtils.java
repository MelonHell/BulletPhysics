package ru.melonhell.bulletphysics.utils.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import lombok.experimental.UtilityClass;
import ru.melonhell.bulletphysics.nms.wrappers.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class BetweenClosedUtils {
    public List<BlockPos> betweenClosed(BoundingBox box) {
        List<BlockPos> result = new ArrayList<>();
        betweenClosed(box, result);
        return result;
    }

    public void betweenClosed(BoundingBox box, Collection<BlockPos> result) {
        Vector3f min = box.getMin(null);
        Vector3f max = box.getMax(null);
        betweenClosed((int) Math.floor(min.x), (int) Math.floor(min.y), (int) Math.floor(min.z), (int) Math.floor(max.x), (int) Math.floor(max.y), (int) Math.floor(max.z), result);
    }

    public List<BlockPos> betweenClosed(int startX, int startY, int startZ, int endX, int endY, int endZ) {
        List<BlockPos> result = new ArrayList<>();
        betweenClosed(startX, startY, startZ, endX, endY, endZ, result);
        return result;
    }

    public void betweenClosed(int startX, int startY, int startZ, int endX, int endY, int endZ, Collection<BlockPos> result) {
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                for (int z = startZ; z < endZ; z++) {
                    result.add(new BlockPos(x, y, z));
                }
            }
        }
    }
}
