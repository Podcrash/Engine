package com.podcrash.api.disguise;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.packetwrapper.abstractpackets.*;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.util.PlayerCache;
import com.podcrash.api.util.Utility;
import com.podcrash.api.plugin.PodcrashSpigot;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles disguising
 */
public final class Disguiser {
    private final static HashMap<Integer, Disguise> disguises = new HashMap<>();
    private final static HashMap<Integer, Disguise> seenDisguises = new HashMap<>();
    private final ProtocolManager protocolManager;

    public Disguiser() {

        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void disguiserIntercepter(){
        /*
         * All the movement packets to account for
         */
        PacketType[] types = new PacketType[] {
                PacketType.Play.Server.ENTITY_STATUS,
                PacketType.Play.Client.USE_ENTITY,
                PacketType.Play.Server.ENTITY_HEAD_ROTATION,
                PacketType.Play.Server.REL_ENTITY_MOVE,
                PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
                PacketType.Play.Server.ENTITY_TELEPORT
        };
        protocolManager.addPacketListener(new PacketAdapter(PodcrashSpigot.getInstance(), ListenerPriority.HIGHEST, types) {
            @Override
            public void onPacketSending(PacketEvent event) {
                //what the code accounts for: https://gitlab.com/rain474/dominate-recreation/snippets/1886024
                HashMap<Integer, Disguise> disguisesClone = (HashMap<Integer, Disguise>) disguises.clone();
                int entityID = event.getPacket().getIntegers().read(0);
                Disguise disguise = disguisesClone.getOrDefault(entityID, null);
                if (disguise != null)
                    event.getPacket().getIntegers().write(0, disguise.getSeen().getEntityId());
            }

            /**
             * Pass use packets towards the original entity as well.
             */
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) {
                    return;
                }

                HashMap<Integer, Disguise> seenDisguisesClone = (HashMap<Integer, Disguise>) seenDisguises.clone();
                WrapperPlayClientUseEntity useEntity = new WrapperPlayClientUseEntity(event.getPacket());
                Disguise disguise = seenDisguisesClone.getOrDefault(useEntity.getTargetID(), null);
                if (disguise != null) {
                    useEntity.setTargetID(disguise.getEntity().getEntityId());
                }
            }

        });
        disguiseDestroyer();
    }

    /**
     * Calls a sync timer that destroys invalid disguise entities.
     * See {@link Disguise}
     */
    private void disguiseDestroyer() {
        TimeHandler.repeatedTime(1, 0, new TimeResource() {
            @Override
            public void task() {
                if (disguises.size() == 0)
                    return;
                Iterator<Integer> entityIds = disguises.keySet().iterator();
                List<Integer> destroyIDs = new ArrayList<>();
                Disguise disguise = null;
                while(entityIds.hasNext()){
                    disguise = disguises.get(entityIds.next());
                    if (!disguise.getEntity().isValid()) {
                        entityIds.remove();
                        destroyIDs.add(disguise.getSeen().getEntityId());
                    }
                }
                if (destroyIDs.size() <= 0)
                    return;
                WrapperPlayServerEntityDestroy entityDestroy = destroyPacket(destroyIDs.stream().mapToInt(i -> i).toArray());
                //this nullpointer error won't actually be called ever.
                PacketUtil.asyncSend(entityDestroy, disguise.getEntity().getWorld().getPlayers());
            }

            @Override
            public boolean cancel() {
                return false;
            }

            @Override
            public void cleanup() {

            }
        });
    }
    public static void disguise(Entity entity, Player object, boolean copyPlayer, List<Player> players) {
        CraftWorld world = ((CraftWorld) entity.getWorld());
        WorldServer worldServer = world.getHandle();
        EntityPlayer entityPlayer = new EntityPlayer(MinecraftServer.getServer(), worldServer, new GameProfile(object.getUniqueId(), object.getName()), new PlayerInteractManager(worldServer));
        Location entityLoc = entity.getLocation();

        WrapperPlayServerEntityDestroy destroy = destroyPacket(entity.getEntityId());

        WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo();
        info.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.setData(
                Collections.singletonList(new PlayerInfoData(WrappedGameProfile.fromPlayer(object),
                Utility.ping(object),
                EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()),
                PlayerCache.getPlayerCache(object).getComponent()
        )));

        WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
        spawn.setPitch(entityLoc.getPitch());
        spawn.setYaw(entityLoc.getYaw());
        spawn.setPosition(entityLoc.toVector());
        spawn.setEntityID(entityPlayer.getId());
        spawn.setPlayerUUID(entityPlayer.getUniqueID());
        List<AbstractPacket> armorPackets = null;
        if (copyPlayer) {
            armorPackets = copyArmorPackets(entityPlayer.getId(), object);
            spawn.setCurrentItem(object.getItemInHand().getType().getId());
        }

        Disguise disguise = new Disguise(entity, EntityType.PLAYER, entityPlayer.getBukkitEntity());
        disguises.put(entity.getEntityId(), disguise);
        seenDisguises.put(entityPlayer.getId(), disguise);

        List<AbstractPacket> allPackets = new ArrayList<>(Arrays.asList(destroy, info, spawn));
        if (armorPackets != null)
            allPackets.addAll(armorPackets);
        for(Player player : players) {
            for(AbstractPacket packet : allPackets) {
                packet.sendPacket(player);
            }
        }
    }
    public static void disguise(Entity entity, EntityType entityType){
        WrapperPlayServerEntityDestroy entityDestroy = destroyPacket(entity.getEntityId());
        World world = entity.getWorld();
        EntityZombie zombie = new EntityZombie(((CraftWorld) world).getHandle());

        WrapperPlayServerSpawnEntityLiving spawnEntity = new WrapperPlayServerSpawnEntityLiving(zombie.getBukkitEntity());
        Location location = entity.getLocation();
        spawnEntity.setX(location.getX());
        spawnEntity.setY(location.getY());
        spawnEntity.setZ(location.getZ());

        Disguise disguise = new Disguise(entity, entityType, zombie.getBukkitEntity());
        disguises.put(entity.getEntityId(), disguise);
        seenDisguises.put(zombie.getId(), disguise);

        AbstractPacket[] packets = new AbstractPacket[] {entityDestroy, spawnEntity};
        List<Player> players = world.getPlayers();
        for(Player player : players) {
            for(AbstractPacket packet : packets) {
                packet.sendPacket(player);
            }
        }
    }

    /**
     * Get the armor packets required to send a disguise.
     * @param object the player whose armor will be copied
     * @return  The armor packets required
     */
    public static List<AbstractPacket> copyArmorPackets(int disguiseID, LivingEntity object) {
        List<AbstractPacket> armorPackets = new ArrayList<>();
        ItemStack[] armors = object.getEquipment().getArmorContents();
        WrapperPlayServerEntityEquipment base = new WrapperPlayServerEntityEquipment();
        base.setEntityID(disguiseID);
        PacketContainer entityArmor = base.getHandle();
        for(int i = 0; i < armors.length; i++){
            ItemStack armor = armors[i];
            if (armor.getType() == Material.AIR) continue;
            WrapperPlayServerEntityEquipment entityEquipment = new WrapperPlayServerEntityEquipment(entityArmor.shallowClone());
            entityEquipment.setSlot(i + 1);
            entityEquipment.setItem(armor);
            armorPackets.add(entityEquipment);
        }
        return armorPackets;
    }
    /**
     * make destroy packets for the ids
     * @param ids the entity ids
     * @return the destroy packet
     */
    public static WrapperPlayServerEntityDestroy destroyPacket(int... ids) {
        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(ids);
        return destroy;
    }

    public static HashMap<Integer, Disguise> getSeenDisguises() {
        return seenDisguises;
    }

    public static boolean isDisguise(Entity entity){
        return seenDisguises.get(entity.getEntityId()) != null;
    }
}
