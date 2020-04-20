package com.podcrash.api.mc.world;

import com.podcrash.api.mc.time.resources.BlockBreakThenRestore;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Currently, it's not being used since it's much easier to update a single block instead of refreshing an entire chunk
 * Inspired by https://github.com/desht/dhutils/blob/master/Lib/src/main/java/me/desht/dhutils/block/CraftMassBlockUpdate.java#L156
 */
public class CraftBlockUpdater implements Runnable {
    private BukkitTask thisTask;
    private final int MAX_BLOCKS = 500;

    private int minX = Integer.MAX_VALUE;
    private int minZ = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxZ = Integer.MIN_VALUE;

    private static final HashMap<String, CraftBlockUpdater> updaters = new HashMap<>();
    private final List<BlockBreakThenRestore> tempPlace = new ArrayList<>();
    private final ArrayDeque<DeferredBlock> deferredBlocks = new ArrayDeque<>();
    private World world;
    private boolean isActive;

    private int blocksModified = 0;

    public CraftBlockUpdater(org.bukkit.World world) {
        this.world = world;
    }

    private void start() {
        if(!isActive) {
            isActive = true;
            thisTask = Bukkit.getScheduler().runTaskTimer(Pluginizer.getSpigotPlugin(), this, 1,0);
        }
    }
    public void stop() {
        thisTask.cancel();
        isActive = false;
        updaters.remove(this.world.getName());
        this.world = null;
        this.deferredBlocks.clear();
    }
    public static void stopAll() {
        for (CraftBlockUpdater updater : updaters.values()) {
            updater.stop();
        }
    }

    public void setBlock(int x, int y, int z, Material material) {
       this.setBlock(x, y, z, material, (byte) 0);
    }

    public void setBlock(int x, int y, int z, Material material, byte data) {
        minX = Math.min(minX, x);
        minZ = Math.min(minZ, z);
        maxX = Math.max(maxX, x);
        maxZ = Math.max(maxZ, z);

        BlockUtil.setBlockFast(world, x, y, z, material, data);
        blocksModified++;
        //deferredBlocks.add(new DeferredBlock(x, y, z));
    }

    public void addRestore(BlockBreakThenRestore restore) {
        tempPlace.add(restore);
    }

    public List<BlockBreakThenRestore> getRestores() {
        return tempPlace;
    }

    @Override
    public void run() {
        if(Bukkit.getWorld(world.getName()) == null) stop();
        if(!isActive) return;
        //long now = System.nanoTime();
        //int n = 0;

        /*
        while(deferredBlocks.peek() != null) {
            DeferredBlock deferredBlock = deferredBlocks.poll();

        }
        */
        Iterator<BlockBreakThenRestore> iterator = tempPlace.iterator();
        while(iterator.hasNext()){
            BlockBreakThenRestore restore = iterator.next();
            if(restore.check()) {
                restore.remove();
                iterator.remove();
            }
        }

        if(blocksModified > 0) {
            notifyClients();
            blocksModified = 0;
        }
    }

    public void notifyClients() {
        /*
        List<ChunkCoordIntPair> pairs = new Stack<>();
        List<PacketPlayOutMapChunk> bulk = new Stack<>();
        //use protocollib
        for (ChunkCoords cc : calculateChunks()) {
            pairs.add(new ChunkCoordIntPair(cc.x, cc.z));
            world.regenerateChunk(cc.x, cc.z);
            bulk.add(new PacketPlayOutMapChunk(((CraftChunk) world.getChunkAt(cc.x, cc.z)).getHandle(), false, 20));
        }

        for(Player player : world.getBukkitPlayers()) {
            EntityPlayer eplayer = ((CraftPlayer) player).getHandle();

            for(PacketPlayOutMapChunk b : bulk) {

                //eplayer.playerConnection.sendPacket(b);
            }
        }
        */
    }

    private Set<ChunkCoords> calculateChunks() {
        Set<ChunkCoords> res = new HashSet<ChunkCoords>();
        if (blocksModified == 0) {
            return res;
        }
        int x1 = minX >> 4; int x2 = maxX >> 4;
        int z1 = minZ >> 4; int z2 = maxZ >> 4;
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                res.add(new ChunkCoords(x, z));
            }
        }
        return res;
    }

    public static CraftBlockUpdater getMassBlockUpdater(org.bukkit.World world) {
        if(!updaters.containsKey(world.getName())) updaters.put(world.getName(), new CraftBlockUpdater(world));
        CraftBlockUpdater updater = updaters.get(world.getName());
        if(!updater.isActive) updater.start();
        return updater;
    }
    private class ChunkCoords {
        public final int x, z;
        public ChunkCoords(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChunkCoords that = (ChunkCoords) o;

            if (x != that.x) return false;
            return z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + z;
            return result;
        }
    }
    private final class DeferredBlock {
        public final int x, y, z;

        public DeferredBlock(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DeferredBlock)) return false;
            DeferredBlock that = (DeferredBlock) o;
            return x == that.x &&
                    y == that.y &&
                    z == that.z;
        }
    }
}
