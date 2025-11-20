package com.byajpay.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null
)

