package com.yelpexplorer.features.business.presentation.businesslist

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.yelpexplorer.features.business.R
import com.yelpexplorer.features.business.domain.usecase.GetBusinessListUseCase
import com.yelpexplorer.libraries.core.utils.Event
import com.yelpexplorer.libraries.core.utils.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class BusinessListViewModel @Inject constructor(
    private val getBusinessListUseCase: GetBusinessListUseCase
) : ViewModel() {

    sealed class ViewAction {
        data class NavigateToDetails(val businessId: String) : ViewAction()
    }

    sealed class ViewState {
        data class ShowLoading(val businessList: BusinessListUiModel?) : ViewState()
        data class ShowBusinessList(val businessList: BusinessListUiModel) : ViewState()
        data class ShowError(@StringRes val errorStringId: Int) : ViewState()
    }

    private val _viewAction = MutableLiveData<Event<ViewAction>>()
    val viewAction: LiveData<Event<ViewAction>>
        get() = _viewAction

    private val _viewState: MutableLiveData<ViewState>
    val viewState: LiveData<ViewState>
        get() = _viewState

    init {
        _viewState = liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
            emitSource(getBusinessListUseCase.execute(
                term = "sushi",
                location = "montreal",
                sortBy = "rating",
                limit = 20
            ))
        }.map { resource ->
            when (resource) {
                is Resource.Loading -> ViewState.ShowLoading(resource.data?.toBusinessListUiModel())
                is Resource.Error -> ViewState.ShowError(R.string.error_something_went_wrong)
                is Resource.Success -> ViewState.ShowBusinessList(resource.data.toBusinessListUiModel())
            }
        } as MutableLiveData<ViewState>
    }

    fun onBusinessClicked(businessId: String) {
        _viewAction.value = Event(ViewAction.NavigateToDetails(businessId))
    }
}