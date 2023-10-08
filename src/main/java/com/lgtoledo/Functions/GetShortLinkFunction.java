package com.lgtoledo.Functions;

import java.util.Optional;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
import com.lgtoledo.DataAccess.RedisCache.RedisCacheService;
import com.lgtoledo.Models.LinkModel;
import com.lgtoledo.utils.LinkUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetShortLinkFunction {

    String longUrlRegex = "^(http|https)://.*$";

    @FunctionName("getShortLink")
    public HttpResponseMessage getShortLink(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        long startTime = System.nanoTime();
        context.getLogger().info("Procesando request para crear un nuevo link corto...");
        
        CosmosDbService cosmosDbService = new CosmosDbService(
                Configurations.COSMOS_DB_ENDPOINT,
                Configurations.COSMOS_DB_KEY,
                "meli-cosmosdb-database",
                "links");
        
        
        // Verifico si el link largo fue enviado en el body del request
        Optional<String> optLongLink = request.getBody();
        if (!optLongLink.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Se debe de proporcionar un link largo válido.").build();
        }

        String longLink = optLongLink.get();

        if (!LinkUtils.isValidUrl(longLink, longUrlRegex)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Se debe de proporcionar un link largo válido.").build();
        }

        Integer maxTries = 10;
        Optional<String> optionalShortLinkId = generateUniqueShortLink(maxTries, cosmosDbService);

        if (!optionalShortLinkId.isPresent()) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo generar un link corto único.").build();
        }

        // guardo el link en Cosmos DB
        String shortLinkId = optionalShortLinkId.get();
        LinkModel savedLink = cosmosDbService.saveLink(new LinkModel(shortLinkId, longLink));
        if (savedLink == null) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo guardar el link en Cosmos DB.").build();
        }

        // guardo el link en Redis Cache
        RedisCacheService redisCacheService = new RedisCacheService(Configurations.REDIS_CACHE_HOST, Configurations.REDIS_CACHE_PORT, Configurations.REDIS_CACHE_KEY);
        redisCacheService.setLink(savedLink);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        return request.createResponseBuilder(HttpStatus.CREATED).body("Link corto creado correctamente. Demora: " + duration + "ms. Link: /" + shortLinkId).build();
    }


    private Optional<String> generateUniqueShortLink(int maxTries, CosmosDbService cosmosDbService) {
        for (int i = 0; i < maxTries; i++) {
            String potentialShortLinkId = LinkUtils.generateShortLink();
            if (!cosmosDbService.existsById(potentialShortLinkId)) {
                return Optional.of(potentialShortLinkId);
            }
        }
        return Optional.empty();
    }
}
