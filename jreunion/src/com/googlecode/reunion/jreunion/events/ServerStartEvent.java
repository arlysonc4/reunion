package com.googlecode.reunion.jreunion.events;

import com.googlecode.reunion.jreunion.server.Server;

public class ServerStartEvent extends ServerEvent {

	public ServerStartEvent(Server server) {
		super(server);
		
	}
}
