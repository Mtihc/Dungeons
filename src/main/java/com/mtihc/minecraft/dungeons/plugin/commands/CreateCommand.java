package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CreateCommand extends SimpleCommand {

	private static final String OK_OR_CANCEL = ChatColor.GREEN + "> Type " + ChatColor.WHITE + "OK" + ChatColor.GREEN + " when you're ready, or type " + ChatColor.WHITE + "CANCEL" + ChatColor.GREEN + ".";
	
	private static final ValidatingPrompt promptRegionSpawn = new SelectRegionPrompt() {
		
		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "> " + "Select the \"spawn region\" (inside the dungeon)."); 
			return OK_OR_CANCEL;
		}
		
		@Override
		protected void setRegion(ConversationContext context, World world, Vector min, Vector max) {
			Dungeon d = (Dungeon) context.getSessionData("dungeon");
			d.setSpawnRegion(min, max);
		}
		
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String input) {
			
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "Spawn region set!");
			
			return promptRegionExit;
		}

		@Override
		protected World getWorld(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getWorld();
		}

		@Override
		protected Vector getMin(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getMinimumPoint();
		}

		@Override
		protected Vector getMax(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getMaximumPoint();
		}
	};
	
	private static final ValidatingPrompt promptRegionExit = new SelectRegionPrompt() {
		
		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "> " + "Select the \"exit region\" (at the end of the dungeon)."); 
			return OK_OR_CANCEL;
		}
		
		@Override
		protected void setRegion(ConversationContext context, World world, Vector min, Vector max) {
			Dungeon d = (Dungeon) context.getSessionData("dungeon");
			d.setExitRegion(min, max);
		}
		
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String input) {
			
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "Exit region set!");
			
			return promptRegionDespawn;
		}

		@Override
		protected World getWorld(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getWorld();
		}

		@Override
		protected Vector getMin(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getMinimumPoint();
		}

		@Override
		protected Vector getMax(ConversationContext context) {
			return ((Dungeon) context.getSessionData("dungeon")).getMaximumPoint();
		}
	};
	
	private static final ValidatingPrompt promptRegionDespawn = new SelectRegionPrompt() {
		
		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "> " + "Select the \"de-spawn region\" (outside the dungeon)."); 
			return OK_OR_CANCEL;
		}
		
		@Override
		protected void setRegion(ConversationContext context, World world, Vector min, Vector max) {
			Dungeon d = (Dungeon) context.getSessionData("dungeon");
			d.setDespawnRegion(world, min, max);
		}
		
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String input) {
			
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "De-spawn region set!");
			
			return promptButton;
		}
		
		@Override
		protected World getWorld(ConversationContext context) {
			return null;
		}

		@Override
		protected Vector getMin(ConversationContext context) {
			return null;
		}

		@Override
		protected Vector getMax(ConversationContext context) {
			return null;
		}
	};

	private static final ValidatingPrompt promptButton = new ValidatingPrompt() {
		
		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "> " + "Place a stone button (for joining the dungeon).");
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "> " + "Look at it.");
			return OK_OR_CANCEL;
		}
		
		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			Player player = (Player) context.getForWhom();
			Block target = player.getTargetBlock(null, 6);
			if(target.getType().equals(Material.STONE_BUTTON)) {
				DungeonControl control = DungeonPlugin.getPlugin().getControl();
				Dungeon d = (Dungeon) context.getSessionData("dungeon");
				Location loc = target.getLocation();
				control.setButton(loc, d.getId());
				return true;
			} else {
				player.sendRawMessage(ChatColor.RED + "You're not looking at a button.");
				return false;
			}
		}
		
		@Override
		protected Prompt acceptValidatedInput(ConversationContext context, String input) {

			Dungeon d = (Dungeon) context.getSessionData("dungeon");
			DungeonPlugin.getPlugin().getControl().saveDungeon(d);
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "Start button set!");
			context.getForWhom().sendRawMessage(ChatColor.GREEN + "Dungeon \"" + d.getId() + "\" saved.");
			return END_OF_CONVERSATION;
		}
	};
	
	
	public CreateCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to create dungeons.",
				"<name>", "Create a dungeon.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Incorrect number of arguments. Expected only a dungeon name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		String id;
		try {
			id = args[0];
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected dungeon name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(DungeonPlugin.getPlugin().getControl().hasDungeon(id)) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" already exists.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		
		Selection sel = DungeonPlugin.getPlugin().getControl().getWorldEdit().getSelection((Player)sender);
		if(sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
			sender.sendMessage(ChatColor.RED + "Select the dungeon's region first.");
			sender.sendMessage(ChatColor.RED + "Use WorldEdit's command:" + ChatColor.WHITE + " //wand");
			sender.sendMessage(ChatColor.RED + "Then, use command: " + ChatColor.WHITE + getUsage());
			return false;
		}
		
		
		Dungeon d = new Dungeon(id, ((Player)sender).getWorld(), sel.getMinimumPoint().toVector(), sel.getMaximumPoint().toVector());
		
		sender.sendMessage(ChatColor.GREEN + "Dungeon \"" + d.getId() + "\" created.");
		
		Map<Object, Object> data = new HashMap<Object, Object>();
		data.put("dungeon", d);
		
		
		
		
		ConversationFactory f = new ConversationFactory(DungeonPlugin.getPlugin())
		.withEscapeSequence("cancel")
		.withFirstPrompt(promptRegionSpawn)
		.withInitialSessionData(data)
		.withLocalEcho(true)
		.withModality(false);
		
		
		
		Conversation c = f.buildConversation((Player)sender);
		c.begin();
		
		
		
		
		return true;
	}
	
	

	@Override
	public boolean hasNested() {
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		return null;
	}

}
