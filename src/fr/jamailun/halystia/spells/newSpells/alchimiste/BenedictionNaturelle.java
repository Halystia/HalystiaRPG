package fr.jamailun.halystia.spells.newSpells.alchimiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class BenedictionNaturelle extends Spell {

	public final static long PERIOD = 8L, TOTAL_TIME = 20*20L;
	
	private List<PotionEffect> effects;
	@Override
	public void init() {
		effects = Arrays.asList(
			new PotionEffect(PotionEffectType.ABSORPTION, 20*35, 1, true, true, true),
			new PotionEffect(PotionEffectType.REGENERATION, 20*25, 1, true, true, true)
		);
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		Player target = null;
		double min = 100;
		for ( Player pl : super.getPlayersAroundPlayer(p, 5, false) )  {
			double dd = pl.getLocation().distance(p.getLocation());
			if(dd < min) {
				target = pl;
				min = dd;
			}
		}
		if(target == null) {
			p.sendMessage(ChatColor.RED + "Il faut un joueur à proximité !");
			return false;
		}
		
		for(PotionEffect eff : effects)
			target.addPotionEffect(eff);
		spawnParticles(target.getLocation(), Particle.DRAGON_BREATH, 200, .1, .1, .09);
		
		return true;
	}

	@Override
	public String getName() {
		return "Bénédiction naturelle";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_AQUA;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ALCHIMISTE;
	}

	@Override
	public int getLevelRequired() {
		return 15;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Protégez un joueur allié grâce à ce",
			ChatColor.GRAY + "sort qui saura réchauffer son coeur."
		);
	}

	@Override
	public String getStringIdentification() {
		return "p-beneNatu";
	}

	@Override
	public int getManaCost() {
		return 12;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
