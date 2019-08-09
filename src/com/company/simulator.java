package com.company;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Random;

public class simulator {
	private particle[] balls;					// Stores all balls/particles that are being simulated
	private wall[] walls;						// Stores all walls in simulation
	private MinPQ<collision> cols;				// This data structure stores data of all collisions, sorted based on time
	private double time;						// Stores time elapsed since start of simulation
	private double nextTime;
	public int num;
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
			//StdOut.println(" Collision added : " + this.a + " , " + this.b + " , " + this.w);
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
				balls[a].collide(walls[w]);
			}
			else
			{
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
				{
					cols.insert(new collision(i, j, true, time));
				}
			}
			for(int j = i + 1; j < balls.length; j++)
			{
				if(balls[i].timeToHit(balls[j]) != Double.POSITIVE_INFINITY)
					cols.insert(new collision(i, j, false, time));
			}
		}

		if(!cols.isEmpty()) {
			collision firstCol = cols.delMin();
			nextTime = firstCol.timeTo;
			cols.insert(firstCol);
		}
		else
		{
			nextTime = Double.POSITIVE_INFINITY;
		}

		/*StdOut.println(" ============================");
		for(collision c : cols)
		{
			StdOut.println("a - " + c.a + " , b - " + c.b + " , w - " + c.w + " ,Time : " + c.timeTo);
		}
		StdOut.println(" ============================ Initialisation");
		 */
	}

	public void proceed(double dt)
	{
		if(nextTime == Double.POSITIVE_INFINITY)
		{
			move(dt);
			StdDraw.clear();
			StdOut.println("No more collisions");
			nextTime = Double.POSITIVE_INFINITY;
			return;
		}

		if(time + dt >= nextTime)
		{
			collision currCol = cols.delMin();

			double new_dt = nextTime - time;
			time = nextTime;
			move(new_dt);

			num++;
			currCol.collide();

			for(int i = 0; i < balls.length; i++)
				if(balls[i].timeToHit(balls[currCol.a]) != Double.POSITIVE_INFINITY)
				{
					cols.insert(new collision(currCol.a, i, false, time));
				}

			for(int i = 0; i < walls.length; i++)
				if(balls[currCol.a].timeToHit(walls[i]) != Double.POSITIVE_INFINITY)
				{
					cols.insert(new collision(currCol.a, i, true, time));
				}

			if(currCol.b != -1)
			{
				for(int i = 0; i < balls.length; i++)
					if(balls[i].timeToHit(balls[currCol.b]) != Double.POSITIVE_INFINITY && i!= currCol.a)
					{
						cols.insert(new collision(currCol.b, i, false, time));
					}

				for(int i = 0; i < walls.length; i++)
					if(balls[currCol.b].timeToHit(walls[i]) != Double.POSITIVE_INFINITY)
					{
						cols.insert(new collision(currCol.b, i, true, time));
					}

			}

			if(!cols.isEmpty()) {
				collision nextCol = cols.delMin();
				while (!nextCol.isValid() && !cols.isEmpty())
					nextCol = cols.delMin();
				nextTime = nextCol.timeTo;
				cols.insert(nextCol);
			}
			else
			{
				nextTime = Double.POSITIVE_INFINITY;
			}

			proceed(dt - new_dt);

			// Remove '/* and */ below to see collisions stored in the Priority Queue.
			/*
			StdOut.println(" ============================");
			for(collision c : cols)
			{
				if(c.isValid())
					StdOut.println("a - " + c.a + " , b - " + c.b + " , w - " + c.w + " ,Time : " + c.timeTo);
			}
			StdOut.println(" ============================");

			StdOut.println(" next collision : " + nextCol.a + " " + nextCol.b + " " + nextCol.w +" t : " + nextCol.timeTo);
			*/
			return;
		}
		else
		{
			time += dt;
			move(dt);
			//StdOut.println("Moved for "+ dt + " units. Times : " + time + " , " + nextTime);
		}

	}

	private void move(double dt)
	{
		for(particle p : balls)
			p.move(dt);
	}

	public void draw()
	{
		StdDraw.setPenColor(Color.BLACK);
		for(wall w : walls)
			w.draw();

		for(particle p : balls)
			p.draw();
	}

	public static void main(String[] args)
	{
		int p_num = 100;
		int w_num = 4;
		int bx_size = 10;

		particle prts[] = new particle[p_num];
		wall wls[] = new wall[w_num];

		wls[0] = new wall(new double[]{bx_size, 0}, new double[]{1, 0});
		wls[1] = new wall(new double[]{0, bx_size}, new double[]{0, 1});
		wls[2] = new wall(new double[]{-bx_size, 0}, new double[]{1, 0});
		wls[3] = new wall(new double[]{0, -bx_size}, new double[]{0, 1});

		//	This code is to generate a bigger particle, for simulating brownian motion.
		// prts[0] = new particle(new double[]{0,0}, new double[]{1,1}, 350, 2, new Color(250, 200, 100));

		for(int k = 0; k < p_num; k++) {
			Random rand = new Random();
			double[] pos = new double[2];
			double[] vel = new double[2];
			pos[1] = ((double) rand.nextInt(200) - 100) / 10;
			pos[0] = ((double) rand.nextInt(200) - 100) / 10;
			vel[0] = ((double) rand.nextInt(10) - 5) / 10;
			vel[1] = ((double) rand.nextInt(10) - 5) / 10;
			int mass = 10 + rand.nextInt(20) / 2;
			double radius = Math.sqrt(mass) / 30;
			prts[k] = new particle(pos, vel, 15, 0.1, new Color((100 * k) % 255, (150 * k) % 255, (220 * k) % 255));

			boolean col = false;
			for(int l = 0; l < w_num; l++)
				if(prts[k].isCollide(wls[l]))
				{
					col = true;
					break;
				}

			for(int l = 0; l < k; l++)
				if(prts[k].isCollide(prts[l]))
				{
					col = true;
					break;
				}

			if(col)
			{
				k--;
				continue;
			}
		}

		/*
		This code generates particles for diffusion experiment.

		for(int i = 0; i < 4; i++)
		{
			prts[2 * i] = new particle(new double[]{0,9 - (2 * i)}, new double[]{0,0}, 100000000, 0.999999999, Color.BLACK);
			prts[1 + (2 * i)] = new particle(new double[]{0, -(9 - (2 * i))}, new double[]{0,0}, 100000000, 0.999999999, Color.BLACK);
		}

		for(int k = 8; k < num; k++) {
			Random rand = new Random();
			double[] pos = new double[2];
			double[] vel = new double[2];
			pos[1] = ((double) rand.nextInt(200) - 100) / 10;
			pos[0] = ((double) rand.nextInt(100)) / 10;
			vel[0] = ((double) rand.nextInt(10) - 5) / 10;
			vel[1] = ((double) rand.nextInt(10) - 5) / 10;
			int mass = 10 + rand.nextInt(20) / 2;
			double radius = Math.sqrt(mass) / 30;
			prts[k] = new particle(pos, vel, mass, radius, new Color((100 * k) % 255, (150 * k) % 255, (220 * k) % 255));

			boolean col = false;
			for(int l = 0; l < w_num; l++)
				if(prts[k].isCollide(wls[l]))
				{
					col = true;
					break;
				}

			for(int l = 0; l < k; l++)
				if(prts[k].isCollide(prts[l]))
				{
					col = true;
					break;
				}

			if(col)
			{
				k--;
				continue;
			}
		}*/

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-11, 11);
		StdDraw.setYscale(-11, 11);
		StdDraw.setPenRadius(0.01);

		simulator sim = new simulator(prts,wls);

		long i = 0;
		while(true)
		{
			StdDraw.clear();
			sim.draw();
			StdDraw.show();
			sim.proceed(0.1);
			StdDraw.pause(0);
			while(!StdDraw.isMousePressed())
				StdDraw.pause(0);
			i++;
		}
	}
}