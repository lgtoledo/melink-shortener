package com.lgtoledo.DataAccess.CosmosDB;

import java.util.Optional;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.implementation.Document;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.lgtoledo.Models.LinkModel;

public class CosmosDbService {
    private final CosmosClient cosmosClient;
    private final CosmosContainer cosmosContainer;

    public CosmosDbService(String endpoint, String key, String databaseName, String containerName) {
        this.cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .buildClient();

        this.cosmosContainer = cosmosClient.getDatabase(databaseName).getContainer(containerName);
    }

    public Optional<LinkModel> getLinkByShortId(String shortLinkId) {
        try {
            CosmosItemResponse<LinkModel> response = cosmosContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), LinkModel.class);
            
            return Optional.ofNullable(response.getItem());
        } catch (Exception e) {

            return Optional.empty();
        }
    }

    public LinkModel saveLink(LinkModel link) {
        try {
             System.out.println("Guardando link: " + link.toJson());

            Document document = new Document(link.toJson());
            CosmosItemResponse<Document> itemResponse = cosmosContainer.createItem(document);

            return itemResponse.getStatusCode() == 201 ? link : null;

        } catch (Exception e) {
            return null;
        }
    }

    public void deleteLink(String shortLinkId) {
        try {
            CosmosItemRequestOptions options = new CosmosItemRequestOptions();
            cosmosContainer.deleteItem(shortLinkId, new PartitionKey(shortLinkId), options);
  
        } catch (Exception e) {
            return;
        }        
    }

    

    public boolean existsById(String shortLinkId) {
        try {
            cosmosContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), LinkModel.class);
            
            return true;  // Si no hay errores, existe
        } catch (Exception e) {
                
            return false;
        }
    }

    public void close() {
        if (cosmosClient != null) {
            cosmosClient.close();
        }
    }
}




