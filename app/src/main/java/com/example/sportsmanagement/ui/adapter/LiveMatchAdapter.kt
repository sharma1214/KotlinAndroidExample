package com.example.sportsmanagement.ui.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.databinding.ItemLiveMatchBinding
import java.text.SimpleDateFormat
import java.util.*

class LiveMatchAdapter(
    private val onMatchClick: (Match) -> Unit
) : ListAdapter<Match, LiveMatchAdapter.LiveMatchViewHolder>(LiveMatchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveMatchViewHolder {
        val binding = ItemLiveMatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LiveMatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LiveMatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LiveMatchViewHolder(
        private val binding: ItemLiveMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var pulseAnimator: ObjectAnimator? = null

        fun bind(match: Match) {
            // Player names
            binding.player1NameText.text = match.player1Name ?: "Player 1"
            binding.player2NameText.text = match.player2Name ?: "Player 2"
            
            // Scores with larger, more prominent display
            binding.player1ScoreText.text = match.score.player1Score.toString()
            binding.player2ScoreText.text = match.score.player2Score.toString()
            
            // Match time
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.matchTimeText.text = dateFormat.format(Date(match.scheduledTime))
            
            // Venue
            binding.venueText.text = match.venue
            
            // Category/Sport
            binding.categoryText.text = match.categoryName ?: "Sport"
            
            // Set category color for visual identification
            val categoryColor = getCategoryColor(match.categoryName ?: "")
            binding.categoryIndicator.setBackgroundColor(binding.root.context.getColor(categoryColor))
            
            // Live indicator with pulsing animation
            if (match.status == Match.Status.LIVE) {
                binding.liveIndicator.visibility = View.VISIBLE
                binding.liveText.visibility = View.VISIBLE
                startPulseAnimation()
                
                // Highlight current leading player
                highlightLeadingPlayer(match)
            } else {
                binding.liveIndicator.visibility = View.GONE
                binding.liveText.visibility = View.GONE
                stopPulseAnimation()
                resetPlayerHighlight()
            }
            
            // Match duration (for live matches)
            if (match.status == Match.Status.LIVE) {
                val duration = System.currentTimeMillis() - match.scheduledTime
                val minutes = duration / (1000 * 60)
                binding.durationText.text = "${minutes}m"
                binding.durationText.visibility = View.VISIBLE
            } else {
                binding.durationText.visibility = View.GONE
            }
            
            // Click listener
            binding.root.setOnClickListener { onMatchClick(match) }
        }

        private fun startPulseAnimation() {
            pulseAnimator?.cancel()
            pulseAnimator = ObjectAnimator.ofFloat(binding.liveIndicator, "alpha", 1f, 0.3f, 1f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
        }

        private fun stopPulseAnimation() {
            pulseAnimator?.cancel()
            binding.liveIndicator.alpha = 1f
        }

        private fun highlightLeadingPlayer(match: Match) {
            val player1Score = match.score.player1Score
            val player2Score = match.score.player2Score
            
            when {
                player1Score > player2Score -> {
                    // Player 1 is leading
                    binding.player1ScoreText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player2ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                }
                player2Score > player1Score -> {
                    // Player 2 is leading
                    binding.player1ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    binding.player2ScoreText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                }
                else -> {
                    // Tied
                    resetPlayerHighlight()
                }
            }
        }

        private fun resetPlayerHighlight() {
            binding.player1ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
            binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
            binding.player2ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
            binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
        }

        private fun getCategoryColor(categoryName: String): Int {
            return when (categoryName.lowercase()) {
                "chess" -> R.color.chess_color
                "carom" -> R.color.carom_color
                "table tennis" -> R.color.table_tennis_color
                "badminton" -> R.color.badminton_color
                "cricket" -> R.color.cricket_color
                "billiards" -> R.color.billiards_color
                else -> R.color.primary
            }
        }

        fun onViewRecycled() {
            stopPulseAnimation()
        }
    }

    override fun onViewRecycled(holder: LiveMatchViewHolder) {
        super.onViewRecycled(holder)
        holder.onViewRecycled()
    }

    private class LiveMatchDiffCallback : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem == newItem
        }
    }
}