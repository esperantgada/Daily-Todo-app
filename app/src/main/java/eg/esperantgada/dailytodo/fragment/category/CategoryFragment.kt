package eg.esperantgada.dailytodo.fragment.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.SettingsActivity
import eg.esperantgada.dailytodo.adapter.ColorAdapter
import eg.esperantgada.dailytodo.adapter.ImageAdapter
import eg.esperantgada.dailytodo.adapter.TodoAdapter
import eg.esperantgada.dailytodo.databinding.FragmentCategoryBinding
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.fragment.todo.TAG3
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.*
import eg.esperantgada.dailytodo.viewmodel.CategoryViewModel
import eg.esperantgada.dailytodo.viewmodel.TodoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CategoryFragment : Fragment(), TodoAdapter.OnItemClickedListener {

    private var _binding: FragmentCategoryBinding? = null
    private val binding = _binding!!

    private val categoryViewModel: CategoryViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()
    private lateinit var todos: List<Todo>

    private lateinit var colorAdapter: ColorAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var iconAdapter: ImageAdapter
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var categoryBackgroundLayout: LinearLayoutManager
    private lateinit var categoryIconLayout: GridLayoutManager
    //private lateinit var listener: OnViewClickedListener
    private lateinit var categoryFragmentLayoutBackground: ConstraintLayout
    private lateinit var recyclerView: RecyclerView

    private val navigationArgs: CategoryFragmentArgs by navArgs()
    private lateinit var searchView: SearchView


/*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnViewClickedListener) listener = context else {
            throw ClassCastException("$context must implement OnClicked")
        }
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryBinding.inflate(inflater)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigationArgs.category?.let { setupLayout(it) }

        if (navigationArgs.category?.categoryName == "Untitled") {
            createNewCategory()
        }

        todoAdapter = TodoAdapter(requireContext(), this)

        binding.apply {
            categoryListTodoRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

/*
            addCategoryButton.setOnClickListener {
                navigationArgs.category?.categoryName?.let { it1 ->
                    val action =
                        CategoryFragmentDirections.actionCategoryFragmentToAddEditTodoFragment()
                }
            }
*/

        }

        //Get a specific category from the database and get its todos
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            navigationArgs.category?.categoryName?.let {
                categoryViewModel
                    .getSingleCategoryByName(navigationArgs
                        .category!!.categoryName).observe(viewLifecycleOwner) { category ->

                        todoAdapter.submitList(category.todos)
                        Log.d(TAG3, "TODO LIST SUBMITTED IN TODO FRAGMENT : ${category.todos}")
                    }
            }
        }

        //Gets and handles the result of adding or editing from AddEditTodoFragment
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_RESULT_KEY)
            todoViewModel.onAddEditTodoResult(result)
        }

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
                            CategoryFragmentDirections.actionCategoryFragmentToAddEditTodoFragment(
                                null, "Add todo", navigationArgs.category)
                        findNavController().navigate(action)
                    }

                    is TodoEvent.GoToEditFragment -> {
                        val action =
                            CategoryFragmentDirections.actionCategoryFragmentToAddEditTodoFragment(
                                event.todo,
                                "Edit todo", navigationArgs.category)
                        findNavController().navigate(action)
                    }
                    is TodoEvent.ShowSavedTodoConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show()
                    }
                    TodoEvent.GotoAlertDialogFragment -> {
                        val action =
                            CategoryFragmentDirections.actionGlobalAlertDialogueFragment()
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
    @Deprecated("Deprecated in Java")
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
    @Deprecated("Deprecated in Java")
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
        todoViewModel.onTodoCheckedChanged(todo, isChecked, requireActivity())
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

            Collections.swap(todos, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val todo = todoAdapter.currentList[viewHolder.adapterPosition]
            if (todo != null) {
                todoViewModel.onItemSwiped(todo)
            }
        }
    })



    private fun createNewCategory() {

        val dialog = MaterialDialog(requireContext()).show {
            title(R.string.create_new_category)
            noAutoDismiss()
            setOnCancelListener {
                requireActivity().supportFragmentManager.popBackStack()
                //Already add to listCategory when create fragment
                //Need remove it when cancel add new category
                navigationArgs.category?.let { it1 -> categoryViewModel.deleteCategory(it1) }
            }
            cancelOnTouchOutside(false)
            customView(
                R.layout.new_category_dialog,
                scrollable = true,
                horizontalPadding = true
            )
            positiveButton(R.string.create_new_category) { dialog ->
                val categoryName = (dialog.getCustomView()
                    .findViewById(R.id.category_name) as TextView).text.toString()
                if (categoryName == "") {
                    Toast.makeText(
                        activity,
                        "Name cannot empty !",
                        Toast.LENGTH_SHORT
                    ).show()
                    noAutoDismiss()
                } else {
                    //Already add to listCategory when create fragment
                    //If call mainViewModel.add will leads to add duplicate category
                    navigationArgs.category?.categoryName = categoryName
                    //collapsingToolbarLayout.title = categoryName
                    navigationArgs.category?.let {
                        (activity as AppCompatActivity).supportActionBar?.setLogo(it.image)
                    }
                    dismiss()
                }
            }
            negativeButton(R.string.cancel) {
                dismiss()
                //Already add to listCategory when create fragment
                //Need remove it when cancel add new category
                navigationArgs.category?.let { it1 -> categoryViewModel.deleteCategory(it1) }
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


        colorAdapter = ColorAdapter(CategoryBackground.backgroundColor).apply {
            adapterListener = object : ColorAdapter.OnItemClickedListener,
                ImageAdapter.OnItemClickedListener {
                override fun onItemClicked(color: Int) {
                    val colorId = context?.let { ContextCompat.getColor(it, color) }
                    if (colorId != null) {
                        categoryFragmentLayoutBackground.setBackgroundColor(colorId)
                    }
                    navigationArgs.category?.image = color
                }
            }
        }

        imageAdapter = ImageAdapter(CategoryBackground.backgroundImage).apply {
            adapterListener = object : ColorAdapter.OnItemClickedListener,
                ImageAdapter.OnItemClickedListener {
                override fun onItemClicked(image: Int) {
                    categoryFragmentLayoutBackground.setBackgroundResource(image)
                    navigationArgs.category?.image = image
                }
            }
        }

        iconAdapter = ImageAdapter(CategoryBackground.backgroundImage).apply {
            adapterListener = object : ColorAdapter.OnItemClickedListener,
                ImageAdapter.OnItemClickedListener {
                override fun onItemClicked(icon: Int) {
                    dialog.getCustomView().findViewById<ImageButton>(R.id.insert_icon)
                        .setImageResource(icon)
                    navigationArgs.category?.icon = icon
                }
            }
        }


        categoryBackgroundLayout =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryIconLayout =
            GridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
        recyclerView =
            dialog.getCustomView().findViewById(R.id.category_background_image_recyclerview)
        recyclerView.layoutManager = categoryBackgroundLayout
        recyclerView.adapter = imageAdapter

        // Change data set in recyclerview
        dialog.getCustomView().findViewById<Button>(R.id.background_image).setOnClickListener {
            if (recyclerView.adapter != imageAdapter) {
                recyclerView.layoutManager = categoryBackgroundLayout
                recyclerView.adapter = imageAdapter
            }
        }
        dialog.getCustomView().findViewById<Button>(R.id.background_color).setOnClickListener {
            if (recyclerView.adapter != colorAdapter) {
                recyclerView.layoutManager = categoryBackgroundLayout
                recyclerView.adapter = colorAdapter
            }
        }
        dialog.getCustomView().findViewById<ImageButton>(R.id.insert_icon).setOnClickListener {
            if (recyclerView.adapter != iconAdapter) {
                recyclerView.layoutManager = categoryIconLayout
                recyclerView.adapter = iconAdapter
            }
        }
    }


    private fun setupLayout(category: Category) {
        //Setup the Toolbar
        //collapsingToolbarLayout.title = category.name
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationArgs.category?.image?.let {
                (activity as AppCompatActivity).supportActionBar?.setLogo(it)
            }
            (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
        }
        //Setup background
        try {
            val color =
                navigationArgs.category?.color?.let { ContextCompat.getColor(requireContext(), it) }
            if (color != null) {
                categoryFragmentLayoutBackground.setBackgroundColor(color)
            }
        } catch (exception: Exception) {
            navigationArgs.category?.image?.let {
                categoryFragmentLayoutBackground.setBackgroundResource(it)
            }
        }
    }


    interface OnViewClickedListener {
        fun onAddToDoButtonClicked(categoryName: String)
    }
}
