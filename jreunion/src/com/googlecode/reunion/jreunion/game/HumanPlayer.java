package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Tools;


/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class HumanPlayer extends Player {

	public HumanPlayer(Client client) {
		super(client);
	}

	public int getBaseDmg(Player player) {
		int randDmg, baseDmg = 0;

		randDmg = player.getMinDmg()
				+ (int) (Math.random() * (player.getMaxDmg() - player
						.getMinDmg()));

		baseDmg = (randDmg + getLevel() / 6 + getStrength() + getDexterity() / 4);

		return baseDmg;
	}

	@Override
	public void meleeAttack(LivingObject livingObject) {
		if (livingObject instanceof Mob) {
			meleeAttackMob((Mob) livingObject);
		} else if (livingObject instanceof Player) {
			meleeAttackPlayer((Player) livingObject);
		}
	}

	private void meleeAttackMob(Mob mob) {
		int newHp;

		newHp = mob.getHp() - getBaseDmg(this);

		if (getEquipment().getMainHand() != null) {
			getEquipment().getMainHand().consumn(this);
		}

		if (newHp <= 0) {

			mob.kill(this);

			if (mob.getType() == 324) {
				Item item = ItemFactory.create(1054);

				item.setExtraStats((int) (Math.random() * 10000));

				//pickupItem(item);
				getInventory().addItem(item);
				getQuest().questEnd(this, 669);
				getQuest().questEff(this);
			}
		} else {
			mob.setHp(newHp);
		}
	}

	private void meleeAttackPlayer(Player player) {

	}

	@Override
	public void useSkill(LivingObject livingObject, int skillId) {

	}
	public int getMaxElectricity(){
		return Tools.statCalc(getDexterity(), 30) +(getLeadership() / 2);
	}
	
	public int getMaxHp(){
		return Tools.statCalc(getStrength(), 80) + Tools.statCalc(getConstitution(), 30)+ (getLeadership() / 2);		
	}
	
	public int getMaxMana(){
		return Tools.statCalc(getWisdom(), 50) + (getLeadership() / 2);		
	}
	public int getMaxStamina(){
		return getStrength() + (getLeadership() / 2);
	}
	

	@Override
	public int getBaseDamage() {
		return (getLevel() / 6) + (getDexterity() / 4)+ getStrength();
		
	}
}