package com.example.weather_api.app.screens.main.weather

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weather_api.R
import com.example.weather_api.app.screens.base.BaseFragment
import com.example.weather_api.app.utils.observeEvent
import com.example.weather_api.databinding.FragmentSearchByCityBinding

class SearchByCityFragment : BaseFragment(R.layout.fragment_search_by_city) {


    override val viewModel by viewModels<SearchByCityViewModel>()
    private lateinit var binding: FragmentSearchByCityBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchByCityBinding.bind(view)

        showSoftKeyboard(binding.cityEditText)
        observeState()
        observeGoBackEvent()

        binding.searchButton.setOnClickListener {
            hideKeyboardFrom(binding.cityEditText)
            viewModel.searchButtonPressed(binding.cityEditText.text.toString())
        }

        binding.cancelButton.setOnClickListener {
            hideKeyboardFrom(binding.cityEditText)
            onCancelButtonPressed()
        }
    }

    private fun observeState() = viewModel.state.observe(viewLifecycleOwner) {
        binding.cityTextInput.error =
            if (it.emptyCityError) getString(R.string.field_is_empty) else null
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboardFrom(view: View?) {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun onCancelButtonPressed() {
        findNavController().popBackStack()
    }

    private fun observeGoBackEvent() = viewModel.goBackEvent.observeEvent(viewLifecycleOwner) {
        findNavController().popBackStack()
    }
}