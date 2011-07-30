package com.Server; 

import java.util.ArrayList;

import drawBig.DColor;
import drawBig.DrawWalls;
import drawBig.SquareGame;

public class SquarePlayer {
	public String Name;
	public String FullName;
	public ArrayList<DrawWalls> MyPieces;
	public int Score;
	public DColor Color;
	public ArrayList<SquareGame> Games;
	public boolean Active;
	public int PlayerID;

	public SquarePlayer(String fullname, DColor dc,int pID) {
		FullName = fullname;
		PlayerID=pID;
		Name = FullName.split("/")[FullName.split("/").length - 1];
		Color = dc;
		MyPieces = new ArrayList<DrawWalls>();
		Games = new ArrayList<SquareGame>();
		Score = 0;
		Active = true;
	}
}