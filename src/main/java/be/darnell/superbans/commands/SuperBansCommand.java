/*
* Copyright (c) 2013 cedeel.
* All rights reserved.
*
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * The name of the author may not be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package be.darnell.superbans.commands;

import be.darnell.superbans.SuperBans;
import com.pneumaticraft.commandhandler.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 *
 * @author cedeel
 */
public abstract class SuperBansCommand extends Command {

    protected final SuperBans plugin;
    protected final ChatColor colour1 = ChatColor.GOLD;
    protected final ChatColor colour2 = ChatColor.GRAY;

    public SuperBansCommand(SuperBans instance) {
        super(instance);
        plugin = instance;
    }

    @Override
    public abstract void runCommand(CommandSender sender, List<String> args);

    public String colourise(ChatColor colour, String value) {
        return colour + value + ChatColor.RESET;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(colour2 + "=== " + colour1 + getCommandName() + colour2 + " ===");
        sender.sendMessage(colour2 + "Usage: " + colour1 + getCommandUsage());
        sender.sendMessage(colour2 + getCommandDesc());
        sender.sendMessage((colour2 + "Permission: " + colour1 + this.getPermissionString()));
        String keys = "";
        String prefix = "";

        if (sender instanceof Player)
            prefix = "/";
        for (String key : this.getKeyStrings())
            keys += prefix + key + ", ";

        keys = keys.substring(0, keys.length() -2);
        sender.sendMessage(colour2 + "Aliases: " + colour1 + keys);
        if (this.getCommandExamples().size() > 0) {
            sender.sendMessage(colour2 + "Examples: ");
            if (sender instanceof Player) {
                for (int i = 0; i < 4 && i < this.getCommandExamples().size(); i++) {
                    sender.sendMessage(this.getCommandExamples().get(i));
                }
            } else {
                for (String c : this.getCommandExamples()) {
                    sender.sendMessage(c);
                }
            }
        }
    }
}
