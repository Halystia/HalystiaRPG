package fr.jamailun.halystia.spells.newSpells.archer;

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
import fr.jamailun.halystia.utils.RandomString;

public class AcierEffrene extends InvocationSpell {

	public final static int ARROWS = 40;
	public final static int DAMAGES = 4;
	public final static int POWER = 5;
	
	@Override
	public boolean cast(Player p) {
		final Invocator thiis = this;
		for(int i = 0; i < ARROWS; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Arrow a = p.launchProjectile(Arrow.class);
					a.setVelocity(p.getLocation().getDirection().multiply(POWER));
					a.setPickupStatus(PickupStatus.DISALLOWED);
					a.setPierceLevel(5);
					a.setShooter(p);
					if(RandomString.randInt(1, 100) < 20)
						a.setFireTicks(300);
					a.setCustomNameVisible(false);
					HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add((Entity)a, p, false, thiis, DAMAGES);
					new BukkitRunnable() {
						@Override
						public void run() {
							if(a.isValid())
								a.remove();
						}
					}.runTaskLater(HalystiaRPG.getInstance(), 15*20L);
					
					for(Player pl : getPlayersAroundPlayer(p, 80, true))
						pl.playSound(p.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.5f, .7f);
				}
			}.runTaskLater(main, i*1L);
		}
		return true;
	}

	@Override
	public String getName() {
		return "Acier effréné";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.RED;
	}

	@Override
	public Classe getClasseRequired() {
		return Classe.ARCHER;
	}

	@Override
	public int getLevelRequired() {
		return 50;
	}

	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY+"Détruisez vos adversaires");
		lore.add(ChatColor.GRAY+"en toute sécurité !");
		return lore;
	}

	@Override
	public String getStringIdentification() {
		return "a-AceEff";
	}

	@Override
	public int getManaCost() {
		return 25;
	}

	@Override
	public int getCooldown() {
		return 6;
	}

}
