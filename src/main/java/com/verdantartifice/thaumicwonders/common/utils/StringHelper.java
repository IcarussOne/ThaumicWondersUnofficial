package com.verdantartifice.thaumicwonders.common.utils;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.TreeMap;

public class StringHelper {
    private static final TreeMap<Integer, String> NUMERAL_MAP = new TreeMap<>();

    public static String getTranslationKey(String unlocName, String type, String... params) {
        StringBuilder builder = new StringBuilder(type + "." + ThaumicWonders.MODID + ":" + unlocName);
        for (String str : params) {
            builder.append(".").append(str);
        }
        return builder.toString();
    }

    public static String getLocalizedString(String unlocName, String type, String... params) {
        return I18n.format(getTranslationKey(unlocName, type, params));
    }

    public static TextComponentTranslation getTranslatedTextComponent(String unlocName, String type, String... params) {
        return new TextComponentTranslation(getTranslationKey(unlocName, type, params));
    }

    public static String getDimensionName(int dimensionId) {
        if (!DimensionManager.isDimensionRegistered(dimensionId)) {
            return Integer.toString(dimensionId);
        }
        DimensionType type = DimensionManager.getProviderType(dimensionId);
        if (type == null) {
            return Integer.toString(dimensionId);
        }
        String name = type.getName();
        int[] dims = DimensionManager.getDimensions(type);
        if (dims != null && dims.length > 1) {
            name += " " + dimensionId;
        }
        return name;
    }

    public static String toRomanNumeral(int number) {
        int l =  NUMERAL_MAP.floorKey(number);
        if (number == l) {
            return NUMERAL_MAP.get(number);
        }
        return NUMERAL_MAP.get(l) + toRomanNumeral(number - l);
    }

    static {
        NUMERAL_MAP.put(1000, "M");
        NUMERAL_MAP.put(900, "CM");
        NUMERAL_MAP.put(500, "D");
        NUMERAL_MAP.put(400, "CD");
        NUMERAL_MAP.put(100, "C");
        NUMERAL_MAP.put(90, "XC");
        NUMERAL_MAP.put(50, "L");
        NUMERAL_MAP.put(40, "XL");
        NUMERAL_MAP.put(10, "X");
        NUMERAL_MAP.put(9, "IX");
        NUMERAL_MAP.put(5, "V");
        NUMERAL_MAP.put(4, "IV");
        NUMERAL_MAP.put(1, "I");
    }
}
