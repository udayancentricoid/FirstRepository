# First Repository

## Spring Boot Simple SOAP Web Service

This repository contains a complete, production-style Spring Boot SOAP web service built with **Spring-WS**.  Every annotation, class, and configuration bean is documented so you can understand both the _what_ and the _why_.

---

## Table of Contents

1. [Project Structure](#1-project-structure)
2. [Technology Stack](#2-technology-stack)
3. [Layer-by-Layer Explanation](#3-layer-by-layer-explanation)
   - [Model Layer](#31-model-layer)
   - [Service / Data Layer](#32-service--data-layer)
   - [Endpoint (Web) Layer](#33-endpoint-web-layer)
   - [Configuration Layer](#34-configuration-layer)
   - [Application Entry Point](#35-application-entry-point)
4. [Request Flow (End-to-End)](#4-request-flow-end-to-end)
5. [Key Annotations Cheat-Sheet](#5-key-annotations-cheat-sheet)
6. [How to Run](#6-how-to-run)
7. [Testing the Service](#7-testing-the-service)

---

## 1. Project Structure

```
soap-service/
├── pom.xml                          ← Maven build + dependency declarations
└── src/
    ├── main/
    │   ├── java/com/example/soapservice/
    │   │   ├── SoapServiceApplication.java      ← @SpringBootApplication entry point
    │   │   ├── config/
    │   │   │   └── WebServiceConfig.java        ← @EnableWs + all Spring-WS beans
    │   │   ├── endpoint/
    │   │   │   └── CountryEndpoint.java         ← @Endpoint  (web / SOAP layer)
    │   │   ├── model/
    │   │   │   ├── package-info.java            ← @XmlSchema namespace default
    │   │   │   ├── Country.java                 ← @XmlType domain object
    │   │   │   ├── Currency.java                ← Java enum  (GBP / EUR / USD)
    │   │   │   ├── GetCountryRequest.java       ← @XmlRootElement request payload
    │   │   │   └── GetCountryResponse.java      ← @XmlRootElement response payload
    │   │   └── service/
    │   │       └── CountryRepository.java       ← @Repository in-memory data store
    │   └── resources/
    │       ├── application.properties           ← server.port = 8080
    │       └── countries.xsd                   ← XML Schema (service contract)
    └── test/
        └── java/com/example/soapservice/
            └── CountryEndpointTest.java         ← @SpringBootTest integration tests
```

---

## 2. Technology Stack

| Dependency | Version | Purpose |
|---|---|---|
| `spring-boot-starter-parent` | 3.2.3 | Managed versions, build defaults |
| `spring-boot-starter-web-services` | (managed) | Spring-WS core + embedded Tomcat |
| `wsdl4j` | (managed) | Runtime WSDL generation |
| `jakarta.xml.bind-api` | (managed) | JAXB API annotations |
| `jaxb-impl` | (managed) | JAXB runtime implementation |
| `spring-ws-test` | (managed) | `MockWebServiceClient` for tests |

---

## 3. Layer-by-Layer Explanation

### 3.1 Model Layer

**Files:** `model/package-info.java`, `Country.java`, `Currency.java`, `GetCountryRequest.java`, `GetCountryResponse.java`

**Purpose:** Defines the Java representation of the XML data exchanged in the SOAP messages.  JAXB (Jakarta XML Binding) converts between these Java objects and XML automatically.

#### `package-info.java`

```java
@XmlSchema(
    namespace = "http://example.com/soap/countries",
    elementFormDefault = XmlNsForm.QUALIFIED
)
package com.example.soapservice.model;
```

| Annotation | Explanation |
|---|---|
| `@XmlSchema` | Sets package-wide JAXB defaults so every class in this package uses the same XML namespace without repeating it on every field. |
| `namespace` | Must match `targetNamespace` in `countries.xsd`.  Every XML element in SOAP messages will carry this namespace. |
| `elementFormDefault = QUALIFIED` | Matches `elementFormDefault="qualified"` in the XSD: child elements (`<name>`, `<capital>`, …) must be namespace-qualified, not bare. |

#### `GetCountryRequest.java`

```java
@XmlRootElement(name = "getCountryRequest",
                namespace = "http://example.com/soap/countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryRequest {
    @XmlElement(required = true)
    private String name;
}
```

| Annotation | Explanation |
|---|---|
| `@XmlRootElement` | Marks this class as an XML root element.  `name` matches the element name in the XSD; `namespace` matches the target namespace.  Spring-WS uses this to route incoming SOAP messages. |
| `@XmlAccessorType(XmlAccessType.FIELD)` | Tells JAXB to bind private fields directly, without needing public getters/setters for marshalling. |
| `@XmlElement(required = true)` | Declares the field as a required XML child element.  Validation will reject requests that omit `<name>`. |

#### `Country.java`

```java
@XmlType(name = "country",
         namespace = "http://example.com/soap/countries",
         propOrder = {"name", "population", "capital", "currency"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Country { ... }
```

| Annotation | Explanation |
|---|---|
| `@XmlType` | Maps this class to a **named complex type** in the schema (`<xs:complexType name="country">`).  Unlike `@XmlRootElement`, this class is always nested inside another element. |
| `propOrder` | Enforces XML element order, matching the `xs:sequence` in the XSD.  Required when there is more than one field. |

---

### 3.2 Service / Data Layer

**File:** `service/CountryRepository.java`

**Purpose:** Encapsulates all data-access logic.  The endpoint knows _what_ to ask for but not _how_ data is retrieved – this is the **Separation of Concerns** principle.

```java
@Repository
public class CountryRepository {

    private final Map<String, Country> countries = new HashMap<>();

    @PostConstruct
    public void initData() { /* populate in-memory map */ }

    public Country findCountry(String name) {
        return countries.get(name.toLowerCase());
    }
}
```

| Annotation | Explanation |
|---|---|
| `@Repository` | Specialisation of `@Component`.  Registers the class as a Spring bean AND enables Spring's persistence-exception translation (wraps low-level DB exceptions in `DataAccessException`). |
| `@PostConstruct` | Method is called **once**, after the bean is constructed and all dependencies injected, but before the application accepts requests.  Ideal for one-time initialisation. |

---

### 3.3 Endpoint (Web) Layer

**File:** `endpoint/CountryEndpoint.java`

**Purpose:** The SOAP equivalent of a REST controller.  Receives the deserialised request, calls the service layer, and returns a response.

```java
@Endpoint
public class CountryEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/soap/countries";

    private final CountryRepository countryRepository;

    @Autowired
    public CountryEndpoint(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
        Country country = countryRepository.findCountry(request.getName());
        return new GetCountryResponse(country);
    }
}
```

| Annotation | Explanation |
|---|---|
| `@Endpoint` | Spring-WS variant of `@Component`.  Registers the class as a bean **and** tells Spring-WS's endpoint mapping to scan it for `@PayloadRoot` methods. |
| `@Autowired` (constructor) | Spring injects `CountryRepository` via constructor.  Constructor injection is preferred because it makes dependencies explicit and enables easy unit testing. |
| `@PayloadRoot(namespace, localPart)` | Routes an incoming SOAP message whose body root element matches `{namespace}localPart` to this method.  The values must exactly match the `@XmlRootElement` on `GetCountryRequest`. |
| `@RequestPayload` | Instructs Spring-WS to **unmarshal** (XML → Java) the SOAP body into the method parameter using the configured `Jaxb2Marshaller`. |
| `@ResponsePayload` | Instructs Spring-WS to **marshal** (Java → XML) the return value and place it in the SOAP response body. |

---

### 3.4 Configuration Layer

**File:** `config/WebServiceConfig.java`

**Purpose:** Wires together all Spring-WS infrastructure beans.  Nothing in the endpoint or service layers needs to know _how_ they are connected.

```java
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter { ... }
```

| Annotation / Bean | Explanation |
|---|---|
| `@Configuration` | Declares the class as a bean-definition source.  `@Bean` methods are proxied (CGLIB) so inter-method calls return the same singleton. |
| `@EnableWs` | Activates Spring-WS annotation support: registers `PayloadRootAnnotationMethodEndpointMapping` (routes by `@PayloadRoot`), `DefaultMethodEndpointAdapter`, and default message factories. |
| `extends WsConfigurerAdapter` | Provides hook methods (`addInterceptors`, etc.) to customise Spring-WS behaviour without replacing its defaults. |
| `messageDispatcherServlet()` | Registers `MessageDispatcherServlet` (Spring-WS's SOAP front-controller) under `/ws/*`.  `setTransformWsdlLocations(true)` rewrites the WSDL `location` to match the actual server address. |
| `marshaller()` | Creates a `Jaxb2Marshaller` bound to the model classes.  Spring-WS auto-detects this bean and uses it for all `@RequestPayload`/`@ResponsePayload` marshalling. |
| `defaultWsdl11Definition()` | Auto-generates a WSDL 1.1 document from the XSD at runtime.  Exposed at `http://localhost:8080/ws/countries.wsdl`. |
| `countriesSchema()` | Loads `countries.xsd` and exposes it for WSDL generation and request validation. |
| `addInterceptors()` | Adds `PayloadValidatingInterceptor` which validates every request/response against the XSD _before_ the endpoint method is called.  Invalid messages receive a SOAP Fault automatically. |

---

### 3.5 Application Entry Point

**File:** `SoapServiceApplication.java`

```java
@SpringBootApplication
public class SoapServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoapServiceApplication.class, args);
    }
}
```

| Annotation | Explanation |
|---|---|
| `@SpringBootApplication` | Meta-annotation combining three annotations: **`@SpringBootConfiguration`** (this is a configuration class), **`@EnableAutoConfiguration`** (auto-configure beans based on classpath JARs), and **`@ComponentScan`** (scan this package and sub-packages for `@Component`, `@Endpoint`, `@Repository`, etc.). |

---

## 4. Request Flow (End-to-End)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  SOAP Client                                                                    │
│  POST http://localhost:8080/ws                                                  │
│  Body: <soapenv:Envelope>                                                       │
│          <soapenv:Body>                                                         │
│            <getCountryRequest xmlns="http://example.com/soap/countries">        │
│              <name>Spain</name>                                                 │
│            </getCountryRequest>                                                 │
│          </soapenv:Body>                                                        │
│        </soapenv:Envelope>                                                      │
└───────────────────────────────┬─────────────────────────────────────────────────┘
                                │  HTTP POST /ws
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  MessageDispatcherServlet  (registered in WebServiceConfig)                       │
│  • Parses HTTP request body into a WebServiceMessage (SOAP envelope)              │
│  • Delegates to the endpoint mapping pipeline                                     │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  PayloadValidatingInterceptor  (registered in addInterceptors)                    │
│  • Validates <getCountryRequest> payload against countries.xsd                    │
│  • Returns SOAP Fault if schema validation fails; otherwise continues             │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  PayloadRootAnnotationMethodEndpointMapping  (enabled by @EnableWs)               │
│  • Matches {http://example.com/soap/countries}getCountryRequest                   │
│  • Selects CountryEndpoint#getCountry() as the handler                            │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  Jaxb2Marshaller  (bean in WebServiceConfig)                                      │
│  • Unmarshals XML body → GetCountryRequest Java object  (@RequestPayload)         │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │  GetCountryRequest{ name="Spain" }
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  CountryEndpoint#getCountry()  (@Endpoint)                                        │
│  • Calls countryRepository.findCountry("Spain")                                   │
│  • Wraps result in GetCountryResponse                                             │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │  GetCountryResponse{ country=Country{Spain,…} }
                                ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│  Jaxb2Marshaller  (same bean)                                                     │
│  • Marshals GetCountryResponse → XML  (@ResponsePayload)                          │
└───────────────────────────────┬───────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│  SOAP Response sent to client                                                   │
│  <soapenv:Envelope>                                                             │
│    <soapenv:Body>                                                               │
│      <getCountryResponse xmlns="http://example.com/soap/countries">             │
│        <country>                                                                │
│          <name>Spain</name>                                                     │
│          <population>46754784</population>                                      │
│          <capital>Madrid</capital>                                              │
│          <currency>EUR</currency>                                               │
│        </country>                                                               │
│      </getCountryResponse>                                                      │
│    </soapenv:Body>                                                              │
│  </soapenv:Envelope>                                                            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Key Annotations Cheat-Sheet

| Annotation | Package | Layer | What it does |
|---|---|---|---|
| `@SpringBootApplication` | `org.springframework.boot` | Entry point | Enables auto-configuration + component scan |
| `@Configuration` | `org.springframework.context` | Config | Marks a bean-definition source class |
| `@EnableWs` | `org.springframework.ws` | Config | Activates Spring-WS annotation support |
| `@Bean` | `org.springframework.context` | Config | Declares a method as a bean factory |
| `@Endpoint` | `org.springframework.ws` | Endpoint | SOAP handler class (like `@Controller`) |
| `@PayloadRoot` | `org.springframework.ws` | Endpoint | Routes SOAP messages by namespace + localPart |
| `@RequestPayload` | `org.springframework.ws` | Endpoint | Unmarshals SOAP body → method parameter |
| `@ResponsePayload` | `org.springframework.ws` | Endpoint | Marshals return value → SOAP body |
| `@Autowired` | `org.springframework.beans` | Any | Injects a Spring-managed dependency |
| `@Repository` | `org.springframework.stereotype` | Data | Registers as a bean; enables exception translation |
| `@PostConstruct` | `jakarta.annotation` | Any | Called once after bean construction |
| `@XmlRootElement` | `jakarta.xml.bind` | Model | Maps class to an XML root element |
| `@XmlType` | `jakarta.xml.bind` | Model | Maps class to a named XML complex type |
| `@XmlAccessorType` | `jakarta.xml.bind` | Model | Controls whether JAXB uses fields or getters |
| `@XmlElement` | `jakarta.xml.bind` | Model | Maps a field to an XML child element |
| `@XmlSchema` | `jakarta.xml.bind` | Model | Sets default namespace / element form for a package |

---

## 6. How to Run

```bash
# From the soap-service directory:
cd soap-service
mvn spring-boot:run

# The service starts on http://localhost:8080
# WSDL available at:
curl http://localhost:8080/ws/countries.wsdl
```

---

## 7. Testing the Service

### Run the unit/integration tests

```bash
cd soap-service
mvn test
```

### Send a SOAP request with curl

```bash
curl -s -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml" \
  -d '
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:gs="http://example.com/soap/countries">
  <soapenv:Body>
    <gs:getCountryRequest>
      <gs:name>Spain</gs:name>
    </gs:getCountryRequest>
  </soapenv:Body>
</soapenv:Envelope>'
```

**Expected response:**

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Body>
    <ns2:getCountryResponse xmlns:ns2="http://example.com/soap/countries">
      <ns2:country>
        <ns2:name>Spain</ns2:name>
        <ns2:population>46754784</ns2:population>
        <ns2:capital>Madrid</ns2:capital>
        <ns2:currency>EUR</ns2:currency>
      </ns2:country>
    </ns2:getCountryResponse>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

### Available countries

| Name | Capital | Currency |
|---|---|---|
| Spain | Madrid | EUR |
| United Kingdom | London | GBP |
| United States | Washington D.C. | USD |

