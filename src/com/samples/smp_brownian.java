package com.samples;

import com.collision.particle;
import com.collision.simulator;
import com.collision.wall;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class smp_brownian {
	public static void main(String[]  args)
	{
		int pt_num = 100;			// Total number of particles simulated
		int bw_num = 2;				// Number of bigger particles simulated
		int wl_num = 4;				// Number of walls simulated
		int bx_sz = 20;				// Defines size of box

		wall[] wls 		= new wall[wl_num];
		particle[] pts 	= new particle[pt_num];
		Random random	= new Random();

		wls[0] = new wall(new double[]{0,  bx_sz / 2}, new double[]{0, 1});
		wls[1] = new wall(new double[]{0, -bx_sz / 2}, new double[]{0, 1});
		wls[2] = new wall(new double[]{ bx_sz / 2, 0}, new double[]{1, 0});
		wls[3] = new wall(new double[]{-bx_sz / 2, 0}, new double[]{1, 0});

		for (int i = 0; i < bw_num; i++)
		{
			double[] pos = new double[2];
			double[] vel = new double[2];
			for (int j = 0; j < 2; j++)
			{
				pos[j] = (double) (random.nextInt(bx_sz * 10) - bx_sz * 10 / 2) / 10;
				vel[j] = (double) (random.nextInt(bx_sz / 2) - (bx_sz / 4)) / 30;
				//vel[j] = pos[j] / 100;
			}
			double mass   = 300 + ((double)random.nextInt(50) / 2);
			double radius = Math.sqrt(mass) / 10;
			Color color   = new Color(255, 124, 50);

			pts[i] = new particle(pos, vel, mass, radius, color);

			// Below code checks if the generated particle is generated inside another particle or wall.
			boolean col = false;
			for(int l = 0; l < wl_num; l++)
				if(pts[i].isCollide(wls[l]))
				{
					col = true;
					break;
				}

			for(int l = 0; l < i; l++)
				if(pts[i].isCollide(pts[l]))
				{
					col = true;
					break;
				}

			if(col)
				i--;
		}

		for (int i = bw_num; i < pt_num; i++)
		{
			double[] pos = new double[2];
			double[] vel = new double[2];
			for (int j = 0; j < 2; j++)
			{
				pos[j] = (double) (random.nextInt(bx_sz * 10) - bx_sz * 10 / 2) / 10;
				vel[j] = (double) (random.nextInt(bx_sz / 2) - (bx_sz / 4)) / 30;
				//vel[j] = pos[j] / 100;
			}
			double mass   = 10 + ((double)random.nextInt(20) / 2);
			double radius = Math.sqrt(mass) / 30;
			Color color   = new Color((100 * i) % 255, (150 * i) % 255, (220 * i) % 255);

			pts[i] = new particle(pos, vel, mass, radius, color);

			// Below code checks if the generated particle is generated inside another particle or wall.
			boolean col = false;
			for(int l = 0; l < wl_num; l++)
				if(pts[i].isCollide(wls[l]))
				{
					col = true;
					break;
				}

			for(int l = 0; l < i; l++)
				if(pts[i].isCollide(pts[l]))
				{
					col = true;
					break;
				}

			if(col)
				i--;
		}

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-((double) bx_sz * 11 / 20), ((double) bx_sz * 11 / 20));
		StdDraw.setYscale(-((double) bx_sz * 11 / 20), ((double) bx_sz * 11 / 20));
		StdDraw.setPenRadius(0.01);
		wall.x_scale = bx_sz / 2;
		wall.y_scale = bx_sz / 2;

		simulator sim = new simulator(pts,wls);

		while(true)
		{
			StdDraw.clear();
			sim.draw();
			StdDraw.show();
			sim.proceed(1);
			StdDraw.pause(0);
			while(!StdDraw.isMousePressed())
				StdDraw.pause(0);
		}
	}
}
