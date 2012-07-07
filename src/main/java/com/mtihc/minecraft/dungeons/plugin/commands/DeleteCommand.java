package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class DeleteCommand extends SimpleCommand {

	public DeleteCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to delete dungeons.",
				"<name>", "Delete a dungeon.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

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
		
		DungeonControl control = DungeonPlugin.getPlugin().getControl();
		if(!control.hasDungeon(id)) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		control.removeDungeon(id);
		sender.sendMessage(ChatColor.YELLOW + "Dungeon \"" + id + "\" deleted.");
		
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
