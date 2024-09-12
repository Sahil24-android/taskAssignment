package com.task.taskapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.task.taskapplication.repository.UserRepository
import com.task.taskapplication.room.entity.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableLiveData<List<UserEntity>>()
    val users: LiveData<List<UserEntity>> get() = _users

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchUsers() {
        viewModelScope.launch {
            val result = repository.getUsers()
            repository.saveUsersToDatabase(result)
            _users.value = repository.getUsersFromDatabase()
        }
    }

    fun updateUserImage(userId: Int, imagePath: String) {
        viewModelScope.launch {
            repository.updateUserImage(userId, imagePath)
            _users.value = repository.getUsersFromDatabase()
        }
    }
}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}