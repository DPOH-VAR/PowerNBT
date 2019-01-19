package me.dpohvar.powernbt.nbt;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.utils.ItemStackUtils.itemStackUtils;

public class NBTContainerItem extends NBTContainer<ItemStack> {

    ItemStack item;

    public NBTContainerItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getObject() {
        return item;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<String> getTypes() {
        int id = item.getType().getId();
        List<String> s = new ArrayList<String>();
        s.add("item");
        if (id == 387 || id == 386) s.add("item_book");
        else if (id >= 298 && id <= 301) s.add("item_leather");
        else if (id == 397) s.add("item_skull");
        else if (id == 403) s.add("item_enchbook");
        else if (id == 373) s.add("item_potion");
        else if (id == 401) s.add("item_rocket");
        else if (id == 402) s.add("item_firework");
        if ((id >= 267 && id <= 279)
                || (id >= 283 && id <= 286)
                || (id >= 290 && id <= 294)
                || (id >= 298 && id <= 317)
                || id == 261
                || id == 346
                || id == 359
                || id == 256
                || id == 257
                ) s.add("item_repair");
        return s;
    }

    @Override
    public NBTTagCompound readTag() {
        Object tag = itemStackUtils.getTag(item);
        if (tag==null) return null;
        return (NBTTagCompound) new NBTTagCompound(false, tag).clone();
    }

    @Override
    public void writeTag(NBTBase base) {
        Object handle = null;
        if (base != null) handle = base.clone().handle;
        itemStackUtils.setTag(item, handle);
    }

    @Override
    public void eraseTag() {
        writeTag(null);
    }

    @Override
    protected Class<ItemStack> getContainerClass() {
        return ItemStack.class;
    }

    @Override
    public String toString(){
        return item.toString();
    }
}
