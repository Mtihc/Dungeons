package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.DungeonButton;
import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;

public class ButtonRemoveCommand extends SimpleCommand {

	public ButtonRemoveCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to add buttons.",
				"", "Remove the start button you're looking at, from a dungeon.");
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
		
		DungeonButton btn = control.getButton(targetLocation);
		if(btn == null) {
			sender.sendMessage(ChatColor.RED + "That button is not linked to any dungeon.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		control.removeButton(targetLocation);
		sender.sendMessage(ChatColor.GREEN + "Start button removed from \"" + btn.getDungeonId() + "\".");
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
