-- Adds session ownership: links each table session to the user who opened it.
-- Nullable so legacy rows (created before auth-table ownership) stay valid and are
-- treated as "not mine / legacy" by the endpoints. An index on (user_id, status)
-- speeds up the one-active-session-per-user lookup used by my_active_session.php.

ALTER TABLE table_sessions
  ADD COLUMN user_id INT NULL AFTER table_id;

ALTER TABLE table_sessions
  ADD INDEX idx_user_status (user_id, status);
