package eg.esperantgada.dailytodo.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.adapter.TodoAdapter
import eg.esperantgada.dailytodo.databinding.FragmentTodoBinding
import eg.esperantgada.dailytodo.utils.onQueryTextChanged
import eg.esperantgada.dailytodo.viewmodel.SortOrder
import eg.esperantgada.dailytodo.viewmodel.TodoViewModel

/**
 * Inject the [TodoViewModel] in the [Fragment]
 */
@AndroidEntryPoint
class TodoFragment : Fragment() {

    private val todoViewModel : TodoViewModel by viewModels()

    private var _binding : FragmentTodoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        val todoAdapter = TodoAdapter()

        binding.apply {
            recyclerView.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        todoViewModel.todos.observe(viewLifecycleOwner){
            todoAdapter.submitList(it)
        }

        //Tell the fragment it has an option menu
        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    /**
     * Logic for search in [SearchView]
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)

        val searchedTodo = menu.findItem(R.id.search_todo)
        val searchView = searchedTodo.actionView as SearchView

        searchView.onQueryTextChanged{
            todoViewModel.searchQuery.value = it
        }
    }

    /**
     * Handles all logic for [actionBar] menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.sort_by_name ->{
                todoViewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }

            R.id.sort_by_date_created ->{
                todoViewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }

            R.id.hide_completed_todo ->{
                item.isChecked = !item.isChecked
                todoViewModel.hideCompleted.value = item.isChecked
                true
            }

            R.id.delete_all_completed_todo ->{
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}