package com.mtihc.minecraft.dungeons.plugin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ButtonCommand extends SimpleCommand {

	public ButtonCommand(SimpleCommand parent, String label,
			List<String> aliases, String permission) {
		super(parent, label, aliases, permission, "You don't have permission to add/remove buttons.",
				"", "Add/remove buttons. Use nested commands.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		if(args == null || args.length == 0) {
			sendHelp(sender, -1);
		}
		else {
			try {
				int page = Integer.parseInt(args[0]);
				sendHelp(sender, page);
			} catch(NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Unknown command: /" + getUniqueName() + " " + args[0]);
				sender.sendMessage("To get command help, type: /" + getUniqueName() + " ?");
			}
		}
		return true;
	}

	@Override
	public boolean hasNested() {
		return true;
	}

	@Override
	public SimpleCommand getNested(String labelOrAlias) {
		String lbl = labelOrAlias.toLowerCase();
		if(lbl.equals("add")) {
			return new ButtonAddCommand(this, "add", null, getPermission());
		}
		else if(lbl.equals("remove")) {
			return new ButtonRemoveCommand(this, "remove", null, getPermission());
		}
		else if(lbl.equals("list")) {
			return new ButtonListCommand(this, "list", null, getPermission());
		}
		else {
			return null;
		}
	}

	@Override
	public String[] getNestedCommandLabels() {
		return new String[]{"add", "remove", "list"};
	}

}
