package com.podcrash.api.world;

import com.podcrash.api.game.GameManager;
import com.podcrash.api.time.resources.BlockBreakThenRestore;
import net.jafama.FastMath;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.Gate;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.*;

public final class BlockUtil {
    private static final double RADIAN = Math.PI/180D;
    private static final Material[] passables = new Material[]{
            Material.AIR,
            Material.SAPLING,
            Material.WATER,
            Material.STATIONARY_WATER,
            Material.LAVA,
            Material.STATIONARY_LAVA,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.WEB,
            Material.LONG_GRASS,
            Material.DEAD_BUSH,
            Material.YELLOW_FLOWER,
            Material.RED_ROSE,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TORCH,
            Material.FIRE,
            Material.REDSTONE_WIRE,
            Material.CROPS,
            Material.SIGN_POST,
            Material.RAILS,
            Material.WALL_SIGN,
            Material.LEVER,
            Material.STONE_PLATE,
            Material.WOOD_PLATE,
            Material.REDSTONE_TORCH_OFF,
            Material.REDSTONE_TORCH_ON,
            Material.STONE_BUTTON,
            Material.SNOW,
            Material.LADDER,
            Material.SUGAR_CANE_BLOCK,
            Material.PORTAL,
            Material.PUMPKIN_STEM,
            Material.MELON_STEM,
            Material.VINE,
            Material.NETHER_WARTS,
            Material.TRIPWIRE_HOOK,
            Material.TRIPWIRE,
            Material.CARROT,
            Material.POTATO,
            Material.WOOD_BUTTON,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON,
            Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON,
            Material.CARPET,
            Material.ACTIVATOR_RAIL,
            Material.DOUBLE_PLANT,
            Material.STANDING_BANNER,
            Material.WALL_BANNER
    };

    private static final Material[] fenceGates = new Material[]{
            Material.FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.ACACIA_FENCE_GATE
    };


    public static boolean hasPlayersInArea(Location location, double radius, List<Player> players, Player user) {
        double radiusSquared = radius * radius;
        for(Player player : players) {
            if (player == user || !GameManager.getGame().isParticipating(player) || GameManager.getGame().isRespawning(player) || GameManager.getGame().isSpectating(player))
                continue;
            Location loc = player.getLocation().add(0, 1, 0);
            double distanceSquared = loc.distanceSquared(location);
            if (distanceSquared <= radiusSquared)
                return true;
        }
        return false;
    }

    public static boolean hasPlayersInArea(Location location, double radius, List<Player> players) {
        double radiusSquared = radius * radius;
        for(Player player : players) {
            Location loc = player.getLocation();
            double distanceSquared = loc.distanceSquared(location);
            if (distanceSquared <= radiusSquared)
                return true;
        }
        return false;
    }

    public static boolean isInWater(LivingEntity entity) {
        Material m = entity.getLocation().getBlock().getType();
        return (m.equals(Material.STATIONARY_WATER) || m.equals(Material.WATER));
    }

    public static boolean isSign(Block block) {
        Material signType = block.getType();
        return signType == Material.WALL_SIGN || signType == Material.SIGN_POST;
    }
    public static boolean isPassable(Block block) {
        if (Arrays.stream(passables).anyMatch(material -> material.equals(block.getType()))) {
            return true;
        } else if (Arrays.stream(fenceGates).anyMatch(material -> material.equals(block.getType()))) {
            BlockState state = block.getState();
            MaterialData data = state.getData();
            if (data instanceof Gate) {
                Gate gate = (Gate) data;
                return gate.isOpen();
            }
        }
        return false;
    }
    public static boolean isSafe(Location location) {
        boolean a = isPassable(location.getBlock());

        if (a)
            return isPassable(location.clone().add(new Vector(0, 1, 0)).getBlock());
        else return false;
    }

    public static Player playerIsHere(Location loc, List<Player> players) {
        if (players == null)
            players = loc.getWorld().getPlayers();
        for(Player p : players) {
            double playerX = p.getLocation().getBlockX();
            double playerY = p.getLocation().getBlockY();
            double playerZ = p.getLocation().getBlockZ();
            double headX =  playerX + 1;
            double headY = playerY + 1;
            double headZ = playerZ + 1;

            double locX = loc.getBlockX();
            double locY = loc.getBlockY();
            double locZ = loc.getBlockZ();
            if ((locX == playerX && locY == playerY && locZ == playerZ) ||
                    (locX == headX && locY == headY && locZ == headZ))
                return p;
        }
        return null;
    }

    public static Location getHighestUnderneath(Location loc) {
        Block block = loc.getBlock();
        while (isPassable(block)) {
            if (loc.getY() < 0)
                return loc;
            loc.subtract(0, 1, 0);
            block = loc.getBlock();
        }

        return loc;
    }

    /**
     * Returns the air block above the y coordinate of a specified loc
     * @param loc
     * @return
     */
    public static Location getHighestAbove(Location loc) {
        Location location = getHighestUnderneath(loc);
        while(!isPassable(loc.getBlock())) {
            location.add(0, 1, 0);
        }
        return location;
    }

    public static Location getHighestUnderneath(Location loc, int range) {
        Block block = loc.getBlock();
        int counter = 0;
        while (isPassable(block)) {
            if (loc.getY() < 0)
                return loc;
            loc.subtract(0, 1, 0);
            counter++;
            block = loc.getBlock();
            if (counter >= range)
                return loc;
        }

        return loc;
    }

    public static double get2dDistanceSquared(Vector location1, Vector location2) {
        double x1 = location1.getX();
        double x2 = location2.getX();
        double z1 = location1.getZ();
        double z2 = location2.getZ();

        double xS = x2 - x1;
        double zS = z2 - z1;

        return xS * xS + zS * zS;
    }
    public static boolean is2DBlockEqual(Location location1, Location location2){
        return location1.getBlockX() == location2.getBlockX() && location1.getBlockZ() == location2.getBlockZ();
    }
    /**
     * Get all blocks within the range
     * @param location
     * @param radius
     * @return
     */
    public static List<Block> getBlocksWithinRange(Location location, int radius) {
        List<Block> locations = new ArrayList<>();
        double distanceSquared = FastMath.pow(radius, 2D);
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Location test = new Location(location.getWorld(), x, y, z);
                    if (test.distanceSquared(location) <= distanceSquared)
                        locations.add(test.getBlock());
                }
            }
        }
        return locations;
    }
    public static Set<Vector> getOuterBlocksWithinRange(Location center, int radius, boolean onlyAir) {
        Set<Vector> locations = new HashSet<>();
        double distanceSquared = radius * radius;
        double distanceMinusSquared = (radius - 1) * (radius - 1);
        for(int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
            for(int y = center.getBlockY() - radius; y <= center.getBlockY() + radius; y++) {
                for(int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                    Vector vect = new Vector(x,y, z);
                    double dist = vect.distanceSquared(center.toVector());
                    if (dist < distanceSquared && dist >= distanceMinusSquared) {
                        if ((onlyAir)) {
                            if (vect.toLocation(center.getWorld()).getBlock().getType() == Material.AIR)
                                locations.add(vect);
                        } else  {
                            locations.add(vect);
                        }
                    }
                }
            }
        }
        return locations;
    }
    public static List<Block> getNonPassableBlocksWithinRange(Location location, int radius) {
        List<Block> locations = new ArrayList<>();
        double distanceSquared = FastMath.pow(radius, 2D);
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Location test = new Location(location.getWorld(), x, y, z);
                    if (!isPassable(test.getBlock()) &&
                            isPassable(test.getBlock().getRelative(BlockFace.UP)) &&
                            test.distanceSquared(location) <= distanceSquared) {
                        locations.add(test.getBlock());
                    }
                }
            }
        }
        return locations;
    }

    public static List<Block> getNonAirWithinRange(Location location, int radius, boolean airTop) {
        List<Block> locations = new ArrayList<>();
        World world = location.getWorld();
        double distanceSquared = FastMath.pow(radius, 2D);
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        for(int x = blockX - radius; x <= blockX + radius; x++) {
            for(int y = blockY - radius; y <= blockY + radius; y++) {
                for(int z = blockZ - radius; z <= blockZ + radius; z++) {
                    Block test = world.getBlockAt(x, y, z);
                    if (locations.contains(test))
                        continue;
                    if (!test.getType().equals(Material.AIR) && test.getLocation().distanceSquared(location) <= distanceSquared) {
                        if (airTop) {
                            if (test.getRelative(BlockFace.UP).getType().equals(Material.AIR))
                                locations.add(test);
                        } else {
                            locations.add(test);
                        }

                    }
                }
            }
        }
        return locations;
    }
    public static List<Block> getSpecificWithinRange(Location location, int radius, Material... materials) {
        List<Block> locations = new ArrayList<>();
        World world = location.getWorld();
        double distanceSquared = FastMath.pow(radius, 2D);
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();

        for(int x = blockX - radius; x <= blockX + radius; x++) {
            for(int y = blockY - radius; y <= blockY + radius; y++) {
                for(int z = blockZ - radius; z <= blockZ + radius; z++) {
                    Block test = world.getBlockAt(x, y, z);
                    if (checkMaterials(test.getType(), materials) && test.getLocation().distanceSquared(location) <= distanceSquared) {
                        locations.add(test);
                    }
                }
            }
        }
        return locations;
    }

    private static boolean checkMaterials(Material material, Material... materials) {
        for(Material check : materials) {
            if (material.equals(check))
                return true;
        }
        return false;
    }

    public static List<Player> getAllPlayersHere(Location loc, List<Player> players) {
        List<Player> result = new ArrayList<>();
        if (players == null) players = loc.getWorld().getPlayers();
        for(Player p : players) {
            double playerX = p.getLocation().getBlockX();
            double playerY = p.getLocation().getBlockY();
            double playerZ = p.getLocation().getBlockZ();
            double headX =  playerX + 1;
            double headY = playerY + 1;
            double headZ = playerZ + 1;

            double locX = loc.getBlockX();
            double locY = loc.getBlockY();
            double locZ = loc.getBlockZ();
            if ((locX == playerX && locY == playerY && locZ == playerZ) ||
                    (locX == headX && locY == headY && locZ == headZ))
                result.add(p);
        }
        return result;
    }
    public static List<Player> getPlayersInArea(Location curLoc, int radius, List<Player> players) {
        List<Player> result = new ArrayList<>();
        if (players == null)
            players = curLoc.getWorld().getPlayers();
        double radiusSquared = radius * radius;
        //distance formula way
        for(Player p: players) {
            if (p.getLocation().add(0,1,0).distanceSquared(curLoc) <= radiusSquared)
                result.add(p);
        }

        /*potato farmer way
        for(Player p: players) {
            for(int x = -radius; x <= radius; x++) {
                for(int y = -radius; y <= radius; y++){
                    for(int z = -radius; z <= radius; z++) {
                        List<Player> p1 = getAllPlayersHere(curLoc.clone().add(x, y, z), null);
                        if (!p1.isEmpty()) {
                            result.addAll(p1);
                        }
                    }
                }
            }
        }

        */
        return result;
    }

    /**
     * Update a block and refresh it really fast
     * @param world
     * @param x
     * @param y
     * @param z
     * @param material
     * @param data
     */
    public static void setBlockFast(World world, int x, int y, int z, Material material, byte data) {
        int combined = material.getId() + (data << 12);
        setBlockFast(world, x, y, z, combined);
    }

    public static void setBlockFast(World world, int x, int y, int z, int combined) {
        world.getChunkAtAsync(x >> 4, z >> 4, bukkitChunk -> {
            net.minecraft.server.v1_8_R3.Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();

            final BlockPosition bp = new BlockPosition(x, y, z);
            final IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(combined);
            chunk.a(bp, ibd);
            chunk.world.notify(bp);
        });
    }

    public static void setBlock(Location location, Material material) {
        CraftBlockUpdater updater = CraftBlockUpdater.getMassBlockUpdater(location.getWorld());
        updater.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), material);
    }

    public static void setBlock(Location location, int blockID) {
        CraftBlockUpdater updater = CraftBlockUpdater.getMassBlockUpdater(location.getWorld());
        updater.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockID);
    }

    public static void setBlock(Location location, Material material, byte data) {
        CraftBlockUpdater updater = CraftBlockUpdater.getMassBlockUpdater(location.getWorld());
        updater.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), material, data);
    }
    public static void replaceBlock(Location location, Material material, int data, boolean physics){
        if (data < -128 || data > 127)
            throw new IllegalArgumentException("data must be between -128 and 127. was " + data);
        CraftWorld craftWorld = ((CraftWorld) location.getWorld());
        /*
        BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        IBlockData blockData = craftWorld.getHandle().getType(blockPosition);
        craftWorld.getHandle().setTypeAndData(blockPosition, blockData.getBlock().fromLegacyData())
        */

        CraftBlock craftBlock = (CraftBlock) location.getBlock();
        craftBlock.setTypeIdAndData(material.getId(), (byte) data, physics);
    }

    /**
     * Place a block, but then restore it to its original form after some time
     * @param location
     * @param material
     * @param data
     * @param durationInSeconds the amount of seconds after it will be restored
     */
    public static void restoreAfterBreak(Location location, Material material, byte data, int durationInSeconds) {
        Block originalBlock = location.getBlock();
        Material before = originalBlock.getType();
        byte originalData = originalBlock.getData();
        setBlock(location, material, data);
        BlockBreakThenRestore resource = new BlockBreakThenRestore(durationInSeconds, before, location, originalData);
        CraftBlockUpdater.getMassBlockUpdater(location.getWorld()).addRestore(resource);
    }
}
