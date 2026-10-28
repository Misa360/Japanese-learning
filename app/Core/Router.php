<?php

namespace App\Core;

class Router
{
    private array $routes = [];
    public Request $request;
    public Response $response;

    public function __construct(Request $request, Response $response)
    {
        $this->request = $request;
        $this->response = $response;
    }

    public function get(string $path, $callback): void
    {
        $this->routes['GET'][$this->normalizePath($path)] = $callback;
    }

    public function post(string $path, $callback): void
    {
        $this->routes['POST'][$this->normalizePath($path)] = $callback;
    }

    private function normalizePath(string $path): string
    {
        return rtrim($path, '/') ?: '/';
    }

    public function resolve()
    {
        $method = $this->request->getMethod();
        $path = $this->request->getPath();

        $callback = $this->routes[$method][$path] ?? false;

        if ($callback === false) {
            $this->response->setStatusCode(404);
            return "<h1>404 - Không tìm thấy trang!</h1>";
        }

        // Nếu callback là Closure (hàm ẩn danh)
        if (is_callable($callback)) {
            return call_user_func($callback, $this->request, $this->response);
        }

        // Nếu callback là [Controller::class, 'method']
        if (is_array($callback)) {
            [$controllerClass, $action] = $callback;
            if (class_exists($controllerClass)) {
                $controller = new $controllerClass();
                if (method_exists($controller, $action)) {
                    return call_user_func([$controller, $action], $this->request, $this->response);
                }
            }
        }

        $this->response->setStatusCode(500);
        return "<h1>500 - Lỗi Controller/Action không tồn tại</h1>";
    }
}