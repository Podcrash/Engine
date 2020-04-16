package com.podcrash.api.db.pojos;

import org.bson.types.ObjectId;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class InvictaPlayer {
    private ObjectId objectId;

    private int discordID;
    private UUID uuid;
    private String lastUsername;

    private Set<String> ranks;
    private Set<String> extraPerms;

    private Set<UUID> friends;

    private Currency currency;

    private Map<String, GameData> gameData;

    public InvictaPlayer() {
    }

    public Set<UUID> getFriends() {
        return friends;
    }

    public void setFriends(Set<UUID> friends) {this.friends = friends;}

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public int getDiscordID() {
        return discordID;
    }

    public void setDiscordID(int discordID) {
        this.discordID = discordID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public Set<String> getRanks() {
        return ranks;
    }

    public void setRanks(Set<String> ranks) {
        this.ranks = ranks;
    }

    public Set<String> getExtraPerms() {
        return extraPerms;
    }

    public void setExtraPerms(Set<String> extraPerms) {
        this.extraPerms = extraPerms;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Map<String, GameData> getGameData() {
        return gameData;
    }

    public void setGameData(Map<String, GameData> gameData) {
        this.gameData = gameData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvictaPlayer{");
        sb.append("objectId=").append(objectId);
        sb.append(", uuid=").append(uuid);
        sb.append(", lastUsername='").append(lastUsername).append('\'');
        sb.append(", ranks=").append(ranks);
        sb.append(", extraPerms=").append(extraPerms);
        sb.append(", currency=").append(currency);
        sb.append(", gameData=").append(gameData);
        sb.append(", friends=").append(friends);
        sb.append('}');
        return sb.toString();
    }
}
