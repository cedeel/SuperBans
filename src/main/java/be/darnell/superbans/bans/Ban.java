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
package be.darnell.superbans.bans;

import java.util.Date;
import java.util.UUID;

/**
 * A representation of a stored ban
 */
public class Ban {
    private int id;
    private String userDisplay;
    private UUID userID;
    private String issuer;
    private BanType type;
    private String message;
    private Date start;
    private long duration = 0l;
    // TODO: Add the rest to the ban

    public Ban(int id, String user, UUID uuid, String issuer, BanType type, String message, Date start, long duration) {
        this.id = id;
        this.userDisplay = user;
        this.userID = uuid;
        this.issuer = issuer;
        this.type = type;
        this.message = message;
        this.start = start;
        this.duration = duration;
    }

    public Ban(String user, UUID uuid, String issuer, BanType type, String message, Date start, long duration) {
        this(0, user, uuid, issuer, type, message, start, duration);
    }

    public Ban(String user, UUID uuid, String issuer, BanType type, String message, long duration) {
        this(user, uuid, issuer, type, message, new Date(System.currentTimeMillis()), duration);
    }

    public Ban(String user, UUID uuid, String issuer, BanType type, String message) {
        this(user, uuid, issuer, type, message, 0l);
    }

    /**
     * Return the user in the ban
     * @return The user in the ban
     */
    public String getUserName() {
        return userDisplay;
    }

    /**
     * Get the UUID of the banned user
     * @return The UUID of the banned user
     */
    public UUID getUserID() {
        return userID;
    }

    public String getIssuer() {
        return issuer;
    }

    /**
     * Get the type of the ban
     * @return The type of the ban
     */
    public BanType getType() {
        return type;
    }

    /**
     * Get the duration of the ban
     * @return The duration of the ban
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Get the start of the ban
     * @return The start date of the ban
     */
    public Date getStart() {
        return start;
    }

    /**
     * Get the ban message
     * @return The ban message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the ID of the ban
     * @return The ID of the ban
     */
    public int getId() {
        return id;
    }

    public boolean isExpired() {
        if (type == BanType.IP_TEMPORARY || type == BanType.TEMPORARY)
            return start.getTime() + duration < System.currentTimeMillis();
        return false;
    }
}
