# Rag Tutorial - Chatbot Hotel Booking System
This tutorial will guide you through the process of creating a chatbot hotel booking system using:
- Spring Boot 
- LangChain4j 
- OpenRouter

## Prerequisites
- Java 21
- Maven

## Step 1: Clone repository
Clone the repository using the following command:
```bash
git clone https://github.com/giuseppe-trisciuoglio/rag-tutorials.git
```
```bash
cd chatbot-hotel-booking-system
```
```bash
mvn clean install
```

## Step 2: Environment setup

```bash
export OPENROUTER_API_KEY=YOUR_OPENROUTER_API_KEY
```

## Step 3: Run the application
```bash
mvn spring-boot:run
```

## Step 4: Test the application
Open your browser and navigate to the following URL:
```bash
http://localhost:8080
```

## Demo checkout flow
- In chat, when the user chooses "prenotazione online", Aurora now creates a pre-booking via tool.
- The assistant returns a checkout link like:
```bash
http://localhost:8080/checkout.html?preBookingId=PB-XXXXXXXXXX&amount=2100
```
- Open the link and click "Conferma pagamento" to simulate payment and finalize booking.
