package com.googlecode.reunion.jreunion.game.skills.aidia;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.server.SkillManager;
public class BugSummonSkill extends Skill {

	public BugSummonSkill(SkillManager skillManager,int id) {
		super(skillManager,id);
	}

	@Override
	public int getMaxLevel() {
		return 25;
	}

	@Override
	public int getLevelRequirement(int skillLevel) {
		return skillLevel;
	}
}