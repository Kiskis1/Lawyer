package lt.viko.eif.lawyer.ui.main.reservation

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentReservationsBinding
import lt.viko.eif.lawyer.model.Reservation
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.ui.main.MainFragmentDirections
import lt.viko.eif.lawyer.utils.Status

class ReservationsFragment : Fragment(R.layout.fragment_reservations),
    ReservationsAdapter.Interaction {
    private val binding by viewBinding(FragmentReservationsBinding::bind)
    private val viewModel: ReservationsViewModel by viewModels()
    private val role = MainActivity.user.value!!.role

    private val reservationsAdapter by lazy { ReservationsAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Reservation>("reservation")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.postReservation(it)
            }
        viewModel.getStatus().observe(this@ReservationsFragment, { handleStatus(it) })
        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.adapter = reservationsAdapter
            recyclerView.visibility = View.INVISIBLE

            if (role == UserTypes.User) {
                textNoItems.text = resources.getString(R.string.reservation_no_active_reservation)
                viewModel.getReservationsForUser(MainActivity.user.value!!.uid)
                    .observe(viewLifecycleOwner) {
                        submitList(it)
                    }
            } else if (role == UserTypes.Lawyer) {
                textNoItems.text = resources.getString(R.string.reservation_no_reservations)
                viewModel.getReservationsForLawyer(MainActivity.user.value!!.uid)
                    .observe(viewLifecycleOwner, {
                        submitList(it)
                    })
            }

        }
    }

    private fun submitList(list: List<Reservation>) {
        with(binding) {
            reservationsAdapter.swapData(list)
            if (list.isNotEmpty()) {
                if (textNoItems.isVisible) textNoItems.toggleVisibilityGoneToVisible()
            } else
                if (textNoItems.isGone) textNoItems.toggleVisibilityGoneToVisible()
            progressBar.progressLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onActionSelected(action: Int, item: Reservation, v: View) {
        when (action) {
            R.id.action_edit -> {
                val dir =
                    MainFragmentDirections.actionMainFragmentToNewReservationFragment(item.lawyer!!,
                        "edit_reservation",
                        item)
                findNavController()
                    .navigate(dir)
            }
            R.id.action_delete -> {
                val builder = MaterialAlertDialogBuilder(v.context)
                builder.setMessage(R.string.dialog_are_you_sure)
                builder.setTitle(R.string.dialog_title_confirm)
                builder.setPositiveButton(R.string.action_delete) { _, _ ->
                    viewModel.deleteReservation(item.id)
                }
                builder.setNegativeButton(R.string.action_cancel) { d, _ ->
                    d.cancel()
                }
                val dialog = builder.create()
                dialog.window?.attributes?.windowAnimations = R.style.DialogAnim
                dialog.show()
            }
        }
    }

    private fun handleStatus(it: Status?) {
        when (it) {
            Status.SUCCESS -> {
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()
                reservationsAdapter.notifyDataSetChanged()
            }

            Status.ERROR -> shortToast(R.string.error_something)

            else -> shortToast(R.string.error_something)
        }
    }
}
