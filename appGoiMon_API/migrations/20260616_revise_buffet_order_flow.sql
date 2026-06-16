-- Revises the buffet flow to paid active sessions with a 100-minute dining window.
-- Safe intent: preserve existing rows, map legacy pending/processing statuses to the new flow.

ALTER TABLE table_sessions
  ADD COLUMN IF NOT EXISTS paid_at DATETIME NULL AFTER payment_status;

UPDATE table_sessions
SET
  payment_status = 'paid',
  paid_at = COALESCE(paid_at, start_time, NOW()),
  status = 'active',
  end_time = COALESCE(end_time, DATE_ADD(COALESCE(start_time, NOW()), INTERVAL 100 MINUTE))
WHERE status = 'pending_payment';

UPDATE table_sessions
SET
  paid_at = COALESCE(paid_at, start_time, NOW()),
  end_time = COALESCE(end_time, DATE_ADD(COALESCE(start_time, NOW()), INTERVAL 100 MINUTE))
WHERE status = 'active';

UPDATE restaurant_tables
SET status = 'occupied'
WHERE status = 'waiting_payment';

ALTER TABLE restaurant_tables
  MODIFY status ENUM('available','occupied') DEFAULT 'available';

ALTER TABLE table_sessions
  MODIFY payment_method ENUM('qr','cash') NULL,
  MODIFY payment_status ENUM('unpaid','paid') DEFAULT 'unpaid',
  MODIFY status ENUM('active','expired','closed') DEFAULT 'active';

ALTER TABLE order_items
  MODIFY status ENUM('pending','processing','approved','served','rejected') DEFAULT 'pending';

ALTER TABLE orders
  MODIFY status ENUM('pending','processing','approved','served','partially_served','rejected','empty') DEFAULT 'pending';

UPDATE order_items
SET status = 'approved'
WHERE status = 'processing' OR status = '';

UPDATE orders
SET status = 'approved'
WHERE status = 'processing' OR status = '';

ALTER TABLE order_items
  MODIFY status ENUM('pending','approved','served','rejected') DEFAULT 'pending';

ALTER TABLE orders
  MODIFY status ENUM('pending','approved','served','partially_served','rejected','empty') DEFAULT 'pending';
