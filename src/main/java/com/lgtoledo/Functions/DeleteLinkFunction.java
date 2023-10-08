package com.lgtoledo.Functions;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
import com.lgtoledo.DataAccess.RedisCache.RedisCacheService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class DeleteLinkFunction {
    
    private static CosmosDbService cosmosDbService = new CosmosDbService(
            Configurations.COSMOS_DB_ENDPOINT,
            Configurations.COSMOS_DB_KEY,
            "meli-cosmosdb-database");

    private static RedisCacheService redisCacheService = new RedisCacheService(
            Configurations.REDIS_CACHE_HOST,
            Configurations.REDIS_CACHE_PORT,
            Configurations.REDIS_CACHE_KEY);

    @FunctionName("deleteLink")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Void> request,
        @CosmosDBInput(name = "items",
                       databaseName = "meli-cosmosdb-database",
                       containerName = "links",
                       sqlQuery = "SELECT * FROM c WHERE c.id = {id}",
                       connection = "CosmosDbConnectionString") String[] items,
        final ExecutionContext context) {

        try {
            final String id = request.getQueryParameters().get("id");

            context.getLogger().info("Procesando request para eliminar linkId: " + id);     

            if (items.length == 0) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontró el link a eliminar.").build();
            }

            cosmosDbService.deleteLinkByIdAsync(id);
            redisCacheService.deleteLinkAsync(id);
            cosmosDbService.deleteLinkAccessStatByIdAsync(id);

            return request.createResponseBuilder(HttpStatus.OK).body("Se eliminaron todos los links correctamente.").build();

        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar link: " + e.getMessage());

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar los links.").build();
        }
    }

}
