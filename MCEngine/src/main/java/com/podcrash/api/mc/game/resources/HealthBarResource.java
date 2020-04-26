package com.podcrash.api.mc.game.resources;

import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.hologram.Hologram;
import com.podcrash.api.mc.util.MathUtil;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthBarResource extends GameResource {
    private final char heart = '\u2764';
    private final Map<String, Hologram> players;
    //health objective
    private final Objective objective;
    public HealthBarResource(int gameID) {
        super(gameID, 1, 0);
        Scoreboard scoreboard = getGame().getGameScoreboard().getBoard();
        objective = scoreboard.registerNewObjective("hbar", "dummy");
        objective.setDisplayName(ChatColor.RED.toString() + heart);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        this.players = new HashMap<>();
        Game game = getGame();
        List<GTeam> teams = game.getTeams();
        for(GTeam team : teams) {
            for(Player player : team.getBukkitPlayers()) {
                players.put(player.getName(), null);
                objective.getScore(player.getName()).setScore((int) player.getHealth());
                /*
                Hologram hologram = HologramMaker.createHologram(player.getLocation(), "");
                hologram.setDistCheck(true);
                players.put(player.getName(), hologram);

                 */
            }
        }


    }

    public void addPlayerToMap(Player player) {
        players.put(player.getName(), null);
        objective.getScore(player.getName()).setScore((int) player.getHealth());
    }

    //make sure this part is async
    @Override
    public void run(long ticks, long delay) {
        runAsync(ticks, delay);
    }

    @Override
    public void task() {
        for(String name : players.keySet()) {
            Player player = Bukkit.getPlayer(name);
            if(player == null)
                continue;

            CraftEntity craftEntity = (CraftEntity) player;
            EntityLiving livingCraft = (EntityLiving) craftEntity.getHandle();
            int absorptionHearts = (int) livingCraft.getAbsorptionHearts();

            objective.getScore(name).setScore((int) player.getHealth() + absorptionHearts);
        }
        //map();
    }

    private void map() {
        for(Map.Entry<String, Hologram> playerData : players.entrySet()) {
            Player player = Bukkit.getPlayer(playerData.getKey());
            if (player == null)
                continue;
            process(player, playerData.getValue());
        }

    }
    private void process(Player player, Hologram hologram) {
        double health = player.getHealth();
        health = MathUtil.round(health, 2);
        String text = Double.toString(health) + ChatColor.RED + heart;
        hologram.setLocation(player.getLocation());
        hologram.editLine(0, text);
        hologram.update();
    }

    @Override
    public void cleanup() {
        players.clear();
    }
}
