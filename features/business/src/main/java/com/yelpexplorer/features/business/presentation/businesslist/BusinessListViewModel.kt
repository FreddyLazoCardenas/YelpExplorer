package com.yelpexplorer.features.business.presentation.businesslist

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.yelpexplorer.features.business.R
import com.yelpexplorer.features.business.domain.model.Business
import com.yelpexplorer.features.business.domain.usecase.GetBusinessListUseCase
import com.yelpexplorer.libraries.core.utils.Event
import com.yelpexplorer.libraries.core.utils.Resource
import kotlinx.coroutines.Dispatchers

class BusinessListViewModel(
    private val getBusinessListUseCase: GetBusinessListUseCase
) : ViewModel() {

    sealed class ViewAction {
        data class NavigateToDetails(val businessId: String) : ViewAction()
    }

    data class ViewState(
        val showLoading: Boolean,
        val businessListUiModel: BusinessListUiModel?,
        @StringRes val errorStringId: Int? = null
    )

    private val _viewAction = MutableLiveData<Event<ViewAction>>()
    val viewAction: LiveData<Event<ViewAction>> = _viewAction

    private val _viewState = liveData<Resource<List<Business>>>(context = viewModelScope.coroutineContext + Dispatchers.Main) {
        emitSource(
            getBusinessListUseCase.execute(
                term = "sushi",
                location = "montreal",
                sortBy = "rating",
                limit = 20
            ).asLiveData()
        )
    }.map { resource ->
        when (resource) {
            is Resource.Loading -> {
                ViewState(
                    showLoading = true,
                    businessListUiModel = resource.data?.toBusinessListUiModel(),
                    errorStringId = null
                )
            }
            is Resource.Error -> {
                ViewState(
                    showLoading = false,
                    businessListUiModel = resource.data?.toBusinessListUiModel(),
                    errorStringId = R.string.error_something_went_wrong
                )
            }
            is Resource.Success -> {
                ViewState(
                    showLoading = false,
                    businessListUiModel = resource.data.toBusinessListUiModel(),
                    errorStringId = null
                )
            }
        }
    } as MutableLiveData<ViewState>
    val viewState: LiveData<ViewState> = _viewState

    fun onBusinessClicked(businessId: String) {
        _viewAction.value = Event(ViewAction.NavigateToDetails(businessId))
    }


//    data class UiState<T>(
//        val loading: Boolean = false,
//        val exception: Exception? = null,
//        val data: T? = null
//    ) {
//        val hasError: Boolean
//            get() = exception != null
//
//        val initialLoad: Boolean
//            get() = data == null && loading && !hasError
//    }
//
//    fun <T> UiState<T>.copyWithResult(value: Result<T>): UiState<T> {
//        return when (value) {
//            is Result.Success -> copy(loading = false, exception = null, data = value.data)
//            is Result.Error -> copy(loading = false, exception = value.exception)
//        }
//    }
//
//    ////////////////////////////////////
//    sealed class Result<out R> {
//        data class Success<out T>(val data: T) : Result<T>()
//        data class Error(val exception: Exception) : Result<Nothing>()
//    }
//
//    val Result<*>.succeeded
//        get() = this is Result.Success && data != null
//
//    fun <T> Result<T>.successOr(fallback: T): T {
//        return (this as? Result.Success<T>)?.data ?: fallback
//    }
}
