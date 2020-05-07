package com.demo.toolstracking.feature.toolsList

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.toolstracking.R
import com.demo.toolstracking.feature.ItemClickListener
import com.demo.toolstracking.feature.appToolbarTitle
import com.demo.toolstracking.feature.friendsList.FriendsListActivity
import com.demo.toolstracking.feature.friendsList.FriendsListAdapter
import com.demo.toolstracking.model.Friend
import com.demo.toolstracking.model.Tool
import com.demo.toolstracking.util.showToast
import com.demo.toolstracking.util.visibleIfTrue
import com.demo.toolstracking.viewModel.ToolsTrackingViewModel
import kotlinx.android.synthetic.main.activity_tools.*
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.tools_dialog.view.*

class ToolsListActivity : AppCompatActivity() {

    private lateinit var toolsTrackingViewModel: ToolsTrackingViewModel
    private var toolsList: List<Tool> = listOf()
    private var toolsListAdapter = ToolsListAdapter()

    companion object {
        const val TOOLS = "TOOLS"
        const val REQUEST_CODE = 10101
        const val IS_DATA_UPDATED = "isDataUpdated"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)

        setToolBar(getString(R.string.tools_list))
        toolsTrackingViewModel = ViewModelProviders.of(this).get(ToolsTrackingViewModel::class.java)

        saveData()
        getToolsList()
        setToolsListAdapter()
    }

    private fun setToolBar(title: String) {
        toolbar.appToolbarTitle(title)
        toolbarNavIcon.visibleIfTrue(false)
        btnToolbarAction.visibleIfTrue(true)
        btnToolbarAction.text = getString(R.string.friends)
        btnToolbarAction.setOnClickListener {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    private fun saveData() {
        toolsTrackingViewModel.saveFirstTimeData(this)
    }

    private fun getToolsList() {
        toolsList = toolsTrackingViewModel.getToolsData()
    }

    private fun setToolsListAdapter() {
        toolsListAdapter.setItems(toolsList)
        toolsListAdapter.setToolClickListener(toolItemClickListener)
        recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = toolsListAdapter
    }

    private val toolItemClickListener = object : ItemClickListener {
        override fun onItemClick(position: Int) {
            showFriendsDialog(toolsListAdapter.getItem(position))
        }
    }

    private fun showFriendsDialog(selectedTool: Tool) {
        val view = layoutInflater.inflate(R.layout.tools_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(view)
        val dialog = alertDialogBuilder.create()
        view.loan_tool_text_view.text = getString(R.string.loan_tool, selectedTool.name)
        val friendsList = toolsTrackingViewModel.getFriendsData(this)
        val friendsListAdapter = FriendsListAdapter()
        friendsListAdapter.setItems(friendsList)
        view.friends_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        view.friends_recycler_view.adapter = friendsListAdapter
        var selectedFriend: Friend? = null
        friendsListAdapter.setFriendClickListener(object : ItemClickListener {
            override fun onItemClick(position: Int) {
                for (friend in friendsList) {
                    friend.is_selected = false
                }
                selectedFriend = friendsListAdapter.getItem(position)
                selectedFriend?.is_selected = true
                friendsListAdapter.notifyDataSetChanged()
            }
        })
        view.ok_button.setOnClickListener {
            selectedFriend?.let {
                if (selectedTool.item_count == 0) {
                    showToast(this, getString(R.string.loan_tool_error, selectedTool.name))
                } else {
                    toolsTrackingViewModel.updateToolsData(selectedTool, it, toolsList)
                    showToast(
                        this,
                        getString(R.string.loan_successfully, selectedTool.name, it.name)
                    )
                    refreshList()
                }
            } ?: showToast(this, getString(R.string.select_friend_error))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun refreshList() {
        getToolsList()
        toolsListAdapter.setItems(toolsList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            refreshList()
        }
    }
}