package com.smartdev.hackaton.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartdev.hackaton.data.model.Tours
import com.smartdev.hackaton.data.network_layer.Api
import com.smartdev.hackaton.data.network_layer.Result
import com.smartdev.hackaton.data.network_layer.asResult
import com.smartdev.hackaton.ui.map.Chip
import com.smartdev.hackaton.ui.map.listCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: Api
) : ViewModel() {


    private val _tours = MutableStateFlow<TourUiState>(TourUiState.Loading)
    val tours = _tours.asStateFlow()


    val chips = MutableStateFlow(listCategory)


    init {
        getTours()
    }

    fun getTours() {
        viewModelScope.launch {
            flow { emit(api.getAllTours()) }
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Error -> {
                            _tours.value = TourUiState.Error(
                                message = result.exception.toString()
                            )
                        }
                        is Result.Success -> {
                            _tours.value = TourUiState.Success(
                                tours = result.data
                            )
                        }
                        Result.Loading -> {
                            _tours.value = TourUiState.Loading
                        }
                    }
                }
        }
    }

    fun changeCategorySelected(category: Chip) {
        chips.value = chips.value.map {
            if (it == category) it.copy(isSelected = true) else if (it.isSelected) it.copy(
                isSelected = false
            ) else it
        }
    }

}

sealed interface TourUiState {
    data class Success(
//        val category: List<com.smartdev.hackaton.ui.map.Chip> =
        val tours: Tours? = null,
    ) : TourUiState

    object Loading : TourUiState
    data class Error(val message: String) : TourUiState
}