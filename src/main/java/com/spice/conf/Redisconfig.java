package com.spice.conf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
 
@Configuration
public class Redisconfig {
	@Value("${spring.redis.host}")
	String redis_host;
	
	@Value("${spring.redis.port}")
	int redis_port;
	


    @Bean
  public   JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redis_host, redis_port);
		config.setPassword("m2mredis");
	    return new JedisConnectionFactory(config);
	}
 

	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
	    final RedisTemplate<String, Object> template1 = new RedisTemplate<String, Object>();
	    template1.setConnectionFactory(jedisConnectionFactory());
	    template1.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
	    return template1;
	}
}