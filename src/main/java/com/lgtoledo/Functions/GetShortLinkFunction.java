package com.lgtoledo.Functions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
import com.lgtoledo.DataAccess.RedisCache.RedisCacheService;
import com.lgtoledo.Models.ApiResponseDTO;
import com.lgtoledo.Models.Link;
import com.lgtoledo.Models.LinkAccessStat;
import com.lgtoledo.utils.CodeGenerator;
import com.lgtoledo.utils.Utils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetShortLinkFunction {

    private static RedisCacheService redisCacheService = new RedisCacheService(
            Configurations.REDIS_CACHE_HOST,
            Configurations.REDIS_CACHE_PORT,
            Configurations.REDIS_CACHE_KEY);

    private static CosmosDbService cosmosDbService = new CosmosDbService(
            Configurations.COSMOS_DB_ENDPOINT,
            Configurations.COSMOS_DB_KEY,
            "meli-cosmosdb-database");

    String longUrlRegex = "^https://.*$";

    @FunctionName("getShortLink")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = { HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        long startTime = System.nanoTime();
        context.getLogger().info("Procesando request para crear un nuevo link corto...");
        
        // Verifico si el link largo fue enviado en el body del request
        Optional<String> optLongLink = request.getBody();
        if (!Utils.isValidUrl(optLongLink, longUrlRegex)) {
            ApiResponseDTO response = new ApiResponseDTO(4001, "Se debe de proporcionar un link largo válido.");

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(response).build();
        }

        String longLink = optLongLink.get();

        Integer maxTries = 10;
        Optional<String> optionalShortLinkId = generateUniqueShortLink(maxTries, cosmosDbService);

        if (!optionalShortLinkId.isPresent()) {
            ApiResponseDTO response = new ApiResponseDTO(5001, "No se pudo generar un link corto único.");

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(response).build();
        }

        // guardo el link en Cosmos DB
        String shortLinkId = optionalShortLinkId.get();
        Link savedLink = cosmosDbService.saveLink(new Link(shortLinkId, longLink));
        if (savedLink == null) {
            ApiResponseDTO response = new ApiResponseDTO(5001, "No se pudo generar un link corto único.");

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(response).build();
        }

        // guardo el link en Redis Cache
        redisCacheService.setLinkAsync(savedLink);
        registerStatAsync(shortLinkId, cosmosDbService);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        context.getLogger().info("Link corto generado: " + savedLink.getId() + ". Duración: " + duration + "ms.");

        String loadBalancerUrl = Configurations.LOAD_BALANCER_URL;
        savedLink.setId(loadBalancerUrl + "/l/" + savedLink.getId());

        ApiResponseDTO response = new ApiResponseDTO(0, "OK", savedLink);

        // response as json
        return request.createResponseBuilder(HttpStatus.CREATED).body(response).build();
    }


    private Optional<String> generateUniqueShortLink(int maxTries, CosmosDbService cosmosDbService) {
        for (int i = 0; i < maxTries; i++) {
            String potentialShortLinkId = CodeGenerator.generateCode(6);
            if (!cosmosDbService.existsLinkById(potentialShortLinkId)) {
                return Optional.of(potentialShortLinkId);
            }
        }
        return Optional.empty();
    }

    private void registerStatAsync(String shortLinkId, CosmosDbService cosmosDbService) {
    CompletableFuture.runAsync(() -> {

            LinkAccessStat newStat = new LinkAccessStat();
            newStat.setId(shortLinkId);
            newStat.setCreationDateUTC(Utils.getCurrentUtcDateTime());
            newStat.setFirstAccessedDateUTC(null);
            newStat.setLastAccessedDateUTC(null);
            newStat.setAccessCount(0);

            cosmosDbService.saveLinkAccessStat(newStat);
        });
    }
}
