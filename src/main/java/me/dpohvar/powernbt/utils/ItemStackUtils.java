package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public final class ItemStackUtils {

    /**
     * static access to utils
     */
    public static final ItemStackUtils itemStackUtils = new ItemStackUtils();


    private final RefClass classCraftItemStack = getRefClass("{cb}.inventory.CraftItemStack, {CraftItemStack}");
    private final RefClass classItemStack = getRefClass("{nms}.ItemStack, {nm}.item.ItemStack, {ItemStack}");
    private final RefField itemHandle = classCraftItemStack.findField(classItemStack);
    private final RefField tag = classItemStack.findField("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");

    private RefMethod asNMSCopy;
    private RefMethod asCraftMirror;
    private RefMethod createStack;
    private RefMethod save;
    private RefMethod write;
    private RefConstructor conNmsItemStack;
    private RefConstructor conCraftItemStack;
    private RefClass classItemMeta;

    private ItemStackUtils(){
        try {
            asNMSCopy = classCraftItemStack.findMethod(new MethodCondition()
                    .withTypes(ItemStack.class)
                    .withReturnType(classItemStack)
            );
            asCraftMirror = classCraftItemStack.findMethod(new MethodCondition()
                    .withTypes(classItemStack)
                    .withReturnType(classCraftItemStack)
            );
        } catch (Exception e) {
            conNmsItemStack = classItemStack.getConstructor(int.class, int.class, int.class);
            conCraftItemStack = classCraftItemStack.getConstructor(classItemStack);
        }
        try {
            classItemMeta = getRefClass("org.bukkit.inventory.meta.ItemMeta");
        } catch (Exception e) {
            classItemMeta = null;
        }
        try {
            createStack = classItemStack.findMethod(
                    new MethodCondition().withStatic(true).withTypes(nbtUtils.getNBTCompoundRefClass()).withReturnType(classItemStack)
            );
        } catch (Exception e){
            createStack = null;
        }
        try {
            save = classItemStack.findMethod(
                    new MethodCondition()
                            .withStatic(false)
                            .withTypes(nbtUtils.getNBTCompoundRefClass())
                            .withReturnType(nbtUtils.getNBTCompoundRefClass())
            );
        } catch (Exception e){
            save = null;
        }
        try {
            write = classItemStack.findMethod(
                    new MethodCondition()
                            .withStatic(false)
                            .withTypes(nbtUtils.getNBTCompoundRefClass())
                            .withReturnType(void.class)
                            .withName("load")
            );
        } catch (Exception e){
            write = null;
        }

    }

    private Object getTag(Object nmsItemStack) {
        return tag.of(nmsItemStack).get();
    }

    @SuppressWarnings("unchecked")
    private void setTag(Object nmsItemStack, Object nbtTagCompound) {
        tag.of(nmsItemStack).set(nbtTagCompound);
    }

    @SuppressWarnings("deprecation")
    public Object createNmsItemStack( ItemStack itemStack){
        if (asNMSCopy != null) {
            return asNMSCopy.call(itemStack);
        } else {
            int type = itemStack.getType().getId();
            int amount = itemStack.getAmount();
            int data = itemStack.getData().getData();
            return conNmsItemStack.create(type, amount, data);
        }
    }

    public ItemStack createCraftItemStack(Object nmsItemStack){
        if (asCraftMirror != null) {
            return (ItemStack) asCraftMirror.call(nmsItemStack);
        } else {
            return (ItemStack) conCraftItemStack.create(nmsItemStack);
        }
    }

    public Object createNMSItemStackFromNBT(Object nbtTagCompound){
        ItemStack itemStack = createCraftItemStack(new ItemStack(Material.APPLE));
        Object nmsItemStack = getHandle(itemStack);
        this.writeNMSItemStack(nmsItemStack, nbtTagCompound);
        return nmsItemStack;
    }

    public ItemStack createCraftItemStackFromNBT(Object nbtTagCompound){
        return createCraftItemStack(createNMSItemStackFromNBT(nbtTagCompound));
    }

    private Object getHandle(ItemStack cbItemStack){
        return itemHandle.of(cbItemStack).get();
    }

    public ItemStack createCraftItemStack(ItemStack item){
        return createCraftItemStack(createNmsItemStack(item));
    }

    public void setTag(ItemStack itemStack, Object nbtTagCompound){
        if (classCraftItemStack.isInstance(itemStack)) setTagCB(itemStack, nbtTagCompound);
        else if (classItemMeta != null) setTagOrigin(itemStack, nbtTagCompound);
    }

    public Object getTag(ItemStack itemStack){
        // if (classCraftItemStack.isInstance(itemStack)) return getTagCB(itemStack);
        Object nmsItemStack = tryGetNMSHandleItemStack(itemStack);
        if (nmsItemStack != null) return getTag(nmsItemStack);
        else if (classItemMeta != null) return getTagOrigin(itemStack);
        else return null;
    }

    private Object tryGetNMSHandleItemStack(ItemStack itemStack){
        if ( !classCraftItemStack.isInstance(itemStack) ) return null;
        Object nmsItemStack = getHandle(itemStack);
        if (nmsItemStack != null) return nmsItemStack;
        return null;
    }



    public Object readItemStack(ItemStack itemStack, Object nbtTagCompound){
        Object nmsItemStack;
        if (classCraftItemStack.isInstance(itemStack)) {
            nmsItemStack = getHandle(itemStack);
        } else {
            nmsItemStack = createNmsItemStack(itemStack);
        }
        return save.of(nmsItemStack).call(nbtTagCompound);
    }

    private void writeNMSItemStack(Object nmsItemStack, Object nbtTagCompound){
        write.of(nmsItemStack).call(nbtTagCompound);
    }

    public void writeItemStack(ItemStack itemStack, Object nbtTagCompound){
        Object nmsItemStack;
        if (classCraftItemStack.isInstance(itemStack)) {
            nmsItemStack = getHandle(itemStack);
            writeNMSItemStack(nmsItemStack, nbtTagCompound);
        } else {
            nmsItemStack = createNmsItemStack(itemStack);
            write.of(nmsItemStack).call(nbtTagCompound);
            ItemStack craftItemStack = createCraftItemStack(nmsItemStack);
            itemStack.setType(craftItemStack.getType());
            itemStack.setAmount(craftItemStack.getAmount());
            itemStack.setData(craftItemStack.getData());
            itemStack.setDurability(craftItemStack.getDurability());
            itemStack.setItemMeta(craftItemStack.getItemMeta());

        }
    }

    private void setTagCB(ItemStack itemStack, Object nbtTagCompound){
        Object nmsItemStack = getHandle(itemStack);
        setTag(nmsItemStack, nbtTagCompound);
    }

    private Object getTagCB(ItemStack itemStack){
        Object nmsItemStack = getHandle(itemStack);
        return getTag(nmsItemStack);
    }

    private void setTagOrigin(ItemStack itemStack, Object nbtTagCompound){
        if (nbtTagCompound == null) {
            itemStack.setItemMeta(null);
            return;
        }
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            setTagCB(copyNMSItemStack, nbtTagCompound);
            ItemMeta meta = copyNMSItemStack.getItemMeta();
            itemStack.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getTagOrigin(ItemStack itemStack){
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            ItemMeta meta = itemStack.getItemMeta();
            copyNMSItemStack.setItemMeta(meta);
            return getTagCB(copyNMSItemStack);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "copy item meta", e);
        }
        return null;
    }

}











