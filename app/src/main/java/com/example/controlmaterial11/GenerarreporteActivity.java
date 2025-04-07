package com.example.controlmaterial11;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GenerarReporteActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_BEFORE = 1;
    private static final int REQUEST_IMAGE_AFTER = 2;

    private EditText txtTicket, txtFechaAsignacion, txtFechaReparacion, txtColonia, txtDireccion,
            txtTipoSuelo, txtReportante, txtTelReportante, txtReparador, txtMaterial;
    private Spinner spinnerDepartamento;
    private ImageView imageViewEvidenciaAntes, imageViewEvidenciaDespues;
    private Button btnSeleccionarImagenAntes, btnSeleccionarImagenDespues, btnGenerarReporte;
    private DBHelper dbHelper;

    private Uri imageUriAntes, imageUriDespues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generarreporte);

        // Inicializar vistas
        txtTicket = findViewById(R.id.txt_ticket);
        txtFechaAsignacion = findViewById(R.id.txt_fecha_asignacion);
        txtFechaReparacion = findViewById(R.id.txt_fecha_reparacion);
        txtColonia = findViewById(R.id.txt_colonia);
        txtDireccion = findViewById(R.id.direccion);
        txtTipoSuelo = findViewById(R.id.txt_tipo_suelo);
        txtReportante = findViewById(R.id.txt_reportante);
        txtTelReportante = findViewById(R.id.txt_tel_reportante);
        txtReparador = findViewById(R.id.txt_reparador);
        txtMaterial = findViewById(R.id.txt_material);
        spinnerDepartamento = findViewById(R.id.spinner);
        imageViewEvidenciaAntes = findViewById(R.id.imageViewEvidencia_antes);
        imageViewEvidenciaDespues = findViewById(R.id.imageViewEvidencia_despues);
        btnSeleccionarImagenAntes = findViewById(R.id.btn_seleccionar_imagen_antes);
        btnSeleccionarImagenDespues = findViewById(R.id.btn_seleccionar_imagen_despues);
        btnGenerarReporte = findViewById(R.id.btn_generar_reporte);

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Cargar departamentos en el Spinner
        cargarDepartamentos();

        // Configurar botones de selección de imágenes
        btnSeleccionarImagenAntes.setOnClickListener(v -> seleccionarImagen(REQUEST_IMAGE_BEFORE));
        btnSeleccionarImagenDespues.setOnClickListener(v -> seleccionarImagen(REQUEST_IMAGE_AFTER));

        // Configurar el botón de generar reporte
        btnGenerarReporte.setOnClickListener(v -> generarReporte());
    }

    // Método para cargar departamentos en el Spinner
    private void cargarDepartamentos() {
        try {
            String response = dbHelper.sendGetRequest(DBHelper.BASE_URL + "get_departments.php");
            JSONArray jsonArray = new JSONArray(response);

            List<String> departamentos = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                departamentos.add(jsonObject.getString("Nombre_Departamento"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    departamentos
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDepartamento.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar departamentos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar una imagen
    private void seleccionarImagen(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (requestCode == REQUEST_IMAGE_BEFORE) {
                imageUriAntes = selectedImage;
                Glide.with(this).load(imageUriAntes).into(imageViewEvidenciaAntes);
            } else if (requestCode == REQUEST_IMAGE_AFTER) {
                imageUriDespues = selectedImage;
                Glide.with(this).load(imageUriDespues).into(imageViewEvidenciaDespues);
            }
        }
    }

    // Método para generar un nuevo reporte
    private void generarReporte() {
        String idUsuario = "1"; // Cambia esto por el ID del usuario logueado
        String fechaAsignacion = txtFechaAsignacion.getText().toString().trim();
        String fechaReparacion = txtFechaReparacion.getText().toString().trim();
        String colonia = txtColonia.getText().toString().trim();
        String tipoSuelo = txtTipoSuelo.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String reportante = txtReportante.getText().toString().trim();
        String telefonoReportante = txtTelReportante.getText().toString().trim();
        String reparador = txtReparador.getText().toString().trim();
        String material = txtMaterial.getText().toString().trim();
        String departamento = spinnerDepartamento.getSelectedItem().toString();

        // Validar campos obligatorios
        if (fechaAsignacion.isEmpty() || fechaReparacion.isEmpty() || colonia.isEmpty() ||
                tipoSuelo.isEmpty() || direccion.isEmpty() || reportante.isEmpty() ||
                telefonoReportante.isEmpty() || reparador.isEmpty() || material.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir imágenes a Base64
        String imagenAntesBase64 = null;
        String imagenDespuesBase64 = null;

        if (imageUriAntes != null) {
            try {
                byte[] imagenAntesBytes = dbHelper.reducirImagen(imageUriAntes, this);
                imagenAntesBase64 = Base64.encodeToString(imagenAntesBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al procesar la imagen 'antes'", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (imageUriDespues != null) {
            try {
                byte[] imagenDespuesBytes = dbHelper.reducirImagen(imageUriDespues, this);
                imagenDespuesBase64 = Base64.encodeToString(imagenDespuesBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al procesar la imagen 'después'", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Enviar datos al servidor
        try {
            JSONObject json = new JSONObject();
            json.put("id_usuario", idUsuario);
            json.put("fecha_asignacion", fechaAsignacion);
            json.put("fecha_reparacion", fechaReparacion);
            json.put("colonia", colonia);
            json.put("tipo_suelo", tipoSuelo);
            json.put("direccion", direccion);
            json.put("reportante", reportante);
            json.put("telefono_reportante", telefonoReportante);
            json.put("reparador", reparador);
            json.put("material", material);
            json.put("departamento", departamento);
            json.put("imagen_antes", imagenAntesBase64);
            json.put("imagen_despues", imagenDespuesBase64);

            String response = dbHelper.sendPostRequest(DBHelper.BASE_URL + "insert_report.php", json.toString());
            if (response.contains("correctamente")) {
                Toast.makeText(this, "Reporte generado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            } else {
                Toast.makeText(this, "Error al generar el reporte", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para limpiar los campos después de generar un reporte
    private void limpiarCampos() {
        txtFechaAsignacion.setText("");
        txtFechaReparacion.setText("");
        txtColonia.setText("");
        txtDireccion.setText("");
        txtTipoSuelo.setText("");
        txtReportante.setText("");
        txtTelReportante.setText("");
        txtReparador.setText("");
        txtMaterial.setText("");
        imageViewEvidenciaAntes.setImageResource(0);
        imageViewEvidenciaDespues.setImageResource(0);
        imageUriAntes = null;
        imageUriDespues = null;
    }
}