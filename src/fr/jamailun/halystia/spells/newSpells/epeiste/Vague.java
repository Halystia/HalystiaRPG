package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;
import fr.jamailun.halystia.spells.spellEntity.EffectAndDamageSpellEntity;

public class Vague extends Spell {

	public final static double RANGE = 1.4;
	public final static double POWER = 1.5;
	
	@Override
	public boolean cast(Player p) {
		EffectAndDamageSpellEntity spell = new EffectAndDamageSpellEntity(p.getLocation().add(0, 1.5, 0).clone(), p, 9*2, 10, false, false);
		spell.setDamages(4);
		spell.setYForce(.2);
		Vector dir = p.getLocation().getDirection();
		Vector dirr = dir.multiply(1.5);
		spell.setDirection(dirr);
		spell.addParticleEffect(Particle.WATER_SPLASH, 450, RANGE/1.8, RANGE/5, .1);
		spell.addSoundEffect(Sound.AMBIENT_UNDERWATER_ENTER, .5f, 1.5f);
		return true;
	}

	@Override
	public String getName() {
		return "Vague";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 15;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Repoussez les enemies qui",
			ChatColor.GRAY + "vous chargent sans réfléchir."
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-vague";
	}

	@Override
	public int getManaCost() {
		return 15;
	}

	@Override
	public int getCooldown() {
		return 5;
	}

}
