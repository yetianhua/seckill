-- 秒杀执行存储过程
DELIMITER $$ -- console ; 转换为$$
-- 定义存储过程
-- 参数:in 输入参数; out 输出参数
-- row_count():返回上一条修改类型sql(delete,insert,update)的影响行数
-- row_count(): 0:未修改数据; >0:表示修改的行数; <0:sql错误/未执行修改sql
CREATE PROCEDURE seckill.execute_seckill(IN v_seckill_id BIGINT,IN v_phone BIGINT, OUT r_result INT)
BEGIN
	DECLARE insert_count INT DEFAULT 0;
	DECLARE v_kill_time TIMESTAMP DEFAULT CURRENT_DATE;
	INSERT IGNORE INTO success_killed(seckill_id,user_phone,state,create_time)VALUES(v_seckill_id,v_phone,0,NOW());
	SELECT ROW_COUNT() INTO insert_count;
	IF insert_count = 0 THEN
		ROLLBACK;
		SET r_result = -1;
	ELSEIF insert_count < 0 THEN
		ROLLBACK;
		SET r_result = -2;
	ELSE
		UPDATE seckill SET number = number -1
			WHERE seckill_id = v_seckill_id 
			AND number > 0 
			AND v_kill_time >= start_time 
			AND v_kill_time <= end_time;
		SELECT ROW_COUNT() INTO insert_count;
		IF insert_count = 1 THEN
			COMMIT;
			SET r_result = 1;
		ELSEIF insert_count = 0 THEN
			ROLLBACK;
			SET r_result = 0;
		ELSEIF insert_count < 0  THEN
			ROLLBACK;
			SET r_result = -2;
		END IF;
	END IF;
END 
$$
DELIMITER ;
	
SET @r_result = -3;
-- 执行存储过程
CALL seckill.execute_seckill(1003,18826408888,@r_result);

-- 获取结果
SELECT @r_result;
	
--存储过程
--1：存储过程优化：事务行级锁持有的时间
--2：不要过度依赖存储过程
--3：简单的逻辑，可以应用存储过程
--4：QPS：一个秒杀单6000/qps
	