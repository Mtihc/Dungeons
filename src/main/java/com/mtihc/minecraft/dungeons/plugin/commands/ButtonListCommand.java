package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class ButtonListCommand extends SimpleCommand {

	public ButtonListCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to list a dungeon's start buttons.",
				"of <dungeon>", "List the buttons of a dungeon.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		String id;
		try {
			id = args[0];
			if(id.equals("of")) {
				id = args[1];
				if(args.length > 2) {
					throw new Exception();
				}
			}
			else if(args.length > 1) {
				throw new Exception();
			}
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected a dungeon name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		DungeonControl control = DungeonPlugin.getPlugin().getControl();
		
		Dungeon d = control.loadDungeon(id);
		if(d == null) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" doesn't exist.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Collection<Location> btns = d.getButtons();
		
		int total = d.getTotalButtons();
		if(total == 0) {
			sender.sendMessage(ChatColor.YELLOW + "Dungeon \"" + d.getId() + "\" doesn't have any buttons.");
			return true;
		}
		
		
		String btnString = "";
		
		int index = 0;
		
		for (Location location : btns) {
			ChatColor color;
			if(index % 2 == 0) {
				color = ChatColor.WHITE;
			}
			else {
				color = ChatColor.GRAY;
			}
			btnString += ", " + locationToString(location) + color;
		}
		
		btnString = ChatColor.WHITE + btnString.substring(2);
		sender.sendMessage(ChatColor.GREEN + "Start buttons of dungeon \"" + d.getId() + "\" (" + total + "):");
		sender.sendMessage(btnString);
		return true;
	}
	
	private String locationToString(Location loc) {
		return loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
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
