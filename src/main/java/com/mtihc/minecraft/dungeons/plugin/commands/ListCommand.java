package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;


public class ListCommand extends SimpleCommand {

	public ListCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to list all dungeons.",
				"[page]", "List all dungeons");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			page = 1;
		} catch(NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Expected a page number, instead of \"" + args[0] + "\".");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Set<String> ids = DungeonPlugin.getPlugin().getControl().getDungeonIds();
		String[] array;
		if(ids == null) {
			array = new String[0];
		}
		else {
			array = ids.toArray(new String[ids.size()]);
		}
		
		int total = array.length;
		int totalPerPage = 10;
		int startIndex = (page - 1) * totalPerPage;
		int endIndex = startIndex + totalPerPage;

		int totalPages = (int) Math.ceil((float) total / totalPerPage);
		if (page > totalPages || page < 1) {
			sender.sendMessage(ChatColor.RED + "Page " + page
					+ " does not exist.");
		}
		if (totalPages > 1) {
			sender.sendMessage(ChatColor.GREEN + "Dungeon list (page "
					+ page + "/" + totalPages + "):");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Dungeon list:");
		}

		for (int i = startIndex; i < endIndex && i < total; i++) {
			String value = array[i];
			sender.sendMessage(ChatColor.DARK_GRAY + " " + i + ". " + ChatColor.WHITE + value);
		}
		
		if(page < total) {
			sender.sendMessage(ChatColor.GREEN + "To see the next page: " + ChatColor.WHITE + getUsage().replace("[page]", String.valueOf(page + 1)));
		}

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
