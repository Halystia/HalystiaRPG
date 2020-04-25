package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class CatapulteCeleste extends Spell {

	public final static double RANGE = 4.5;
	
	@Override
	public boolean cast(Player p) {
		final List<Entity> around = getEntitiesAroundPlayer(p, RANGE, false);
		if(around.isEmpty()) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Aucun joueur à côté de toi !");
			return false;
		}
		
		final double x = p.getLocation().getX();
		final double y = p.getLocation().getY();
		final double z = p.getLocation().getZ();
		
		for(Entity entity : around) {
			if(!(entity instanceof LivingEntity))
				continue;
			LivingEntity en = (LivingEntity) entity;
			en.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20+20+5, 0, false, false, true));
			
			Location loc = en.getEyeLocation();
			Vector vector = new Vector(loc.getX() - x, loc.getY() - y, loc.getZ() - z);
			vector.multiply(1.3);
			en.setVelocity(vector);
			
			if(en instanceof Player) {
				((Player)en).playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, .9f);
			}
		}
		
		for(Player pl : getPlayersAround(p.getLocation(), 100)) {
			pl.spawnParticle(Particle.FLAME, x, y, z, 200, 1, 1, 1, 0.01);
			pl.spawnParticle(Particle.FLASH, x, y, z, 2, 1, 1, 1, 0.5);
			pl.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2f, .4f);
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Catapulte céleste";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
				ChatColor.GRAY + "Conjurez les forces de la terre pour",
				ChatColor.GRAY + "repousser toute entité vous entourant."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-cataCeleste";
	}

	@Override
	public int getManaCost() {
		return 30;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
