package com.lgtoledo.Functions;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class DeleteAllLinksFunction {
    


    @FunctionName("deleteAllLinks")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Void> request,
        final ExecutionContext context) {

        context.getLogger().info("Procesando request para eliminar todos los links de la base de datos...");



        try (CosmosClient cosmosClient = createCosmosClient()) {

            CosmosDatabase database = cosmosClient.getDatabase("meli-cosmosdb-database");

            boolean containerExists = database.readAllContainers().stream()
                    .anyMatch(containerProperties -> containerProperties.getId().equals("links"));
            
            if (containerExists) {
                database.getContainer("links").delete();
            }

            database.createContainerIfNotExists("links", "/id");

            //TODO: limpiar caché

            return request.createResponseBuilder(HttpStatus.OK).body("Se eliminaron todos los links correctamente.").build();

        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar los links: " + e.getMessage());

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar los links.").build();
        }
    }

    private CosmosClient createCosmosClient() {
        String endpoint = System.getenv("CosmosDbEndpoint");
        String key = System.getenv("CosmosDbKey");
        
        return new CosmosClientBuilder()
            .endpoint(endpoint)
            .key(key)
            .buildClient();
    }

}
