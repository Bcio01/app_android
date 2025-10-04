package com.devst.app;


import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;



public class HomeActivity extends AppCompatActivity {

    // Variables de bienvenida
    private String emailUsuario = "";
    private TextView tvBienvenida;

    // Linterna y Cámara
    private Button btnLinterna;
    private android.hardware.camera2.CameraManager camara;
    private String camaraID = null;
    private boolean luz = false;


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

    // Pedir permiso de cámara en tiempo de ejecución
    private final androidx.activity.result.ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    alternarluz();
                } else {
                    android.widget.Toast.makeText(this, "Permiso de cámara denegado", android.widget.Toast.LENGTH_SHORT).show();
                }
            });

    private void alternarluz() {
        try {
            luz = !luz;
            camara.setTorchMode(camaraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (camaraID != null && luz) {
            try {
                camara.setTorchMode(camaraID, false);
                luz = false;
                if (btnLinterna != null) btnLinterna.setText("Encender Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }


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
        btnLinterna     = findViewById(R.id.btnLinterna);
        Button btnCamara = findViewById(R.id.btnCamara);

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

        // linterna (flash de la cámara)
        camara = (android.hardware.camera2.CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String id : camara.getCameraIdList()) {
                android.hardware.camera2.CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean tieneFlash = cc.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = cc.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING);
                if (Boolean.TRUE.equals(tieneFlash)
                        && lensFacing != null
                        && lensFacing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK) {
                    camaraID = id; // prioriza cámara trasera
                    break;
                }
            }
        } catch (android.hardware.camera2.CameraAccessException e) {
            android.widget.Toast.makeText(this, "No se puede acceder a la cámara", android.widget.Toast.LENGTH_SHORT).show();
        }
        // Click linterna: toggle con permiso
        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                android.widget.Toast.makeText(this, "No hay flash disponible", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            boolean camGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED;
            if (camGranted) {
                alternarluz();
            } else {
                permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA);
            }
        });

        // Abrir CamaraActivity (explícito)
        btnCamara.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, CamaraActivity.class))
        );
    }

}