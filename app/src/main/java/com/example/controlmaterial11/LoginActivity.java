package com.example.controlmaterial11;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsuario, txtClave;
    private Button btnIngresar, btnRegistrarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Inicializar las vistas
        txtUsuario = findViewById(R.id.txtusuario);
        txtClave = findViewById(R.id.txtclave);
        btnIngresar = findViewById(R.id.btningresar);
        btnRegistrarme = findViewById(R.id.btnregistrarme);

        // Verificar si el usuario ya está logueado
        verificarEstadoSesion();

        // Configurar el botón de inicio de sesión
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = txtUsuario.getText().toString().trim();
                String clave = txtClave.getText().toString().trim();

                // Validar campos
                if (usuario.isEmpty() || clave.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuario.length() < 3 || clave.length() < 3) {
                    Toast.makeText(LoginActivity.this, "El usuario y la clave deben tener al menos 3 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar credenciales
                boolean existeUsuario = verificarCredenciales(usuario, clave);
                if (!existeUsuario) {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón de registro
        btnRegistrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Registro_Activity.class);
                startActivity(intent);
            }
        });
    }

    // Método para verificar credenciales
    private boolean verificarCredenciales(String usuario, String clave) {
        try {
            JSONObject json = new JSONObject();
            json.put("usuario", usuario);
            json.put("contraseña", clave);

            String response = new DBHelper(this).sendPostRequest(DBHelper.BASE_URL + "login.php", json.toString());
            Log.d("LoginActivity", "Respuesta del servidor: " + response);

            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.optBoolean("success", false)) {
                int idUsuario = jsonResponse.getInt("id_usuario");

                // Guardar el ID del usuario y el nombre en SharedPreferences
                guardarEstadoSesion(usuario, idUsuario);

                // Redirigir a la actividad principal
                Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, DrawerBaseActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                String message = jsonResponse.optString("message", "Error desconocido");
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Log.e("LoginActivity", "Error al comunicarse con el servidor: " + e.getMessage());
            Toast.makeText(LoginActivity.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Método para guardar el estado de inicio de sesión en SharedPreferences
    private void guardarEstadoSesion(String usuario, int idUsuario) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // Marcar como logueado
        editor.putString("user_name", usuario); // Guardar el nombre de usuario
        editor.putInt("Id_Usuario", idUsuario); // Guardar el ID del usuario
        editor.apply(); // Guardar los cambios
    }

    // Método para verificar si el usuario ya está logueado
    private void verificarEstadoSesion() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Si el usuario ya está logueado, redirigirlo a la actividad principal
            String usuario = sharedPreferences.getString("user_name", "");
            int idUsuario = sharedPreferences.getInt("Id_Usuario", -1);

            Intent intent = new Intent(LoginActivity.this, DrawerBaseActivity.class);
            intent.putExtra("nombre_usuario", usuario);
            startActivity(intent);
            finish();
        }
    }
}