//	This class creates n random particles, and simulates their collision in a 2D square box.

package com.company;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class smp_random {
	public static void main(String[] args)
	{
		int pt_num;					// Number of particles simulated
		int wl_num = 4;				// Number of walls simulated
		int bx_sz;					// Defines size of box

		if(args.length > 0)
		{
			pt_num = Integer.parseInt(args[0]);
			if(args.length > 1)
				bx_sz = Integer.parseInt(args[1]);
			else
				bx_sz = 20;
		}
		else
		{
			pt_num = 100;
			bx_sz = 20;
		}

		wall[] wls 		= new wall[wl_num];
		particle[] pts 	= new particle[pt_num];
		Random random	= new Random();

		wls[0] = new wall(new double[]{0,  bx_sz / 2}, new double[]{0, 1});
		wls[1] = new wall(new double[]{0, -bx_sz / 2}, new double[]{0, 1});
		wls[2] = new wall(new double[]{ bx_sz / 2, 0}, new double[]{1, 0});
		wls[3] = new wall(new double[]{-bx_sz / 2, 0}, new double[]{1, 0});

		for (int i = 0; i < pt_num; i++)
		{
			double[] pos = new double[2];
			double[] vel = new double[2];
			for (int j = 0; j < 2; j++)
			{
				pos[j] = (double) (random.nextInt(bx_sz * 10) - bx_sz * 10 / 2) / 10;
				//vel[j] = (double) (random.nextInt(bx_sz / 2) - (bx_sz / 4)) / 10;
				vel[j] = pos[j] / 100;
			}
			double mass   = 15 + ((double)random.nextInt(10) / 2);
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
			sim.proceed(1);
			StdDraw.pause(0);
			while(!StdDraw.isMousePressed())
				StdDraw.pause(0);
		}
	}
}