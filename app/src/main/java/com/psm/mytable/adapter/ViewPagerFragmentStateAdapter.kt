package com.psm.mytable.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.psm.mytable.ui.ingredients.cold.ColdStorageFragment
import com.psm.mytable.ui.ingredients.frozen.FrozenStorageFragment
import com.psm.mytable.ui.ingredients.roomTemperature.RoomTemperatureStorageFragment

/**
 * 재료관리 (냉장, 냉동, 실온)
 * TabLayout + ViewPager2
 * */
class ViewPagerFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    // ViewPager에서 이동할 Fragment List(냉장, 냉동, 실온)
    private var fragments: ArrayList<Fragment> = arrayListOf(
        ColdStorageFragment(),
        FrozenStorageFragment(),
        RoomTemperatureStorageFragment()
    )

    /**
     * FragmentStateAdapter 상속 시 무조건 override 해야 하는 Function
     * ViewPager에 담길 Fragments Count Return
     * */
    override fun getItemCount(): Int {
        return fragments.size
    }


    /**
     * FragmentStateAdapter 상속 시 무조건 override 해야 하는 Function
     * View Pager의 Position에 해당하는 Fragment Return
     * */
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}