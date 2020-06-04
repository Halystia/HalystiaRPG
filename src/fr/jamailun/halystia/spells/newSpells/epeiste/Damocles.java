package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class Damocles extends Spell {
	
	public final static int DURATION = 30;
	public static final double DAMAGES = 6;

	public final static String EFFECT_NAME = "damocles";
	@Override
	public void init() {
		main.getPlayerEffectsManager().registerNewEffect(EFFECT_NAME);
	}
	
	@Override
	public boolean cast(Player p) {
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vos dégâts sont doublés, mais vous perdrez "+(DAMAGES)+" points de dégâts par attaque durant les "+DURATION+" prochaines secondes.");
		
		main.getPlayerEffectsManager().applyEffect(EFFECT_NAME, p, DURATION);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!main.getPlayerEffectsManager().hasEffect(EFFECT_NAME, p)) {
					cancel();
					return;
				}
				spawnParticles(p.getLocation(), Particle.CRIT, 50, 0.2, 0.2, .5);
			}
		}.runTaskTimer(HalystiaRPG.getInstance(), 0, 20L);
		return true;
	}

	@Override
	public String getName() {
		return "Damoclès";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 30;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "La puissance de vos coup est",
			ChatColor.GRAY + "doublée, mais il y aura un prix",
			ChatColor.GRAY + "à payer pour chaque coup donné."
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-damocles";
	}

	@Override
	public int getManaCost() {
		return 25;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
