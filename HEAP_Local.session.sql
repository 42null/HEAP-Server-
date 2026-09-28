SELECT CONCAT('SELECT * FROM `', table_name, '`;') AS query
FROM information_schema.tables
WHERE table_schema = 'heap_data';