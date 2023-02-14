package com.example.weather_api.app.screens.main.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.weather_api.R
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.core_data.models.Location
import com.example.weather_api.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(R.layout.fragment_favorite) {

    override val viewModel by viewModels<FavoritesViewModel>()

    private val binding by viewBinding(FragmentFavoriteBinding::bind)

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        FavoriteAdapter(object : FavoritesClickListener {
            override fun deleteFromFavorites(citiName: String) {
                viewModel.deleteFromFavorites(citiName)
            }

            override fun showDetailsWeather(location: Location) {
                viewModel.setCurrentLocation(location)
                findNavController().navigate(R.id.action_favoritesFragment_to_weather_graph)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        observeFavoriteState()
    }

    private fun observeFavoriteState() {
        viewModel.favoritesState.observe(viewLifecycleOwner) {
            adapter.favoritesList = it
        }
    }
}