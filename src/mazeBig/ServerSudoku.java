package mazeBig;

import java.util.Random;

import sudokuBig.SudokuGenerator;
 
public class ServerSudoku {

	public int[][] theIndexes;

	public ServerSudoku() {

		theIndexes = new int[9][9];
		initMaze();
	}

	Random r = new Random(4987654);

	public void initMaze() {
		for (int i = 0; i < 9; i++) {
			for (int a = 0; a < 9; a++) {
				theIndexes[i][a] = r.nextInt(15) - 5;
				if (theIndexes[i][a] < -1) {
					theIndexes[i][a] = -1;
				}
			}
		}

		SudokuGenerator n = new SudokuGenerator(3);
		theIndexes = n.generate(false);
	
		for (int i = 0; i < 9; i++) {
			for (int a = 0; a < 9; a++) {
				if (r.nextInt(15) < 9) {
					theIndexes[i][a] = -1;
				}
			}
		}

	}

}