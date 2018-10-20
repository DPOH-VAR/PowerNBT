package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.exception.NBTReadException;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

final class NBTUtils_Bukkit_named extends NBTUtils {

    private RefClass class_NBTBase = getRefClass("{nms}.NBTBase, {NBTBase}");
    private RefClass class_NBTTagByte = getRefClass("{nms}.NBTTagByte, {NBTTagByte}");
    private RefClass class_NBTTagShort = getRefClass("{nms}.NBTTagShort, {NBTTagShort}");
    private RefClass class_NBTTagInt = getRefClass("{nms}.NBTTagInt, {NBTTagInt}");
    private RefClass class_NBTTagLong = getRefClass("{nms}.NBTTagLong, {NBTTagLong}");
    private RefClass class_NBTTagFloat = getRefClass("{nms}.NBTTagFloat, {NBTTagFloat}");
    private RefClass class_NBTTagDouble = getRefClass("{nms}.NBTTagDouble, {NBTTagDouble}");
    private RefClass class_NBTTagString = getRefClass("{nms}.NBTTagString, {NBTTagString}");
    private RefClass class_NBTTagByteArray = getRefClass("{nms}.NBTTagByteArray, {NBTTagByteArray}");
    private RefClass class_NBTTagIntArray = getRefClass("{nms}.NBTTagIntArray, {NBTTagIntArray}");
    private RefClass class_NBTTagList = getRefClass("{nms}.NBTTagList, {NBTTagList}");
    private RefClass class_NBTTagCompound = getRefClass("{nms}.NBTTagCompound, {NBTTagCompound}");

    private RefConstructor con_NBTagByte = class_NBTTagByte.getConstructor(String.class, byte.class);
    private RefConstructor con_NBTagShort = class_NBTTagShort.getConstructor(String.class, short.class);
    private RefConstructor con_NBTagInt =  class_NBTTagInt.getConstructor(String.class, int.class);
    private RefConstructor con_NBTagLong = class_NBTTagLong.getConstructor(String.class, long.class);
    private RefConstructor con_NBTagFloat = class_NBTTagFloat.getConstructor(String.class, float.class);
    private RefConstructor con_NBTagDouble = class_NBTTagDouble.getConstructor(String.class, double.class);
    private RefConstructor con_NBTagString = class_NBTTagString.getConstructor(String.class, String.class);
    private RefConstructor con_NBTagByteArray = class_NBTTagByteArray.getConstructor(String.class, byte[].class);
    private RefConstructor con_NBTagIntArray = class_NBTTagIntArray.getConstructor(String.class, int[].class);
    private RefConstructor con_NBTagCompound = class_NBTTagCompound.getConstructor(String.class);
    private RefConstructor con_NBTagList = class_NBTTagList.getConstructor(String.class);

    private RefField field_NBTagByte_data = class_NBTTagByte.findField(byte.class);
    private RefField field_NBTagShort_data = class_NBTTagShort.findField(short.class);
    private RefField field_NBTagInt_data = class_NBTTagInt.findField(int.class);
    private RefField field_NBTagLong_data = class_NBTTagLong.findField(long.class);
    private RefField field_NBTagFloat_data = class_NBTTagFloat.findField(float.class);
    private RefField field_NBTagDouble_data = class_NBTTagDouble.findField(double.class);
    private RefField field_NBTagString_data = class_NBTTagString.findField(String.class);
    private RefField field_NBTagByteArray_data = class_NBTTagByteArray.findField(byte[].class);
    private RefField field_NBTagIntArray_data = class_NBTTagIntArray.findField(int[].class);
    private RefField field_NBTagCompound_map = class_NBTTagCompound.findField(Map.class);
    private RefField field_NBTagList_list = class_NBTTagList.findField(List.class);
    private RefField field_NBTagList_byte = class_NBTTagList.findField(byte.class);
    private RefField field_NBTBase_name = class_NBTBase.findField(String.class);

    private RefMethod met_NBTBase_getTypeId = class_NBTBase.findMethodByReturnType(byte.class);
    private RefMethod met_NBTBase_clone = class_NBTBase.findMethodByName("clone");
    private RefMethod met_NBTBase_createTag = class_NBTBase.findMethodByParams(byte.class, String.class);
    private RefMethod met_NBTBase_write = class_NBTBase.findMethodByParams(DataOutput.class);

    private RefConstructor con_NBTReadLimiter;
    private RefMethod met_NBTBase_load;
    private int met_NBTBase_load_args = 0;

    NBTUtils_Bukkit_named(){
        if (met_NBTBase_load_args==0) try{
            RefClass class_NBTReadLimiter = getRefClass("{nms}.NBTReadLimiter, {nm}.nbt.NBTReadLimiter, {NBTReadLimiter}");
            con_NBTReadLimiter = class_NBTReadLimiter.getConstructor(long.class);
            met_NBTBase_load = class_NBTBase.findMethodByParams(DataInput.class, int.class, class_NBTReadLimiter);
            met_NBTBase_load_args = 3;
        } catch (Exception ignored){}
        if (met_NBTBase_load_args==0) try{
            met_NBTBase_load = class_NBTBase.findMethodByParams(DataInput.class, int.class);
            met_NBTBase_load_args = 2;
        } catch (Exception ignored){}
        if (met_NBTBase_load_args==0) {
            met_NBTBase_load = class_NBTBase.findMethod(
                    new MethodCondition().withTypes(DataInput.class).withAbstract(true)
            );
            met_NBTBase_load_args = 1;
        }
    }

    @Override
    RefClass getNBTCompoundRefClass(){
        return class_NBTTagCompound;
    }

    @Override
    public Object createTagByte(Byte a) {
        return con_NBTagByte.create("", a);
    }

    @Override
    public Object createTagShort(Short a) {
        return con_NBTagShort.create("", a);
    }

    @Override
    public Object createTagInt(Integer a) {
        return con_NBTagInt.create("", a);
    }

    @Override
    public Object createTagLong(Long a) {
        return con_NBTagLong.create("", a);
    }

    @Override
    public Object createTagFloat(Float a) {
        return con_NBTagFloat.create("", a);
    }

    @Override
    public Object createTagDouble(Double a) {
        return con_NBTagDouble.create("", a);
    }

    @Override
    public Object createTagString(CharSequence a) {
        return con_NBTagString.create("", a.toString());
    }

    @Override
    public Object createTagByteArray(byte[] a) {
        return con_NBTagByteArray.create("", a);
    }

    @Override
    public Object createTagIntArray(int[] a) {
        return con_NBTagIntArray.create("", a);
    }

    @Override
    public Object getValue(Object tag) throws NBTReadException {
        if (tag==null) return null;
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        switch (getTagType(tag)){
            case 1: return field_NBTagByte_data.of(tag).get();
            case 2: return field_NBTagShort_data.of(tag).get();
            case 3: return field_NBTagInt_data.of(tag).get();
            case 4: return field_NBTagLong_data.of(tag).get();
            case 5: return field_NBTagFloat_data.of(tag).get();
            case 6: return field_NBTagDouble_data.of(tag).get();
            case 7: return field_NBTagByteArray_data.of(tag).get();
            case 8: return field_NBTagString_data.of(tag).get();
            case 9: return NBTList.forNBT(tag);
            case 10: return NBTCompound.forNBT(tag);
            case 11: return field_NBTagIntArray_data.of(tag).get();
            default: throw new RuntimeException("unexpected tag: "+tag.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setRawValue(Object tag, Object value) throws NBTReadException {
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        switch (getTagType(tag)){
            case 1: field_NBTagByte_data.of(tag).set(value); break;
            case 2: field_NBTagShort_data.of(tag).set(value); break;
            case 3: field_NBTagInt_data.of(tag).set(value); break;
            case 4: field_NBTagLong_data.of(tag).set(value); break;
            case 5: field_NBTagFloat_data.of(tag).set(value); break;
            case 6: field_NBTagDouble_data.of(tag).set(value); break;
            case 7: field_NBTagByteArray_data.of(tag).set(value); break;
            case 8: field_NBTagString_data.of(tag).set(value); break;
            case 9: NBTList list = NBTList.forNBT(tag);
                list.clear();
                list.addAll((Collection)value);
                break;
            case 10: NBTCompound compound = NBTCompound.forNBT(tag);
                compound.clear();
                compound.putAll((Map)value);
                break;
            case 11: field_NBTagIntArray_data.of(tag).set(value); break;
            default: throw new RuntimeException("unexpected tag: "+tag.getClass());
        }
    }

    @Override
    public byte getTagType(Object tag) throws NBTReadException{
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        return (Byte) met_NBTBase_getTypeId.of(tag).call();
    }

    @Override
    public Object createTagOfType(byte type) {
        return met_NBTBase_createTag.call(type, "");
    }

    @Override
    public Object cloneTag(Object tag) {
        return met_NBTBase_clone.of(tag).call();
    }

    @Override
    public Object createTagCompound() {
        return con_NBTagCompound.create("");
    }

    @Override
    public Object createTagList() {
        return con_NBTagList.create("");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getHandleMap(Object nbtTagCompound) {
        return (Map<String, Object>) field_NBTagCompound_map.of(nbtTagCompound).get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getHandleList(Object nbtTagList) {
        return (List<Object>) field_NBTagList_list.of(nbtTagList).get();
    }

    @Override
    public byte getNBTTagListType(Object nbtTagList) {
        return (Byte) field_NBTagList_byte.of(nbtTagList).get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setNBTTagListType(Object nbtTagList, byte type) {
        field_NBTagList_byte.of(nbtTagList).set(type);
    }

    @Override
    public boolean isNBTTag(Object tag) {
        return class_NBTBase.isInstance(tag);
    }

    @Override
    public void readInputToTag(DataInput input, Object tag) throws IOException {
        switch (met_NBTBase_load_args) {
            case 1:
                met_NBTBase_load.of(tag).call(input);
                break;
            case 2:
                met_NBTBase_load.of(tag).call(input, 0);
                break;
            case 3:
                long readLimit = Long.MAX_VALUE / 2;
                met_NBTBase_load.of(tag).call(input, 0, con_NBTReadLimiter.create(readLimit));
                break;
        }
    }

    @Override
    public void writeTagDataToOutput(DataOutput output, Object tag) throws IOException {
        met_NBTBase_write.of(tag).call(output);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void seTagName(Object tag, String name) {
        field_NBTBase_name.of(tag).set(name);
    }

    @Override
    public String getTagName(Object tag) {
        return (String) field_NBTBase_name.of(tag).get();
    }

}
