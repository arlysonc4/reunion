package com.googlecode.reunion.jreunion.game;

import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.events.map.ItemDropEvent;
import com.googlecode.reunion.jreunion.game.quests.objective.Objective;
import com.googlecode.reunion.jreunion.game.quests.reward.Reward;
import com.googlecode.reunion.jreunion.game.quests.type.QuestType;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Quest {

	private int id;
	
	private String description;

	//TODO: move this to another location
	private int tp; //total points

	//TODO: move this to another location
	private int pt; //obtained points
	
	private int minLevel;
	
	private int maxLevel;
	
	private QuestType type;
	
	private List<Objective> objectives;
	
	private List<Reward> rewards;

	/*
	public Quest(Player player, QuickSlotItem quickSlotItem) {
		this.id = getQuest(player, quickSlotItem);
		objectives = new Vector<Objective>();
		rewards = new Vector<Reward>();
	}
	*/
	
	public Quest(int questId) {
		this.id = questId;
		objectives = new Vector<Objective>();
		rewards = new Vector<Reward>();
	}
	
	public int getId(){
		return this.id;
	}

	public int getMinLevel(){
		return this.minLevel;
	}
	
	public int getMaxLevel(){
		return this.maxLevel;
	}
	
	public void setMinLevel(int minLevel){
		this.minLevel = minLevel;
	}
	
	public void setMaxLevel(int maxLevel){
		this.maxLevel = maxLevel;
	}
	
	public String getDescrition(){
		return this.description;
	}
	
	public QuestType getType(){
		return this.type;
	}
	
	public void setType(QuestType type){
		this.type = type;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void addObjective(Objective objective){
		if(objective != null)
			this.objectives.add(objective);
	}
	
	public Objective getObjective(int id){
		for(Objective objective: objectives){
			if(objective.getId() == id)
				return objective;
		}
		return null;
	}
	
	public List<Objective> getObjectiveList(){
		return this.objectives;
	}
	
	public boolean deleteObjective(int id){
		Objective objective = getObjective(id);
		if(objective != null)
			return objectives.remove(objective);
		
		return false;
	}
	
	public boolean objectivesIsEmpty(){
		return objectives.isEmpty();
	}
	
	public void addReward(Reward reward){
		if(reward != null)
			this.rewards.add(reward);
	}
	
	public Reward getReward(int id){
		for(Reward reward: rewards){
			if(reward.getId() == id)
				return reward;
		}
		return null;
	}
	
	public List<Reward> getRewardList(){
		return this.rewards;
	}
	
	public boolean deleteReward(int id){
		Reward reward = getReward(id);
		if(reward != null)
			return rewards.remove(reward);
		
		return false;
	}
	
	public boolean rewardsIsEmpty(){
		return rewards.isEmpty();
	}
	
	/****** Cancel the current player Quest ********/
	public void cancelQuest(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		player.setQuest(null);
		client.sendPacket(Type.SAY, "Quest cancelled.");
		client.sendPacket(Type.QT, "get -1");
	}

	/****** Update Quest Points Obtained ********/
	public void changeQuestPT(Player player, int remainPoints,
			int obtainedPoints) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		client.sendPacket(Type.QT, "pt " + remainPoints + " " + obtainedPoints);

		/****** Quest Points Reached Zero ********/
		if (remainPoints == 0) {
			Item item = ItemFactory.create(1053);

			item.setExtraStats(0);
			item.setGemNumber(0);

			// QuestSecondFase(player);
			
			client.sendPacket(Type.QT, "nt");
			player.getInventory().storeItem(item);
			
		}
	}

	/****** Update Quest Total points ********/
	public void changeQuestTP(Player player, int tp) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		client.sendPacket(Type.QT, "tp " + tp);
	}

	/****** Quest Points Reached Zero ********/
	/*
	 * public void QuestSecondFase(G_Player player){ S_Client client =
	 * S_Server.getInstance().getNetworkModule().getClient(player);
	 * 
	 * if(client==null) return;
	 * 
	 * String packetData = "qt nt\n";
	 * S_Server.getInstance().getNetworkModule().SendPacket
	 * (client.networkId,packetData); }
	 */

	public int getID() {
		return id;
	}

	public int getPT() {
		return pt;
	}

	/****** Handle client packets ********/
	public boolean loadQuest(Player player, QuestType type, int slot) {
		Client client = player.getClient();

		if (client == null) {
			return false;
		}
		
		Item item = player.getQuickSlot().getItem(slot).getItem();
		
		client.sendPacket(Type.QT, "get " + getId());
		client.sendPacket(Type.QT, "quick " + slot + " " + item.getExtraStats());
		return true;
	}

	public int getTP() {
		return tp;
	}

	/****** Quest Eff ********/
	public void questEff(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		client.sendPacket(Type.QT, "eff " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getEntityId());
	}

	/****** Quest End ********/
	public void questEnd(Player player, int questId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		player.setQuest(null);
		
		client.sendPacket(Type.QT, "end " + questId);
	}

	/****** Quest Kill ********/
	public void questKill(Player player) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		int pos = 0;
		int ammount = 1;
		
		client.sendPacket(Type.QT, "kill " + pos + " " + ammount);
		// S> qt kill [Pos] [Ammount]
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setPT(int pt) {
		this.pt = pt;
	}

	public void setTP(int tp) {
		this.tp = tp;
	}

	/****** Quest Spawn Of Ruin ********/
	public void spawnOfRuin(Player player, int slot) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		String packetData = "usq succ " + slot + "\n";
				client.sendData(packetData);
	}
}