package fr.jamailun.halystia.spells.newSpells.archer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;

public class Partageforce extends InvocationSpell {
	
	public String getStringIdentification() {
		return "a-Partage";
	}
	
	public boolean cast(final Player p) {
		
		p.sendMessage(ChatColor.DARK_RED + "santa banana n'a rien fait !");
		//TODO mdr
		
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 15;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Echangez votre barre de vie");
		lore.add(ChatColor.GRAY+"avec un autre joueur !");
		return lore;
	}

	@Override
	public String getName() {
		return "Partageforce";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.BLUE;
	}

	@Override
	public
	int getManaCost() {
		return 18;
	}

	@Override
	public
	int getCooldown() {
		return 2;
	}

}
