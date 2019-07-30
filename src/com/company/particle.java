package com.company;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class particle {
	private double[] pos;			// Stores position(x, y components in array) of particle
	private double[] vel;			// Stores velocity(x, y components in array) of particle
	private double mass;				// Stores mass of particle
	private double radius;				// Stores radius of particle

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
	}

	public void collide(particle that)
	{
		if(this.vel.length != that.vel.length)
			throw new IllegalArgumentException(" Two particles are in different dimensional spaces");

		double mass_diff = this.mass - that.mass;
		double mass_sum  = this.mass + that.mass;
		for(int i = 0; i < vel.length; i++)
		{
			double thisvel = ((2 * that.mass * that.vel[i]) + (mass_diff * this.vel[i]))/mass_sum;
			double thatvel = ((2 * this.mass * this.vel[i]) - (mass_diff * that.vel[i]))/mass_sum;
			this.vel[i] = thisvel;
			that.vel[i] = thatvel;
		}
		while(this.isCollide(that))
			that.move(0.01);
	}

	public void move(double dt)
	{
		for(int i = 0; i < pos.length; i++)
		{
			if (Math.abs(pos[i]) >= 10 - radius)
				vel[i] *= -1;
			pos[i] += vel[i] * dt;
		}
	}

	public void draw()
	{
		if(pos.length > 2)
			throw new IllegalArgumentException(" Only till 2 dimensions can be drawn as of now.");

		if(pos.length == 1)
			StdDraw.point(pos[0],0);
		else
			//StdDraw.point(pos[0], pos[1]);
			StdDraw.filledCircle(pos[0], pos[1], radius);
	}

	public String toString()
	{
		String str = "";
		str += " Dimension : " + pos.length + "\n";
		str += " Mass      : " + mass + "\n";
		str += " Radius    : " + radius + "\n";
		str += " Position  : " + "\n";
		for(Double d : pos)
			str += d.toString() + " , ";
		str += "\n";
		str += " Velocity  : " + "\n";
		for(Double v : vel)
			str += v.toString() + " , ";
		str += "\n";
		return str;
	}

	public double distanceTo(particle that)
	{
		double dist = 0;
		for(int i = 0; i < pos.length; i++)
		{
			dist += Math.pow(this.pos[i] - that.pos[i], 2);
		}
		dist = Math.sqrt(dist);
		return dist;
	}

	public boolean isCollide(particle that)
	{
		if(this.distanceTo(that) <= this.radius + that.radius)
			return true;
		return false;
	}
}