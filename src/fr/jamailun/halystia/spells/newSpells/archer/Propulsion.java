package fr.jamailun.halystia.spells.newSpells.archer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class Propulsion extends Spell {
	
	@Override
	public boolean cast(Player p) {
		Block b = p.getLocation().add(0, -1, 0).getBlock();
		if(b.getType() == Material.AIR || b.getType() == Material.CAVE_AIR) {
			p.sendMessage(ChatColor.RED + "Il faut être au sol pour pouvoir se propulser !");
			return false;
		}
		Vector vec = p.getLocation().getDirection();
		
		p.setVelocity(vec.multiply(3.2));
		
		return true;
	}

	@Override
	public String getName() {
		return "Propulsion";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.BLUE;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public int getLevelRequired() {
		return 5;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Propulsez-vous dans une direction");
		lore.add(ChatColor.GRAY+"aux dépends de vos adversaires !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-Propul";
	}
	
	@Override
	public int getManaCost() {
		return 12;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
