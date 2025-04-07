package com.example.controlmaterial11;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.controlmaterial11.databinding.ActivityEliminarReporteBinding;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Eliminar_reporteActivity extends AppCompatActivity {
    ActivityEliminarReporteBinding eliminarReporteBinding;
    DBHelper dbHelper; // Instancia de DBHelper
    private int id_ticketActual = -1;  // Variable para almacenar el ID del reporte actual
    private Spinner spinnerDepartamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eliminarReporteBinding = ActivityEliminarReporteBinding.inflate(getLayoutInflater());
        setContentView(eliminarReporteBinding.getRoot());

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Configurar el botón de búsqueda
        eliminarReporteBinding.buttonBuscar.setOnClickListener(v -> {
            // Obtener el ID del ticket ingresado
            String idTicketStr = eliminarReporteBinding.editTextBusqueda.getText().toString().trim();
            if (!idTicketStr.isEmpty()) {
                int id_ticket = Integer.parseInt(idTicketStr);
                buscarYMostrarReporte(id_ticket);
            } else {
                Toast.makeText(Eliminar_reporteActivity.this, "Por favor ingresa un ID de ticket", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de eliminar
        eliminarReporteBinding.btnEliminarReporte.setOnClickListener(v -> mostrarDialogoConfirmacion());

        // Configurar el Spinner de departamentos
        spinnerDepartamento = findViewById(R.id.spinner);
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

    // Método para buscar y mostrar un reporte por ID
    private void buscarYMostrarReporte(int id_ticket) {
        try {
            String response = dbHelper.sendGetRequest(DBHelper.BASE_URL + "get_report_by_id.php?id_ticket=" + id_ticket);
            JSONObject jsonObject = new JSONObject(response);

            id_ticketActual = id_ticket;  // Guardar el ID del ticket actual

            // Extraer y mostrar los datos del reporte
            eliminarReporteBinding.txtTicket.setText(jsonObject.getString("Id_ticket"));
            eliminarReporteBinding.txtFechaAsignacion.setText(jsonObject.getString("Fecha_asignacion"));
            eliminarReporteBinding.txtFechaReparacion.setText(jsonObject.getString("Fecha_reparacion"));
            eliminarReporteBinding.txtColonia.setText(jsonObject.getString("Colonia"));
            eliminarReporteBinding.txtTipoSuelo.setText(jsonObject.getString("Tipo_suelo"));
            eliminarReporteBinding.direccion.setText(jsonObject.getString("Direccion"));
            eliminarReporteBinding.txtReportante.setText(jsonObject.getString("Reportante"));
            eliminarReporteBinding.txtTelReportante.setText(jsonObject.getString("Telefono_reportante"));
            eliminarReporteBinding.txtReparador.setText(jsonObject.getString("Reparador"));
            eliminarReporteBinding.txtMaterial.setText(jsonObject.getString("Material"));

            // Cargar las imágenes si existen
            String imagenAntesBase64 = jsonObject.optString("Imagen_antes", null);
            String imagenDespuesBase64 = jsonObject.optString("Imagen_despues", null);

            if (imagenAntesBase64 != null) {
                byte[] imagenAntesBytes = android.util.Base64.decode(imagenAntesBase64, android.util.Base64.DEFAULT);
                Glide.with(this).load(imagenAntesBytes).into(eliminarReporteBinding.imageViewEvidenciaAntes);
            } else {
                eliminarReporteBinding.imageViewEvidenciaAntes.setImageResource(R.drawable.info);
            }

            if (imagenDespuesBase64 != null) {
                byte[] imagenDespuesBytes = android.util.Base64.decode(imagenDespuesBase64, android.util.Base64.DEFAULT);
                Glide.with(this).load(imagenDespuesBytes).into(eliminarReporteBinding.imageViewEvidenciaDespues);
            } else {
                eliminarReporteBinding.imageViewEvidenciaDespues.setImageResource(R.drawable.info);
            }

            // Seleccionar el valor correcto en el Spinner de departamentos
            String departamento = jsonObject.getString("Departamento");
            int position = ((ArrayAdapter<String>) spinnerDepartamento.getAdapter()).getPosition(departamento);
            if (position >= 0) {
                spinnerDepartamento.setSelection(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Reporte no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para mostrar un cuadro de diálogo de confirmación
    private void mostrarDialogoConfirmacion() {
        if (id_ticketActual != -1) {  // Verificar que hay un reporte seleccionado
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmar eliminación");
            builder.setMessage("¿Deseas eliminar este reporte?");
            builder.setPositiveButton("Sí", (dialog, which) -> eliminarReporte()); // Llamar al método para eliminar el reporte
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            // Mostrar el cuadro de diálogo
            builder.create().show();
        } else {
            Toast.makeText(this, "Primero busca un reporte para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para eliminar el reporte actual
    private void eliminarReporte() {
        if (id_ticketActual != -1) {  // Verificar que hay un reporte seleccionado
            try {
                JSONObject json = new JSONObject();
                json.put("id_ticket", id_ticketActual);

                String response = dbHelper.sendPostRequest(DBHelper.BASE_URL + "delete_report.php", json.toString());
                if (response.contains("correctamente")) {
                    // Limpiar los campos si el reporte fue eliminado
                    eliminarReporteBinding.editTextBusqueda.setText("");
                    eliminarReporteBinding.txtTicket.setText("");
                    eliminarReporteBinding.txtFechaAsignacion.setText("");
                    eliminarReporteBinding.txtFechaReparacion.setText("");
                    eliminarReporteBinding.txtColonia.setText("");
                    eliminarReporteBinding.txtTipoSuelo.setText("");
                    eliminarReporteBinding.direccion.setText("");
                    eliminarReporteBinding.txtReportante.setText("");
                    eliminarReporteBinding.txtTelReportante.setText("");
                    eliminarReporteBinding.txtReparador.setText("");
                    eliminarReporteBinding.txtMaterial.setText("");
                    eliminarReporteBinding.imageViewEvidenciaAntes.setImageResource(0);
                    eliminarReporteBinding.imageViewEvidenciaDespues.setImageResource(0);

                    Toast.makeText(this, "Reporte eliminado exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al eliminar el reporte", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al enviar los datos", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Primero busca un reporte para eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}