package com.task.pepultask.data.ui.main.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.pepultask.R
import com.task.pepultask.data.model.response.Data


class ListAdapter(val clickListner: ListClickListner, private val contents: ArrayList<Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val LOADING = 0
    private val ITEM = 1
    private var isLoadingAdded = false


    class DataViewHolder(itemView: View, val clickListner: ListClickListner) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        fun bind(content: Data) {
            if (content.file_type == 0) {
                Glide.with(ivImage.context).load(content.file).into(ivImage)
            } else {

            }
            itemView.setOnLongClickListener(OnLongClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setTitle("Delete!")
                builder.setMessage("Are you sure want to delete this Item")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Yes"){ _, _ ->
                    clickListner.onItemLongClicked(content.id.toString())
                }
                builder.setNegativeButton("No"){dialogInterface, which ->
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
                true
            })

            itemView.setOnClickListener(View.OnClickListener {
                clickListner.onItemClicked(content)
            })
        }
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById<View>(R.id.loadmore_progress) as ProgressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> {
                val viewItem: View = inflater.inflate(R.layout.item_layout, parent, false)
                viewHolder = DataViewHolder(viewItem, clickListner)
            }
            LOADING -> {
                val viewLoading: View = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingViewHolder(viewLoading)
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ITEM -> {
                val dataViewHolder = holder as DataViewHolder
                dataViewHolder.bind(contents[position])
            }
            LOADING -> {
                val loadingViewHolder = holder as LoadingViewHolder
                loadingViewHolder.progressBar.visibility = View.VISIBLE
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == contents.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Data(0, "", 0))
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position: Int = contents.size - 1
        contents.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun add(content: Data) {
        contents.add(content)
        notifyItemInserted(contents.size - 1)
    }

    fun addAll(contentResults: List<Data?>) {
        for (result in contentResults) {
            add(result!!)
        }
    }

//    private fun getItem(position: Int): Content {
//        return contents[position]
//    }


    fun addContents(contents: List<Data>) {
        this.contents.apply {
            clear()
            addAll(contents)
        }

    }

    interface ListClickListner {
        fun onItemClicked(selectedItem: Data)
        fun onItemLongClicked(selectedItemId : String)
    }
}