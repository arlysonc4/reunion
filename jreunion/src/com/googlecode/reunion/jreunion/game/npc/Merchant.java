package com.googlecode.reunion.jreunion.game.npc;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.VendorItem;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.ItemManager;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Merchant extends Npc {

	public Merchant(int type) {
		super(type);
		
		shopReference = new Parser();
		shop = null;
	}
	
	public void addItem(VendorItem item) {
		itemsList.add(item);
	}

	private String shop;

	private Parser shopReference;

	private List<VendorItem> itemsList = new Vector<VendorItem>();
	

	public Iterator<VendorItem> itemsListIterator() {
		return itemsList.iterator();
	}
	
	
	private int sellRate;

	private int buyRate;
	

	public void setBuyRate(int buyRate) {
		this.buyRate = buyRate;
	}

	

	public void setSellRate(int sellRate) {
		this.sellRate = sellRate;
	}
	

	public int getBuyRate() {
		return buyRate;
	}
	public int getSellRate() {
		return sellRate;
	}
	

	public String getShop() {
		return shop;
	}
	
	public void setShop(String shop) {
		this.shop = shop;
	}
	
	
	@Override
	public void loadFromReference(int type) {
		super.loadFromReference(type);
		
		super.setHp(this.getMaxHp());
		
		ParsedItem npc = this.getPosition().getLocalMap().getNpcSpawnReference().getItemById(this.getSpawn().getId());
				
		setSellRate(Integer.parseInt(npc.getMemberValue("SellRate")));
		setBuyRate(Integer.parseInt(npc.getMemberValue("BuyRate")));
		setShop(npc.getMemberValue("Shop"));
		
		
		try {
			shopReference.Parse("data/"+getShop());
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
		loadItemList();
			
	}

	public void loadItemList() {

		
		if (shopReference != null) {
	
			itemsList.clear();
	
			Iterator<ParsedItem> iter = shopReference.getItemListIterator();
	
			while (iter.hasNext()) {
	
				ParsedItem i = iter.next();
	
				if (!i.checkMembers(new String[] { "Type" })) {
					Logger.getLogger(Merchant.class).info("Error loading a Npc Shop Item on map: "
							+ getPosition().getLocalMap());
					continue;
				}
				
				VendorItem item = new VendorItem(Integer.parseInt(i.getMemberValue("Type")));
				addItem(item);
			}
		}
	}

	
	

	/****** Buy items from merchant shop ******/
	public boolean buyItem(Player player, int itemType, int tab, int quantity) {

		Client client = player.getClient();
		ItemManager itemManager = player.getClient().getWorld().getItemManager();

		Item<?> item = itemManager.create(itemType);
		
		int count = 0;

		if (player.getLime() - item.getType().getPrice() * quantity < 0) {
			client.sendPacket(Type.MSG, "Not enough lime.");
			return false;
		}

		for (int i = 0; i < quantity; i++) {
			
			item = itemManager.create(itemType);
			
			if (player.getInventory().freeSlots(tab, item) == false) {
				return false;
			}
			
			if (item != null) {
				player.getPosition().getLocalMap().createEntityId(item);
				player.pickItem(item, -1, tab);
				count++;
			}
			
		}
		
		if (item != null) {
			int cost = item.getType().getPrice() * this.getBuyRate() / 100 * count;
			synchronized(player) {
				player.setLime(player.getLime()-cost);
			
			}
			//player.updateStatus(10, item.getPrice() * this.getBuyRate() / 100 * -1 * count, 0);
		
		}
		return true;
	}

	/****** Open Merchant Shop ******/
	public void openShop(Player player) {

		Client client = player.getClient();

		client.sendPacket(Type.SHOP_RATE, this);
		
		Iterator<VendorItem> itemListIter = this.itemsListIterator();

		while (itemListIter.hasNext()) {
			VendorItem vendorItem = itemListIter.next();
			
			client.sendPacket(Type.SHOP_ITEM, vendorItem);

		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(Player player) {
		
		Item<?> item = player.getInventory().getHoldingItem().getItem();
	
		if (item != null) {
			int price = (int) (item.getType().getPrice() * ((double)this.getSellRate() / 100));
			Logger.getLogger(Merchant.class).info("Player "+player+" sold item "+item+" for "+price+" Lime");
			synchronized(player){
				player.setLime(player.getLime()+price);
			}

			player.getInventory().setHoldingItem(null);
			DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
		}
		else{
			Logger.getLogger(Merchant.class).error("Sell failed, no item selected");			
		}		
	}
}