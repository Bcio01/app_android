package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private EditText edtNombre, edtCorreo, edtPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        edtNombre = findViewById(R.id.edtNombre);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnGuardar = findViewById(R.id.btnGuardarPerfil);
        Button btnAcercaDe = findViewById(R.id.btnAcercaDe);

        btnGuardar.setOnClickListener(v -> {
            String nombre = edtNombre.getText().toString().trim();
            String correo = edtCorreo.getText().toString().trim();
            String pass = edtPassword.getText().toString();

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent data = new Intent();
            data.putExtra("NOMBRE", nombre);
            setResult(RESULT_OK, data);
            finish();
        });

        btnAcercaDe.setOnClickListener(v -> {
            Intent i = new Intent(PerfilActivity.this, AboutActivity.class); // EXPLÍCITO
            startActivity(i);
        });
    }
}
