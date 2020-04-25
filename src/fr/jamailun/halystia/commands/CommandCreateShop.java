package fr.jamailun.halystia.commands;

import static org.bukkit.ChatColor.*;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import fr.jamailun.halystia.HalystiaRPG;

public class CommandCreateShop implements CommandExecutor {
	
	private final HalystiaRPG main;
	
	public CommandCreateShop(HalystiaRPG main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if( ! (sender instanceof Player)) {
			sender.sendMessage(RED + "Tu dois être un joueur !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(! HalystiaRPG.isInRpgWorld(p)) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Possible uniquement dans le monde RP !");
			return true;
		}
		
		RayTraceResult targetBlockInfo = p.rayTraceBlocks(10.0D, FluidCollisionMode.NEVER);
		if (targetBlockInfo == null) {
			p.sendMessage(HalystiaRPG.PREFIX + RED + "Impossible de trouver de spawn pour le shop !");
			return true;
		}
		Block targetBlock = targetBlockInfo.getHitBlock();
		BlockFace targetBlockFace = targetBlockInfo.getHitBlockFace();
		assert ((targetBlock != null) && (!targetBlock.isEmpty()));
		assert (targetBlockFace != null);
		
		Location spawnLocation = determineSpawnLocation(p, targetBlock, targetBlockFace);
		
		main.getShopManager().createNewShop(spawnLocation);
		
		p.sendMessage(HalystiaRPG.PREFIX + GREEN + "Shop créé avec succès, n'oubliez pas de le paramétrer !");
		
		return true;
	}
	
	public Location determineSpawnLocation(Player player, Block targetBlock, BlockFace targetBlockFace) {
		assert ((player != null) && (targetBlock != null) && (targetBlockFace != null));
		Block spawnBlock;
		if (targetBlock.isPassable())
			spawnBlock = targetBlock;
		else
			spawnBlock = targetBlock.getRelative(targetBlockFace);
		
		Location spawnLocation = spawnBlock.getLocation().add(0.5D, 0, 0.5D);
		
		spawnLocation.setDirection(player.getEyeLocation().subtract(spawnLocation).toVector());
		return spawnLocation;
	}

}
