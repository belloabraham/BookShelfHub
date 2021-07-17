package com.bookshelfhub.bookshelfhub.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ShelfStorePagerAdapter( fm: FragmentManager, private val fragments:List<Fragment>, private val fragmentTitles:Array<String>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence{
        return fragmentTitles[position]
    }

    override fun getCount(): Int {
        return fragmentTitles.size
    }

}