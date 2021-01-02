package lt.viko.eif.lawyer.ui.main.askedquestions

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.ItemQuestionBinding
import lt.viko.eif.lawyer.model.Question
import lt.viko.eif.lawyer.model.UserTypes

class QuestionListAdapter(
    private val interaction: Interaction? = null,
    private val role: UserTypes,
) :
    ListAdapter<Question, QuestionListAdapter.QuestionListViewHolder>(QuestionDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuestionListViewHolder(
        ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false).root,
        interaction, role
    )

    override fun onBindViewHolder(holder: QuestionListViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Question>) {
        submitList(data.toMutableList())
    }

    inner class QuestionListViewHolder(
        itemView: View,
        private val interaction: Interaction?,
        private val role: UserTypes,
    ) : RecyclerView.ViewHolder(itemView), OnClickListener {
        private val binding = ItemQuestionBinding.bind(itemView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (adapterPosition == RecyclerView.NO_POSITION) return
            val clicked = getItem(adapterPosition)
            val popup = PopupMenu(v?.context, binding.menuButton)
            popup.inflate(R.menu.menu_question_actions)
            if (role == UserTypes.Lawyer) {
                popup.menu.findItem(R.id.action_edit).isVisible = false
            } else if (role == UserTypes.User)
                popup.menu.findItem(R.id.action_call).isVisible = false
            popup.setOnMenuItemClickListener {
                interaction?.onActionSelected(it.itemId, clicked, itemView)
                true
            }
            popup.show()
        }

        fun bind(item: Question) = with(itemView) {
            with(binding) {
                textQuestion.text = item.description
                if (role == UserTypes.Lawyer) {
                    textCountry.text =
                        resources.getString(R.string.item_question_country, item.sender!!.country)
                    textCity.text =
                        resources.getString(R.string.item_question_city, item.sender!!.city)
                    textEmail.text =
                        resources.getString(R.string.item_question_email, item.sender!!.email)
                    textPhone.text =
                        resources.getString(R.string.item_question_phone, item.sender!!.phone)
                    textFullname.text =
                        resources.getString(R.string.item_question_full_name,
                            item.sender!!.fullname)
                } else if (role == UserTypes.User) {
                    textCountry.text =
                        resources.getString(R.string.item_question_country,
                            item.destination!!.country)
                    textCity.text =
                        resources.getString(R.string.item_question_city, item.destination!!.city)
                    textEmail.text =
                        resources.getString(R.string.item_question_email, item.destination!!.email)
                    textPhone.text =
                        resources.getString(R.string.item_question_phone, item.destination!!.phone)
                    textFullname.text =
                        resources.getString(R.string.item_question_lawyer_full_name,
                            item.destination!!.fullname)
                }

            }
        }
    }

    interface Interaction {
        fun onActionSelected(action: Int, item: Question, v: View)
    }

    private class QuestionDC : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(
            oldItem: Question,
            newItem: Question,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Question,
            newItem: Question,
        ) = oldItem == newItem
    }
}
