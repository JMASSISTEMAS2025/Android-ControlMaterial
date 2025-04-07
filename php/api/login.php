<?php
// login.php

require_once 'db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$usuario = $data['usuario'];
$contraseña = $data['contraseña'];

$query = "SELECT Id_Usuario FROM Login WHERE Usuario = ? AND Contraseña = ?";
$params = array($usuario, $contraseña);

$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al iniciar sesión: " . print_r(sqlsrv_errors(), true)));
} else {
    $row = sqlsrv_fetch_array($stmt, SQLSRV_FETCH_ASSOC);
    if ($row) {
        echo json_encode(array("success" => true, "id_usuario" => $row['Id_Usuario']));
    } else {
        echo json_encode(array("success" => false, "message" => "Credenciales incorrectas"));
    }
}
?>