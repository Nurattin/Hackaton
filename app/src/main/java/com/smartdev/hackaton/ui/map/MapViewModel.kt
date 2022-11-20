package com.smartdev.hackaton.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.smartdev.hackaton.data.model.TourDetail
import com.smartdev.hackaton.data.network_layer.Api
import com.smartdev.hackaton.data.network_layer.Result
import com.smartdev.hackaton.data.network_layer.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val api: Api
) : ViewModel() {

    private val _places = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val places = _places.asStateFlow()


    val chips = MutableStateFlow<List<Chip>>(emptyList())


    init {
        getCategory()
    }

    fun getCategory() {
        viewModelScope.launch {
            flow { emit(api.getCategoryForPlace()) }.asResult().collect { result ->
                when (result) {
                    is Result.Error -> {}
                    Result.Loading -> {}
                    is Result.Success -> {
                        chips.value = result.data.data.mapIndexed { index, data ->
                            if (index == 1) Chip(isSelected = true, name = data.name)
                            else Chip(isSelected = false, name = data.name)
                        }
                    }
                }
            }
        }
    }

    fun getDetailTour(id: Int) {
        viewModelScope.launch {
            flow { emit(api.getDetailTour(id)) }
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Error -> {
                            _places.value = MapUiState.Error(
                                message = result.exception.toString()
                            )
                        }
                        is Result.Success -> {
                            _places.value = MapUiState.Success(
                                places = result.data
                            )
                        }
                        Result.Loading -> {
                            _places.value = MapUiState.Loading
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

//    fun changeCategorySelected(category: Chip) {
//        _mainUiState.update { currentState ->
//            currentState.copy(
//                chipList = currentState.chipList.map {
//                    if (it == category) it.copy(isSelected = true) else if (it.isSelected) it.copy(
//                        isSelected = false
//                    ) else it
//                }
//            )
//        }
//    }
}

sealed interface MapUiState {
    data class Success(
//        val category: List<com.smartdev.hackaton.ui.map.Chip> =
        val places: TourDetail? = null
    ) : MapUiState

    object Loading : MapUiState
    data class Error(val message: String) : MapUiState
}

data class Chip(
    val isSelected: Boolean = false,
    val name: String
)

val listCategory = listOf(
    com.smartdev.hackaton.ui.map.Chip(isSelected = false, name = "Спорт"),
    com.smartdev.hackaton.ui.map.Chip(isSelected = true, name = "Популярное"),
    com.smartdev.hackaton.ui.map.Chip(isSelected = false, name = "Еда"),
    com.smartdev.hackaton.ui.map.Chip(isSelected = false, name = "Экстрим"),
    com.smartdev.hackaton.ui.map.Chip(isSelected = false, name = "Горный"),
    com.smartdev.hackaton.ui.map.Chip(isSelected = false, name = "Отдых"),
)