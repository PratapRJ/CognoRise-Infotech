package com.prj.todoapplication
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prj.todoapplication.model.Task
import com.prj.todoapplication.viewmodel.TaskViewModel
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addTaskButton: FloatingActionButton

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()


        // Initialize RecyclerView and Adapter with callbacks
        adapter = RecyclerAdapter(
            this,
            onTaskDelete = { task -> taskViewModel.delete(task) },
            onTaskUpdate = { task -> taskViewModel.update(task) }
        )
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe tasks
        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let { adapter.submitList(it) }
        }

        // Insert task on FAB click
        addTaskButton = findViewById(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_task)

        // Set dialog background to transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val taskTitleInput = dialog.findViewById<EditText>(R.id.taskTitleInput)
        val taskDescriptionInput = dialog.findViewById<EditText>(R.id.taskDescriptionInput)
        val addButton = dialog.findViewById<Button>(R.id.addButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        addButton.setOnClickListener {
            val title = taskTitleInput.text.toString()
            val description = taskDescriptionInput.text.toString()
            if (title.isNotEmpty() && description.isNotEmpty()) {
                val newTask = Task(title = title, description = "Description: "+description, isCompleted = false)
                taskViewModel.insert(newTask)
                dialog.dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ToDo Application"
    }

}
