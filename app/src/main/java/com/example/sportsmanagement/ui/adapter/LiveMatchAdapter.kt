package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.MatchStatus
import com.example.sportsmanagement.databinding.ItemLiveMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class LiveMatchAdapter(
    private val onItemClick: (Match) -> Unit
) : ListAdapter<Match, LiveMatchAdapter.LiveMatchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveMatchViewHolder {
        val binding = ItemLiveMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LiveMatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LiveMatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bind(match)
    }

    inner class LiveMatchViewHolder(
        private val binding: ItemLiveMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            binding.apply {
                // Set match details
                categoryNameText.text = match.categoryName
                player1NameText.text = match.player1Name
                player2NameText.text = match.player2Name
                
                // Set scores
                player1ScoreText.text = match.score.player1Score.toString()
                player2ScoreText.text = match.score.player2Score.toString()
                
                // Set venue
                venueText.text = match.venue
                
                // Set round information
                roundText.text = match.round
                
                // Show live indicator
                when (match.status) {
                    MatchStatus.LIVE -> {
                        liveIndicator.visibility = android.view.View.VISIBLE
                        statusText.text = "LIVE"
                        statusText.setTextColor(binding.root.context.getColor(R.color.live_color))
                    }
                    else -> {
                        liveIndicator.visibility = android.view.View.GONE
                        statusText.text = match.status.name
                        statusText.setTextColor(binding.root.context.getColor(R.color.secondary_text))
                    }
                }

                // Set click listener
                root.setOnClickListener {
                    onItemClick(match)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}