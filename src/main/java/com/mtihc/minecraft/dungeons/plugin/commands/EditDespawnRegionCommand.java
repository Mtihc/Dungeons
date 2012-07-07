package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class EditDespawnRegionCommand extends EditRegion {

	public EditDespawnRegionCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to edit dungeons' regions.",
				"Update a dungeon's exit region.");
	}

	@Override
	protected void updateRegion(Player player, Dungeon dungeon,
			Selection selection) {
		dungeon.setDespawnRegion(selection.getWorld(), selection.getMinimumPoint().toVector(), selection.getMaximumPoint().toVector());
		player.sendMessage(ChatColor.GREEN + "Despawn region updated!");
		
	}

}
