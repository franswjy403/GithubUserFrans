package com.example.githubuser.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionsPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragments = Follow()
        fragments.arguments = Bundle().apply {
            putInt(Follow.ARG_SECTION_NUMBER, position + 1)
        }
        return fragments
    }

}