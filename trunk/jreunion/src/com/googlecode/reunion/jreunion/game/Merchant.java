package com.googlecode.reunion.jreunion.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Reference;
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
		ParsedItem npc = Reference.getInstance().getNpcReference().getItemById(type);
		
		
		setSellRate(Integer.parseInt(npc.getMemberValue("SellRate")));
		setBuyRate(Integer.parseInt(npc.getMemberValue("BuyRate")));
		setShop(npc.getMemberValue("Shop"));

		loadItemList();
		try {
			shopReference.Parse("data/"+getShop());
		} catch (IOException e) {
			Logger.getLogger(this.getClass()).warn("Exception",e);
		}
			
	}

	public void loadItemList() {


		if (shopReference != null) {
	
			itemsList.clear();
	
			Iterator<ParsedItem> iter = shopReference.getItemListIterator();
	
			while (iter.hasNext()) {
	
				ParsedItem i = iter.next();
	
				if (!i.checkMembers(new String[] { "Type" })) {
					Logger.getLogger(Merchant.class).info("Error loading a Npc Shop Item on map: "
							+ getPosition().getMap());
					continue;
				}
				VendorItem item = new VendorItem(Integer.parseInt(i.getMemberValue("Type")));
				addItem(item);
			}
		}
	}

	
	

	/****** Buy items from merchant shop ******/
	public void buyItem(Player player, int itemType, int tab,
			int quantity) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		// if(player.getInventory().freeSlots(tab) == false)
		// return;

		Item item = ItemFactory.create(itemType);
		
		int count = 0;

		if (player.getLime() - item.getPrice() < 0) {
			String packetData = "msg Not enough lime.\n";
					client.SendData(packetData);
			return;
		}

		for (int i = 0; i < quantity; i++) {
			if (player.getInventory().freeSlots(tab, item) == false) {
				break;
			}
			item = ItemFactory.create(itemType);
			
			if (item != null) {
			player.pickItem(item);
			count++;
			}
			
		}
		if (item != null) {
		player.updateStatus(10, item.getPrice() * this.getBuyRate() / 100 * -1 * count, 0);
		
		
		}

	}

	/****** Open Merchant Shop ******/
	public void openShop(Player player) {

		Client client = player.getClient();


		String packetData = "shop_rate " + this.getBuyRate() + " "
				+ this.getSellRate() + "\n";
				client.SendData(packetData);

		Iterator<VendorItem> itemListIter = this.itemsListIterator();

		while (itemListIter.hasNext()) {
			VendorItem item = itemListIter.next();

			packetData = "shop_item " + item.getType() + "\n";
					client.SendData(packetData);
		}
	}

	/****** Sell items to merchant shop ******/
	public void sellItem(Player player) {
		
		try {
			Item item = player.getInventory().getItemSelected().getItem();
		
			if (item != null) {
				player.updateStatus(10, (item.getPrice() * (this.getSellRate() / 100)), 0);
				player.getInventory().setItemSelected(null);
				DatabaseUtils.getInstance().deleteItem(item);
			}
			
		} catch (Exception e) {
			Logger.getLogger(Merchant.class).error("Item Sell bug");
		}
	}
}