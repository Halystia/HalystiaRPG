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
	
	@Override
	public synchronized boolean cast(Player p) {
		if( ! canInvoke(p.getUniqueId(), 2)) {
			p.sendMessage(ChatColor.RED+"Tu ne peux pas invoquer plus de golems de neige à la fois !");
			return false;
		}
		
		Block block = getLookedBlock(p, 10);
		if(block == null) {
			return false;
		}
		
		for(int i = 1; i <= 2; i++) {
			Snowman golem = p.getWorld().spawn(block.getLocation().add(i*0.01, 1, i*0.01), Snowman.class);
			golem.setCustomName(ChatColor.DARK_AQUA + "Golem de " + ChatColor.BLUE +  p.getName());
			golem.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5.0);
			golem.setHealth(5.0);
			addInvocation(golem, p, false, 0);
		}
		
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
			ChatColor.GRAY + "Invoque deux bonhommes de neige.",
			ChatColor.GRAY + "SnowMan : " + ChatColor.RED + "5 PV" + ChatColor.GRAY + " et " + ChatColor.BLUE + "0 dmgs" + ChatColor.GRAY + "."
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
