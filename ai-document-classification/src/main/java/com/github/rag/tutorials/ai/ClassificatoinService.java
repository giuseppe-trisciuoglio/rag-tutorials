package com.github.rag.tutorials.ai;

import com.github.rag.tutorials.dto.Document;
import com.github.rag.tutorials.utils.EnvUtils;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassificatoinService {
    private static final String OPENAI_API_KEY = "OPENAI_API_KEY";
    
    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(EnvUtils.getEnv(OPENAI_API_KEY))
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
            .logRequests(false)
            .logResponses(false)
            .build();
    
    private final ClassificationExtractor extractor;

    public ClassificatoinService() {
        this.extractor = AiServices.create(ClassificationExtractor.class, model);
    }

    public Document classify(Document document) {
        log.info("Classifying document: {}", document.getFileName());
        if (null == document.getContent()) {
            log.error("Document content is null: {}", document.getFileName());
            return document;
        }
        if (document.getContent().length() >= 1048576) {
            return document;
        }
        Document extractedDocument = extractor.extract(document.getContent());
        log.info("Extracted document: {}", extractedDocument);
        extractedDocument.setFileName(document.getFileName());
        extractedDocument.setContent(document.getContent());
        log.info("Classified ranking: {}", document.getRanking());
        return extractedDocument;
    }
}
