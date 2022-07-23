package eg.esperantgada.dailytodo.fragment.category

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.adapter.CategoryAdapter
import eg.esperantgada.dailytodo.databinding.FragmentCategoryListBinding
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.viewmodel.CategoryListViewModel

const val CATEGORY_TAG = "CategoryListFragment"

@Suppress("NAME_SHADOWING")
@AndroidEntryPoint
class CategoryListFragment : Fragment(), CategoryAdapter.OnCategoryItemClickedListener {

    private val categoryListViewModel: CategoryListViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(CATEGORY_TAG, "onCreate IS CALLED")

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCategoryListBinding.inflate(inflater)

        Log.d(CATEGORY_TAG, "onCreateView IS CALLED")


        return binding.root

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(CATEGORY_TAG, "onViewCreated IS CALLED")


        binding.addCategoryButton.setOnClickListener {
            val category =
                Category(
                    "Untitled",
                    R.drawable.category_background_1,
                    R.drawable.icon_list)
            categoryListViewModel.addCategory(category)

            try{
                val action =
                    CategoryListFragmentDirections.actionCategoryListFragmentToCategoryTodoFragment(category)
                findNavController().navigate(action)

            }catch(exception: IllegalArgumentException){
                exception.printStackTrace()
            }
        }



        categoryAdapter = CategoryAdapter(requireContext(), this)


        binding.categoryRecyclerView.apply {
            setHasFixedSize(true)
            adapter = categoryAdapter

            layoutManager = StaggeredGridLayoutManager(
                if (resources.configuration.orientation == Configuration
                        .ORIENTATION_PORTRAIT
                ) 2 else 3, StaggeredGridLayoutManager.VERTICAL
            )

        }


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            categoryListViewModel.allCategories.observe(viewLifecycleOwner) {
                categoryAdapter.submitList(it)
                categoryAdapter.notifyDataSetChanged()

                Log.d(CATEGORY_TAG, "LIST OF CATEGORIES : $it")
            }
        }
    }

    override fun onItemClicked(category: Category) {
        try{
            val action =
                CategoryListFragmentDirections.actionCategoryListFragmentToCategoryTodoFragment(category)
            findNavController().navigate(action)

        }catch(exception: IllegalArgumentException){
            exception.printStackTrace()
        }
    }

    override fun onDeleteCategoryClicked(category: Category, isClicked: Boolean) {
        MaterialDialog(requireContext()).show {
            title(R.string.delete_category)
            message(R.string.delete_warning)
            positiveButton(R.string.delete) {
                categoryListViewModel.deleteCategory(category)
            }
            negativeButton(R.string.cancel)
        }

    }

    override fun onPause() {
        super.onPause()

        Log.d(CATEGORY_TAG, "onPause IS CALLED")

    }

    override fun onResume() {
        super.onResume()

        Log.d(CATEGORY_TAG, "onResume IS CALLED")

    }

    override fun onStop() {
        super.onStop()

        Log.d(CATEGORY_TAG, "onStop IS CALLED")

    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.d(CATEGORY_TAG, "onDestroyView IS CALLED")

        _binding = null
    }
}
