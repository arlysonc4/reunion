package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.items.etc.ScrollOfNAgen;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Exchange {

	private List<ExchangeItem> itemList;

	public Exchange(Player player) {
		itemList = new Vector<ExchangeItem>();
	}

	public void addItem(ExchangeItem item) {
		if (itemList.contains(item)) {
			return;
		}
		
		itemList.add(item);
	}

	public void clearExchange() {
		itemList.clear();
	}

	public ExchangeItem getItem(int posX, int posY) {
		Iterator<ExchangeItem> exchangeIter = itemListIterator();

		while (exchangeIter.hasNext()) {
			ExchangeItem exchangeItem = exchangeIter.next();

			for (int x = 0; x < exchangeItem.getItem().getType().getSizeX(); x++) {
				for (int y = 0; y < exchangeItem.getItem().getType().getSizeY(); y++) {
					if (posX == x + exchangeItem.getPosition().getPosX()
							&& posY == y + exchangeItem.getPosition().getPosY()) {
						return exchangeItem;
					}
				}
			}
		}
		return null;
	}

	public Iterator<ExchangeItem> itemListIterator() {
		return itemList.iterator();
	}

	public int listSize() {
		return itemList.size();
	}

	public void removeItem(ExchangeItem item) {
		if (!itemList.contains(item)) {
			return;
		}
		itemList.remove(item);
	}
	
	public boolean isAllInstanceOfScrollOfNAgen(){
		for(ExchangeItem exchangeItem: itemList){
			Item<?> item = exchangeItem.getItem();
			if(!(item.is(ScrollOfNAgen.class))){
				return false;
			}
		}
		return true;
	}
}