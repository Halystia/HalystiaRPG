package fr.jamailun.spellParser.structures;

import fr.jamailun.spellParser.contexts.ApplicativeContext;
import fr.jamailun.spellParser.contexts.TokenContext;
import fr.jamailun.spellParser.structures.abstraction.CommandStructure;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

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
		entity.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
}