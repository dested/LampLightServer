package mazeBig;

public class ServerMaze {
	public int MazeSize;
	public Walls[][] theWalls;

	public ServerMaze(int mazeSize) {
		MazeSize = mazeSize;
		theWalls = new Walls[MazeSize][MazeSize];
		initMaze();
	}

	public void initMaze() {
		for (int i = 0; i < MazeSize; i++) {
			for (int a = 0; a < MazeSize; a++) {
				theWalls[i][a] = Walls.All();
			}
		}
		Carver c = new Carver(theWalls, MazeSize);
		c.Walk();

	}

}