package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.SportCategory
import com.example.sportsmanagement.databinding.ItemSportCategoryBinding

class SportCategoryAdapter(
    private val onItemClick: (SportCategory) -> Unit
) : ListAdapter<SportCategory, SportCategoryAdapter.SportCategoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCategoryViewHolder {
        val binding = ItemSportCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SportCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SportCategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class SportCategoryViewHolder(
        private val binding: ItemSportCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: SportCategory) {
            binding.apply {
                // Set category name
                categoryNameText.text = category.name
                
                // Set description
                categoryDescriptionText.text = category.description
                
                // Set participant count
                participantCountText.text = "${category.participants.size}/${category.maxParticipants} players"
                
                // Load category image
                if (category.imageUrl.isNotEmpty()) {
                    Glide.with(binding.root.context)
                        .load(category.imageUrl)
                        .transform(RoundedCorners(16))
                        .placeholder(R.drawable.ic_sports_placeholder)
                        .error(R.drawable.ic_sports_placeholder)
                        .into(categoryImageView)
                } else {
                    // Set default image based on category type
                    val defaultImage = when (category.id) {
                        SportCategory.CHESS -> R.drawable.ic_chess
                        SportCategory.CAROM -> R.drawable.ic_carom
                        SportCategory.TABLE_TENNIS -> R.drawable.ic_table_tennis
                        SportCategory.BADMINTON -> R.drawable.ic_badminton
                        SportCategory.CRICKET -> R.drawable.ic_cricket
                        SportCategory.BILLIARDS -> R.drawable.ic_billiards
                        else -> R.drawable.ic_sports_placeholder
                    }
                    categoryImageView.setImageResource(defaultImage)
                }
                
                // Set active status
                activeIndicator.visibility = if (category.isActive) 
                    android.view.View.VISIBLE else android.view.View.GONE

                // Set click listener
                root.setOnClickListener {
                    onItemClick(category)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SportCategory>() {
        override fun areItemsTheSame(oldItem: SportCategory, newItem: SportCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SportCategory, newItem: SportCategory): Boolean {
            return oldItem == newItem
        }
    }
}