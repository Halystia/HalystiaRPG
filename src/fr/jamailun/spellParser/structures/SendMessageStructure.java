package fr.jamailun.spellParser.structures;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;

public class SendMessageStructure extends CommandStructure {

	public static final String REGEX = "send \".*\" to %[\\pL\\pN_]+";

	private String message;

	public SendMessageStructure(TokenContext context) {
		super(context);
	}

	public void setMessage(String message) {
		if(message == null) {
			System.err.println("Error : message cannot be null.");
			invalidate();
			return;
		}
		this.message = message;
		//System.out.println("message = ("+message+")");
	}

	@Override
	public void apply(ApplicativeContext context) {
		Entity entity = context.getEntity(target);
		if(entity == null)
			return;
		String message = ChatColor.translateAlternateColorCodes('&', this.message);
		if ( ! message.contains("%") ) {
			entity.sendMessage(message);
			return;
		}
		
		String[] words = message.split(" ");
		for(int i = 0; i < words.length; i++) {
			if ( ! words[i].startsWith("%") )
				continue;
			String key = this.context.getDefinition(words[i]);
			if(context.isDefinedHasEntity(key)) {
				Entity var = context.getEntity(key);
				if(var == null)
					continue;
				String name = var.getName();
				if(var.getCustomName() != null)
					name = var.getCustomName();
				words[i] = name;
				continue;
			}
		}
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < words.length; i++) {
			builder.append(words[i]);
			if(i < words.length - 1)
				builder.append(" ");
		}
		entity.sendMessage(builder.toString());
	}
}