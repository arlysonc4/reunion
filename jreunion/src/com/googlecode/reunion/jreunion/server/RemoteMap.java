package com.googlecode.reunion.jreunion.server;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.Event;

public class RemoteMap extends Map {

	public RemoteMap(int id) {
		super(id);
	}
	

	@Override
	public void load() {
		super.load();
		Logger.getLogger(RemoteMap.class).info("Remote server registered on "+getAddress().getHostName()+":"+getAddress().getPort()+" for "+this.getName());
	}


	@Override
	public void handleEvent(Event event) {

	}

}
