package ru.melonhell.bulletphysics.bullet.collision.body.shape

import com.jme3.math.Quaternion

interface MinecraftShape {
    fun getTriangles(quaternion: Quaternion): List<Triangle>
    val volume: Float
}