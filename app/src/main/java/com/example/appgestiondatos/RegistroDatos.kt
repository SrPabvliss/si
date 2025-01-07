package com.example.appgestiondatos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appgestiondatos.database.AppDatabase
import com.example.appgestiondatos.databinding.ActivityRegistroDatosBinding
import com.example.appgestiondatos.model.Car
import com.example.appgestiondatos.utils.Constants
import java.util.concurrent.Executors

class RegistroDatos : AppCompatActivity() {
  private lateinit var binding: ActivityRegistroDatosBinding
  private val appDatabase: AppDatabase by lazy {
    AppDatabase.getInstance(this)
  }

  private var id = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    binding = ActivityRegistroDatosBinding.inflate(layoutInflater)

    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(binding.root)
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    initializeData()
    handleEvents()
  }

  private fun handleEvents() {
    with(binding) {
      button.setOnClickListener {
        val marca = edtMarca.text.toString()
        val modelo = edtModelo.text.toString()
        val preferencias = edtPreferencias.text.toString()

        if (marca.isNotEmpty() && modelo.isNotEmpty() && preferencias.isNotEmpty()) {
          val car = Car(id, marca, modelo, preferencias)
          if (id == 0) {
            Executors.newSingleThreadExecutor().execute {
              appDatabase.carDao().insert(car)
              runOnUiThread {
                Toast.makeText(this@RegistroDatos, "Registro exitoso", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
              }
            }
          } else {
            Executors.newSingleThreadExecutor().execute {
              appDatabase.carDao().update(car)
              runOnUiThread {
                Toast.makeText(this@RegistroDatos, "Actualización exitosa", Toast.LENGTH_SHORT)
                  .show()
                onBackPressedDispatcher.onBackPressed()
              }
            }
          }
          finish()
        }
      }
    }
  }

  private fun initializeData() {
    val bundle = intent.extras
    bundle?.let {
      val car = bundle.getSerializable(Constants.KEY_CAR) as Car
      with(binding) {
        button.setText("ACTUALIZAR")
        id = car.id
        edtMarca.setText(car.marca)
        edtModelo.setText(car.modelo)
        edtPreferencias.setText(car.preferencias)
      }
    } ?: kotlin.run {
      with(binding) {
        button.setText("REGISTRAR")
        edtMarca.setText("")
        edtModelo.setText("")
        edtPreferencias.setText("")
      }
    }

    binding.edtMarca.requestFocus()
  }
}