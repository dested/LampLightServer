package com.Server;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.LampLight.GameHosts.SquareHost;
import com.MessageParseJunk.SudokuGameRoomMessage;
import com.MessageParseJunk.SudokuGameRoomMessage.SudokuGameRoomMessageType;

import drawBig.DrawWalls;
import drawBig.SquareGame;

import mazeBig.ServerSudoku;

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
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

public class LampConnector {
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);

		// Reads a single line from the console
		// and stores into name variable

		new LampConnector();

		while (true) {
			String ds = in.nextLine();
			if (ds.toLowerCase().equals("quit"))
				break;

		}

	}

	XMPPConnection xmpp;

	public LampConnector() {
		ConnectionConfiguration config = new ConnectionConfiguration("lamplightonline.com", 5222, "gameService.lamplightonline.com");
		config.setSecurityMode(SecurityMode.disabled);
		config.setCompressionEnabled(true);
		xmpp = new XMPPConnection(config);
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File("HostedGames.xml"));
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		String hostName = doc.getElementsByTagName("LampHosts").item(0).getAttributes().getNamedItem("HostName").getTextContent();
		NamedNodeMap gm = doc.getElementsByTagName("Watcher").item(0).getAttributes();
		String uName = gm.getNamedItem("Name").getTextContent();
		String pWord = gm.getNamedItem("Password").getTextContent();

		try {
			xmpp.connect();
			xmpp.login(uName, pWord);
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

		for (int i = 1; i <= 1; i++) {

			try {
				// DoSudokuGameRoom(i);

				ClassLoader loader = ClassLoader.getSystemClassLoader();

				SquareHost fs = (SquareHost) Class.forName(hostName).getConstructors()[0].newInstance(i);
				fs.UserName = uName;
				fs.xmpp = xmpp;
				fs.onConnectionEstablished();
				// Object main = (SquareHost)loader.loadClass().newInstance();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

 

	public class DrawGameInformation {

		public ArrayList<SquarePlayer> PlayersInGame = new ArrayList<SquarePlayer>();
		public DrawWalls[][] blockData;

		public ArrayList<SquareGame> Games;

	}

}
