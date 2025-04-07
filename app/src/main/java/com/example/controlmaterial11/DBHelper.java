package com.example.controlmaterial11;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    public static final String BASE_URL = "http://192.168.1.235/api/"; // Cambia esto por tu URL base

    public DBHelper(Context context) {
        // Constructor vacío, ya que no usamos SQLite
    }

    // Método para reducir el tamaño de una imagen
    public byte[] reducirImagen(Uri imageUri, Context context) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        int nuevoAncho = 900; // Cambia a un tamaño deseado
        int nuevoAlto = 900; // Cambia a un tamaño deseado
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, nuevoAncho, nuevoAlto, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); // Comprimir a un 50% de calidad
        return stream.toByteArray();
    }

    // Método para insertar un reporte
    public boolean insertarReporte(int id_usuario, String fecha_asignacion, String fecha_reparacion, String colonia,
                                   String tipo_suelo, String direccion, String reportante, String telefono_reportante,
                                   String reparador, String material, Uri imagenAntesUri, Uri imagenDespuesUri, Context context) {
        try {
            JSONObject json = new JSONObject();
            json.put("id_usuario", id_usuario);
            json.put("fecha_asignacion", fecha_asignacion);
            json.put("fecha_reparacion", fecha_reparacion);
            json.put("colonia", colonia);
            json.put("tipo_suelo", tipo_suelo);
            json.put("direccion", direccion);
            json.put("reportante", reportante);
            json.put("telefono_reportante", telefono_reportante);
            json.put("reparador", reparador);
            json.put("material", material);

            if (imagenAntesUri != null) {
                byte[] imagenAntes = reducirImagen(imagenAntesUri, context);
                json.put("imagen_antes", Base64.encodeToString(imagenAntes, Base64.DEFAULT));
            }
            if (imagenDespuesUri != null) {
                byte[] imagenDespues = reducirImagen(imagenDespuesUri, context);
                json.put("imagen_despues", Base64.encodeToString(imagenDespues, Base64.DEFAULT));
            }

            String response = sendPostRequest(BASE_URL + "insert_report.php", json.toString());
            return response.contains("correctamente");
        } catch (Exception e) {
            Log.e("DBHelper", "Error al insertar reporte: " + e.getMessage());
            return false;
        }
    }

    // Método para verificar un usuario
    public boolean verificarUsuario(String usuario, String clave) {
        try {
            JSONObject json = new JSONObject();
            json.put("Usuario", usuario);
            json.put("Contraseña", clave);

            String response = sendPostRequest(BASE_URL + "login.php", json.toString());
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.optBoolean("success", false);
        } catch (Exception e) {
            Log.e("DBHelper", "Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método para obtener todos los reportes
    public List<Reporte> obtenerTodosLosReportes() {
        List<Reporte> reportes = new ArrayList<>();
        try {
            String response = sendGetRequest(BASE_URL + "get_reports.php");
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Reporte reporte = new Reporte(
                        jsonObject.getString("Id_ticket"),
                        jsonObject.getString("Colonia"),
                        jsonObject.getString("Direccion")
                );
                reportes.add(reporte);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error al obtener reportes: " + e.getMessage());
        }
        return reportes;
    }

    // Método para eliminar un reporte
    public boolean eliminarReporte(int id_ticket) {
        try {
            JSONObject json = new JSONObject();
            json.put("id_ticket", id_ticket);

            String response = sendPostRequest(BASE_URL + "delete_report.php", json.toString());
            return response.contains("correctamente");
        } catch (Exception e) {
            Log.e("DBHelper", "Error al eliminar reporte: " + e.getMessage());
            return false;
        }
    }

    // Método para actualizar un reporte
    public boolean actualizarReporte(int id_ticket, String departamento, String colonia, String direccion,
                                     String reportante, String telefonoReportante, String tipoSuelo,
                                     String reparador, String material, String fechaAsignacion,
                                     String fechaReparacion, Uri imagenAntesUri, Uri imagenDespuesUri, Context context) {
        try {
            JSONObject json = new JSONObject();
            json.put("id_ticket", id_ticket);
            json.put("departamento", departamento);
            json.put("colonia", colonia);
            json.put("direccion", direccion);
            json.put("reportante", reportante);
            json.put("telefono_reportante", telefonoReportante);
            json.put("tipo_suelo", tipoSuelo);
            json.put("reparador", reparador);
            json.put("material", material);
            json.put("fecha_asignacion", fechaAsignacion);
            json.put("fecha_reparacion", fechaReparacion);

            if (imagenAntesUri != null) {
                byte[] imagenAntes = reducirImagen(imagenAntesUri, context);
                json.put("imagen_antes", Base64.encodeToString(imagenAntes, Base64.DEFAULT));
            }
            if (imagenDespuesUri != null) {
                byte[] imagenDespues = reducirImagen(imagenDespuesUri, context);
                json.put("imagen_despues", Base64.encodeToString(imagenDespues, Base64.DEFAULT));
            }

            String response = sendPostRequest(BASE_URL + "update_report.php", json.toString());
            return response.contains("correctamente");
        } catch (Exception e) {
            Log.e("DBHelper", "Error al actualizar reporte: " + e.getMessage());
            return false;
        }
    }

    // Método para enviar una solicitud POST
    public String sendPostRequest(String urlString, String jsonInputString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            Log.d("DBHelper", "URL: " + urlString); // Log de la URL
            Log.d("DBHelper", "Datos enviados: " + jsonInputString); // Log de los datos enviados

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Enviar los datos JSON al servidor
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta del servidor
            int responseCode = connection.getResponseCode();
            Log.d("DBHelper", "Código de respuesta HTTP: " + responseCode); // Log del código de respuesta

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    String response = readStream(is);
                    Log.d("DBHelper", "Respuesta del servidor: " + response); // Log de la respuesta
                    return response;
                }
            } else {
                Log.e("DBHelper", "Código de error HTTP: " + responseCode);
                return "";
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error en la solicitud POST: " + e.getMessage());
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método para enviar una solicitud GET
    public String sendGetRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    return readStream(is);
                }
            } else {
                Log.e("DBHelper", "Error en la solicitud GET: Código " + responseCode);
                return "";
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error en la solicitud GET: " + e.getMessage());
            return "";
        }
    }

    // Método para leer el contenido de un InputStream
    private String readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // Buffer de 1 KB
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8"); // Convertir a String usando UTF-8
    }
}