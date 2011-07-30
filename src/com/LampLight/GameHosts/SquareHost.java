package com.LampLight.GameHosts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.LampLight.Server.LampLightHost;
import com.LampLight.Server.LampPlayer;
import com.Server.ClientConnector.GameInformation;

public class SquareHost extends LampLightHost {
	static HashMap<Integer, ArrayList<LampPlayer>> GlobalPlayers = new HashMap<Integer, ArrayList<LampPlayer>>(100);
	Integer myGameIndex;

	public SquareHost(int gameIndex) {
		super(gameIndex);
		GlobalPlayers.put(gameIndex, new ArrayList<LampPlayer>());
		myGameIndex = gameIndex;
	}

	public static LampLightHost SearchForGame(LampPlayer player) {
		return null;
	}
 
	@Override
	public void RecieveNetworkMessage(LampPlayer whoFrom, Serializable content) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionEstablished() {
		super.onConnectionEstablished();

	}

	@Override
	public void onUserLogin(LampPlayer lampPlayer) {
		GlobalPlayers.get(myGameIndex).add(lampPlayer);
	}

	@Override
	public void onUserLogout(LampPlayer lampPlayer) {
		GlobalPlayers.get(myGameIndex).remove(lampPlayer);
	}

}
