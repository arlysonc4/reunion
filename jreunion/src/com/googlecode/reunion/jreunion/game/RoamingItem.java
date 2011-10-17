package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Session;

public class RoamingItem extends WorldObject{

	private Item<?> item;
	private Player owner;
	
	public RoamingItem(Item<?> item) {
		this.setItem(item);
	}	
	
	private void setItem(Item<?> item) {
		this.item = item;
	}
	public Item<?> getItem() {
		return item;
	}
	@Override
	public void enter(Session session) {
		session.getOwner().getClient().sendPacket(Type.IN_ITEM, this);
		
	}
	@Override
	public void exit(Session session) {
		session.getOwner().getClient().sendPacket(Type.OUT, this);
	}
	
	
	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

}
