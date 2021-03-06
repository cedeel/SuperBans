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
import be.darnell.superbans.util.Formatting;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

public class IPBanCommand extends  SuperBansCommand {

    public IPBanCommand(SuperBans plugin) {
        super(plugin);
        this.setName("SuperBans: IP Ban");
        this.setCommandUsage("/ipban <user> <reason>");
        this.setArgRange(1, 20);
        this.addKey("superbans ipban");
        this.addKey("sb ipban");
        this.addKey("ipban");
        this.setPermission("superbans.ipban", "Allows this user to ban other users by IP address.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args.get(0));
        if (target == null) {
            sender.sendMessage(colour2 + "The target " + colour1 + args.get(0) + colour2 + " doesn't exist.");
            return;
        }

        String reason = plugin.getDefaultReason();
        if (args.size() > 1)
            reason = Formatting.combineStrings(args, 1, args.size());

        plugin.getBanManager().ipBan(sender, target, reason);
    }
}
