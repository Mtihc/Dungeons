package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class ButtonAddCommand extends SimpleCommand {

	public ButtonAddCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to add buttons.",
				"to <dungeon>", "Add the start button you're looking at, to a dungeon.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		
		Block target = ((Player)sender).getTargetBlock(null, 6);
		if(target == null || !target.getType().equals(Material.STONE_BUTTON)) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a button.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		DungeonControl control = DungeonPlugin.getPlugin().getControl();
		
		Location targetLocation = target.getLocation();
		
		if(control.hasButton(targetLocation)) {
			sender.sendMessage(ChatColor.RED + "That button is already linked to a dungeon.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		String id;
		
		try {
			id = args[0];
			if(id.equals("to")) {
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
		if(!control.hasDungeon(id)) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" does not exist.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		control.setButton(targetLocation, id);
		sender.sendMessage(ChatColor.GREEN + "Start button added to \"" + id + "\".");
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
