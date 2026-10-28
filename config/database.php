<?php

return [
    'driver'    => $_ENV['DB_CONNECTION'] ?? 'mysql',
    'host'      => $_ENV['DB_HOST'] ?? '127.0.0.1',
    'port'      => $_ENV['DB_PORT'] ?? '3306',
    'database'  => $_ENV['DB_DATABASE'] ?? $_ENV['DB_NAME'] ?? 'nihongo_app',
    'username'  => $_ENV['DB_USERNAME'] ?? $_ENV['DB_USER'] ?? 'root',
    'password'  => $_ENV['DB_PASSWORD'] ?? $_ENV['DB_PASS'] ?? 'Buingocanh@234',
    'charset'   => 'utf8mb4',
    'collation' => 'utf8mb4_unicode_ci',
    
    // Cấu hình PDO an toàn và tối ưu
    'options'   => [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, // Ném Exception khi có lỗi truy vấn
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,       // Dữ liệu trả về dạng mảng kết hợp (Key => Value)
        PDO::ATTR_EMULATE_PREPARES   => false,                  // Bắt buộc dùng Prepared Statement thật của MySQL (chống SQLi)
        PDO::ATTR_PERSISTENT         => false,                  // Đóng kết nối khi kết thúc request
    ],
];