package co.tiagoaguiar.fitnesstracker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import co.tiagoaguiar.fitnesstracker.model.Calc

class ImcActivity : AppCompatActivity() {
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc)
        editHeight = findViewById(R.id.edit_imc_height)
        editWeight = findViewById(R.id.edit_imc_weight)

        val btnSend: Button = findViewById(R.id.btn_imc_send)
        btnSend.setOnClickListener {
            if (!validateFields()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imcValue = calculateImc()
            val imcResponseId = imcResponse(imcValue)

            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.imc_response, imcValue))
                setMessage(imcResponseId)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    // do nothing
                }
                setNegativeButton(R.string.save) { dialog, which ->
                    Thread {
                        val app = application as App
                        val dao = app.db.calcDao()

                        dao.insert(Calc(type = "imc", res = imcValue))

                        runOnUiThread {
                            val intent = Intent(this@ImcActivity, ListCalcActivity::class.java)
                            intent.putExtra("type", "imc")
                            startActivity(intent)
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

    @StringRes
    private fun imcResponse(imc: Double): Int {
        return when {
            imc < 15.0 -> R.string.imc_severely_low_weight
            imc < 16.0 -> R.string.imc_very_low_weight
            imc < 18.5 -> R.string.imc_low_weight
            imc < 25.0 -> R.string.normal
            imc < 30.0 -> R.string.imc_high_weight
            imc < 35.0 -> R.string.imc_so_high_weight
            imc < 40.0 -> R.string.imc_severely_high_weight
            else -> R.string.imc_extreme_weight
        }
    }

    private fun calculateImc(): Double {
        val weight = editWeight.text.toString().toDouble()
        val height = editHeight.text.toString().toDouble() / 100.0
        return weight / (height * height)
    }

    private fun validateFields(): Boolean {
        val weight = editWeight.text.toString().toFloatOrNull()
        val height = editHeight.text.toString().toFloatOrNull()
        return weight != null && height != null && weight > 0 && height > 0
    }

}