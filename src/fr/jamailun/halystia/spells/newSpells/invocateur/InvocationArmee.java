package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;

public class InvocationArmee extends InvocationSpell {
	
	public void init() {
		LIMIT = 3;
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), LIMIT)) {
			p.sendMessage(ChatColor.RED+"Attends que tous les grands golems aient disparus !");
			return false;
		}
		
		Block block = getLookedBlock(p, 10);
		if(block == null) {
			return false;
		}
		for(int i =1; i <= 3; i++) {
			IronGolem golem = p.getWorld().spawn(block.getLocation().add(Math.random(), 1 + Math.random(), Math.random()), IronGolem.class);
			golem.setCustomName(ChatColor.YELLOW + "Golem d'acier de " + ChatColor.GOLD +  p.getName());
			
			golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1500);
			golem.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.3);
			golem.setHealth(1500);
			
			addInvocation(golem, p, false, 80);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					if(golem.isValid())
						golem.damage(3000);
				}
			}.runTaskLater(main, 20*120);
		}
		
		return true;
	}

	@Override
	public String getName() {
		return "Armée d'invocations";
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
		return 50;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Invoque trois golems d'acier",
			ChatColor.GRAY + "Golem d'acier : " + ChatColor.RED + "1500 PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "80 dmgs" + ChatColor.GRAY + ".",
			ChatColor.GRAY + "Durée des invocations : " + ChatColor.GREEN + 120 + "s" + ChatColor.GRAY + "."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-invocArmee";
	}

	@Override
	public int getManaCost() {
		return 40;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
