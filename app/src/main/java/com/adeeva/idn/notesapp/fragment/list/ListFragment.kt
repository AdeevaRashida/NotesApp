package com.adeeva.idn.notesapp.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.adeeva.idn.notesapp.fragment.adapter.ListAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adeeva.idn.notesapp.R
import com.adeeva.idn.notesapp.data.model.NoteData
import com.adeeva.idn.notesapp.data.viewModelData.NotesViewModel
import com.adeeva.idn.notesapp.databinding.FragmentListBinding
import com.adeeva.idn.notesapp.fragment.SharedViewModels
import com.adeeva.idn.notesapp.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mNotesViewModel : NotesViewModel by viewModels()
    private val adapter : ListAdapter by lazy { ListAdapter() }
    private val mSharedViewModels : SharedViewModels by viewModels()
    private var _listBinding : FragmentListBinding? = null
    private val listBinding get() = _listBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _listBinding = FragmentListBinding.inflate(inflater, container, false)
        listBinding.lifecycleOwner = this
        listBinding.mSharedViewModel = mSharedViewModels

        setUpRecyclerView()

        mNotesViewModel.getAllData.observe(viewLifecycleOwner , { data ->
            mSharedViewModels.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return listBinding.root
    }

    private fun setUpRecyclerView() {
        listBinding.rvList.adapter = adapter
        listBinding.rvList.layoutManager = StaggeredGridLayoutManager (2, StaggeredGridLayoutManager.VERTICAL)
        listBinding.rvList.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(listBinding.rvList)

    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mNotesViewModel.deleteData(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeletedData(viewHolder.itemView, deletedItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: NoteData) {
        val snackBar = Snackbar.make(
            view, "Deleted `${deletedItem.title}`",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            mNotesViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_all -> confirmRemoveAll()
            R.id.menu_priority_high -> mNotesViewModel.sortedByHighPriority.observe(this, {
                adapter.setData(it)
            })
            R.id.menu_priority_low -> mNotesViewModel.sortedByHighPriority.observe(this, {
                adapter.setData(it)
            })

        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoveAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All?")
            .setMessage("Are you sure you want to remove all notes?")
            .setPositiveButton("Yes"){_, _ ->
                mNotesViewModel.deleteAllData()
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed All Notes",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        mNotesViewModel.searchDatabase(searchQuery).observe(
            this, {
                list -> list.let { adapter.setData(it) }
            }
        )
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onDestroy() {
        _listBinding = null
        super.onDestroy()
    }
}