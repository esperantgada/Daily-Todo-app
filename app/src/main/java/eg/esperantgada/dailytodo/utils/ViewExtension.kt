package eg.esperantgada.dailytodo.utils

import android.widget.SearchView

/**
 * Extension function that handles [SearchView] action
 */

inline fun androidx.appcompat.widget.SearchView.onQueryTextChanged(crossinline listener : (String) -> (Unit)){
    this.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            listener(p0.orEmpty())
            return true
        }

    })
}