package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

/**
 * ҵ��ӿڣ�վ�ڡ�ʹ���ߡ��Ƕ����ýӿ� ������棺�����������ȣ���������ͣ�return ����/�쳣��
 */
public interface SeckillService {
	/**
	 * ��ѯ������ɱ��¼
	 * 
	 * @return
	 */
	List<Seckill> getSeckillList();

	/**
	 * ��ѯ������ɱ��¼
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);

	/**
	 * ��ɱ����ʱ�����ɱ�ӿڵ�ַ, �������ϵͳʱ�����ɱʱ��
	 * 
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);

	/**
	 * ִ����ɱ����
	 * 
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException;
	
	/**
	 * 调用存储过程执行秒杀操作，因为不需要抛出异常来使程序rollback，所以删除运行时异常异常
	 * @param seckillId 秒杀id
	 * @param userPhone 用户电话
	 * @param md5 md5
	 * @return 返回秒杀执行结果
	 */
	SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5);
}
