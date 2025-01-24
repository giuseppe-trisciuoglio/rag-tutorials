package com.github.rag.tutorials.knowledge;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvReadException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.rag.tutorials.dto.Document;
import com.github.rag.tutorials.utils.EnvUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class KnowledgeStore {
    private final static String KNOWLEDGE_FILE_DB_PATH = "KNOWLEDGE_FILE_DB_PATH";
    private final CsvMapper csvMapper = new CsvMapper();

    public Optional<Document> findByName(String name) {
        Map<String, Document> knowledgeBase = readKnowledgeBase();
        if (knowledgeBase.containsKey(name)) {
            return Optional.of(knowledgeBase.get(name));
        }
        return Optional.empty();
    }

    public void save(Document document) {
        final Map<String, Document> knowledgeBase = readKnowledgeBase();
        if (knowledgeBase.containsKey(document.getFileName())) {
            log.info("Updating document: {}", document.getFileName());
            knowledgeBase.put(document.getFileName(), document);
        } else {
            log.info("Saving document: {}", document.getFileName());
            knowledgeBase.put(document.getFileName(), document);
        }
        saveKnowledgeBase(knowledgeBase);
    }

    private void saveKnowledgeBase(Map<String, Document> knowledgeBase) {
        CsvSchema schema = csvMapper.schemaFor(Document.class).withHeader();
        try {
            csvMapper.writer(schema).writeValue(new File(EnvUtils.getEnv(KNOWLEDGE_FILE_DB_PATH)), knowledgeBase.values());
        } catch (IOException e) {
            log.error("Error saving knowledge base", e);
            throw new RuntimeException(e);
        }
    }

    private Map<String, Document> readKnowledgeBase() {
        Map<String, Document> knowledgeBase = new HashMap<>();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Document> documentMappingIterator;
        try {
            documentMappingIterator = csvMapper
                    .readerFor(Document.class)
                    .with(schema)
                    .readValues(new File(EnvUtils.getEnv(KNOWLEDGE_FILE_DB_PATH)));
            List<Document> documents = documentMappingIterator.readAll();
            for (Document document : documents) {
                knowledgeBase.put(document.getFileName(), document);
            }
        } catch (CsvReadException e) {
            log.warn("Knowledge base is empty");
        } catch (IOException e) {
            log.error("Error reading knowledge base", e);
            throw new RuntimeException(e);
        }
        return knowledgeBase;
    }

    public void save(List<Document> documents) {
        Map<String, Document> knowledgeBase = readKnowledgeBase();
        documents.forEach(document -> knowledgeBase.put(document.getFileName(), document));
        saveKnowledgeBase(knowledgeBase);
    }
}
