package com.demo.toolstracking.feature.friendsList

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.demo.toolstracking.R
import com.demo.toolstracking.feature.ItemClickListener
import com.demo.toolstracking.model.Friend
import com.demo.toolstracking.util.inflate
import kotlinx.android.synthetic.main.friends_list_item.view.*

class FriendsListAdapter : RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    private var friendList: List<Friend> = listOf()

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.friends_list_item))

    override fun getItemCount() = friendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(friendList[position])
        holder.bindClickListener(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(friend: Friend) {
            with(itemView) {
                friend_name_text_view.text = friend.name

                if (friend.is_selected) {
                    friend_name_text_view.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white_color
                        )
                    )
                    friend_view.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    friend_name_text_view.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.primary_text_color
                        )
                    )
                    friend_view.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white_color
                        )
                    )
                }
            }
        }

        fun bindClickListener(position: Int) {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(position)
            }
        }
    }

    fun setItems(toolsList: List<Friend>) {
        this.friendList = toolsList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Friend {
        return friendList[position]
    }

    fun setFriendClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}