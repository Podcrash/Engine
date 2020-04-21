package com.podcrash.api.mc.util;

import com.abstractpackets.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedAttribute;
import com.comphenix.protocol.wrappers.WrappedAttributeModifier;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Deprecated, might be useful later
 */
public final class RevealUtil {
    private static final Set<UUID> hiddenEntities = Collections.synchronizedSet(new HashSet<>());

    /**
     * This method is functionality equivalent to entity#hideentity
     * @param entity
     * @param recievers
     */
    public static void hide(Player entity, List<Player> recievers) {
        Validate.notNull(entity, "hidden entity cannot be null");
        if (hiddenEntities.contains(entity.getUniqueId())) return; //if the entity is already hidden
        recievers.remove(entity); //remove the entity, since he will always see himself
        hiddenEntities.add(entity.getUniqueId());

        WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
        destroy.setEntityIds(new int[] {entity.getEntityId()});
        PacketUtil.asyncSend(destroy, recievers);
        Bukkit.broadcastMessage("test: hide " + entity.getName());
    }

    /**
     * This method is functionality equivalent to entity#showentity
     * @param entity
     * @param recievers
     */
    public static void show(Player entity, List<Player> recievers) {

        Validate.notNull(entity, "hidden entity cannot be null");
        if (!hiddenEntities.contains(entity.getUniqueId())) return; //if the entity is already seen
        recievers.remove(entity); //remove the entity, since he will always see himself
        hiddenEntities.remove(entity.getUniqueId());

        //convert to protcollib
        EntityPlayer hidden =  ((CraftPlayer) entity).getHandle();
        AttributeMapServer attributemapserver = (AttributeMapServer) hidden.getAttributeMap();
        Collection<AttributeInstance> collection = attributemapserver.c();

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(hidden);
        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(hidden.getId(), hidden.getDataWatcher(), true);
        PacketPlayOutUpdateAttributes attributes = new PacketPlayOutUpdateAttributes(hidden.getId(), collection);
        List<PacketPlayOutEntityEquipment> equipments = getEquipmentPackets(hidden);
        boolean sendEquipments = equipments.size() > 0;
        for(Player viewer : recievers) {
            EntityPlayer entityPlayer = ((CraftPlayer) viewer).getHandle();
            PlayerConnection connection = entityPlayer.playerConnection;
            connection.sendPacket(spawn);
            connection.sendPacket(metadata);
            if (!collection.isEmpty()) connection.sendPacket(attributes);
            if (sendEquipments) equipments.forEach(connection::sendPacket);

        }

        /*
        //use interfaces instead.
        List<AbstractPacket> packets = new ArrayList<>();
        if (entity instanceof Player) {
            WrapperPlayServerNamedEntitySpawn spawn = new WrapperPlayServerNamedEntitySpawn();
            spawn.setEntityID(entity.getEntityId());
            spawn.setPlayerUUID(entity.getUniqueId());

            Location location = entity.getLocation();
            spawn.setX(location.getX());
            spawn.setY(location.getY());
            spawn.setZ(location.getZ());
            spawn.setYaw(location.getYaw());
            spawn.setPitch(location.getPitch());

            ItemStack itemHand = ((CraftPlayer) entity).getItemInHand();
            spawn.setCurrentItem(itemHand == null ? 0 : itemHand.getTypeId());

            WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity);
            spawn.setMetadata(watcher);
            packets.add(spawn);

            WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata();
            metadata.setEntityID(entity.getEntityId());
            metadata.setMetadata(watcher.getWatchableObjects());

            packets.add(metadata);
        }else {
            WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity();
            spawn.setEntityID(entity.getEntityId());
            //todo: look at how the packets are set up and effectively copy it, (similar to how it is done above)
            packets.add(spawn);
        }
        PacketUtil.syncSend(packets, recievers);


         */
        Bukkit.broadcastMessage("test: reveal " + entity.getName());
    }

    /**
     * This method is functionality equivalent to entity#seeentity(entity)
     * @param entity
     */
    public static boolean isHidden(Entity entity) {
        return hiddenEntities.contains(entity.getUniqueId());
    }

    private static List<PacketPlayOutEntityEquipment> getEquipmentPackets(EntityPlayer player) {
        List<PacketPlayOutEntityEquipment> equipments = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            ItemStack itemstack = player.getEquipment(i);

            if (itemstack != null) {
                equipments.add(new PacketPlayOutEntityEquipment(player.getId(), i, itemstack));
            }
        }
        return equipments;
    }
}
