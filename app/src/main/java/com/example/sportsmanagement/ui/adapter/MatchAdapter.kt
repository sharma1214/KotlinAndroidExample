package com.example.sportsmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.MatchStatus
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

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(match: Match) {
            binding.apply {
                // Player names
                player1NameText.text = match.player1Name ?: "Player 1"
                player2NameText.text = match.player2Name ?: "Player 2"

                // Venue and Round
                venueText.text = match.venue
                roundText.text = match.round

                // Date and Time
                val date = Date(match.scheduledTime)
                matchDateText.text = dateFormat.format(date)
                matchTimeText.text = timeFormat.format(date)

                // Scores
                player1ScoreText.text = match.score.player1Score.toString()
                player2ScoreText.text = match.score.player2Score.toString()

                // Match status
                val statusColor = when (match.status) {
                    Match.Status.LIVE, MatchStatus.LIVE -> R.color.live_color
                    Match.Status.SCHEDULED, MatchStatus.SCHEDULED -> R.color.scheduled_color
                    Match.Status.COMPLETED, MatchStatus.COMPLETED -> R.color.completed_color
                    Match.Status.CANCELLED, MatchStatus.CANCELLED -> R.color.cancelled_color
                    Match.Status.POSTPONED, MatchStatus.POSTPONED -> R.color.postponed_color
                }
                statusText.text = match.status.name
                statusText.setTextColor(root.context.getColor(statusColor))

                // Winner Highlight (for completed matches)
                if (match.status == Match.Status.COMPLETED && match.winnerId != null) {
                    if (match.winnerId == match.player1Id) {
                        player1NameText.setTextColor(root.context.getColor(R.color.winner_color))
                        player2NameText.setTextColor(root.context.getColor(R.color.default_text_color))
                    } else if (match.winnerId == match.player2Id) {
                        player2NameText.setTextColor(root.context.getColor(R.color.winner_color))
                        player1NameText.setTextColor(root.context.getColor(R.color.default_text_color))
                    }
                } else {
                    player1NameText.setTextColor(root.context.getColor(R.color.default_text_color))
                    player2NameText.setTextColor(root.context.getColor(R.color.default_text_color))
                }

                // Live indicator
                liveIndicator.visibility =
                    if (match.status == Match.Status.LIVE || match.status == MatchStatus.LIVE)
                        View.VISIBLE else View.GONE

                // Admin controls
                val showAdminControls = currentUser?.isAdmin == true &&
                        match.status == Match.Status.LIVE &&
                        onScoreUpdate != null

                adminControlsGroup.visibility =
                    if (showAdminControls) View.VISIBLE else View.GONE

                if (showAdminControls) {
                    player1ScoreIncrement.setOnClickListener {
                        val newScore = match.score.copy(player1Score = match.score.player1Score + 1)
                        onScoreUpdate?.invoke(match, newScore)
                    }
                    player1ScoreDecrement.setOnClickListener {
                        val newScore = match.score.copy(player1Score = maxOf(0, match.score.player1Score - 1))
                        onScoreUpdate?.invoke(match, newScore)
                    }
                    player2ScoreIncrement.setOnClickListener {
                        val newScore = match.score.copy(player2Score = match.score.player2Score + 1)
                        onScoreUpdate?.invoke(match, newScore)
                    }
                    player2ScoreDecrement.setOnClickListener {
                        val newScore = match.score.copy(player2Score = maxOf(0, match.score.player2Score - 1))
                        onScoreUpdate?.invoke(match, newScore)
                    }
                }

                // Card background color
                val cardBackgroundRes = when (match.status) {
                    Match.Status.LIVE -> R.color.live_match_background
                    Match.Status.SCHEDULED -> R.color.scheduled_match_background
                    Match.Status.COMPLETED -> R.color.completed_match_background
                    Match.Status.CANCELLED -> R.color.cancelled_match_background
                    else -> R.color.primary
                }
                matchCard.setCardBackgroundColor(root.context.getColor(cardBackgroundRes))

                // Click listener
                root.setOnClickListener { onMatchClick(match) }
            }
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
