package com.radenmas.smartenergy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {

    private MaterialButton btnLogin;
    private TextInputEditText etEmail, etPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etUsernameLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    Snackbar.make(view, "Email kosong", Snackbar.LENGTH_SHORT).show();
                } else if (pass.isEmpty()) {
                    Snackbar.make(view, "Password kosong", Snackbar.LENGTH_SHORT).show();
                } else if (pass.length() < 8) {
                    Snackbar.make(view, "Password minimal 8 karakter", Snackbar.LENGTH_SHORT).show();
                } else {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(etEmail.getApplicationWindowToken(), 0);
                    manager.hideSoftInputFromWindow(etPassword.getApplicationWindowToken(), 0);

                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_layout);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(this).addOnFailureListener(this);

                }
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        progressDialog.dismiss();
        startActivity(new Intent(LoginActivity.this, Cadangan.class));
        finish();
    }

    @Override
    public void onFailure(Exception e) {
        progressDialog.dismiss();
        Toast.makeText(this, "Login gagal !", Toast.LENGTH_SHORT).show();
    }
}