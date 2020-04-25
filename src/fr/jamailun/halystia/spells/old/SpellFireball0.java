package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellFireball0 extends Spell {
	
	public String getStringIdentification() {
		return "fb0";
	}
	
	public boolean cast(Player p) {
		Fireball ball = p.launchProjectile(Fireball.class);
		ball.setBounce(false);
		ball.setIsIncendiary(false);
		ball.setInvulnerable(false);
		ball.setYield(1);
		p.sendMessage(ChatColor.GOLD + "Piou piou piouuuuu ! Attention sa pique !");
		
		scheduleRemoveEntity(ball, 10);
		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 10;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Invoque une petite boule de feu");
		lore.add(ChatColor.GRAY+"qui ne fera que plus de");
		lore.add(ChatColor.GRAY+"peur que de mal...");
		return lore;
	}

	@Override
	public String getName() {
		return "Feu follet";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 2;
	}

}
