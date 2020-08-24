package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.RED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.utils.RpgEquipment;

public class CommandEditEquipment extends HalystiaCommand {
	
	public CommandEditEquipment(HalystiaRPG main) {
		super(main, "edit-equipment");
	}
	
	private final static List<String> attr = Arrays.asList("health", "mana", "armor", "damages", "damageBuff", "level", "life-steal");
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String label, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length < 1) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "/"+label+" <attribut> [valeur]");
			return true;
		}
		
		ItemStack itemS = p.getInventory().getItemInMainHand();
		if(itemS == null) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut un équipement en main !");
			return true;
		}
		RpgEquipment item = new RpgEquipment(itemS);
		
		if( ! attr.contains(args[0])) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Attribut '"+args[0]+"' non reconnu. Attributs valides :\n"+Arrays.toString(attr.toArray()));
			return true;
		}
		
		if( args.length < 2 ) {
			switch(args[0]) {
			case "level":
				p.sendMessage(ChatColor.GRAY + "Level : " + item.getHealth());
				break;
			case "health":
				p.sendMessage(ChatColor.GRAY + "Vie : " + item.getHealth());
				break;
			case "mana":
				p.sendMessage(ChatColor.GRAY + "Mana : " + item.getMana());
				break;
			case "armor":
				p.sendMessage(ChatColor.GRAY + "Armure : " + item.getArmor());
				break;
			case "damages":
				p.sendMessage(ChatColor.GRAY + "Damages : " + item.getDamagesInt());
				break;
			case "damageBuff":
				p.sendMessage(ChatColor.GRAY + "Buff de dégats : " + item.getDamageBuff());
				break;
			case "life-steal":
				p.sendMessage(ChatColor.GRAY + "Vol de vie : " + item.getDamageBuff());
				break;
			}
			return true;
		}
		
		double data;
		try {
			data = Double.parseDouble(args[1]);
		} catch ( NumberFormatException e ) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED + "Il faut un nombre, et '"+args[1]+"' n'en est pas un.");
			return true;
		}
		
		switch(args[0]) {
		case "level":
			item.setLevel((int) data);
			break;
		case "health":
			item.setHealth((int) data);
			break;
		case "mana":
			item.setMana((int)data);
			break;
		case "armor":
			item.setArmor((int)data);
			break;
		case "damages":
			item.setDamagesInt((int)data);
			break;
		case "damageBuff":
			item.setDamageBuff(data);
			break;
		case "life-steal":
			item.setLifeSteal(data);
			break;
		}
		
		p.sendMessage(ChatColor.GREEN + "Succès ! " + ChatColor.AQUA + args[0] + " = " + data + ".");
		p.getInventory().setItemInMainHand(item.toItemStack());
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(args.length == 1)
			return attr.stream().filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
		return new ArrayList<>();
	}
}