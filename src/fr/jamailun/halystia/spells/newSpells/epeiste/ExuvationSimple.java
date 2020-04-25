package fr.jamailun.halystia.spells.newSpells.epeiste;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class ExuvationSimple extends Spell {

	public final static double RANGE = 50;
	
	private final PotionEffect effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*30, 0);
	
	@Override
	public boolean cast(Player p) {
		p.addPotionEffect(effect);
		Player pl = getClosestPlayerAtRange(p.getLocation(), RANGE);
		if(pl == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "L'exuvation n'a eu d'effet que sur vous.");
			return true;
		}
		pl.addPotionEffect(effect);
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "L'exuvation a aussi eu un effet sur " + ChatColor.GOLD + pl.getName() + ChatColor.GREEN + ".");
		pl.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vous recevez un bonus de force grâce à " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + ".");
		return true;
	}

	@Override
	public String getName() {
		return "Exuvation simple";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GREEN;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 1;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Augmente votre force ainsi",
			ChatColor.GRAY + "que celle du joueur le plus proche."
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-exuvSimple";
	}

	@Override
	public int getManaCost() {
		return 3;
	}

	@Override
	public int getCooldown() {
		return 2;
	}

}
