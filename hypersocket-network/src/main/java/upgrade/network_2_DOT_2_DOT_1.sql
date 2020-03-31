EXIT IF FRESH;
ALTER TABLE launchers ADD CONSTRAINT launchers_cascade_1 FOREIGN KEY (realm_id) REFERENCES realms (resource_id) ON DELETE CASCADE;
ALTER TABLE network_protocols ADD CONSTRAINT network_protocols_cascade_1 FOREIGN KEY (realm_id) REFERENCES realms (resource_id) ON DELETE CASCADE;
