package com.lgtoledo.Functions;

import java.util.Optional;

import com.lgtoledo.Configurations;
import com.lgtoledo.DataAccess.CosmosDB.CosmosDbService;
import com.lgtoledo.Models.ApiResponseDTO;
import com.lgtoledo.Models.LinkAccessStat;
import com.lgtoledo.Models.LinkAccessStatDTO;
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

        Optional<LinkAccessStat> optLinkAccessStat = cosmosDbService.getLinkAccessStatById(shortLinkId);

        if (!optLinkAccessStat.isPresent()) {
            ApiResponseDTO response = new ApiResponseDTO(4004, "No se encontró información estadística para el enlace proporcionado.");
            return request.createResponseBuilder(HttpStatus.NOT_FOUND).body(response).build();
        }

        context.getLogger().info("Datos encontrados en DB: " + optLinkAccessStat.get());
        LinkAccessStatDTO linkAccessStatDTO = optLinkAccessStat.get().toDTO();
        
        ApiResponseDTO response = new ApiResponseDTO(0, "OK", linkAccessStatDTO);

        // return as json
        return request.createResponseBuilder(HttpStatus.OK).body(response).build();
    }
    
}
