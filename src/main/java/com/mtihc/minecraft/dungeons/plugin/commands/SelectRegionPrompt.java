package com.mtihc.minecraft.dungeons.plugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.dungeons.plugin.DungeonPlugin;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public abstract class SelectRegionPrompt extends ValidatingPrompt {

	public SelectRegionPrompt() {
		super();
	}
	
	@Override
	public String getPromptText(ConversationContext context) {
		return ChatColor.GREEN + "Type " + ChatColor.WHITE + "OK" + ChatColor.GREEN + " when you're ready.";
	}
	
	@Override
	public boolean blocksForInput(ConversationContext context) {
		// TODO Auto-generated method stub
		return super.blocksForInput(context);
	}

	protected abstract World getWorld(ConversationContext context);
	protected abstract Vector getMin(ConversationContext context);
	protected abstract Vector getMax(ConversationContext context);

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		
		if (input.equalsIgnoreCase("ok")) {
			Player p = (Player) context.getForWhom();

			WorldEditPlugin we = DungeonPlugin.getPlugin().getControl().getWorldEdit();
			Selection sel = we.getSelection(p);
			if (sel == null) {
				p.sendRawMessage(ChatColor.RED + "Select the region first.");
				return false;
			}
			World world = getWorld(context);
			World w = sel.getWorld();
			if (world != null && !world.getName().equals(w.getName())) {
				p.sendRawMessage(ChatColor.RED + "Select a region in world \"" + world.getName() + "\".");
				return false;
			}

			Vector minimum = getMin(context);
			Vector min = sel.getMinimumPoint().toVector();
			boolean outofbounds = false;
			if (minimum != null) {
				if (minimum.getBlockX() > min.getBlockX()
						|| minimum.getBlockY() > min.getBlockY()
						|| minimum.getBlockZ() > min.getBlockZ()) {
					outofbounds = true;
				}
			}
			Vector maximum = getMax(context);
			Vector max = sel.getMaximumPoint().toVector();
			if (maximum != null) {
				if (maximum.getBlockX() < max.getBlockX()
						|| maximum.getBlockY() < max.getBlockY()
						|| maximum.getBlockZ() < max.getBlockZ()) {
					outofbounds = true;
				}
			}

			if (outofbounds) {
				p.sendRawMessage(ChatColor.RED + "Select a region between ("
						+ minimum.getBlockX() + ", " + minimum.getBlockY()
						+ ", " + minimum.getBlockZ() + ") and ("
						+ maximum.getBlockX() + ", " + maximum.getBlockY()
						+ ", " + maximum.getBlockZ() + ")");
				return false;
			}

			setRegion(context, w, min, max);

			
			context.getForWhom().sendRawMessage(ChatColor.DARK_PURPLE + "Region set! " + ChatColor.WHITE + "(" + min.getBlockX() + ", " + min.getBlockY() + ", " + min.getBlockZ() + ") < (" + max.getBlockX() + ", " + max.getBlockY() + ", " + max.getBlockZ() + ")");
			context.getForWhom().sendRawMessage(ChatColor.DARK_PURPLE + "World: " + ChatColor.WHITE + w.getName());
			context.getForWhom().sendRawMessage(ChatColor.DARK_PURPLE + "Width: " + ChatColor.WHITE + String.valueOf(max.getBlockX() - min.getBlockX()) + " " + ChatColor.DARK_PURPLE + "Length: " + ChatColor.WHITE + String.valueOf(max.getBlockZ() - min.getBlockZ()) + " " + ChatColor.DARK_PURPLE + "Height: " + ChatColor.WHITE + String.valueOf(max.getBlockY() - min.getBlockY()));
			
			return true;
		}
		return false;
	}

	protected abstract void setRegion(ConversationContext context, World world, Vector min, Vector max);

}