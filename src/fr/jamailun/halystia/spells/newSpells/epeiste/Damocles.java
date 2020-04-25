package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class Damocles extends Spell {

	public final static int DURATION = 30;
	public final static Set<UUID> damoclers = new HashSet<>();
	public static final double DAMAGES = 4;
	
	@Override
	public boolean cast(Player p) {
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vos dégâts sont doublés, mais vous perdrez 2 coeurs par attaque durant les "+DURATION+" prochaines secondes.");
		damoclers.add(p.getUniqueId());
		new BukkitRunnable() {
			private int time = 0;
			@Override
			public void run() {
				if(!p.isValid()) {
					stop();
					return;
				}
				time += 1;
				if(time >= DURATION) {
					stop();
					return;
				}
				spawnParticles(p.getLocation(), Particle.CRIT, 50, 0.2, 0.2, .5);
			}
			private void stop() {
				cancel();
				damoclers.remove(p.getUniqueId());
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
		return 15;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
