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

package be.darnell.superbans;

import be.darnell.superbans.bans.BanManager;
import be.darnell.superbans.commands.*;
import be.darnell.superbans.listeners.ChatListener;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuperBans extends JavaPlugin {

    private boolean debug = false;

    private BanManager          banManager;
    private CommandHandler      commandHandler;
    private FileConfiguration   configuration;
    private File                configFile;
    private String              defaultBanReason;

    @Override
    public void onEnable() {
        registerConfiguration();
        registerBans();
        registerEvents();
        registerCommands();

        defaultBanReason = this.getConfig().getString("Messages.DefaultReason", "No reason given");

        // Enable plugin metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.enable();
        } catch (IOException e) {
            getLogger().warning("An error occurred while posting results to the Metrics.");
            warn(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> allArgs = new ArrayList<String>();
        allArgs.addAll(Arrays.asList(args));
        allArgs.add(0, label);
        return commandHandler.locateAndRunCommand(sender, allArgs);
    }

    private void registerConfiguration() {
        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/config.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    private void registerBans() {
        banManager = new BanManager(this);
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        // TODO: Create listeners and register them here
        ChatListener cListener = new ChatListener(this);
        manager.registerEvents(cListener, this);
    }

    private void registerCommands() {
        PermissionsModule pm = new PermissionsModule();
        commandHandler = new CommandHandler(this, pm);
        // TODO: Register commands
        // Base commands
        commandHandler.registerCommand(new BaseCommand(this));
        // Misc. commands
        commandHandler.registerCommand(new VersionCommand(this));
        // Ban related commands
        commandHandler.registerCommand(new BanCommand(this));
        commandHandler.registerCommand(new UnbanCommand(this));
        commandHandler.registerCommand(new TempbanCommand(this));
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    public void debug(String message) {
        if (debug) {
            message = "[Debug] " + message;
            getLogger().info(message);
        }
    }

    public String getDefaultReason() {
        return defaultBanReason;
    }
}
