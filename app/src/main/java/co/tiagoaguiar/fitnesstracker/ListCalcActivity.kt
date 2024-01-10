package co.tiagoaguiar.fitnesstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tiagoaguiar.fitnesstracker.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        val result = mutableListOf<Calc>()
        val adapter = ListCalcAdapter(result)
        rv = findViewById(R.id.rv_list)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val type = intent?.extras?.getString("type")
            ?: throw IllegalStateException("type not found")

        Thread {
            val app = application as App
            val dao = app.db.calcDao()
            val response = dao.getRegisterByType(type)

            runOnUiThread {
                result.addAll(response)
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    private inner class ListCalcAdapter(
        private val calcList: List<Calc>,
    ) :
        RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCalcViewHolder {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ListCalcViewHolder(view)
        }

        override fun onBindViewHolder(holder: ListCalcViewHolder, position: Int) {
            val currentItem = calcList[position]
            holder.bind(currentItem)
        }

        override fun getItemCount(): Int {
            return calcList.size
        }

        private inner class ListCalcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                val textView = itemView as TextView
                val res = item.res
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                val data = sdf.format(item.createdDate)

                textView.text = getString(R.string.list_response, res, data)
            }
        }
    }
}