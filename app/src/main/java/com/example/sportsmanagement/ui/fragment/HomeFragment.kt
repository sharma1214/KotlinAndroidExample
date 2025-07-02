package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sportsmanagement.R
import com.example.sportsmanagement.databinding.FragmentHomeBinding
import com.example.sportsmanagement.ui.adapter.LiveMatchAdapter
import com.example.sportsmanagement.ui.adapter.SportCategoryAdapter
import com.example.sportsmanagement.ui.adapter.MatchAdapter
import com.example.sportsmanagement.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var liveMatchAdapter: LiveMatchAdapter
    private lateinit var featuredSportsAdapter: SportCategoryAdapter
    private lateinit var upcomingMatchesAdapter: MatchAdapter
    private lateinit var recentResultsAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        observeViewModel()
        setupSwipeRefresh()
    }

    private fun setupRecyclerViews() {
        // Live Matches RecyclerView
        liveMatchAdapter = LiveMatchAdapter { match ->
            // Navigate to match detail
            // findNavController().navigate(
            //     HomeFragmentDirections.actionHomeToMatchDetail(match.id)
            // )
        }
        binding.liveMatchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = liveMatchAdapter
        }

        // Featured Sports RecyclerView
        featuredSportsAdapter = SportCategoryAdapter { category ->
            // Navigate to category detail
            // findNavController().navigate(
            //     HomeFragmentDirections.actionHomeToCategoryDetail(category.id)
            // )
        }
        binding.featuredSportsRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = featuredSportsAdapter
        }

        // Upcoming Matches RecyclerView
        upcomingMatchesAdapter = MatchAdapter { match ->
            // Navigate to match detail
            // findNavController().navigate(
            //     HomeFragmentDirections.actionHomeToMatchDetail(match.id)
            // )
        }
        binding.upcomingMatchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = upcomingMatchesAdapter
        }

        // Recent Results RecyclerView
        recentResultsAdapter = MatchAdapter { match ->
            // Navigate to match detail
            // findNavController().navigate(
            //     HomeFragmentDirections.actionHomeToMatchDetail(match.id)
            // )
        }
        binding.recentResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentResultsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userNameText.text = getString(R.string.welcome_message) + ", ${user.name}!"
                binding.userNameText.visibility = View.VISIBLE
            } else {
                binding.userNameText.visibility = View.GONE
            }
        }

        viewModel.liveMatches.observe(viewLifecycleOwner) { matches ->
            liveMatchAdapter.submitList(matches)
            binding.noLiveMatchesText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            binding.liveMatchesRecyclerView.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.featuredCategories.observe(viewLifecycleOwner) { categories ->
            featuredSportsAdapter.submitList(categories)
        }

        viewModel.upcomingMatches.observe(viewLifecycleOwner) { matches ->
            upcomingMatchesAdapter.submitList(matches)
            binding.noUpcomingMatchesText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            binding.upcomingMatchesRecyclerView.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.recentResults.observe(viewLifecycleOwner) { matches ->
            recentResultsAdapter.submitList(matches)
            binding.noRecentResultsText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            binding.recentResultsRecyclerView.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                // Show error message (you can implement a Snackbar or Toast here)
                // Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}