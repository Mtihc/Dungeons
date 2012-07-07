package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class EditDungeonRegionCommand extends EditRegion {

	public EditDungeonRegionCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to edit dungeons' regions.",
				"Update a dungeon's region.");
	}

	@Override
	protected void updateRegion(Player player, Dungeon dungeon,
			Selection selection) {
		if(selection.getWorld().getName().equals(dungeon.getWorld().getName())) {
			dungeon.setDungeonRegion(selection.getMinimumPoint().toVector(), selection.getMaximumPoint().toVector());
			player.sendMessage(ChatColor.GREEN + "Region updated!");
		}
		else {
			player.sendMessage(ChatColor.RED + "Please select a region in world \"" + dungeon.getWorld().getName() + "\".");
		}
	}

}
