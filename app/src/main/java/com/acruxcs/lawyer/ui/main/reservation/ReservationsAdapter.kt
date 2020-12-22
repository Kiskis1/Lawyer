package com.acruxcs.lawyer.ui.main.reservation

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.ItemReservationBinding
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.utils.Utils.dateFormat
import java.util.Date

class ReservationsAdapter(
    private val interaction: Interaction? = null,
    private val history: Boolean = false,
) :
    ListAdapter<Reservation, ReservationsAdapter.ReservationsViewHolder>(ReservationDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReservationsViewHolder(
        ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false).root,
        interaction, history
    )

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Reservation>) {
        submitList(data.toMutableList())
    }

    inner class ReservationsViewHolder(
        itemView: View,
        private val interaction: Interaction?,
        history: Boolean,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        private val binding = ItemReservationBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
            binding.menuButton.visibility = if (!history) View.VISIBLE else View.INVISIBLE
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
            val strDate = dateFormat.parse("${clicked.date} ${clicked.time}")
            if (!Date().after(strDate)) {
                val popup = PopupMenu(v?.context, binding.menuButton)
                popup.inflate(R.menu.menu_reservation_actions)
                popup.setOnMenuItemClickListener {
                    interaction?.onActionSelected(it.itemId, clicked, itemView)
                    true
                }
                popup.show()
            }
        }

        fun bind(item: Reservation) = with(itemView) {
            with(binding) {
                textFullname.text = item.lawyer!!.fullname
                textDate.text = item.date
                textTime.text = item.time
                textAddress.text = item.lawyer!!.address
                textVisitType.text = when (item.inPerson) {
                    true -> resources.getString(R.string.reservation_visit_type_person)
                    false -> resources.getString(R.string.reservation_visit_type_remote)
                }
                textPaymentType.text =
                    resources.getString(R.string.item_reservation_payment_type,
                        item.lawyer!!.paymentTypes)
                val strDate = dateFormat.parse("${item.date} ${item.time}")
                if (Date().after(strDate))
                    binding.menuButton.visibility = View.GONE
            }
        }
    }

    interface Interaction {
        fun onActionSelected(action: Int, item: Reservation, v: View)
    }

    private class ReservationDC : DiffUtil.ItemCallback<Reservation>() {
        override fun areItemsTheSame(
            oldItem: Reservation,
            newItem: Reservation,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Reservation,
            newItem: Reservation,
        ) = oldItem == newItem
    }
}
