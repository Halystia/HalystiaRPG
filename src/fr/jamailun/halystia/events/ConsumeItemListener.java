package fr.jamailun.halystia.events;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.PlayerData;

public class ConsumeItemListener extends HalystiaListener {

	public ConsumeItemListener(HalystiaRPG main) {
		super(main);
	}
	
	@EventHandler
	public void playerConsumeItemEvent(PlayerItemConsumeEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getPlayer()))
			return;
		
		int health = getHealthOfItem(e.getItem());
		if(health > -1) {
			e.getPlayer().setHealth(Math.max(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), e.getPlayer().getHealth() + (health * 2)));
			return;
		}
		
		int manaGiven = main.getPotionManager().getPowerOfBottle(e.getItem());
		if(manaGiven > -1) {
			try {
				PlayerData pc = main.getClasseManager().getPlayerData(e.getPlayer());
				if(pc.addManaRegen(manaGiven)) {
					pc.getPlayer().sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Potion de mana consommée ! Tu regagneras " + ChatColor.GOLD + manaGiven + ChatColor.AQUA + " mana.");
					return;
				}
				pc.getPlayer().sendMessage(HalystiaRPG.PREFIX + ChatColor.AQUA + "Tu as déjà tous tes points de mana !");
				e.setCancelled(true);
				return;
			} catch(NullPointerException ee) {}
		}
	}
	
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent e) {
		if( ! HalystiaRPG.isInRpgWorld(e.getEntity()))
			return;
		e.setCancelled(true);
	}
	
	private int getHealthOfItem(ItemStack item) {
		if(item == null)
			return -1;
		if( ! item.hasItemMeta())
			return -1;
		ItemMeta meta = item.getItemMeta();
		if( ! meta.hasLore())
			return -1;
		List<String> lore = meta.getLore();
		if(lore.size() < 1)
			return -1;
		String line = lore.get(0);
		if(line.startsWith(ChatColor.RED + "+")) {
			String raw1 = line.replace(ChatColor.RED + "+", "");
			String raw = raw1.replace("❤", "");
			try {
				int health = Integer.parseInt(raw);
				return health;
			} catch(NumberFormatException e) {
				return -1;
			}
		}
		
		return -1;
		
	}
}