package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsmanagement.R
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.databinding.FragmentMatchesBinding
import com.example.sportsmanagement.ui.adapter.MatchAdapter
import com.example.sportsmanagement.ui.viewmodel.MatchViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupTabs()
        observeViewModel()
        setupSwipeRefresh()
        
        viewModel.loadMatches()
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter(
            onMatchClick = { match ->
                findNavController().navigate(
                    MatchesFragmentDirections.actionMatchesToMatchDetail(match.id)
                )
            },
            onScoreUpdate = { match, newScore ->
                viewModel.updateMatchScore(match.id, newScore)
            }
        )
        
        binding.matchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.filterMatches(Match.Status.LIVE)
                    1 -> viewModel.filterMatches(Match.Status.SCHEDULED)
                    2 -> viewModel.filterMatches(Match.Status.COMPLETED)
                    3 -> viewModel.showAllMatches()
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.filteredMatches.observe(viewLifecycleOwner) { matches ->
            matchAdapter.submitList(matches)
            binding.emptyStateText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            binding.matchesRecyclerView.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            matchAdapter.updateCurrentUser(user)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.scoreUpdateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Snackbar.make(binding.root, "Score updated successfully", Snackbar.LENGTH_SHORT).show()
                viewModel.clearScoreUpdateSuccess()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshMatches()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}