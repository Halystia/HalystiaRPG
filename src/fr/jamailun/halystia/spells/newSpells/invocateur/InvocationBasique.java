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

public class InvocationBasique extends InvocationSpell {
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), 1)) {
			p.sendMessage(ChatColor.RED+"Tu as atteint le nombre maximum d'invocation de ce type : " + LIMIT + ".");
			return false;
		}
		
		Block block = getLookedBlock(p, 6);
		if(block == null) {
			return false;
		}
		
		IronGolem golem = p.getWorld().spawn(block.getLocation().add(0, 1, 0), IronGolem.class);
		golem.setCustomName(ChatColor.YELLOW + "Golem de " + ChatColor.GOLD +  p.getName());
		
		golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		golem.setHealth(20.0);
		
		addInvocation(golem, p, false, 5);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(golem.isValid())
					golem.damage(100);
			}
		}.runTaskLater(main, 20*50);
		
		return true;
	}

	@Override
	public String getName() {
		return "Invocation basique";
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
		return 1;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Invoque un golem de fer",
			ChatColor.GRAY + "Golem : " + ChatColor.RED + "20 PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "5 dmgs" + ChatColor.GRAY + ".",
			ChatColor.GRAY + "Durée des invocations : " + ChatColor.GREEN + 50 + "s" + ChatColor.GRAY + "."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-invocBas";
	}

	@Override
	public int getManaCost() {
		return 7;
	}

	@Override
	public int getCooldown() {
		return 1;
	}

}
