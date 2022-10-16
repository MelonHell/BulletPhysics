package ru.melonhell.bulletphysics.bullet.collision.body.shape;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Triangle {
    private final Vector3f[] vertices;
    private final Vector3f centroid;
    private final Vector3f area;

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3) {
        this.vertices = new Vector3f[]{v1, v2, v3};
        this.centroid = new Vector3f().add(v1).add(v2).add(v3).divideLocal(3.0f);

        final var e1 = v1.subtract(v2);
        final var e2 = v2.subtract(v3);

        this.area = e2.cross(e1).multLocal(0.5f);
        this.area.multLocal(Math.signum(centroid.dot(area))); // make sure it faces outward
    }

    public static List<Triangle> getMeshOf(BoundingBox boundingBox) {
        final var triangles = new ArrayList<Triangle>();

        for (var quad : getQuads(boundingBox)) {
//            final var centroid = new Vector3f();
//
//            for (var point : quad.getPoints()) {
//                centroid.addLocal(point);
//            }
//
//            centroid.divideLocal(4);
//
//            triangles.add(new Triangle(quad.getPoints().get(0), centroid, quad.getPoints().get(1)));
//            triangles.add(new Triangle(quad.getPoints().get(1), centroid, quad.getPoints().get(2)));
//            triangles.add(new Triangle(quad.getPoints().get(2), centroid, quad.getPoints().get(3)));
//            triangles.add(new Triangle(quad.getPoints().get(3), centroid, quad.getPoints().get(0)));
            triangles.add(new Triangle(quad.p0(), quad.p1(), quad.p2()));
            triangles.add(new Triangle(quad.p2(), quad.p3(), quad.p0()));
        }

        return triangles;
    }

    private static List<Quad> getQuads(BoundingBox box) {
        Vector3f min = box.getMin(null);
        Vector3f max = box.getMax(null);

        return List.of(
                new Quad(Quad.Side.NORTH,
                        new Vector3f(min.x, max.y, min.z),
                        new Vector3f(max.x, max.y, min.z),
                        new Vector3f(max.x, min.y, min.z),
                        new Vector3f(min.x, min.y, min.z)),
                new Quad(Quad.Side.EAST,
                        new Vector3f(max.x, max.y, min.z),
                        new Vector3f(max.x, max.y, max.z),
                        new Vector3f(max.x, min.y, max.z),
                        new Vector3f(max.x, min.y, min.z)),
                new Quad(Quad.Side.SOUTH,
                        new Vector3f(max.x, max.y, max.z),
                        new Vector3f(min.x, max.y, max.z),
                        new Vector3f(min.x, min.y, max.z),
                        new Vector3f(max.x, min.y, max.z)),
                new Quad(Quad.Side.WEST,
                        new Vector3f(min.x, max.y, max.z),
                        new Vector3f(min.x, max.y, min.z),
                        new Vector3f(min.x, min.y, min.z),
                        new Vector3f(min.x, min.y, max.z)),
                new Quad(Quad.Side.UP,
                        new Vector3f(max.x, max.y, min.z),
                        new Vector3f(min.x, max.y, min.z),
                        new Vector3f(min.x, max.y, max.z),
                        new Vector3f(max.x, max.y, max.z)),
                new Quad(Quad.Side.DOWN,
                        new Vector3f(max.x, min.y, max.z),
                        new Vector3f(min.x, min.y, max.z),
                        new Vector3f(min.x, min.y, min.z),
                        new Vector3f(max.x, min.y, min.z)
                ));
    }

    private static Vector3f transform(Vector3f vector, Quaternion rotation) {
        Quaternion quaternion = new Quaternion(rotation);
        quaternion.multLocal(new Quaternion(vector.x, vector.y, vector.z, 0.0F));
        Quaternion quaternion2 = new Quaternion(-rotation.getX(), -rotation.getY(), -rotation.getZ(), rotation.getW());
        quaternion.multLocal(quaternion2);
        return new Vector3f(quaternion.getX(), quaternion.getY(), quaternion.getZ());
    }

    public Vector3f[] getVertices() {
        return this.vertices;
    }

    public Vector3f getCentroid() {
        return this.centroid;
    }

    public Vector3f getArea() {
        return this.area;
    }

    public Triangle transform(Quaternion quaternion) {
        return new Triangle(
                transform(vertices[0].clone(), quaternion),
                transform(vertices[1].clone(), quaternion),
                transform(vertices[2].clone(), quaternion));
    }
}