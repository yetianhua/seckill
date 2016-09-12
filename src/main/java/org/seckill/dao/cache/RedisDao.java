package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private JedisPool jedisPoll;

	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	public RedisDao(String ip, int port) {
		this.jedisPoll = new JedisPool(ip, port);
	}

	/**
	 * 根据seckillId获取seckill，如果seckill为空，则表示报错或者redis中不存在该seckill，否则返回seckill实例
	 * 
	 * @param seckillId
	 * @return
	 */
	public Seckill getSeckill(long seckillId) {
		// redis操作逻辑
		try {
			Jedis jedis = null;
			try {
				jedis = jedisPoll.getResource();
				String key = "seckill:" + seckillId;
				// 序列化问题
				byte[] value = jedis.get(key.getBytes());
				if (value != null) {
					// 空对象
					Seckill seckill = schema.newMessage();
					// protostuff封装了protobuffer(google写的),protobuffer反序列化需要schema
					// protostuff
					ProtostuffIOUtil.mergeFrom(value, seckill, schema);
					return seckill;
				}
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 向redis填充seckill，如果填充成功返回"OK",如果填充失败返回"内部错误"
	 * 
	 * @param seckill
	 * @return
	 */
	public String putSeckill(Seckill seckill) {
		String result = null;
		Jedis jedis = null;
		try {
			jedis = jedisPoll.getResource();
			String key = "seckill:" + seckill.getSeckillId();
			// toByteArray()方法的第三个参数是“缓存器”,它是为了当序列化的对象比较大的时候需要的缓冲，
			// 下面“缓存器”的大小是默认大小
			byte[] value = ProtostuffIOUtil.toByteArray(seckill, schema,
					LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
			// 超时缓存
			int timeout = 60 * 60;
			result = jedis.setex(key.getBytes(), timeout, value);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result = "内部错误";
			return result;
		}
	}
}
