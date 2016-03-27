package com.taraxippus.go.util;

import com.taraxippus.go.game.gameobject.*;
import java.util.*;

public class Ray
{
    public final VectorF point = new VectorF();
    public final VectorF direction = new VectorF();

    public final VectorF tmp1 = new VectorF();

	public static final VectorF MIN = new VectorF(-0.5F, -0.5F, -0.5F);
	public static final VectorF MAX = new VectorF(0.5F, 0.5F, 0.5F);
	
	public float distance;
	public final VectorF normal = new VectorF();
	public final VectorF intersection = new VectorF();
	
    public Ray() {}

    public Ray(VectorF point, VectorF direction)
    {
        this.point.set(point);
        this.direction.set(direction);
        this.direction.normalize();
    }

    public Ray(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        this.point.set(x1, y1, z1);
        this.direction.set(x2, y2, z2);
        this.direction.normalize();
    }

    public boolean intersects(Plane p, VectorF result)
    {
        final float DotProduct = direction.dot(p.normal);

        if (DotProduct == 0)
        {
            return false;
        }

        final float distance = p.normal.dot(tmp1.set(p.point).subtract(point)) / DotProduct;

        if (Float.isNaN(distance) || distance < 0)
        {
            return false;
        }

        result.set(getPoint(distance));
		normal.set(p.normal);
        return true;
    }

    public SceneObject intersectsFirst(ArrayList<SceneObject> entities)
    {
        float distance = this.distance = 10000;
		final VectorF normal = new VectorF();
        SceneObject first = null;

        for (SceneObject entity : entities)
        {
            if (entity.touchable && this.intersects(entity.modelMatrix, entity.invModelMatrix) && this.distance < distance)
            {
                distance = this.distance;
				normal.set(this.normal);
                first = entity;
            }
        }

        this.distance = distance;
		this.normal.set(normal);

        intersection.set(getPoint(distance));
        return first;
    }
	
	public boolean intersects(float[] matrix, float[] invMatrix)
    {
		return intersects(matrix, invMatrix, MIN, MAX, false);
	}
	
	public boolean intersects(float[] matrix, float[] invMatrix, boolean inverse)
    {
		return intersects(matrix, invMatrix, MIN, MAX, inverse);
	}

    public boolean intersects(float[] matrix, float[] invMatrix, VectorF min, VectorF max, boolean inverse)
    {
        float t1, t2;
        float tMin = -10000;
        float tMax = 10000;

        VectorF point;
        VectorF point2;
        VectorF direction;

        if (matrix == null)
        {
            point = this.point;
            direction = this.direction;
        }
        else
        {
            point = this.point.copy().multiplyBy(invMatrix);
            point2 = this.point.copy().add(this.direction).multiplyBy(invMatrix);
            direction = point.copy().subtract(point2).normalize();
        }
		
		normal.set(VectorF.zero);

        if (direction.x != 0.0)
        {
            t1 = (min.x - point.x) / direction.x;
            t2 = (max.x - point.x) / direction.x;

			if (inverse && tMin < Math.min(t1, t2))
                normal.set(t1 < t2 ? 1 : -1, 0, 0);
			else if (!inverse && tMax > Math.max(t1, t2))
				normal.set(t1 < t2 ? 1 : -1, 0, 0);
			
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        if (direction.y != 0.0)
        {
            t1 = (min.y - point.y) / direction.y;
            t2 = (max.y - point.y) / direction.y;

			if (inverse && tMin < Math.min(t1, t2))
                normal.set(0, t1 < t2 ? 1 : -1, 0);
			else if (!inverse && tMax > Math.max(t1, t2))
				normal.set(0, t1 < t2 ? 1 : -1, 0);
			
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        if (direction.z != 0.0)
        {
            t1 = (min.z - point.z) / direction.z;
            t2 = (max.z - point.z) / direction.z;

			if (inverse && tMin < Math.min(t1, t2))
                normal.set(0, 0, t1 < t2 ? 1 : -1);
			else if (!inverse && tMax > Math.max(t1, t2))
				normal.set(0, 0, t1 < t2 ? 1 : -1);
				
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }

        distance = tMax;
		
		if (matrix != null)
		{
			tmp1.set(direction).multiplyBy(distance).add(point);
			tmp1.multiplyBy(matrix);
			intersection.set(tmp1);
			
			tmp1.subtract(this.point);
			distance = tmp1.length();
		}
		else
		{
			intersection.set(getPoint(distance));
		}
		
		normal.roundInt();
		
        return tMax >= tMin;
    }

    public VectorF getPoint(float t)
    {
        return tmp1.set(direction).multiplyBy(t).add(point);
    }
}
