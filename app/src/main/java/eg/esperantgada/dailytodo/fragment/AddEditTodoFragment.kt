package eg.esperantgada.dailytodo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.FragmentAddEditBinding
import eg.esperantgada.dailytodo.event.AddEditTodoEvent
import eg.esperantgada.dailytodo.utils.ADD_EDIT_RESULT_KEY
import eg.esperantgada.dailytodo.utils.REQUEST_KEY
import eg.esperantgada.dailytodo.utils.exhaustive
import eg.esperantgada.dailytodo.viewmodel.AddEditTodoViewModel
import kotlinx.coroutines.flow.collect

@Suppress("IMPLICIT_CAST_TO_ANY")
@AndroidEntryPoint
class AddEditTodoFragment : Fragment() {

    private var _binding : FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AddEditTodoViewModel by viewModels()

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

            //Sets item's name in the viewModel to the name of edited item and save it SaveStateHandle
            todoName.addTextChangedListener {
                viewModel.todoName = it.toString()
            }

            //Sets item's importance in the viewModel to the importance of edited item and save it SaveStateHandle
            importantTodo.setOnCheckedChangeListener { _, isChecked  ->
                viewModel.isImportant = isChecked
            }


            saveFloatingButton.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTodoEvent.collect { event ->
                when (event){
                    is AddEditTodoEvent.GoBackWithResult -> {
                        binding.todoName.clearFocus()

                        //Navigates back to TodoFragment with the result of adding or editing action
                        setFragmentResult(
                            REQUEST_KEY,
                            bundleOf(ADD_EDIT_RESULT_KEY to event.result)
                        )
                        findNavController().popBackStack()
                    }

                    is AddEditTodoEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    }
                }.exhaustive
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}