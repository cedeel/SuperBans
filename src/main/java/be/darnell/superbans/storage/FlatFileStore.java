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
package be.darnell.superbans.storage;

import be.darnell.superbans.bans.Ban;
import be.darnell.superbans.bans.BanType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FlatFileStore implements SuperBanStore {

    private Map<String, List<Ban>> bans;
    private int nextId;
    private File file;

    public FlatFileStore(File storage) {
        file = storage;
        fromDisk();
    }

    @Override
    public boolean isBanned(String target) {
        List<Ban> result = bans.get(target);
        if (result != null) {
            Ban ban = result.get(result.size() -1);
            if (!ban.isExpired())
                return true;
        }
        return false;
    }

    @Override
    public List<Ban> getBans(String target) {
        return bans.get(target);
    }

    @Override
    public int ban(Ban ban) {
        Ban toStore = applyId(ban);
        List<Ban> oldBans = new ArrayList<>();
        try {
            oldBans.addAll(getBans(ban.getUser()));
        } catch (NullPointerException ignored) {
        }
        oldBans.add(toStore);
        bans.put(toStore.getUser(), oldBans);
        toDisk();
        return toStore.getId();
    }

    @Override
    public void unban(String target) {
        bans.remove(target);
        toDisk();
    }

    private Ban applyId(Ban ban) {
        return new Ban(
                nextId++,
                ban.getUser(),
                ban.getType(),
                ban.getMessage(),
                ban.getStart(),
                ban.getDuration()
        );
    }

    private void fromDisk() {
        bans = new HashMap<>();
        YamlConfiguration banlist = YamlConfiguration.loadConfiguration(file);
        for (String key : banlist.getKeys(false)) {
            ConfigurationSection section = banlist.getConfigurationSection(key);
            List<Ban> userBans = new ArrayList<>();
            for (String i : section.getKeys(false)) {
                int id = Integer.parseInt(i);
                if (id >= nextId)
                    nextId = id + 1;
                ConfigurationSection s = section.getConfigurationSection(i);
                String message = s.getString("message");
                BanType type = BanType.valueOf(s.getString("type"));
                Date start = new Date(s.getLong("start"));
                Long duration = s.getLong("duration");
                userBans.add(new Ban(id, key, type, message, start, duration));
            }
            bans.put(key, userBans);
        }
    }

    private void toDisk() {
        YamlConfiguration banlist = new YamlConfiguration();
        // Make one list of bans
        List<Ban> bansList = new ArrayList<>();
        for (List<Ban> l : bans.values()) {
            bansList.addAll(l);
        }

        for (Ban ban : bansList) {
            String prefix = ban.getUser() + "." + ban.getId();
            banlist.set(prefix + ".message", ban.getMessage());
            banlist.set(prefix + ".type", ban.getType().name());
            banlist.set(prefix + ".start", ban.getStart().getTime());
            banlist.set(prefix + ".duration", ban.getDuration());
        }
        try {
            banlist.save(file);
        } catch (IOException e) {
            System.out.println("Could not save bans to file");
            System.out.println(e.getMessage());
        }
    }
}
