package com.Server;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


import com.MessageParseJunk.DrawGameRoomMessage;
import com.MessageParseJunk.GameRoomMessage;
import com.MessageParseJunk.SudokuGameRoomMessage;
import com.MessageParseJunk.GameRoomMessage.GameRoomMessageType;
import com.MessageParseJunk.SudokuGameRoomMessage.SudokuGameRoomMessageType;
import com.MessageParseJunk.WaitingRoomMessage;
import com.MessageParseJunk.WaitingRoomMessage.MessageType;

import drawBig.DColor;
import drawBig.DrawWalls;
import drawBig.SquareGame;

import mazeBig.Point;
import mazeBig.Rectangle;
import mazeBig.ServerMaze;
import mazeBig.ServerSudoku;
import mazeBig.WallStuff;
import mazeBig.Walls;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;  
public class ClientConnector {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);

		// Reads a single line from the console
		// and stores into name variable

		new ClientConnector();

		while (true) {
			String ds = in.nextLine();
			if (ds.toLowerCase().equals("quit"))
				break;

		}

	}

	XMPPConnection xmpp;
	final String uName = "sudoker";

	public ClientConnector() {

		// XMPPConnection.DEBUG_ENABLED=true;

		// ConnectionConfiguration config = new
		// ConnectionConfiguration("localhost", 5222,
		// "gameService.lamplightonline.com");
		ConnectionConfiguration config = new ConnectionConfiguration("lamplightonline.com", 5222, "gameService.lamplightonline.com");
		// ConnectionConfiguration config = new
		// ConnectionConfiguration("72.222.152.39", 5222,
		// "gameservice.lamplightonline.com");
		config.setSecurityMode(SecurityMode.disabled);

		config.setCompressionEnabled(true);
		xmpp = new XMPPConnection(config);

		try {
			xmpp.connect();

			xmpp.login(uName, "d");
			Timer t = new Timer();
			t.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					xmpp.sendPacket(new Presence(Presence.Type.available, null, 1, Presence.Mode.available));
				}
			}, 100, 50 * 1000);

		} catch (XMPPException e) {
			e.printStackTrace();
		}

	 
			
			
			xmpp.addPacketListener(new PacketListener() {

				@Override
				public void processPacket(Packet arg0) {
				
					System.out.println(arg0.toXML());

					 
				}

			}, new PacketFilter() {

				@Override
				public boolean accept(Packet arg0) {
					return true;
					// TODO Auto-generated method stub
					// return false;
				}
			});
			
			 
		

		for (int i = 1; i <= 3; i++) {

			try {
				DoWaitingRoom(i);
				DoGameRoom(i);
				DoDrawGameRoom(i);
				DoSudokuGameRoom(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public class Player {
		public String Name;
		public String FullName;
		public boolean IsReady = false;

		public Player(String fullname) {
			FullName = fullname;
			Name = FullName.split("/")[FullName.split("/").length - 1];
		}
	}

	public class DrawGameInformation {

		public ArrayList<SquarePlayer> PlayersInGame = new ArrayList<SquarePlayer>();
		public DrawWalls[][] blockData;

		public ArrayList<SquareGame> Games;

	}

	Random r = new Random();

	public void DoDrawGameRoom(final int gameRoomIndex) throws Exception {

		final DrawGameInformation game = new DrawGameInformation();

		game.Games = new ArrayList<SquareGame>();

		System.out.println("Draw Game Room" + gameRoomIndex + " Started ");

		game.blockData = new DrawWalls[DrawGameRoomMessage.FULLSIZE][DrawGameRoomMessage.FULLSIZE];

		for (int i = 0; i < DrawGameRoomMessage.FULLSIZE; i++) {
			for (int a = 0; a < DrawGameRoomMessage.FULLSIZE; a++) {
				game.blockData[i][a] = new DrawWalls();
			}
		}

		final MultiUserChat muc = new MultiUserChat(xmpp, "drawgameroom" + gameRoomIndex + "@gameservice.lamplightonline.com");
		try {
			// muc.create(uName);
			muc.join(uName);

			for (Iterator<String> it = muc.getOccupants(); it.hasNext();) {
				String vf = it.next();
				if (vf.endsWith(uName)) {
					continue;
				}

				throw new Exception("No Users allowed before me");

			}
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.Ping).GenerateMessage());
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		}, 100, 50 * 1000);

		muc.addParticipantListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				if (arg0.getFrom().endsWith(uName)) {
					return;
				}

				Presence pre = (Presence) arg0;
				SquarePlayer p = null;

				switch (pre.getType()) {
				case available:
					boolean okay = false;
					SquarePlayer selectedPlayer = null;
					for (SquarePlayer pd : game.PlayersInGame) {
						if (pd.FullName.equals(arg0.getFrom())) {
							okay = true;
							p = pd;
							selectedPlayer = pd;
							pd.Active = true;
							System.out.println(pd.Name + " is active.");

							break;

						}
					}

					if (!okay) {

						game.PlayersInGame.add(p = new SquarePlayer(arg0.getFrom(), DColor.Random(), 0));
						p.PlayerID = game.PlayersInGame.size();
						System.out.println(p.Name + " Has Joined");
						selectedPlayer = p;
					}

					try {
						System.out.println("Sending Game Data to " + p.Name);
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.SendStartData, p.FullName, game.blockData,
								selectedPlayer.PlayerID).GenerateMessage());

						System.out.println("Sending Game perimeters to " + p.Name);
						for (SquareGame gm : game.Games) {
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.CreateNewGame, gm.Color, gm.Name).GenerateMessage());
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePerimeter, gm.Perimeter, gm.Name)
									.GenerateMessage());
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateCurrentPlayer, gm).GenerateMessage());

							ArrayList<Integer> pIds1 = new ArrayList<Integer>();
							for (SquarePlayer pi : gm.PlayersInGame)
								pIds1.add(pi.PlayerID);
							// muc.sendMessage(new
							// DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateGamePlayerInfo,
							// gm.Name, pIds1).GenerateMessage());

						}
						System.out.println("Sending Player Info " + p.Name);
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePlayersInfo, game.PlayersInGame)
								.GenerateMessage());

						System.out.println("Done");

					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case unavailable:
					for (SquarePlayer pd : game.PlayersInGame) {

						if (pd.FullName.equals(arg0.getFrom())) {

							for (SquareGame gam : game.Games) {
								if (gam.CurrentPlayer.FullName.equals(pd.FullName)) {

									if (gam.NumActive() == 2) {
										// bot
										gam.MoveNextPlayer();
									} else if (gam.NumActive() == 1) {
										// empty
									} else {

										gam.MoveNextPlayer();
									}
									try {
										muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateCurrentPlayer, gam)
												.GenerateMessage());
									} catch (XMPPException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							pd.Active = false;

							System.out.println(pd.Name + " Is Inactive");
							break;
						}
					}
					try {
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePlayersInfo, game.PlayersInGame)
								.GenerateMessage());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				}
			}
		});

		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet message) {
				if (message.getFrom().endsWith(uName)) {
					return;
				}
				SquarePlayer curPlayer = null;
				for (SquarePlayer pd : game.PlayersInGame) {
					if (pd.FullName.equals(message.getFrom())) {
						curPlayer = pd;
						break;
					}
				}

				System.out.println(((Message) message).getBody());
				DrawGameRoomMessage d = DrawGameRoomMessage.Parse(((Message) message).getBody());

				switch (d.Type) {
				case SendStartData:
					/*
					 * if (d.point.X == game.MazeSize - 1 && d.point.Y ==
					 * game.MazeSize - 1) { try { muc.sendMessage(new
					 * GameRoomMessage
					 * (GameRoomMessageType.GameFinish).GenerateMessage()); }
					 * catch (XMPPException e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); } }
					 */
					break;
				case JoinGame:
					ArrayList<Integer> pIds = new ArrayList<Integer>();

					for (SquareGame gam : game.Games) {
						if (gam.Name.equals(d.GameName)) {
							gam.PlayersInGame.add(curPlayer);
							for (SquarePlayer p : gam.PlayersInGame)
								pIds.add(p.PlayerID);
							break;
						}
					}

					try {
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateGamePlayerInfo, d.GameName, pIds)
								.GenerateMessage());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case AppendLineServer:

					SquareGame exists = null;
					for (SquareGame gam : game.Games) {
						if (inPerimeter(gam.Perimeter, d.UpdatePoint)) {
							exists = gam;
							break;
						}
					}

					boolean okay = false;
					if (exists != null) {
						for (SquarePlayer sp : exists.PlayersInGame) {
							if (sp.FullName.equals(curPlayer.FullName)) {
								okay = true;
							}
						}
						if (!okay) {
							try {
								muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.AskJoinGame, true, exists.Name,
										curPlayer.FullName).GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}
					}

					if (exists == null) {
						game.Games.add(exists = new SquareGame(DrawGameRoomMessage.FULLSIZE, curPlayer.Color));
						exists.Name = "Game" + game.Games.size();
						exists.PlayersInGame.add(curPlayer);
						exists.CurrentPlayer = curPlayer;
						try {
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.CreateNewGame, exists.Color, exists.Name)
									.GenerateMessage());
							ArrayList<Integer> pIds1 = new ArrayList<Integer>();
							pIds1.add(curPlayer.PlayerID);
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateGamePlayerInfo, exists.Name, pIds1)
									.GenerateMessage());
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						if (!exists.CurrentPlayer.FullName.equals(curPlayer.FullName)) {
							return;
						}
					}
					boolean bad = false;
					switch (d.UpdateLine) {
					case North:
						bad = game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].North;
						break;
					case South:
						bad = game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].South;
						break;
					case East:
						bad = game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].East;
						break;
					case West:
						bad = game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].West;
						break;
					}
					if (bad) {

						return;
					}

					try {
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.AppendLine, d.UpdatePoint, d.UpdateLine,
								curPlayer.PlayerID).GenerateMessage());
					} catch (XMPPException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					punchWhole(d.UpdatePoint, exists.Perimeter, game.Games);
					exists.MoveNextPlayer();
					if (exists.NumActive() > 0) {
						try {
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateCurrentPlayer, exists).GenerateMessage());
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					switch (d.UpdateLine) {
					case North:
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].North = true;
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].NorthOwner = curPlayer.PlayerID;
						break;
					case South:
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].South = true;
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].SouthOwner = curPlayer.PlayerID;
						break;
					case East:
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].East = true;
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].EastOwner = curPlayer.PlayerID;
						break;
					case West:
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].West = true;
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].WestOwner = curPlayer.PlayerID;
						break;
					}

					WallStuff gc = getOpposite(d.UpdateLine);
					int dx = d.UpdatePoint.X + getDX(d.UpdateLine),
					dy = d.UpdatePoint.Y + getDY(d.UpdateLine);

					switch (gc) {
					case North:
						if (!(dy < 0 || dy > DrawGameRoomMessage.FULLSIZE)) {
							game.blockData[dx][dy].North = true;
							game.blockData[dx][dy].NorthOwner = curPlayer.PlayerID;
						}
						break;
					case South:
						if (!(dy < 0 || dy > DrawGameRoomMessage.FULLSIZE)) {
							game.blockData[dx][dy].South = true;
							game.blockData[dx][dy].SouthOwner = curPlayer.PlayerID;
						}
						break;
					case East:
						if (!(dx < 0 || dx > DrawGameRoomMessage.FULLSIZE)) {
							game.blockData[dx][dy].East = true;
							game.blockData[dx][dy].EastOwner = curPlayer.PlayerID;
						}

						break;
					case West:
						if (!(dx < 0 || dx > DrawGameRoomMessage.FULLSIZE)) {
							game.blockData[dx][dy].West = true;
							game.blockData[dx][dy].WestOwner = curPlayer.PlayerID;
						}
						break;
					}

					if (game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].Full()) {
						curPlayer.Score++;
						exists.CurrentPlayer = curPlayer;
						try {
							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateCurrentPlayer, exists).GenerateMessage());

							muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePlayersInfo, game.PlayersInGame)
									.GenerateMessage());
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						game.blockData[d.UpdatePoint.X][d.UpdatePoint.Y].FullOwner = curPlayer.PlayerID;
					}

					if (!(dx < 0 || dx > DrawGameRoomMessage.FULLSIZE) && !(dy < 0 || dy > DrawGameRoomMessage.FULLSIZE)) {
						if (game.blockData[dx][dy].Full()) {
							exists.CurrentPlayer = curPlayer;

							curPlayer.Score++;

							try {
								muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdateCurrentPlayer, exists).GenerateMessage());

								muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePlayersInfo, game.PlayersInGame)
										.GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							game.blockData[dx][dy].FullOwner = curPlayer.PlayerID;
						}
					}

					try {
						muc.sendMessage(new DrawGameRoomMessage(DrawGameRoomMessage.GameRoomMessageType.UpdatePerimeter, exists.Perimeter, exists.Name)
								.GenerateMessage());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case Leave:
					break;

				}
			}

		});
		System.out.println("Draw Game Room " + gameRoomIndex + " Done ");
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
			return 1;
		case West:
			return -1;
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

	private void punchWhole(Point updatePoint, DrawWalls[][] perimeter, ArrayList<SquareGame> gams) {
		int c = 3;
		Rectangle rt = new Rectangle(updatePoint.X - c, updatePoint.Y - c, c * 2, c * 2);

		if (rt.X < 0)
			rt.X = 0;
		if (rt.Y < 0)
			rt.Y = 0;
		if (rt.X > DrawGameRoomMessage.FULLSIZE - 1)
			rt.X = DrawGameRoomMessage.FULLSIZE - 1;
		if (rt.Y > DrawGameRoomMessage.FULLSIZE - 1)
			rt.Y = DrawGameRoomMessage.FULLSIZE;

		int area = 0;
		for (DrawWalls[] w : perimeter) {
			for (DrawWalls wc : w) {
				if (wc != null) {
					wc.reset();
					area++;
				}
			}
		}

		int maxArea = 80;

		for (int i = rt.X; i < rt.Right(); i++) {
			for (int a = rt.Y; a < rt.Bottom(); a++) {
				if (perimeter[i][a] == null) {
					boolean bad = false;
					for (SquareGame gq : gams) {
						if (gq.Perimeter[i][a] != null) {
							bad = true;
							break;
						}
					}
					if (!bad) {
						if (area <= maxArea) {
							area++;
							perimeter[i][a] = new DrawWalls(i, a);
						}

					}

				}
			}
		}

		for (int x = 0; x < DrawGameRoomMessage.FULLSIZE; x++) {
			for (int y = 0; y < DrawGameRoomMessage.FULLSIZE; y++) {
				DrawWalls w = perimeter[x][y];
				if (w == null)
					continue;
				if (x - 1 < 0) {
					w.West = true;
				} else if (perimeter[x - 1][y] == null) {
					w.West = true;
				}

				if (x + 1 >= DrawGameRoomMessage.FULLSIZE) {
					w.East = true;
				} else if (perimeter[x + 1][y] == null) {
					w.East = true;
				}

				if (y - 1 < 0) {
					w.North = true;
				} else if (perimeter[x][y - 1] == null) {
					w.North = true;
				}

				if (y + 1 >= DrawGameRoomMessage.FULLSIZE) {
					w.South = true;
				} else if (perimeter[x][y + 1] == null) {
					w.South = true;
				}

			}
		}
	}

	private boolean inPerimeter(DrawWalls[][] perimeter, Point updateLine) {
		return perimeter[updateLine.X][updateLine.Y] != null;
	}

	public class SudokuGameInformation {
		public boolean gameStarted;
		public ArrayList<Player> PlayersInGame = new ArrayList<Player>();
		public int[][] CurrentIndexes;

	}

	public void DoSudokuGameRoom(final int gameRoomIndex) throws Exception {

		final SudokuGameInformation game = new SudokuGameInformation();
		System.out.println("Sudoku Game Room" + gameRoomIndex + " Started ");
		game.CurrentIndexes = new ServerSudoku().theIndexes;

		final MultiUserChat muc = new MultiUserChat(xmpp, "sudokugameroom" + gameRoomIndex + "@gameservice.lamplightonline.com");

		try {
			// muc.create(uName);
			muc.join(uName);

			for (Iterator<String> it = muc.getOccupants(); it.hasNext();) {
				String vf = it.next();
				if (vf.endsWith(uName)) {
					continue;
				}

				throw new Exception("No Users allowed before me");

			}
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		muc.addParticipantListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				if (arg0.getFrom().endsWith(uName)) {
					return;
				}

				Presence pre = (Presence) arg0;
				Player p;

				switch (pre.getType()) {
				case available:
					game.PlayersInGame.add(p = new Player(arg0.getFrom()));
					System.out.println(p.Name + " Has Joined");

					if (!game.gameStarted && game.PlayersInGame.size() > 0) {
						try {
							muc.sendMessage(new SudokuGameRoomMessage(SudokuGameRoomMessageType.SudokuData, game.CurrentIndexes).GenerateMessage());

							muc.sendMessage(new SudokuGameRoomMessage(SudokuGameRoomMessageType.GameStarted).GenerateMessage());
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					break;
				case unavailable:
					for (Player pd : game.PlayersInGame) {

						if (pd.FullName.equals(arg0.getFrom())) {
							game.PlayersInGame.remove(pd);
							System.out.println(pd.Name + " Has Left");
							break;
						}
					}

					break;
				}
			}
		});

		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet message) {
				if (message.getFrom().endsWith(uName)) {
					return;
				}

				System.out.println(((Message) message).getBody());
				SudokuGameRoomMessage d = SudokuGameRoomMessage.Parse(((Message) message).getBody());

				switch (d.Type) {
				case SudokuMove:
					/*
					 * if (d.point.X == game.MazeSize - 1 && d.point.Y ==
					 * game.MazeSize - 1) { try { muc.sendMessage(new
					 * GameRoomMessage
					 * (GameRoomMessageType.GameFinish).GenerateMessage()); }
					 * catch (XMPPException e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); } }
					 */
					break;
				case GameStarted:
					break;
				case GameFinish:
					game.gameStarted = false;
					game.CurrentIndexes = new ServerSudoku().theIndexes;

					// wait and start one up again
					break;
				case Leave:
					break;

				}

			}
		});
		System.out.println("Sudoku Game Room " + gameRoomIndex + " Done ");
	}

	public class GameInformation {
		public boolean gameStarted;
		public ArrayList<Player> PlayersInGame = new ArrayList<Player>();
		public ArrayList<Player> PlayersInWaiting = new ArrayList<Player>();
		public Walls[][] CurrentMaze;
		public int MazeSize;

	}

	public void DoGameRoom(final int gameRoomIndex) throws Exception {

		final GameInformation game = new GameInformation();
		System.out.println("Game Room" + gameRoomIndex + " Started ");
		game.MazeSize = 33;
		game.CurrentMaze = new ServerMaze(game.MazeSize).theWalls;

		final MultiUserChat muc = new MultiUserChat(xmpp, "mazegameroom" + gameRoomIndex + "@gameservice.lamplightonline.com");

		try {
			// muc.create(uName);
			muc.join(uName);

			for (Iterator<String> it = muc.getOccupants(); it.hasNext();) {
				String vf = it.next();
				if (vf.endsWith(uName)) {
					continue;
				}

				throw new Exception("No Users allowed before me");

			}
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		muc.addParticipantListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				if (arg0.getFrom().endsWith(uName)) {
					return;
				}

				Presence pre = (Presence) arg0;
				Player p;

				switch (pre.getType()) {
				case available:
					game.PlayersInWaiting.add(p = new Player(arg0.getFrom()));
					System.out.println(p.Name + " Has Joined");

					if (!game.gameStarted && game.PlayersInWaiting.size() > 1) {
						beginMazeTimer(muc, game);

					}

					break;
				case unavailable:
					for (Player pd : game.PlayersInWaiting) {

						if (pd.FullName.equals(arg0.getFrom())) {
							game.PlayersInWaiting.remove(pd);
							break;
						}
					}
					for (Player pd : game.PlayersInGame) {

						if (pd.FullName.equals(arg0.getFrom())) {
							game.PlayersInGame.remove(pd);
							break;
						}
					}
					if (game.PlayersInGame.size() == 0) {
						game.gameStarted = false;
					}
					break;
				}
			}

		});

		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet message) {
				if (message.getFrom().endsWith(uName)) {
					return;
				}

				System.out.println(((Message) message).getBody());
				GameRoomMessage d = GameRoomMessage.Parse(((Message) message).getBody());

				switch (d.Type) {
				case MazeMove:
					if (d.point.X == game.MazeSize - 1 && d.point.Y == game.MazeSize - 1) {
						try {
							muc.sendMessage(new GameRoomMessage(GameRoomMessageType.GameFinish).GenerateMessage());

							for (Player pc : game.PlayersInGame) {
								game.PlayersInWaiting.add(pc);
							}

							if (game.PlayersInWaiting.size() > 1) {
								beginMazeTimer(muc, game);
							}

						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				case GameStarted:
					break;
				case GameFinish:
					game.gameStarted = false;
					game.CurrentMaze = new ServerMaze(33).theWalls;

					// wait and start one up again
					break;
				case Leave:
					break;

				}

			}
		});
		System.out.println("Game Room " + gameRoomIndex + " Done ");
	}

	protected void beginMazeTimer(final MultiUserChat muc, final GameInformation game) {
		try {
			muc.sendMessage(new GameRoomMessage(GameRoomMessageType.GameStarting, 6).GenerateMessage());
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				for (Player pc : game.PlayersInWaiting) {
					game.PlayersInGame.add(pc);
				}

				game.gameStarted = true;
				try {
					muc.sendMessage(new GameRoomMessage(GameRoomMessageType.MazeData, game.CurrentMaze, game.MazeSize).GenerateMessage());
					muc.sendMessage(new GameRoomMessage(GameRoomMessageType.GameStarted).GenerateMessage());
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 6 * 1000);

	}

	public void DoWaitingRoom(final int waitingRoomIndex) throws Exception {
		final ArrayList<Player> players = new ArrayList<Player>();
		final MultiUserChat muc = new MultiUserChat(xmpp, "waitingroom" + waitingRoomIndex + "@gameservice.lamplightonline.com");
		System.out.println("Waiting Room " + waitingRoomIndex + " Started ");
		try {
			// muc.create(uName);
			muc.join(uName);

			for (Iterator<String> it = muc.getOccupants(); it.hasNext();) {
				String vf = it.next();
				if (vf.endsWith(uName)) {
					continue;
				}
				throw new Exception("No Users allowed before me");

			}
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					muc.sendMessage(new WaitingRoomMessage(MessageType.Ping).GenerateMessage());
				} catch (XMPPException e) {
					e.printStackTrace();
				}

			}
		}, 100, 50 * 1000);

		muc.addParticipantListener(new PacketListener() {
			@Override
			public void processPacket(Packet arg0) {
				if (arg0.getFrom().endsWith(uName)) {
					return;
				}

				Presence pre = (Presence) arg0;
				Player p;
				WaitingRoomMessage vm;
				switch (pre.getType()) {
				case available:
					players.add(p = new Player(arg0.getFrom()));

					vm = new WaitingRoomMessage(MessageType.Chat, "*", p.Name + " Has Joined");
					try {
						muc.sendMessage(vm.GenerateMessage());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println(p.Name + " Has Joined");

					break;
				case unavailable:
					for (Player pd : players) {

						if (pd.FullName.equals(arg0.getFrom())) {
							players.remove(pd);
							vm = new WaitingRoomMessage(MessageType.Chat, "*", pd.Name + " Has Left");
							try {
								muc.sendMessage(vm.GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println(pd.Name + " Has Left");

							break;
						}
					}
					break;
				}
			}
		});

		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet message) {
				if (message.getFrom().endsWith(uName)) {
					return;
				}

				System.out.println(((Message) message).getBody());
				for (Player p : players) {
					if (p.FullName.equals(message.getFrom())) {
						WaitingRoomMessage d = WaitingRoomMessage.Parse(((Message) message).getBody());
						switch (d.Type) {
						case Chat:
							System.out.println("Chat: " + d.Argument);
							break;
						case TurnStatusOff:
							p.IsReady = false;

							System.out.println(p.Name + "is not ready");
							break;
						case TurnSudokuStatusOn:
							p.IsReady = true;
							try {
								muc.sendMessage(new WaitingRoomMessage(MessageType.JoinSudokuRoom, p.Name, "sudokugameroom" + waitingRoomIndex)
										.GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println(p.Name + "is ready to play Sudoku");
							break;
						case TurnDrawStatusOn:
							p.IsReady = true;
							try {
								muc.sendMessage(new WaitingRoomMessage(MessageType.JoinDrawRoom, p.Name, "drawgameroom" + waitingRoomIndex).GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println(p.Name + "is ready to play Draw");
							break;
						case TurnMazeStatusOn:
							p.IsReady = true;
							try {
								muc.sendMessage(new WaitingRoomMessage(MessageType.JoinMazeRoom, p.Name, "mazegameroom" + waitingRoomIndex).GenerateMessage());
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.out.println(p.Name + "is ready to play Maze");
							break;

						}
					}
				}

			}
		});
		System.out.println("Waiting Room " + waitingRoomIndex + " Done");
	}
}
