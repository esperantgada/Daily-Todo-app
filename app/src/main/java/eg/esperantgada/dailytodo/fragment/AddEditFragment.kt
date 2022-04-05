package eg.esperantgada.dailytodo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.FragmentAddEditBinding
import eg.esperantgada.dailytodo.viewmodel.AddEdithViewModel

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding : FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AddEdithViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            todoName.setText(viewModel.todoName)
            importantTodo.isChecked = viewModel.isImportant
            importantTodo.jumpDrawablesToCurrentState()
            dateCreatedTextView.isVisible = viewModel.sentTodo != null
            dateCreatedTextView.text = context?.getString(R.string.created_at, viewModel.sentTodo?.dataFormatted)
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}