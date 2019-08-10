package com.collision;

import edu.princeton.cs.algs4.StdDraw;

public class wall {
	public static double x_scale  = 10;
	public static double y_scale = 10;

	private final double[] point;
	private final double[] normal;

	public wall(double[] point, double[] normal)
	{
		if(point == null || normal == null)
			throw new IllegalArgumentException(" Null arguments were passed");
		if(point.length != normal.length)
			throw new IllegalArgumentException(" Point and normal have different dimensions");

		this.point = point.clone();
		this.normal = normal.clone();

		double magnitude = 0;
		for(int i = 0; i < point.length; i++)
			magnitude += Math.pow(normal[i],2);
		magnitude = Math.sqrt(magnitude);
		for(int i = 0; i < point.length; i++)
			this.normal[i] /= magnitude;
	}

	double[] getPoint()
	{
		return point.clone();
	}

	double[] getNormal()
	{
		return normal.clone();
	}

	void draw() {
		if (point.length > 2)
			throw new IllegalArgumentException(" Drawing supported only for upto 2 dimensions as of now.");

		if (point.length == 1)
			StdDraw.point(0, point[0]);

		if (point.length == 2) {
			double slope = -normal[0] / normal[1];

			double c = point[1] - (slope * point[0]);

			if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
				StdDraw.line(point[0], y_scale, point[0], -y_scale);
				return;
			}

			if (slope == 0) {
				StdDraw.line(x_scale, point[1], -x_scale, point[1]);
				return;
			}

			double x1 = 0, y1 = 0, x2 = 0, y2 = 0;


				if (Math.abs(slope) > y_scale/x_scale) {
					y1 = y_scale;
					y2 = -y_scale;
					x1 = (y1 - c)/slope;
					x2 = (y2 - c)/slope;
				}
				else {
					x1 = x_scale;
					x2 = -x_scale;
					y1 = (slope * x1) + c;
					y2 = (slope * x2) + c;
				}

			StdDraw.line(x1, y1, x2, y2);
		}
	}

	public String toString()
	{
		StringBuilder str = new StringBuilder();
		int dimension = point.length;

		str.append("\n");
		str.append(" Dimension : " + dimension + "\n");
		str.append(" Point	  : ");
		for(int i = 0; i < dimension; i++)
			str.append(point[i] + "  ");

		str.append("\n");
		str.append(" Normal   : ");
		for(int i = 0; i < dimension; i++)
			str.append(normal[i] + "  ");
		str.append("\n");

		if(dimension == 2)
		{
			double slope = -normal[0] / normal[1];

			double c = point[1] - (slope * point[0]);

			str.append(" Line representation : y = " + slope + " ( x ) + " + c + "\n");
		}

		return str.toString();
	}

}