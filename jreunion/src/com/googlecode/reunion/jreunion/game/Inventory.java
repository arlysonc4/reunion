package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.Network;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Inventory {
	private List<InventoryItem> items;

	private boolean success = false;
	
	private Item[][][] inventory=new Item[3][8][6];

	private InventoryItem selected = null;

	private Player player;

	public Inventory(Player player) {
		this.player = player;
		items = new Vector<InventoryItem>();
	}

	public void addItem(Item item) {
		success = false;

		for (int tab = 0; tab < 3; tab++) {
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 6; y++) {
					if (success == true) {
						return;
					}

					addItem(x, y, item, tab);
				}
			}
		}
	}

	public void addItem(int posX, int posY, Item item, int tab) {

		InventoryItem inventoryItem = new InventoryItem(item, posX, posY,
				tab);

		if (itemFit(tab, posX, posY, item.getSizeX(), item.getSizeY()) == true) {
			items.add(inventoryItem);
			success = true;
			// Logger.getLogger(Inventory.class).info("Item Inserted\n");
			// PrintInventoryMap(0);
			// PrintInventoryMap(1);
			// PrintInventoryMap(2);
		}

	}

	public boolean freeSlots(int tab, Item item) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 6; y++) {
				if (posEmpty(tab, x, y) == true) {
					if (itemFit(tab, x, y, item.getSizeX(), item.getSizeY())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Iterator<InventoryItem> getInventoryIterator() {
		return items.iterator();
	}

	public InventoryItem getItem(Item item) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem invItem = iter.next();

			if (invItem.getItem() == item) {
				return invItem;
			}
		}
		return null;
	}

	public InventoryItem getItem(int itemId) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem invItem = iter.next();

			if (invItem.getItem().getId() == itemId) {
				return invItem;
			}
		}
		return null;
	}

	public InventoryItem getItem(int tab, int posX, int posY) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem invItem = iter.next();

			for (int x = invItem.getX(); x < invItem.getX()
					+ invItem.getItem().getSizeX(); x++) {
				for (int y = invItem.getY(); y < invItem.getY()
						+ invItem.getItem().getSizeY(); y++) {
					if (x == posX && y == posY && invItem.getTab() == tab) {
						return invItem;
					}
				}
			}
		}
		return null;
	}

	public InventoryItem getItemSelected() {
		return selected;
	}

	public int getSize() {
		return items.size();
	}

	public boolean itemFit(int tab, int posX, int posY, int sizeX, int sizeY) {

		if (posX + sizeX > 8 || posY + sizeY > 6) {
			return false;
		}

		for (int x = posX; x < posX + sizeX; x++) {
			for (int y = posY; y < posY + sizeY; y++) {
				if (posEmpty(tab, x, y) == false) {
					return false;
				}
			}
		}

		return true;
	}

	/****** Manages the Items on the Inventory ******/
	public void handleInventory(Player player, int tab, int posX, int posY) {
	
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		InventoryItem oldInvItem = null;
		InventoryItem newInvItem = null;
		InventoryItem auxItem = null;
		int count = 0;

		if (player.getInventory().posEmpty(tab, posX, posY) == false) {
			if (player.getInventory().getItemSelected() == null) {
				newInvItem = player.getInventory().getItem(tab, posX, posY);
				player.getInventory().removeItem(newInvItem);
				player.getInventory().setItemSelected(newInvItem);
			} else {
				oldInvItem = player.getInventory().getItemSelected();

				for (int x = 0; x < oldInvItem.getItem().getSizeX(); x++) {
					for (int y = 0; y < oldInvItem.getItem().getSizeY(); y++) {
						newInvItem = player.getInventory().getItem(tab,
								posX + x, posY + y);

						if (auxItem != newInvItem) {
							auxItem = newInvItem;
							count++;	
						}

						if (count > 1) {
							return;
						}
					}
				}

				if (newInvItem == null) {
					return;
				}

				oldInvItem.setX(posX);
				oldInvItem.setY(posY);
				oldInvItem.setTab(tab);

				player.getInventory().removeItem(newInvItem);
				player.getInventory().setItemSelected(newInvItem);
				player.getInventory().addItem(posX, posY, oldInvItem.getItem(),
						tab);
			}
		} else {
			newInvItem = player.getInventory().getItemSelected();

			if (newInvItem == null) {
				return;
			}

			Logger.getLogger(Inventory.class).info("Item Selected: " + newInvItem.getItem().getType() + "\n");
			if (newInvItem != null) {
				for (int x = 0; x < newInvItem.getItem().getSizeX(); x++) {
					for (int y = 0; y < newInvItem.getItem().getSizeY(); y++) {
						
						oldInvItem = player.getInventory().getItem(tab, posX + x,
								posY + y);
						if (auxItem != oldInvItem && oldInvItem != null) {
							auxItem = oldInvItem;
							count++;
						}
						if (count > 1) {
							return;
						}
						
					}
				}
			}
				
		

			if (auxItem == null) {
				player.getInventory().setItemSelected(null);
			} else {
				player.getInventory().removeItem(auxItem);
				player.getInventory().setItemSelected(auxItem);
			}

			newInvItem.setX(posX);
			newInvItem.setY(posY);
			newInvItem.setTab(tab);
			player.getInventory()
					.addItem(posX, posY, newInvItem.getItem(), tab);
		}
	}

	public boolean posEmpty(int tab, int posX, int posY) {

		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem item = iter.next();

			for (int x = item.getX(); x < item.getX()
					+ item.getItem().getSizeX(); x++) {
				for (int y = item.getY(); y < item.getY()
						+ item.getItem().getSizeY(); y++) {
					if (x == posX && y == posY && item.getTab() == tab) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void PrintInventoryMap(int tab) { // Debug Only
		boolean[][] newInvMap = new boolean[8][6];

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 6; y++) {
				newInvMap[x][y] = false;
			}
		}

		Logger.getLogger(Inventory.class).debug("Tab " + tab + ": \n");
		Iterator<InventoryItem> iter = getInventoryIterator();
		while (iter.hasNext()) {
			InventoryItem item = iter.next();

			for (int x = item.getX(); x < item.getX()
					+ item.getItem().getSizeX(); x++) {
				for (int y = item.getY(); y < item.getY()
						+ item.getItem().getSizeY(); y++) {
					if (item.getTab() == tab) {
						newInvMap[x][y] = true;
					}
				}
			}
		}

		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 8; x++) {
				if (newInvMap[x][y] == false) {
					Logger.getLogger(Inventory.class).debug("0");
				}
				if (newInvMap[x][y] == true) {
					Logger.getLogger(Inventory.class).debug("1");
				}
			}
		}
	}

	public void removeItem(InventoryItem invItem) {
		if (invItem == null) {
			return;
		}

		while (items.contains(invItem)) {
			items.remove(invItem);
		}
	}

	public void setItemSelected(InventoryItem selected) {
		// PrintInventoryMap(0);
		this.selected = selected;
	}

	public void use(int tab, int x, int y) {

		InventoryItem invItem = getItem(tab,x,y);
		
		Item item = invItem.getItem();
		
		player.getPosition().getLocalMap().getWorld().getCommand().useItem(player, item);
		
		removeItem(invItem);
		
	}
}