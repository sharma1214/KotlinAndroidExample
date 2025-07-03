package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.databinding.FragmentMatchDetailBinding
import com.example.sportsmanagement.ui.viewmodel.MatchViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class MatchDetailFragment : Fragment() {

    private var _binding: FragmentMatchDetailBinding? = null
    private val binding get() = _binding!!

    private val args: MatchDetailFragmentArgs by navArgs()
    private val viewModel: MatchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        observeViewModel()
        
        viewModel.loadMatchDetails(args.matchId)
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.player1ScoreIncrement.setOnClickListener {
            incrementScore(true)
        }
        
        binding.player1ScoreDecrement.setOnClickListener {
            decrementScore(true)
        }
        
        binding.player2ScoreIncrement.setOnClickListener {
            incrementScore(false)
        }
        
        binding.player2ScoreDecrement.setOnClickListener {
            decrementScore(false)
        }
        
        binding.startMatchButton.setOnClickListener {
            viewModel.startMatch(args.matchId)
        }
        
        binding.endMatchButton.setOnClickListener {
            viewModel.endMatch(args.matchId)
        }
    }

    private fun incrementScore(isPlayer1: Boolean) {
        viewModel.selectedMatch.value?.let { match ->
            val currentScore = match.score
            val newScore = if (isPlayer1) {
                currentScore.copy(player1Score = currentScore.player1Score + 1)
            } else {
                currentScore.copy(player2Score = currentScore.player2Score + 1)
            }
            viewModel.updateMatchScore(match.id, newScore)
        }
    }

    private fun decrementScore(isPlayer1: Boolean) {
        viewModel.selectedMatch.value?.let { match ->
            val currentScore = match.score
            val newScore = if (isPlayer1) {
                currentScore.copy(player1Score = maxOf(0, currentScore.player1Score - 1))
            } else {
                currentScore.copy(player2Score = maxOf(0, currentScore.player2Score - 1))
            }
            viewModel.updateMatchScore(match.id, newScore)
        }
    }

    private fun observeViewModel() {
        viewModel.selectedMatch.observe(viewLifecycleOwner) { match ->
            if (match != null) {
                updateMatchUI(match)
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            // Show/hide admin controls based on user role
            val isAdmin = user?.isAdmin == true
            binding.adminControlsCard.visibility = if (isAdmin) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.scoreUpdateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                viewModel.clearScoreUpdateSuccess()
            }
        }
    }

    private fun updateMatchUI(match: Match) {
        // Match basic info
        binding.player1NameText.text = match.player1Name ?: "Player 1"
        binding.player2NameText.text = match.player2Name ?: "Player 2"
        binding.venueText.text = match.venue
        
        // Score
        binding.player1ScoreText.text = match.score.player1Score.toString()
        binding.player2ScoreText.text = match.score.player2Score.toString()
        
        // Match time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        binding.matchTimeText.text = dateFormat.format(Date(match.scheduledTime))
        
        // Match status
        binding.matchStatusText.text = match.status.name
        binding.matchStatusText.setTextColor(
            when (match.status) {
                Match.Status.LIVE -> resources.getColor(R.color.live_match_color, null)
                Match.Status.COMPLETED -> resources.getColor(R.color.completed_match_color, null)
                Match.Status.SCHEDULED -> resources.getColor(R.color.scheduled_match_color, null)
                Match.Status.CANCELLED -> resources.getColor(R.color.cancelled_match_color, null)
            }
        )
        
        // Winner (if match is completed)
        if (match.status == Match.Status.COMPLETED && match.winnerId != null) {
            val winnerName = if (match.winnerId == match.player1Id) {
                match.player1Name ?: "Player 1"
            } else {
                match.player2Name ?: "Player 2"
            }
            binding.winnerText.text = "Winner: $winnerName"
            binding.winnerText.visibility = View.VISIBLE
        } else {
            binding.winnerText.visibility = View.GONE
        }
        
        // Live indicator
        binding.liveIndicator.visibility = if (match.status == Match.Status.LIVE) View.VISIBLE else View.GONE
        
        // Control buttons visibility based on match status
        val canUpdateScore = match.status == Match.Status.LIVE
        binding.scoreControlsGroup.visibility = if (canUpdateScore) View.VISIBLE else View.GONE
        
        binding.startMatchButton.visibility = if (match.status == Match.Status.SCHEDULED) View.VISIBLE else View.GONE
        binding.endMatchButton.visibility = if (match.status == Match.Status.LIVE) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}