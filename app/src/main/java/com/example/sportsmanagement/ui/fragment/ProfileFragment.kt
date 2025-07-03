package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsmanagement.R
import com.example.sportsmanagement.databinding.FragmentProfileBinding
import com.example.sportsmanagement.ui.adapter.SportCategoryAdapter
import com.example.sportsmanagement.ui.adapter.MatchAdapter
import com.example.sportsmanagement.ui.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var registeredCategoriesAdapter: SportCategoryAdapter
    private lateinit var matchHistoryAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()
        
        viewModel.loadUserProfile()
    }

    private fun setupRecyclerViews() {
        // Registered Categories RecyclerView
        registeredCategoriesAdapter = SportCategoryAdapter(
            onCategoryClick = { category ->
                // Handle category click - maybe show details or unregister option
            },
            onRegisterClick = { category ->
                viewModel.toggleRegistration(category.id)
            }
        )
        
        binding.registeredCategoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = registeredCategoriesAdapter
        }

        // Match History RecyclerView
        matchHistoryAdapter = MatchAdapter(
            onMatchClick = { match ->
                // Navigate to match details if needed
            },
            onScoreUpdate = null // No score updates in profile view
        )
        
        binding.matchHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchHistoryAdapter
        }
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            // TODO: Navigate to edit profile or show edit dialog
            Snackbar.make(binding.root, "Edit profile feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
        
        binding.settingsButton.setOnClickListener {
            // TODO: Navigate to settings
            Snackbar.make(binding.root, "Settings feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
        
        binding.adminPanelButton.setOnClickListener {
            // TODO: Navigate to admin panel
            Snackbar.make(binding.root, "Admin panel feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userNameText.text = user.name
                binding.userEmailText.text = user.email
                binding.userPhoneText.text = user.phone
                
                // Show/hide admin panel button based on user role
                binding.adminPanelButton.visibility = if (user.isAdmin) View.VISIBLE else View.GONE
                
                // Load profile image if available
                // TODO: Implement image loading with Glide
                // Glide.with(this).load(user.profileImageUrl).into(binding.profileImageView)
            }
        }

        viewModel.registeredCategories.observe(viewLifecycleOwner) { categories ->
            registeredCategoriesAdapter.submitList(categories)
            binding.registeredCategoriesCard.visibility = if (categories.isEmpty()) View.GONE else View.VISIBLE
            binding.noRegistrationsText.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.matchHistory.observe(viewLifecycleOwner) { matches ->
            matchHistoryAdapter.submitList(matches)
            binding.matchHistoryCard.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
            binding.noMatchHistoryText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.userStats.observe(viewLifecycleOwner) { stats ->
            binding.totalMatchesText.text = stats.totalMatches.toString()
            binding.wonMatchesText.text = stats.wonMatches.toString()
            binding.winRateText.text = "${stats.winPercentage}%"
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}