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
import com.example.sportsmanagement.data.model.MatchStatus
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
            binding.apply {
                // Category name
                categoryNameText.text = match.categoryName ?: "Sport"

                // Player names
                player1NameText.text = match.player1Name ?: "Player 1"
                player2NameText.text = match.player2Name ?: "Player 2"

                // Scores
                player1ScoreText.text = match.score.player1Score.toString()
                player2ScoreText.text = match.score.player2Score.toString()

                // Match time
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                matchTimeText.text = dateFormat.format(Date(match.scheduledTime))

                // Venue
                venueText.text = match.venue

                // Round info
                roundText.text = match.round

                // Category indicator color
                val categoryColor = getCategoryColor(match.categoryName ?: "")
                categoryIndicator.setBackgroundColor(root.context.getColor(categoryColor))

                // Match status
                when (match.status) {
                    Match.Status.LIVE, MatchStatus.LIVE -> {
                        liveIndicator.visibility = View.VISIBLE
                        liveText.visibility = View.VISIBLE
                        statusText.text = "LIVE"
                        statusText.setTextColor(root.context.getColor(R.color.live_color))
                        startPulseAnimation()
                        highlightLeadingPlayer(match)

                        // Duration
                        val duration = System.currentTimeMillis() - match.scheduledTime
                        val minutes = duration / (1000 * 60)
                        durationText.text = "${minutes}m"
                        durationText.visibility = View.VISIBLE
                    }
                    else -> {
                        liveIndicator.visibility = View.GONE
                        liveText.visibility = View.GONE
                        statusText.text = match.status.name
                        statusText.setTextColor(root.context.getColor(R.color.secondary_text))
                        durationText.visibility = View.GONE
                        stopPulseAnimation()
                        resetPlayerHighlight()
                    }
                }

                // Click listener
                root.setOnClickListener {
                    onMatchClick(match)
                }
            }
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
                    binding.player1ScoreText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player2ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                }
                player2Score > player1Score -> {
                    binding.player2ScoreText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player2NameText.setTextColor(binding.root.context.getColor(R.color.leading_player_color))
                    binding.player1ScoreText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                    binding.player1NameText.setTextColor(binding.root.context.getColor(R.color.default_text_color))
                }
                else -> {
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
