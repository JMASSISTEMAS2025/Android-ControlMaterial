package com.example.controlmaterial11;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Editar_reporte_Activity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_BEFORE = 1;
    private static final int REQUEST_IMAGE_AFTER = 2;

    private EditText txtColonia, txtDireccion, txtTipoSuelo, txtReportante, txtTelReportante, txtReparador, txtMaterial;
    private ImageView imageViewEvidenciaAntes, imageViewEvidenciaDespues;
    private Spinner spinnerDepartamento;
    private Uri imageUriAntes, imageUriDespues;
    private DBHelper dbHelper;
    private int id_ticketActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reporte);

        // Inicializar vistas
        txtColonia = findViewById(R.id.txt_colonia);
        txtDireccion = findViewById(R.id.direccion);
        txtTipoSuelo = findViewById(R.id.txt_tipo_suelo);
        txtReportante = findViewById(R.id.txt_reportante);
        txtTelReportante = findViewById(R.id.txt_tel_reportante);
        txtReparador = findViewById(R.id.txt_reparador);
        txtMaterial = findViewById(R.id.txt_material);
        imageViewEvidenciaAntes = findViewById(R.id.imageViewEvidencia_antes);
        imageViewEvidenciaDespues = findViewById(R.id.imageViewEvidencia_despues);
        spinnerDepartamento = findViewById(R.id.spinner);

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Configurar botones de selección de imágenes
        imageViewEvidenciaAntes.setOnClickListener(v -> seleccionarImagen(REQUEST_IMAGE_BEFORE));
        imageViewEvidenciaDespues.setOnClickListener(v -> seleccionarImagen(REQUEST_IMAGE_AFTER));

        // Configurar el botón de guardar
        findViewById(R.id.btn_editar_reporte).setOnClickListener(v -> guardarCambiosReporte());

        // Obtener el ID del ticket desde el Intent
        id_ticketActual = getIntent().getIntExtra("id_ticket", -1);
        if (id_ticketActual != -1) {
            buscarYMostrarReporte(id_ticketActual);
        } else {
            Toast.makeText(this, "ID de reporte no válido", Toast.LENGTH_SHORT).show();
        }

        // Cargar departamentos en el Spinner
        cargarDepartamentos();
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

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departamentos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDepartamento.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar departamentos", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para buscar y mostrar un reporte por ID
    private void buscarYMostrarReporte(int id_ticket) {
        try {
            String response = dbHelper.sendGetRequest(DBHelper.BASE_URL + "get_report_by_id.php?id_ticket=" + id_ticket);
            JSONObject jsonObject = new JSONObject(response);

            txtColonia.setText(jsonObject.getString("Colonia"));
            txtDireccion.setText(jsonObject.getString("Direccion"));
            txtTipoSuelo.setText(jsonObject.getString("Tipo_suelo"));
            txtReportante.setText(jsonObject.getString("Reportante"));
            txtTelReportante.setText(jsonObject.getString("Telefono_reportante"));
            txtReparador.setText(jsonObject.getString("Reparador"));
            txtMaterial.setText(jsonObject.getString("Material"));

            // Mostrar imágenes si existen
            String imagenAntesBase64 = jsonObject.optString("Imagen_antes", null);
            String imagenDespuesBase64 = jsonObject.optString("Imagen_despues", null);

            if (imagenAntesBase64 != null) {
                byte[] imagenAntesBytes = Base64.decode(imagenAntesBase64, Base64.DEFAULT);
                Bitmap bitmapAntes = BitmapFactory.decodeByteArray(imagenAntesBytes, 0, imagenAntesBytes.length);
                imageViewEvidenciaAntes.setImageBitmap(bitmapAntes);
            }

            if (imagenDespuesBase64 != null) {
                byte[] imagenDespuesBytes = Base64.decode(imagenDespuesBase64, Base64.DEFAULT);
                Bitmap bitmapDespues = BitmapFactory.decodeByteArray(imagenDespuesBytes, 0, imagenDespuesBytes.length);
                imageViewEvidenciaDespues.setImageBitmap(bitmapDespues);
            }

            // Seleccionar el departamento en el Spinner
            String departamento = jsonObject.getString("Departamento");
            int position = ((ArrayAdapter<String>) spinnerDepartamento.getAdapter()).getPosition(departamento);
            if (position >= 0) {
                spinnerDepartamento.setSelection(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al buscar el reporte", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para guardar los cambios del reporte
    private void guardarCambiosReporte() {
        String colonia = txtColonia.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String tipoSuelo = txtTipoSuelo.getText().toString().trim();
        String reportante = txtReportante.getText().toString().trim();
        String telefonoReportante = txtTelReportante.getText().toString().trim();
        String reparador = txtReparador.getText().toString().trim();
        String material = txtMaterial.getText().toString().trim();
        String departamento = spinnerDepartamento.getSelectedItem().toString();

        // Validar campos obligatorios
        if (colonia.isEmpty() || direccion.isEmpty() || tipoSuelo.isEmpty() || reportante.isEmpty() ||
                telefonoReportante.isEmpty() || reparador.isEmpty() || material.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
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
            json.put("id_ticket", id_ticketActual);
            json.put("colonia", colonia);
            json.put("direccion", direccion);
            json.put("tipo_suelo", tipoSuelo);
            json.put("reportante", reportante);
            json.put("telefono_reportante", telefonoReportante);
            json.put("reparador", reparador);
            json.put("material", material);
            json.put("departamento", departamento);
            json.put("imagen_antes", imagenAntesBase64);
            json.put("imagen_despues", imagenDespuesBase64);

            String response = dbHelper.sendPostRequest(DBHelper.BASE_URL + "update_report.php", json.toString());
            if (response.contains("correctamente")) {
                Toast.makeText(this, "Reporte actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el reporte", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
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
}