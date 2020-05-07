package com.demo.toolstracking.feature.toolsList

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.toolstracking.R
import com.demo.toolstracking.feature.ItemClickListener
import com.demo.toolstracking.model.Tool
import com.demo.toolstracking.util.inflate
import com.demo.toolstracking.util.setAvatarImage
import kotlinx.android.synthetic.main.tool_list_item.view.*

class ToolsListAdapter : RecyclerView.Adapter<ToolsListAdapter.ViewHolder>() {

    private var toolsList: List<Tool> = listOf()

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.tool_list_item))

    override fun getItemCount() = toolsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(toolsList[position])
        holder.bindClickListener(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(tool: Tool) {
            with(itemView) {
                tool_name_text_view.text = tool.name
                total_tool_count_text_view.text = tool.item_count.toString()
                borrowed_tool_count_text_view.text = tool.borrowed_count.toString()
                setAvatarImage(context, tool.image_name, avatar_image_view)
            }
        }

        fun bindClickListener(position: Int) {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(position)
            }
        }
    }

    fun setItems(toolsList: List<Tool>) {
        this.toolsList = toolsList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Tool {
        return toolsList[position]
    }

    fun setToolClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}