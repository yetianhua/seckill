package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml" })
public class RedisDaoTest {

	@Autowired
	RedisDao redisDao;

	@Autowired
	SeckillDao seckillDao;

	@Test
	public void testSeckill() {
		Long seckillId = 1000L;
		Seckill seckill = null;
		seckill = redisDao.getSeckill(seckillId);
		if (seckill != null) {
			System.out.println(seckill);
		} else {
			seckill = seckillDao.queryById(seckillId);
			if (seckill != null) {
				redisDao.putSeckill(seckill);
			}
		}
	}

}
