package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill") // url:/ģ��/��Դ/{id}/ϸ��/ /seckill/list
public class SeckillController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model) {
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list", list);
		return "list";
	}

	@RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
	public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
		if (seckillId == null) {
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if (seckill == null) {
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}

	@RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	@ResponseBody
	public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
		SeckillResult<Exposer> result = null;

		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(exposer);
		} catch (Exception e) {
			logger.error(e.getMessage());
			result = new SeckillResult<Exposer>(e.getMessage());
		}

		return result;
	}

	@RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {
			"application/json;charset=UTF-8" })
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
			@CookieValue(value = "userPhone", required = false) Long userPhone, @PathVariable("md5") String md5) {

		SeckillExecution seckillExecution = null;
		// ���userPhoneΪ��
		// spring valid��֤��������֤֮��������spring valid����֤һ��Ƚ϶࣬��Ȼû��Ҫʹ��
		if (userPhone == null) {
			return new SeckillResult<SeckillExecution>("手机号不能为空");
		}

		try {
			seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(seckillExecution);
		} catch (SeckillCloseException e) {
			logger.error(e.getMessage(), e);
			return new SeckillResult<SeckillExecution>(new SeckillExecution(seckillId, SeckillStateEnum.END));
		} catch (RepeatKillException e) {
			logger.error(e.getMessage(), e);
			return new SeckillResult<SeckillExecution>(new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL));
		} catch (SeckillException e) {
			logger.error(e.getMessage(), e);
			return new SeckillResult<SeckillExecution>(new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR));
		}

	}

	@RequestMapping("/time/now")
	@ResponseBody
	public SeckillResult<Long> time() {
		Long time = new Date().getTime();
		return new SeckillResult<Long>(time);
	}

}
