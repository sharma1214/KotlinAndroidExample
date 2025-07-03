package com.example.sportsmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sportsmanagement.R
import com.example.sportsmanagement.databinding.FragmentCategoriesBinding
import com.example.sportsmanagement.ui.adapter.SportCategoryAdapter
import com.example.sportsmanagement.ui.viewmodel.CategoryViewModel
import com.google.android.material.snackbar.Snackbar

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: SportCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
        
        viewModel.loadCategories()
    }

    private fun setupRecyclerView() {
        categoryAdapter = SportCategoryAdapter(
            onCategoryClick = { category ->
                findNavController().navigate(
                    CategoriesFragmentDirections.actionCategoriesToCategoryDetail(category.id)
                )
            },
            onRegisterClick = { category ->
                viewModel.toggleRegistration(category.id)
            }
        )
        
        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = categoryAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
            binding.emptyStateText.visibility = if (categories.isEmpty()) View.VISIBLE else View.GONE
            binding.categoriesRecyclerView.visibility = if (categories.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            categoryAdapter.updateCurrentUser(user)
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

        viewModel.registrationSuccess.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                viewModel.clearRegistrationSuccess()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshCategories()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}