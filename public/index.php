<?php

define('ROOT_DIR', dirname(__DIR__));

// Tự động nạp class: Ưu tiên Composer, nếu chưa có thì dùng spl_autoload
if (file_exists(ROOT_DIR . '/vendor/autoload.php')) {
    require_once ROOT_DIR . '/vendor/autoload.php';
} else {
    spl_autoload_register(function ($class) {
        $prefix = 'App\\';
        $base_dir = ROOT_DIR . '/app/';
        $len = strlen($prefix);

        if (strncmp($prefix, $class, $len) !== 0) {
            return;
        }

        $relative_class = substr($class, $len);
        $file = $base_dir . str_replace('\\', '/', $relative_class) . '.php';

        if (file_exists($file)) {
            require $file;
        }
    });
}

use App\Core\App;

// Khởi tạo ứng dụng
$app = new App();

// 👉 GÁN BIẾN $router ĐỂ FILE routes/web.php CÓ THỂ SỬ DỤNG
$router = $app->router;

// Nạp các tuyến đường
require_once ROOT_DIR . '/routes/web.php';

// Thực thi
$app->run();