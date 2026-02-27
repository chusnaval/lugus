-- Verificación post-deploy: control de versión de esquema

-- 1) Comprueba que exista la tabla de control
SELECT EXISTS (
    SELECT 1
    FROM information_schema.tables
    WHERE table_schema = 'lugus'
      AND table_name = 'schema_version'
) AS schema_version_exists;

-- 2) Devuelve la versión actual aplicada
SELECT version, applied_at, notes
FROM lugus.schema_version
ORDER BY applied_at DESC, id DESC
LIMIT 1;
