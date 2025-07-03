package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.SportCategory
import com.example.sportsmanagement.data.model.User
import com.example.sportsmanagement.databinding.ItemSportCategoryBinding

class SportCategoryAdapter(
    private val onCategoryClick: (SportCategory) -> Unit,
    private val onRegisterClick: (SportCategory) -> Unit
) : ListAdapter<SportCategory, SportCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var currentUser: User? = null

    fun updateCurrentUser(user: User?) {
        this.currentUser = user
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemSportCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(
        private val binding: ItemSportCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: SportCatego
