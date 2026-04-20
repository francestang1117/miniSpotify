package com.frances.spotify.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frances.spotify.datamodel.Section
import com.frances.spotify.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

    private val repository: HomeRepository
): ViewModel() {
    // Model /state exposed to homefragmnet
    // init uiState
    //        stateflow
    // UI <--------------- value publisher
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState(feed = listOf(), isLoading = true))
    // Data state has no relationship with compose, state flow is only a data flow, like:
    // once the HomeUiState changes, it will trigger the state
    // 用stateflow的state去trigger compse 的state
    // compose is used for UI
    // Could use but not recommend:
    // private val _feed: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState(feed = listOf(), isLoading = false))
    // val feed: State<HomeUiState> = _feed
    // when testing, compose 需要用到真实的数据和环境，因为android还有ui testing, 是真正模拟用户操作
    // 所以我们这里只有一个data flow， 只需要测试逻辑，不需要真实页面变化
    val uiState: StateFlow<HomeUiState> = _uiState

    // Event from homefragment
    fun fetchHomeScreen() {

        viewModelScope.launch {
            val sections: List<Section> = repository.getHomeSections()
            // Get the sections data and update state
            _uiState.value = HomeUiState(feed = sections, isLoading = false)

            Log.d("HomeViewModel", _uiState.value.toString())
        }
    }
}

data class HomeUiState(
    val feed: List<Section>,
    val isLoading: Boolean
)