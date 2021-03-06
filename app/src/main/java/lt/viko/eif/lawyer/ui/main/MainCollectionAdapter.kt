package lt.viko.eif.lawyer.ui.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import lt.viko.eif.lawyer.ui.main.askedquestions.AskedQuestionsFragment
import lt.viko.eif.lawyer.ui.main.reservation.ReservationsFragment

class MainCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AskedQuestionsFragment()
            1 -> ReservationsFragment()

            else -> AskedQuestionsFragment()
        }
    }
}
