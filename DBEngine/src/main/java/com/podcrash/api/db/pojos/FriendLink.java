package com.podcrash.api.db.pojos;

import java.util.UUID;

public final class FriendLink {

    private boolean pending;
    private UUID playerOne;
    private UUID playerTwo;

    public FriendLink() {}

    public void setPlayerOne(UUID uuid) { playerOne = uuid; }
    public UUID getPlayerOne() { return playerOne; }

    public void setPlayerTwo(UUID uuid) { playerTwo = uuid; }
    public UUID getPlayerTwo() { return playerTwo; }

    public void setPending(boolean bool) { pending = bool; }
    public boolean getPending() { return pending; }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder("FriendLink{");
        result.append("pending=").append(pending);
        result.append(", playerOne=").append(playerOne);
        result.append(", playerTwo=").append(playerTwo);

        return result.toString();
    }
}
