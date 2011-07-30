package mazeBig;

public class Point {
	public int X;
	public int Y;

	public Point(int x, int y) {
		X = x;
		Y = y;
	}

	public static Point Clone(Point point) {
		return new Point(point.X, point.Y);
	}
	public Point(Point pm) {
		X = pm.X;
		Y = pm.Y;
	}
	public void Offset(int i, int j) {
		X += i;
		Y += j;

	}

	public static Point Difference(Point lastMous2, Point point) {
		return new Point((lastMous2.X - point.X), (lastMous2.Y - point.Y));
	}

	public void Combine(Point point) {
		X += point.X;
		Y += point.Y;

	}

	public Point Negative() {
		// TODO Auto-generated method stub
		return new Point(-X, -Y);
	}

	public void reduce(int i) {
		//
		if (X > 0) {
			X -= i;
			if (X < 0) {
				X = 0;
			}
		}
		if (X <0) {
			X += i;
			if (X > 0) {
				X = 0;
			}
		}
		if (Y > 0) {
			Y -= i;
			if (Y < 0) {
				Y = 0;
			}
		}
		if (Y <0) {
			Y += i;
			if (Y > 0) {
				Y = 0;
			}
		}

	}

	public boolean Zero() {
return X==0 && Y==0;
	}
}
