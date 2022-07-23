package eg.esperantgada.dailytodo.fragment.category

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
import eg.esperantgada.dailytodo.databinding.FragmentCategoryTodoBinding
import eg.esperantgada.dailytodo.event.TodoEvent
import eg.esperantgada.dailytodo.fragment.todo.TAG3
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.repository.SortOrder
import eg.esperantgada.dailytodo.utils.*
import eg.esperantgada.dailytodo.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

const val TAG_C = "CategoryTodoFragment"

@Suppress("DEPRECATION", "KDocUnresolvedReference")
@AndroidEntryPoint
class CategoryTodoFragment : Fragment(), TodoAdapter.OnItemClickedListener {

    private var _binding: FragmentCategoryTodoBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by viewModels()

    private lateinit var todos: List<Todo>

    private lateinit var colorAdapter: ColorAdapter
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var iconAdapter: ImageAdapter
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var categoryBackgroundLayout: LinearLayoutManager
    private lateinit var categoryIconLayout: GridLayoutManager

    private lateinit var recyclerView: RecyclerView

    private val navigationArgs: CategoryTodoFragmentArgs by navArgs()
    private lateinit var searchView: SearchView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().popBackStack()
            }
        })
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryTodoBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()

        if (navigationArgs.category?.categoryName == "Untitled") {
            createNewCategory()
        }

        binding.addTodoButton.setOnClickListener {
            categoryViewModel.addNewTodo()
        }

        todoAdapter = TodoAdapter(requireContext(), this)

        binding.apply {
            categoryListTodoRecyclerview.apply {
                adapter = todoAdapter
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
            }
        }

        //Get a specific category from the database and get its todos

        navigationArgs.category?.categoryName?.let {
            categoryViewModel
                .getAllTodoByCategoryName(it).observe(viewLifecycleOwner) { tasks ->

                    todoAdapter.submitList(tasks)
                    Log.d(TAG3, "TODO LIST SUBMITTED IN TODO FRAGMENT : $tasks")
                }
        }


        //Gets and handles the result of adding or editing from AddEditTodoFragment
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_RESULT_KEY)
            categoryViewModel.onAddEditTodoResult(result)
        }

        /**
         * This will handle the different events action that the ViewModel will send.
         */
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            categoryViewModel.todoEvent.collect { event ->
                when (event) {
                    is TodoEvent.ShowUndoDeleteTodoMessage -> {
                        showSnackBarAndToast(event)
                    }

                    is TodoEvent.GoToAddTodoFragment -> {
                        val action =
                            CategoryTodoFragmentDirections.actionCategoryTodoFragmentToAddEditTodoFragment(
                                null, "Add todo", navigationArgs.category)
                        findNavController().navigate(action)
                    }

                    is TodoEvent.GoToEditFragment -> {
                        val action =
                            CategoryTodoFragmentDirections.actionCategoryTodoFragmentToAddEditTodoFragment(
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
                            CategoryTodoFragmentDirections.actionGlobalAlertDialogueFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        itemTouchHelper.attachToRecyclerView(binding.categoryListTodoRecyclerview)
    }


    //Method that shows SnackBar and Toast messages
    private fun showSnackBarAndToast(event: TodoEvent.ShowUndoDeleteTodoMessage) {
        Snackbar.make(requireView(), "TASK DELETED", Snackbar.LENGTH_LONG)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction("UNDO") {
                categoryViewModel.onUndoDelete(event.todo)
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
        val pendingQuery = categoryViewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchedTodo.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            categoryViewModel.searchQuery.value = it
        }

        //Read the current hideCompleted state from the DataStore
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.hide_completed_todo).isChecked =
                categoryViewModel.preferenceFlow.first().hideCompleted
        }
    }


    /**
     * Handles all logic for [actionBar] menu
     */
    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.sort_by_name -> {
                categoryViewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_by_date_created -> {
                categoryViewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.hide_completed_todo -> {
                item.isChecked = !item.isChecked
                categoryViewModel.onHideCompleted(item.isChecked)
                true
            }

            R.id.delete_all_completed_todo -> {
                categoryViewModel.goToAlertDialogFragment()
                true
            }

            R.id.setting -> {
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
        categoryViewModel.onTodoSelected(todo)
    }


    override fun onCheckBoxClicked(todo: Todo, isChecked: Boolean) {
        categoryViewModel.onTodoCheckedChanged(todo, isChecked, requireActivity())
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
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition

            Collections.swap(todos, fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val todo = todoAdapter.currentList[viewHolder.absoluteAdapterPosition]
            if (todo != null) {
                categoryViewModel.onItemSwiped(todo)
            }
        }
    })



    @SuppressLint("CheckResult")
    private fun createNewCategory() {

        val dialog = MaterialDialog(requireContext()).show {
            title(R.string.create_new_category)
            noAutoDismiss()
            setOnCancelListener {
                requireActivity().supportFragmentManager.popBackStack()
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
                        "Name field cannot be empty !",
                        Toast.LENGTH_SHORT
                    ).show()
                    noAutoDismiss()
                } else {
                    //Already add to listCategory when create fragment
                    //If call mainViewModel.add will leads to add duplicate category
                    navigationArgs.category?.categoryName = categoryName
                    navigationArgs.category?.let { categoryViewModel.addCategory(it) }
                    binding.categoryCollapsetoolbar.title = categoryName
                    navigationArgs.category?.let {
                        (activity as AppCompatActivity).supportActionBar?.setLogo(it.icon)
                    }
                    dismiss()
                }
            }
            negativeButton(R.string.cancel) {
                dismiss()
                navigationArgs.category?.let { it1 -> categoryViewModel.deleteCategory(it1) }
                //requireActivity().supportFragmentManager.popBackStack()
                findNavController().popBackStack()

            }
        }


        colorAdapter = ColorAdapter(CategoryBackground.backgroundColor).apply {
            colorAdapterListener = object : ColorAdapter.OnColorItemClickedListener{
                override fun onItemClicked(color: Int) {
                    val colorId = context?.let { ContextCompat.getColor(it, color) }
                    if (colorId != null) {
                        binding.categoryConstraintLayout.setBackgroundColor(colorId)
                    }
                    navigationArgs.category?.background = color
                }
            }
        }

        imageAdapter = ImageAdapter(CategoryBackground.backgroundImage).apply {
            adapterListener = object : ImageAdapter.OnImageItemClickedListener{
                override fun onItemClicked(imageId: Int) {
                    binding.categoryConstraintLayout.setBackgroundResource(imageId)
                    navigationArgs.category?.background = imageId
                }
            }
        }

        iconAdapter = ImageAdapter(CategoryBackground.backgroundIcon).apply {
            adapterListener = object : ImageAdapter.OnImageItemClickedListener{
                override fun onItemClicked(imageId: Int) {
                    dialog.getCustomView().findViewById<ImageButton>(R.id.insert_icon)
                        .setImageResource(imageId)
                    navigationArgs.category?.icon = imageId
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
        dialog.getCustomView().findViewById<Button>(R.id.background_color).setOnClickListener {
            if (recyclerView.adapter != colorAdapter) {
                recyclerView.layoutManager = categoryBackgroundLayout
                recyclerView.adapter = colorAdapter
            }
        }

        dialog.getCustomView().findViewById<Button>(R.id.background_image).setOnClickListener {
            if (recyclerView.adapter != imageAdapter) {
                recyclerView.layoutManager = categoryBackgroundLayout
                recyclerView.adapter = imageAdapter
            }
        }

        dialog.getCustomView().findViewById<ImageButton>(R.id.insert_icon).setOnClickListener {
            if (recyclerView.adapter != iconAdapter) {
                recyclerView.layoutManager = categoryIconLayout
                recyclerView.adapter = iconAdapter
            }
        }
    }


    private fun setupLayout() {
        //Setup the Toolbar
        binding.categoryCollapsetoolbar.title = navigationArgs.category?.categoryName
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.categoryLayoutToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navigationArgs.category?.icon?.let {
                (activity as AppCompatActivity).supportActionBar?.setLogo(it)
            }
            (activity as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)
        }
        //Setup background
        try {
            val layoutBackground =
                navigationArgs.category?.background?.let { ContextCompat.getColor(requireContext(), it) }
            if (layoutBackground != null) {
                binding.categoryConstraintLayout.setBackgroundColor(layoutBackground)
            }
        } catch (exception: Exception) {
            navigationArgs.category?.background?.let {
                binding.categoryConstraintLayout.setBackgroundResource(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        Log.d(TAG_C, "onPause IS CALLED")

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().popBackStack()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG_C, "onResume IS CALLED")

    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG_C, "onStop IS CALLED")

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })


    }
}
