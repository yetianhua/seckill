package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	 * ���id��ѯ��ɱ����
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(long seckillId);

	/**
	 * ���ƫ������ѯ��ɱ��Ʒ�б�
	 * 
	 * @param offest
	 * @param limit
	 * @return
	 */
	// javaû�б����βεļ�¼��queryAll(int offset,int limit) -> queryAll(arg0,arg1)
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
	
	/**
	 * 调用存储过程执行秒杀
	 * @param param
	 */
	//由于是存储过程所以返回为void，且因为存储过程的参数可能是in或者out，所以使用Map来存参数
	void killByProceduce(Map<String,Object> param);
}
