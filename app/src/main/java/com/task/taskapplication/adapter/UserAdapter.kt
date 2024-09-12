package com.task.taskapplication.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.task.taskapplication.databinding.UserItemBinding
import com.task.taskapplication.room.entity.UserEntity

class UserAdapter(private val listener: OnClick) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val list: ArrayList<UserEntity> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = list[position]
        holder.binding.name.text = "${user.firstName} ${user.lastName}"
        holder.binding.email.text = user.email
        if (!user.uploadedImage.isNullOrEmpty()) {
            Picasso.get().load(Uri.parse(user.uploadedImage)).into(holder.binding.avatar)
        } else {
            Picasso.get().load(user.avatar).into(holder.binding.avatar)
        }
    }

    fun updateList(newList: List<UserEntity>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }


    inner class UserViewHolder(val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.uploadIcon.setOnClickListener {
                listener.onUploadClick(list[adapterPosition])
            }
        }
    }

    interface OnClick {
        fun onUploadClick(user: UserEntity)
    }
}