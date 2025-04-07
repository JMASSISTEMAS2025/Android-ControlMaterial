<?php
// update_report.php

require_once 'db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$id_ticket = $data['id_ticket'];
$fecha_reparacion = $data['fecha_reparacion'];
$reparador = $data['reparador'];
$material = $data['material'];
$imagen_despues = base64_decode($data['imagen_despues']);

$query = "UPDATE Reportes SET Fecha_reparacion = ?, Reparador = ?, Material = ?, Imagen_despues = ? WHERE Id_ticket = ?";
$params = array($fecha_reparacion, $reparador, $material, $imagen_despues, $id_ticket);

$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al actualizar reporte: " . print_r(sqlsrv_errors(), true)));
} else {
    echo json_encode(array("message" => "Reporte actualizado correctamente"));
}
?>