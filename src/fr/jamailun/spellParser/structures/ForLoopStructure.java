package fr.jamailun.spellParser.structures;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.BlockStructure;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class ForLoopStructure extends BlockStructure {
	private double range = 5;
	private boolean shouldApplyCaster = false;
	private final String targetSymbol, targetCategory;
	private String sourceEntity = "%caster";

	public ForLoopStructure(TokenContext context, String targetCategory, String targetSymbol) {
		super(context);
		this.targetSymbol =targetSymbol;
		this.targetCategory = targetCategory;
	}

	public void setRangeDouble(double range) {
		if(range <= 0) {
			System.err.println("Error : range must be greater than 0");
			invalidate();
			return;
		}
		this.range = range;
	}

	@Override
	public void apply(ApplicativeContext applicativeContext) {
		String casterVariable = context.getDefinition(sourceEntity); // = %caster
		Entity caster = applicativeContext.getEntity(casterVariable); // Player
		caster.getWorld().getEntities().forEach(en -> {
			if( corresponds(en) ) {
				if(en.getLocation().distance(caster.getLocation()) < range) {
					if( ! en.getUniqueId().equals(caster.getUniqueId()) || shouldApplyCaster) {
						ApplicativeContext child = applicativeContext.createChild();
						child.define(targetSymbol, en);
						super.children.forEach(structure -> structure.apply(child));
					}
				}
			}
		});

	}

	private boolean corresponds(Entity entity) {
		if(targetCategory.equalsIgnoreCase("all"))
			return true;
		if(targetCategory.equalsIgnoreCase("mob") || targetCategory.equalsIgnoreCase("mobs"))
			return entity instanceof Monster;
		if(targetCategory.equalsIgnoreCase("entity") || targetCategory.equalsIgnoreCase("entities"))
			return entity instanceof LivingEntity;
		if(targetCategory.equalsIgnoreCase("player") || targetCategory.equalsIgnoreCase("players"))
			return entity instanceof Player;
		System.err.println("Undefined target value in for loop : '"+targetCategory+"'.");
		return false;
	}

	public void setAroundValue(String sourceEntity) {
		this.sourceEntity = sourceEntity;
	}
}