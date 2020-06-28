package fr.jamailun.spellParser.structures;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jamailun.halystia.HalystiaRPG;
import fr.jamailun.halystia.spells.Invocator;
import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.DataBlockStructure;

public class ThrowStructure extends DataBlockStructure implements Invocator {

	private Class<? extends Projectile> type;
	public static final String REGEX = "throw [a-zA-Z_]+ from %[a-zA-Z0-9_]+ with \\{";
	
	public ThrowStructure(TokenContext context, String type) {
		super(context);
		this.type = getType(type);
		if(this.type == null) {
			System.err.println("Error : unknown projectile type in throw block ('"+type+"').");
			invalidate();
		}
	}

	@Override
	public void apply(ApplicativeContext applicativeContext) {
		String shooterId = context.getDefinition(target);
		Entity shooterRaw = applicativeContext.getEntity(shooterId);
		if(shooterRaw == null)
			return;
		if( ! ( shooterRaw instanceof ProjectileSource))
			return;
		ProjectileSource shooter = (ProjectileSource) shooterRaw;
		final Projectile projectile = shooter.launchProjectile(type);
		
		HalystiaRPG.getInstance().getSpellManager().getInvocationsManager().add(projectile, (LivingEntity) shooterRaw, false, this, (int)Math.min(0, super.getDoubleData("damage")));
		if(super.isDataSet("gravity"))
			projectile.setGravity(getBooleanData("gravity"));
		if(super.isDataSet("bounce"))
			projectile.setBounce(getBooleanData("bounce"));
		if(super.isDataSet("fire-chances"))
			projectile.setFireTicks(Math.random() <= getDoubleData("fire-chances") ? 50 : 0);
		
		if(projectile instanceof AbstractArrow) {
			if(super.isDataSet("critical-chances"))
				((AbstractArrow) projectile).setCritical(Math.random() <= getDoubleData("critical-chances"));
			if(super.isDataSet("pierce-level"))
				((AbstractArrow) projectile).setPierceLevel((int)Math.min(0, getDoubleData("pierce-level")));
		}

		if(super.isDataSet("velocity"))
			projectile.setVelocity(projectile.getVelocity().multiply(Math.min(0.0001, getDoubleData("velocity"))));
		if(super.isDataSet("speed"))
			projectile.setVelocity(projectile.getVelocity().multiply(Math.min(0.0001, getDoubleData("speed"))));
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(projectile.isValid())
					projectile.remove();
			}
		}.runTaskLater(HalystiaRPG.getInstance(), 200L);
	}

	@Override
	public List<String> getAllKeys() {
		return Arrays.asList("bounce", "damage", "fire-chances", "critical-chances", "gravity", "pierce-level", "velocity", "speed");
	}
	
	private Class<? extends Projectile> getType(String type) {
		type = type.toLowerCase(Locale.ENGLISH);
		switch(type) {
		case "arrow":
			return Arrow.class;
		case "trident":
			return Trident.class;
		case "fireball":
		case "fire_ball":
			return Fireball.class;
		case "skull":
		case "wither":
		case "wither_skull":
			return WitherSkull.class;
		case "egg":
			return Egg.class;
		case "enderpearl":
		case "ender_pearl":
		case "pearl":
			return EnderPearl.class;
		case "snowball":
		case "snow_ball":
		case "snow":
			return Snowball.class;
		case "llama_spit":
		case "llama":
		case "spit":
			return LlamaSpit.class;
		case "shulkerbullet":
		case "shulker_bullet":
		case "shulker":
			return ShulkerBullet.class;
		}
		return null;
	}

	@Override
	public boolean canInvoke(UUID uuid, int homMany) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void oneIsDead(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

}