package fr.jamailun.halystia.spells.newSpells.invocateur;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;

public class DiversionDeGrele extends InvocationSpell {
	
	public final static double HEALTH = 12;
	
	@Override
	public void init() {
		LIMIT = 5;
	}
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), 1)) {
			p.sendMessage(ChatColor.RED+"Tu ne peux pas invoquer plus de "+LIMIT+" golems de neige à la fois !");
			return false;
		}
		
		Block block = getLookedBlock(p, 10);
		if(block == null) {
			return false;
		}
		Snowman golem = p.getWorld().spawn(block.getLocation().add(0, 1, 0), Snowman.class);
		golem.setCustomName(ChatColor.DARK_AQUA + "Golem de " + ChatColor.BLUE +  p.getName());
		golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HEALTH);
		golem.setHealth(HEALTH);
		addInvocation(golem, p, false, 0);
		
		return true;
	}

	@Override
	public String getName() {
		return "Diversion de grêle";
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
			ChatColor.GRAY + "Invoque un bonhomme de neige.",
			ChatColor.GRAY + "SnowMan : " + ChatColor.RED + HEALTH + " PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "0 dmgs" + ChatColor.GRAY + "."
		);
	}

	@Override
	public String getStringIdentification() {
		return "i-invocGrele";
	}

	@Override
	public int getManaCost() {
		return 4;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
