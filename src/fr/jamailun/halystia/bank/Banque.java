package fr.jamailun.halystia.bank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FilenameUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.jobs.JobsManager;
import fr.jamailun.halystia.shops.Trade;

public class Banque {

	private Map<UUID, PlayerBanque> viewers;
	private Map<UUID, PlayerBanque> data;
	private final String path;
	private BanqueRules rules;
	
	public Banque(String path, JobsManager jobs) {
		this.path = path;
		viewers = new HashMap<>();
		data = new HashMap<>();
		try {
			Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(f -> {
				String name = FilenameUtils.removeExtension(f.toFile().getName());
				UUID uuid = UUID.fromString(name);
				data.put(uuid, new PlayerBanque(path, uuid));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		rules = new BanqueRules()
				.changeCostForLevel(1, new ArrayList<>())
				.changeCostForLevel(2, new ItemStack(Material.EMERALD_BLOCK, 16))
				.changeCostForLevel(3, jobs.getItemManager().getWithKey("gold2").getRpgItem().toItemBuilder().setAmount(32).toItemStack(), new ItemStack(Material.EMERALD_BLOCK, 32))
				.changeCostForLevel(4, jobs.getItemManager().getWithKey("gold2").getRpgItem().toItemBuilder().setAmount(64).toItemStack(), new ItemStack(Material.EMERALD_BLOCK, 64));
	}
	
	public BanqueRules getCurrentRules() {
		return rules;
	}
	
	public void levelupAccount(Player p) {
		final UUID uuid = p.getUniqueId();
		if( ! data.containsKey(uuid)) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED+"Ouvrez d'abord une fois votre coffre de banque !");
			return;
		}
		if( ! data.get(uuid).canImproveLevel()) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED+"Vous avez déjà un compte de niveau maximum !");
			return;
		}
		int newLevel = data.get(uuid).getLevel() + 1;
		Trade trade = new Trade(null, rules.getCost(newLevel));
		if( ! trade.trade(p, true)) {
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED+"Vous n'avez pas tous les objets requis.");
			StringBuilder builder = new StringBuilder();
			for(ItemStack item : rules.getCost(newLevel)) {
				builder.append(" ").append((item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType() : item.getType()))
					.append(ChatColor.RED.toString())
					.append(" x").append(item.getAmount());
			}
			p.sendMessage(HalystiaRPG.PREFIX + ChatColor.RED+"Prix :"+builder.toString()+".");
			return;
		}
		data.get(uuid).improveLevel();
		p.sendMessage(HalystiaRPG.PREFIX + ChatColor.GREEN + "Félicitations. Votre compte est passé niveau " + newLevel+".");
	}
	
	public void openAccount(Player p) {
		this.openAccount(p, p.getUniqueId());
	}
	
	public void openAccount(Player p, UUID uuid) {
		if(isLooking(uuid))
			close(uuid);
		if( ! data.containsKey(uuid)) {
			PlayerBanque account = new PlayerBanque(path, uuid);
			data.put(uuid, account);
			account.openInventoryToOwner(p);
			viewers.put(uuid, account);
			return;
		}
		viewers.put(uuid, data.get(uuid));
		data.get(uuid).openInventoryToOwner(p);
		return;
	}
	
	public boolean isLooking(UUID uuid) {
		return viewers.containsKey(uuid);
	}
	
	public void close(UUID uuid) {
		if( ! isLooking(uuid))
			return;
		viewers.get(uuid).saveInventory();
		viewers.remove(uuid);
	}
	
	public void changeRules(BanqueRules rules) {
		if(rules == null)
			throw new IllegalArgumentException("BanqueRules cannot be null !");
		this.rules = rules;
	}

	public int getLevelOf(Player p) {
		if(data.containsKey(p.getUniqueId()))
			return data.get(p.getUniqueId()).getLevel();
		return 0;
	}
}