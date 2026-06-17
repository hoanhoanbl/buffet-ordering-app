<?php

require_once __DIR__ . '/../../config/helpers.php';

// Returns the CURRENT user's single active (non-expired) table session, decorated exactly like
// get_session_status.php (table_code/table_name/combo_name + payment_status + remaining time, plus
// the OFFLINE VietQR fields for unpaid QR sessions), or null when the user has no active session.
// The app calls this right after login / cold-start restore to auto-resume the running session.
run_endpoint(function (): void {
    require_method('GET');
    $pdo = db();
    $userId = int_param($_GET, 'user_id');

    if ($userId <= 0) {
        json_response(false, 'Thiếu user_id', null, 422);
    }

    // Run the same expired-session release the other endpoints use so we never resume a dead session.
    release_expired_sessions($pdo);

    $stmt = $pdo->prepare(
        'SELECT ts.*, t.table_code, t.table_name, c.combo_name AS combo_name
         FROM table_sessions ts
         JOIN restaurant_tables t ON t.id = ts.table_id
         JOIN buffet_combos c ON c.id = ts.combo_id
         WHERE ts.user_id = ? AND ' . active_session_condition('ts') . '
         ORDER BY ts.id DESC LIMIT 1'
    );
    $stmt->execute([$userId]);
    $session = $stmt->fetch();

    if (!$session) {
        // No active session — the app routes to the table picker.
        json_response(true, 'Khong co phien hoat dong', null);
    }

    $session = decorate_session($pdo, $session);

    // If the release/refresh just expired it, report none.
    if (($session['status'] ?? '') !== 'active' || ($session['is_expired'] ?? false) === true) {
        json_response(true, 'Khong co phien hoat dong', null);
    }

    // Re-attach the OFFLINE VietQR payload for unpaid QR sessions so the waiting screen can render
    // the real, scannable bank-transfer QR. Display only — payment confirmation is untouched.
    if (($session['payment_method'] ?? '') === 'qr' && ($session['payment_status'] ?? '') !== 'paid') {
        $session = array_merge(
            $session,
            vietqr_payment_fields((int) $session['id'], (float) $session['total_amount'])
        );
    }

    json_response(true, 'Thanh cong', $session);
});
