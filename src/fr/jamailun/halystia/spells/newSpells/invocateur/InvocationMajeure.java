package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;

public class InvocationMajeure extends InvocationSpell {
	
	public void init() {
		LIMIT = 2;
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), LIMIT)) {
			p.sendMessage(ChatColor.RED+"Attends que tous les grands golems aient disparus !");
			return false;
		}
		
		Block block = getLookedBlock(p, 8);
		if(block == null) {
			return false;
		}
		for(int i =1; i <= 2; i++) {
			IronGolem golem = p.getWorld().spawn(block.getLocation().add(Math.random(), 1 + Math.random(), Math.random()), IronGolem.class);
			golem.setCustomName(ChatColor.YELLOW + "Grand golem de " + ChatColor.GOLD +  p.getName());
			
			golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
			golem.setHealth(600);
			
			addInvocation(golem, p, false, 50);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if(golem.isValid())
						golem.damage(2000);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), 20*60);
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Invocation majeure";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GOLD;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public int getLevelRequired() {
		return 30;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Invoque deux grands golems de fer",
			ChatColor.GRAY + "Grand golem : " + ChatColor.RED + "600 PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "50 dmgs" + ChatColor.GRAY + ".",
			ChatColor.GRAY + "Durée des invocations : " + ChatColor.GREEN + 60 + "s" + ChatColor.GRAY + "."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-invocMaj";
	}

	@Override
	public int getManaCost() {
		return 20;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
