CREATE ROLE "{{name}}" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}' INHERIT;

GRANT CONNECT ON DATABASE "%DATABASE%" TO "{{name}}";
GRANT USAGE ON SCHEMA public TO "{{name}}";
GRANT vault_access TO "{{name}}"