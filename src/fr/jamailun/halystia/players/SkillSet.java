package fr.jamailun.halystia.players;

import java.util.Arrays;

public class SkillSet {
	
	public static final String SKILL_FORCE = "puiss";		// Augmente % de coup crit'
	public static final String SKILL_INTELLIGENCE = "intel";	// Augmente la regen de mana
	public static final String SKILL_CONSTITUTION = "consti";	// Diminue baisse la faim
	public static final String SKILL_AGILITE = "agi";			// Augmente % d'esquive
	
	public static final String SEP = ",";
	
	protected static String[] skillArray = new String[] {SKILL_FORCE, SKILL_CONSTITUTION, SKILL_INTELLIGENCE, SKILL_AGILITE};
	
	private int[] skills;
	
	public SkillSet(String serialized) {
		String[] array = serialized.split(SEP);
		skills = new int[skillArray.length];
		for( int i = 0; i < array.length; i++ ) {
			String sk = array[i];
			try {
				skills[i] = Integer.parseInt(sk);
			} catch (NumberFormatException e) {
				System.err.println("Could not deserialize skill set : (" +serialized + ") : BAD INTEGER : ("+sk+").");
				skills[i] = 0;
			}
		}
	}
	
	public SkillSet() {
		skills = new int[skillArray.length];
		for( int i = 0; i < skills.length; i++ ) {
			skills[i] = 0;
		}
	}
	
	public String serialize() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < skills.length; i++) {
			builder.append(skills[i]);
			if(i < skills.length - 1)
				builder.append(SEP);
		}
		return builder.toString();
	}
	
	public boolean updateSkill(String skill) {
		int id = -1;
		for(int i = 0; i < skillArray.length; i++) {
			if(skillArray[i].equals(skill)) {
				id = i;
				break;
			}
		}
		if(id == -1)
			return false;
		
		skills[id] = skills[id] + 1;
		
		return true;
	}
	
	public int[] getLevels() {
		return Arrays.copyOf(skills, skills.length);
	}
	
	public int getTotalPoints() {
		int t = 0;
		for(int skill : skills)
			t += skill;
		return t;
	}
	
}