package com.example.soapservice.config;

import com.example.soapservice.model.Country;
import com.example.soapservice.model.Currency;
import com.example.soapservice.model.GetCountryRequest;
import com.example.soapservice.model.GetCountryResponse;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

/**
 * WebServiceConfig  –  Configuration Layer
 * ══════════════════════════════════════════
 * This class wires together all the infrastructure beans required by
 * Spring-WS.  Think of it as the "wiring diagram" for the SOAP layer.
 *
 * <h2>Layer purpose</h2>
 * The <em>configuration layer</em> separates infrastructure concerns
 * (servlet registration, marshaller setup, WSDL generation, schema
 * validation) from business logic.  Nothing in the endpoint or service
 * layers needs to know <em>how</em> they are wired together.
 *
 * <h2>Spring-WS request processing pipeline</h2>
 * <pre>
 * HTTP POST /ws/*
 *   │
 *   ▼
 * MessageDispatcherServlet        ← registered by messageDispatcherServlet()
 *   │  parses the HTTP request into a WebServiceMessage (SOAP envelope)
 *   ▼
 * PayloadValidatingInterceptor    ← registered by addInterceptors()
 *   │  validates the SOAP body against countries.xsd (returns SOAP fault on error)
 *   ▼
 * PayloadRootAnnotationMethodEndpointMapping
 *   │  matches @PayloadRoot(namespace, localPart) → CountryEndpoint#getCountry()
 *   ▼
 * Jaxb2Marshaller (unmarshalling) ← registered by marshaller()
 *   │  converts XML body → GetCountryRequest Java object
 *   ▼
 * CountryEndpoint#getCountry()
 *   │  calls CountryRepository, builds GetCountryResponse
 *   ▼
 * Jaxb2Marshaller (marshalling)   ← same bean used for marshalling
 *   │  converts GetCountryResponse → XML
 *   ▼
 * SOAP response sent to client
 * </pre>
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @Configuration}</dt>
 *   <dd>
 *     Marks this class as a source of bean definitions.  Spring
 *     processes it at startup and instantiates all {@code @Bean}
 *     methods, registering the results in the application context.
 *     Unlike {@code @Component}, methods annotated with {@code @Bean}
 *     inside a {@code @Configuration} class are proxied so that
 *     inter-bean method calls return the <em>same</em> singleton
 *     instance (CGLIB subclassing).
 *   </dd>
 *
 *   <dt>{@code @EnableWs}</dt>
 *   <dd>
 *     Enables the Spring-WS annotation model.  Internally it imports
 *     {@code WsConfigurationSupport}, which registers:
 *     <ul>
 *       <li>{@code PayloadRootAnnotationMethodEndpointMapping} – routes
 *           requests to the correct {@code @PayloadRoot} method.</li>
 *       <li>{@code DefaultMethodEndpointAdapter} – invokes the method
 *           with the right arguments and handles the return value.</li>
 *       <li>Default message factories and exception resolvers.</li>
 *     </ul>
 *   </dd>
 * </dl>
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    // ── Servlet registration ──────────────────────────────────────────────────

    /**
     * Registers Spring-WS's {@link MessageDispatcherServlet} with the
     * embedded Tomcat server, mapping it to the {@code /ws/*} URL pattern.
     *
     * <p>{@code MessageDispatcherServlet} is analogous to Spring MVC's
     * {@code DispatcherServlet} but purpose-built for SOAP.  It:
     * <ul>
     *   <li>Reads the raw HTTP request body and wraps it in a
     *       {@code WebServiceMessage} (SOAP envelope).</li>
     *   <li>Delegates to registered endpoint mappings to find the
     *       correct {@code @Endpoint} method.</li>
     *   <li>Writes the SOAP response back to the HTTP response.</li>
     * </ul>
     *
     * <p>{@code setTransformWsdlLocations(true)} rewrites the
     * {@code location} attribute in the generated WSDL to reflect
     * the actual host/port used by the incoming request, so the WSDL
     * is always self-consistent regardless of the server address.
     *
     * @param applicationContext the Spring application context, injected
     *                           automatically by Spring into {@code @Bean} methods
     * @return a {@link ServletRegistrationBean} that registers the servlet
     *         under the {@code /ws/*} URL pattern
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {

        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // ── WSDL generation ───────────────────────────────────────────────────────

    /**
     * Produces a WSDL document at runtime from the XSD schema.
     *
     * <p>Spring-WS exposes this bean under the URL
     * {@code /ws/{beanName}.wsdl}, i.e.
     * {@code http://localhost:8080/ws/countries.wsdl}.
     *
     * <p>{@link DefaultWsdl11Definition} auto-generates a WSDL 1.1
     * document by convention:
     * <ul>
     *   <li>Every {@code xs:element} whose name ends in {@code Request}
     *       becomes a WSDL {@code <message>} and a {@code <portType>}
     *       operation input.</li>
     *   <li>The corresponding {@code Response} element becomes the
     *       operation output.</li>
     *   <li>A SOAP binding and a service port are added automatically.</li>
     * </ul>
     *
     * @param countriesSchema the {@link XsdSchema} bean (see below)
     * @return the WSDL definition bean named "countries"
     */
    @Bean(name = "countries")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CountriesPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://example.com/soap/countries");
        wsdl11Definition.setSchema(countriesSchema);
        return wsdl11Definition;
    }

    // ── Marshaller ────────────────────────────────────────────────────────────

    /**
     * Configures the {@link Jaxb2Marshaller} that converts between Java
     * objects and XML for every SOAP message.
     *
     * <p>{@code setClassesToBeBound} tells JAXB exactly which classes
     * participate in marshalling / unmarshalling.  At startup JAXB
     * builds a {@code JAXBContext} for these classes; at request time:
     * <ul>
     *   <li><em>Unmarshalling (XML → Java)</em>: the incoming SOAP body
     *       XML is converted into a {@link GetCountryRequest} instance
     *       and injected into the endpoint method via
     *       {@code @RequestPayload}.</li>
     *   <li><em>Marshalling (Java → XML)</em>: the {@link GetCountryResponse}
     *       returned by the endpoint method is serialised to XML and
     *       placed in the SOAP response body.</li>
     * </ul>
     *
     * <p>Spring-WS's {@code DefaultMethodEndpointAdapter} auto-detects
     * any {@link Jaxb2Marshaller} bean and uses it for all
     * {@code @RequestPayload} / {@code @ResponsePayload} handling.
     *
     * @return the configured JAXB marshaller
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                GetCountryRequest.class,
                GetCountryResponse.class,
                Country.class,
                Currency.class);
        return marshaller;
    }

    // ── Schema ────────────────────────────────────────────────────────────────

    /**
     * Loads {@code countries.xsd} from the classpath and exposes it as
     * a Spring-managed {@link XsdSchema} bean.
     *
     * <p>This bean is used by:
     * <ul>
     *   <li>{@link DefaultWsdl11Definition} to generate the WSDL.</li>
     *   <li>{@link PayloadValidatingInterceptor} (added below) to
     *       validate incoming request payloads.</li>
     * </ul>
     *
     * @return the parsed XSD schema
     */
    @Bean
    public XsdSchema countriesSchema() {
        return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
    }

    // ── Interceptors (optional but recommended) ───────────────────────────────

    /**
     * Registers a {@link PayloadValidatingInterceptor} that validates
     * incoming SOAP request payloads against {@code countries.xsd}
     * <em>before</em> the endpoint method is invoked.
     *
     * <p>If the payload is invalid the interceptor returns a SOAP fault
     * to the client instead of forwarding the request to the endpoint,
     * protecting the service from malformed input without any
     * boilerplate validation code in the endpoint itself.
     *
     * <p>{@code addInterceptors} is a hook provided by
     * {@link WsConfigurerAdapter} (which this class extends).
     * It is the Spring-WS equivalent of
     * {@code WebMvcConfigurer#addInterceptors}.
     */
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.setXsdSchema(countriesSchema());
        interceptors.add(validatingInterceptor);
    }
}
