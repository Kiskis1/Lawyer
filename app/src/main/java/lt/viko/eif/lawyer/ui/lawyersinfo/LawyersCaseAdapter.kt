package lt.viko.eif.lawyer.ui.lawyersinfo

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.ItemCaseBinding
import lt.viko.eif.lawyer.model.Case
import lt.viko.eif.lawyer.ui.profile.ProfileFragmentDirections
import lt.viko.eif.lawyer.ui.profile.ProfileViewModel
import java.text.DateFormat

class LawyersCaseAdapter(private val fragment: Fragment, private val viewModel: ProfileViewModel?) :
    ListAdapter<Case, LawyersCaseAdapter.LawyersCaseViewHolder>(CaseDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LawyersCaseViewHolder(
        ItemCaseBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    )

    override fun onBindViewHolder(holder: LawyersCaseViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Case>) {
        submitList(data.toMutableList())
    }

    inner class LawyersCaseViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        private val binding = ItemCaseBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
            binding.menuButton.visibility = if (viewModel != null) View.VISIBLE else View.INVISIBLE
        }

        override fun onClick(v: View?) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            if (viewModel != null) {
                val clicked = getItem(adapterPosition)
                val popup = PopupMenu(v?.context, binding.menuButton)
                popup.inflate(R.menu.menu_case_actions)
                popup.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_edit -> {
                            val dir =
                                ProfileFragmentDirections.actionProfileFragmentToNewCaseFragment(
                                    clicked,
                                    "edit_case")
                            fragment.findNavController().navigate(dir)
                        }
                        R.id.action_delete -> {
                            val builder = AlertDialog.Builder(itemView.context)
                            builder.setMessage(R.string.dialog_are_you_sure)
                            builder.setTitle(R.string.dialog_title_confirm)
                            builder.setPositiveButton(R.string.action_delete) { _, _ ->
                                viewModel.deleteCase(clicked.id)
                            }
                            builder.setNegativeButton(R.string.action_cancel) { d, _ ->
                                d.cancel()
                            }
                            val dialog = builder.create()
                            dialog.window?.attributes?.windowAnimations = R.style.DialogAnim
                            dialog.show()
                        }
                    }
                    true
                }
                popup.show()
            }
        }

        fun bind(item: Case) = with(itemView) {
            with(binding) {
                textArea.text = resources.getString(R.string.item_case_area, item.area)
                textCourt.text = resources.getString(R.string.item_case_court, item.court)
                textDate.text = resources.getString(
                    R.string.item_case_date,
                    DateFormat.getDateInstance().format(item.date)
                )
                textDesc.text =
                    resources.getString(R.string.item_case_short_description, item.shortDesc)
                textOutcome.text =
                    resources.getString(R.string.item_case_outcome, item.outcome)
                textType.text = resources.getString(R.string.item_case_type, item.type)
            }
        }
    }

    private class CaseDC : DiffUtil.ItemCallback<Case>() {
        override fun areItemsTheSame(
            oldItem: Case,
            newItem: Case,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Case,
            newItem: Case,
        ) = oldItem == newItem
    }
}
