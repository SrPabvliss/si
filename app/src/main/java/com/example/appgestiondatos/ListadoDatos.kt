package com.example.appgestiondatos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appgestiondatos.database.AppDatabase
import com.example.appgestiondatos.databinding.ActivityListadoDatosBinding
import com.example.appgestiondatos.model.Car
import com.example.appgestiondatos.utils.Constants
import java.util.concurrent.Executors

class ListadoDatos : AppCompatActivity() {
  private lateinit var binding: ActivityListadoDatosBinding
  private val adapter: AdapterCar by lazy {
    AdapterCar()
  }
  private val appDatabase: AppDatabase by lazy {
    AppDatabase.getInstance(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityListadoDatosBinding.inflate(layoutInflater)
    enableEdgeToEdge()
    setContentView(binding.root)

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }
    chargeAdatper()
    chargeData()
    handleEvents()
  }

  private fun handleEvents() {
    binding.fbNuevo.setOnClickListener {
      startActivity(Intent(this, RegistroDatos::class.java))
    }

    adapter.setOnClickListenerCarEdit = {
      val intent = Intent(this, RegistroDatos::class.java)
      intent.putExtra(Constants.KEY_CAR, it)
      startActivity(intent)
    }

    adapter.setOnClickListenerCarDelete = { car ->
      showDeleteConfirmationDialog(car)
    }
  }

  private fun showDeleteConfirmationDialog(car: Car) {
    AlertDialog.Builder(this)
      .setTitle("Eliminar registro")
      .setMessage("¿Estás seguro de que deseas eliminar este registro?")
      .setPositiveButton("Sí") { _, _ ->
        deleteCar(car)
      }
      .setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()
      }
      .create()
      .show()
  }

  private fun deleteCar(car: Car) {
    val executor = Executors.newSingleThreadExecutor()
    executor.execute {
      appDatabase.carDao().delete(car)
      runOnUiThread {
        Toast.makeText(this, "Registro eliminado con éxito", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun chargeData() {
    appDatabase.carDao().getCars().observe(this) {
      adapter.updateListCars(it)
    }
  }

  private fun chargeAdatper() {
    binding.rvListado.adapter = adapter
  }
}