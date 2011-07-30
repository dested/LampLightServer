package mazeBig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

 

public class Carver {
	private Walls[][] theWalls;
	private int MazeSize;
	public boolean[][] bw;

	public Carver(Walls[][] wallz, int mazeSize) {
		theWalls = wallz;
		MazeSize = mazeSize;
	}

	public void Walk() {
		bw = new boolean[MazeSize][MazeSize];
		walker(0, 0);
	}

	public void walker(int cx, int cy) {
		for (WallStuff direction : RandomizeEach()) {
			int nx = cx + getDX(direction);
			int ny = cy + getDY(direction);
			if (ny >= 0 && ny <= MazeSize-1 && nx >= 0 && nx <= MazeSize-1 && !bw[nx][ny]) {

				//if (!bw[nx][ny]) 
				{
					bw[nx][ny]=true;
					theWalls[cx][cy].remove(direction);
					theWalls[nx][ny].remove(getOpposite(direction));
					walker(nx, ny);
				}
			}
		}

	}


	Random rr = new Random();

	private ArrayList<WallStuff> MixList(ArrayList<WallStuff> ipl) {
		ArrayList<WallStuff> inputList = new ArrayList<WallStuff>(ipl);
		ArrayList<WallStuff> randomList = new ArrayList<WallStuff>();

		int randomIndex = 0;
		while (inputList.size() > 0) {
			randomIndex = rr.nextInt(inputList.size()); // Choose a random, obj in the list
			randomList.add(inputList.get(randomIndex)); // add it to the new, random list
			inputList.remove(randomIndex); // remove to avoid duplicates
		}

		return randomList; // return the new random list
	}

	ArrayList<WallStuff> gms = new ArrayList<WallStuff>(Arrays.asList(WallStuff.values()));

	private ArrayList<WallStuff> RandomizeEach() {
		return MixList(gms);
	}

	private WallStuff getOpposite(WallStuff wallse) {
		switch (wallse) {
		case North:
			return WallStuff.South;
		case South:
			return WallStuff.North;
		case East:
			return WallStuff.West;
		case West:
			return WallStuff.East;
		}
		return WallStuff.East;// never hit
	}
	private int getDX(WallStuff wallse) {

		switch (wallse) {
		case North:
		case South:
			return 0;
		case East:
			return -1;
		case West:
			return 1;
		}
		return 0;
	}

	private int getDY(WallStuff wallse) {

		switch (wallse) {
		case North:
			return -1;
		case South:
			return 1;
		case East:
		case West:
			return 0;
		}
		return 0;
	}

}