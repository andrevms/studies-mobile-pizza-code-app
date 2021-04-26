package com.example.pizzacodeapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddProductsAdmin : AppCompatActivity(), View.OnClickListener {

    private var editTextAddProductName: TextInputEditText? = null
    private var editTextAddProductDescription: TextInputEditText? = null
    private var editTextAddProductPrice: TextInputEditText? = null
    private var editTextAddProductNStock: TextInputEditText? = null
    private var editTextAddProductTipo: TextInputEditText? = null
    private var addProductButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_products_admin)
        supportActionBar?.hide()

        editTextAddProductName = findViewById(R.id.add_product_name) as TextInputEditText
        editTextAddProductDescription = findViewById(R.id.add_product_description) as TextInputEditText
        editTextAddProductPrice = findViewById(R.id.add_product_price) as TextInputEditText
        editTextAddProductNStock = findViewById(R.id.add_product_n_stock) as TextInputEditText
        editTextAddProductTipo = findViewById(R.id.add_product_tipo) as TextInputEditText

        findViewById<View>(R.id.button_add_product).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        filterInputs()
    }

    private fun filterInputs() {
        val name = editTextAddProductName!!.text.toString().trim { it <= ' ' }
        val description = editTextAddProductDescription!!.text.toString().trim { it <= ' ' }
        val price = editTextAddProductPrice!!.text.toString().trim { it <= ' ' }
        val nStock = editTextAddProductNStock!!.text.toString().trim { it <= ' ' }
        val tipo = editTextAddProductTipo!!.text.toString().trim { it <= ' ' }

        if (name.isEmpty()) {
            editTextAddProductName!!.error = "Nome do produto é necessário"
            editTextAddProductName!!.requestFocus()
            return
        }

        if (description.isEmpty()) {
            editTextAddProductDescription!!.error = "Descrição do produto é necessária"
            editTextAddProductDescription!!.requestFocus()
            return
        }

        if (price.isEmpty()) {
            editTextAddProductPrice!!.error = "Preço do produto é necessário"
            editTextAddProductPrice!!.requestFocus()
            return
        }

        if (nStock.isEmpty()) {
            editTextAddProductNStock!!.error = "Quantidade do produto é necessário"
            editTextAddProductNStock!!.requestFocus()
            return
        }

        if (tipo.isEmpty()) {
            editTextAddProductTipo!!.error = "Tipo do produto é necessário"
            editTextAddProductTipo!!.requestFocus()
            return
        }

        addProduct(name, description, price, nStock, tipo)

        editTextAddProductName!!.setText("")
        editTextAddProductDescription!!.setText("")
        editTextAddProductPrice!!.setText("")
        editTextAddProductNStock!!.setText("")
        editTextAddProductTipo!!.setText("")
    }

    private fun addProduct(name: String, description: String, price: String, nStock: String, tipo: String) {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        val ref = FirebaseDatabase.getInstance("https://pizza-code-app-default-rtdb.firebaseio.com/").reference
        ref.child("products").child(name).setValue(ItensProducts(
            name,
            description,
            price.toDouble(),
            nStock = nStock.toInt(),
            tipoProducts = tipo
        ))

        Toast.makeText(this, "Produto Adicionado", Toast.LENGTH_LONG).show()
    }


}