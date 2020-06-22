package fr.jamailun.halystia.titles;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GRAY;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.ClasseManager;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.sql.temporary.DataHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class TitleHolder extends PlaceholderExpansion {

	private final TitlesManager titles;
	private final DataHandler bdd;
	private final ClasseManager players;

	public TitleHolder(TitlesManager titles, DataHandler bdd, ClasseManager players) {
		this.titles = titles;
		this.bdd = bdd;
		this.players = players;
	}

	@Override
	public String getAuthor() {
		return "jamailun";
	}

	@Override
	public String getIdentifier() {
		return "rpg";
	}

	@Override
	public String getVersion() {
		return "4.0.0";
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {

		if ( identifier.equals("title") ) {
			String tag = bdd.getCurrentTitleOfPlayer(p);
			if(tag == null)
				return "";
			Title title = titles.getTitleWithTag(tag);
			if(title == null)
				return "";
			return title.getDisplayName();
		}

		if ( identifier.equals("level") ) {
			PlayerData pc = players.getPlayerData(p);
			if(pc == null)
				return ChatColor.GRAY + "[0] ";
			int level = pc.getLevel();
			p.setLevel(level);
			return (pc.getClasse() == Classe.NONE) ? GRAY+"[0] " : Classe.getColor(level)+(level>=100?BOLD+"":"")+"["+level+"] " + pc.getKarmaColor();
		}

		if ( identifier.equals("barText") ) {
			PlayerData pc = players.getPlayerData(p);
			if(pc == null)
				return GRAY + "(Loading)";
			String text = pc.getAmeString() + " " + pc.getManaString();
			return text;
		}
		
		if ( identifier.equals("health") ) {
			PlayerData pc = players.getPlayerData(p);
			if(pc == null)
				return GRAY + "(Loading)";
			return pc.getHealth() + "";
		}

		if ( identifier.equals("barColor") ) {
			PlayerData pc = players.getPlayerData(p);
			if(pc == null)
				return BarColor.BLUE.toString();
			return pc.getManaBarColor().toString();
		}

		if ( identifier.equals("barProgress") ) {
			PlayerData pc = players.getPlayerData(p);
			if(pc == null)
				return "100";
			p.setExp((float)pc.getPercentXp());
			return pc.getManaProgress() + "";
		}

		return "";
	}

}