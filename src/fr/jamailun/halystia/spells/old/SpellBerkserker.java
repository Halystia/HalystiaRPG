package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellBerkserker extends Spell {

	@Override
	public
	boolean cast(final Player p) {
		
		List<PotionEffect> effects = Arrays.asList(
			new PotionEffect(PotionEffectType.SPEED, 20*20, 1, true, true, true),
			new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*20, 2, true, true, true),
			new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 0, true, true, true),
			new PotionEffect(PotionEffectType.NIGHT_VISION, 20*20, 0, true, true, false),
			new PotionEffect(PotionEffectType.ABSORPTION, 20*20, 0, true, true, false),
			new PotionEffect(PotionEffectType.REGENERATION, 20*20, 1, true, true, false),
			new PotionEffect(PotionEffectType.FAST_DIGGING, 20*20, 2, true, true, false)
		);
		
		p.sendMessage(ChatColor.GOLD + "" + ChatColor.RED + "Sens la rage gronder en toi...");
		for(PotionEffect effect : effects)
			p.removePotionEffect(effect.getType());
		p.addPotionEffects(effects);
		
		for(int i = 0; i <= 19; i++) {
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
				@Override
				public void run() {
					if(p.isDead())
						return;
					if(p.getPotionEffect(PotionEffectType.SPEED) == null && p.getPotionEffect(PotionEffectType.FAST_DIGGING) == null)
						return;
					spawnParticles(p.getLocation(), Particle.LAVA, 50, .5, 1, .4);
					for(Player pl : getPlayersAroundPlayer(p, 40, true))
						pl.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_PIGMAN_ANGRY, .7f, .5f);
				}
			}, i*20);
			
		}

		return true;
	}
	
	@Override
	public String getName() {
		return "Rage de sang";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public
	int getLevelRequired() {
		return 40;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Que tous vos sens s'aiguisent,");
		lore.add(ChatColor.GRAY+"que votre rage vous dévore,");
		lore.add(ChatColor.GRAY+"que vos enemis périssent.");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "rage1";
	}
	
	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 60;
	}
	
}
