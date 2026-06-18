<?php

class Controller
{
    protected function view(string $view, array $data = []): void
    {
        require_once __DIR__ . '/../../includes/helpers.php';

        extract($data, EXTR_SKIP);

        ob_start();
        require __DIR__ . '/../Views/' . $view . '.php';
        $content = ob_get_clean();

        require __DIR__ . '/../Views/layouts/main.php';
    }

    protected function redirect(string $url): void
    {
        header('Location: ' . $url);
        exit;
    }
}
