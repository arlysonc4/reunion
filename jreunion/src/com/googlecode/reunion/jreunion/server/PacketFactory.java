package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketFactory {

	public static enum Type{
		VERSION_ERROR,
		FAIL,
		OK,
		GO_WORLD,
		GOTO,
		PARTY_DISBAND, 
		HOUR, 
		CHAR_IN, 
		OUT_CHAR, 
		SAY, 
		OUT_ITEM, DROP, IN_NPC
		
	}
	
	//public static final int PT_VERSION_ERROR = 1001;
	//public static final int PT_OK = 1002;
	//parametered arguments
	
	// PacketFactory.createPacket(Type.FAIL);

	public static String createPacket(Type packetType, Object... args) {
		switch (packetType) {
		case VERSION_ERROR: {
			if (args.length == 1) {
				
				
				String clientVersion = (String) args[0];
				String requiredVersion = Reference.getInstance().getServerReference().getItem("Server").getMemberValue("Version");
				String message = "Wrong clientversion: current version "
						+ clientVersion + " required version "
						+ requiredVersion;

				return PacketFactory.createPacket(Type.FAIL, message);
			}
			break;

		}
		case FAIL:{
			String message = "";
			for(Object o: args){
				message+=" "+o;
			}
			return "fail"+message;
		}
		
		case OK: {
			return "OK";
		}
		
		case GO_WORLD:{
			
			if(args.length>0){				
				LocalMap map = (LocalMap)args[0];
				int unknown =  args.length>1?(Integer)args[1]:0;
				InetSocketAddress address = map.getAddress();
				return "go_world "+address.getAddress().getHostAddress()+" "+address.getPort()+" " + map.getId()+" "+unknown;
			}
			break;
		}
		case GOTO:{
			if(args.length>0){
				Position position = (Position)args[0];
			
				return "goto " + position.getX() + " " + position.getY() + " "+position.getZ()+" "	+ position.getRotation();
			
			}
			break;
			
		}
		case PARTY_DISBAND:{
			return "party disband";
			
		}
		case HOUR:{
			
			if(args.length>0){
				int hour = (Integer)args[0];
				return "hour " + hour;
			}
			break;
		}
		
		case CHAR_IN:{
			
			if(args.length>0){
				Player player = (Player)args[0];
				boolean warping = false;
				if(args.length>1){
					warping = (Boolean)args[1];					
				}
				
				int combat = player.getCombatMode()?1:0;

				Equipment eq = player.getEquipment();

				int eqHelmet = -1;
				int eqArmor = -1;
				int eqPants = -1;
				int eqShoulderMount = -1;
				int eqBoots = -1;
				int eqFirstHand = -1;
				int eqSecondHand = -1;
				if (eq.getHelmet() != null) {
					eqHelmet = eq.getHelmet().getType();
				}
				if (eq.getArmor() != null) {
					eqArmor = eq.getArmor().getType();
				}
				if (eq.getPants() != null) {
					eqPants = eq.getPants().getType();
				}
				if (eq.getShoulderMount() != null) {
					eqShoulderMount = eq.getShoulderMount().getType();
				}
				if (eq.getBoots() != null) {
					eqBoots = eq.getBoots().getType();
				}
				if (eq.getMainHand() != null) {
					eqFirstHand = eq.getMainHand().getType();
				}
				if (eq.getOffHand() != null) {
					eqSecondHand = eq.getOffHand().getType();
				}

				int percentageHp = player.getCurrHp() * 100 / player.getMaxHp();
				String packetData = warping?"appear ":"in ";

				packetData += "char " + player.getId() + " " + player.getName()
						+ " " + player.getRace().ordinal() + " " + player.getSex().ordinal() + " "
						+ player.getHairStyle() + " " + player.getPosition().getX()
						+ " " + player.getPosition().getY() + " "
						+ player.getPosition().getZ() + " "
						+ player.getPosition().getRotation() + " " + eqHelmet + " "
						+ eqArmor + " " + eqPants + " " + eqShoulderMount + " "
						+ eqBoots + " " + eqSecondHand + " " + eqFirstHand + " "
						+ percentageHp + " " + combat + " 0 0 0 0 0 0\n";
				// in char [UniqueID] [Name] [Race] [Gender] [HairStyle] [XPos]
				// [YPos] [ZPos] [Rotation] [Helm] [Armor] [Pants] [ShoulderMount]
				// [Boots] [Shield] [Weapon] [Hp%] [CombatMode] 0 0 0 [Boosted] [PKMode]
				// 0 [Guild]
				// [MemberType] 1
				return packetData;
			}
			break;
		}
		case OUT_CHAR:{
			
			if(args.length>0){
				Player player = (Player)args[0];
				return "out char " + player.getId();
			}
			break;
		}
		case SAY:{
			if(args.length>0){
				String text = (String)args[0];
				
				
				LivingObject from = null;
				
				if(args.length>1){
					from = (LivingObject)args[1];
				}
				
				if(from==null) {
				
					return "say "+ -1 +" "+text;
					
				} else { 
					if(args.length>2){
						
						boolean admin = false;
						String name = (String)args[2];
						if(args.length>3) {
							admin = (Boolean)args[3];
							return "say "+from.getId()+" "+name+" " + text + " "+(admin?1:0);	
						}
					}
				}
											
			}
			break;
		}
		
		case DROP:{
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Position position = roamingItem.getPosition();
				Item item = roamingItem.getItem();

				return "drop " + item.getId() + " " + item.getType() + " "
				+ position.getX() + " " + position.getY() + " " + position.getZ() + " "+position.getRotation()+" " + item.getGemNumber() + " "
				+ item.getExtraStats();
				
			}
			
			
		}
		
		case OUT_ITEM:{
			if(args.length>0){
				Item item = (Item)args[0];
				return "out item " + item.getId();				
			}			
		}
		case IN_NPC:{
			
			if(args.length>0){
				Mob mob = (Mob)args[0];
				Boolean spawn = false;
				if(args.length>1){
					
					spawn = (Boolean)args[1];
				}

			int percentageHp = mob.getCurrHp() * 100 / mob.getMaxHp();

			String packetData = "in npc " + mob.getId() + " " + mob.getType()
					+ " " + mob.getPosition().getX() + " "
					+ mob.getPosition().getY() + " 0 "
					+ mob.getPosition().getRotation() + " " + percentageHp + " "
					+ mob.getMutant() + " " + mob.getUnknown1() + " "
					+ mob.getNeoProgmare() + " 0 " + (spawn ? 1 : 0) + " "
					+ mob.getUnknown2() + "\n";
			
			
			}
		}
		
		default:{			
			throw new NotImplementedException();
		}
			

		}
		throw new RuntimeException("Invalid parameters for "+packetType+" message");
	}

	public PacketFactory() {
		super();

	}

}
