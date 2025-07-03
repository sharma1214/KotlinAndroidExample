package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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

        fun bind(category: SportCategory) {
            binding.categoryNameText.text = category.name
            binding.categoryDescriptionText.text = category.description
            binding.participantCountText.text = "${category.participants.size}/${category.maxParticipants}"
            
            // Set sport icon and color based on category
            val iconRes = when (category.name.lowercase()) {
                "chess" -> R.drawable.ic_chess_24
                "carom" -> R.drawable.ic_carom_24
                "table tennis" -> R.drawable.ic_table_tennis_24
                "badminton" -> R.drawable.ic_badminton_24
                "cricket" -> R.drawable.ic_cricket_24
                "billiards" -> R.drawable.ic_billiards_24
                else -> R.drawable.ic_sports_24
            }
            binding.categoryIcon.setImageResource(iconRes)
            
            // Set category color
            val colorRes = when (category.name.lowercase()) {
                "chess" -> R.color.chess_color
                "carom" -> R.color.carom_color
                "table tennis" -> R.color.table_tennis_color
                "badminton" -> R.color.badminton_color
                "cricket" -> R.color.cricket_color
                "billiards" -> R.color.billiards_color
                else -> R.color.primary
            }
            val color = binding.root.context.getColor(colorRes)
            binding.categoryCard.setCardBackgroundColor(color)
            
            // Registration status
            val isRegistered = currentUser?.let { user ->
                category.participants.contains(user.id)
            } ?: false
            
            val isFull = category.participants.size >= category.maxParticipants
            
            binding.registerButton.text = if (isRegistered) "Registered" else {
                if (isFull) "Full" else "Register"
            }
            binding.registerButton.isEnabled = isRegistered || !isFull
            
            // Status indicator
            binding.statusIndicator.text = when {
                !category.isActive -> "Inactive"
                isFull -> "Full"
                else -> "Open"
            }
            
            val statusColor = when {
                !category.isActive -> R.color.inactive_status_color
                isFull -> R.color.full_status_color
                else -> R.color.open_status_color
            }
            binding.statusIndicator.setTextColor(binding.root.context.getColor(statusColor))
            
            // Click listeners
            binding.root.setOnClickListener { onCategoryClick(category) }
            binding.registerButton.setOnClickListener { onRegisterClick(category) }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<SportCategory>() {
        override fun areItemsTheSame(oldItem: SportCategory, newItem: SportCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SportCategory, newItem: SportCategory): Boolean {
            return oldItem == newItem
        }
    }
}