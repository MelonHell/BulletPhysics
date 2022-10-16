package ru.melonhell.bulletphysics.bullet.collision.body.shape;

import com.jme3.math.Vector3f;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

public record Quad(Side side, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
    public List<Vector3f> getPoints() {
        return List.of(this.p0, this.p1, this.p2, this.p3);
    }

    @Getter
    @RequiredArgsConstructor
    public enum Side {
        NORTH(0, 0, -1),
        EAST(1, 0, 0),
        SOUTH(0, 0, 1),
        WEST(-1, 0, 0),
        UP(0, 1, 0),
        DOWN(0, -1, 0);

        private final int x;
        private final int y;
        private final int z;

        public Vector3f toVector() {
            return new Vector3f(x, y, z);
        }
    }
}
