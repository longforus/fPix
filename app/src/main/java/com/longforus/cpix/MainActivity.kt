package com.longforus.cpix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.longforus.cpix.databinding.MainActivityBinding
import com.longforus.cpix.fragment.FavoriteFragment
import com.longforus.cpix.fragment.ImageFragment
import com.longforus.cpix.fragment.SettingFragment
import com.longforus.cpix.fragment.VideoFragment
import com.longforus.cpix.util.StatusBarUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(this)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragments = listOf(ImageFragment(),VideoFragment(),FavoriteFragment(),SettingFragment())
        val tabTitles  = listOf("Image","Video","Favorite","Setting")
        val tabIcons = listOf(android.R.drawable.ic_menu_gallery,android.R.drawable.ic_menu_slideshow,android.R.drawable.btn_star,android.R.drawable.ic_menu_preferences)
        binding.vp.adapter = object :FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
               return fragments[position]
            }

        }
        binding.vp.offscreenPageLimit = 1
        binding.vp.isUserInputEnabled = false
        TabLayoutMediator(
            binding.tl,
            binding.vp,
            true
        ) { tab, position ->
            tab.setText(tabTitles[position]).setIcon(tabIcons[position])
        }.attach()
    }
}

