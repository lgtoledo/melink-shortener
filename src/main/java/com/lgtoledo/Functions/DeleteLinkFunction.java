package com.lgtoledo.Functions;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    @FunctionName("deleteLink")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Void> request,
        @CosmosDBInput(name = "items",
                       databaseName = "meli-cosmosdb-database",
                       containerName = "links",
                       sqlQuery = "SELECT * FROM c",
                       connection = "CosmosDbConnectionString") String[] items,
        final ExecutionContext context) {

        context.getLogger().info("Procesando request para eliminar todos los links de la base de datos...");

        try (CosmosClient cosmosClient = createCosmosClient()) {

            CosmosContainer container = cosmosClient.getDatabase("meli-cosmosdb-database").getContainer("links");
            ObjectMapper objectMapper = new ObjectMapper();

            container.delete();

            for (String item : items) {
                try {
                    JsonNode rootNode = objectMapper.readTree(item);
                    JsonNode idNode = rootNode.path("id");

                    if (!idNode.isMissingNode()) {
                        String id = idNode.asText();
                    
                        // Eliminar el elemento usando el ID
                        container.deleteItem(id, new PartitionKey(id), new CosmosItemRequestOptions());
                        context.getLogger().info("Elemento eliminado con éxito: " + id);
                    }
                } catch (Exception e) {
                    context.getLogger().warning("Error al procesar el elemento: " + e.getMessage());
                }
            }

            return request.createResponseBuilder(HttpStatus.OK).body("Se eliminaron todos los links correctamente.").build();

        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar los links: " + e.getMessage());

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete all links.").build();
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
