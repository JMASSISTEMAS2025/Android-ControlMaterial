package com.example.controlmaterial11;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

public class Registro_Activity extends AppCompatActivity {

    // Declarar las vistas y el botón
    private EditText txtNombreUsuario, txtIdEmpleado, txtClave;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Inicializar las vistas del layout
        txtNombreUsuario = findViewById(R.id.txtnombreusuario);
        txtIdEmpleado = findViewById(R.id.txt_idempleado);
        txtClave = findViewById(R.id.txtclave);
        btnRegistrar = findViewById(R.id.btnregistrar);

        // Configurar la acción del botón "Registrar"
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados en los campos de texto
                String nombreUsuario = txtNombreUsuario.getText().toString().trim();
                String idEmpleado = txtIdEmpleado.getText().toString().trim();
                String clave = txtClave.getText().toString().trim();

                // Verificar si los campos están vacíos
                if (nombreUsuario.isEmpty() || idEmpleado.isEmpty() || clave.isEmpty()) {
                    Toast.makeText(Registro_Activity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Registrar el usuario enviando los datos al servidor
                    registrarUsuario(nombreUsuario, idEmpleado, clave);
                }
            }
        });
    }

    // Método para registrar un usuario enviando los datos al servidor
    private void registrarUsuario(String nombreUsuario, String idEmpleado, String clave) {
        try {
            // Crear un objeto JSON con los datos del usuario
            JSONObject json = new JSONObject();
            json.put("nombre_usuario", nombreUsuario);
            json.put("id_empleado", idEmpleado);
            json.put("clave", clave);

            // Enviar los datos al servidor mediante una solicitud POST
            String response = new DBHelper(this).sendPostRequest(DBHelper.BASE_URL + "register_user.php", json.toString());

            // Analizar la respuesta del servidor
            if (response.contains("correctamente")) {
                Toast.makeText(Registro_Activity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish(); // Finalizar la actividad de registro
            } else {
                Toast.makeText(Registro_Activity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Registro_Activity.this, "Error al comunicarse con el servidor", Toast.LENGTH_SHORT).show();
        }
    }
}
