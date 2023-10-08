package com.lgtoledo;

public class Configurations {
    public static final String COSMOS_DB_ENDPOINT = System.getenv("CosmosDbEndpoint");
    public static final String COSMOS_DB_KEY = System.getenv("CosmosDbKey");
    public static final String REDIS_CACHE_CONNECTION_STRING = System.getenv("RedisCacheConnectionString");
    public static final String REDIS_CACHE_HOST = System.getenv("RedisCacheHost");
    public static final Integer REDIS_CACHE_PORT = Integer.parseInt(System.getenv("RedisCachePort"));
    public static final String REDIS_CACHE_KEY = System.getenv("RedisCacheKey");

}
