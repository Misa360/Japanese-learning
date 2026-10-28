<?php

namespace App\Core;

class Request
{
    private array $get;
    private array $post;
    private array $server;

    public function __construct()
    {
        $this->get = $_GET;
        $this->post = $_POST;
        $this->server = $_SERVER;
    }

    public function getMethod(): string
    {
        return strtoupper($this->server['REQUEST_METHOD'] ?? 'GET');
    }

    public function getPath(): string
    {
        $path = $this->server['REQUEST_URI'] ?? '/';
        $position = strpos($path, '?');
        if ($position !== false) {
            $path = substr($path, 0, $position);
        }
        return rtrim($path, '/') ?: '/';
    }

    public function getBody(): array
    {
        $body = [];
        if ($this->getMethod() === 'GET') {
            foreach ($this->get as $key => $value) {
                $body[$key] = filter_input(INPUT_GET, $key, FILTER_DEFAULT);
            }
        }
        if ($this->getMethod() === 'POST') {
            foreach ($this->post as $key => $value) {
                $body[$key] = filter_input(INPUT_POST, $key, FILTER_DEFAULT);
            }
        }
        return $body;
    }
}