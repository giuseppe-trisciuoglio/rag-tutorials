package com.github.rag.tutorials;

import com.github.rag.tutorials.ai.ClassificatoinService;
import com.github.rag.tutorials.dto.Document;
import com.github.rag.tutorials.knowledge.DocumentLoader;
import com.github.rag.tutorials.knowledge.KnowledgeStore;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class Application {

    public static void main(String[] args) {
        final KnowledgeStore knowledgeStore = new KnowledgeStore();
        final DocumentLoader documentLoader = new DocumentLoader();
        final ClassificatoinService classificatoinService = new ClassificatoinService();

        final List<Document> documents = documentLoader.loadDocuments();
        knowledgeStore.save(documents);
        documents.forEach(document -> {
            log.info("Processing document: {}", document.getFileName());
            Optional<Document> optional = knowledgeStore.findByName(document.getFileName());
            if (optional.isPresent()) {
                log.info("Document already exists: {}", document.getFileName());
                Document existingDocument = optional.get();
                log.info("Existing document ranking: {}", existingDocument.getRanking());
                if (null != existingDocument.getRanking()
                        && existingDocument.getRanking() > document.getRanking()) {
                    return;
                }
            }
            log.info("Classifying document: {}", document.getFileName());
            Document classifiedDocument = classificatoinService.classify(document);
            knowledgeStore.save(classifiedDocument);
        });

    }
}
