<?php
// test_db.php

// Nạp thủ công file Database thay vì dùng Composer
require_once __DIR__ . '/app/Core/Database.php';

use App\Core\Database;

try {
    $db = Database::getInstance()->getConnection();
    
    // Thử truy vấn bảng users bạn đã tạo
    $stmt = $db->query("SELECT id, full_name, email, role_id FROM users LIMIT 2");
    $users = $stmt->fetchAll();

    echo "========================================\n";
    echo "  KẾT NỐI DATABASE THÀNH CÔNG RỒI! 🎉 \n";
    echo "========================================\n";
    print_r($users);
} catch (Exception $e) {
    echo "❌ LỖI KẾT NỐI: " . $e->getMessage() . "\n";
}