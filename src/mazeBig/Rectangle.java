package mazeBig;
 

public class Rectangle {
	public int X;
	public int Y;
	public int Width;
	public int Height;

	public Rectangle(int x, int y, int width, int height) {
		X = x;
		Y = y;
		Width = width;
		Height = height;

	}

	public boolean Collides(int x2, int y2) {
		return (X < x2 && X + Width > x2 && Y < y2 && Y + Height > y2);

	}

 

	public int Bottom() {
		// TODO Auto-generated method stub
		return Y + Height;
	}

	public void Bottom(int set) {
		// TODO Auto-generated method stub
		Height = set - Y;
	}

	public int Right() {
		// TODO Auto-generated method stub
		return X + Width;
	}

	public void Right(int set) {
		// TODO Auto-generated method stub
		Width = set - X;
	}
}
