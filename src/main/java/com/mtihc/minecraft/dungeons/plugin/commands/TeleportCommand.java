package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class TeleportCommand extends SimpleCommand {

	public TeleportCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to teleport to dungeons",
				"<name>", "Teleport to a dungeon's spawn area.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(args == null || args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Incorrect number of arguments.");
			sender.sendMessage(ChatColor.RED + "Expected a dungeon name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		String id;
		try {
			id = args[0];
		} catch(IndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.RED + "Expected a dungeon name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Dungeon d = DungeonPlugin.getPlugin().getControl().loadDungeon(id);
		if(d == null) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" does not exist.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Location loc = d.getNextRandomSpawn(new Random());
		((Player)sender).teleport(loc);
		sender.sendMessage(ChatColor.RED + "Teleported to dungeon \"" + d.getId() + "\".");
		
		return true;
	}

	@Override
	public boolean hasNested() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNestedCommandLabels() {
		// TODO Auto-generated method stub
		return null;
	}

}
