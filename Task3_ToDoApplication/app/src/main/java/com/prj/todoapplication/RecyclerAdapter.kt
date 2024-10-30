package com.prj.todoapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.prj.todoapplication.model.Task

class RecyclerAdapter(
    private val context: Context,
    private val onTaskDelete: (Task) -> Unit,
    private val onTaskUpdate: (Task) -> Unit
) : ListAdapter<Task, RecyclerAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.todo_task_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)

        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description
        holder.taskDescription.visibility = View.GONE // Initially hide the description
//        holder.checkBox.isChecked = task.isCompleted // Set checkbox status based on the task's completion status

        // Toggle visibility and icon on button click
        holder.addTaskDescriptionButton.setOnClickListener {
            if (holder.taskDescription.visibility == View.GONE) {
                holder.taskDescription.visibility = View.VISIBLE
                holder.addTaskDescriptionButton.setImageResource(R.drawable.ic_up) // Set to up arrow
            } else {
                holder.taskDescription.visibility = View.GONE
                holder.addTaskDescriptionButton.setImageResource(R.drawable.ic_down) // Set to down arrow
            }
        }

//        // Handle checkbox click to mark task as complete/incomplete
//        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
//            val updatedTask = task.copy(isCompleted = isChecked) // Create a new Task with updated status
//            onTaskUpdate(updatedTask) // Callback to update task in database or list
//        }


        // Handle delete button click to remove task
        holder.deleteButton.setOnClickListener {
            onTaskDelete(task) // Callback to delete task
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val addTaskDescriptionButton: ImageButton = itemView.findViewById(R.id.addTaskDescriptionButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
}

// DiffUtil to optimize RecyclerView performance
class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}
