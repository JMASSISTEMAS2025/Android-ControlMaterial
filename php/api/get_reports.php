<?php
// get_reports.php

require_once 'db_connect.php';

$query = "SELECT * FROM Reportes";
$stmt = sqlsrv_query($conn, $query);

if ($stmt === false) {
    echo json_encode(array("error" => "Error al obtener reportes: " . print_r(sqlsrv_errors(), true)));
} else {
    $reports = array();
    while ($row = sqlsrv_fetch_array($stmt, SQLSRV_FETCH_ASSOC)) {
        $reports[] = $row;
    }
    echo json_encode($reports);
}
?>