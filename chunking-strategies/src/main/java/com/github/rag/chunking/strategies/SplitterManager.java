package com.github.rag.chunking.strategies;

import com.github.rag.chunking.strategies.splitter.SemanticChunkingDocumentSplitter;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class SplitterManager {
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingStoreIngestor semanticChunkingIngestor;

    public SplitterManager() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        this.semanticChunkingIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(new SemanticChunkingDocumentSplitter(embeddingModel))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }
    
    public void ingestDocument(Document document) {
        semanticChunkingIngestor.ingest(document);
    }
}
