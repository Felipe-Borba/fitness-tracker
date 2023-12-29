package co.tiagoaguiar.fitnesstracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var rvMain: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainItems = mutableListOf<MainItem>(
            MainItem(
                id = 1,
                drawableId = R.drawable.ic_baseline_wb_sunny_24,
                textStringId = R.string.label_imc,
                color = Color.GREEN
            )
        )

        rvMain = findViewById(R.id.rv_main)
        rvMain.adapter = MainAdapter(mainItems)
        rvMain.layoutManager = LinearLayoutManager(this)
    }

    private inner class MainAdapter(private val mainItems: List<MainItem>) :
        RecyclerView.Adapter<MainViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(R.layout.main_item, parent, false)
            return MainViewHolder(view)
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val currentItem = mainItems[position]
            holder.bind(currentItem)
        }

        override fun getItemCount(): Int {
            return mainItems.size
        }

    }

    private class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MainItem) {
            val img: ImageView = itemView.findViewById(R.id.item_img)
            val name: TextView = itemView.findViewById(R.id.item_txt_name)
            val container = itemView as LinearLayout

            img.setImageResource(item.drawableId)
            name.setText(item.textStringId)
            container.setBackgroundColor(item.color)
        }

    }
}