package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;

	@Autowired
	private RedisDao redisDao;

	private final String slat = "c53np8y	m485345xm3495xc n396	xm9532y42592mo9523X7N9345";

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		// 优化点：缓存优化
		// 1、使用redis换成seckill
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			seckill = seckillDao.queryById(seckillId);
			if (seckill != null) {
				redisDao.putSeckill(seckill);
			}
		}

		// Seckill seckill = seckillDao.queryById(seckillId);
		if (seckill == null) {// û�и���ɱ��Ʒ
			return new Exposer(false, seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		// �����ɱʱ�䲻���
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		// ת���ض��ַ�Ĺ�̣�������
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	/**
	 * ʹ��ע��������񷽷����ŵ� 1�������ŶӴ��һ��Լ������ȷ��ע���񷽷��ı�̷��
	 * 2����֤���񷽷���ִ��ʱ�価���ܶΣ���Ҫ���������������RPC/HTTP������߰��뵽���񷽷��ⲿ��
	 * 3���������еķ�������Ҫ������ֻ��һ���޸Ĳ�����ֻ����������Ҫ�������
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		// ִ����ɱ�߼�������� + ��¼������Ϊ
		Date nowTime = new Date();
		try {
			// 先增加购买明细，然后减库存
			// 增加购买明细
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

			if (insertCount <= 0) {// 重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				// 减库存，热点商品竞争
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {// 秒杀已经结束
					throw new SeckillCloseException("seckill is closed");
				} else {
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e) {
			throw e;
		} catch (RepeatKillException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SeckillException("seckill innser error:" + e.getMessage());
		}
	}

	/**
	 * 调用存储过程执行秒杀操作，因为不需要抛出异常来使程序rollback，所以删除运行时异常异常
	 * 
	 * @param seckillId
	 *            秒杀id
	 * @param userPhone
	 *            用户电话
	 * @param md5
	 *            md5
	 * @return 返回秒杀执行结果
	 */
	@Override
	public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("seckillId", seckillId);
			param.put("phone", userPhone);
			param.put("result", -3);
			seckillDao.killByProceduce(param);
			Integer result = MapUtils.getInteger(param, "result", -3);
			if (result == 1) {
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.valueOf(result.toString()));
			}
		} catch (Exception e) {// 避免执行seckillDao.killByProceduce()出错
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
