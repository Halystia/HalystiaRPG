package fr.jamailun.spellParser.structures;

import org.bukkit.entity.Entity;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.Selector;
import fr.jamailun.spellParser.structures.abstraction.BlockStructure;

public class ForLoopStructure extends BlockStructure {
	private double range = 5;
	private boolean shouldApplyCaster = false;
	private final String targetSymbol;
	private final Selector selector;
	private String sourceEntity = "%caster";

	public static final String REGEX = "for [A-Za-z0-9_]+ as %[A-Za-z0-9_]+ around %[A-Za-z0-9_]+ in [0-9.]+ do \\{";
	
	public ForLoopStructure(TokenContext context, String targetCategory, String targetSymbol) {
		super(context);
		this.targetSymbol = targetSymbol;
		this.selector = Selector.fromString(targetCategory);
		if(selector == Selector.NONE) {
			System.out.println("Error : for loop selector '"+targetCategory+"' could not be resolved.");
			invalidate();
		}
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
			//Bukkit.broadcastMessage(">> TEST : "+en.getName());
			if( selector.isAllowed(en) ) {
			//	Bukkit.broadcastMessage(">> identifier : valid");
				if(en.getLocation().distance(caster.getLocation()) < range) {
			//		Bukkit.broadcastMessage(">> range : ok");
					if( ! en.getUniqueId().equals(caster.getUniqueId()) || shouldApplyCaster) {
			//			Bukkit.broadcastMessage(">> concerned : ok");
						ApplicativeContext child = applicativeContext.createChild();
						child.define(targetSymbol, en);
						super.children.forEach(structure -> {
			//				Bukkit.broadcastMessage(">>> calling structures");
							if(structure.isValid())
								structure.apply(child);
						});
					}
				}
			}
		});

	}

	public void setAroundValue(String sourceEntity) {
		this.sourceEntity = sourceEntity;
	}

}