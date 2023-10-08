package com.lgtoledo.Functions;

import java.util.Optional;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
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

public class GetLinkStatsFunction {

    private static CosmosDbService cosmosDbService = new CosmosDbService(
            Configurations.COSMOS_DB_ENDPOINT,
            Configurations.COSMOS_DB_KEY,
            "meli-cosmosdb-database");

    @FunctionName("getLinkStats")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                route = "linkStats/{shortLinkId}",
                methods = { HttpMethod.GET },
                authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @BindingName("shortLinkId") String shortLinkId,
            final ExecutionContext context) {
        
        context.getLogger().info("No se encontraron datos en Redis Cache. Se obtendrán de Cosmos DB...");

        // obtener el link largo de Cosmos DB
        Optional<LinkAccessStat> optLinkAccessStat = cosmosDbService.getLinkAccessStatById(shortLinkId);

        if (!optLinkAccessStat.isPresent()) {
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontró información estadística para el enlace requerido.")
                    .build();
        }

        context.getLogger().info("Datos encontrados en Cosmos DB: " + optLinkAccessStat.get());
        
        // return as json
        return request.createResponseBuilder(HttpStatus.OK).body(optLinkAccessStat.get().toJson()).build();
    }
    
}
