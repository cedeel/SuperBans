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

    private Map<UUID, List<Ban>> bans;
    private int nextId;
    private File file;

    private static final String ISSUER_FIELD = "issuer";
    private static final String KNOWN_NAME_FIELD = "aka";
    private static final String MESSAGE_FIELD = "message";
    private static final String TYPE_FIELD = "type";
    private static final String START_FIELD = "start";
    private static final String DURATION_FIELD = "duration";

    public FlatFileStore(File storage) {
        file = storage;
        fromDisk();
    }

    @Deprecated
    public boolean isBanned(String target) {
        return !getBans(target).isEmpty();
    }

    @Override
    public boolean isBanned(UUID uuid) {
        List<Ban> result = bans.get(uuid);
        if (result != null) {
            Ban ban = result.get(result.size() - 1);
            if (!ban.isExpired())
                return true;
        }
        return false;
    }

    @Override
    public List<Ban> getBans(String target) {
        //return bans.get(target);
        List<Ban> result = new ArrayList<>();
        String checkFor = target.toLowerCase();
        for (UUID id : bans.keySet()) {
            for (Ban ban : bans.get(id)) {
                if (ban.getUserName().equals(checkFor))
                    result.add(ban);
            }
        }
        return result;
    }

    @Override
    public List<Ban> getBans(UUID target) {
        return bans.get(target);
    }

    @Override
    public int ban(Ban ban) {
        Ban toStore = applyId(ban);
        List<Ban> oldBans = new ArrayList<>();
        try {
            oldBans.addAll(getBans(ban.getUserName()));
        } catch (NullPointerException ignored) {
        }
        oldBans.add(toStore);
        bans.put(toStore.getUserID(), oldBans);
        toDisk();
        return toStore.getId();
    }

    @Override
    public void unban(String target) {
        List<Ban> userBans = getBans(target);
        if (!userBans.isEmpty()) {
            bans.remove(userBans.get(0).getUserID());
            toDisk();
        }
    }

    @Override
    public void unban(UUID target) {
        if (bans.remove(target) != null)
            toDisk();
    }

    private Ban applyId(Ban ban) {
        return new Ban(
                nextId++,
                ban.getUserName(),
                ban.getUserID(),
                ban.getIssuer(),
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
            UUID uuid = UUID.fromString(key);
            ConfigurationSection section = banlist.getConfigurationSection(key);
            List<Ban> userBans = new ArrayList<>();
            for (String i : section.getKeys(false)) {
                int id = Integer.parseInt(i);
                if (id >= nextId)
                    nextId = id + 1;
                ConfigurationSection s = section.getConfigurationSection(i);
                String name = s.getString(KNOWN_NAME_FIELD);
                String issuer = s.getString(ISSUER_FIELD);
                String message = s.getString(MESSAGE_FIELD);
                BanType type = BanType.valueOf(s.getString(TYPE_FIELD));
                Date start = new Date(s.getLong(START_FIELD));
                Long duration = s.getLong(DURATION_FIELD);
                userBans.add(new Ban(id, name, uuid, issuer, type, message, start, duration));
            }
            bans.put(uuid, userBans);
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
            ConfigurationSection cs = banlist.createSection(ban.getUserID().toString()     + "." + ban.getId());

            cs.set(ISSUER_FIELD, ban.getIssuer());
            cs.set(KNOWN_NAME_FIELD, ban.getUserName());
            cs.set(MESSAGE_FIELD, ban.getMessage());
            cs.set(TYPE_FIELD, ban.getType().name());
            cs.set(START_FIELD, ban.getStart().getTime());
            cs.set(DURATION_FIELD, ban.getDuration());
        }
        try {
            banlist.save(file);
        } catch (IOException e) {
            System.out.println("Could not save bans to file");
            System.out.println(e.getMessage());
        }
    }

    public void store() {
        toDisk();
    }

}
