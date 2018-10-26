package com.cms.utils;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class JedisUtils
{
	/** 公共日志实现类 */
	private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static ShardedJedisPool jedisPool = (ShardedJedisPool) ApplicationContextHelper.applicationContext
            .getBean("shardedJedisPool");

    private static JedisPool pool = (JedisPool) ApplicationContextHelper.applicationContext
    		.getBean("jedisPool");


    public static void lpush(String key, String value)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            jedis.lpushx(key, value);

        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }
    
    public static void setString(String key, String value)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            jedis.set(key, value);
        }catch(Exception e) {
        	logger.error("获取jedis失败.", e);
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static String lPop(String key) throws Exception
    {
        ShardedJedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            logger.debug("rPop key=" + key);
            return jedis.lpop(key);
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static void zDelByScore(String key, Long start, Long end)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            jedis.zremrangeByScore(key, start, end);
        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }
    
    public static String getString(String key)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            return jedis.get(key);
        }catch(Exception e) {
        	logger.error("获取jedis失败.", e);
        	return null;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }

    public static void zRem(String key,String member)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            jedis.zrem(key, member);

        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }
    
    public static Long hincrby(String key,String field,Long value) 
    {
    	ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            return jedis.hincrBy(key, field, value);
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }
    
    public static Long zGetCount(String key)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            return jedis.zcount(key, 0, 999999999999999L);

        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static Set<String> zRange(String key, Long start, Long end)
    {

        ShardedJedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            return jedis.zrange(key, start, end);

        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static Double zScore(String key, String member)
    {

        ShardedJedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            return jedis.zscore(key, member);

        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static Map<String, String> hgetAll(String key)
    {

        ShardedJedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            logger.debug("查询缓存:Find Cache = " + key);
            Map<String, String> result = jedis.hgetAll(key);
            if(result.keySet().size()>0) {
            	return result;
            }else {
            	return null;
            }
        }catch(Exception e) {
        	logger.error("获取jedis失败.", e);
        	return null;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }
    
    public static String hSet(String key,Map<String,String> hash)
    {

        ShardedJedis jedis = null;
        try
        {
            jedis = jedisPool.getResource();
            logger.debug("存入缓存:Cache = " + key);
            return jedis.hmset(key, hash);
        }catch(Exception e) {
        	logger.error("获取jedis失败.", e);
        	return null;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static BitSet getBit(String key)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            // 如果保存成员将不能新增，需要先删除。
            byte[] result = jedis.get(key.getBytes());
            if (null != result)
            {
                return convertToBitSet(result);
            }
            else
            {
                return new BitSet();
            }

        }

        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static BitSet convertToBitSet(byte[] tmp)
    {
        /*
         * ByteBuffer bytebuffer = ByteBuffer.wrap(tmp);
         * 
         * bytebuffer = bytebuffer.slice().order(ByteOrder.BIG_ENDIAN); int i;
         * for (i = bytebuffer.remaining(); i > 0 && bytebuffer.get(i - 1) == 0;
         * i--) ; long al[] = new long[(i + 7) / 8]; bytebuffer.limit(i); int j
         * = 0; while (bytebuffer.remaining() >= 8) al[j++] =
         * bytebuffer.getLong(); int k = bytebuffer.remaining(); for (int l = 0;
         * l < k; l++) al[j] |= ((long) bytebuffer.get() & 255L) << 8 * l;
         * return BitSet.valueOf(al);
         */

        BitSet bitSet = new BitSet(tmp.length * 8);

        int index = 0;
        for (int i = 0; i < tmp.length; i++)
        {
            for (int j = 7; j >= 0; j--)
            {
                bitSet.set(index++, (tmp[i] & (1 << j)) >> j == 1 ? true
                        : false);
            }
        }

        return bitSet;
    }

    public static Set<String> getSet(String key) throws Exception
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            logger.debug("查询缓存:Find Cache = " + key);

            return jedis.smembers(key);

        }
        catch (JedisException e)
        {
            logger.error("查询缓存异常：key=" + key, e);
            throw e;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }

    public static void setSet(String key, Set<String> values, boolean setNullKey)
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            ShardedJedisPipeline p = jedis.pipelined();
            logger.debug("更新:Find Cache = " + key);
            p.del(key);
            for (Iterator<String> iter = values.iterator(); iter.hasNext();)
            {
                p.sadd(key, iter.next());
            }
            if (setNullKey && CollectionUtils.isEmpty(values))
            {
                p.sadd(key, "");
            }
            p.sync();

        }
        catch (JedisException e)
        {
            logger.error("更新缓存异常：key=" + key, e);

        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }

    }

    public static boolean exists(String key) throws Exception
    {

        ShardedJedis jedis = null;
        try
        {

            jedis = jedisPool.getResource();
            logger.debug("查询缓存:Find Cache = " + key);
            return jedis.exists(key);

        }
        catch (JedisException e)
        {
            logger.error("查询缓存异常：key=" + key, e);
            throw e;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }
    
    /**
     * 模糊批量删除
     * @param key
     */
    public static void delKeysStartWith(String pre) 
    {
    	Jedis jedis = null;
    	StringBuffer sb = new StringBuffer();
        try
        {
            logger.debug("删除缓存:Del Cache = " + pre+"*");
            jedis = pool.getResource();
            Pipeline p = jedis.pipelined();
            Set<String> keys = jedis.keys(pre+"*");
            for(String redisKey : keys) {
            	sb.append(redisKey+",");
            	p.del(redisKey);
            }
            p.sync();
            logger.debug("删除缓存:Del Cache = " + pre + "*:" + sb);
        }
        catch (Exception e)
        {
            logger.error("删除缓存异常：key=" + pre+"*", e);
        }
        finally
        {
            if (null != jedis)
            {
            	pool.returnResourceObject(jedis);
            }
        }
    }

    public static void del(String key)
    {
        ShardedJedis jedis = null;
        try
        {
            logger.debug("删除缓存:Del Cache = " + key);
            jedis = jedisPool.getResource();
            Long result = jedis.del(key);
            logger.debug("删除缓存:Del Cache = " + key + ":" + result);
        }
        catch (JedisException e)
        {
            logger.error("删除缓存异常：key=" + key, e);
//            throw e;
        }
        finally
        {
            if (null != jedis)
            {
                jedisPool.returnResourceObject(jedis);
            }
        }
    }
    
    public static void main(String[] args) {
    	try {
    		hSet("111",new HashMap<String,String>());
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	}

}
