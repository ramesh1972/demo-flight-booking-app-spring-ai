# Flight & Sightseeing Chat Interface

## 1. Introduction

Welcome to the **Flight & Sightseeing Chat Interface** - a smart travel application that helps you search for flights and discover amazing sightseeing destinations. This application uses artificial intelligence to understand your travel needs and provide personalized recommendations.

The system combines real flight data with tourist attractions using advanced AI technology to give you the best travel planning experience. Whether you're looking for cheap flights or want to know what to see in a new city, this app has got you covered!

---

## 2. Project Structure

```txt
demo-flight-booking-app-spring-ai/         # Java Spring Boot Backend
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java              # Spring Boot entry point with VectorDB init
│   ├── controller/
│   │   ├── AIFlightController.java       # Flight search & booking endpoints
│   │   ├── SightseeingController.java    # Sightseeing search endpoints
│   │   └── ChatController.java           # General chat/message endpoint
│   ├── service/
│   │   ├── FlightAIService.java          # Flight search with RAG & LLM
│   │   ├── SightseeingService.java       # Sightseeing search with flights
│   │   └── ChatService.java              # General message service
│   ├── ai/
│   │   ├── converters/                   # JSON response converters
│   │   │   ├── FlightStructuredOutputConverter.java
│   │   │   ├── SightseeingStructuredOutputConverter.java
│   │   │   └── SightseeingWithFlightsConverter.java
│   │   ├── core/
│   │   │   └── SpringAIChatClient.java   # Azure OpenAI client
│   │   └── templates/                    # Prompt templates
│   │       ├── FlightPromptTemplate.java
│   │       └── SightseeingPromptTemplate.java
│   ├── entities/                         # Data models
│   │   ├── Flight.java
│   │   ├── BookedFlight.java
│   │   ├── Sightseeing.java
│   │   └── SightseeingInfo.java
│   ├── vectordb/                         # In-memory vector database
│   │   ├── DemoVectorDB.java
│   │   ├── SearchResult.java
│   │   └── DocumentChunk.java
│   └── data/
│       ├── jsons/
│       │   └── flights.json              # 20 flight records
│       └── docs/
│           ├── mumbai-sightseeing.txt
│           ├── delhi-sightseeing.txt
│           └── chennai-sightseeing.txt
├── pom.xml                               # Maven dependencies
├── target/                               # Build output
└── README.md                             # This file
```

## 3. Features

### Flight Search

- Search flights by departure city, destination city, or date
- Get a list of all available flights with prices and timings
- Make flight optional - search with just one parameter
- Book multiple flights for round trips

### Sightseeing Discovery

- Explore tourist attractions in any city
- Get information about best times to visit
- Learn estimated costs for sightseeing
- Discover places with detailed descriptions

### Smart Travel Planning

- Search sightseeing locations with flights together
- Get outgoing flights from a destination
- View all booked flights in one place
- Make round trip bookings (return flights optional)

### AI-Powered

- Uses Azure OpenAI for intelligent responses
- RAG (Retrieval Augmented Generation) for accurate data
- Structured output for consistent results

---

## 3. How to Use with Swagger

### Access Swagger Documentation

1. Start the application
2. Open your browser and go to: `http://localhost:8080/swagger-ui.html`
3. You will see all available API endpoints

## 4. Spring AI Features

### Features Used

1. **ChatClient**
   - Sends prompts to Azure OpenAI
   - Gets responses as structured data
   - Handles streaming and batch responses

2. **Prompt Templates**
   - Create flexible prompts with variables
   - Reuse prompts for different queries
   - Format consistency across requests

3. **Structured Output Converters**
   - Convert AI responses to Java objects
   - Handle JSON parsing automatically
   - Clean up formatting issues

4. **Azure OpenAI Integration**
   - Uses Azure's powerful language models
   - Secure and enterprise-ready
   - Fast response times

## 5. Example Flows

### Use Case 1: RAG with VectorDB for Flight Search

```txt
User Request: "Show flights to Mumbai"
    ↓
1. FlightAIService receives request
    ↓
2. RAG Search: vectorDB.search("flights", "Mumbai")
    ↓
3. VectorDB returns matching documents:
   {
     "airline": "Air India",
     "flightNo": "AI202",
     "departureCity": "Delhi",
     "destinationCity": "Mumbai",
     "price": 3500
   }
    ↓
4. Context created from VectorDB results:
   "Available flights: Air India AI202 from Delhi to Mumbai at Rs 3500..."
    ↓
5. Context passed to next step
```

### Use Case 2: Prompt Template Building

```txt
Input: 
  - User query: "Mumbai"
  - Context: "Air India AI202, IndiGo 6E101..."
    ↓
FlightPromptTemplate.createFlightSearchPromptForList()
    ↓
Template Variables:
  {
    "from": "Delhi",
    "to": "Mumbai",
    "date": null,
    "context": "Air India AI202...",
    "format": FlightStructuredOutputConverter.getFormat()
  }
    ↓
Generated Prompt:
  """
  Based on the following flight database:
  Air India AI202, IndiGo 6E101...
  
  User wants flights to Mumbai.
  Return ALL matching flights as JSON array:
  [
    {
      "airline": "string",
      "flightNo": "string",
      ...
    }
  ]
  """
```

### Use Case 3: Structured Output Conversion

```txt
Azure OpenAI Response:
  ```json
  {
    "airline": "Air India",
    "flightNo": "AI202",
    "departureCity": "Delhi",
    "destinationCity": "Mumbai",
    "departureDate": "2025-01-25T10:30:00",
    "price": 3500.0
  }
  ```

FlightStructuredOutputConverter.convertResponse(response)
    ↓

1. Strip markdown: replaceAll("```json\\n?", "")
    ↓
2. Parse JSON to Flight object using ObjectMapper
    ↓
3. Return: Flight {
     airline: "Air India",
     flightNo: "AI202",
     ...
   }

```txt

### Use Case 4: Complete End-to-End Flow

```

HTTP Request: GET /api/ai/flights/search?to=Mumbai
    ↓
AIFlightController.searchFlight(null, "Mumbai", null)
    ↓
FlightAIService.searchFlight(null, "Mumbai", null)
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 1: RAG - Search VectorDB                      │
│   vectorDB.search("flights", "Mumbai")             │
│   Result: 4 matching flight records                │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 2: Build Context String                       │
│   context = results.stream()                       │
│             .map(DocumentChunk::getContent)        │
│             .collect(Collectors.joining("\n"))     │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 3: Create Prompt with Template                │
│   FlightPromptTemplate.createFlightSearchPrompt... │
│   Variables: {to: "Mumbai", context: "..."}        │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 4: Send to Azure OpenAI via Spring AI         │
│   ChatClient.create(chatModel)                     │
│            .prompt(llmPrompt)                      │
│            .call()                                 │
│            .entity(new ParameterizedTypeReference  │
│                    <List<Flight>>(){})             │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 5: Azure OpenAI Processing                    │
│   - Analyzes prompt + context                      │
│   - Generates structured JSON response             │
│   - Returns list of Flight objects                 │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 6: Converter Parses Response                  │
│   FlightStructuredOutputConverter                  │
│   - Strips markdown formatting                     │
│   - Deserializes JSON to List<Flight>              │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 7: Return to Controller                       │
│   List<Flight> flights = [AI202, 6E101, ...]       │
└─────────────────────────────────────────────────────┘
    ↓
HTTP Response: 200 OK
[
  {
    "airline": "Air India",
    "flightNo": "AI202",
    "departureCity": "Delhi",
    "destinationCity": "Mumbai",
    "price": 3500
  },
  ...
]

```txt

#### Use Case 5: Multi-Service Orchestration (Sightseeing + Flights)

```

Request: GET /api/sightseeing/search-with-flights?prompt=Mumbai
    ↓
SightseeingService.searchWithFlights("Mumbai")
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 1: RAG for Sightseeing                        │
│   vectorDB.search("sightseeing", "Mumbai")         │
│   Context: "Gateway of India, Marine Drive..."     │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 2: Extract Cities via ChatService             │
│   Prompt: "Extract city names from: Gateway..."    │
│   ChatService.sendMessage(prompt)                  │
│   Result: "Mumbai"                                 │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 3: Get Flights via FlightAIService            │
│   flightAIService.searchFlight(null, "Mumbai", null)│
│   Uses RAG + Prompt Template + Converter           │
│   Result: List<Flight> with 4 flights              │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 4: Combine Contexts                           │
│   sightseeingContext + flightContext               │
│   Create unified prompt with both data             │
└─────────────────────────────────────────────────────┘
    ↓
┌─────────────────────────────────────────────────────┐
│ STEP 5: Generate SightseeingInfo via LLM           │
│   PromptTemplate with combined context             │
│   SightseeingWithFlightsConverter                  │
│   Result: SightseeingInfo with embedded flights    │
└─────────────────────────────────────────────────────┘
    ↓
Response: List<SightseeingInfo> with places + flights

## 6. VectorDB Design

### Database Structure

┌─────────────────────────────────────────┐
│           VectorDB (In-Memory)          │
├─────────────────────────────────────────┤
│                                         │
│  INDEX: "flights"                       │
│  ├─ Source: data/jsons/flights.json    │
│  ├─ Documents: 20 flight records        │
│  └─ Searchable: airline, route, price   │
│                                         │
│  INDEX: "sightseeing"                   │
│  ├─ Source: data/docs/*.txt             │
│  ├─ Documents: 3 city descriptions      │
│  └─ Searchable: city, places, details   │
│                                         │
└─────────────────────────────────────────┘

### Search Process

Query: "Mumbai flights"
    ↓
├─ Keyword: "Mumbai"
│
├─ Search "flights" Index
│  ├─ Match destination_city = "Mumbai"
│  └─ Return: [AI202, 6E101, SG020, ...]
│
├─ Search "sightseeing" Index
│  ├─ Match city = "Mumbai"
│  └─ Return: [sightseeing info]
│
└─ Return Results to Service

### Index Details

| Index | Type | Records | Fields |
|-------|------|---------|--------|
| flights | JSON | 20 | airline, flightNo, departureCity, destinationCity, departureDate, arrivalDate, price |
| sightseeing | TXT | 3 | city, places, description, bestTimeToVisit, averageCost |

### VectorDB Search Process

User Query: "Show flights to Mumbai"
    ↓
Extract Keywords: ["flights", "Mumbai"]
    ↓
VectorDB Index Search:
    ├─ Check "flights" index
    │   ├─ Match "Mumbai" in destination_city ✓
    │   ├─ Found: 6E101, AI202, I5313, SG020
    │   └─ Score: Relevance ranking
    │
    └─ Return top 20 results
    ↓
RAG Context Created:
"Airlines: IndiGo, Air India, SpiceJet
Flight Numbers: 6E101, AI202, I5313, SG020
Destinations: Mumbai
Prices: 2700-4500"
    ↓
Pass to AI with prompt
    ↓
AI generates structured response

## 7. Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Azure OpenAI API key

### Installation

```bash
# Clone the project
git clone <repository-url>

# Navigate to project directory
cd demo-flight-booking-app-spring-ai

# Build the project
mvn clean build

# Run the application
mvn spring-boot:run
```

### API Documentation

Once running, visit: `http://localhost:8080/swagger-ui.html`

---
