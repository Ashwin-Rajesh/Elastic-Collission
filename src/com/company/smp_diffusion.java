package com.company;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class smp_diffusion {
	public static void main(String[] args)
	{
		int pt_num = 200;			// Number of particles simulated
		int wl_num = 4;				// Number of walls simulated
		int bx_sz = 20;				// Defines size of box

		wall[] wls 		= new wall[wl_num];
		particle[] pts 	= new particle[pt_num];
		Random random	= new Random();

		wls[0] = new wall(new double[]{0,  bx_sz / 2}, new double[]{0, 1});
		wls[1] = new wall(new double[]{0, -bx_sz / 2}, new double[]{0, 1});
		wls[2] = new wall(new double[]{ bx_sz / 2, 0}, new double[]{1, 0});
		wls[3] = new wall(new double[]{-bx_sz / 2, 0}, new double[]{1, 0});

		for(int i = 0; i < 4; i++)
		{
			pts[2 * i] = new particle(new double[]{0,9 - (2 * i)}, new double[]{0,0}, 1000000000, 0.999999999, Color.BLACK);
			pts[1 + (2 * i)] = new particle(new double[]{0, -(9 - (2 * i))}, new double[]{0,0}, 1000000000, 0.999999999, Color.BLACK);
		}

		for (int i = 8; i < pt_num; i++)
		{
			double[] pos = new double[2];
			double[] vel = new double[2];

			pos[0] = (double) (random.nextInt(bx_sz * 5)) / 10;
			vel[0] = (double) (random.nextInt(bx_sz / 2) - (bx_sz / 4)) / 10;
			pos[1] = (double) (random.nextInt(bx_sz * 10) - bx_sz * 10 / 2) / 10;
			vel[1] = (double) (random.nextInt(bx_sz / 2) - (bx_sz / 4)) / 10;


			double mass   = 15 + ((double)random.nextInt(10) / 2);
			double radius = Math.sqrt(mass) / 30;
			Color color   = pos[1] > 0 ? new Color(0,128,255) : new Color(255, 128,0);

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
			{
				i--;
				continue;
			}
		}

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-(bx_sz * 11 / 20), (bx_sz * 11 / 20));
		StdDraw.setYscale(-(bx_sz * 11 / 20), (bx_sz * 11 / 20));
		StdDraw.setPenRadius(0.01);

		simulator sim = new simulator(pts,wls);

		while(true)
		{
			StdDraw.clear();
			sim.draw();
			StdDraw.show();
			sim.proceed(0.5);
			StdDraw.pause(0);
			while(!StdDraw.isMousePressed())
				StdDraw.pause(0);
		}
	}
}
