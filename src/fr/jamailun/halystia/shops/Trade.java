package fr.jamailun.halystia.shops;

import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.players.PlayerData;
import fr.jamailun.halystia.utils.ItemBuilder;

public class Trade implements Comparable<Trade> {
	
	private ItemStack toSell;
	private List<ItemStack> toTrade;
	private int levelRequired;
	private Classe classe;
	
	private final String key;
	
	public Trade(String key, Classe classe, ItemStack toSell, List<ItemStack> toTrade, int levelRequired) {
		if(toSell == null)
			this.toSell = null;
		else
			toSell = new ItemStack(toSell);
		this.classe = classe;
		this.key = key;
		this.toTrade = new ArrayList<>(toTrade);
		this.levelRequired = levelRequired;
	}
	
	public boolean canAfford(Player p) {
		for(ItemStack item : toTrade) {
			if( ! playerHasItems(p, item, item.getAmount()))
				return false;
		}
		return true;
	}
	
	public boolean trade(Player p) {
		return trade(p, false);
	}
	
	public boolean trade(Player p, boolean silent) {
		PlayerData pc = HalystiaRPG.getInstance().getClasseManager().getPlayerData(p);
		if(pc.getClasse() != classe && classe != Classe.NONE) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Impossible d'effectuer cet échange, il faut être de la classe " + DARK_RED + classe.getName().toLowerCase() + RED + ".");
			return false;
		}
		if(pc.getLevel() < levelRequired) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Impossible d'effectuer cet échange, il faut être " + classe.getName().toLowerCase() + " de niveau " + DARK_RED + levelRequired + RED + " (tu es niveau " + pc.getLevel() + ")." );
			return false;
		}
		if( ! canAfford(p)) {
			if(!silent)
				p.sendMessage(HalystiaRPG.PREFIX + RED + "Impossible d'effectuer cet échange, tu n'as pas tous les objets requis !");
			return false;
		}
		for(ItemStack item : toTrade) {
			removeItems(p, item, item.getAmount());
		}
		
		if ( toSell != null ) {
			ItemBuilder crafted = new ItemBuilder(toSell);
			if(!silent && classe != Classe.NONE)
				crafted.addLoreLine(TradeManager.COLOR_LORE_CLASSE + TradeManager.LORE_CLASSE + classe.getName().toLowerCase());
			
			boolean done = false;
			for(ItemStack stack : p.getInventory().getStorageContents()) {
				if(stack == null) {
					p.getInventory().addItem(crafted.toItemStack());
					done = true;
					break;
				}
			}
			if(!done)
				p.getWorld().dropItemNaturally(p.getLocation(), crafted.toItemStack());
		}
		
		if(!silent)
			p.sendMessage(HalystiaRPG.PREFIX + GREEN + "L'échange est un succès !");
		return true;
	}
	
	
	private boolean playerHasItems(Player p, ItemStack items, int amount) {
		Inventory inv = p.getInventory();
		int qtt = 0;
		for(ItemStack item : inv.getContents())
			if(item != null)
				if(item.getType() != Material.AIR)
					if(areItemsTheSame(item, items))
						qtt += item.getAmount();
		if(qtt >= amount)
			return true;
		return false;
	}
	
	private void removeItems(Player p, ItemStack items, int amount) {
		Inventory inv = p.getInventory();
		for(ItemStack item : inv.getContents()) {
			if(amount <= 0)
				break;
			
			if(item == null)
				continue;
			if(item.getType() == Material.AIR)
				continue;
			
			if(areItemsTheSame(item, items)) {
				//p.sendMessage(ITALIC + "§a(amount="+amount+")§r   trouvé " + items.getType() + " §6§l("+item.getAmount()+")");
				if(item.getAmount() < amount) {
					//p.sendMessage(ITALIC + "§7On enlève tout !");
					amount -= item.getAmount();
					item.setAmount(0);
					continue;
				} else {
					//p.sendMessage(ITALIC + "§7On retire simplement ["+amount+"]");
					item.setAmount(item.getAmount() - amount);
					break;
				}
			}
		}
		p.updateInventory();
	}
	
	public static boolean areItemsTheSame(ItemStack a, ItemStack b) {
		if(a.getType() != b.getType())
			return false;
		if(a.hasItemMeta() != b.hasItemMeta())
			return false;
		if(!a.hasItemMeta())
			return true;
		ItemMeta aa = a.getItemMeta();
		ItemMeta bb = b.getItemMeta();
		if(aa.hasDisplayName() != bb.hasDisplayName())
			return false;
		if(aa.hasDisplayName()) {
			if( ! aa.getDisplayName().equals(bb.getDisplayName()))
				return false;
		}
		if(aa.hasLore() != bb.hasLore())
			return false;
		if(aa.hasLore()) {
			if(aa.getLore().size() != bb.getLore().size())
				return false;
			for(int i = 0; i < aa.getLore().size(); i++) {
				if( ! aa.getLore().get(i).equals(bb.getLore().get(i)))
					return false;
			}
		}
		return true;
	}
	
	public Classe getClasse() {
		return classe;
	}
	
	public ItemStack getItemToSell() {
		return new ItemStack(toSell);
	}
	
	public int getLevelRequired() {
		return levelRequired;
	}
	
	public List<ItemStack> getItemNeeded() {
		return new ArrayList<>(toTrade);
	}
	
	public String getKey() {
		return key;
	}
	
	public ItemStack getIcone() {
		ItemStack toSell = getItemToSell();
		ItemBuilder builder = new ItemBuilder(toSell).setName(YELLOW + "" + (toSell.hasItemMeta() ? toSell.getItemMeta().getDisplayName() :  toSell.getType()) + GRAY + (toSell.getAmount()>1 ? "x"+toSell.getAmount() : ""));
		for(ItemStack item : getItemNeeded()) {
			builder.addLoreLine(GRAY + " - [" + (item.hasItemMeta() ? item.getItemMeta().getDisplayName() : WHITE +""+ item.getType()) + GRAY + "] " + (item.getAmount()>1 ? "x"+item.getAmount() : ""));
		}
		builder.addLoreLine(GRAY + "Niveau requis : " + LIGHT_PURPLE + getLevelRequired());
		builder.addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
		return builder.toItemStack();
	}

	@Override
	public int compareTo(Trade o) {
		return levelRequired - o.getLevelRequired();
	}
	
}
