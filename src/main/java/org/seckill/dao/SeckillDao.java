package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	/**
	 * �����
	 * 
	 * @param seckillId
	 * @param killTime
	 * @return ���Ӱ������=1����ʾ���µļ�¼����
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	/**
	 * ����id��ѯ��ɱ����
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);

	/**
	 * ����ƫ������ѯ��ɱ��Ʒ�б�
	 * 
	 * @param offest
	 * @param limit
	 * @return
	 */
	// javaû�б����βεļ�¼��queryAll(int offset,int limit) -> queryAll(arg0,arg1)
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
