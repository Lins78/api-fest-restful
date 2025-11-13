package com.exemplo.apifest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resposta paginada padrão da API
 * 
 * Fornece estrutura consistente para listagens com paginação,
 * incluindo metadados de navegação e links úteis.
 * 
 * @param <T> Tipo dos itens na lista
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 5 - Documentação e Padronização
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada da API")
public class PagedResponse<T> {

    @Schema(description = "Lista de itens da página atual")
    private List<T> content;

    @Schema(description = "Metadados de paginação")
    private PageMetadata page;

    @Schema(description = "Links de navegação")
    private PageLinks links;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Metadados de paginação")
    public static class PageMetadata {
        
        @Schema(description = "Número da página atual (base 0)", example = "0")
        private int number;
        
        @Schema(description = "Tamanho da página", example = "10")
        private int size;
        
        @Schema(description = "Total de elementos", example = "50")
        private long totalElements;
        
        @Schema(description = "Total de páginas", example = "5")
        private int totalPages;
        
        @Schema(description = "Indica se é a primeira página", example = "true")
        private boolean first;
        
        @Schema(description = "Indica se é a última página", example = "false")
        private boolean last;
        
        @Schema(description = "Indica se tem próxima página", example = "true")
        private boolean hasNext;
        
        @Schema(description = "Indica se tem página anterior", example = "false")
        private boolean hasPrevious;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Links de navegação")
    public static class PageLinks {
        
        @Schema(description = "Link para primeira página", 
                example = "/api/restaurantes?page=0&size=10")
        private String first;
        
        @Schema(description = "Link para última página", 
                example = "/api/restaurantes?page=4&size=10")
        private String last;
        
        @Schema(description = "Link para próxima página", 
                example = "/api/restaurantes?page=1&size=10")
        private String next;
        
        @Schema(description = "Link para página anterior", 
                example = "/api/restaurantes?page=0&size=10")
        private String previous;
    }
}