package com.demo.toolstracking.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.demo.toolstracking.feature.toolsList.ToolsListActivity
import com.demo.toolstracking.model.Friend
import com.demo.toolstracking.model.Tool
import com.demo.toolstracking.util.FRIENDS_JSON
import com.demo.toolstracking.util.TOOLS_JSON
import com.demo.toolstracking.util.loadJSONFromAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ToolsTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences =
        application.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)

    fun saveFirstTimeData(context: Context) {
        val jsonString = sharedPreferences.getString(ToolsListActivity.TOOLS, null)
        if (jsonString.isNullOrEmpty()) {
            saveToolsList(getToolsJsonData(context))
            val friendsList = getFriendsJsonData(context)
            for (friend in friendsList) {
                saveData(friend.id, Gson().toJson(friend))
            }
        }
    }

    private fun saveToolsList(list: List<Tool>) {
        saveData(ToolsListActivity.TOOLS, Gson().toJson(list))
    }

    private fun saveData(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getToolsJsonData(context: Context): List<Tool> {
        val toolsJsonString = loadJSONFromAsset(context, TOOLS_JSON)
        val arrayTutorialType = object : TypeToken<List<Tool>>() {}.type
        return Gson().fromJson(toolsJsonString, arrayTutorialType)
    }

    private fun getFriendsJsonData(context: Context): List<Friend> {
        val toolsJsonString = loadJSONFromAsset(context, FRIENDS_JSON)
        val arrayTutorialType = object : TypeToken<List<Friend>>() {}.type
        return Gson().fromJson(toolsJsonString, arrayTutorialType)
    }

    fun getToolsData(): List<Tool> {
        val jsonString = sharedPreferences.getString(ToolsListActivity.TOOLS, null)
        val listType = object : TypeToken<List<Tool>>() {}.type
        return Gson().fromJson<List<Tool>>(jsonString, listType)
    }

    fun getFriendsData(context: Context): List<Friend> {
        val friendListData: ArrayList<Friend> = ArrayList()
        val friendsList = getFriendsJsonData(context)
        for (friend in friendsList) {
            val jsonString = sharedPreferences.getString(friend.id, null)
            val type = object : TypeToken<Friend>() {}.type
            friendListData.add(Gson().fromJson<Friend>(jsonString, type))
        }
        return friendListData
    }

    fun updateToolsData(selectedTool: Tool, selectedFriend: Friend, toolsList: List<Tool>) {
        val toolsMutableList: MutableList<Tool> = toolsList.toMutableList()
        for (tool in toolsMutableList) {
            if (tool.id == selectedTool.id) {
                tool.item_count--
                tool.borrowed_count++
                if (selectedFriend.borrowed_tools == null)
                    selectedFriend.borrowed_tools = ArrayList()
                var isPresent = false
                for (borrowedTool in selectedFriend.borrowed_tools) {
                    if (borrowedTool.id == tool.id) {
                        borrowedTool.borrowed_count++
                        isPresent = true
                        break
                    }
                }
                if (!isPresent) {
                    val friendTool =
                        Tool(tool.id, tool.name, tool.item_count, 1, tool.image_name, false)
                    friendTool.borrowed_count = 1
                    selectedFriend.borrowed_tools.add(friendTool)
                }
                selectedFriend.is_selected = false
                break
            }
        }
        saveToolsList(toolsMutableList)
        saveData(selectedFriend.id, Gson().toJson(selectedFriend))
    }

    fun updateFriendsData(selectedFriend: Friend, selectedTool: Tool) {
        // Update friends tool data
        for (tool in selectedFriend.borrowed_tools) {
            if(tool.id == selectedTool.id) {
                if (tool.borrowed_count > 1)
                    tool.borrowed_count--
                else
                    selectedFriend.borrowed_tools.remove(tool)
                break
            }
        }
        saveData(selectedFriend.id, Gson().toJson(selectedFriend))

        // Update tools data
        val toolsMutableList = getToolsData().toMutableList()
        for (tool in toolsMutableList) {
            if (tool.id == selectedTool.id) {
                tool.borrowed_count--
                tool.item_count++
                break
            }
        }
        saveToolsList(toolsMutableList)
    }

    fun getBorrowedToolsList(friendId: String): ArrayList<Tool> {
        val jsonString = sharedPreferences.getString(friendId, null)
        val type = object : TypeToken<Friend>() {}.type
        return Gson().fromJson<Friend>(jsonString, type).borrowed_tools
    }
}