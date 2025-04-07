<?php
// insert_report.php

require_once 'db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$id_usuario = $data['id_usuario'];
$id_departamento = $data['id_departamento'];
$id_tipo_suelo = $data['id_tipo_suelo'];
$fecha_asignacion = $data['fecha_asignacion'];
$fecha_reparacion = $data['fecha_reparacion'];
$colonia = $data['colonia'];
$direccion = $data['direccion'];
$reportante = $data['reportante'];
$telefono_reportante = $data['telefono_reportante'];
$reparador = $data['reparador'];
$material = $data['material'];
$imagen_antes = base64_decode($data['imagen_antes']);
$imagen_despues = base64_decode($data['imagen_despues']);

$query = "INSERT INTO Reportes (
    Id_ticket, Id_Usuario, Id_Departamento, Id_Tipo_Suelo, Fecha_asignacion, Fecha_reparacion,
    Colonia, Direccion, Reportante, Telefono_reportante, Reparador, Material, Imagen_antes, Imagen_despues
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

$params = array(
    null, $id_usuario, $id_departamento, $id_tipo_suelo, $fecha_asignacion, $fecha_reparacion,
    $colonia, $direccion, $reportante, $telefono_reportante, $reparador, $material,
    $imagen_antes, $imagen_despues
);

$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al insertar reporte: " . print_r(sqlsrv_errors(), true)));
} else {
    echo json_encode(array("message" => "Reporte insertado correctamente"));
}
?>