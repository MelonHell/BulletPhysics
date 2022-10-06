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
package ru.melonhell.bulletphysics.init;

import com.jme3.bullet.util.NativeLibrary;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public final class NativeLibraryLoader {
    private static final Logger logger = Logger.getLogger(NativeLibraryLoader.class.getName());
    private final File nativesDir;
    private final File nativesTmpDir;
    private final String buildType = "Release";
    private final String flavor = "Sp";
    private final String currentVersion = NativeLibrary.expectedVersion;

    public NativeLibraryLoader(JavaPlugin javaPlugin) {
        this.nativesDir = new File(javaPlugin.getDataFolder(), "natives");
        this.nativesTmpDir = new File(System.getProperty("java.io.tmpdir"), "BulletPhysicsNatives");
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        File libFile = new File(nativesDir, getFileName());

        if (!libFile.exists()) {
            libFile.getParentFile().mkdirs();
            Files.copy(getUrl().openStream(), libFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            load(libFile);
        } catch (UnsatisfiedLinkError ex) {
            load(tempCopy(libFile));
        }
    }

    public String getFileName() {
        Platform platform = JmeSystem.getPlatform();

        String ext = switch (platform) {
            case Windows32, Windows64, Windows_ARM32, Windows_ARM64 -> ".dll";
            case Android_ARM7, Android_ARM8, Linux32, Linux64, Linux_ARM32, Linux_ARM64 -> ".so";
            case MacOSX32, MacOSX64, MacOSX_ARM64 -> ".dylib";
            default -> throw new RuntimeException("platform = " + platform);
        };
        return "libbulletjme_v" + currentVersion + "_" + platform.name().toLowerCase() + "_" + buildType.toLowerCase() + "_" + flavor.toLowerCase() + ext;
    }

    @SneakyThrows
    @SuppressWarnings({"ConstantConditions", "ConditionCoveredByFurtherCondition"})
    public URL getUrl() {
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
        return new URL("https://github.com/stephengold/Libbulletjme/releases/download/" + currentVersion + "/" + platform + buildType + flavor + "_" + name);
    }

    @SneakyThrows
    public File tempCopy(File file) {
        nativesTmpDir.mkdirs();
        File res = new File(nativesTmpDir, UUID.randomUUID() + "_" + file.getName());
        Files.copy(file.toPath(), res.toPath());
        res.deleteOnExit();
        return res;
    }

    public boolean load(File file) {
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
