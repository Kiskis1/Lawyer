package lt.viko.eif.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.toggleVisibilityInvisibleToVisible
import com.crazylegend.viewbinding.viewBinding
import com.vivekkaushik.datepicker.OnDateSelectedListener
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentNewReservationBinding
import lt.viko.eif.lawyer.model.Reservation
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.ui.lawyers.LawyersViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NewReservationFragment : Fragment(R.layout.fragment_new_reservation),
    TimeSelectionAdapter.Interaction {
    private var lawyer: User? = null
    private var reservation: Reservation? = null
    private var selectedDate: LocalDate? = null
    private var selectedTime: LocalTime? = null
    private val binding by viewBinding(FragmentNewReservationBinding::bind)
    private val viewModel: LawyersViewModel by viewModels({ requireParentFragment() })
    private var tagas: String? = null
    private val args: NewReservationFragmentArgs by navArgs()

    private val timeAdapter by lazy { TimeSelectionAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lawyer = args.lawyer
        tagas = args.tag
        reservation = args.reservation
        if (tagas == null && tagas != "edit_reservation") {
            reservation = Reservation(lawyer = lawyer,
                user = MainActivity.user.value!!.uid,
                userName = MainActivity.user.value!!.fullname)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            recyclerView.adapter = timeAdapter
            toolbar.toolbar.apply {
                setTitle(R.string.dialog_title_reservation)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }
            lawyer!!.workingHours?.let {
                toolbar.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_confirm -> {
                            selectedDate ?: run {
                                shortToast(R.string.error_select_date)
                                return@setOnMenuItemClickListener true
                            }
                            selectedTime ?: run {
                                shortToast(R.string.error_select_time)
                                return@setOnMenuItemClickListener true
                            }
                            reservation!!.date = "$selectedDate"
                            reservation!!.time = "$selectedTime"
                            reservation!!.dateLawyer = "$selectedDate" + "_" + lawyer!!.uid
                            viewModel.postReservation(reservation!!)
                            findNavController().navigateUp()
                            true
                        }
                        else -> false
                    }
                }
                visitTypeGroup.setOnCheckedChangeListener(radioGroupListener)
                dayPicker.apply {
                    setInitialDate(
                        LocalDate.now().year,
                        LocalDate.now().month.value - 1,
                        LocalDate.now().dayOfMonth
                    )
                    setOnDateSelectedListener(dateSelectedListener)

                }
            } ?: run {
                toolbar.toolbar.menu.findItem(R.id.action_confirm).isVisible = false
                textEmptyList.visibility = View.VISIBLE
                visitTypeGroup.visibility = View.GONE
                recyclerView.visibility = View.GONE
                dayPicker.visibility = View.GONE
            }
        }
    }

    private val radioGroupListener = RadioGroup.OnCheckedChangeListener { _, id ->
        when (id) {
            R.id.radio_person ->
                reservation!!.inPerson = true
            R.id.radio_remote ->
                reservation!!.inPerson = false
        }
    }

    private val dateSelectedListener = object : OnDateSelectedListener {
        override fun onDateSelected(
            year: Int,
            month: Int,
            day: Int,
            dayOfWeek: Int,
        ) {
            val str = String.format("$year-%02d-%02d", month + 1, day)
            selectedDate =
                LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            selectedTime = null
            timeAdapter.resetSelected()
            viewModel.getAvailableTimes(str, dayOfWeek, lawyer!!)
                .observe(viewLifecycleOwner, {
                    timeAdapter.swapData(it)
                    if (it.isEmpty() && binding.textNoTimes.visibility == View.INVISIBLE) {
                        binding.textNoTimes.toggleVisibilityInvisibleToVisible()
                    } else if (it.isNotEmpty() && binding.textNoTimes.visibility == View.VISIBLE) {
                        binding.textNoTimes.toggleVisibilityInvisibleToVisible()
                    }
                })
        }

        override fun onDisabledDateSelected(
            year: Int,
            month: Int,
            day: Int,
            dayOfWeek: Int,
            isDisabled: Boolean,
        ) = Unit
    }

    override fun onTimeSelected(time: LocalTime?) {
        time?.let { selectedTime = it } ?: run { return }
    }
}
