package com.lgtoledo.DataAccess.RedisCache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.lgtoledo.Models.Link;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.FlushMode;

public class RedisCacheService {
    private final JedisPool jedisPool;

    public RedisCacheService(String host, Integer port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password, true);
    }

    public Optional<Link> getLinkByShortId(String shortLinkId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String longLink = jedis.get(shortLinkId);
            
            if (longLink != null && !longLink.isEmpty()) {
                Link linkModel = new Link();
                linkModel.setId(shortLinkId);
                linkModel.setlongLink(longLink);

                return Optional.of(linkModel);
            }
        }
        return Optional.empty();
    }

    public void setLinkAsync(Link link) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                if (link != null && link.getId() != null && link.getlongLink() != null) {
                    long expireMinutes = 60;
                    
                    jedis.setex(link.getId(), expireMinutes * 60, link.getlongLink());
                }
            }
        });
    }

    public void deleteLinkAsync(String shortLinkId) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(shortLinkId);
            } catch (Exception e) {
                return;
            }
        });
    }

    public void flushAllAsync() {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.flushDB(FlushMode.ASYNC);
            } catch (Exception e) {
                return;
            }
        });
    }

}
