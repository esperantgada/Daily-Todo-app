package eg.esperantgada.dailytodo.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.SettingsActivity
import eg.esperantgada.dailytodo.databinding.FragmentStartBinding


@Suppress("DEPRECATION")
class StartFragment : Fragment() {

    private var _binding : FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            scheduleTaskButton.setOnClickListener {
                if (!findNavController().popBackStack(R.id.categoryListFragment, false)){
                    findNavController()
                        .navigate(
                            StartFragmentDirections
                                .actionStartFragmentToCategoryListFragment(),
                            NavOptions.Builder().setLaunchSingleTop(true).build()
                        )

                }
            }

            takeNoteButton.setOnClickListener {
                val action =
                    StartFragmentDirections.actionStartFragmentToNoteListFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.start_fragement_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.setting){
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
            true
        }else{
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}