package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalDungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl.LocalPlayer;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class ListPlayersCommand extends SimpleCommand {

	public ListPlayersCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to see who's in a running dungeon.",
				"[dungeon]", "See who's in the specified dungeon. Or your dungeon.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		DungeonControl control = DungeonPlugin.getPlugin().getControl();
		
		LocalDungeon d;
		
		if(args == null || args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game. Or specify a dungeon name.");
				sender.sendMessage(getUsage());
				return false;
			}
			LocalPlayer player = control.getPlayer(sender.getName());
			if(player == null || player.getDungeon() == null) {
				sender.sendMessage(ChatColor.RED + "You're not in a dungeon.");
				sender.sendMessage(getUsage());
				return false;
			}
			d = player.getDungeon();
		}
		else if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments. Or a dungeon id.");
			sender.sendMessage(getUsage());
			return false;
		}
		else {
			String id = args[0];
			d = control.getLocalDungeon(id);
			if(d == null) {
				if(!control.hasDungeon(id)) {
					sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" doesn't exist.");
					sender.sendMessage(getUsage());
					return false;
				}
				else {
					sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" is not started.");
					sender.sendMessage(getUsage());
					return false;
				}
			}
		}
		
		
		
		String[] names = d.getPlayerNames();
		String nameString = "";
		for (String name : names) {
			nameString += ", " + name;
		}
		if(!nameString.isEmpty()) {
			nameString = nameString.substring(2);
		}
		
		sender.sendMessage(ChatColor.GREEN + "List of players in \"" + d.getId() + "\" (" + names.length + "):");
		sender.sendMessage(nameString);
		
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
