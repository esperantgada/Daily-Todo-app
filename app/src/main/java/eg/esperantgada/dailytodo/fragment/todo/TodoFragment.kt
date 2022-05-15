package eg.esperantgada.dailytodo.fragment.todo

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.SettingsActivity
import eg.esperantgada.dailytodo.adapter.TodoAdapter
import eg.esperantgada.dailytodo.databinding.FragmentTodoBinding
import eg.esperantgada.dailytodo.databinding.TodoItemBinding
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.ADD_EDIT_RESULT_KEY
import eg.esperantgada.dailytodo.utils.REQUEST_KEY
import eg.esperantgada.dailytodo.utils.exhaustive
import eg.esperantgada.dailytodo.utils.onQueryTextChanged
import eg.esperantgada.dailytodo.viewmodel.TodoViewModel
import kotlinx.android.synthetic.main.todo_item.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

const val TAG3 = "TodoFragment"

/**
 * Inject the [TodoViewModel] in the [Fragment]
 */
@Suppress("DEPRECATION")
@AndroidEntryPoint
class TodoFragment : Fragment(), TodoAdapter.OnItemClickedListener {

    private val todoViewModel: TodoViewModel by viewModels()

    private var _binding : FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var  todoItemBinding : TodoItemBinding

    private lateinit var todoAdapter: TodoAdapter

    private lateinit var searchView: SearchView

    private lateinit var todoList : List<Todo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTodoBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        todoAdapter = TodoAdapter(requireContext(), this, todoViewModel)

        binding.apply {
            recyclerView.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            floating.setOnClickListener { todoViewModel.addNewTodo() }

            //Gets and handles the result of adding or editing from AddEditTodoFragment
            setFragmentResultListener(REQUEST_KEY) { _, bundle ->
                val result = bundle.getInt(ADD_EDIT_RESULT_KEY)
                todoViewModel.onAddEditTodoResult(result)
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            todoViewModel.todos.collectLatest { todosList ->
                todoAdapter.submitData(viewLifecycleOwner.lifecycle, todosList)

                Log.d(TAG3, "TODO LIST SUBMITTED IN TODO FRAGMENT : $todosList")
            }
        }

        todoViewModel.timer.observe(viewLifecycleOwner){
            val timer = it

            Log.d(TAG3, "TIMER FROM VIEWMODEL : $timer")
        }


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            todoViewModel.allTodo.observe(viewLifecycleOwner){ todos ->
                todoList = todos
                binding.emptyItemTextView.isVisible = todoList.isEmpty()

                Log.d(TAG3, "TODO LIST IN TODO FRAGMENT: $todos")
            }
        }


        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        /**
         * This will handle the different events action that the ViewModel will send.
         */
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            todoViewModel.todoEvent.collect { event ->
                when (event) {
                    is TodoEvent.ShowUndoDeleteTodoMessage -> {
                        showSnackBarAndToast(event)
                    }

                    is TodoEvent.GoToAddTodoFragment -> {
                        val action =
                            TodoFragmentDirections.actionTodoFragmentToAddEditFragment(
                                null, "Add todo")
                        findNavController().navigate(action)
                    }

                    is TodoEvent.GoToEditFragment -> {
                        val action =
                            TodoFragmentDirections.actionTodoFragmentToAddEditFragment(
                                event.todo,
                                "Edit todo")
                        findNavController().navigate(action)
                    }
                    is TodoEvent.ShowSavedTodoConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    }
                    TodoEvent.GotoAlertDialogFragment -> {
                        val action =
                            TodoFragmentDirections.actionGlobalAlertDialogueFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }


    //Method that shows SnackBar and Toast messages
    private fun showSnackBarAndToast(event: TodoEvent.ShowUndoDeleteTodoMessage) {
        Snackbar.make(requireView(), "TASK DELETED", Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction("UNDO") {
                todoViewModel.onUndoDelete(event.todo)
                Toast.makeText(requireContext(), "Undone successfully", Toast.LENGTH_LONG)
                    .show()
            }.show()
    }

    /**
     * Logic for search in [SearchView]
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)

        val searchedTodo = menu.findItem(R.id.search_todo)
        searchView = searchedTodo.actionView as SearchView

        //This will help to handle searchQuery state during device configuration changes
        val pendingQuery = todoViewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchedTodo.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            todoViewModel.searchQuery.value = it
        }

        //Read the current hideCompleted state from the DataStore
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.hide_completed_todo).isChecked =
                todoViewModel.preferenceFlow.first().hideCompleted
        }
    }


    /**
     * Handles all logic for [actionBar] menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.sort_by_name -> {
                todoViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_by_date_created -> {
                todoViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.hide_completed_todo -> {
                item.isChecked = !item.isChecked
                todoViewModel.onHideCompleted(item.isChecked)
                true
            }

            R.id.delete_all_completed_todo -> {
                todoViewModel.goToAlertDialogFragment()
                true
            }

            R.id.setting ->{
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    /**
     * If an item is clicked or if a task is checked, the viewModel updates this one in the database
     */
    override fun onItemClicked(todo: Todo) {
        todoViewModel.onTodoSelected(todo)
    }


    override fun onCheckBoxClicked(todo: Todo, isChecked: Boolean) {
        todoViewModel.onTodoCheckedChanged(todo, isChecked)
    }

    override fun setTodoCountDownTimer(): String {
        return todoList.toString()
    }


    //Sets drag and swipe actions on items in the recycler view
    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
        @SuppressLint("NotifyDataSetChanged")
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition

            Collections.swap(todoList, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val todo = todoAdapter.snapshot()[viewHolder.adapterPosition]
            if (todo != null) {
                todoViewModel.onItemSwiped(todo)
            }
        }
    })


    inner class TodoCountDownTimerReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                gettimer(intent)
                Log.d(TAG3, "onReceived CALLED IN  TODO FRAGMENT")
            }
        }
    }


    private fun gettimer(intent: Intent) {
        if (intent.extras != null) {
            val todoTimer = intent.getStringExtra("timer")
            if (todoTimer != null) {
                todoViewModel.setTimer(todoTimer)
                Log.d(TAG3, "TIMER IN TODO FRAGMENT : $todoTimer")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.my.app")
        val receiver = TodoCountDownTimerReceiver()
        activity?.registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        val receiver = TodoCountDownTimerReceiver()
        activity?.let { LocalBroadcastManager.getInstance(it) }?.unregisterReceiver(receiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
        // searchView.setOnQueryTextListener(null)
    }
}