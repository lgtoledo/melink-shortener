package com.lgtoledo;

import java.util.Optional;

import com.lgtoledo.utils.LinkUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GetShortLinkFunction {

    String longUrlRegex = "^(http|https)://.*$";

    @FunctionName("getShortLink")
    public HttpResponseMessage getShortLink(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "document", databaseName = "meli-cosmosdb-database", containerName = "links", connection = "CosmosDbConnectionString") OutputBinding<String> document,
            final ExecutionContext context) {

        long startTime = System.currentTimeMillis();
        context.getLogger().info("Procesando request para crear un nuevo link corto...");
        
        // Verifico si el link largo fue enviado en el body del request
        Optional<String> optLongLink = request.getBody();
        if (!optLongLink.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Se debe de proporcionar un link largo válido.").build();
        }

        String longLink = optLongLink.get();

        if (!LinkUtils.isValidUrl(longLink, longUrlRegex)) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Se debe de proporcionar un link largo válido.").build();
        }

        String shortLinkId = LinkUtils.generateShortLink();

        // Creo el objeto json que se va a guardar en BD
        String json = String.format("{\"id\": \"%s\", \"long_link\": \"%s\"}", shortLinkId, longLink);

        // escribo el json en BD (usando el output binding)
        document.setValue(json);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return request.createResponseBuilder(HttpStatus.CREATED).body("Link corto creado correctamente. Demora: " + duration + "ms. Link: /" + shortLinkId).build();
    }
}
