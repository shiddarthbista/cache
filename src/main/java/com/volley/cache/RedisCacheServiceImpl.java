package com.volley.cache;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RedisCacheServiceImpl implements RedisCacheService{

    @Autowired
    private JedisPool jedisPool;

    private final Gson gson = new Gson();

    //TTL(Time to live) of session data in seconds
    @Value("${ttl}")
    private int timeToLive;


    // Acquire Jedis instance from the jedis pool.
    private Jedis acquireJedisInstance() {
        log.info("Connected to redis succesfully");
        return jedisPool.getResource();
    }

    // Releasing the current Jedis instance once completed the job.
    private void releaseJedisInstance(Jedis jedis) {

        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
    }

    @Override
    public void storeEmployee(String employeeId, Employee employee) {

        Jedis jedis = null;

        try {

            jedis = acquireJedisInstance();

            String json = gson.toJson(employee);
            jedis.set(employeeId, json);
            log.info("SAved to redis. key=" + employeeId + " Value = " + json);
            jedis.expire(employeeId, timeToLive);

        } catch (Exception e) {
            log.error("Error occurred while storing data to the cache{} ", e.getMessage());
            releaseJedisInstance(jedis);
            throw new RuntimeException(e);

        } finally {
            releaseJedisInstance(jedis);
        }

    }

    @Override
    public Optional<Employee> retrieveEmployee(String employeeId) {

        Jedis jedis = null;

        try {

            jedis = acquireJedisInstance();
            if(jedis.exists(employeeId)){
                log.info("It IS present in cache");
            }else {
                log.info("CANNOT FIND IN CACHE YEEHAW");
            }
            String employeeJson = jedis.get(employeeId);

            if (StringUtils.hasText(employeeJson)) {
                Employee fromCache =  gson.fromJson(employeeJson, Employee.class);
                log.info("Retrieved from cache{}", fromCache);
                return Optional.of(fromCache);
            }

        } catch (Exception e) {
            log.error("Error occurred while retrieving data from the cache {}", e.getMessage());
            releaseJedisInstance(jedis);
            throw new RuntimeException(e);

        } finally {
            releaseJedisInstance(jedis);
        }

        return Optional.empty();
    }

    @Override
    public void flushEmployeeCache(String employeeId) {

        Jedis jedis = null;
        try {

            jedis = acquireJedisInstance();

            List<String> keys = jedis.lrange(employeeId, 0, -1);
            if (!CollectionUtils.isEmpty(keys)) {
                // add the list key in as well
                keys.add(employeeId);

                // delete the keys and list
                jedis.del(keys.toArray(new String[0]));
            }
        } catch (Exception e) {
            log.error("Error occurred while flushing specific data from the cache{} ", e.getMessage());
            releaseJedisInstance(jedis);
            throw new RuntimeException(e);

        } finally {
            releaseJedisInstance(jedis);
        }

    }

    @Override
    public void clearAll() {

        Jedis jedis = null;
        try {

            jedis = acquireJedisInstance();
            jedis.flushAll();

        } catch (Exception e) {
            log.error("Error occured while flushing all data from the cache {} ", e.getMessage());
            releaseJedisInstance(jedis);
            throw new RuntimeException(e);

        } finally {
            releaseJedisInstance(jedis);
        }

    }
}
