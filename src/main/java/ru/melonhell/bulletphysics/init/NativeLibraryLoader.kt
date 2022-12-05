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
package ru.melonhell.bulletphysics.init

import com.jme3.bullet.util.NativeLibrary
import com.jme3.system.JmeSystem
import com.jme3.system.Platform
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.stereotype.Component
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class NativeLibraryLoader(
    javaPlugin: JavaPlugin
) {
    private val nativesDir: File
    private val nativesTmpDir: File
    private val buildType = "Release"
    private val flavor = "Sp"
    private val currentVersion = NativeLibrary.expectedVersion

    init {
        nativesDir = File(javaPlugin.dataFolder, "natives")
        nativesTmpDir = File(System.getProperty("java.io.tmpdir"), "BulletPhysicsNatives")
    }

    @PostConstruct
    fun init() {
        val libFile = File(nativesDir, filename())
        if (!libFile.exists()) {
            libFile.parentFile.mkdirs()
            Files.copy(url().openStream(), libFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        try {
            load(libFile)
        } catch (ex: UnsatisfiedLinkError) {
            load(tempCopy(libFile))
        }
    }

    private fun filename(): String {
        val platform = JmeSystem.getPlatform()
        val ext = when (platform) {
            Platform.Windows32, Platform.Windows64, Platform.Windows_ARM32, Platform.Windows_ARM64 -> ".dll"
            Platform.Android_ARM7, Platform.Android_ARM8, Platform.Linux32, Platform.Linux64, Platform.Linux_ARM32, Platform.Linux_ARM64 -> ".so"
            Platform.MacOSX32, Platform.MacOSX64, Platform.MacOSX_ARM64 -> ".dylib"
            else -> throw RuntimeException("platform = $platform")
        }
        return "libbulletjme_v" + currentVersion + "_" + platform.name.lowercase(Locale.getDefault()) + "_" + buildType.lowercase(
            Locale.getDefault()
        ) + "_" + flavor.lowercase(Locale.getDefault()) + ext
    }

    private fun url(): URL {
        assert(buildType == "Debug" || buildType == "Release")
        assert(flavor == "Sp" || flavor == "SpMt" || flavor == "SpMtQuickprof" || flavor == "SpQuickprof" || flavor == "Dp" || flavor == "DpMt")
        val platform = JmeSystem.getPlatform()
        val name = when (platform) {
            Platform.Windows32, Platform.Windows64, Platform.Windows_ARM32, Platform.Windows_ARM64 -> "bulletjme.dll"
            Platform.Android_ARM7, Platform.Android_ARM8, Platform.Linux32, Platform.Linux64, Platform.Linux_ARM32, Platform.Linux_ARM64 -> "libbulletjme.so"
            Platform.MacOSX32, Platform.MacOSX64, Platform.MacOSX_ARM64 -> "libbulletjme.dylib"
            else -> throw RuntimeException("platform = $platform")
        }
        return URL("https://github.com/stephengold/Libbulletjme/releases/download/" + currentVersion + "/" + platform + buildType + flavor + "_" + name)
    }

    private fun tempCopy(file: File): File {
        nativesTmpDir.mkdirs()
        val res = File(nativesTmpDir, UUID.randomUUID().toString() + "_" + file.name)
        Files.copy(file.toPath(), res.toPath())
        res.deleteOnExit()
        return res
    }

    private fun load(file: File): Boolean {
        val absoluteFilename = file.absolutePath
        var success = false
        if (!file.exists()) {
            logger.log(Level.SEVERE, "{0} does not exist", absoluteFilename)
        } else if (!file.canRead()) {
            logger.log(Level.SEVERE, "{0} is not readable", absoluteFilename)
        } else {
            logger.log(Level.INFO, "Loading native library from {0}", absoluteFilename)
            System.load(absoluteFilename)
            success = true
        }
        return success
    }

    companion object {
        private val logger = Logger.getLogger(NativeLibraryLoader::class.java.name)
    }
}