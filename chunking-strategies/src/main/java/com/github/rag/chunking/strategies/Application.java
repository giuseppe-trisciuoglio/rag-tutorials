package com.github.rag.chunking.strategies;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            final SplitterManager manager = new SplitterManager();
            Document document = loadDocument(toPath("giro_del_mondo_80_giorni.pdf"), new ApachePdfBoxDocumentParser());
            manager.ingestDocument(document);
        } catch (Exception e) {
            log.error("Error during application execution", e);
        }
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = Application.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}