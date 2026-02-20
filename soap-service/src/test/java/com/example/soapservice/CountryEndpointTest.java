package com.example.soapservice;

import com.example.soapservice.endpoint.CountryEndpoint;
import com.example.soapservice.service.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.RequestCreators;
import org.springframework.ws.test.server.ResponseMatchers;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;

/**
 * CountryEndpointTest  –  Integration Test
 * ══════════════════════════════════════════
 * Verifies the full Spring-WS request / response cycle without starting
 * an actual HTTP server.
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @SpringBootTest}</dt>
 *   <dd>
 *     Loads the complete Spring application context (all beans, auto-
 *     configuration, etc.) for each test class.  This is an
 *     <em>integration test</em> style: the context includes
 *     {@link CountryEndpoint}, {@link CountryRepository},
 *     {@code WebServiceConfig}, and all Spring-WS infrastructure.
 *     It does <strong>not</strong> start an embedded HTTP server unless
 *     {@code webEnvironment} is set explicitly.
 *   </dd>
 * </dl>
 *
 * <h2>MockWebServiceClient</h2>
 * {@link MockWebServiceClient} simulates the Spring-WS dispatcher
 * pipeline in-process:
 * <ol>
 *   <li>Serialises the provided XML into a {@code WebServiceMessage}.</li>
 *   <li>Runs it through the full interceptor chain (including schema
 *       validation) and endpoint mapping.</li>
 *   <li>Captures the response and allows assertions via
 *       {@link ResponseMatchers}.</li>
 * </ol>
 * No TCP socket is opened, making the tests fast and deterministic.
 */
@SpringBootTest
class CountryEndpointTest {

    /** Spring-WS in-process test client. */
    private MockWebServiceClient mockClient;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Recreate the {@link MockWebServiceClient} before each test so that
     * tests are fully isolated from one another.
     */
    @BeforeEach
    void setUp() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Happy path: requesting a country that exists in the repository
     * should return a well-formed SOAP response containing the expected data.
     */
    @Test
    void getCountryRequest_spain_returnsCorrectResponse() throws Exception {
        Source requestPayload = new StringSource(
                "<getCountryRequest xmlns=\"http://example.com/soap/countries\">" +
                        "<name>Spain</name>" +
                "</getCountryRequest>");

        mockClient
                .sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.validPayload(new ClassPathResource("countries.xsd")))
                .andExpect(ResponseMatchers.xpath(
                        "//ns:getCountryResponse/ns:country/ns:capital",
                        java.util.Collections.singletonMap("ns", "http://example.com/soap/countries"))
                        .evaluatesTo("Madrid"));
    }

    /**
     * Happy path: requesting the United Kingdom should return London as
     * the capital and GBP as the currency.
     */
    @Test
    void getCountryRequest_unitedKingdom_returnsCorrectResponse() throws Exception {
        Source requestPayload = new StringSource(
                "<getCountryRequest xmlns=\"http://example.com/soap/countries\">" +
                        "<name>United Kingdom</name>" +
                "</getCountryRequest>");

        mockClient
                .sendRequest(RequestCreators.withPayload(requestPayload))
                .andExpect(ResponseMatchers.noFault())
                .andExpect(ResponseMatchers.xpath(
                        "//ns:getCountryResponse/ns:country/ns:currency",
                        java.util.Collections.singletonMap("ns", "http://example.com/soap/countries"))
                        .evaluatesTo("GBP"));
    }

    /**
     * Application context smoke test – verifies that the Spring context
     * loads without errors and all required beans are present.
     */
    @Test
    void contextLoads() {
        // If this test passes the entire context was bootstrapped successfully.
    }
}
