package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	SeckillService seckillService;

	@Test
	public void testGetSeckillList() {
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("seckillList = {}", list);
	}

	@Test
	public void testGetById() {
		long seckillId = 1000L;
		Seckill seckill = seckillService.getById(seckillId);
		logger.info("seckill = {}", seckill);
	}

	@Test
	public void testExportSeckillUrl() {
		long seckillId = 1000L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {// ·µ»Øurl³É¹¦
			// 1¡¢exposer = Exposer [exposed=true,
			// md5=d6ab1d8c4fa077a9ccee96e92039e2d3, seckillId=1000, now=0,
			// start=0, end=0]
			logger.info("exposer = {}", exposer);
		} else {
			// 1¡¢exposer = Exposer [exposed=false, md5=null, seckillId=0,
			// now=1471274025249, start=1470067200000, end=1470153600000]
			// 2¡¢exposer = Exposer [exposed=false, md5=null, seckillId=999,
			// now=0, start=0, end=0]
			logger.info("exposer = {}", exposer);
		}
	}

	@Test
	public void testExecuteSeckill() {
		long seckillId = 1000L;
		long userPhone = 18826408881L;
		String md5 = "d6ab1d8c4fa077a9ccee96e92039e2d3";
		SeckillExecution seckillExecution = null;
		try {
			seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
		} catch (SeckillCloseException e) {
			logger.warn(e.getMessage());
		} catch (RepeatKillException e) {
			logger.warn(e.getMessage());
		} catch (SeckillException e) {
			logger.warn(e.getMessage());
		}
		if (seckillExecution != null) {
			logger.info("seckillExecution = {}", seckillExecution);
		}
	}

	@Test
	public void testSeckillLogic() {
		long seckillId = 1001L;
		long userPhone = 18826408881L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed()) {
			SeckillExecution seckillExecution = null;
			try {
				seckillExecution = seckillService.executeSeckill(seckillId, userPhone, exposer.getMd5());
			} catch (SeckillCloseException e) {
				logger.warn(e.getMessage());
			} catch (RepeatKillException e) {
				logger.warn(e.getMessage());
			} catch (SeckillException e) {
				logger.warn(e.getMessage());
			}
			if (seckillExecution != null) {
				logger.info("seckillExecution = {}", seckillExecution);
			}

		} else {
			logger.info("exposer = {}", exposer);
		}
	}

}
