<?php
// delete_report.php

require_once 'db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$id_ticket = $data['id_ticket'];

$query = "DELETE FROM Reportes WHERE Id_ticket = ?";
$params = array($id_ticket);

$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al eliminar reporte: " . print_r(sqlsrv_errors(), true)));
} else {
    echo json_encode(array("message" => "Reporte eliminado correctamente"));
}
?>