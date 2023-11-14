package com.example.mymaze

import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.app.AppCompatActivity


class MazeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maze)

        // Create an instance of MazeActivity2
        val mazeView = MazeActivity2(this, null)

        // Set layout parameters to make it full scree

        // Set the content view to the MazeActivity2 instance
        setContentView(mazeView)


        // You can interact with MazeActivity2 here if needed
    }
}
