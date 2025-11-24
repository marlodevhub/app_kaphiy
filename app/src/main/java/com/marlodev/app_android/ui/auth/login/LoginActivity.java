package com.marlodev.app_android.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.admin.AdminMainActivity;
import com.marlodev.app_android.ui.auth.register.RegisterActivity;
import com.marlodev.app_android.ui.barista.BaristaMainActivity;
import com.marlodev.app_android.ui.delivery.DeliveryMainActivity;
import com.marlodev.app_android.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private TextView txtRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initViewModel();

        // Si ya hay sesión activa, saltar el login
        if (sessionManager.isLoggedIn()) {
            navigateToRoleHome(sessionManager.getRole());
            finish();
            return;
        }

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Acción de ir a pantalla de registro
        txtRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtRegistrarse = findViewById(R.id.txtRegistrarse);
        sessionManager = SessionManager.getInstance(this);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getLoginToken().observe(this, token -> {
            progressBar.setVisibility(View.GONE);
            if (token != null) {
                handleLoginSuccess(token);
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        authViewModel.login(email, password);
    }

    private void handleLoginSuccess(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Token inválido");

            byte[] decodedBytes = android.util.Base64.decode(
                    parts[1],
                    android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP
            );

            String payload = new String(decodedBytes);
            JSONObject json = new JSONObject(payload);

            String email = json.optString("email", "");
            JSONArray rolesArray = json.optJSONArray("roles");
            String role = (rolesArray != null && rolesArray.length() > 0)
                    ? rolesArray.getString(0)
                    : "CLIENT";

            sessionManager.saveToken(token);
            sessionManager.saveEmail(email);
            sessionManager.saveRole(role);

            navigateToRoleHome(role);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar token", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToRoleHome(String role) {

        String redirect = getIntent().getStringExtra("redirect");
        Intent intent;

        if ("ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else if ("BARISTA".equalsIgnoreCase(role) || "ROLE_BARISTA".equalsIgnoreCase(role)) {
            intent = new Intent(this, BaristaMainActivity.class);
        } else if ("DELIVERY".equalsIgnoreCase(role) || "ROLE_DELIVERY".equalsIgnoreCase(role)) {
            intent = new Intent(this, DeliveryMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        if ("main".equals(redirect)) {
            intent = new Intent(this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
