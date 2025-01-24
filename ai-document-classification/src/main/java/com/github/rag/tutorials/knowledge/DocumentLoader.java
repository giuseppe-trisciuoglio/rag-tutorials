package com.github.rag.tutorials.knowledge;

import com.github.rag.tutorials.dto.Document;
import com.github.rag.tutorials.utils.EnvUtils;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DocumentLoader {
    private static final String DOCUMENTS_PATH_KEY = "DOCUMENTS_PATH";
    
    public List<Document> loadDocuments() {
        String documentsPath = EnvUtils.getEnv(DOCUMENTS_PATH_KEY);
        log.info("Loading documents from path: {}", documentsPath);
        final List<Document> documents = new ArrayList<>();
        FileUtils.listFiles(new File(documentsPath), new String[]{"pdf", "txt"}, true)
                .forEach(file -> {
                    try {
                        dev.langchain4j.data.document.Document document = FileSystemDocumentLoader.loadDocument(file.getAbsolutePath());
                        log.info("Loaded document: {}", file.getName());
                        documents.add(new Document(file.getName(), document.text()));
                    } catch (Exception e) {
                        log.error("Error loading document: {}", file.getName(), e);
                    }
                });
        return documents;
    }
}
