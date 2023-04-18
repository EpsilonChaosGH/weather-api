package com.example.weather_api.app.screens.main.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weather_api.R
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(R.layout.fragment_favorite) {

    override val viewModel by viewModels<FavoritesViewModel>()

    private val binding by viewBinding(FragmentFavoriteBinding::bind)

    private val adapter = FavoriteAdapter(object : FavoritesClickListener {
        override fun deleteFromFavorites(city: String) {
            viewModel.deleteFromFavorites(city)
        }

        override fun showDetailsWeather(city: String) {
            viewModel.setCurrentWeather(city)
            findNavController().navigate(R.id.action_favoritesFragment_to_weather_graph)
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            refreshLayout.setColorSchemeResources(R.color.main_text_color)
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.main_color)
            refreshLayout.setOnRefreshListener {
                viewModel.refreshFavorites()
            }
        }
        observeFavoritesState()
    }

    private fun observeFavoritesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoritesState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .distinctUntilChanged()
                .collect { favoritesState ->
                    favoritesState?.let {
                        adapter.favoritesList = favoritesState.favorites
                        binding.recyclerView.visibility =
                            if (favoritesState.emptyListState) View.GONE else View.VISIBLE
                        binding.refreshLayout.isRefreshing = favoritesState.refreshState
                    }
                }
        }
    }
}