package com.devst.app;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;



public class HomeActivity extends AppCompatActivity {
    private String emailUsuario = "";
    private TextView tvBienvenida;

    //Funcion para capturar resultados para el perfil
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if(nombre != null){
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //Referencias
        tvBienvenida = findViewById(R.id.tvBienvenida);

        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);

        //Recibir datos desde el login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenida: " + emailUsuario);

        btnIrPerfil.setOnClickListener(View -> {
                Intent perfil = new Intent(HomeActivity.this, PerfilActivity.class);
                perfil.putExtra("email_usuario", emailUsuario);
                editarPerfilLauncher.launch(perfil);
                });

        //EvENTOS iMPLICITOS
        btnAbrirWeb.setOnClickListener(View -> {
            Uri url = Uri.parse("http://www.santotomas.cl");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, url);
            startActivity(viewWeb);
        });

        //Evento implicito correo
        btnEnviarCorreo.setOnClickListener(View -> {
            Intent correo = new Intent(Intent.ACTION_SENDTO);
            correo.setData(Uri.parse("mailto!")); //Solo Correo Electronico
            correo.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            correo.putExtra(Intent.EXTRA_SUBJECT, "Prueba de correo");
            correo.putExtra(Intent.EXTRA_TEXT, "Hola mundo desde el ");
            startActivity(Intent.createChooser(correo, "Enviar Corre: "));
        });

        //Evento Implicito compartir
        btnCompartir.setOnClickListener(View -> {
            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("text/plain");
            compartir.putExtra(Intent.EXTRA_TEXT, "Hola mundo desde android");
            startActivity(Intent.createChooser(compartir, "Compartiendo: "));
        });
    }

}