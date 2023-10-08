package com.lgtoledo.DataAccess.CosmosDB;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.lgtoledo.Configurations;
import com.lgtoledo.Models.Link;
import com.lgtoledo.Models.LinkAccessStat;

public class CosmosDbService {
    private final CosmosClient cosmosClient;
    private final CosmosDatabase cosmosDatabase;
    private final CosmosContainer cosmosLinksContainer;
    private final CosmosContainer cosmosLinksAccessStatsContainer;


    public CosmosDbService(String endpoint, String key, String databaseName) {
        this.cosmosClient = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .buildClient();
        this.cosmosDatabase = cosmosClient.getDatabase(databaseName);
        this.cosmosLinksContainer = cosmosDatabase.getContainer(Configurations.COSMOS_DB_LINKS_CONTAINER);
        this.cosmosLinksAccessStatsContainer = cosmosDatabase.getContainer(Configurations.COSMOS_DB_LINKS_ACCESS_STATS_CONTAINER);
    }

    // Links
    public Optional<Link> getLinkByShortId(String shortLinkId) {
        try {
            CosmosItemResponse<Link> response = cosmosLinksContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), Link.class);
            
            return Optional.ofNullable(response.getItem());
        } catch (Exception e) {

            return Optional.empty();
        }
    }

    public Link saveLink(Link link) {
        try {
            // Document document = new Document(link.toJson());
            CosmosItemResponse<Link> itemResponse = cosmosLinksContainer.createItem(link);

            return itemResponse.getStatusCode() == 201 ? link : null;

        } catch (Exception e) {
            return null;
        }
    }

    public void deleteLinkByIdAsync(String shortLinkId) {
        CompletableFuture.runAsync(() -> {
            try {
                CosmosItemRequestOptions options = new CosmosItemRequestOptions();
                cosmosLinksContainer.deleteItem(shortLinkId, new PartitionKey(shortLinkId), options);
    
            } catch (Exception e) {
                return;
            } 
        });
    }

    public void deleteAllLinksAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                boolean containerExists = cosmosDatabase.readAllContainers().stream()
                .anyMatch(containerProperties -> containerProperties.getId().equals("links"));
        
                if (containerExists) {
                    cosmosLinksContainer.delete();
                }
                cosmosDatabase.createContainerIfNotExists("links", "/id");

            } catch (Exception e) {
                return;
            }
        });
    }

    public boolean existsLinkById(String shortLinkId) {
        try {
            cosmosLinksContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), Link.class);
            
            return true;  // Si no hay errores, existe
        } catch (Exception e) {
                
            return false;
        }
    }

    // Link Access Stats
    public Optional<LinkAccessStat> getLinkAccessStatById(String shortLinkId) {
        try {
            CosmosItemResponse<LinkAccessStat> response = cosmosLinksAccessStatsContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), LinkAccessStat.class);
            
            return Optional.ofNullable(response.getItem());
        } catch (Exception e) {
            System.out.println("Error al obtener estadística: " + e.getMessage());
            return Optional.empty();
        }
    }

    public LinkAccessStat saveLinkAccessStat(LinkAccessStat linkAccessStat) {
        try {
            // Document document = new Document(linkAccessStat);
            // CosmosItemResponse<Document> itemResponse = cosmosLinksAccessStatsContainer.upsertItem(document);
            CosmosItemResponse<LinkAccessStat> itemResponse = cosmosLinksAccessStatsContainer.upsertItem(linkAccessStat);

            return itemResponse.getStatusCode() == 200 || itemResponse.getStatusCode() == 201 ? itemResponse.getItem() : null;

        } catch (Exception e) {
            System.out.println("Error al obtener estadística: " + e.getMessage());
            return null;
        }
    }

    public void deleteLinkAccessStatByIdAsync(String shortLinkId) {
        try {
            CosmosItemRequestOptions options = new CosmosItemRequestOptions();
            cosmosLinksAccessStatsContainer.deleteItem(shortLinkId, new PartitionKey(shortLinkId), options);
  
        } catch (Exception e) {
            return;
        }        
    }

    public void deleteAllLinkAccessStatsAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                boolean containerExists = cosmosDatabase.readAllContainers().stream()
                .anyMatch(containerProperties -> containerProperties.getId().equals("linksAccessStats"));
        
                if (containerExists) {
                    cosmosLinksAccessStatsContainer.delete();
                }
                cosmosDatabase.createContainerIfNotExists("linksAccessStats", "/id");

            } catch (Exception e) {
                return;
            }
        });
    }

    public boolean existsLinkAccessStatById(String shortLinkId) {
        try {
            cosmosLinksAccessStatsContainer.readItem(shortLinkId, new PartitionKey(shortLinkId), LinkAccessStat.class);
            
            return true;  // Si no hay errores, existe
        } catch (Exception e) {
                
            return false;
        }
    }
}




