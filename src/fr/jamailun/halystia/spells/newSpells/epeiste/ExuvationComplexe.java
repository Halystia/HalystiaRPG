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

public class ExuvationComplexe extends Spell {

	public final static double RANGE = 50;
	
	private final PotionEffect effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*40, 1);
	
	@Override
	public boolean cast(Player p) {
		p.addPotionEffect(effect);
		Player pl = getClosestPlayerAtRange(p, RANGE);
		if(pl == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.YELLOW + "L'exuvation n'a eu d'effet que sur vous.");
			return true;
		}
		pl.addPotionEffect(effect);
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "L'exuvation a aussi eu un effet sur " + ChatColor.GOLD + pl.getName() + ChatColor.GREEN + ".");
		pl.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Vous recevez un bonus de force grâce à " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + ".");
		return true;
	}

	@Override
	public String getName() {
		return "Exuvation complexe";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.EPEISTE;
	}

	@Override
	public int getLevelRequired() {
		return 40;
	}

	@Override
	public List<String> getLore() {
		return Arrays.asList(
			ChatColor.GRAY + "Augmente votre force ainsi",
			ChatColor.GRAY + "que celle du joueur le plus proche.",
			ChatColor.GRAY + "Soyez mieux préparés que vos adversaires !"
		);
	}

	@Override
	public String getStringIdentification() {
		return "e-exuvComplexe";
	}

	@Override
	public int getManaCost() {
		return 40;
	}

	@Override
	public int getCooldown() {
		return 3;
	}

}
