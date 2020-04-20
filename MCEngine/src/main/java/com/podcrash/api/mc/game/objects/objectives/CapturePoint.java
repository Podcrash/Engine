package com.podcrash.api.mc.game.objects.objectives;

import com.abstractpackets.packetwrapper.WrapperPlayServerWorldEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.CapturePointPojo;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.WinObjective;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;


public final class CapturePoint extends WinObjective {
    private static final ObjectiveType otype = ObjectiveType.CAPTURE_POINT;
    private final Random random = new Random();
    private String color;
    private final String name;
    /**
     * 0 = white
     * 1 = red
     * -1 = blue
     */
    private Location[] bounds;
    private Location[] cornerWools;
    private Location[][] woolGlass;

    private final byte[][] blocks;
    private boolean isFull;
    private int progress;
    private final int[][] captureStyle;

    private final Game game;
    /**
     * Constructor for setting up a capture point
     * @param name Name of the capture point
     * @param vector This is the center of the point, or the exact beacon location.
     */
    public CapturePoint(String name, Vector vector){
        super(vector);
        this.name = name;
        this.color = "white";
        this.blocks = new byte[][] {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };
        this.progress = 0;
        this.isFull = true;
        this.captureStyle = new int[25][2];
        for(int i = 0; i < this.captureStyle.length; i++){
            this.captureStyle[i][0] = -100;
            this.captureStyle[i][1] = -100;
        }
        this.captureStyle[24][0] = 2;
        this.captureStyle[24][1] = 2;

        this.game = GameManager.getGame();
    }

    public CapturePoint(CapturePointPojo pojo) {
        this(pojo.getName(), PojoHelper.convertPoint2Vector(pojo.getPoint()));
    }


    @Override
    public void spawnFirework() {
        this.fireworkEffect = FireworkEffect.builder().withColor(TeamEnum.getByColor(color).getColor().getColor()).with(FireworkEffect.Type.BALL_LARGE).build();
        super.spawnFirework();
    }

    public ObjectiveType getObjectiveType(){
        return otype;
    }
    public String getName() {
        return name;
    }
    public String getColor() {
        return color;
    }
    public static ObjectiveType getOtype() {
        return otype;
    }

    /**
     * Find if the capture point if the following criteria are met:
     *      1. If there are wool blocks that center around the beacon
     *      2. If there is a glass on top of the above structure
     *      3. If there are 4 wool blocks that are abs(3,5,3) from the beacon.
     * @return
     */
    public boolean validate(World world){
        setWorld(world);
        if(!getLocation().getBlock().getType().equals(Material.BEACON)) return false;
        System.out.println("is beacon");
        Location center = getLocation().clone().add(0, 1,0);
        double x1 = center.getX() + 2;
        double z1 = center.getZ() + 2;
        double x2 = center.getX() - 2;
        double z2 = center.getZ() - 2;
        double y = center.getY();
        for(double x = x1; x >= x2; x--){
            for(double z = z1; z >= z2; z--){
                Location location = new Location(getWorld(), x, y, z);
                //glass/condition2
                boolean isGlass = (location.getBlock().getType().equals(Material.STAINED_GLASS) || location.getBlock().getType().equals(Material.GLASS));
                if(isGlass){
                    Location below = location.clone().subtract(0, 1, 0);
                    //wool/condition1
                    boolean isWoolOrBeacon = (below.equals(getLocation()) || below.getBlock().getType().equals(Material.WOOL));
                    if(isWoolOrBeacon) continue;
                }
                return false;
            }
        }

        System.out.println("is full glass");
        //condition 3:
        y += 4;
        for(double x = x1 + 1; x >= x2 - 1; x -= 6){
            for(double z = z1 + 1; z >= z2 - 1; z-= 6){
                Location location = new Location(getWorld(), x, y, z);
                if(!location.getBlock().getType().equals(Material.WOOL)) {
                    return false;
                }
            }
        }

        System.out.println("has 4 wool");
        return true;
    }

    public boolean isFull() {
        return isFull;
    }
    public boolean isCaptured(){
        return this.color.equalsIgnoreCase(TeamEnum.RED.getName()) || this.color.equalsIgnoreCase(TeamEnum.BLUE.getName());
    }
    public TeamEnum getTeamColor(){
        return TeamEnum.getByColor(color);
    }

    /**
     * Set the variable as well as change the 4 corner wools
     * @param color
     */
    public void setTeamColor(String color){
        TeamEnum team = TeamEnum.getByColor(color);
        if(team != null) {
            this.color = color;
            Location[] corners = getCornerWools();
            for(int i = 0; i < corners.length; i++){//TODO: FIREWORK?
                BlockUtil.replaceBlock(corners[i], Material.WOOL, team.getData(), false);
            }
        }
        else throw new IllegalArgumentException("color must be red, blue, or white~!");
    }

    /**
     * Before attempting to capture, all the wools should start at the same color
     * @param color
     * @return whether it is assured to be captured
     */
    private boolean assure(String color) {
        if(isCaptured()) return true;
        String opposite = (color.equalsIgnoreCase("red")) ? "blue" : "red";
        TeamEnum oppoTeam =  TeamEnum.getByColor(opposite);
        for(int x = 0; x < this.blocks.length; x++){
            for(int z = 0; z < this.blocks[0].length; z++){
                //If the block is the opposing color AND the block is not the middle block (unless progress is 24 i.e last block)
                if(this.blocks[x][z] == oppoTeam.getByteData() && (progress != 1 == (x != 2 || z != 2))){
                    replaceBlock(TeamEnum.WHITE, getLocation().clone().add(x - this.blocks.length/2, 0, z - this.blocks[0].length/2));
                    progress--;
                    this.blocks[x][z] = TeamEnum.WHITE.getByteData();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Recursive part of the function to make sure that the wool starts at the same color.
     * @param color
     * @param times how many times it will be ran.
     * @return
     */
    public TeamEnum capture(String color, int times){
        boolean assured = false;
        TeamEnum capturing;
        while (times > 0) {
            times--;
            //Try to make capture point the correct color first (i.e blue wants to cap a point with red blocks still on)
            assured = assure(color);
            //If it changed a block, that means we have the scenario above ^^^
            if (!assured) {
                //Assure it once more because of restoring cap naturally (the previous one was b/c of player)
                assured = assure(color);
                continue;
            }
            capturing = capture(color);
            if (capturing != null) return capturing;
        }
        return null;
    }

    /**
     * Find a random part of the wool map.
     * Turn it into red or blue
     * If the entire thing is red or blue, return the team.
     * @param color
     * @return if it isn't null, the point is captured and its color set
     */
    public TeamEnum capture(String color){// yeah we need enums
        TeamEnum team = TeamEnum.getByColor(color);
        if(team == null) throw new IllegalArgumentException("color cannot be " + color + ". Allowed: red, blue, white");
        if(this.color.equalsIgnoreCase(color)) {
            //Two cases:
            // (a) Red on a full "red capture" <= do nothing
            if (!isFull) {
                // (b) Red on a not-full "red capture"
                restoreCapture(); // TWICE because of "natural" restoration + player induced restoration
                restoreCapture();
            }
            return null;
        }
        if(isCaptured()) team = TeamEnum.WHITE;
        byte colorByte = team.getByteData();
        int row, col;
        byte current;
        do {
            row = random.nextInt(blocks.length);
            col = random.nextInt(blocks[0].length);
            //Do while block is not a good block OR (progress is not 24 and block is the middle block)
        } while (this.blocks[row][col] == colorByte || ((progress == 24) == (row != 2 || col != 2)));

        isFull = false;
        if(!isCaptured()) {
            SoundPlayer.sendSound(getLocation(), "dig.stone", 1, 90);
        }
        progress++;
        int deltaX = row - this.blocks.length/2;
        int deltaZ = col - this.blocks[0].length/2;
        Location loc = getLocation().clone();
        loc.add(deltaX, 0, deltaZ);
        replaceBlock(team, loc);
        this.blocks[row][col] = team.getByteData();
        //update world
        World world = getLocation().getWorld();
        List<Player> players = game == null ? world.getPlayers() : game.getBukkitPlayers();
        sendWoolPackets(team, players);

        if(progress == 25){
            setTeamColor(team.getName());
            progress = 0;
            isFull = true;
            return team;
        }
        return null;
    }

    /*
    public void neutralize(int times){
        if(times <= 1) neutralize();
        else neutralize(times - 1);
    }
    */

    /**
     * Restore the capture point to its original state (1 time)
     */
    public void restoreCapture() {
        if(isFull) return;
        progress--;
        TeamEnum team = TeamEnum.getByColor(getColor());
        byte teamByte = team.getByteData();
        boolean check = false;
        boolean once = false;

        World world = getWorld();
        List<Player> players = game == null ? world.getPlayers() : game.getBukkitPlayers();

        sendWoolPackets(team, players);
        for(int x = 0; x < this.blocks.length; x++){
            if(once) break;
            for(int z = 0; z < this.blocks[0].length; z++){
                if(this.blocks[x][z] != teamByte){
                    this.blocks[x][z] = teamByte;
                    int deltaX = x - this.blocks.length/2;
                    int deltaZ = z - this.blocks[0].length/2;
                    replaceBlock(team, getLocation().clone().add(new Vector(deltaX, 0, deltaZ)));
                    check = true;
                    once = true;
                    break;
                }
            }
        }
        if(!check) {
            progress = 0;
            isFull = true;
        }
    }

    /**
     * Wool packets to make the sound and particles
     * @param team the color
     * @param players players in game
     */
    private void sendWoolPackets(TeamEnum team, List<Player> players) {
        for(Location woolLocation : getCornerWools()) {
            WrapperPlayServerWorldEvent effect = new WrapperPlayServerWorldEvent();
            effect.setEffectId(2001);
            effect.setData(team.getProtocolData());
            effect.setLocation(new BlockPosition(woolLocation.toVector()));
            PacketUtil.asyncSend(effect, players);
        }
    }

    /**
     * @see {@link BlockUtil#replaceBlock(Location, Material, int, boolean)}
     * @param team
     * @param wool
     */
    private void replaceBlock(TeamEnum team, Location wool){
        if(!wool.getBlock().getType().equals(Material.BEACON)) BlockUtil.replaceBlock(wool, Material.WOOL, team.getData(), false);
        BlockUtil.replaceBlock(wool.add(0, 1, 0), Material.STAINED_GLASS, team.getData(), true);
    }

    /**
     * Return the 2 bounds of the capture point, from the beacon
     * [0] has the bigger x, z
     * [1] has the bigger y
     * @return the 2 boundaries of the capture point that must be reached to cap
     */
    public Location[] getBounds(){
        if(bounds != null) return bounds;
        else{
            Location center = getLocation().clone().add(0, 2,0 );
            Location p1 = center.clone().add(2, 0, 2);
            Location p2 = center.clone().add(-2, 3.5, -2);
            bounds = new Location[]{p1, p2};
            return getBounds();
        }
    }
    public Location[] getCornerWools(){
        //pls no recursion error
        if(cornerWools != null && cornerWools.length == 4) return cornerWools;
        else{
            Location center = getLocation().clone().add(0, 1,0);
            double x1 = center.getX() + 3;
            double z1 = center.getZ() + 3;
            double x2 = center.getX() - 3;
            double z2 = center.getZ() - 3;
            double y = center.getY() + 4;
            Location[] corners = new Location[4]; //this is four because we know exactly how many wools there will be.
            int i = 0;
            for(double x = x1; x >= x2; x -= 6){
                for(double z = z1; z >= z2; z-= 6){
                    Location location = new Location(getWorld(), x, y, z);
                    if(location.getBlock().getType().equals(Material.WOOL)) {
                        corners[i] = location;
                        i++;
                    }
                }
            }
            cornerWools = corners;
            return getCornerWools();
        }
    }
    /**
     * Iterator for the capture point blocks
     * next[0] = Wool/Beacon Location
     * next[1] = Glass
     * Be aware that the beacon can be next[0]
     */
    public Location[][] getWoolAndGlass(){
        if(woolGlass != null && woolGlass.length == 25 && woolGlass[0].length == 2) return woolGlass;
        Location[][] wools = new Location[25][2];
        double x2 = getVector().getX() + 2d;
        double z2 = getVector().getZ() + 2d;

        double y = getVector().getY();
        int i = 0;
        for(double x = getVector().getX() - 2d; x <= x2; x++){
            for(double z = getVector().getZ() - 2d; z <= z2; z++){
                Location wool = new Location(getWorld(), x, y, z);
                Location glass = wool.clone().add(0, 1, 0);
                if((wool.getBlock().getType().equals(Material.WOOL) || wool.getBlock().getType().equals(Material.BEACON)) &&
                        (glass.getBlock().getType().equals(Material.STAINED_GLASS) || glass.getBlock().getType().equals(Material.GLASS))) {
                    wools[i][0] = wool;
                    wools[i][1] = glass;
                }
            }
        }
        woolGlass = wools;
        return getWoolAndGlass();
    }


}
