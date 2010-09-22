package com.googlecode.reunion.jreunion.events.map;

import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.server.LocalMap;

public class ItemDropEvent extends MapEvent {

	RoamingItem roamingItem;
	public ItemDropEvent(RoamingItem roamingItem) {
		super(roamingItem.getPosition().getMap());
		this.roamingItem = roamingItem;
	}
}
