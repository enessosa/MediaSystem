-- E-Mail wird nicht mehr gebraucht (kein Mailversand, privates Hosting), siehe ADR-011.
ALTER TABLE app_user DROP COLUMN email;