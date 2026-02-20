package com.example.soapservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SoapServiceApplication  –  Application Entry Point
 * ════════════════════════════════════════════════════
 * The {@code main} method is the single entry point for the JVM.
 * {@link SpringApplication#run} bootstraps the entire Spring application context.
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @SpringBootApplication}</dt>
 *   <dd>
 *     A convenience meta-annotation that combines three annotations:
 *     <ol>
 *       <li>
 *         <strong>{@code @SpringBootConfiguration}</strong> –
 *         Designates this class as a configuration class (extends
 *         {@code @Configuration}) and indicates that the Spring Boot
 *         test infrastructure should look for {@code @Bean} definitions here.
 *       </li>
 *       <li>
 *         <strong>{@code @EnableAutoConfiguration}</strong> –
 *         Tells Spring Boot to automatically configure the application
 *         based on the JARs on the classpath.  For example, because
 *         {@code spring-ws-core} is present, Spring Boot auto-configures
 *         the Spring-WS message factories and marshaller infrastructure.
 *       </li>
 *       <li>
 *         <strong>{@code @ComponentScan}</strong> –
 *         Scans the package of this class (and all sub-packages) for
 *         Spring-managed components:
 *         {@code @Component}, {@code @Service}, {@code @Repository},
 *         {@code @Controller}, {@code @Endpoint}, {@code @Configuration},
 *         etc.  This is how Spring discovers
 *         {@code CountryEndpoint}, {@code CountryRepository}, and
 *         {@code WebServiceConfig} without any explicit bean wiring.
 *       </li>
 *     </ol>
 *   </dd>
 * </dl>
 *
 * <h2>Startup sequence</h2>
 * <ol>
 *   <li>JVM invokes {@code main()}.</li>
 *   <li>{@code SpringApplication.run()} creates a
 *       {@code ConfigurableApplicationContext}.</li>
 *   <li>Auto-configuration and component scanning populate the context
 *       with beans.</li>
 *   <li>{@code WebServiceConfig} registers the
 *       {@code MessageDispatcherServlet} under {@code /ws/*}.</li>
 *   <li>{@code CountryRepository#initData()} (@PostConstruct) populates
 *       the in-memory country map.</li>
 *   <li>The embedded Tomcat server starts on port 8080.</li>
 *   <li>The WSDL is available at
 *       {@code http://localhost:8080/ws/countries.wsdl}.</li>
 * </ol>
 */
@SpringBootApplication
public class SoapServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapServiceApplication.class, args);
    }
}
