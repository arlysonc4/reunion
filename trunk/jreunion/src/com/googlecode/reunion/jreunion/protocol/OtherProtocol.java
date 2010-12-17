package com.googlecode.reunion.jreunion.protocol;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Map;
import com.googlecode.reunion.jreunion.server.Server;

public class OtherProtocol extends Protocol {

	public static Pattern locationRegex = Pattern.compile("(?:place|walk) (-?\\d+) (-?\\d+) (-?\\d+) (-?\\d+)(?: (-?\\d+) (-?\\d+))?\\n");

	int [] location = new int [4];
	public int iter = -1;
	public short iterCheck = -1;
	
	public OtherProtocol(Client client) {
		super(client);
		if(client!=null){
			setAddress(getClient().getSocketChannel().socket().getLocalAddress());
			setPort(getClient().getSocketChannel().socket().getLocalPort());
			setVersion(version = getClient().getVersion());
			for(Map map :Server.getInstance().getWorld().getMaps()){
				if(map.getAddress().getAddress().equals(address)&&map.getAddress().getPort()==port) {
					mapId = map.getId();
				}
			}
		}
	}
	int magic0 = -1;
	int magic1 = -1;
	
	private InetAddress address;
	private int port = 4005;
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
		this.magic0 = magic(address,0);
		this.magic1 = magic(address,1);
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	private int version = -1;
	private int mapId = 4;

	@Override
	public String decryptServer(byte[] data) {
			
		String result = decryptServer(data, iter, iterCheck);		
		if(result.contains("walk")||result.contains("place")) {
			Matcher matcher = locationRegex.matcher(result);
			while(matcher.find()) {
				for(int i=0;i<location.length;i++){
					location[i]= Integer.parseInt(matcher.group(i+1));
				}
			}
		}
		if(result.contains("encrypt_key")){
			
			System.out.println("place: "+location[0] +" "+ location[1] +" "+ location[2]);
			int v17 = magic1 + location[0] + location[1] - location[2];
			int magicx = v17 ^ v17 - v17;
			
			iter =  magicx%4;
			iterCheck += ( magic1 ^ (magicx + 2 * magic1 - mapId));
			System.out.println("magics: "+magicx+" "+iter+" "+iterCheck);
		}
		
		return result;
	}
	
	public String decryptServer(byte[] data, int iter, int iterCheck) {
		String result = null;
		switch(iter + 1){
			case 0:
				int magic3 = (port - 17) % 131;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((magic0 ^ data[i]) + 49) ^ magic3);
				}
				result = new String(data);
				break;
			case 1:
				int magic4 = iterCheck - version + 10;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(data[i]^magic4^version);
				}
				result = new String(data);
				break;
			case 2:
				int magic5 = iterCheck +magic1-magic0;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^magic5)-19)^mapId);
				}
				result = new String(data);
				break;
			case 3: // ?
				int magic6 = port + 3 * mapId + mapId % 3;
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^magic6)^iterCheck)+4);
				}
				result = new String(data);
				break;
			case 4: 
				for(int i=0;i<data.length;i++){
					data[i]=(byte)(((data[i]^(iterCheck+111))+33)^version);
				}
				result = new String(data);
				break;
		}
		if(result==null)
			throw new RuntimeException("Unable to Decrypt");
		return result;
	}
	
	@Override
	public String decryptClient(byte[] data){
		
		int magic4 = magic0 - port - mapId + version;
		for(int i=0;i<data.length;i++){
			
			int step1 = magic1 ^ data[i];
			int step2 = step1 - 19;			
			int step3 = step2 ^ magic4;			
			data[i] = (byte)step3;
		}		
		return new String(data);
	}
	
	@Override
	public byte[] encryptClient(String data){
		
		return null;
	}
	
	@Override
	public byte[] encryptServer(String packet) {
	
		//refresh version because its not always available on connect
		if(getVersion() == -1&&getClient()!=null)
			setVersion(getClient().getVersion());
		if(mapId==-1) {
			throw new RuntimeException("Invalid Map");
		}
		int magic4 = magic0 - port - mapId + version;
		byte [] data = packet.getBytes();
		for(int i = 0; i<data.length; i++) {
			int rstep3 = data[i] ^ magic4;
			int rstep2 = rstep3 + 19;
			int rstep1 = magic1 ^ rstep2;
			data[i] = (byte)rstep1;
		}
		return data;
		
	}
	
	public static int magic(InetAddress ip, int mask)
	{
		byte [] rip = ip.getAddress();
		
		if ( mask == 1 )
			return rip[0] ^ rip[1] ^ rip[2] ^ rip[3];
		else
			return rip[0] + rip[1] + rip[2] + rip[3];	   
	}
	
	public static void main(String[] args) {
		
		int [] place = new int[4];
		String result = "walk 6973 5281 106 -10650 14 1\n";
		
		Matcher matcher = locationRegex.matcher(result);
		while(matcher.find()){
			System.out.println(matcher.group());
			System.out.println(matcher.groupCount());
			for(int i=0;i<4;i++){
				System.out.println(matcher.group(i+1));
				place[i]= Integer.parseInt(matcher.group(i+1));
			}
		}
		
	}
}
