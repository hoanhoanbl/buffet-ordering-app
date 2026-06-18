<?php

require_once __DIR__ . '/Database.php';

class Repository
{
    protected PDO $db;

    public function __construct()
    {
        $this->db = Database::connect();
    }
}
