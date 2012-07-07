package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EditCommand extends SimpleCommand {

	public EditCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to edit dungeons.",
				"", "Edit a dungeon's settings.");
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
		if(lbl.equals("dungeon") || lbl.equals("dungeon-region")) {
			return new EditDungeonRegionCommand(this, "dungeon-region", Arrays.asList(new String[]{"dungeon"}), getPermission());
		}
		else if(lbl.equals("spawn") || lbl.equals("spawn-region")) {
			return new EditSpawnRegionCommand(this, "spawn-region", Arrays.asList(new String[]{"spawn"}), getPermission());
		}
		else if(lbl.equals("exit") || lbl.equals("exit-region")) {
			return new EditExitRegionCommand(this, "exit-region", Arrays.asList(new String[]{"exit"}), getPermission());
		}
		else if(lbl.equals("despawn") || lbl.equals("despawn-region")) {
			return new EditDespawnRegionCommand(this, "despawn-region", Arrays.asList(new String[]{"despawn"}), getPermission());
		}
		else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[]{"dungeon", "spawn", "exit", "despawn"};
	}

}
