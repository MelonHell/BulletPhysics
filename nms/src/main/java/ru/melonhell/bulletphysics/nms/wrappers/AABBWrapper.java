package ru.melonhell.bulletphysics.nms.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.Vector;

import javax.annotation.concurrent.Immutable;

@Immutable
@Getter
@RequiredArgsConstructor
public class AABBWrapper {
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public AABBWrapper(BlockPosWrapper pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public boolean intersects(AABBWrapper box) {
        return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }

    public AABBWrapper inflate(double value) {
        return inflate(value, value, value);
    }

    public AABBWrapper inflate(double x, double y, double z) {
        double d = this.minX - x;
        double e = this.minY - y;
        double f = this.minZ - z;
        double g = this.maxX + x;
        double h = this.maxY + y;
        double i = this.maxZ + z;
        return new AABBWrapper(d, e, f, g, h, i);
    }

    public double getXsize() {
        return this.maxX - this.minX;
    }

    public double getYsize() {
        return this.maxY - this.minY;
    }

    public double getZsize() {
        return this.maxZ - this.minZ;
    }

    public Vector getCenter() {
        double x = (this.minX + this.maxX) / 2.0;
        double y = (this.minY + this.maxY) / 2.0;
        double z = (this.minZ + this.maxZ) / 2.0;
        return new Vector(x, y, z);
    }
}
