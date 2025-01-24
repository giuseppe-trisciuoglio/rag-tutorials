package com.github.rag.tutorials.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private String fileName;
    private String author;
    private String mainTopic;
    private Set<String> relatedTopics;
    private Date publicationDate;
    private Long ranking;
    @JsonIgnore
    private String content;

    public Document(String name, String content) {
        this.fileName = name;
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "Document{" +
                "fileName='" + fileName + '\'' +
                ", author='" + author + '\'' +
                ", mainTopic='" + mainTopic + '\'' +
                ", ranking=" + ranking +
                '}';
    }
}
