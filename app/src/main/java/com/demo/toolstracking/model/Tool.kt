package com.demo.toolstracking.model

data class Tool(
    var id: String,
    var name: String,
    var item_count: Int,
    var borrowed_count: Int,
    var image_name: String,
    var is_selected: Boolean
)