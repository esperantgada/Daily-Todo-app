package eg.esperantgada.dailytodo.fragment.note

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.SettingsActivity
import eg.esperantgada.dailytodo.adapter.NoteAdapter
import eg.esperantgada.dailytodo.databinding.FragmentNoteListBinding
import eg.esperantgada.dailytodo.event.NoteEvent
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.*
import eg.esperantgada.dailytodo.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class NoteListFragment : Fragment(), NoteAdapter.OnNoteClickedListener {

    private var _binding : FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter

    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private val noteViewModel : NoteViewModel by viewModels()

    private lateinit var notes : List<Note>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoteListBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteAdapter = NoteAdapter(requireContext(), this)

        binding.apply {
            noteRecyclerView.apply {
                layoutManager  = StaggeredGridLayoutManager(
                    if (resources.configuration.orientation == Configuration
                            .ORIENTATION_PORTRAIT) 2 else 3, StaggeredGridLayoutManager.VERTICAL
                )

                setHasFixedSize(true)
                adapter = noteAdapter
            }

            floating.setOnClickListener { noteViewModel.addNewNote() }
        }

        //Gets and handles the result of adding or editing from AddEditTodoFragment
        setFragmentResultListener(NOTE_REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_NOTE_RESULT_KEY)
            noteViewModel.onAddEditNoteResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            noteViewModel.notes.observe(viewLifecycleOwner){ notesList ->
                notes = notesList
                binding.emptyItemTextView.isVisible = notes.isEmpty()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            noteViewModel.notesList.collectLatest {
                noteAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        itemTouchHelper.attachToRecyclerView(binding.noteRecyclerView)


        /**
         * This will handle the different events action that the ViewModel will send.
         */
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            noteViewModel.noteEvent.collect { event ->
                when (event) {
                    is NoteEvent.ShowUndoDeleteNoteMessage -> {
                        showSnackBarAndToast(event)
                    }

                    is NoteEvent.GoToAddNoteFragment -> {
                        val action =
                            NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(
                                null, "Add note")
                        findNavController().navigate(action)
                    }

                    is NoteEvent.GoToEditNoteFragment -> {
                        val action =
                            NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(
                                event.note,
                                "Edit note")
                        findNavController().navigate(action)
                    }

                    is NoteEvent.ShowSavedNoteConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    }

                    NoteEvent.GotoAlertDialogFragment -> {
                        val action =
                            NoteListFragmentDirections.actionGlobalAlertDialogueFragment()
                        findNavController().navigate(action)
                    }

                    is NoteEvent.DeleteNote -> {noteViewModel.onDeleteClicked(event.note)}


                    is NoteEvent.ShareNote -> {shareNote(event.note)}
                }.exhaustive
            }
        }
    }

    private fun shareNote(note: Note){
        val intent= Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,"NOTE TITLE : "+note.title+"\n"+"NOTE DESCRIPTION :\n"+note.description)
        val chooser= Intent.createChooser(intent,"Share this note text using...")
        startActivity(chooser, null)

    }

    //Method that shows SnackBar and Toast messages
    private fun showSnackBarAndToast(event: NoteEvent.ShowUndoDeleteNoteMessage) {
        Snackbar.make(requireView(), "NOTE DELETED", Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction("UNDO") {
                noteViewModel.onUndoDelete(event.note)
                Toast.makeText(requireContext(), "Undone successfully", Toast.LENGTH_LONG)
                    .show()
            }.show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.sort_by_name -> {
                noteViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_by_date_created -> {
                noteViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.setting ->{
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_menu, menu)

        val searchedNote = menu.findItem(R.id.search_note)
        searchView = searchedNote.actionView as androidx.appcompat.widget.SearchView

        //This will help to handle searchQuery state during device configuration changes
        val pendingQuery = noteViewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchedNote.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            noteViewModel.searchQuery.value = it
        }

    }

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.RIGHT or ItemTouchHelper.START, ItemTouchHelper.RIGHT) {
        @SuppressLint("NotifyDataSetChanged")
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition

            Collections.swap(notes, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            return
        }
    })


    //Note adapter interface implementation
    override fun onNoteClicked(note: Note) {
        noteViewModel.onNoteSelected(note)
    }

    override fun onShareNoteClicked(note: Note, isClicked: Boolean) {
        noteViewModel.onShareNoteClicked(note)
    }

    override fun onDeleteNoteClicked(note: Note, isClicked: Boolean) {
        noteViewModel.onDeleteClicked(note)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}