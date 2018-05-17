package com.acloudchina.coap.outbound.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(name = "outbound.service.adapter", havingValue = "redis")
@Slf4j
public class RedisTool {
	@Value("${spring.redis.host}")
	public String ADDR_ARRAY;

	@Value("${spring.redis.port:6379}")
	public int PORT;

	@Value("${spring.redis.pool.max-active}")
	public int MAX_ACTIVE;

	@Value("${spring.redis.pool.max-idle}")
	public int MAX_IDLE;

	@Value("${spring.redis.pool.min-idle}")
	public int MIN_IDLE;

	public int MAX_WAIT = -1;

	@Value("${spring.redis.timeout}")
	public int TIMEOUT;
	public boolean TEST_ON_BORROW = true;

	private JedisPool jedisPool = null;

	/**
	 * 初始化Redis连接池
	 */
	private void initialPool() {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(MAX_ACTIVE);
			config.setMaxIdle(MAX_IDLE);
			config.setMinIdle(MIN_IDLE);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[0], PORT, TIMEOUT);
		} catch (Exception e) {
			log.error("First create JedisPool error : " + e);
			try {
				// 如果第一个IP异常，则访问第二个IP
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(MAX_ACTIVE);
				config.setMaxIdle(MAX_IDLE);
				config.setMaxWaitMillis(MAX_WAIT);
				config.setTestOnBorrow(TEST_ON_BORROW);
				jedisPool = new JedisPool(config, ADDR_ARRAY.split(",")[1], PORT, TIMEOUT);
			} catch (Exception e2) {
				log.error("Second create JedisPool error : " + e2);
			}
		}
	}

	/**
	 * 在多线程环境同步初始化
	 */
	private synchronized void poolInit() {
		if (jedisPool == null) {
			initialPool();
		}
	}

	/**
	 * 同步获取Jedis实例
	 * 
	 * @return Jedis
	 */
	@Bean
	public synchronized Jedis getJedis() {
		if (jedisPool == null) {
			poolInit();
		}
		Jedis jedis = null;
		try {
			if (jedisPool != null) {
				jedis = jedisPool.getResource();
			}
		} catch (Exception e) {
			log.error("Get jedis error : " + e);
		} finally {
			returnResource(jedis);
		}
		return jedis;
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	private void returnResource(final Jedis jedis) {
		if (jedis != null && jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}

}
