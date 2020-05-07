package com.demo.toolstracking.feature.friendsList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.toolstracking.R
import com.demo.toolstracking.feature.ItemClickListener
import com.demo.toolstracking.feature.appToolbarTitle
import com.demo.toolstracking.feature.toolsList.ToolsListActivity.Companion.IS_DATA_UPDATED
import com.demo.toolstracking.model.Friend
import com.demo.toolstracking.model.Tool
import com.demo.toolstracking.util.showToast
import com.demo.toolstracking.util.visibleIfTrue
import com.demo.toolstracking.viewModel.ToolsTrackingViewModel
import kotlinx.android.synthetic.main.activity_tools.*
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.tools_dialog.view.*

class FriendsListActivity : AppCompatActivity() {

    private lateinit var toolsTrackingViewModel: ToolsTrackingViewModel
    private var friendsList: List<Friend> = listOf()
    private var friendsListAdapter = FriendsListAdapter()
    private var isDataUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        setToolBar(getString(R.string.friends))
        toolsTrackingViewModel = ViewModelProviders.of(this).get(ToolsTrackingViewModel::class.java)

        getFriendsList()
        setFriendsListAdapter()
    }

    private fun setToolBar(title: String) {
        toolbar.appToolbarTitle(title)
        btnToolbarAction.visibleIfTrue(false)
        toolbarNavIcon.visibleIfTrue(true)
        toolbarNavIcon.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getFriendsList() {
        friendsList = toolsTrackingViewModel.getFriendsData(this)
    }

    private fun setFriendsListAdapter() {
        friendsListAdapter.setItems(friendsList)
        friendsListAdapter.setFriendClickListener(friendItemClickListener)
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = friendsListAdapter
    }

    private val friendItemClickListener = object : ItemClickListener {
        override fun onItemClick(position: Int) {
            val selectedFriend = friendsListAdapter.getItem(position)
            val toolsList = toolsTrackingViewModel.getBorrowedToolsList(selectedFriend.id)
            if (toolsList != null && toolsList.size > 0)
                showToolsDialog(toolsList, selectedFriend)
            else showToast(
                applicationContext,
                getString(R.string.not_borrowed_tool, selectedFriend.name)
            )
        }
    }

    private fun showToolsDialog(toolsList: ArrayList<Tool>, selectedFriend: Friend) {
        val view = layoutInflater.inflate(R.layout.tools_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)
        val dialog = alertDialogBuilder.create()
        view.loan_tool_text_view.text = getString(R.string.tools_loaned, selectedFriend.name)
        view.ok_button.text = getString(R.string.close)
        var isItemSelected = false
        val friendToolsListAdapter = FriendToolsListAdapter()
        friendToolsListAdapter.setItems(toolsList)
        view.friends_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (toolsList.size > 5) {
            val params = view.friends_recycler_view.layoutParams
            params.height = 800
            view.friends_recycler_view.layoutParams = params
        }
        view.friends_recycler_view.adapter = friendToolsListAdapter
        var selectedTool: Tool? = null
        friendToolsListAdapter.setToolClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                for (tool in toolsList) {
                    tool.is_selected = false
                }
                selectedTool = friendToolsListAdapter.getItem(position)
                selectedTool?.let {
                    it.is_selected = true
                    isItemSelected = true
                    view.loan_tool_text_view.text = getString(R.string.mark_return, it.name)
                    view.ok_button.text = getString(R.string.ok)
                    friendToolsListAdapter.notifyDataSetChanged()
                }
            }
        })
        view.ok_button.setOnClickListener {
            if (isItemSelected && selectedTool != null) {
                toolsTrackingViewModel.updateFriendsData(selectedFriend, selectedTool!!)
                showToast(
                    this,
                    getString(
                        R.string.return_successfully,
                        selectedTool!!.name,
                        selectedFriend.name
                    )
                )
                refreshList()
                dialog.dismiss()
            } else
                dialog.dismiss()
        }
        dialog.show()
    }

    private fun refreshList() {
        isDataUpdated = true
        getFriendsList()
        friendsListAdapter.setItems(friendsList)
    }

    override fun onBackPressed() {
        if (isDataUpdated) {
            val data = Intent()
            data.putExtra(IS_DATA_UPDATED, true)
            setResult(Activity.RESULT_OK, data)
        }
        super.onBackPressed()
    }
}