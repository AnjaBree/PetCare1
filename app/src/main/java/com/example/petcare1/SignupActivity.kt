package com.example.petcare1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petcare1.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {
    // Декларација променљивих за повезивање са UI елементима и Firebase базом
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инфлатирање XML фајла и повезивање са UI елементима преко View Binding-а
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
// Иницијација Firebase базе података
        firebaseDatabase = FirebaseDatabase.getInstance()
        // Постављање референце на део базе података који се односи на кориснике
        databaseReference = firebaseDatabase.reference.child("users")
// Постављање слушаоца за клик на дугме за регистрацију
        binding.signupButton.setOnClickListener {
            // Узимање унетих података из поља за корисничко име и лозинку
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()

            // Проверaва се да ли су оба поља попуњена
            if (signupUsername.isNotEmpty() && signupPassword.isNotEmpty()) {
                // Ако су поља попуњена, позива се функција signupUser да региструје корисника
                signupUser(signupUsername, signupPassword)
            } else {
                // Ако не, приказује се порука да су сва поља обавезна
                Toast.makeText(this@SignupActivity, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            }
        }
        // Постављање слушаоца за клик на линк који води до активности за пријаву
        binding.loginRedirect.setOnClickListener {
            // Покреће LoginActivity и завршава текућу активност
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
    }
    // Функција која врши регистрацију новог корисника у Firebase бази
    private fun signupUser(username: String, password: String) {
        // Пре тражења новог корисника, проверава се да ли корисник већ постоји у бази
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            // Метод који се позива када се подаци из Firebase-а учитају
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Ако подаци не постоје (корисник не постоји)
                if (!dataSnapshot.exists()) {
                    // Генерише се нови ID за корисника
                    val id = databaseReference.push().key
                    // Креирање новог објекта UserData са ID, корисничким именом и лозинком
                    val userData = UserData(id, username, password)
                    // Сетовање новог корисника у базу података
                    databaseReference.child(id!!).setValue(userData)
                    // Приказује се порука о успешном упису
                    Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT).show()
                    // Покреће LoginActivity након што је регистрација успешна
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish() // Завршава тренутну активност
                } else {
                    // Ако корисник већ постоји, приказује се порука да већ постоји тај корисник
                    Toast.makeText(this@SignupActivity, "User already exists", Toast.LENGTH_SHORT).show()
                }
            }

            // Метод који се позива ако се операција неуспешно заврши (нпр. грешка у комуникацији са Firebase-ом)
            override fun onCancelled(databaseError: DatabaseError) {
                // Приказује се порука о грешци при раду са базом
                Toast.makeText(this@SignupActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
