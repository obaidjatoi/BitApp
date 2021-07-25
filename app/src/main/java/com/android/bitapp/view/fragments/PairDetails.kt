package com.android.bitapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.bitapp.R
import com.android.bitapp.adapters.AllPairsAdapter
import com.android.bitapp.adapters.TradesAdapter
import com.android.bitapp.databinding.PairDetailFragmentBinding
import com.android.bitapp.models.AllPairItem
import com.android.bitapp.utils.REFRESH_KEY
import com.android.bitapp.utils.SpacesItemDecoration
import com.android.bitapp.utils.interfaces.ClickListenerRv
import com.android.bitapp.viewmodels.SharedViewModel


class PairDetails : Fragment(), ClickListenerRv {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    lateinit var binding: PairDetailFragmentBinding
    private lateinit var switcherAdapter: AllPairsAdapter
    private lateinit var tradesAdapter: TradesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PairDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!sharedViewModel.data.isNullOrEmpty()) {
            prepareSwitcherRecycler()
        }

        setNameAndGeneralInfoColumn()
        setupTradesSection()
        setClickListeners()
        handleBack()

    }

    private fun setupTradesSection() {
        setTradesAdapter()
        sharedViewModel.currentSubscribeTradeData.observe(viewLifecycleOwner, { newTrade ->
            if (newTrade.id == REFRESH_KEY) {
                // when we are doing on screen switching we need to refresh the recycler
                tradesAdapter.notifyDataSetChanged()
                binding.loadingGroup.visibility = VISIBLE
                binding.tradesGroup.visibility = GONE
                return@observe
            }

            checkVisibilityOfTradesRecyclerView()

            if (sharedViewModel.tradesList.size > 30) {
                sharedViewModel.tradesList.removeFirst()
                tradesAdapter.notifyItemRemoved(0)
            }

            sharedViewModel.tradesList.add(newTrade)
            tradesAdapter.notifyItemInserted(sharedViewModel.tradesList.size - 1)
            binding.tradesRv.scrollToPosition(sharedViewModel.tradesList.size - 1)
        })
    }

    private fun checkVisibilityOfTradesRecyclerView() {
        if (binding.loadingGroup.visibility == VISIBLE && sharedViewModel.tradesList.size > 0) {
            binding.loadingGroup.visibility = GONE
        } else if (binding.tradesGroup.visibility == GONE && sharedViewModel.tradesList.size > 0) {
            binding.tradesGroup.visibility = VISIBLE
        }
    }

    private fun setClickListeners() {
        binding.switchBtn.setOnClickListener {
            binding.selectorRv.visibility = View.VISIBLE
        }

        binding.backButton.setOnClickListener {
            navigateUp()
        }
        binding.backTv.setOnClickListener {
            navigateUp()
        }
    }

    private fun navigateUp() {
        findNavController().navigateUp()
    }

    private fun prepareSwitcherRecycler() {
        val spanCount = 2 // 3 columns
        val spacing = 50 // 50px
        val includeEdge = true
        binding.selectorRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.selectorRv.addItemDecoration(SpacesItemDecoration(spanCount, spacing, includeEdge))
        switcherAdapter = AllPairsAdapter(sharedViewModel.data, true, this)
        binding.selectorRv.adapter = switcherAdapter
    }

    private fun setTradesAdapter() {
        tradesAdapter = TradesAdapter(sharedViewModel.tradesList)
        binding.tradesRv.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = tradesAdapter
        }
    }

    override fun onItemClick(item: Any?) {
        sharedViewModel.subscribeFlow(item as AllPairItem)
        binding.selectorRv.visibility = GONE
    }

    private fun setNameAndGeneralInfoColumn() {
        setTitles()
        sharedViewModel.currentSubscribePairItem.observe(viewLifecycleOwner, { pair ->
            binding.name.text = pair.name
        })

        sharedViewModel.currentSubscribeTickerData.observe(viewLifecycleOwner, { pair ->
            binding.price.text = resources.getString(R.string.price_with_colon).plus(pair.lastPrice)
            binding.row1.columnOne.value.text = pair.lastPrice
            binding.row1.columnTwo.value.text = pair.dailyChange
            binding.row2.columnOne.value.text = pair.bid
            binding.row2.columnTwo.value.text = pair.ask
            binding.row3.columnOne.value.text = pair.lastPrice
            binding.row3.columnTwo.value.text =
                pair.high.plus("~").plus(pair.low)
        })
    }


    private fun setTitles() {
        binding.row1.columnOne.title.text =
            requireContext().resources.getString(R.string.last_trade_title)
        binding.row1.columnTwo.title.text =
            requireContext().resources.getString(R.string.daily_change_title)
        binding.row2.columnOne.title.text =
            requireContext().resources.getString(R.string.top_bid_title)
        binding.row2.columnTwo.title.text =
            requireContext().resources.getString(R.string.lowest_ask_title)
        binding.row3.columnOne.title.text =
            requireContext().resources.getString(R.string.last_price_title)
        binding.row3.columnTwo.title.text =
            requireContext().resources.getString(R.string.daily_range_title)
        binding.tradeItemsHeading.amount.text =
            requireContext().resources.getString(R.string.amount)
        binding.tradeItemsHeading.price.text =
            requireContext().resources.getString(R.string.price)
        binding.tradeItemsHeading.time.text =
            requireContext().resources.getString(R.string.time)
    }

    override fun onDestroy() {
        sharedViewModel.unsubscribeFromALL()
        super.onDestroy()
    }

    private fun handleBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.selectorRv.visibility == VISIBLE) {
                        binding.selectorRv.visibility = GONE
                    } else {
                        navigateUp()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}