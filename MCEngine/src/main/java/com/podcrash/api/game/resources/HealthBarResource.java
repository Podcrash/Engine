package com.podcrash.api.game.resources;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.data.FakeTeam;
import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.hologram.Hologram;
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

public class HealthBarResource extends TimeGameResource {
    private final char heart = '\u2764';
    private final Map<String, Hologram> players;
    //health objective
    private final Objective objective;
    public HealthBarResource(int gameID) {
        super(gameID, 1, 0);
        Scoreboard scoreboard = game.getGameScoreboard().getBoard();
        objective = scoreboard.registerNewObjective("hbar", "dummy");
        objective.setDisplayName(ChatColor.RED.toString() + heart);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        this.players = new HashMap<>();
        List<GTeam> teams = game.getTeams();
        for(GTeam team : teams) {
            for(Player player : team.getBukkitPlayers()) {
                players.put(player.getName(), null);
                FakeTeam fakeTeam = NametagEdit.getApi().getFakeTeam(player);
                String name = fakeTeam.getName();
                objective.getScore(name).setScore((int) player.getHealth());
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
    }

    @Override
    public void cleanup() {
        players.clear();
    }
}
