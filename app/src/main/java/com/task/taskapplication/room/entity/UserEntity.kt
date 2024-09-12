package com.task.taskapplication.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
    data class UserEntity(
    @PrimaryKey val id: Int,
    val email: String,
    val firstName: String,
    val lastName: String,
    val avatar: String?,
    val uploadedImage: String? = null
)
