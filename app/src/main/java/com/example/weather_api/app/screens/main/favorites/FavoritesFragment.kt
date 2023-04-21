package com.example.weather_api.app.screens.main.favorites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weather_api.R
import com.example.weather_api.app.utils.collectEventFlow
import com.example.weather_api.app.utils.collectFlow
import com.example.weather_api.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorite) {

    private val viewModel by viewModels<FavoritesViewModel>()

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
        observeEvents()
        observeFavoritesState()
    }

    private fun observeEvents() {
        collectEventFlow(viewModel.showErrorMessageResEvent) { massage ->
            Toast.makeText(requireContext(), getString(massage), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeFavoritesState() {
        collectFlow(viewModel.favoritesState) { favoritesState ->
            adapter.favoritesList = favoritesState.favorites
            binding.recyclerView.visibility =
                if (favoritesState.emptyListState) View.GONE else View.VISIBLE
            binding.refreshLayout.isRefreshing = favoritesState.refreshState
        }
    }
}