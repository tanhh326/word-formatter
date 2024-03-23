package com.crane.wordformat.restful.utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
public class RedisUtil {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final Long retryMill = (long) 1000 * 1000;

    @Autowired
    private JedisPool jedisPool;

    public final <OUT> OUT redisExec(Function<Jedis, OUT> function) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return function.apply(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("redisExec error, errorMsg = {}", e.getMessage());
            throw e;
        } finally {
            Optional.ofNullable(jedis).ifPresent(j -> j.close());
        }
    }


    /**
     * 获取分布式锁
     *
     * @param lockKey
     * @param requestId
     * @param expireTime 毫秒
     */
    public Boolean tryDistributedLock(String lockKey, String requestId, int expireTime) {
        log.info("tryGetDistributedLock-->>lockKey = {}, requestId = {}, expireTime = {}", lockKey, requestId, expireTime);
        String result = this.setNxxxExpx(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        log.info("tryGetDistributedLock-->>lockKey = {}, requestId = {}, result = {}", lockKey, requestId, result);
        return LOCK_SUCCESS.equals(result);
    }

    /**
     * @param lockKey       上锁的key
     * @param expireTime    锁的超时时间
     * @param retryTimeNano 最多尝试多久 纳秒单位
     * @return
     * @throws InterruptedException
     */
    public String lock(String lockKey, int expireTime, long retryTimeNano) {
        int rt = 0;
        String key = null;
        try {
            while (true) {
                key = String.valueOf(System.nanoTime());
                String result = this.setNxxxExpx(lockKey, key, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                if (LOCK_SUCCESS.equals(result)) {
                    return key;
                }
                // 获取锁失败

                rt += retryMill;//每隔1 ms执行一次
                if (rt <= retryTimeNano) { // 下次可以执行
                    Thread.sleep(retryMill);
                } else {
                    return  null;
                }
            }
        } catch (InterruptedException e) {
            return null;
        }

    }


    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        log.info("releaseDistributedLock-->>, lockKey = {}, requestId = {}", lockKey, requestId);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = this.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        log.info("releaseDistributedLock, lockKey = {}, requestId = {}, result = {}", lockKey, requestId, result);
        if (RELEASE_SUCCESS.equals(result)) {
            log.info("releaselock>>>>>>>>>>>>>>>> lockKey = {}, requestId = {}", lockKey, requestId);
            return true;
        }
        log.info("锁已释放, lockKey = {}, requestId = {}", lockKey, requestId);
        return false;

    }

    public String setNxxxExpx(final String key, final String value, final String nxxx, final String expx,
                              final int time) {
        return redisExec(jedis -> jedis.set(key, value, nxxx, expx, time));
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        return redisExec(jedis -> jedis.eval(script, keys, args));
    }

    public String get(String key) {
        return this.redisExec(redis -> redis.get(key));
    }

    public byte[] getBytes(byte[] key) {
        return this.redisExec(redis -> redis.get(key));
    }

    public void set(String key, String val) {
        this.redisExec(redis -> redis.set(key, val));
    }

    public Long incr(String key) {
        return this.redisExec(redis -> redis.incr(key));
    }

    public Long append(String key, String value) {
        return this.redisExec(redis->redis.append(key, value));
    }
    public void set(String key, String val, int expireSecond) {
        this.redisTxExec(tx -> {
            tx.set(key, val);
            tx.expire(key, expireSecond);
        });
    }

    public Long rpush(String key, String val) {
        return this.redisExec(tx-> tx.rpush(key, val));
    }

    public String lpop(String key) {
        return this.redisExec(tx->tx.lpop(key));
    }

    public void setBytes(byte[] key, byte[] val, int expireSecond) {
        this.redisTxExec(tx -> {
            tx.set(key, val);
            tx.expire(key, expireSecond);
        });
    }

    public Long SAdd(String key, final String... field) {
        return this.redisExec((redis -> redis.sadd(key, field)));
    }

    public Long ZAdd(String key, double score, String mem) {
        return this.redisExec((redis -> redis.zadd(key, score, mem)));
    }

    public Long SRem(String key, final String... field) {
        return this.redisExec((redis -> redis.srem(key, field)));
    }


    public Long IncBy(String key, Long val) {
        return this.redisExec((redis -> redis.incrBy(key, val)));
    }

    public Long del(String key) {
        return this.redisExec(redis -> redis.del(key));
    }

    public Long del(byte[] key) {
        return this.redisExec(redis -> redis.del(key));
    }

    public Long delByte(byte[] key) {
        return this.redisExec(redis -> redis.del(key));
    }

    public Long hdel(String key, String fileds) {
        return this.redisExec(redis -> redis.hdel(key, fileds));
    }

    public Boolean exists(String key) {
        return this.redisExec(redis -> redis.exists(key));
    }

    public Long scard(String key) {
        return this.redisExec(redis -> redis.scard(key));
    }

    public String srandmember(String key) {
        return this.redisExec(redis -> redis.srandmember(key));
    }

    public Set<String> smembers(String key) {
        return this.redisExec(redis -> redis.smembers(key));
    }

    public boolean sismember(String key, String value) {
        return this.redisExec(redis -> redis.sismember(key, value));
    }

    public Boolean hexists(String key, String ele) {
        return this.redisExec(redis -> redis.hexists(key, ele));
    }

    public String hget(String key, String filed) {
        return this.redisExec(redis -> redis.hget(key, filed));
    }

    public Map<String, String> hgetall(String key) {
        return redisExec(redis -> redis.hgetAll(key));
    }

    public Set<String> zrange(String key, Long start, Long end) {
        return this.redisExec(redis -> redis.zrange(key, start, end));
    }


    public Long zadd(String key, double score, String member) {
        return this.redisExec(redis->redis.zadd(key, score, member));
    }

    public Long zrem(String key, String... mem) {
        return this.redisExec(r -> r.zrem(key, mem));
    }

    public Set<String> zrevrange(String key, Long start, Long end) {
        return this.redisExec(redis -> redis.zrevrange(key, start, end));
    }

    public Set<Tuple> zrevrangeWithScores(String key, Long start, Long end) {
        return this.redisExec(redis -> redis.zrevrangeWithScores(key, start, end));
    }


    public Set<Tuple> zrangeWithScores(String key, Long start, Long end) {
        return this.redisExec(redis -> redis.zrangeWithScores(key, start, end));
    }

    public Long zrank(String key, String member) {
        return this.redisExec(redis -> redis.zrank(key, member));
    }

    public Set<String> hkeys(String key) {
        return this.redisExec(redis -> redis.hkeys(key));
    }

    public List<String> lrange(String key, Long start, Long end) {
        return this.redisExec(redis -> redis.lrange(key, start, end));
    }

    public Long expire(String key, int expireSecond) {
        return this.redisExec(redis -> redis.expire(key, expireSecond));
    }

    public Long TTL(String key) {
        return this.redisExec(redis -> redis.ttl(key));
    }


    public void redisTxExec(Consumer<Transaction> consumer) {
        List<Object> result = this.redisExec(jedis -> {
            Transaction tx = jedis.multi();
            consumer.accept(tx);
            return tx.exec();
        });
    }

    public void redisWatchTxExec(Consumer<Transaction> consumer, String... args) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.watch(args);
            Transaction tx = jedis.multi();
            consumer.accept(tx);
            List<Object> execResult = tx.exec();
            log.info("jedis tx result = {}", execResult);
            if (CollectionUtils.isEmpty(execResult)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("redisExec error, errorMsg = {}", e.getMessage());
            throw e;
        } finally {
            Optional.ofNullable(jedis).ifPresent(j -> j.close());
        }
    }
}
