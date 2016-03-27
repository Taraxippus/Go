package com.taraxippus.go.util;

import android.opengl.*;
import java.nio.*;

public class VectorF
{
    public float x, y, z;
    public static final VectorF zero = new VectorF();

    public VectorF(double x, double y, double z)
    {
        set(x, y, z);
    }

    public VectorF(int[] vec)
    {
        set(vec);
    }

    public VectorF(float[] vec)
    {
        set(vec);
    }

    public VectorF(double[] vec)
    {
        set(vec);
    }

    public VectorF(VectorF other)
    {
        this.set(other);
    }


    public VectorF()
    {
    }

    public VectorF add(VectorF other)
    {
        x += other.x;
        y += other.y;
        z += other.z;
        return this;
    }

    public VectorF add(float x, float y, float z)
    {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public VectorF subtract(VectorF other)
    {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        return this;
    }

    public VectorF subtract(float x, float y, float z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public VectorF multiplyBy(float d)
    {
        x *= d;
        y *= d;
        z *= d;
        return this;
    }

    public VectorF multiplyBy(VectorF other)
    {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }

    public VectorF multiplyBy(float x, float y, float z)
    {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    static final float[] tmpVector0 = new float[] {0, 0, 0, 0};
    static final float[] tmpVector1 = new float[] {0, 0, 0, 1};
    static final float[] tmpVector2 = new float[] {0, 0, 0, 1};

    public VectorF multiplyBy(float[] matrix, int offset)
    {
        this.getVec4(tmpVector1);
		Matrix.multiplyMV(tmpVector2, 0, matrix, offset, tmpVector1, 0);
        
        if (tmpVector2[3] == 0)
            this.set(tmpVector2[0], tmpVector2[1], tmpVector2[2]);
        else
            this.set(tmpVector2[0] / tmpVector2[3], tmpVector2[1] / tmpVector2[3], tmpVector2[2] / tmpVector2[3]);

        return this;
    }

    public VectorF multiplyBy(float[] matrix, int offset, boolean direction)
    {
        if (direction)
            this.getVec4(tmpVector0);
        else
            this.getVec4(tmpVector1);

        Matrix.multiplyMV(tmpVector2, 0, matrix, offset, direction ?  tmpVector0 :tmpVector1, 0);
        
        if (tmpVector2[3] == 0)
            this.set(tmpVector2[0], tmpVector2[1], tmpVector2[2]);
        else
            this.set(tmpVector2[0] / tmpVector2[3], tmpVector2[1] / tmpVector2[3], tmpVector2[2] / tmpVector2[3]);

        return this;
    }

    public VectorF multiplyBy(float[] matrix)
    {
        this.getVec4(tmpVector1);
        Matrix.multiplyMV(tmpVector2, 0, matrix, 0, tmpVector1, 0);
        
        if (tmpVector2[3] == 0)
            this.set(tmpVector2[0], tmpVector2[1], tmpVector2[2]);
        else
            this.set(tmpVector2[0] / tmpVector2[3], tmpVector2[1] / tmpVector2[3], tmpVector2[2] / tmpVector2[3]);

        return this;
    }

    public VectorF multiplyBy(float[] matrix, boolean direction)
	{
        if (direction)
            this.getVec4(tmpVector0);
        else
            this.getVec4(tmpVector1);

        Matrix.multiplyMV(tmpVector2, 0, matrix, 0, direction ?  tmpVector0 :tmpVector1, 0);
        
        if (tmpVector2[3] == 0)
            this.set(tmpVector2[0], tmpVector2[1], tmpVector2[2]);
        else
            this.set(tmpVector2[0] / tmpVector2[3], tmpVector2[1] / tmpVector2[3], tmpVector2[2] / tmpVector2[3]);

        return this;
    }

    public VectorF divideBy(VectorF other)
    {
        x /= other.x;
        y /= other.y;
        z /= other.z;
        return this;
    }

    public VectorF divideBy(float x, float y, float z)
    {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public VectorF divideBy(float d)
    {
        x /= d;
        y /= d;
        z /= d;
        return this;
    }

    public float dot(float x, float y, float z)
    {
        return x * this.x + y * this.y + z * this.z;
    }

    public float dot(VectorF other)
    {
        return x * other.x + y * other.y + z * other.z;
    }

    public VectorF cross(float x1, float y1, float z1)
    {
        float x, y, z;
        x = this.y * z1 - this.z * y1;
        y = this.z * x1 - this.x * z1;
        z = this.x * y1 - this.y * x1;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF cross(VectorF other)
    {
        float x, y, z;
        x = this.y * other.z - this.z * other.y;
        y = this.z * other.x - this.x * other.z;
        z = this.x * other.y - this.y * other.x;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF negate()
    {
        this.x *= -1;
        this.y *= -1;
        this.z *= -1;

        return this;
    }

    public VectorF absolute()
    {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);

        return this;
    }

    public VectorF round()
    {
        this.x = (int)(this.x * 1000) / 1000F;
        this.y = (int)(this.y * 1000) / 1000F;
        this.z = (int)(this.z * 1000) / 1000F;

        return this;
    }
	
	public VectorF roundInt()
    {
        this.x = Math.round(this.x);
        this.y = Math.round(this.y);
        this.z = Math.round(this.z);

        return this;
    }

    public VectorF floor()
    {
        this.x = (float)Math.floor(this.x);
        this.y = (float)Math.floor(this.y);
        this.z = (float)Math.floor(this.z);

        return this;
    }

    public float length()
    {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public float lengthSquared()
    {
        return x * x + y * y + z * z;
    }

    public VectorF normalize()
    {
        this.divideBy(length());
        return this;
    }

    public boolean normalized()
    {
        return lengthSquared() == 1;
    }

    public VectorF approach(VectorF start, VectorF goal, float percent)
    {
        x = start.x + (goal.x - start.x) * percent;
        y = start.y + (goal.y - start.y) * percent;
        z = start.z + (goal.z - start.z) * percent;

        return this;
    }

    public VectorF rotateX(float x1)
    {
		x1 = (float) Math.toRadians(x1);
		
        float x, y, z;
        x = this.x;
        y = (float) (this.y * Math.cos(x1) - this.z * Math.sin(x1));
        z = (float) (this.z * Math.cos(x1) + this.y * Math.sin(x1));
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF rotateX2(float x1)
    {
		x1 = (float) Math.toRadians(x1);
		
        float x, y, z;
        x = this.x;
        y = this.y;
        z = (float) (this.z * Math.cos(x1) + this.y * Math.sin(x1));
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF rotateY(float y1)
    {
		y1 = (float) Math.toRadians(y1);
		
        float x, y, z;
        x = (float) (this.z * Math.sin(y1) + this.x * Math.cos(y1));
        y = this.y;
        z = (float) (this.z * Math.cos(y1) - this.x * Math.sin(y1));
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF rotateZ(float z1)
    {
		z1 = (float) Math.toRadians(z1);
		
        float x, y, z;
        x = (float) (this.x * Math.cos(z1) + this.y * -Math.sin(z1));
        y = (float) (this.x * -Math.sin(z1) + this.y * Math.cos(z1));
        z = this.z;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public VectorF toEuler()
    {
        while (x < 0)
            x += 360;

        x = x % 360;

        while (y < 0)
            y += 360;

        y = y % 360;

        while (z < 0)
            z += 360;

        z = z % 360;

        return this;
    }

    public float[] getVec41()
    {
        return new float[] {x, y, z, 1};
    }

    public float[] getVec40()
    {
        return new float[] {x, y, z, 0};
    }

    public float[] getVec4(float[] vec4)
    {
        vec4[0] = x;
        vec4[1] = y;
        vec4[2] = z;
        return vec4;
    }

    public VectorF copy()
    {
        return new VectorF(x, y, z);
    }

    public VectorF set(VectorF vector)
    {
        x = vector.x;
        y = vector.y;
        z = vector.z;

        return this;
    }

    public VectorF set(int[] vector)
    {
        return set(vector[0], vector[1], vector[2]);
    }

    public VectorF set(float[] vector)
    {
        return set(vector[0], vector[1], vector[2]);
    }

    public VectorF set(double[] vector)
    {
        return set(vector[0], vector[1], vector[2]);
    }

    public VectorF set(double x, double y, double z)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;

        return this;
    }

    public float get(int offset)
    {
        return offset == 0 ? x : (offset == 1 ? y : z);
    }

    public VectorF load(ByteBuffer buffer)
    {
        return this.set(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
    }

    public VectorF save(ByteBuffer buffer)
    {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);

        return this;
    }

    @Override
    public String toString()
    {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof VectorF && ((VectorF)o).x == x && ((VectorF)o).y == y && ((VectorF)o).z == z;
    }

    public boolean equals(float x, float y, float z)
    {
        return this.x == x && this.y == y && this.z == z;
    }

    public boolean equals(float x, float y, float z, float epsilon)
    {
        return Math.abs(this.x - x) < epsilon && Math.abs(this.y - y) < epsilon && Math.abs(this.z - z) < epsilon;
    }

    public boolean opposite(Object o)
    {
        return o instanceof VectorF && ((VectorF)o).x == -x && ((VectorF)o).y == -y && ((VectorF)o).z == -z;
    }

    public boolean opposite(float x, float y, float z)
    {
        return this.x == -x && this.y == -y && this.z == -z;
    }

    @Override
    public int hashCode()
    {
        return Float.valueOf(x).hashCode() ^ Float.valueOf(y).hashCode() ^ Float.valueOf(z).hashCode();
    }

    public int getByteCount()
    {
        return Float.SIZE / 8 * 3;
    }
}
