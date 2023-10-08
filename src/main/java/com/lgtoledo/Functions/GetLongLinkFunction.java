package com.lgtoledo.Functions;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
import com.lgtoledo.DataAccess.RedisCache.RedisCacheService;
import com.lgtoledo.Models.Link;
import com.lgtoledo.Models.LinkAccessStat;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetLongLinkFunction {

    private static CosmosDbService cosmosDbService = new CosmosDbService(
            Configurations.COSMOS_DB_ENDPOINT,
            Configurations.COSMOS_DB_KEY,
            "meli-cosmosdb-database");

    private static RedisCacheService redisCacheService = new RedisCacheService(
            Configurations.REDIS_CACHE_HOST,
            Configurations.REDIS_CACHE_PORT,
            Configurations.REDIS_CACHE_KEY);

    @FunctionName("getLongLink")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                route = "l/{shortLinkId}",
                methods = { HttpMethod.GET },
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("shortLinkId") String shortLinkId,
            final ExecutionContext context) {

        // obtener el link largo de la caché si existe
        Optional<Link> optLongLinkFromCache = redisCacheService.getLinkByShortId(shortLinkId);
        
        if (optLongLinkFromCache.isPresent()) {
            String longLink = optLongLinkFromCache.get().getlongLink();

            // registro la estadística
            registerStatAsync(shortLinkId, cosmosDbService);

            // Redirijo al link largo...
            return request.createResponseBuilder(HttpStatus.FOUND).header("Location", longLink).build();
        }

        context.getLogger().info("No se encontraron datos en Redis Cache. Se obtendrán de Cosmos DB...");

        // obtener el link largo de Cosmos DB
        Optional<Link> optLongLink = cosmosDbService.getLinkByShortId(shortLinkId);

        if (!optLongLink.isPresent()) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontró el enlace requerido.")
                    .build();
        }

        context.getLogger().info("Datos encontrados en Cosmos DB: " + optLongLink.get());
        String longLink = optLongLink.get().getlongLink();

        if (longLink == null || longLink.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el enlace.")
                    .build();
        }
        // registro la estadística
        registerStatAsync(shortLinkId, cosmosDbService);

        // guardo el link en redis
        redisCacheService.setLinkAsync(optLongLink.get());

        // redirijo al link largo...
        return request.createResponseBuilder(HttpStatus.FOUND).header("Location", longLink).build();
    }

    private void registerStatAsync(String shortLinkId, CosmosDbService cosmosDbService) {
    CompletableFuture.runAsync(() -> {
            Optional<LinkAccessStat> optLinkAccessStat = cosmosDbService.getLinkAccessStatById(shortLinkId);

            if (!optLinkAccessStat.isPresent()) {
                // si no existe, lo creo
                LinkAccessStat newStat = new LinkAccessStat();
                newStat.setId(shortLinkId);
                newStat.setCreationDate(LocalDateTime.now());
                newStat.setFirstAccessedDate(null);
                newStat.setLastAccessedDate(null);
                newStat.setAccessCount(0);

                cosmosDbService.saveLinkAccessStat(newStat);
            } else {
                // si existe, actualizo el contador y las fechas
                LinkAccessStat linkAccessStat = optLinkAccessStat.get();

                linkAccessStat.setAccessCount(linkAccessStat.getAccessCount() + 1);
                if (linkAccessStat.getFirstAccessedDate() == null) {
                    linkAccessStat.setFirstAccessedDate(LocalDateTime.now());
                }
                linkAccessStat.setLastAccessedDate(LocalDateTime.now());

                cosmosDbService.saveLinkAccessStat(linkAccessStat);
            } 
        });
    }


}
