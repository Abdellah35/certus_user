DROP FUNCTION IF EXISTS certus_core_db.getUsernameById;

DELIMITER $$
$$
CREATE FUNCTION `certus_core_db`.`getUsernameById`(id_v int) RETURNS varchar(300) CHARSET latin1
begin
	declare username varchar(300);
	select `email_id` into username from certus_core_db.user_tbl where id = id_v;
	return username;
END$$
DELIMITER ;



-- ------------------

DROP FUNCTION IF EXISTS certus_core_db.getUserIdByUsername;

DELIMITER $$
$$
CREATE FUNCTION `certus_core_db`.`getUserIdByUsername`(name VARCHAR(300))
RETURNS INT(11)
BEGIN
	DECLARE `user_id` INT;
	SELECT `id` INTO `user_id` FROM `certus_core_db`.`user_tbl` WHERE email_id = name ;
	RETURN `user_id`;
end



$$
DELIMITER ;


