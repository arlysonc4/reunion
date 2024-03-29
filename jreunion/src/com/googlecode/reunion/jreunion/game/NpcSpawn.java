package com.googlecode.reunion.jreunion.game;

import java.util.TimerTask;

import com.googlecode.reunion.jreunion.server.LocalMap;

public class NpcSpawn extends Spawn 
{
	public NpcSpawn(){
		
	}
	
	private int npcType;
	
	public void setNpcType(int npcType) {
		this.npcType = npcType;
	}
	public int getNpcType() {
		return npcType;
	}
	
	/**
	 * @param respawnTime
	 *            The respawnTime to set.
	 */
	public void setRespawnTime(float respawnTime) {
		this.respawnTime = respawnTime;
	}
	
	private float respawnTime; // seconds
	
	/**
	 * @return Returns the respawnTime.
	 */
	public float getRespawnTime() {
		return respawnTime;
	}
	
	public Position spawn() {
		
		LocalMap map = getPosition().getLocalMap();
		
		Npc npc = null;
		
		int npcType = getNpcType();
		npc = Npc.create(npcType);
		
		if(npc==null)
			return null;
		npc.setSpawn(this);
		
		Position position = super.spawn(npc);
		
		npc.loadFromReference(npcType);
		
		return position;
		
	}
	
	public void kill() {
		
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				spawn();				
			}
			
		}, (long) (respawnTime*1000));
	}

}
