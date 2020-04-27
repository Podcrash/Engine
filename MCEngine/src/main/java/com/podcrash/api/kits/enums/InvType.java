package com.podcrash.api.kits.enums;

import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;


public enum InvType {
    SWORD("Sword Ability", "Sword Skills", Material.IRON_SWORD), //sword
    AXE("Axe Ability", "Axe Skills", Material.IRON_AXE), //axe
    SHOVEL("Shovel Ability", "Shovel Skills", Material.IRON_SPADE), //shovel
    BOW("Bow Ability", "Bow Skills", Material.BOW), //bow
    PRIMARY_PASSIVE("Primary Passive", "Primary Passives", null), //primary
    SECONDARY_PASSIVE("Secondary Passive", "Secondary Passives", null), //secondary
    INNATE("Innate Passive", "Innate Passive", Material.DIAMOND), //Innate
    DROP("Active Ability", "Active Abilities", null);

    //The reason why this is written out so that it stays in order.
    private final static InvType[] details = new InvType[] {SWORD, SHOVEL, AXE, BOW, DROP, PRIMARY_PASSIVE, SECONDARY_PASSIVE, INNATE};

    private String name;
    private String displayName;
    public Material material;

    InvType(String name, String displayName, Material material) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
    }

    public String getName() {
        return name;
    }


    public ItemStack createItemStack() {
        switch (this){
            case SWORD:
            case AXE:
            case BOW:
            case SHOVEL:
            case INNATE:
                return ItemStackUtil.createItem(material, String.format("%s%s%s", ChatColor.GOLD, ChatColor.BOLD, displayName), null);
            case PRIMARY_PASSIVE:
                Dye red = new Dye();
                red.setColor(DyeColor.RED);
                ItemStack itemStack = red.toItemStack(1);
                ItemStackUtil.setItemName(itemStack, String.format("%s%s%s", ChatColor.GOLD, ChatColor.BOLD, displayName));
                return itemStack;
            case SECONDARY_PASSIVE:
                Dye blue = new Dye();
                blue.setColor(DyeColor.BLUE);
                ItemStack itemStack1 = blue.toItemStack(1);
                ItemStackUtil.setItemName(itemStack1, String.format("%s%s%s", ChatColor.GOLD, ChatColor.BOLD, displayName));
                return itemStack1;
            case DROP:
                Dye green = new Dye();
                green.setColor(DyeColor.GREEN);
                ItemStack itemStack2 = green.toItemStack(1);
                ItemStackUtil.setItemName(itemStack2, String.format("%s%s%s", ChatColor.GOLD, ChatColor.BOLD, displayName));
                return itemStack2;
            default:
                throw new IllegalArgumentException("Not allowed");
        }
    }

    public static InvType[] details() {
        return details;
    }
}
