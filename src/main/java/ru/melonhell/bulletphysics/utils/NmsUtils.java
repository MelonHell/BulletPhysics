package ru.melonhell.bulletphysics.utils;

import ru.melonhell.bulletphysics.nms.NmsTools;
import ru.melonhell.bulletphysics.nms.NmsTools_v1_19_2;

public class NmsUtils {
    private static NmsTools nmsTools;
    public static NmsTools getNmsTools() {
        if (nmsTools == null) nmsTools = new NmsTools_v1_19_2();
        return nmsTools;
    }
}
