package lt.viko.eif.lawyer.ui.main.askedquestions

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.kotlinextensions.views.isGone
import com.crazylegend.kotlinextensions.views.toggleVisibilityGoneToVisible
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentAskedQuestionsBinding
import lt.viko.eif.lawyer.model.Question
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.ui.main.MainFragmentDirections
import lt.viko.eif.lawyer.utils.Status
import lt.viko.eif.lawyer.utils.Utils

class AskedQuestionsFragment : Fragment(R.layout.fragment_asked_questions),
    QuestionListAdapter.Interaction {
    private val viewModel: QuestionsViewModel by viewModels()
    private val binding by viewBinding(FragmentAskedQuestionsBinding::bind)
    private val questionsAdapter by lazy { QuestionListAdapter(this, role) }
    private val role = MainActivity.user.value!!.role

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus().observe(this, { handleStatus(it) })
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Question>("question")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.postQuestion(it)
            }
        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            recyclerView.adapter = questionsAdapter

            if (role == UserTypes.User) {
                textNoItems.text = resources.getString(R.string.question_no_active_questions)
                viewModel.getSentQuestions(MainActivity.user.value!!.uid)
                    .observe(viewLifecycleOwner) {
                        submitList(it)
                    }
            } else if (role == UserTypes.Lawyer) {
                textNoItems.text = resources.getString(R.string.question_no_questions)
                viewModel.getAskedQuestions(MainActivity.user.value!!.uid)
                    .observe(viewLifecycleOwner, {
                        submitList(it)
                    })
            }
        }
    }

    private fun submitList(list: List<Question>) {
        with(binding) {
            questionsAdapter.swapData(list)
            if (list.isNotEmpty()) {
                if (textNoItems.isVisible) textNoItems.toggleVisibilityGoneToVisible()
            } else
                if (textNoItems.isGone) textNoItems.toggleVisibilityGoneToVisible()
            progressBar.progressLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun handleStatus(it: Status?) {
        when (it) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()

            Status.ERROR -> shortToast(R.string.error_something)

            else -> shortToast(R.string.error_something)
        }
    }

    override fun onActionSelected(action: Int, item: Question, v: View) {
        when (action) {
            R.id.action_edit -> {
                val dir = MainFragmentDirections.actionMainFragmentToQuestionFragment(null,
                    item,
                    "edit_question")
                findNavController().navigate(dir)
            }
            R.id.action_delete -> {
                val dialog = MaterialAlertDialogBuilder(v.context)
                dialog.setMessage(R.string.dialog_are_you_sure)
                dialog.setTitle(R.string.dialog_title_confirm)
                dialog.setPositiveButton(R.string.action_delete) { _, _ ->
                    viewModel.deleteQuestion(item.id)
                }
                dialog.setNegativeButton(R.string.action_cancel) { d, _ ->
                    d.cancel()
                }
                dialog.create().show()
            }
            R.id.action_call -> {
                Utils.showCallDialog(requireContext(), item.sender!!.phone, UserTypes.User)
            }
        }
    }
}
