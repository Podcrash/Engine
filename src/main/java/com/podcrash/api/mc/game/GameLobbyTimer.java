package com.podcrash.api.mc.game;

import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.sound.SoundWrapper;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.TimeResource;
import org.bukkit.ChatColor;

public class GameLobbyTimer {

    private final int maxTime;
    private int currentTime;
    private String status;
    private boolean isRunning;
    private String defaultStatus;

    public GameLobbyTimer() {
        this.maxTime = 10;
        this.currentTime = maxTime;
        this.defaultStatus = "Waiting for Players...";
        this.status = defaultStatus;
        this.isRunning = false;
    }

    /**
     * Start the countdown, which will start the game once the timer reaches zero.
     * By default, it will wait for 10 seconds.
     */
    public void start() {
        if(isRunning) return;
        isRunning = true;
        TimeHandler.repeatedTimeSeconds(1, 0, new TimeResource() {
            @Override
            public void task() {
                if (isRunning) {
                    status = String.format("Starting in %s%s seconds", timeColor(), currentTime);
                    currentTime--;
                }
            }

            @Override
            public boolean cancel() {
                if(currentTime < 0) {
                    status = "Starting...";
                    GameManager.startGame();
                    return true;
                }
                return !isRunning;
            }

            @Override
            public void cleanup() {

            }
        });
    }

    private ChatColor timeColor() {
        if(currentTime <= 5) {
            SoundPlayer.sendSound(GameManager.getGame().getBukkitPlayers(), "note.pling", 1, 63);
            return ChatColor.RED;
        }
        return ChatColor.WHITE;
    }

    /**
     * Stops the countdown.
     * @param pause If true, saves the current time (implying you will resume later). If false, sets the counter back to max and status to waiting.
     */
    public void stop(boolean pause) {
        isRunning = false;
        if(!pause) {
            currentTime = maxTime;
            status = defaultStatus;
        }
    }

    /**
     * Update the current status of the timer.
     * @param status The new status to display, as a presentable string.
     */
    public void updateStatus(String status) {
        this.status = status;
    }

    /**
     * @return How much time is left on the countdown, in seconds.
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * @return The time that it should take for a game to start, in seconds.
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * The status of the LobbyTimer could be waiting for players, starting, ect.
     * @return The current status of the game as a presentable string.
     */
    public String getStatus() {return status;}

    /**
     * @return A boolean representing whether a countdown is running or not.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @return The default status message to be displayed when the timer is not running.
     */
    public String getDefaultStatus() {return defaultStatus;}
}
