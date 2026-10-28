<?php

/** @var \App\Core\Router $router */

$router->get('/', function () {
    return "
        <div style='font-family: Arial, sans-serif; text-align: center; margin-top: 100px;'>
            <h1 style='color: #2c3e50;'>🌸 Chào mừng đến với JLEARN-WEB!</h1>
            <p style='color: #27ae60; font-size: 18px;'>Hệ thống Backend MVC & Database đã kết nối thành công.</p>
        </div>
    ";
});

$router->get('/ping', function ($req, $res) {
    return $res->json(['status' => 'success', 'message' => 'pong', 'timestamp' => time()]);
});