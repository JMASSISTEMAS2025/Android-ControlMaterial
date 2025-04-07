<?php
header("Content-Type: application/json");

// Incluir la conexión a la base de datos
include 'db_connect.php';

// Obtener el nombre de usuario desde la solicitud POST
$data = json_decode(file_get_contents('php://input'), true);
$username = $data['usuario'];

// Consultar el ID del usuario
$query = "SELECT Id_Usuario FROM Login WHERE Username = ?";
$params = array($username);
$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta"));
    exit;
}

$row = sqlsrv_fetch_array($stmt, SQLSRV_FETCH_ASSOC);
if ($row) {
    echo json_encode(array("success" => true, "id_usuario" => $row['Id_Usuario']));
} else {
    echo json_encode(array("success" => false, "message" => "Usuario no encontrado"));
}
?>