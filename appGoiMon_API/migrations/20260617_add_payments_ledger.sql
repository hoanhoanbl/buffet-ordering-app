-- Adds an idempotency ledger for payment confirmations.
-- Each confirmed gateway callback is recorded once, keyed by a unique transaction_id,
-- so replays (webhook retries / duplicate simulate taps) never double-apply a payment.

CREATE TABLE IF NOT EXISTS payments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  session_id INT NOT NULL,
  transaction_id VARCHAR(100) NOT NULL UNIQUE,
  amount DECIMAL(12,2) NOT NULL,
  method VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'paid',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
