package ru.melonhell.bulletphysics.utils.math;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BoundingBoxUtils {
    public void inflate(BoundingBox boundingBox, float value) {
        inflate(boundingBox, value, value, value);
    }
    public void inflate(BoundingBox boundingBox, float x, float y, float z) {
        boundingBox.setXExtent(boundingBox.getXExtent() + x);
        boundingBox.setYExtent(boundingBox.getYExtent() + y);
        boundingBox.setZExtent(boundingBox.getZExtent() + z);
    }

    public boolean intersects(BoundingBox boundingBox1, BoundingBox boundingBox2) {
        Vector3f min1 = boundingBox1.getMin(null);
        Vector3f max1 = boundingBox1.getMax(null);
        Vector3f min2 = boundingBox2.getMin(null);
        Vector3f max2 = boundingBox2.getMax(null);
        return min1.x < max2.x && max1.x > min2.x && min1.y < max2.y && max1.y > min2.y && min1.z < max2.z && max1.z > min2.z;
    }

    public void clearCenter(BoundingBox boundingBox) {
        float xExtent = boundingBox.getXExtent();
        float yExtent = boundingBox.getYExtent();
        float zExtent = boundingBox.getZExtent();

        boundingBox.setMinMax(new Vector3f(-xExtent, -yExtent, -zExtent), new Vector3f(xExtent, yExtent, zExtent));
    }
}
