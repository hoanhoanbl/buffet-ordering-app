<?php

require_once __DIR__ . '/../../config/helpers.php';

run_endpoint(function (): void {
    require_method('GET');
    $stmt = db()->query(
        "SELECT id, combo_name AS name, price_per_person, description, status
         FROM buffet_combos
         WHERE status = 'active'
         ORDER BY price_per_person ASC, id ASC"
    );

    $imageSets = [
        '209' => ['209_1.jpg', '209_2.jpg', '209_3.jpg', '209_4.jpg', '209_5.jpg', '209_6.jpg'],
        '229' => ['229_1.jpg', '229_2.jpg', '229_3.jpg', '229_4.jpg', '229_5.jpg', '229_6.jpg'],
        '299' => ['299_1.jpg', '299_2.jpg', '299_3.jpg', '299_4.jpg', '299_5.jpg', '299_6.jpg', '299_7.jpg', '299_8.jpg'],
        'hasu' => ['209_1.jpg', '209_2.jpg', '209_3.jpg', '209_4.jpg', '209_5.jpg', '209_6.jpg'],
        'sakura' => ['229_1.jpg', '229_2.jpg', '229_3.jpg', '229_4.jpg', '229_5.jpg', '229_6.jpg'],
        'kiku' => ['299_1.jpg', '299_2.jpg', '299_3.jpg', '299_4.jpg', '299_5.jpg', '299_6.jpg', '299_7.jpg', '299_8.jpg'],
    ];
    $fallbackKeys = ['209', '229', '299'];

    $combos = $stmt->fetchAll();
    foreach ($combos as $index => &$combo) {
        $name = strtolower((string) ($combo['name'] ?? ''));
        $priceKey = (string) (int) round(((float) $combo['price_per_person']) / 1000);
        $key = null;

        if (isset($imageSets[$priceKey])) {
            $key = $priceKey;
        }

        foreach ($imageSets as $candidate => $_) {
            if ($key !== null) {
                break;
            }
            if (str_contains($name, $candidate)) {
                $key = $candidate;
                break;
            }
        }

        $key ??= $fallbackKeys[$index % count($fallbackKeys)];
        $images = array_map(
            fn (string $file): string => "uploads/combo/{$file}",
            $imageSets[$key]
        );

        $combo['image'] = $images[0] ?? null;
        $combo['images'] = $images;
        $combo['description'] = $combo['name'];
    }
    unset($combo);

    json_response(true, 'Thành công', $combos);
});
