package com.bookshelfhub.bookshelfhub.adapters.viewpager

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val totalNoOfFragments:Int=4) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragments = emptyList<Fragment>()

    override fun getItemCount(): Int {
        return totalNoOfFragments
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment:List<Fragment>){
       fragments =  fragments.plus(fragment)
    }

}