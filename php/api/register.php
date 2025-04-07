<?php
// register.php

require_once 'db_connect.php';

$data = json_decode(file_get_contents('php://input'), true);

$usuario = $data['usuario'];
$contraseña = $data['contraseña'];

$query = "INSERT INTO Login (Usuario, Contraseña) VALUES (?, ?)";
$params = array($usuario, $contraseña);

$stmt = sqlsrv_query($conn, $query, $params);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al registrar usuario: " . print_r(sqlsrv_errors(), true)));
} else {
    echo json_encode(array("message" => "Usuario registrado correctamente"));
}
?>