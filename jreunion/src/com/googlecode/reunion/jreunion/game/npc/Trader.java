package com.googlecode.reunion.jreunion.game.npc;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.game.ExchangeItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.items.equipment.Armor;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.DatabaseUtils;
import com.googlecode.reunion.jreunion.server.ItemManager;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Trader extends Npc {

	public Trader(int id) {
		super(id);
		loadFromReference(id);
	}

	/****** Exchange 5 "grade n" gems for 1 "grade n-1" gem ******/
	/****** or Gem Gambler ******/
	public void chipExchange(Player player, int gemTraderType, int chipType,
			int playerBet) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		ItemManager itemManager = client.getWorld().getItemManager();
		int serverBetResult = (int) (Math.random() * 6);

		Iterator<ExchangeItem> exchangeIter = player.getExchange()
				.itemListIterator();

		while (exchangeIter.hasNext()) {
			ExchangeItem exchangeItem = exchangeIter.next();
			Item<?> item = exchangeItem.getItem();
			DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
		}

		player.getExchange().clearExchange();

		String packetData = new String();

		if (gemTraderType == 0) {
			int newChipType = getNewChipTypeUp(chipType);
			Item<?> item = itemManager.create(newChipType);
			ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0);
			player.getExchange().addItem(exchangeItem);
			packetData = "chip_exchange 0 ok " + item.getType().getTypeId() + " "
					+ item.getEntityId() + "\n";
		} else {
			if (playerBet == serverBetResult) {
				int newChipType = getNewChipTypeUp(chipType);
				Item<?> item = itemManager.create(newChipType);
				ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0);
				player.getExchange().addItem(exchangeItem);
				packetData = "chip_exchange 1 ok win " + item.getType().getTypeId() + " "
						+ playerBet + " " + item.getEntityId() + "\n";
			} else {
				int newChipType = getNewChipTypeDown(chipType);
				if (newChipType != -1) {
					Item<?> item = itemManager.create(newChipType);
					ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0);
					player.getExchange().addItem(exchangeItem);
				}
				packetData = "chip_exchange 1 ok lose " + newChipType + " "
						+ serverBetResult + "\n";
			}
		}

				client.sendData(packetData);
	}

	/******
	 * Exchange a certain race armor part for another race armor part of the
	 * same level
	 ******/
	public void exchangeArmor(Player player, int armorType) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		ItemManager itemManager = client.getWorld().getItemManager();
		String packetData = new String();

		if (armorType == 0) {
			packetData = "ichange 0 0 0 0 0";
		} else {
			Iterator<ExchangeItem> exchangeIter = player.getExchange()
					.itemListIterator();
			ExchangeItem oldExchangeItem = exchangeIter.next();
			Item<?> newItem = itemManager.create(armorType);
			Item<?> oldItem = Item.load(oldExchangeItem
					.getItem().getItemId());

			if (newItem.is(Armor.class) == false
					|| ((Armor)newItem.getType()).getLevel() != ((Armor)oldItem.getType()).getLevel()) {
				return;
			}

			DatabaseUtils.getDinamicInstance().deleteItem(oldItem.getItemId());
			ExchangeItem newExchangeItem = new ExchangeItem(newItem, 0, 0);

			player.getExchange().clearExchange();
			player.getExchange().addItem(newExchangeItem);
			int cost =  (int) (newItem.getType().getPrice() * 0.333328);
			synchronized(player){							
				player.setLime(player.getLime()-cost);

			}
			packetData = "ichange " + oldExchangeItem.getItem().getEntityId()
					+ " " + newItem.getEntityId() + " " + newItem.getType().getTypeId()
					+ " " + newItem.getGemNumber() + " "
					+ newItem.getExtraStats() + "\n";
		}

				client.sendData(packetData);

	}

	public int getNewChipTypeDown(int chipType) {

		int newChipType = 0;

		switch (chipType) {
		case 521: {
			newChipType = 528;
			break;
		}
		case 522: {
			newChipType = 529;
			break;
		}
		case 523: {
			newChipType = 530;
			break;
		}
		case 524: {
			newChipType = 531;
			break;
		}
		case 525: {
			newChipType = 532;
			break;
		}
		case 526: {
			newChipType = 533;
			break;
		}
		case 527: {
			newChipType = 534;
			break;
		}
		case 528: {
			newChipType = 535;
			break;
		}
		case 529: {
			newChipType = 536;
			break;
		}
		case 530: {
			newChipType = 537;
			break;
		}
		case 531: {
			newChipType = 538;
			break;
		}
		case 532: {
			newChipType = 539;
			break;
		}
		case 533: {
			newChipType = 540;
			break;
		}
		case 534: {
			newChipType = 541;
			break;
		}
		default:
			newChipType = -1;
		}
		return newChipType;
	}

	public int getNewChipTypeUp(int chipType) {

		int newChipType = 0;

		switch (chipType) {
		case 521: {
			newChipType = 222;
			break;
		}
		case 522: {
			newChipType = 223;
			break;
		}
		case 523: {
			newChipType = 224;
			break;
		}
		case 524: {
			newChipType = 225;
			break;
		}
		case 525: {
			newChipType = 226;
			break;
		}
		case 526: {
			newChipType = 227;
			break;
		}
		case 527: {
			newChipType = 228;
			break;
		}
		case 528: {
			newChipType = 521;
			break;
		}
		case 529: {
			newChipType = 522;
			break;
		}
		case 530: {
			newChipType = 523;
			break;
		}
		case 531: {
			newChipType = 524;
			break;
		}
		case 532: {
			newChipType = 525;
			break;
		}
		case 533: {
			newChipType = 526;
			break;
		}
		case 534: {
			newChipType = 527;
			break;
		}
		case 535: {
			newChipType = 528;
			break;
		}
		case 536: {
			newChipType = 529;
			break;
		}
		case 537: {
			newChipType = 530;
			break;
		}
		case 538: {
			newChipType = 531;
			break;
		}
		case 539: {
			newChipType = 532;
			break;
		}
		case 540: {
			newChipType = 533;
			break;
		}
		case 541: {
			newChipType = 534;
			break;
		}
		default:
			newChipType = -1;
		}
		return newChipType;
	}
}