<?php

namespace App\Core;

use PDO;
use PDOException;
use RuntimeException;

class Database
{
    private static ?Database $instance = null;
    private PDO $pdo;

    /**
     * Khởi tạo kết nối ẩn (Private Constructor)
     */
    private function __construct()
    {
        // Đường dẫn tới file config/database.php
        $configPath = dirname(__DIR__, 2) . '/config/database.php';

        if (!file_exists($configPath)) {
            throw new RuntimeException("Tệp cấu hình cơ sở dữ liệu không tồn tại tại: {$configPath}");
        }

        $config = require $configPath;

        $dsn = sprintf(
            '%s:host=%s;port=%s;dbname=%s;charset=%s',
            $config['driver'],
            $config['host'],
            $config['port'],
            $config['database'],
            $config['charset']
        );

        try {
            $this->pdo = new PDO(
                $dsn,
                $config['username'],
                $config['password'],
                $config['options'] ?? []
            );
        } catch (PDOException $e) {
            // Khi gặp lỗi kết nối, ẩn mật khẩu và thông tin nhạy cảm
            throw new RuntimeException("Không thể kết nối đến Cơ sở dữ liệu: " . $e->getMessage(), (int)$e->getCode());
        }
    }

    /**
     * Lấy thực thể duy nhất của Database (Singleton)
     */
    public static function getInstance(): self
    {
        if (self::$instance === null) {
            self::$instance = new self();
        }
        return self::$instance;
    }

    /**
     * Lấy đối tượng PDO gốc để thao tác
     */
    public function getConnection(): PDO
    {
        return $this->pdo;
    }

    /**
     * Ngăn chặn việc nhân bản (clone) đối tượng
     */
    private function __clone() {}

    /**
     * Ngăn chặn việc unserialize đối tượng
     */
    public function __wakeup()
    {
        throw new RuntimeException("Không thể khôi phục (unserialize) lớp Singleton Database.");
    }

    /* ---------------------------------------------------------
     * CÁC HÀM TIỆN ÍCH DÙNG NHANH CHO TRANSACTION & TRUY VẤN
     * --------------------------------------------------------- */

    public function prepare(string $sql)
    {
        return $this->pdo->prepare($sql);
    }

    public function query(string $sql)
    {
        return $this->pdo->query($sql);
    }

    public function lastInsertId(): string
    {
        return $this->pdo->lastInsertId();
    }

    public function beginTransaction(): bool
    {
        return $this->pdo->beginTransaction();
    }

    public function commit(): bool
    {
        return $this->pdo->commit();
    }

    public function rollBack(): bool
    {
        return $this->pdo->rollBack();
    }
}