/*
 * Copyright (c) 2009-2019 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dev.lazurite.rayon;

import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import lombok.SneakyThrows;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NativeLibraryLoader {
    final public static Logger logger = Logger.getLogger(NativeLibraryLoader.class.getName());

    public static String getName(String buildType, String flavor) {
        assert buildType.equals("Debug") || buildType.equals("Release") :
                buildType;
        assert flavor.equals("Sp") || flavor.equals("SpMt")
                || flavor.equals("SpMtQuickprof")
                || flavor.equals("SpQuickprof")
                || flavor.equals("Dp") || flavor.equals("DpMt") : flavor;

        Platform platform = JmeSystem.getPlatform();

        String name = switch (platform) {
            case Windows32, Windows64, Windows_ARM32, Windows_ARM64 -> "bulletjme.dll";
            case Android_ARM7, Android_ARM8, Linux32, Linux64, Linux_ARM32, Linux_ARM64 -> "libbulletjme.so";
            case MacOSX32, MacOSX64, MacOSX_ARM64 -> "libbulletjme.dylib";
            default -> throw new RuntimeException("platform = " + platform);
        };
        return platform + buildType + flavor + "_" + name;
    }

    @SneakyThrows
    public static File copyTmp(File file) {
        File tmpDir = new File(file.getParentFile(), "tmp");
        tmpDir.mkdirs();
        File res = new File(tmpDir, UUID.randomUUID() + file.getName());
        try (InputStream is = new FileInputStream(file); OutputStream os = new FileOutputStream(res)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
        return res;
    }
    public static boolean load(File file) {
        String absoluteFilename = file.getAbsolutePath();
        boolean success = false;
        if (!file.exists()) {
            logger.log(Level.SEVERE, "{0} does not exist", absoluteFilename);
        } else if (!file.canRead()) {
            logger.log(Level.SEVERE, "{0} is not readable", absoluteFilename);
        } else {
            logger.log(Level.INFO, "Loading native library from {0}", absoluteFilename);
            System.load(absoluteFilename);
            success = true;
        }

        return success;
    }
}
