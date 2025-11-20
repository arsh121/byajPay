package com.byajpay.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val phoneNumber: String,
    val name: String? = null,
    val businessName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null
)

