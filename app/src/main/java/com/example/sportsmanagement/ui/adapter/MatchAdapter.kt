package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.MatchStatus
import com.example.sportsmanagement.databinding.ItemMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class MatchAdapter(
    private val onItemClick: (Match) -> Unit
) : ListAdapter<Match, MatchAdapter.MatchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position)
        holder.bind(match)
    }

    inner class MatchViewHolder(
        private val binding: ItemMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(match: Match) {
            binding.apply {
                // Set match details
                categoryNameText.text = match.categoryName
                player1NameText.text = match.player1Name
                player2NameText.text = match.player2Name
                
                // Set venue and round
                venueText.text = match.venue
                roundText.text = match.round
                
                // Set scheduled time
                val date = Date(match.scheduledTime)
                matchDateText.text = dateFormat.format(date)
                matchTimeText.text = timeFormat.format(date)
                
                // Handle different match statuses
                when (match.status) {
                    MatchStatus.SCHEDULED -> {
                        statusText.text = "Scheduled"
                        statusText.setTextColor(binding.root.context.getColor(R.color.scheduled_color))
                        scoreContainer.visibility = android.view.View.GONE
                        timeContainer.visibility = android.view.View.VISIBLE
                    }
                    MatchStatus.COMPLETED -> {
                        statusText.text = "Completed"
                        statusText.setTextColor(binding.root.context.getColor(R.color.completed_color))
                        scoreContainer.visibility = android.view.View.VISIBLE
                        timeContainer.visibility = android.view.View.GONE
                        
                        // Set scores for completed matches
                        player1ScoreText.text = match.score.player1Score.toString()
                        player2ScoreText.text = match.score.player2Score.toString()
                        
                        // Highlight winner
                        if (match.winnerId.isNotEmpty()) {
                            if (match.winnerId == match.player1Id) {
                                player1NameText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                                player1ScoreText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                            } else if (match.winnerId == match.player2Id) {
                                player2NameText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                                player2ScoreText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                            }
                        }
                    }
                    MatchStatus.CANCELLED -> {
                        statusText.text = "Cancelled"
                        statusText.setTextColor(binding.root.context.getColor(R.color.cancelled_color))
                        scoreContainer.visibility = android.view.View.GONE
                        timeContainer.visibility = android.view.View.VISIBLE
                    }
                    MatchStatus.POSTPONED -> {
                        statusText.text = "Postponed"
                        statusText.setTextColor(binding.root.context.getColor(R.color.postponed_color))
                        scoreContainer.visibility = android.view.View.GONE
                        timeContainer.visibility = android.view.View.VISIBLE
                    }
                    MatchStatus.LIVE -> {
                        statusText.text = "Live"
                        statusText.setTextColor(binding.root.context.getColor(R.color.live_color))
                        scoreContainer.visibility = android.view.View.VISIBLE
                        timeContainer.visibility = android.view.View.GONE
                        
                        player1ScoreText.text = match.score.player1Score.toString()
                        player2ScoreText.text = match.score.player2Score.toString()
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