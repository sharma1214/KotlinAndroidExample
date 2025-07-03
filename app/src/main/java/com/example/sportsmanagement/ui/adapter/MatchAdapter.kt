package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.User
import com.example.sportsmanagement.databinding.ItemMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class MatchAdapter(
    private val onMatchClick: (Match) -> Unit,
    private val onScoreUpdate: ((Match, Match.Score) -> Unit)?
) : ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback()) {

    private var currentUser: User? = null

    fun updateCurrentUser(user: User?) {
        this.currentUser = user
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MatchViewHolder(
        private val binding: ItemMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            // Player names
            binding.player1NameText.text = match.player1Name ?: "Player 1"
            binding.player2NameText.text = match.player2Name ?: "Player 2"
            
            // Scores
            binding.player1ScoreText.text = match.score.player1Score.toString()
            binding.player2ScoreText.text = match.score.player2Score.toString()
            
            // Match time
            val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            binding.matchTimeText.text = dateFormat.format(Date(match.scheduledTime))
            
            // Venue
            binding.venueText.text = match.venue
            
            // Match status
            binding.matchStatusText.text = match.status.name
            val statusColor = when (match.status) {
                Match.Status.LIVE -> R.color.live_match_color
                Match.Status.SCHEDULED -> R.color.scheduled_match_color
                Match.Status.COMPLETED -> R.color.completed_match_color
                Match.Status.CANCELLED -> R.color.cancelled_match_color
            }
            binding.matchStatusText.setTextColor(binding.root.context.getColor(statusColor))
            
            // Live indicator
            binding.liveIndicator.visibility = if (match.status == Match.Status.LIVE) View.VISIBLE else View.GONE
            
            // Winner highlight (for completed matches)
            if (match.status == Match.Status.COMPLETED && match.winnerId != null) {
                when (match.winnerId) {
                    match.player1Id -> {
                        binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                        binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    }
                    match.player2Id -> {
                        binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                        binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.winner_color))
                    }
                    else -> {
                        binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                        binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    }
                }
            } else {
                binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
            }
            
            // Admin controls (score update buttons)
            val showAdminControls = currentUser?.isAdmin == true && 
                                   match.status == Match.Status.LIVE && 
                                   onScoreUpdate != null
            
            binding.adminControlsGroup.visibility = if (showAdminControls) View.VISIBLE else View.GONE
            
            if (showAdminControls) {
                binding.player1ScoreIncrement.setOnClickListener {
                    val newScore = match.score.copy(player1Score = match.score.player1Score + 1)
                    onScoreUpdate?.invoke(match, newScore)
                }
                
                binding.player1ScoreDecrement.setOnClickListener {
                    val newScore = match.score.copy(player1Score = maxOf(0, match.score.player1Score - 1))
                    onScoreUpdate?.invoke(match, newScore)
                }
                
                binding.player2ScoreIncrement.setOnClickListener {
                    val newScore = match.score.copy(player2Score = match.score.player2Score + 1)
                    onScoreUpdate?.invoke(match, newScore)
                }
                
                binding.player2ScoreDecrement.setOnClickListener {
                    val newScore = match.score.copy(player2Score = maxOf(0, match.score.player2Score - 1))
                    onScoreUpdate?.invoke(match, newScore)
                }
            }
            
            // Match card background based on status
            val cardBackgroundRes = when (match.status) {
                Match.Status.LIVE -> R.color.live_match_background
                Match.Status.SCHEDULED -> R.color.scheduled_match_background
                Match.Status.COMPLETED -> R.color.completed_match_background
                Match.Status.CANCELLED -> R.color.cancelled_match_background
            }
            binding.matchCard.setCardBackgroundColor(binding.root.context.getColor(cardBackgroundRes))
            
            // Click listener
            binding.root.setOnClickListener { onMatchClick(match) }
        }
    }

    private class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}