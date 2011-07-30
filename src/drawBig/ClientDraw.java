package drawBig;

import java.util.ArrayList;

import mazeBig.Point;

public class ClientDraw {

	public DrawWalls[][] theWalls;
	public ArrayList<Player> Players;
	public Point CurrentViewPoint;
	public ArrayList<SquareGame> Games;



	public ClientDraw(DrawWalls[][] wz) {
		theWalls = wz;
		CurrentViewPoint = new Point(40, 40);
		Players = new ArrayList<Player>();
		Games = new ArrayList<SquareGame>();
	}

	public void initMaze() {

	}

}