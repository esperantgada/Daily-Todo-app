package eg.esperantgada.dailytodo.fragment.category

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.adapter.CategoryAdapter
import eg.esperantgada.dailytodo.databinding.FragmentCategoryListBinding
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.viewmodel.CategoryViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("NAME_SHADOWING")
@AndroidEntryPoint
class CategoryListFragment : Fragment(), CategoryAdapter.OnItemClickedListener {

    private val categoryViewModel : CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    private var _binding : FragmentCategoryListBinding? = null
    private val binding = _binding!!

   //private lateinit var  listener : OnCategoryItemClickedListener


/*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCategoryItemClickedListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnClicked.")
        }
    }
*/



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCategoryListBinding.inflate(inflater)

        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addCategoryButton.setOnClickListener {
            val category =
                Category(
                    "Untitled",
                    R.drawable.icon_list,
                    R.color.background_color,
                    R.drawable.icon_list )
            categoryViewModel.addCategory(category)
            //listener.onAddCategoryButtonClicked(category)
        }


        categoryAdapter = CategoryAdapter(requireContext(), this)


        binding.categoryRecyclerView.apply {
            setHasFixedSize(true)
            adapter = categoryAdapter
            layoutManager  = StaggeredGridLayoutManager(
                if (resources.configuration.orientation == Configuration
                        .ORIENTATION_PORTRAIT) 2 else 3, StaggeredGridLayoutManager.VERTICAL
            )
        }


        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            categoryViewModel.categoryList.collectLatest {
                categoryAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onItemClicked(category: Category) {

    }

    override fun onDeleteCategoryClicked(category: Category, isClicked: Boolean) {
        MaterialDialog(requireContext()).show {
            title(R.string.delete_category)
            message(R.string.delete_warning)
            positiveButton(R.string.delete){
                categoryViewModel.deleteCategory(category)
            }
            negativeButton(R.string.cancel)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

/*
    interface OnCategoryItemClickedListener {
        fun onAddCategoryButtonClicked(category: Category)

        fun onCategorySelected(category: Category)

    }
*/

}