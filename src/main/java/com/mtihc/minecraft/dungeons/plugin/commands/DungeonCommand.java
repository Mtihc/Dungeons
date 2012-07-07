package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DungeonCommand extends SimpleCommand {

	public class Permission {

		public static final String EDIT = "dungeon.edit";
		public static final String CREATE = EDIT;
		public static final String DELETE = EDIT;
		public static final String LIST = "dungeon.list";
		public static final String TELEPORT = "dungeon.tp";
		public static final String LIST_PLAYERS = "dungeon.list-players";
		public static final String LIST_RUNNING = "dungeon.list-running";
		
		private Permission() {
			
		}
	}
	
	public DungeonCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission for the main dungeon command.",
				"", "This is the main command. It shows all commands.");
		
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(args == null || args.length == 0) {
			sendHelp(sender, -1);
		}
		else {
			try {
				int page = Integer.parseInt(args[0]);
				sendHelp(sender, page);
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Unknown command: /" + getUniqueName() + " " + args[0]);
				sender.sendMessage("To get command help, type: /" + getUniqueName() + " ?");
			}
		}
		return true;
	}

	@Override
	public boolean hasNested() {
		return true;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();

		if(lbl.equals("btn") || lbl.equals("button")) {
			return new ButtonCommand(this, "button", Arrays.asList("btn"), Permission.EDIT);
		}
		else if(lbl.equals("create")) {
			return new CreateCommand(this, "create", null, Permission.CREATE);
		}
		else if(lbl.equals("delete")) {
			return new DeleteCommand(this, "delete", null, Permission.DELETE);
		}
		else if(lbl.equals("edit")) {
			return new EditCommand(this, "edit", null, Permission.EDIT);
		}
		else if(lbl.equals("list")) {
			return new ListCommand(this, "list", null, Permission.LIST);
		}
		else if(lbl.equals("players") || lbl.equals("playerlist") || lbl.equals("who")) {
			return new ListPlayersCommand(this, "players", Arrays.asList("playerlist", "who"), Permission.LIST_PLAYERS);
		}
		else if(lbl.equals("running") || lbl.equals("dungeons")) {
			return new ListRunningCommand(this, "running", Arrays.asList("dungeons"), Permission.LIST_RUNNING);
		}
		else if(lbl.equals("tp") || lbl.equals("teleport")) {
			return new TeleportCommand(this, "tp", Arrays.asList(new String[]{"teleport"}), Permission.TELEPORT);
		}
		else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[]{"button", "create", "delete", "edit", "list", "players", "running", "teleport"};
	}

}
