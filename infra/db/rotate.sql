CREATE ROLE "{{name}}"
  LOGIN
  PASSWORD '{{password}}'
  VALID UNTIL '{{expiration}}'
  INHERIT;

GRANT vault_access TO "{{name}}";

GRANT CONNECT ON DATABASE "%DATABASE%" TO "{{name}}";
GRANT USAGE  ON SCHEMA public TO "{{name}}";

ALTER ROLE "{{name}}" SET search_path = public;
