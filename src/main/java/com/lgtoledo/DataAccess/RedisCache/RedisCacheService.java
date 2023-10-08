package com.lgtoledo.DataAccess.RedisCache;

import java.util.Optional;

import com.lgtoledo.Models.LinkModel;

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

    public Optional<LinkModel> getLinkByShortId(String shortLinkId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String longLink = jedis.get(shortLinkId);
            
            if (longLink != null && !longLink.isEmpty()) {
                LinkModel linkModel = new LinkModel();
                linkModel.setId(shortLinkId);
                linkModel.setLong_link(longLink);

                return Optional.of(linkModel);
            }
        }
        return Optional.empty();
    }

    public void setLink(LinkModel link) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (link != null && link.getId() != null && link.getLong_link() != null) {
                long expireMinutes = 60;
                
                jedis.setex(link.getId(), expireMinutes * 60, link.getLong_link());
            }
        }
    }

    public void deleteLink(String shortLinkId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(shortLinkId);
        }
    }

    public void flushAll() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB(FlushMode.ASYNC);
        }
    }

}
