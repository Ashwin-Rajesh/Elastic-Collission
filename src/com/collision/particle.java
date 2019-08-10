package com.collision;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Arrays;

public class particle {
	private double[] pos;				// Stores position(x, y components in array) of particle
	private double[] vel;				// Stores velocity(x, y components in array) of particle
	private double mass;				// Stores mass of particle
	private double radius;				// Stores radius of particle
	private int colCount;				// Stores number of collisions
	private Color color;				// Stores a color that ball will be displayed in

	public particle(double[] pos, double[] vel, double mass, double radius, Color color)
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
		this.color = color;
	}

	public boolean equals(Object obj)
	{
		if(this == obj)								return true;
		if(this.getClass() != obj.getClass())		return false;
		particle that = (particle) obj;
		if(Arrays.equals(pos, that.pos))
			if(Arrays.equals(pos, that.vel))
				if(mass == that.mass)
					if(radius == that.radius)
						return true;
		return false;
	}

	void collide(particle that)
	{
		/*for(int i = 0; i < vel.length; i++)
			StdOut.println(this.vel[i] + " , " +  that.vel[i]);
		StdOut.println("Mass : " + this.mass + " : " + that.mass);
		*/
		/*
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
		this.colCount++;*/

		double mass_sum = this.mass + that.mass;
		for(int i = 0; i < vel.length; i++)
		{
			double temp_vel = ((2 * that.mass * that.vel[i]) + this.vel[i]*(this.mass - that.mass))/mass_sum;
			that.vel[i] = ((2 * this.mass * this.vel[i]) + that.vel[i]*(that.mass - this.mass))/mass_sum;
			this.vel[i] = temp_vel;
		}

		this.colCount++;
		that.colCount++;

		/*for(int i = 0; i < vel.length; i++)
			StdOut.println(this.vel[i] + " , " +  that.vel[i]);

		StdOut.println(" collision processed.");*/
	}

	void collide(wall that)
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

		/*while(this.isCollide(that))
			this.move(0.01);*/
	}

	void move(double dt)
	{
		for(int i = 0; i < pos.length; i++)
		{
			//if (Math.abs(pos[i]) >= 10 - radius)
			//	vel[i] *= -1;
			pos[i] += vel[i] * dt;
		}
	}

	void draw()
	{
		if(pos.length > 2)
			throw new IllegalArgumentException(" Only till 2 dimensions can be drawn as of now.");

		StdDraw.setPenColor(color);

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
		for(int i = 0; i < pos.length; i++)
			str.append(pos[i] + " , ");
		str.append("\n");

		str.append(" Velocity  : ");
		for(int i = 0; i < vel.length; i++)
			str.append(vel[i] + " , ");
		str.append("\n");

		return str.toString();
	}

	double distanceTo(particle that)
	{
		double dist = 0;
		for(int i = 0; i < pos.length; i++)
		{
			dist += Math.pow(this.pos[i] - that.pos[i], 2);
		}
		dist = Math.sqrt(dist);
		return Math.abs(dist);
	}

	double distanceTo(wall that)
	{
		double[] normal = that.getNormal();
		double[] point = that.getPoint();
		int dimension = pos.length;

		if(normal.length != dimension)
			throw new IllegalArgumentException(" The particle and wall have different number of dimensions");

		double[] dist = new double[dimension];

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

	double timeToHit(particle that)
	{
		if(that.equals(this))
			return Double.POSITIVE_INFINITY;

		int dimension = pos.length;
		double[] dv = new double[dimension];
		double[] dx = new double[dimension];
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

		double d = (dxdv * dxdv) - (dvdv) * (dxdx - Math.pow((this.radius + that.radius), 2));

		if(d < 0)
			return Double.POSITIVE_INFINITY;

		return -(dxdv + Math.sqrt(d))/dvdv;
	}

	double timeToHit(wall that)
	{
		if(that.getNormal().length != pos.length)
			throw new IllegalArgumentException(" The wall and point have different number of dimensions.");

		double[] normal = that.getNormal();
		double veldot = 0;
		int dimensions = pos.length;
		double[] point = that.getPoint();
		double disdot = 0;

		for(int i = 0; i < dimensions; i++)
		{
			veldot += vel[i] * normal[i];
			double dx = pos[i] - point[i];
			disdot += dx * normal[i];
		}

		if(disdot * veldot >= 0) {
			return Double.POSITIVE_INFINITY;
		}
		else {
			return Math.abs((Math.abs(disdot) - radius) / veldot);
		}
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

	int getCount()
	{
		return colCount;
	}
}