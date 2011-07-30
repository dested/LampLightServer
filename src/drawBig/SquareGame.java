package drawBig;

import java.util.ArrayList;

import mazeBig.Point;

import com.Server.SquarePlayer;

public class SquareGame {
	public ArrayList<SquarePlayer> PlayersInGame;
	public DrawWalls[][] Perimeter;
	public DColor Color;
	public String Name;
	
	public SquarePlayer CurrentPlayer;
	
	

	public SquareGame(int size,DColor col) {
		PlayersInGame = new ArrayList<SquarePlayer>();
		Perimeter = new DrawWalls[size][size];
		Color=col;
	}



	public int NumActive() {
		int actives=0;
		for (int j = 0; j < PlayersInGame.size(); j++) {
			SquarePlayer sp = PlayersInGame.get(j);
			if(sp.Active){
				actives++;
		 
			}
			
		}
		return actives;
	}
	public void MoveNextPlayer() {
		
		for (int j = 0; j < PlayersInGame.size(); j++) {
		SquarePlayer sp = PlayersInGame.get(j);
		if (sp.FullName.equals(CurrentPlayer.FullName)) {
			while (true) {
				if (j == PlayersInGame.size() - 1) {

					CurrentPlayer = PlayersInGame.get(0);
					if (!CurrentPlayer.Active) {
						j = 1;
						continue;
					}
					break;
				} else {
					CurrentPlayer = PlayersInGame.get(j + 1);
					if (!CurrentPlayer.Active) {
						j++;
						continue;
					}
					break;
				}
			}
			break;
		}
	}
		
	}



	public Point MidPoint() {
		int fLeft=200;
		int fRight=0;

		int fTop=200;
		int fBottom=0;
		
		for(int x=0;x<Perimeter.length;x++){
			for(int y=0;y<Perimeter.length;y++){
				if(Perimeter[x][y]!=null){
					if(x<fLeft){
						fLeft=x;
					}
					if(x>fRight){
						fRight=x;
					}
					if(y<fTop){
						fTop=y;
					}
					if(y>fBottom){
						fBottom=y;
					}
				}
			}		
		}
		return new Point(fLeft+(fRight-fLeft)/2,fTop+(fBottom-fTop)/2);
		

		
		
	}
}