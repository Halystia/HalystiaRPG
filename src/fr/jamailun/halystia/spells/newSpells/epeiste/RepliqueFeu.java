package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.ArrayList;
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

public class RepliqueFeu extends Spell {

	public final static double RANGE = 1;
	public final static double POWER = 8;
	
	@Override
	public boolean cast(Player p) {
		EffectAndDamageSpellEntity spell = new EffectAndDamageSpellEntity(p.getLocation().add(0, 1.5, 0).clone(), p, 15, new ArrayList<>(), RANGE, false, 3*20, POWER, 0, true);
		
		Vector dir = p.getLocation().getDirection();
		Vector dirr = dir.multiply(1);
		spell.setDirection(dirr);
		spell.addParticleEffect(Particle.LANDING_LAVA, 100, RANGE/3, RANGE/3, .5);
		spell.addSoundEffect(Sound.BLOCK_FIRE_AMBIENT, .5f, 1.5f);
		return true;
	}

	@Override
	public String getName() {
		return "Réplique de feu";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 1;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Un sort très utile pour",
			ChatColor.GRAY + "le pugiliste que vous êtes !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-replifeu";
	}

	@Override
	public int getManaCost() {
		return 4;
	}

	@Override
	public int getCooldown() {
		return 1;
	}

}
