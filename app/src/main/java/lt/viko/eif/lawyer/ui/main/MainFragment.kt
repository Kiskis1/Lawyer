package lt.viko.eif.lawyer.ui.main

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.MyApplication
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentMainBinding
import lt.viko.eif.lawyer.repository.UsersRepository
import lt.viko.eif.lawyer.utils.Utils.observeOnce

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

        MainActivity.user.observeOnce(viewLifecycleOwner, {
            binding.viewPager.adapter = MainCollectionAdapter(this)
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> resources.getString(R.string.title_asked_questions)
                    1 -> resources.getString(R.string.title_reservations)
                    else -> ""
                }
            }.attach()
            activityProgressLayout.visibility = View.GONE
            // Lingver.getInstance().setLocale(requireContext(), Utils.convertToLocaleCode(it.country))

        })

        MyApplication.fcmToken.observe(viewLifecycleOwner) { token ->
            Firebase.auth.uid?.let {
                UsersRepository.updateToken(token, it)
            }
        }
    }
}
