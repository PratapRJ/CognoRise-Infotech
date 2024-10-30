package com.prj.todoapplication.model

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: Task): Long  // Return Long, as Room can handle this

    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Update
    fun update(task: Task): Int // Return Int

    @Delete
    fun delete(task: Task): Int // Return Int
}

