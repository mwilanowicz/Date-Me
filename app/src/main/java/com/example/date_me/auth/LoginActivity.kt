package com.example.date_me.auth

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.date_me.MainActivity
import com.example.date_me.R
import com.example.date_me.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        dialog = AlertDialog.Builder(this)
            .setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()

        binding.sendOTP.setOnClickListener {
            if(binding.userNumber.text.toString().isEmpty()) {
                binding.userNumber.error = "Please enter your number"
            } else {
                sendOTP(binding.userNumber.text.toString())
            }
        }

        binding.verifyOTP.setOnClickListener {
            if(binding.userOTP.text.toString().isEmpty()) {
                binding.userOTP.error = "Please enter your OTP"
            } else {
                verifyOTP(binding.userOTP.text.toString())
            }
        }
    }

    private fun sendOTP(number: String) {
        dialog.show()
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                dialog.dismiss()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dialog.dismiss()
                Toast.makeText(this@LoginActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                this@LoginActivity.verificationId = verificationId
                dialog.dismiss()
                binding.numberLayout.visibility = View.GONE
                binding.otpLayout.visibility = View.VISIBLE
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+48$number")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOTP(otp: String) {
        dialog.show()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkUserExist(binding.userNumber.text.toString())
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, "Sign in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExist(number: String) {
        FirebaseDatabase.getInstance().getReference("users").child(number)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@LoginActivity, "Database error: ${p0.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    dialog.dismiss()
                    if (p0.exists()) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                        finish()
                    }
                }
            })
    }
}

