package com.company;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		// write your code here
		int num = 100;
		int w_num = 4;
		int bx_size = 10;

		particle prts[] = new particle[num];
		wall wls[] = new wall[w_num];

		//prts[1] = new particle(new double[]{-3,0}, new double[]{0.1,0}, 10,2);
		//prts[0] = new particle(new double[]{5,0}, new double[]{-0.1,0}, 1,1);
		for(int k = 0; k < num; k++) {
			Random rand = new Random();
			double[] pos = new double[2];
			double[] vel = new double[2];
			pos[1] = ((double) rand.nextInt(200) - 100) / 10;
			pos[0] = ((double) rand.nextInt(200) - 100) / 10;
			vel[0] = ((double) rand.nextInt(10) - 5) / 10;
			vel[1] = ((double) rand.nextInt(10) - 5) / 10;
			int mass = 10 + rand.nextInt(20) / 2;
			double radius = Math.sqrt(mass) / 30;
			prts[k] = new particle(pos, vel, mass, radius, new Color((k * 10) % 127 + 128,(k * 7) % 255,(k * 8) % 255));
			StdOut.println("Point number " + k + "\n" + prts[k].toString());
		}

		//prts[0] = new particle(new double[]{0,0}, new double[]{0.1,0}, 100, 0.5);
		StdOut.println("\n" + prts[0].toString());

		wls[0] = new wall(new double[]{bx_size,0}, new double[]{1,0});
		wls[1] = new wall(new double[]{0,bx_size}, new double[]{0,1});
		wls[2] = new wall(new double[]{-bx_size,0}, new double[]{1,0});
		wls[3] = new wall(new double[]{0,-bx_size}, new double[]{0,1});
		//wls[4] = new wall(new double[]{3,0}, new double[]{1,1});
		//wls[5] = new wall(new double[]{-3,-0}, new double[]{1,1});

		StdDraw.enableDoubleBuffering();
		StdDraw.setXscale(-11, 11);
		StdDraw.setYscale(-11, 11);
		StdDraw.setPenRadius(0.01);

		for (int i = 0; true; i++)
		{
			//StdOut.println("====================");
			StdDraw.clear();

			StdDraw.setPenColor(128,210,130);
			for(wall w : wls)
				w.draw();

			for(int j = 0; j < num; j++)
			{
				for(wall w : wls)
					if(prts[j].isCollide(w))
					{
						//StdOut.println("collission! - " + prts[j] + " and " + w.toString() + prts[j].distanceTo(w));
						prts[j].collide(w);
						StdOut.println(prts[j].toString());
						//StdOut.println("=========================================");
					}
				prts[j].move(0.5);
				for(int k = j + 1; k < num; k++)
				{
					if(prts[j].isCollide(prts[k]))
					{
						prts[j].collide(prts[k]);
					}
				}
				prts[j].draw();
			}

			StdDraw.show();
			StdDraw.pause(0);
		}
	}
}