package com.example.pizzacodeapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EditProductsAdminActivity : AppCompatActivity(), View.OnClickListener {

    private var editTextAddProductName: TextInputEditText? = null
    private var editTextAddProductDescription: TextInputEditText? = null
    private var editTextAddProductPrice: TextInputEditText? = null
    private var editTextAddProductNStock: TextInputEditText? = null
    private var editTextAddProductTipo: TextInputEditText? = null

    private var nameItem : String = ""
    private var description : String = ""
    private var price : String = ""
    private var tipoProducts : String = ""
    private var nStock : String = ""

    private var boxProductName: TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_products_admin)

        nameItem = intent.getStringExtra("nameItem")!!
        description = intent.getStringExtra("description")!!
        price = intent.getStringExtra("price")!!
        tipoProducts = intent.getStringExtra("tipoProducts")!!
        nStock = intent.getStringExtra("nStock")!!

        editTextAddProductName = findViewById(R.id.add_product_name) as TextInputEditText
        editTextAddProductDescription = findViewById(R.id.add_product_description) as TextInputEditText
        editTextAddProductPrice = findViewById(R.id.add_product_price) as TextInputEditText
        editTextAddProductNStock = findViewById(R.id.add_product_n_stock) as TextInputEditText
        editTextAddProductTipo = findViewById(R.id.add_product_tipo) as TextInputEditText
        findViewById<View>(R.id.button_add_product).setOnClickListener(this)

        editTextAddProductName?.hint = "Name: " + nameItem
        editTextAddProductDescription?.hint = "Description : " + description
        editTextAddProductPrice?.hint = "Price: " + price
        editTextAddProductNStock?.hint = "nº Stock: " + nStock
        editTextAddProductTipo?.hint = "Tipo: " + tipoProducts
    }


    override fun onClick(v: View?) {
        filterInputs(nameItem, description, price, nStock, tipoProducts)
    }

    private fun filterInputs(name: String, description: String, price: String, nStock: String, tipo: String) {
        var name1 = editTextAddProductName!!.text.toString().trim { it <= ' ' }
        var description1 = editTextAddProductDescription!!.text.toString().trim { it <= ' ' }
        var price1 = editTextAddProductPrice!!.text.toString().trim { it <= ' ' }
        var nStock1 = editTextAddProductNStock!!.text.toString().trim { it <= ' ' }
        var tipo1 = editTextAddProductTipo!!.text.toString().trim { it <= ' ' }

        if (name1.isEmpty()
            && description1.isEmpty()
            && price1.isEmpty()
            && nStock1.isEmpty()
            && tipo1.isEmpty()
        ) {
            Toast.makeText(this, "Preencha um dos campos", Toast.LENGTH_LONG).show()
            return }


        if (description1.isEmpty()) { description1 = description }

        if (price1.isEmpty()) { price1 = price}

        if (nStock1.isEmpty()) { nStock1 = nStock}

        if (tipo1.isEmpty()) { tipo1 = tipo}

        if (name1.isEmpty()) {
            name1 = name
            editProduct(name1, description1, price1, nStock1, tipo1)
        }
        else{
            Log.v("I","Entrando aqui")
            val ref = FirebaseDatabase.getInstance("https://pizza-code-app-default-rtdb.firebaseio.com/").reference
            ref.child("products").child(nameItem).removeValue()
            editProduct(name1, description1, price1, nStock1, tipo1)
        }

        editTextAddProductName!!.setText("")
        editTextAddProductDescription!!.setText("")
        editTextAddProductPrice!!.setText("")
        editTextAddProductNStock!!.setText("")
        editTextAddProductTipo!!.setText("")
    }

    private fun editProduct(name: String, description: String, price: String, nStock: String, tipo: String) {
        val mAuth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance("https://pizza-code-app-default-rtdb.firebaseio.com/").reference
        ref.child("products").child(name).setValue(ItensProducts(
            name,
            description,
            price.toDouble(),
            nStock = nStock.toInt(),
            tipoProducts = tipo
        ))

        editTextAddProductName?.hint = "Name: " + name
        editTextAddProductDescription?.hint = "Description : " + description
        editTextAddProductPrice?.hint = "Price: " + price
        editTextAddProductNStock?.hint = "nº Stock: " + nStock
        editTextAddProductTipo?.hint = "Tipo: " + tipo
    }

}