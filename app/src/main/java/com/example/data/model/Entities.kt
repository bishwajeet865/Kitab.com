package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chronicles")
data class Chronic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val userHandle: String,
    val avatarColorIndex: Int = 0, // Used to procedurally draw/render futuristic neon avatar gradients
    val timestamp: Long = System.currentTimeMillis(),
    val latency: String = "1.8ms",  // Space-time telemetry data
    val nodeSector: String = "CORE-SEC-09", // Cosmic router quadrant
    val text: String,
    val pulseCount: Int = 12,
    val isPulsed: Boolean = false,
    val isArchived: Boolean = false,
    val commentsCount: Int = 0,
    val visualSeed: Int = 0 // Used to generate distinct cool vector abstract telemetry designs procedurally
)

@Entity(tableName = "echoes")
data class Echo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chronicId: Int,
    val username: String,
    val userHandle: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sectorSource: String = "NEBULA-X4"
)
