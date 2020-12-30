package lt.viko.eif.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.metadata
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.snackbar
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding
import lt.viko.eif.lawyer.ActivityViewModel
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentLawyersInfoBinding
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.ui.lawyers.LawyersViewModel
import lt.viko.eif.lawyer.utils.Status
import lt.viko.eif.lawyer.utils.Utils

class LawyersInfoFragment : Fragment(R.layout.fragment_lawyers_info) {
    private lateinit var lawyer: User
    private val lawyersCasesAdapter by lazy { LawyersCaseAdapter(this, null) }
    private val viewModel: LawyersViewModel by viewModels({ requireParentFragment() })
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLawyersInfoBinding::bind)
    private val args: LawyersInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lawyer = args.lawyer
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTextViews()
        setTransitions()
        viewModel.getStatus().observe(this) { handleStatus(it) }
        with(binding) {
            toolbar.toolbar.apply {
                title = lawyer.fullname
                menu.findItem(R.id.action_confirm).isVisible = false
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }
            speeddial.inflate(R.menu.menu_speed_dial)
            speeddial.setOnActionSelectedListener { item ->
                when (item.id) {
                    R.id.fab_call_lawyer -> {
                        Utils.showCallDialog(requireContext(), lawyer.phone, UserTypes.Lawyer)
                        speeddial.close()
                    }
                    R.id.fab_message_lawyer -> {
                        val dir =
                            LawyersInfoFragmentDirections.actionLawyersInfoFragmentToQuestionFragment(
                                lawyer, null, null)
                        findNavController().navigate(dir)
                        speeddial.close()
                    }
                    R.id.fab_book_lawyer -> {
                        val dir =
                            LawyersInfoFragmentDirections.actionLawyersInfoFragmentToNewReservationFragment(
                                lawyer, null, null)
                        findNavController().navigate(dir)
                        speeddial.close()
                    }
                }
                speeddial.close()
                false
            }

            recyclerView.adapter = lawyersCasesAdapter
            activityViewModel.getLawyersCases(lawyer.uid).observe(viewLifecycleOwner, {
                lawyersCasesAdapter.swapData(it)
                if (it.isNotEmpty()) {
                    if (textEmptyCases.isVisible) textEmptyCases.toggleVisibilityGoneToVisible()
                } else
                    if (textEmptyCases.isGone) textEmptyCases.toggleVisibilityGoneToVisible()
            })
        }
    }

    private fun setTextViews() {
        with(binding) {
            textName.text = lawyer.fullname
            textEducation.text = lawyer.education
            textSpecialization.text = lawyer.specialization
            textLocation.text =
                resources.getString(R.string.two_string_comma, lawyer.country, lawyer.city)
            if (lawyer.experience != "N/A") {
                textExperience.text = resources.getString(R.string.item_lawyer_experience,
                    Integer.parseInt(lawyer.experience))
            } else
                textExperience.text = lawyer.experience

            textWonCases.text =
                resources.getString(R.string.item_lawyer_won_cases, lawyer.wonCases)
            textAddress.text = lawyer.address
            imageProfile.load(lawyer.imageRef) {
                error(R.drawable.ic_person_24)
                placeholderMemoryCacheKey(imageProfile.metadata?.memoryCacheKey)
            }
        }
    }

    private fun setTransitions() {
        ViewCompat.setTransitionName(binding.infoLayout, lawyer.uid + "infoLayout")
        ViewCompat.setTransitionName(binding.imageProfile, lawyer.uid)
        ViewCompat.setTransitionName(binding.textName, lawyer.fullname)
        ViewCompat.setTransitionName(binding.textEducation, lawyer.uid + lawyer.education)
        ViewCompat.setTransitionName(binding.textSpecialization, lawyer.uid + lawyer.specialization)
        ViewCompat.setTransitionName(binding.textExperience,
            lawyer.uid + lawyer.experience)
        ViewCompat.setTransitionName(binding.textWonCases, lawyer.uid + lawyer.wonCases.toString())
        ViewCompat.setTransitionName(binding.textLocation, lawyer.uid + lawyer.city)
        ViewCompat.setTransitionName(binding.textAddress, lawyer.uid + lawyer.address)
    }

    private fun handleStatus(it: Status?) {
        when (it) {
            Status.SUCCESS -> requireView().snackbar(R.string.success)

            Status.ERROR -> shortToast(R.string.error_something)

            else -> shortToast(R.string.error_something)
        }
    }
}
