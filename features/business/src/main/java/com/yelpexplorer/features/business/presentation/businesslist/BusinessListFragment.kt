package com.yelpexplorer.features.business.presentation.businesslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.yelpexplorer.features.business.R
import com.yelpexplorer.features.business.databinding.FragmentBusinessListBinding
import com.yelpexplorer.libraries.core.data.local.Const
import com.yelpexplorer.libraries.core.utils.EventObserver
import com.yelpexplorer.libraries.core.utils.Router
import org.koin.androidx.viewmodel.ext.android.viewModel

class BusinessListFragment : Fragment() {

    private var _binding: FragmentBusinessListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BusinessListViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBusinessListBinding.inflate(inflater)

        setHasOptionsMenu(true)

        viewModel.viewAction.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is BusinessListViewModel.ViewAction.NavigateToDetails -> navigateToDetails(it.businessId)
            }
        })

        viewModel.viewState.observe(owner = viewLifecycleOwner) {
            render(it)
        }

        binding.rvBusinessList.adapter = BusinessListAdapter {
            viewModel.onBusinessClicked(it.id)
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                navigateToSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun render(viewState: BusinessListViewModel.ViewState) {
        showLoading(viewState.showLoading)
        showError(viewState.errorStringId)
        showBusinessList(viewState.businessListUiModel)
    }

    private fun showLoading(showLoading: Boolean) {
        // TOOD
    }

    private fun showBusinessList(businessListUiModel: BusinessListUiModel?) {
        (binding.rvBusinessList.adapter as BusinessListAdapter).setData(businessListUiModel?.businessList.orEmpty())
    }

    private fun navigateToDetails(businessId: String) {
        // https://issuetracker.google.com/issues/143280818
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.businessListFragment) {
            navController.navigate(
                R.id.action_businessListFragment_to_businessDetailsFragment,
                bundleOf(Const.KEY_BUSINESS_ID to businessId)
            )
        }
    }

    private fun navigateToSettings() {
        startActivity(Router.getSettingsIntent(requireContext()))
    }

    private fun showError(@StringRes errorStringId: Int?) {
        // TODO This should be a 1 time event OR a dedicated error banner that is persistent
        errorStringId?.let {
            showToast(getString(it))
        }
    }

    private fun showToast(message: String, length: Int = Toast.LENGTH_LONG) {
        Toast.makeText(requireContext(), message, length).show()
    }
}
