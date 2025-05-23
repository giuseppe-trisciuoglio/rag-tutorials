# Smart Context Management
This tutorial shows how to build a **Smart Context** using the RAG System.
Link a post on [Medium.com](https://medium.com/@giuseppetrisciuoglio/advanced-strategies-to-optimize-large-language-model-costs-351c6777afbc) 

## Introduction
Smart Context Management is a key feature of the RAG System. 
It allows to manage the context of the user in a smart way. 
The context is a set of information that describes the user's environment. 
The context can be used to provide personalized services to the user. 
For example, the context can be used to provide recommendations to the user based on his/her preferences. 
The context can also be used to adapt the user interface to the user's needs. 
In this tutorial, we will show how to build a Smart Context using the RAG System.

## Prerequisites
Before starting this tutorial, you need to have the following prerequisites:
- Java Development Kit (JDK) 21 or higher
- Maven 3.6.3 or higher
- Basic knowledge of Java
- Basic knowledge of LangChain4j
- Anthropic API Key for Claude Sonnet 3.5 and Haiku models
- Cohere API Key (To register and get a free API key for Cohere, please visit the following link https://dashboard.cohere.com/welcome/register)

## Building the project
To build the project, you need to follow these steps:
1. Clone the repository
2. Go to the `rag-smart-context` directory
3. Run the following command:
```bash
mvn clean install
```
4. The project will be built and the JAR file will be created in the `target` directory
5. You can run the JAR file using the following command:
```bash
java -jar target/rag-smart-context-1.0-SNAPSHOT.jar
```

## Environment Configuration
The Smart Context Management system requires the following environment variables:
- `COHERE_API_KEY`: the API key for Cohere
- `CLAUDE_API_KEY`: the API key for Claude Sonnet 3.5 model