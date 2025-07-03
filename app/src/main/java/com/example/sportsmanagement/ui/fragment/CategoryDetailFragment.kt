package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sportsmanagement.R
import com.example.sportsmanagement.databinding.FragmentCategoryDetailBinding
import com.example.sportsmanagement.ui.adapter.MatchAdapter
import com.example.sportsmanagement.ui.viewmodel.CategoryViewModel
import com.google.android.material.snackbar.Snackbar

class CategoryDetailFragment : Fragment() {

    private var _binding: FragmentCategoryDetailBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryDetailFragmentArgs by navArgs()
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var participantsAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        viewModel.loadCategoryDetails(args.categoryId)
    }

    private fun setupRecyclerView() {
        participantsAdapter = MatchAdapter(
            onMatchClick = { match ->
                // Navigate to match detail
                findNavController().navigate(
                    CategoryDetailFragmentDirections.actionCategoryDetailToMatchDetail(match.id)
                )
            },
            onScoreUpdate = null // No score updates in category detail
        )
        
        binding.categoryMatchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = participantsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            viewModel.toggleRegistration(args.categoryId)
        }
        
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            if (category != null) {
                binding.categoryNameText.text = category.name
                binding.categoryDescriptionText.text = category.description
                binding.participantCountText.text = "${category.participants.size}/${category.maxParticipants}"
                
                // Update registration button
                viewModel.currentUser.value?.let { user ->
                    val isRegistered = category.participants.contains(user.id)
                    binding.registerButton.text = if (isRegistered) "Unregister" else "Register"
                    binding.registerButton.isEnabled = isRegistered || category.participants.size < category.maxParticipants
                }
            }
        }

        viewModel.categoryMatches.observe(viewLifecycleOwner) { matches ->
            participantsAdapter.submitList(matches)
            binding.noMatchesText.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
            binding.categoryMatchesRecyclerView.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            // Update registration status when user data changes
            viewModel.selectedCategory.value?.let { category ->
                val isRegistered = category.participants.contains(user.id)
                binding.registerButton.text = if (isRegistered) "Unregister" else "Register"
                binding.registerButton.isEnabled = isRegistered || category.participants.size < category.maxParticipants
            }
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

        viewModel.registrationSuccess.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                viewModel.clearRegistrationSuccess()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}