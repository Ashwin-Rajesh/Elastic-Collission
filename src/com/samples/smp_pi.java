package com.samples;

import com.collision.particle;
import com.collision.simulator;
import com.collision.wall;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;

public class smp_pi {
	public static void main(String[] args)
	{
		particle[] pts = new particle[2];
		wall[] wls = new wall[1];

		wls[0] = new wall(new double[]{0,0}, new double[]{-1,0});

		pts[0] = new particle(new double[]{2,0}, new double[]{0,0}, 1, 0.1, new Color(128,128,128));
		pts[1] = new particle(new double[]{4,0}, new double[]{-1,0}, 100, 0.2, new Color(0,0,0));

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-1, 11);
		StdDraw.setYscale(-6, 6);
		StdDraw.setPenRadius(0.01);

		simulator sim = new simulator(pts,wls);

		while(true)
		{
			StdDraw.clear();
			sim.draw();
			StdDraw.show();
			sim.proceed(0.01);
			StdDraw.pause(0);
			while(!StdDraw.isMousePressed())
				StdDraw.pause(0);
		}
	}
}
