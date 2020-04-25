package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.InvocationSpell;
import fr.jamailun.halystia.spells.Invocator;

public class SpellFleches20 extends InvocationSpell {
	
	public String getStringIdentification() {
		return "flech20";
	}
	
	public final static int VAGUES = 10;
	public final static int POWER = 5;
	
	public boolean cast(final Player p) {
		
		final Invocator thiis = this;
		for(int i = 0; i < VAGUES; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					
					Arrow a = p.launchProjectile(Arrow.class);
					a.setVelocity(p.getLocation().getDirection().multiply(POWER));
					a.setPickupStatus(PickupStatus.DISALLOWED);
					a.setPierceLevel(3);
					a.setCustomNameVisible(false);
					HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add((Entity)a, p, false, thiis, 2);
					new BukkitRunnable() {
						@Override
						public void run() {
							a.remove();
						}
					}.runTaskLater(HalystiaRPG.getInstance(), 120L);
					
					for(Player pl : getPlayersAroundPlayer(p, 50, true))
						pl.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.5f, .7f);
				}
			}.runTaskLater(HalystiaRPG.getInstance(), i*3L);
		}

		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 50;
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
		lore.add(ChatColor.GRAY+"Transforme votre regard");
		lore.add(ChatColor.GRAY+"en une nuée de flèches");
		return lore;
	}

	@Override
	public String getName() {
		return "Regard d'acier";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.GRAY;
	}

	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 180;
	}

}
