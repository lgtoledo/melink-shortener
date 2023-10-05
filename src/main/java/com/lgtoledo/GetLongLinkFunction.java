package com.lgtoledo;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetLongLinkFunction {

    @FunctionName("getLongLink")
    public HttpResponseMessage run(
        @HttpTrigger(name = "req", route = "l/{shortLinkId}", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
        @BindingName("shortLinkId") String shortLinkId,
        @CosmosDBInput(name = "item",
                       databaseName = "meli-cosmosdb-database",
                       containerName = "links",
                       sqlQuery = "SELECT * FROM c WHERE c.id = {shortLinkId}",
                       connection = "CosmosDbConnectionString") Optional<String> item,
        final ExecutionContext context) {

        context.getLogger().info("Procesando request para obtener el link largo...");

        if (!item.isPresent()) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontró el enlace requerido.").build();
        }       

        String longLink = extractLongLinkFromJson(item.get());

        context.getLogger().info("Datos encontrados en Cosmos DB: " + item.get());

        if (longLink == null) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el enlace.").build();
        }

        // Redirijo al link largo...
        return request.createResponseBuilder(HttpStatus.FOUND).header("Location", longLink).build();
    }

    private String extractLongLinkFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            // Si el contenido es un array, obtiene el primer elemento
            if (rootNode.isArray() && rootNode.size() > 0) {
                rootNode = rootNode.get(0);
            }

            JsonNode longLinkNode = rootNode.path("long_link");

            if (longLinkNode.isMissingNode()) {

                return null;
            }

            return longLinkNode.asText();
        } catch (Exception e) {

            return null; 
        }
    }

}
