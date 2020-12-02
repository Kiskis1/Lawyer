package com.acruxcs.lawyer.ui.main

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentMainBinding
import com.acruxcs.lawyer.utils.Utils.observeOnce
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private lateinit var activityProgressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressBar.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityProgressLayout.visibility = View.VISIBLE

        MainApplication.user.observeOnce(viewLifecycleOwner, {
            binding.viewPager.adapter = MainCollectionAdapter(this)
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> resources.getString(R.string.title_asked_questions)
                    1 -> resources.getString(R.string.title_reservations)
                    else -> ""
                }
            }.attach()
            activityProgressLayout.visibility = View.GONE

        })
    }
}
