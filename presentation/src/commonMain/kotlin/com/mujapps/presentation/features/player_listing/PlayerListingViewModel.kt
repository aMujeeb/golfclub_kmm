package com.mujapps.presentation.features.player_listing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mujapps.domain.use_cases.GetAllPlayersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mujapps.domain.models.GolfPlayer
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class PlayerListingViewModel(val mGetAllPlayersUseCase: GetAllPlayersUseCase) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val mPlayersPagingFlow: Flow<PagingData<GolfPlayer>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            mGetAllPlayersUseCase.executePaging(query.takeIf { it.isNotBlank() })
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}