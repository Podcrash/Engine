package com.podcrash.api.mc.events;

import com.comphenix.net.sf.cglib.asm.$ClassReader;
import com.podcrash.api.mc.damage.Cause;
import com.podcrash.api.mc.damage.Damage;

import com.podcrash.api.mc.damage.DamageSource;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import net.jafama.FastMath;
import net.md_5.bungee.protocol.packet.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Only for players
 */
public class DeathApplyEvent extends Event implements Cancellable {
    //this is cancellable because maybe cancel death later??
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private LivingEntity attacker;

    private Damage lastAttackerDamage;
    private Damage damage;
    private Deque<Damage> history;
    private boolean cancel;


    /**
     * Called whenever a player dies by this system
     * @param lastDamage - the last damage dealt (for reference)
     * @param damages - the history of all the damages that were done.
     */
    public DeathApplyEvent(Damage lastDamage, Deque<Damage> damages) {
        this.player = (Player) lastDamage.getVictim();
        this.damage = lastDamage;
        this.history = damages;
        this.attacker = findAttacker();//findAttacker();
        //boolean combat12SecondsAgo = (damages != null && damages.size() != 0)
        //        && FastMath.abs(lastDamage.getTime() - damages.getLast().getTime()) > 1200;
    }

    public Player getPlayer() {
        return player;
    }

    public LivingEntity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage.getDamage();
    }

    public Cause getCause() {
        return damage.getCause();
    }

    public Arrow getArrow() {
        return damage.getArrow();
    }

    public List<DamageSource> getSources() {
        return damage.getSource();
    }

    public ItemStack getItemInHand() {
        return damage.getItem();
    }
    public boolean isApplyKnockback() {
        return damage.isApplyKnockback();
    }

    /**
     * Sample: "Death> 1 was killed by 2 using a, b, c." with colors and stuff.
     * @return the player dying message
     */
    public String getDeathMessage() {
        String withMsg = withMsgCause(damage);


        if(getSources().size() > 1) {
            StringBuilder builder = new StringBuilder(withMsg);
            for (int i = 1; i < getSources().size(); i++) {
                DamageSource source = getSources().get(i);
                builder.append(", ");
                builder.append(source.getPrefix());
                builder.append(source.getName());
            }
            withMsg = builder.toString();
        }
        Game game = GameManager.getGame();
        TeamEnum victimT = game.getTeamEnum(player);

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.LIGHT_PURPLE);
        builder.append("Death> ");
        builder.append(ChatColor.RESET);
        builder.append(victimT.getChatColor()).append(player.getDisplayName());
        builder.append(ChatColor.GRAY);
        builder.append(" was killed by ");
        builder.append(ChatColor.RESET);

        //TODO: This needs refactor and testing
        if(attacker == null) {
            builder.append(getCause().name());
        }else {
            String attackerName;
            if (attacker instanceof Player) {
                Player attackerCast = ((Player) attacker);
                TeamEnum attackerT = game.getTeamEnum(attackerCast);
                builder.append(attackerT.getChatColor());
                attackerName = attackerCast.getDisplayName();
            } else attackerName = attacker.getName();

            builder.append(attackerName);

            int i = findAssists();

            if (i != 0) builder.append(" + " + i);
            builder.append(ChatColor.GRAY);
            builder.append(" using ");
            builder.append(ChatColor.RESET);
            builder.append(withMsg);
            builder.append(ChatColor.GRAY);
            builder.append(".");
        }
        return builder.toString();
    }

    private void finishAttacker(StringBuilder builder) {

    }
    private String withMsgCause(Damage damage) {
        String withMsg;
        Cause cause = damage.getCause();
        switch(cause) {
            case PROJECTILE:
                withMsg = ChatColor.YELLOW + "Archery";
                break;
            case MELEE:
                if (damage.getItem() == null || damage.getItem().getItemMeta() == null) withMsg = "Fists";
                else withMsg = damage.getItem().getItemMeta().getDisplayName();
                break;
            case VOID:
                withMsg = ChatColor.DARK_PURPLE + "Void";
                break;
            case CUSTOM:
                DamageSource first = damage.getSource().get(0);
                withMsg =  first.getPrefix() + first.getName();
                break;
            case NULL:
                withMsg = ChatColor.DARK_PURPLE + "Magic?";
                break;
            default:
                withMsg = (lastAttackerDamage == null) ? ChatColor.DARK_PURPLE + cause.name() : withMsgCause(lastAttackerDamage);
                break;
        }
        return withMsg;
    }

    private LivingEntity findAttacker() {
        if(damage == null) return null;
        if(history == null || history.size() == 0) return damage.getAttacker();
        List<Damage> damageList = new ArrayList<>(history);
        //find the player who last damaged
        Damage lastEntityDamage = damage;
        for(int i = damageList.size() - 1; i >= 0; i--) {
            if(lastEntityDamage.getAttacker() != null) break;
            lastEntityDamage = damageList.get(i);
        }

        if(lastEntityDamage == null) return null;
        if(damage.getTime() - lastEntityDamage.getTime() > 12000) return null;
        lastAttackerDamage = lastEntityDamage;
        return lastEntityDamage.getAttacker();
    }

    public int findAssists() {
        int a = 0;
        HashMap<LivingEntity, ArrayList<Damage>> sources = getHistoryByPlayers();
        for (LivingEntity entity : sources.keySet()) {
            if (entity != null && entity.getUniqueId() != attacker.getUniqueId()) {
                a++;
            }
        }
        return a;
    }

    public boolean wasUnsafe() {
        //Gradually add other stuff, maybe if  was stuck in a block?
        return player.getLocation().getY() <= 0;
    }

    private HashMap<LivingEntity, ArrayList<Damage>> getHistoryByPlayers() {
        HashMap<LivingEntity, ArrayList<Damage>> playerMap= new HashMap<>();
        long time = System.currentTimeMillis();
        for (Damage dmg : history) {
            if (time - dmg.getTime() >= 8000L) continue;
            LivingEntity attacker = dmg.getAttacker();
            if (!playerMap.containsKey(attacker)) {
                playerMap.put(attacker, new ArrayList<>());
            }
            playerMap.get(attacker).add(dmg);
        }
        return playerMap;
    }

    public String getCausesMessage() {
        Game game = GameManager.getGame();
        StringBuilder builder = new StringBuilder();
        HashMap<LivingEntity, ArrayList<Damage>> sources = getHistoryByPlayers();

        builder.append(ChatColor.BLUE).append("Reasons>\n").append(ChatColor.WHITE); // Prompt

        boolean hasSomething = false;

        for (LivingEntity entity : sources.keySet()) {
            ArrayList<Damage> damages = sources.get(entity);
            if (entity == null) {
                continue;
            } else {
                String attackerName;
                if (entity instanceof Player) {
                    Player attackerCast = ((Player) entity);
                    TeamEnum attackerT = game.getTeamEnum(attackerCast);
                    attackerName = attackerT.getChatColor() + attackerCast.getDisplayName();
                } else attackerName = entity.getName();
                builder.append(formatDeathCause(attackerName, damages));
                hasSomething = true;
            }
        }
        if (!hasSomething) return null;
        return builder.toString();
    }

    /**
     *
     * @param attackerName
     * @param damages
     * @return The stringbuilder for a single reason of death ex. " -  [20] - Trishula (Longshot, Frost Arrows)"
     */
    private StringBuilder formatDeathCause(String attackerName, ArrayList<Damage> damages) {
        int totalDamage = 0;
        HashSet<String> causes = new HashSet<>();
        for (Damage dmg : damages) {
            totalDamage += (int) dmg.getDamage();
            String withMsg = withMsgCause(dmg);
            if (!causes.contains(withMsg)) {
                causes.add(withMsg);
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(" -  ").append("[").append(totalDamage).append("] - ")
                .append(attackerName).append(ChatColor.GRAY).append(" (")
                .append(String.join(", ", causes))
                .append(ChatColor.GRAY).append(")\n");

        return builder;
    }

    public Deque<Damage> getHistory() {
        return history;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
