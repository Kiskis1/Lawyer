package com.acruxcs.lawyer.ui.main.reservation

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.ItemReservationBinding
import com.acruxcs.lawyer.model.Reservation

class ReservationsAdapter :
    ListAdapter<Reservation, ReservationsAdapter.ReservationsViewHolder>(ReservationDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReservationsViewHolder(
        ItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
    )

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Reservation>) {
        submitList(data.toMutableList())
    }

    inner class ReservationsViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return

            val clicked = getItem(adapterPosition)
        }

        fun bind(item: Reservation) = with(itemView) {
            with(ItemReservationBinding.bind(itemView)) {
                textLawyer.text =
                    resources.getString(R.string.reservation_lawyer_name, item.lawyer!!.fullname)
                textDate.text = resources.getString(R.string.reservation_date, item.date)
                textTime.text = resources.getString(R.string.reservation_time, item.time)
                textAddress.text =
                    resources.getString(R.string.reservation_lawyer_address, item.lawyer!!.address)
            }
        }
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
