<?php
header("Content-Type: application/json");

// Incluir la conexión a la base de datos
include 'db_connect.php';

try {
    // Obtener los datos del cuerpo de la solicitud POST
    $data = json_decode(file_get_contents('php://input'), true);

    // Validar que los datos contengan las claves 'usuario' y 'clave'
    if (!isset($data['Usuario']) || !isset($data['Contraseña'])) {
        echo json_encode(array("success" => false, "message" => "Datos incompletos"));
        exit;
    }

    // Extraer los valores del JSON
    $usuario = $data['Usuario'];
    $clave = $data['Contraseña'];

    // Validar que los datos no estén vacíos
    if (empty($usuario) || empty($clave)) {
        echo json_encode(array("success" => false, "message" => "Usuario o clave vacíos"));
        exit;
    }

    // Consultar la base de datos para verificar las credenciales
    $query = "SELECT Id_Usuario FROM Login WHERE Usuario = ? AND Contraseña = ?";
    $params = array($usuario, $clave); // Parámetros para la consulta preparada
    $stmt = sqlsrv_query($conn, $query, $params);

    if ($stmt === false) {
        // Si la consulta falla, mostrar el error específico
        $errors = sqlsrv_errors();
        echo json_encode(array("success" => false, "message" => "Error al ejecutar la consulta", "details" => $errors));
        exit;
    }

    // Verificar si se encontró un usuario con las credenciales proporcionadas
    $row = sqlsrv_fetch_array($stmt, SQLSRV_FETCH_ASSOC);
    if ($row) {
        echo json_encode(array("success" => true, "message" => "Inicio de sesión exitoso", "id_usuario" => $row['Id_Usuario']));
    } else {
        echo json_encode(array("success" => false, "message" => "Credenciales incorrectas"));
    }
} catch (Exception $e) {
    // Capturar cualquier excepción y devolver un mensaje de error
    echo json_encode(array("success" => false, "message" => "Error interno: " . $e->getMessage()));
}
?>