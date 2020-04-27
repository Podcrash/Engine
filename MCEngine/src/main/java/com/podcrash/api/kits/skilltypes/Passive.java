package com.podcrash.api.kits.skilltypes;

import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.enums.ItemType;
import org.bukkit.inventory.ItemStack;

//TODO: Make ActiveSkill (DropQ)
public abstract class Passive extends Skill {
    public Passive() {
        super();
    }
    /*
    Returns true if drops sword, bow, shovel, or axe
     */
    protected final boolean checkItem(ItemStack itemStack){
        for(ItemType itemtype : ItemType.values()){
            if(itemStack.getType().name().toLowerCase().contains(itemtype.getName().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    protected final ItemType getItemType(ItemStack itemStack){
        for(ItemType itemtype : ItemType.values()){
            if(itemStack.getType().name().toLowerCase().contains(itemtype.getName().toLowerCase())){
                return itemtype;
            }
        }
        return null;
    }

    /**
     * Most of the time, this is null.
     * @return
     */
    @Override
    public ItemType getItemType() {
        return ItemType.NULL;
    }
}
