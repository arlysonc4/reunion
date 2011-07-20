package com.googlecode.reunion.jreunion.game.skills;

import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;

public class PlaceHolder extends Skill{

	public PlaceHolder(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int level) {
		return 0;
	}

}
