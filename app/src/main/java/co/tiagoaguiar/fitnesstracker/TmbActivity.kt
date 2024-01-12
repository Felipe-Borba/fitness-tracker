package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc

class TmbActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var editAge: EditText
    private lateinit var editHeight: EditText
    private lateinit var lifestyle: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmb)

        editHeight = findViewById(R.id.edit_tmb_height)
        editAge = findViewById(R.id.edit_tmb_age)
        editWeight = findViewById(R.id.edit_tmb_weight)

        lifestyle = findViewById(R.id.auto_lifestyle)
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lifestyle.setText(items.first())
        lifestyle.setAdapter(adapter)


        val btnSend: Button = findViewById(R.id.btn_tmb_send)
        btnSend.setOnClickListener {
            if (!validateFields()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val weight = editWeight.text.toString().toFloat()
            val height = editHeight.text.toString().toFloat()
            val age = editAge.text.toString().toInt()

            val result = calculateTmb(weight, height, age)
            val response = tmbLifestyleAdjust(result)


            AlertDialog.Builder(this).apply {
                setMessage(getString(R.string.tmb_response, response))
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    // do nothing
                }
                setNegativeButton(R.string.save) { dialog, which ->
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()

                        dao.insert(Calc(type = "tmb", res = response))

                        runOnUiThread {
                            openListActivity()
                        }
                    }.start()
                }
                create()
                show()
            }

            val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            service.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun tmbLifestyleAdjust(tmb: Double): Double {
        val items = resources.getStringArray(R.array.tmb_lifestyle)
        return when (lifestyle.text.toString()) {
            items[0] -> tmb * 1.2
            items[1] -> tmb * 1.375
            items[2] -> tmb * 1.55
            items[3] -> tmb * 1.725
            items[4] -> tmb * 1.9
            else -> 0.0
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
            finish()
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun openListActivity() {
        val intent = Intent(this, ListCalcActivity::class.java)
        intent.putExtra("type", "tmb")
        startActivity(intent)
    }

    private fun calculateTmb(weight: Float, height: Float, age: Int): Double {
        return 66 + (13.8 * weight) + (5 * height) - (6.8 * age)
    }

    private fun validateFields(): Boolean {
        val weight = editWeight.text.toString().toFloatOrNull()
        val height = editHeight.text.toString().toFloatOrNull()
        val age = editAge.text.toString().toFloatOrNull()
        return weight != null && height != null && weight > 0 && height > 0 && age != null && age > 0
    }
}