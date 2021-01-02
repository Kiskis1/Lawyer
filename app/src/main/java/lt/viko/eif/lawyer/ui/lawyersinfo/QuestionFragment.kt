package lt.viko.eif.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.crazylegend.viewbinding.viewBinding
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentQuestionBinding
import lt.viko.eif.lawyer.model.Question
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.ui.lawyers.LawyersViewModel
import lt.viko.eif.lawyer.utils.Utils
import lt.viko.eif.lawyer.utils.Utils.checkFieldIfEmpty
import lt.viko.eif.lawyer.utils.Utils.yes

class QuestionFragment : Fragment(R.layout.fragment_question) {
    private var question: Question? = null
    private var lawyer: User? = null
    private val binding by viewBinding(FragmentQuestionBinding::bind)
    private var tagas: String? = null
    private val args: QuestionFragmentArgs by navArgs()
    private val viewModel: LawyersViewModel by viewModels({ requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lawyer = args.lawyer
        tagas = args.tag
        question = args.question
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            if (tagas != null && tagas == "edit_question") {
                toolbar.toolbar.setTitle(R.string.dialog_title_edit)
                editDescription.setText(question?.description)
            } else {
                question = Question(sender = MainActivity.user.value!!, destination = lawyer)
                toolbar.toolbar.setTitle(R.string.dialog_title_question)
            }
            toolbar.toolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener(toolbarMenuClickListener)
            }
        }
    }

    private val toolbarMenuClickListener = Toolbar.OnMenuItemClickListener { item ->
        with(binding) {
            when (item.itemId) {
                R.id.action_confirm -> {
                    if (isValid()) {
                        question!!.description = editDescription.text.toString().trim()
                        Utils.hideKeyboard(requireContext(), binding.root)
                        viewModel.postQuestion(question!!)
                        findNavController().navigateUp()
                    }
                    true
                }
                else -> false
            }
        }

    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(
                editDescription, layoutDescription, requireContext()
            ).yes { valid = false }
        }
        return valid
    }
}
