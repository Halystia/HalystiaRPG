package fr.jamailun.halystia.spells.old;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.players.Classe;
import fr.jamailun.halystia.spells.Spell;

public class SpellFireballs10 extends Spell {
	
	public String getStringIdentification() {
		return "fb10";
	}
	
	public final static int BOULES = 8;
	
	public boolean cast(final Player p) {
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, BOULES*16, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, BOULES*16*2, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BOULES*8, 50));
		
		for(int i = 0; i <= BOULES-1; i++) {
			final int j = i;
			Bukkit.getScheduler().runTaskLater(HalystiaRPG.getInstance(), new Runnable() {
				@Override
				public void run() {
					Fireball ball = p.launchProjectile(Fireball.class);
					ball.setBounce(false);
					ball.setIsIncendiary(true);
					ball.setInvulnerable(true);
					ball.setYield(4);
					scheduleRemoveEntity(ball, 10);
					
					for(Player pl : getPlayersAroundPlayer(p, 100, true))
						pl.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.2f, 2f);
						
					if(j == (BOULES-1)) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120*20, 9));
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120*20, 2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120*20, 9));
						p.sendMessage(ChatColor.GRAY+"La puissance vous a traversée."+ChatColor.RED+" Vous sentez votre âme se vider.");
						while(HalystiaRPG.getInstance().getDataBase().getHowManySouls(p) > 0)
							HalystiaRPG.getInstance().getDataBase().looseSoul(p);
						HalystiaRPG.getInstance().getSoulManager().tryRefreshSoul(p);
					}
					
					spawnParticles(p.getLocation(), Particle.ENCHANTMENT_TABLE, 100, 1, .5, .5);
					spawnParticles(p.getLocation(), Particle.FLASH, 1+5*j, 1, 1, 2);
				}
			}, i*15L);
		}
		
		for(Player pl : getPlayersAroundPlayer(p, 200, true)) {
			pl.sendMessage(ChatColor.GRAY+"Vous sentez une puissance colossale et ancienne se déverser aux alentours...");
		}
		
		for(Player pl : getPlayersAroundPlayer(p, 100, true)) {
				pl.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_WITHER, 1.5f, .8f);
		}

		return true;
	}

	@Override
	public
	int getLevelRequired() {
		return 80;
	}

	@Override
	public
	Classe getClasseRequired() {
		return Classe.INVOCATEUR;
	}

	@Override
	public
	List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.RED+"Invoque une pluie de boules de feu...");
		lore.add(ChatColor.RED+"Soyez prêt à plonger votre adversaire");
		lore.add(ChatColor.RED+"dans un enfer purificateur.");
		lore.add(ChatColor.DARK_RED+""+ChatColor.ITALIC+"Sortilège interdit");
		return lore;
	}

	@Override
	public String getName() {
		return "Dévastation infernale";
	}

	@Override
	public ChatColor getColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public
	int getManaCost() {
		return 10;
	}

	@Override
	public
	int getCooldown() {
		return 1800;
	}

}
