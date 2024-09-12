package com.task.taskapplication.repository

import com.task.taskapplication.data.UserResponse
import com.task.taskapplication.network.ApiServices
import com.task.taskapplication.network.Result
import com.task.taskapplication.network.RetrofitClient
import com.task.taskapplication.room.dao.UserDao
import com.task.taskapplication.room.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun getUsers(): List<UserEntity> {
        val response = RetrofitClient.apiServices.getUsers()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.data.map {
                UserEntity(it.id!!, it.email!!, it.firstName!!, it.lastName!!, it.avatar)
            }
        } else {
            emptyList()
        }
    }

    suspend fun getUsersFromDatabase(): List<UserEntity> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsers()
        }
    }

    suspend fun saveUsersToDatabase(users: List<UserEntity>) {
        withContext(Dispatchers.IO) {
            userDao.insertUsers(users)
        }
    }

    suspend fun updateUserImage(userId: Int, imagePath: String) {
        withContext(Dispatchers.IO) {
            userDao.updateUserImage(userId, imagePath)
        }
    }
}