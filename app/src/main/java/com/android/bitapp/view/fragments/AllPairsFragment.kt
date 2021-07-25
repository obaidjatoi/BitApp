package com.android.bitapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.android.bitapp.R
import com.android.bitapp.adapters.AllPairsAdapter
import com.android.bitapp.databinding.AllPairsFragmentBinding
import com.android.bitapp.models.AllPairItem
import com.android.bitapp.utils.ScreenState
import com.android.bitapp.utils.SpacesItemDecoration
import com.android.bitapp.utils.interfaces.ClickListenerRv
import com.android.bitapp.viewmodels.SharedViewModel

class AllPairsFragment : Fragment(), ClickListenerRv {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: AllPairsFragmentBinding
    private lateinit var adapter: AllPairsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.getTradingPairs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.screenState.observe(viewLifecycleOwner, { state ->
            when (state) {
                ScreenState.DATA_LOADED -> {
                    displayData()
                    binding.dataListRv.visibility = VISIBLE
                    binding.loadingGroup.visibility = GONE
                }
                ScreenState.DATA_LOADING -> {
                    binding.dataListRv.visibility = GONE
                    binding.loadingGroup.visibility = VISIBLE
                }
                ScreenState.ERROR -> {
                    binding.dataListRv.visibility = GONE
                    binding.loadingGroup.visibility = GONE
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.general_error_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
                ScreenState.NETWORK_ERROR -> {
                    binding.dataListRv.visibility = GONE
                    binding.loadingGroup.visibility = GONE
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.internet_problem),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                }
            }
        })
    }

    private fun displayData() {
        val spanCount = 2
        val spacing = 5
        val includeEdge = true
        binding.dataListRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.dataListRv.addItemDecoration(SpacesItemDecoration(spanCount, spacing, includeEdge))
        adapter = AllPairsAdapter(sharedViewModel.data, false, this)
        binding.dataListRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllPairsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onItemClick(item: Any?) {
        item?.let {
            sharedViewModel.subscribeFlow(item as AllPairItem)
            Navigation.findNavController(requireView())
                .navigate(AllPairsFragmentDirections.actionAllPairsFragmentToPairDetails())
        }
    }
}