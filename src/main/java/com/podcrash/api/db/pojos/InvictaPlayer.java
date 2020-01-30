package com.podcrash.api.db.pojos;

import com.podcrash.api.mc.events.game.GameDamageEvent;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class InvictaPlayer {
    private ObjectId objectId;
    private UUID uuid;
    private String lastUsername;

    private Set<String> ranks;
    private Set<String> extraPerms;

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
        sb.append('}');
        return sb.toString();
    }
}
