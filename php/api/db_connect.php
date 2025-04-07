<?php
// db_connect.php

$serverName = "SISTEMASV"; // Cambia esto por el nombre de tu servidor SQL
$connectionOptions = array(
    "Database" => "Reportes_material", // Nombre de la base de datos
    "Uid" => "sa",            // Usuario de SQL Server
    "PWD" => "6433"          // Contraseña del usuario
);

// Establecer la conexión
$conn = sqlsrv_connect($serverName, $connectionOptions);

if ($conn === false) {
    die(json_encode(array("error" => "Error al conectar a la base de datos: " . print_r(sqlsrv_errors(), true))));
} else
{
    echo $conn;
}
?>