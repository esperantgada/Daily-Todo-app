@file:Suppress("IMPLICIT_CAST_TO_ANY")

package eg.esperantgada.dailytodo.fragment.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.databinding.FragmentAddEditNoteBinding
import eg.esperantgada.dailytodo.event.AddEditNoteEvent
import eg.esperantgada.dailytodo.utils.*
import eg.esperantgada.dailytodo.viewmodel.AddEditNoteViewModel
import java.util.*

@AndroidEntryPoint
class AddEditNoteFragment : Fragment() {

    private var _binding : FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel : AddEditNoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditNoteBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            noteTitle.setText(viewModel.noteTitle)
            noteDescription.setText(viewModel.noteDescription)

            //Sets item's name in the viewModel to the name of edited item and save it SaveStateHandle
            noteTitle.addTextChangedListener {
                viewModel.noteTitle = it.toString()
            }


            noteDescription.addTextChangedListener {
                viewModel.noteDescription = it.toString()
            }

            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }

            deleteButton.setOnClickListener { deleteInputs() }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditNoteEvent.collect { event ->
                when (event){
                    is AddEditNoteEvent.GoBackWithResult -> {
                        binding.noteTitle.clearFocus()
                        binding.noteDescription.clearFocus()

                        //Navigates back to NoteListFragment with the result of adding or editing action
                        setFragmentResult(
                            NOTE_REQUEST_KEY,
                            bundleOf(ADD_EDIT_NOTE_RESULT_KEY to event.result)
                        )
                        findNavController().popBackStack()
                    }

                    is AddEditNoteEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    }
                }.exhaustive
            }
        }
    }

    private fun deleteInputs() {
        if (binding.noteTitle.text.isBlank() && binding.noteDescription.text.isBlank()){
            Snackbar.make(binding.deleteButton, "Nothing to delete", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .show()
        }else{
            binding.noteTitle.setText("")
            binding.noteDescription.setText("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}