package com.github.rag.tutorials.ai;

import com.github.rag.tutorials.dto.Document;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ClassificationExtractor {
    @UserMessage("""
      Extracts author name, publication date, main topic, related topics, and document quality.
      Return only JSON, without any markdown markup.
      Here are the contents of the document:
      ---
      {{textDocument}}
      """)
    Document extract(@V("textDocument") String textDocument);
}
