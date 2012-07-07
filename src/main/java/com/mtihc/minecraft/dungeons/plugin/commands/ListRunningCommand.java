package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class ListRunningCommand extends SimpleCommand {

	public ListRunningCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to list running dungeons.",
				"", "List all running dungeons.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		

		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		DungeonControl control = DungeonPlugin.getPlugin().getControl();
		
		String[] ids = control.getLocalDungeonIds();
		
		String idString = "";
		for (String id : ids) {
			idString += ", " + id;
		}
		if(!idString.isEmpty()) {
			idString = idString.substring(2);
		}
		
		sender.sendMessage(ChatColor.GREEN + "List of running dungeons (" + ids.length + "): ");
		sender.sendMessage(idString);
		
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
