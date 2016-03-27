package com.taraxippus.go.util;

public class Plane
{
    public final VectorF normal = new VectorF();
    public final VectorF point = new VectorF();
    final VectorF tmp = new VectorF();
    public double d;

    public Plane() {}

    public Plane(VectorF normal, VectorF point)
    {
        set(normal, point);
    }

    public Plane set(VectorF normal, VectorF point)
    {
        this.normal.set(normal).normalize();
        this.point.set(point);
        this.d = tmp.set(normal).normalize().negate().dot(point);
        return this;
    }

    public Plane set(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        this.normal.set(x1, y1, z1).normalize();
        this.point.set(x2, y2, z2);
        this.d = tmp.set(normal).normalize().negate().dot(point);
        return this;
    }

    public Plane set(VectorF normal, float d)
    {
        this.normal.set(normal).normalize();
        this.d = d / normal.length();

        return this;
    }

    public double distance(VectorF point)
    {
        return tmp.set(point).dot(normal) + d;
    }

    @Override
    public String toString()
    {
        return "Normal: " + normal + ", " + "Point: " + point;
    }
}
