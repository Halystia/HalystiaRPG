package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;
import fr.jamailun.halystia.spells.spellEntity.EffectSpellEntity;
import fr.jamailun.halystia.utils.RandomPick;

public class Revenants extends InvocationSpell {
	
	public final static int VAGUES = 5;
	public final static int DELAY = 5;
	public final static int NB_PER_VAGUES = 5;
	public final static double RAYON = 3.0;
	
	public void init() {
		LIMIT = 25;
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), 25)) {
			p.sendMessage(ChatColor.RED+"Attends que tous les revenants aient disparus !");
			return false;
		}
		
		Block block = getLookedBlock(p, 20);
		if(block == null) {
			return false;
		}
		
		Map<Location, Block> map = new HashMap<>();
		final RandomPick<Location> picks = new RandomPick<>(new HashMap<>());
		final Location loc = block.getLocation();
		
		for(int x = (int) (loc.getX() - RAYON); x <= loc.getX() + RAYON; x++) {
			for(int z = (int) (loc.getZ() - RAYON); z <= loc.getZ() + RAYON; z++) {
				Location newLoc = new Location(loc.getWorld(), x, loc.getY(), z);
				if(newLoc.distance(loc) > RAYON * 0.9)
					continue;
				Block bl = newLoc.getBlock();
				if(bl.getType().isSolid()) {
					map.put(newLoc, bl);
					picks.addPick(newLoc);
				}
			}
		}
		
		if(map.size() < RAYON * RAYON * Math.PI / 3) {
			p.sendMessage(ChatColor.RED+"L'espace visé n'est pas assez grand pour le cercle d'invocation !");
			return false;
		}
		
		changeBlocksTemporarely(map, Material.BLACK_CONCRETE, VAGUES*DELAY, 10);

		for(int vague = 0; vague < VAGUES; vague ++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					
					for(int n = 0; n < NB_PER_VAGUES; n++) {
						
						WitherSkeleton sk = p.getWorld().spawn(picks.nextPick().clone().add(0,1.1,0), WitherSkeleton.class);
						sk.setCustomName(ChatColor.RED + "Revenant de " + ChatColor.GOLD +  p.getName());
						
						sk.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5.0);
						sk.setHealth(5.0);
						
						addInvocation(sk, p, false, 5);
						
						new BukkitRunnable() {
							@Override
							public void run() {
								if(sk.isValid())
									sk.damage(100);
							}
						}.runTaskLater(main, 20*VAGUES*DELAY*2);
					}
				}
			}.runTaskLater(main, 20*DELAY*vague);
			
		}
		
		//Effets de particules :
		EffectSpellEntity effect = new EffectSpellEntity(loc, p, 4*VAGUES*DELAY, new ArrayList<>(), 1, false);
		effect.addParticleEffect(Particle.SMOKE_LARGE, 50, RAYON/2, 2, .1);
		effect.addSoundEffect(Sound.ENTITY_ZOMBIE_STEP, 2f, 5f);
		return true;
	}

	@Override
	public String getName() {
		return "Revenants";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 5;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Conjure les forces obscure pour faire",
			ChatColor.GRAY + "revenir à la vie une horde de morts vivants.",
			ChatColor.GRAY + "Revenant : " + ChatColor.RED + "5 PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "5 dmgs" + ChatColor.GRAY + ".",
			ChatColor.GRAY + "Durée des invocations : " + ChatColor.GREEN + (VAGUES*DELAY*2) + "s" + ChatColor.GRAY + "."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-revenants";
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
