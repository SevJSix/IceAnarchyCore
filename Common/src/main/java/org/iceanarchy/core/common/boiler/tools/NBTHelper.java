package org.iceanarchy.core.common.boiler.tools;

import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTReadLimiter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagEnd;

import java.io.*;
import java.lang.reflect.Method;
public class NBTHelper {

    private static Method loadM;
    private static Method createTagM;

    static {
        try {
            createTagM = NBTBase.class.getDeclaredMethod("createTag", byte.class);
            createTagM.setAccessible(true);
            loadM = NBTBase.class.getDeclaredMethod("load", DataInput.class, int.class, NBTReadLimiter.class);
            loadM.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static NBTBase readBaseFromInput(DataInput input) throws Throwable {
        byte typeId = input.readByte();
        if (typeId == 0) return NBTTagEnd.class.newInstance();
        NBTBase base = (NBTBase) createTagM.invoke(NBTBase.class, typeId);
        input.readUTF();
        loadM.invoke(base, input, 0, NBTReadLimiter.a);
        return base;
    }

    @SneakyThrows
    public static NBTTagCompound readNBT(DataInput input) {
        return (NBTTagCompound) readBaseFromInput(input);
    }

    @SneakyThrows
    public static void writeNBT(NBTTagCompound compound, DataOutput output) {
        output.writeByte(compound.getTypeId());
        if (compound.getTypeId() == 0) return;
        output.writeUTF("");
        Method writeM = NBTBase.class.getDeclaredMethod("write", DataOutput.class);
        writeM.setAccessible(true);
        writeM.invoke(compound, output);
    }

    @SneakyThrows
    public static NBTTagCompound loadNBTFromFile(File file) {
        FileInputStream fis = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fis);
        NBTTagCompound compound = readNBT(in);
        in.close();
        fis.close();
        return compound;
    }

    @SneakyThrows
    public static void writeAndFlush(NBTTagCompound compound, File file) {
        FileOutputStream fos = new FileOutputStream(file);
        DataOutputStream out = new DataOutputStream(fos);
        NBTHelper.writeNBT(compound, out);
        out.flush();
        out.close();
        fos.close();
    }
}
