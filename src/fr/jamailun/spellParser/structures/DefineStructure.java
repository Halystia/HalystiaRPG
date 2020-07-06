package fr.jamailun.spellParser.structures;

import org.bukkit.entity.Entity;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.data.Selector;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class DefineStructure extends CommandStructure {

	private String mode = "closest", defined = "%def";
	private Selector selector = Selector.NONE;
	private double range = 20;
	
	public DefineStructure(TokenContext context) {
		super(context);
	}

	public final static String REGEX = "define %[A-Za-z0-9_]+ as (location at (.*)|closest [A-Za-z0-9]+ from %[A-Za-z0-9_]+ (within|in|around) [0-9.]+)";	//define %defined as [closest] from %target within dist

	public void read(String line) {
		
	}
	
	public void setSelectorString(String string) {
		selector = Selector.fromString(string);
		if(selector == Selector.NONE) {
			System.err.println("Invalid identifier for define function : '"+string+"'.");
			invalidate();
		}
	}
	
	public void setModeString(String string) {
		mode = string;
		if( ! mode.equals("closest")) {
			System.err.println("Invalid mode for define function : '"+string+"'.");
			invalidate();
		}
	}
	
	public void setRangeDouble(double value) {
		if( value <= 0 ) {
			System.err.println("Error : max distance for defined should be greater than 0.");
			invalidate();
			return;
		}
		this.range = value;
	}
	
	public void setDefinitionString(String string) {
		if( string == null ) {
			System.err.println("Error : new definition can't be null.");
			invalidate();
			return;
		}
		if( context.isDefined(string)) {
			System.err.println("Error : variable '"+string+"' is already defined !");
			invalidate();
			return;
		}
		defined = string;
	}
	
	@Override
	public void apply(ApplicativeContext applicativeContext) {
		String targetId = context.getDefinition(target);
		Entity around = applicativeContext.getEntity(targetId);
		if(mode.equals("closest")) {
			Entity entity = getClosestAround(around);
			if(entity == null)
				return;
			applicativeContext.define(defined, entity);
		}
	}
	
	private Entity getClosestAround(Entity around) {
		Entity best = null;
		double dd = Double.MAX_VALUE;
		for(Entity en : around.getWorld().getEntities()) {
			if(en.getUniqueId().equals(around.getUniqueId()))
				continue;
			if( ! selector.isAllowed(en))
				continue;
			double dist = en.getLocation().distance(around.getLocation());
			if(range != -1 && dist > range)
				continue;
			if(dist < dd) {
				dd = dist;
				best = en;
			}
		}
		return best;
	}
	
}