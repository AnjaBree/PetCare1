package com.example.petcare1

import android.content.Intent
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petcare1.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference /*DatabaseReference: Тип објекта који представља референцу на
     чвор у Firebase Realtime Database-у. Овај објекат се користи за читање или писање података на одређеној локацији у бази.*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root) //се користи за постављање изгледа (UI) активности на основу View објекта који је генерисан преко view binding-а.


        // Иницијализација Firebase базе података и постављање референце на чвор "users".
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        // Подешавање клика на дугме за пријаву.
        binding.loginButton.setOnClickListener {
            // Преузимање унесених података за корисничко име и лозинку.
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()
            // Провера да ли су сва поља попуњена.
            if (loginUsername.isNotEmpty() && loginPassword.isNotEmpty()) {
                // Позива метод за проверу корисничких података у бази.
                loginUser(loginUsername, loginPassword)
            } else {
                // Приказује поруку ако нека поља нису попуњена.
                Toast.makeText(this@LoginActivity, "All fields are mandatory", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Подешавање клика за редирекцију на страницу за регистрацију.
        binding.signupRedirect.setOnClickListener {
            // Прелазак на SignupActivity и завршетак тренутне активности.
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }
    }

    // Метод за проверу корисничког имена и лозинке у бази података.
    private fun loginUser(username: String, password: String) {
        // Претрага базе података по "username".
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // Метод који се позива када подаци постану доступни.
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Провера да ли је пронађен корисник са датим корисничким именом.
                    if (dataSnapshot.exists()) {
                        // Петља кроз све пронађене кориснике.
                        for (userSnapshot in dataSnapshot.children) {
                            // Преузимање корисничких података као објекат класе UserData.
                            val userData = userSnapshot.getValue(UserData::class.java)

                            // Провера да ли је лозинка исправна.
                            if (userData != null && userData.password == password) {
                                // Приказује поруку о успешној пријави и отвара главну активност.
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                                return
                            }
                        }
                    }
                    // Приказује поруку ако пријава није успела.
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                }

                // Метод који се позива ако дође до грешке у приступу бази.
                override fun onCancelled(databaseError: DatabaseError) {
                    // Приказује поруку о грешци из базе.
                    Toast.makeText(
                        this@LoginActivity,
                        "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
