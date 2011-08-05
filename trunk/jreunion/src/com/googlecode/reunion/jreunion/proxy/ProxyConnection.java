package com.googlecode.reunion.jreunion.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import com.googlecode.reunion.jreunion.network.Connection;
import com.googlecode.reunion.jreunion.network.protocol.DefaultProtocol;
import com.googlecode.reunion.jreunion.network.protocol.Protocol;
import com.googlecode.reunion.jreunion.server.packets.ClientSerializator;
import com.googlecode.reunion.jreunion.server.packets.FailPacket;
import com.googlecode.reunion.jreunion.server.packets.LoginPacket;
import com.googlecode.reunion.jreunion.server.packets.Packet;
import com.googlecode.reunion.jreunion.server.packets.PassPacket;
import com.googlecode.reunion.jreunion.server.packets.PlayPacket;
import com.googlecode.reunion.jreunion.server.packets.StubPacket;
import com.googlecode.reunion.jreunion.server.packets.UserPacket;
import com.googlecode.reunion.jreunion.server.packets.VersionPacket;

public class ProxyConnection extends Connection<ProxyConnection> {

	public ConnectionState state;
	
	Protocol protocol;
	
	public ConnectionState getState() {
		return state;
	}

	public void setState(ConnectionState state) {
		this.state = state;
	}

	enum ConnectionState{
		ACCEPTED,
		EXPECTING_LOGIN,
		EXPECTING_USERNAME,
		EXPECTING_PASSWORD,
		LOGIN_SERVER,
		GAME_SERVER,
	}
	
	public void write(ClientSerializator packet){
		List<String> packets = packet.readClientPacket();
		byte [] data = protocol.encryptServer(packets);
		this.write(data);
	}
	
	public ProxyConnection(ProxyServer proxy,
			SocketChannel socketChannel) throws IOException {
		super(proxy, socketChannel);
		setState(ConnectionState.ACCEPTED);
	}
	
	public ProxyServer getProxy(){
		return (ProxyServer) getNetworkThread();		
	}
	
	public void onData(ByteBuffer inputBuffer) 
	{
		if(protocol==null){
			protocol = new DefaultProtocol();
		}
		byte [] data = getData();
		
		List<String> packets = protocol.decryptServer(data);
		for(String packetData: packets){
			Packet packet = null;
			switch (state){
				case ACCEPTED:
					short version = Short.parseShort(packetData);
					packet = new VersionPacket(version);
					setState(ConnectionState.EXPECTING_LOGIN);
					break;
				case EXPECTING_LOGIN:
					if(packetData=="login"){
						packet =new LoginPacket();
					}else if(packetData=="play"){
						packet = new PlayPacket();
					}
					if (packet!=null)
					{
						setState(ConnectionState.EXPECTING_USERNAME);
					}
					break;
				case EXPECTING_USERNAME:
					packet = new UserPacket(packetData);
					setState(ConnectionState.EXPECTING_PASSWORD);
					break;
				case EXPECTING_PASSWORD:
					packet = new PassPacket(packetData);
					break;
					
				case LOGIN_SERVER:
					
					break;
					
				case GAME_SERVER:
					
					break;
				
			}
			ProxyServer server = getProxy();			
			server.onPacketReceived(this, packet);
				
		}
		write(new StubPacket("char_list"));
		//write(new FailPacket("derp"));
		
	}

}
