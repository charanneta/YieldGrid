-- Non-destructive migration for existing YieldGrid installations.
-- Passwords are upgraded automatically on the next successful login:
-- legacy plaintext values are verified once and replaced with PBKDF2 hashes.
-- Do not run destructive DROP or DELETE statements as part of this migration.

USE yieldgrid;

ALTER TABLE users MODIFY password VARCHAR(255) NOT NULL;

-- Existing order_items rows must be preserved. Inspect the current FK first:
SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'order_items'
  AND COLUMN_NAME = 'crop_id'
  AND REFERENCED_TABLE_NAME = 'crops';

-- Run the following only if the returned constraint currently has CASCADE:
-- Replace fk_order_items_crop with the actual constraint name if different.
-- ALTER TABLE order_items DROP FOREIGN KEY fk_order_items_crop;
-- ALTER TABLE order_items
--     ADD CONSTRAINT fk_order_items_crop
--     FOREIGN KEY (crop_id) REFERENCES crops(crop_id) ON DELETE RESTRICT;

-- Existing users keep working through legacy-login upgrade. Accounts that
-- cannot be logged into should be reset by an administrator outside this
-- script; never bulk-print or export existing password values.
