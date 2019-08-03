package com.company;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class particle {
	private double[] pos;				// Stores position(x, y components in array) of particle
	private double[] vel;				// Stores velocity(x, y components in array) of particle
	private double mass;				// Stores mass of particle
	private double radius;				// Stores radius of particle
	private int colCount;				// Stores number of collisions

	public particle(double[] pos, double[] vel, double mass, double radius)
	{
		if(pos == null || vel == null)
			throw new IllegalArgumentException(" Null arguments passed");

		if(pos.length != vel.length)
			throw new IllegalArgumentException(" Position, velocity vectors do not have same dimnensions");

		this.pos = pos.clone();
		this.vel = vel.clone();
		this.mass = mass;
		this.radius = radius;
		this.colCount = 0;
	}

	public boolean equals(Object obj)
	{
		if(this == obj)								return true;
		if(this.getClass() != obj.getClass())		return false;
		particle that = (particle) obj;
		if(pos.equals(that.pos))
			if(vel.equals(that.vel))
				if(mass == that.mass)
					if(radius == that.radius)
						return true;
		return false;
	}

	public void collide(particle that)
	{
		int dimension = pos.length;
		double dv[] = new double[dimension];
		double dx[] = new double[dimension];
		double dvdx = 0;
		for(int i = 0; i < dimension; i++)
		{
			dx[i] = this.pos[i] - that.pos[i];
			dv[i] = this.vel[i] - that.vel[i];
			dvdx += dx[i] * dv[i];
		}
		double J = (2 * this.mass * that.mass * dvdx)/((this.mass + that.mass) * (this.radius + that.radius));
		double[] j = new double[dimension];					// This stands for impulse transferred along an axis
		for(int i = 0; i < dimension; i++)
		{
			j[i] = J * dx[i] / (that.radius + this.radius);
			this.vel[i] -= j[i] / this.mass;
			that.vel[i] += j[i] / that.mass;
		}

		that.colCount++;
		this.colCount++;

		while(this.isCollide(that))
			that.move(0.01);
	}

	public void collide(wall that)
	{
		double[] normal = that.getNormal();
		int dimension = pos.length;

		if(normal.length != dimension)
			throw new IllegalArgumentException(" The wall and point have different number of dimensions.");

		double veldot = 0;
		for(int i = 0; i < dimension; i++)
		{
			veldot += normal[i] * vel[i];
		}

		for(int i = 0; i < dimension; i++)
		{
			vel[i] -= 2 * normal[i] * veldot;
		}

		this.colCount++;

		while(this.isCollide(that))
			this.move(0.01);
	}

	public void move(double dt)
	{
		for(int i = 0; i < pos.length; i++)
		{
			//if (Math.abs(pos[i]) >= 10 - radius)
			//	vel[i] *= -1;
			pos[i] += vel[i] * dt;
		}
	}

	public void draw()
	{
		if(pos.length > 2)
			throw new IllegalArgumentException(" Only till 2 dimensions can be drawn as of now.");

		if(pos.length == 1)
			StdDraw.filledCircle(pos[0],0, radius);
		else
			//StdDraw.point(pos[0], pos[1]);
			StdDraw.filledCircle(pos[0], pos[1], radius);
	}

	public String toString()
	{
		StringBuilder str = new StringBuilder();
		int dimension = pos.length;

		str.append("\n");
		str.append(" Dimension : " + dimension + "\n");
		str.append(" Mass      : " + mass + "\n");
		str.append(" Radius    : " + radius + "\n");
		str.append(" Position  : ");
		for(double p : pos)
			str.append(p + " , ");
		str.append("\n");

		str.append(" Velocity  : ");
		for(double v : vel)
			str.append(v + " , ");
		str.append("\n");

		return str.toString();
	}

	public double distanceTo(particle that)
	{
		double dist = 0;
		for(int i = 0; i < pos.length; i++)
		{
			dist += Math.pow(this.pos[i] - that.pos[i], 2);
		}
		dist = Math.sqrt(dist);
		return Math.abs(dist);
	}

	public double distanceTo(wall that)
	{
		double[] normal = that.getNormal();
		double[] point = that.getPoint();
		int dimension = pos.length;

		if(normal.length != dimension)
			throw new IllegalArgumentException(" The particle and wall have different number of dimensions");

		double dist[] = new double[dimension];

		for (int i = 0; i < dimension; i++)
		{
			dist[i] = point[i] - pos[i];
		}

		double distance = 0;

		for(int i = 0; i < dimension; i++)
		{
			distance += dist[i] * normal[i];
		}

		return Math.abs(distance) - radius;
	}

	public double timeToHit(particle that)
	{
		if(that.equals(this))
			return Double.POSITIVE_INFINITY;

		int dimension = pos.length;
		double dv[] = new double[dimension];
		double dx[] = new double[dimension];
		double dxdv = 0;
		double dvdv = 0;
		double dxdx = 0;

		for(int i = 0; i < dimension; i++)
		{
			dv[i] = this.vel[i] - that.vel[i];
			dx[i] = this.pos[i] - that.pos[i];
			dxdv += dv[i] * dx[i];
			dxdx += dx[i] * dx[i];
			dvdv += dv[i] * dv[i];
		}

		if(dxdv >= 0)
			return Double.POSITIVE_INFINITY;

		double d = (dxdv * dxdv) - (dvdv) * (dxdx - (this.radius + that.radius));

		if(d < 0)
			return Double.POSITIVE_INFINITY;

		return -(dxdv + Math.sqrt(d))/dvdv;
	}

	public double timeToHit(wall that)
	{
		if(that.getNormal().length != pos.length)
			throw new IllegalArgumentException(" The wall and point have different number of dimensions.");

		double normal[] = that.getNormal();
		double veldot = 0;
		int dimensions = pos.length;
		double point[] = that.getPoint();
		double disdot = 0;

		for(int i = 0; i < dimensions; i++)
		{
			veldot += vel[i] * normal[i];
			double dx = pos[i] + point[i];
			disdot += dx * normal[i];
		}

		if(disdot * veldot >= 0)
			return Double.POSITIVE_INFINITY;
		else
			return -(disdot - radius) / veldot;
	}

	public boolean isCollide(particle that)
	{
		if(this.distanceTo(that) <= this.radius + that.radius)
			return true;
		return false;
	}

	public boolean isCollide(wall that)
	{
		if(this.distanceTo(that) <= radius)
			return true;
		return false;
	}

	public int getCount()
	{
		return colCount;
	}
}