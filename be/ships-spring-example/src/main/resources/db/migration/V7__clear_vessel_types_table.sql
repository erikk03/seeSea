DELETE FROM [vessel_type];
DBCC CHECKIDENT ('vessel_type', RESEED, 0);