package com.podcrash.api.db.pojos;

import com.podcrash.api.mc.events.game.GameDamageEvent;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class InvictaPlayer {
    private ObjectId objectId;
    private UUID uuid;
    private String lastUsername;
    private String rank;

    private List<String> extraPerms;

    private Currency currency;

    private Map<String, GameData> gameData;

    public InvictaPlayer() {
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public List<String> getExtraPerms() {
        return extraPerms;
    }

    public void setExtraPerms(List<String> extraPerms) {
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
}
