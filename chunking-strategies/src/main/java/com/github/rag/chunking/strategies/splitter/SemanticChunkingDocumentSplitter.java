package com.github.rag.chunking.strategies.splitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.HierarchicalDocumentSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static dev.langchain4j.internal.Utils.isNullOrBlank;

/**
 * A DocumentSplitter implementation that divides documents based on semantic coherence.
 * This splitter groups text segments that are semantically similar, identifying topic
 * boundaries where the semantic similarity drops below a specified threshold.
 * <p>
 * This approach creates more meaningful chunks compared to fixed-size or delimiter-based
 * splitting, which is particularly useful for RAG (Retrieval-Augmented Generation) applications.
 */
public class SemanticChunkingDocumentSplitter extends HierarchicalDocumentSplitter {

    private final EmbeddingModel embeddingModel;
    private final double similarityThreshold;
    private final int bufferSize;
    private final int minSegmentSize;
    private final int maxSegmentSize;
    private final DocumentSplitter sentenceSplitter;

    /**
     * Creates a SemanticChunkingDocumentSplitter with the specified parameters.
     *
     * @param embeddingModel      The embedding model used to generate vector representations
     *                            of text segments for similarity calculation.
     * @param similarityThreshold The minimum cosine similarity required for segments
     *                            to be considered part of the same chunk (0.0 to 1.0).
     * @param bufferSize          The number of sentences to consider at once for similarity
     *                            calculation. A value of 1 means comparing each sentence only
     *                            with its immediate neighbor.
     * @param minSegmentSize      The minimum number of sentences in a segment.
     * @param maxSegmentSize      The maximum number of sentences in a segment.
     */
    public SemanticChunkingDocumentSplitter(
            EmbeddingModel embeddingModel,
            double similarityThreshold,
            int bufferSize,
            int minSegmentSize,
            int maxSegmentSize) {
        super(1, 1);
        this.embeddingModel = Objects.requireNonNull(embeddingModel, "embeddingModel cannot be null");
        this.similarityThreshold = validateThreshold(similarityThreshold);
        this.bufferSize = validatePositive(bufferSize, "bufferSize");
        this.minSegmentSize = validatePositive(minSegmentSize, "minSegmentSize");
        this.maxSegmentSize = validatePositive(maxSegmentSize, "maxSegmentSize");
        this.sentenceSplitter = new DocumentBySentenceSplitter(1, 1);

        if (minSegmentSize > maxSegmentSize) {
            throw new IllegalArgumentException("minSegmentSize cannot be greater than maxSegmentSize");
        }
    }

    public SemanticChunkingDocumentSplitter(int maxSegmentSizeInChars,
                                            int maxOverlapSizeInChars) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, null);
    }

    public SemanticChunkingDocumentSplitter(int maxSegmentSizeInChars,
                                            int maxOverlapSizeInChars,
                                            DocumentSplitter subSplitter) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, subSplitter);
    }

    public SemanticChunkingDocumentSplitter(int maxSegmentSizeInTokens,
                                            int maxOverlapSizeInTokens,
                                            TokenCountEstimator tokenCountEstimator) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, null);
    }

    public DocumentByParagraphSplitter(int maxSegmentSizeInTokens,
                                       int maxOverlapSizeInTokens,
                                       TokenCountEstimator tokenCountEstimator,
                                       DocumentSplitter subSplitter) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, subSplitter);
    }

    /**
     * Creates a SemanticChunkingDocumentSplitter with default parameters.
     *
     * @param embeddingModel The embedding model used to generate vector representations
     *                       of text segments for similarity calculation.
     */
    public SemanticChunkingDocumentSplitter(EmbeddingModel embeddingModel) {
        this(embeddingModel, 0.7, 1, 1, 10);
    }

    @Override
    public List<TextSegment> split(Document document) {
        if (document == null || isNullOrBlank(document.text())) {
            return Collections.emptyList();
        }

        // Split the document into sentences first
        List<TextSegment> sentences = sentenceSplitter.split(document);
        if (sentences.isEmpty()) {
            return Collections.emptyList();
        }

        if (sentences.size() <= minSegmentSize) {
            // If the document is too short, return it as a single segment
            return Collections.singletonList(TextSegment.from(document.text(), document.metadata()));
        }

        // Create sentence groups based on buffer size
        List<String> sentenceGroups = createSentenceGroups(sentences);

        // Generate embeddings for all sentence groups
        List<Embedding> embeddings = generateEmbeddings(sentenceGroups);

        // Find split points based on semantic similarity
        List<Integer> splitPoints = findSplitPoints(embeddings);

        // Create final segments
        return createSemanticSegments(sentences, splitPoints, document);
    }

    private List<String> createSentenceGroups(List<TextSegment> sentences) {
        List<String> groups = new ArrayList<>();

        for (int i = 0; i < sentences.size(); i += bufferSize) {
            StringBuilder groupText = new StringBuilder();
            for (int j = 0; j < bufferSize && i + j < sentences.size(); j++) {
                if (j > 0) {
                    groupText.append(" ");
                }
                groupText.append(sentences.get(i + j).text());
            }
            groups.add(groupText.toString());
        }

        return groups;
    }

    private List<Embedding> generateEmbeddings(List<String> sentenceGroups) {
        List<Embedding> embeddings = new ArrayList<>();

        for (String group : sentenceGroups) {
            embeddings.add(embeddingModel.embed(group).content());
        }

        return embeddings;
    }

    private List<Integer> findSplitPoints(List<Embedding> embeddings) {
        List<Integer> splitPoints = new ArrayList<>();
        splitPoints.add(0); // Always start with 0

        for (int i = 0; i < embeddings.size() - 1; i++) {
            double similarity = calculateCosineSimilarity(embeddings.get(i), embeddings.get(i + 1));

            if (similarity < similarityThreshold) {
                splitPoints.add((i + 1) * bufferSize);
            }
        }

        splitPoints.add(embeddings.size() * bufferSize); // Always end with the total length
        return splitPoints;
    }

    private double calculateCosineSimilarity(Embedding embedding1, Embedding embedding2) {
        float[] vector1 = embedding1.vector();
        float[] vector2 = embedding2.vector();

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            normA += vector1[i] * vector1[i];
            normB += vector2[i] * vector2[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<TextSegment> createSemanticSegments(List<TextSegment> sentences,
                                                     List<Integer> splitPoints,
                                                     Document document) {
        List<TextSegment> segments = new ArrayList<>();

        for (int i = 0; i < splitPoints.size() - 1; i++) {
            int start = splitPoints.get(i);
            int end = Math.min(splitPoints.get(i + 1), sentences.size());

            // Ensure segments meet size constraints
            if (end - start < minSegmentSize && i < splitPoints.size() - 2) {
                continue; // Merge with next segment
            }

            while (end - start > maxSegmentSize) {
                // Split large segments
                int splitAt = start + maxSegmentSize;
                segments.add(createSegment(sentences, start, splitAt, document));
                start = splitAt;
            }

            if (start < end) {
                segments.add(createSegment(sentences, start, end, document));
            }
        }

        return segments;
    }

    private TextSegment createSegment(List<TextSegment> sentences, int start, int end, Document document) {
        StringBuilder segmentText = new StringBuilder();

        for (int i = start; i < end; i++) {
            if (i > start) {
                segmentText.append(" ");
            }
            segmentText.append(sentences.get(i).text());
        }

        return TextSegment.from(segmentText.toString(), document.metadata());
    }

    private double validateThreshold(double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("similarityThreshold must be between 0.0 and 1.0");
        }
        return threshold;
    }

    private int validatePositive(int value, String paramName) {
        if (value <= 0) {
            throw new IllegalArgumentException(paramName + " must be positive");
        }
        return value;
    }

    @Override
    protected String[] split(String s) {
        return new String[0];
    }

    @Override
    public String joinDelimiter() {
        return " ";
    }

    @Override
    protected DocumentSplitter defaultSubSplitter() {
        return sentenceSplitter;
    }

}
