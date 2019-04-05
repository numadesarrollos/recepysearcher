package com.example.mo2o.extensions

import androidx.recyclerview.widget.RecyclerView


fun <T : RecyclerView.ViewHolder> T.onClick(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}