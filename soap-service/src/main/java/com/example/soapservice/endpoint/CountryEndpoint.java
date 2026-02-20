package com.example.soapservice.endpoint;

import com.example.soapservice.model.Country;
import com.example.soapservice.model.GetCountryRequest;
import com.example.soapservice.model.GetCountryResponse;
import com.example.soapservice.service.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * CountryEndpoint  –  Web / Endpoint Layer
 * ══════════════════════════════════════════
 * This class is the entry point for all SOAP requests sent to the
 * {@code /ws} path.  Spring-WS inspects every incoming SOAP envelope,
 * extracts the <em>payload</em> (the content of the {@code <Body>}
 * element), and dispatches it to the matching method in this class.
 *
 * <h2>Layer purpose</h2>
 * The <em>endpoint layer</em> (analogous to a controller in REST/MVC)
 * is responsible for:
 * <ol>
 *   <li>Receiving the deserialised request object from Spring-WS.</li>
 *   <li>Calling the service/data layer to do the actual work.</li>
 *   <li>Wrapping the result in a response object and returning it.</li>
 * </ol>
 * It should contain <strong>no</strong> business logic or data-access
 * code – those belong in the service layer.
 *
 * <h2>Request routing in Spring-WS</h2>
 * <pre>
 * Client
 *   │  HTTP POST /ws
 *   ▼
 * MessageDispatcherServlet   ← registered in WebServiceConfig
 *   │  reads SOAP envelope
 *   ▼
 * PayloadRootAnnotationMethodEndpointMapping
 *   │  matches localPart + namespace
 *   ▼
 * CountryEndpoint#getCountry()
 *   │  calls CountryRepository
 *   ▼
 * JAXB Marshaller serialises GetCountryResponse → XML
 *   │
 *   ▼
 * SOAP response sent back to client
 * </pre>
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @Endpoint}</dt>
 *   <dd>
 *     A specialisation of {@code @Component} specific to Spring-WS.
 *     It:
 *     <ul>
 *       <li>Registers the class as a Spring bean.</li>
 *       <li>Marks it as a SOAP endpoint so Spring-WS's
 *           {@code PayloadRootAnnotationMethodEndpointMapping} scans
 *           it for {@code @PayloadRoot}-annotated methods.</li>
 *     </ul>
 *   </dd>
 *
 *   <dt>{@code @PayloadRoot(namespace, localPart)}</dt>
 *   <dd>
 *     Maps an incoming SOAP message to this handler method based on
 *     the <em>root element</em> of the SOAP body payload:
 *     <ul>
 *       <li>{@code namespace} – the XML namespace of the root element
 *           (must match {@code targetNamespace} in countries.xsd).</li>
 *       <li>{@code localPart} – the local name of the root element
 *           (must match the {@code name} attribute of the
 *           {@code @XmlRootElement} on {@link GetCountryRequest}).</li>
 *     </ul>
 *     Only one method in the application should match any given
 *     namespace + localPart combination.
 *   </dd>
 *
 *   <dt>{@code @RequestPayload}</dt>
 *   <dd>
 *     Instructs Spring-WS to unmarshal (deserialise) the SOAP body
 *     payload into the annotated parameter using the configured
 *     {@code Jaxb2Marshaller}.  This is the binding between incoming
 *     XML and the Java type {@link GetCountryRequest}.
 *   </dd>
 *
 *   <dt>{@code @ResponsePayload}</dt>
 *   <dd>
 *     Instructs Spring-WS to marshal (serialise) the return value of
 *     the method back to XML and place it in the {@code <Body>} of the
 *     SOAP response envelope.  Without this annotation the return value
 *     is ignored.
 *   </dd>
 * </dl>
 */
@Endpoint
public class CountryEndpoint {

    /** Namespace constant – must match targetNamespace in countries.xsd. */
    private static final String NAMESPACE_URI = "http://example.com/soap/countries";

    /**
     * Data/service layer dependency injected by Spring.
     * Using constructor injection (preferred over field injection)
     * makes the dependency explicit and the class easier to unit-test.
     */
    private final CountryRepository countryRepository;

    @Autowired
    public CountryEndpoint(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Handles a {@code <getCountryRequest>} SOAP message.
     *
     * <p>Flow:
     * <ol>
     *   <li>Spring-WS unmarshals the SOAP body into {@code request}.</li>
     *   <li>This method queries the {@link CountryRepository} for the
     *       country matching the requested name.</li>
     *   <li>The result is wrapped in a {@link GetCountryResponse} and
     *       returned; Spring-WS marshals it into the SOAP response body.</li>
     * </ol>
     *
     * @param request the deserialised SOAP request payload
     * @return the response payload to be serialised into the SOAP reply
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
        Country country = countryRepository.findCountry(request.getName());
        return new GetCountryResponse(country);
    }
}
