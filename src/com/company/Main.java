package com.company;

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.awt.*;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // write your code here
        int num = 100;
        particle prts[] = new particle[num];

        //prts[1] = new particle(new double[]{0,0}, new double[]{0.1,0}, 10);
        //prts[0] = new particle(new double[]{5, 0}, new double[]{-0.1,0}, 1);
        for(int k = 0; k < num; k++)
        {
            Random rand = new Random();
            double[] pos = new double[2];
            double[] vel = new double[2];
            pos[1] = ((double)rand.nextInt(200) - 100)/10;
            pos[0] = ((double)rand.nextInt(200) - 100)/10;
            vel[0] = ((double) rand.nextInt(10) - 5 )/ 10;
            vel[1] = ((double) rand.nextInt(10) - 5 )/ 10;
            int mass = 10 + rand.nextInt(20)/2;
            double radius = Math.sqrt(mass)/10;
            prts[k] = new particle(pos, vel, mass, radius);
            StdOut.println(prts[k].toString());
        }

        prts[0] = new particle(new double[]{0,0}, new double[]{0.1,0}, 100, 2);
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(-10, 10);
        StdDraw.setYscale(-10, 10);
        StdDraw.setPenRadius(0.01);

        for (int i = 0; true; i++)
        {
            //StdOut.println("====================");
            StdDraw.clear();
            for(int j = 0; j < num; j++)
            {
                StdDraw.setPenColor((j * 10) % 127 + 128,(j * 7) % 255,(j * 8) % 255);
                prts[j].move(0.1);
                for(int k = j + 1; k < num; k++)
                {
                    if(prts[j].isCollide(prts[k]))
                    {
                        StdOut.println(j + " collide " + k);
                        StdOut.println(prts[j].toString());
                        StdOut.println(prts[k].toString());
                        prts[j].collide(prts[k]);
                        StdOut.println(prts[j].toString());
                        StdOut.println(prts[k].toString());
                    }
                }
                //StdOut.println("------------------------");
                //StdOut.println(" Point no : " + j);
                //StdOut.println(prts[j].toString());
                prts[j].draw();
            }
            StdDraw.show();
            StdDraw.pause(0);
        }
    }
}