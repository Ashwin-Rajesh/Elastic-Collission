package com.company;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.util.Random;

public class simulator {
	private particle[] balls;					// Stores all balls/particles that are being simulated
	private wall[] walls;						// Stores all walls in simulation
	private MinPQ<collision> cols;				// This data structure stores data of all collisions, sorted based on time
	private double time;						// Stores time elapsed since start of simulation
	private double nextTime;
												// This class is used to store data of collisions.
	private class collision implements Comparable<collision>
	{
		private final int w;					// The first 3 values store indices for 2 particles and a wall.
		private final int a;					// If 2 particles are involved, w is -1
		private final int b;					// If wall and particle are involved b is -1

		private final int acol,bcol;			// These store no. of collisions of particles when collision is predicted (for use in isValid())
		private final double timeTo;			// Stores time to collision from beginning of simulation

												// isWall is true when a wall, and particle collide, else false
		public collision(int a, int b, boolean isWall, double currTime)
		{
			this.a = a;
			this.acol = balls[a].getCount();
			if(isWall)
			{
				this.b = -1;
				this.bcol = -1;
				this.w = b;
				this.timeTo = currTime + balls[a].timeToHit(walls[w]);
			}
			else
			{
				this.w = -1;
				this.b = b;
				this.bcol = balls[b].getCount();
				this.timeTo = currTime + balls[a].timeToHit(balls[b]);
			}
			StdOut.println(" Collision added : " + this.a + " , " + this.b + " , " + this.w);
		}

												// Comparison is done on the basis of predicted time
		public int compareTo(collision that)
		{
			if(this.timeTo > that.timeTo)
				return 1;
			if(this.timeTo < that.timeTo)
				return -1;
			return 0;
		}

												// Compares value of collision count during prediction and during access to check if a collision happened in between
		public boolean isValid()
		{
			if(b == -1)
			{
				if(balls[a].getCount() == acol)
					return true;
				else
					return false;
			}
			else
			{
				if(balls[a].getCount() == acol && balls[b].getCount() == bcol)
					return true;
				else
					return false;
			}
		}

												// Calls the collide() function of one particle.
		public void collide()
		{
			if(b == -1) {
				StdOut.println(" Point " + a + " collide with wall " + w);
				balls[a].collide(walls[w]);
			}
			else
			{
				StdOut.println(" Point " + a + " collide with point " + b);
				balls[a].collide(balls[b]);
			}
		}
	}

	public simulator(particle[] a_balls, wall[] a_walls)
	{
		if(a_balls == null || a_walls == null)
			throw new IllegalArgumentException(" Null arguments were passed.");

		balls = a_balls.clone();
		walls = a_walls.clone();
		time = 0;
		cols = new MinPQ<>();

		for(int i = 0; i < balls.length; i++)
		{
			for(int j = 0; j < walls.length; j++)
			{
				if(balls[i].timeToHit(walls[j]) != Double.POSITIVE_INFINITY)
					cols.insert(new collision(i, j, true, time));
			}
			for(int j = i + 1; j < balls.length; j++)
			{
				if(balls[i].timeToHit(balls[j]) != Double.POSITIVE_INFINITY)
					cols.insert(new collision(i, j, false, time));
			}
		}

		collision firstCol = cols.delMin();
		nextTime = firstCol.timeTo;
		cols.insert(firstCol);
	}

	public void proceed(double dt)
	{
		if(cols.isEmpty())
		{
			StdOut.println(" EMPTY");
			StdDraw.clear(Color.BLACK);
			return;
		}

		if(time + dt >= nextTime)
		{
			dt = nextTime - time;

			collision currCol = cols.delMin();
			if(currCol.timeTo != nextTime)
				StdOut.println(" Disrepancy! " + currCol.timeTo + " " + nextTime);
			currCol.collide();

			time += dt;

			predict(currCol.a);

			if(currCol.b != -1)
			{
				predict(currCol.b);
			}

			move(dt);
			collision nextCol = cols.delMin();
			while(!nextCol.isValid() && !cols.isEmpty())
				nextCol = cols.delMin();
			nextTime = nextCol.timeTo;

			StdOut.println("Moved for "+ dt + " units. Times : " + time + " , " + nextTime + "\n Final points :");
			for(particle p : balls)
				StdOut.println(p.toString());
			return;
		}
		else
		{
			time += dt;
			move(dt);
			StdOut.println("Moved for "+ dt + " units. Times : " + time + " , " + nextTime + "\n Final points :");
			for(particle p : balls)
				StdOut.println(p.toString());
		}

	}

	private void predict(int a)
	{
		for(int i = 0; i < balls.length; i++)
			if(balls[i].timeToHit(balls[a]) != Double.POSITIVE_INFINITY)
				cols.insert(new collision(a, i, false, time));

		for(int i = 0; i < walls.length; i++)
			if(balls[a].timeToHit(walls[i]) != Double.POSITIVE_INFINITY)
				cols.insert(new collision(a, i, true, time));
	}

	private void move(double dt)
	{
		for(particle p : balls)
			p.move(dt);
	}

	public void draw()
	{
		for(wall w : walls)
			w.draw();

		for(particle p : balls)
			p.draw();
	}

	public static void main(String[] args)
	{
		int num = 1;
		int w_num = 4;
		int bx_size = 10;

		particle prts[] = new particle[num];
		wall wls[] = new wall[w_num];

		for(int k = 0; k < num; k++) {
			Random rand = new Random();
			double[] pos = new double[2];
			double[] vel = new double[2];
			pos[1] = ((double) rand.nextInt(200) - 100) / 10;
			pos[0] = ((double) rand.nextInt(200) - 100) / 10;
			vel[0] = ((double) rand.nextInt(10) - 5) / 10;
			vel[1] = ((double) rand.nextInt(10) - 5) / 10;
			int mass = 10 + rand.nextInt(20) / 2;
			double radius = Math.sqrt(mass) / 10;
			prts[k] = new particle(pos, vel, mass, radius);
		}

		StdOut.println("\n" + prts[0].toString());

		wls[0] = new wall(new double[]{bx_size,0}, new double[]{1,0});
		wls[1] = new wall(new double[]{0,bx_size}, new double[]{0,1});
		wls[2] = new wall(new double[]{-bx_size,0}, new double[]{1,0});
		wls[3] = new wall(new double[]{0,-bx_size}, new double[]{0,1});

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-11, 11);
		StdDraw.setYscale(-11, 11);
		StdDraw.setPenRadius(0.01);

		simulator sim = new simulator(prts,wls);

		while(true)
		{
			StdDraw.clear();
			sim.draw();
			StdDraw.show();
			sim.proceed(0.5);
			StdDraw.pause(10);
			while(!StdDraw.isMousePressed())
				StdDraw.pause();
		}
	}
}