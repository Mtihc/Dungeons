package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.dungeons.core.Dungeon;
import com.mtihc.minecraft.dungeons.core.DungeonControl;
import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public abstract class EditRegion extends SimpleCommand {

	private static final String NAME_ARGUMENT = "<name>";

	public EditRegion(SimpleCommand parent, String label,
			List<String> aliases, String permission, String permissionMessage, String description) {
		super(parent, label, aliases, permission, permissionMessage,
				NAME_ARGUMENT, description);
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be executed by a player, in game.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(args.length > 1) {
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
		Dungeon d = control.loadDungeon(id);
		if(d == null) {
			sender.sendMessage(ChatColor.RED + "Dungeon \"" + id + "\" doesn't exists.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Selection sel = DungeonPlugin.getPlugin().getControl().getWorldEdit().getSelection((Player)sender);
		if(sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
			sender.sendMessage(ChatColor.RED + "Select a region first. Use WorldEdit's command //wand");
			sender.sendMessage(getUsage().replace(NAME_ARGUMENT, id));
			return false;
		}
		
		updateRegion((Player)sender, d, sel);
		control.saveDungeon(d);
		return true;
	}

	protected abstract void updateRegion(Player player, Dungeon dungeon, Selection selection);
	

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
