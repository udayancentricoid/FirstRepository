package com.example.soapservice.service;

import com.example.soapservice.model.Country;
import com.example.soapservice.model.Currency;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * CountryRepository  –  Service / Data Layer
 * ════════════════════════════════════════════
 * Provides access to country data.  In this example the data is stored
 * in an in-memory {@link Map} that is populated once at application
 * startup.  In a real application this class would delegate to a
 * JPA repository, a database call, or a remote API.
 *
 * <h2>Layer purpose</h2>
 * The <em>service / data layer</em> encapsulates all business logic and
 * data-access concerns.  The endpoint class ({@link com.example.soapservice.endpoint.CountryEndpoint})
 * depends on this layer but knows nothing about how or where the data is
 * stored – a classic application of the <em>Separation of Concerns</em>
 * principle.
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @Repository}</dt>
 *   <dd>
 *     A specialisation of {@code @Component} that:
 *     <ul>
 *       <li>Registers this class as a Spring-managed bean so it can be
 *           injected via {@code @Autowired} / constructor injection.</li>
 *       <li>Enables Spring's <em>persistence exception translation</em>:
 *           data-access exceptions (e.g. {@code SQLException}) are
 *           automatically wrapped in Spring's {@code DataAccessException}
 *           hierarchy, decoupling callers from the underlying storage
 *           technology.</li>
 *     </ul>
 *   </dd>
 *
 *   <dt>{@code @PostConstruct}</dt>
 *   <dd>
 *     Marks {@link #initData()} to be called by the Spring container
 *     <em>after</em> the bean has been fully constructed and all
 *     dependency injection is complete, but <em>before</em> the bean is
 *     put into service.  This is the correct place to initialise state
 *     that depends on injected dependencies (e.g. a DataSource).
 *   </dd>
 * </dl>
 */
@Repository
public class CountryRepository {

    /**
     * In-memory store keyed by country name (case-insensitive look-up
     * achieved by normalising to lower-case on both insert and query).
     */
    private final Map<String, Country> countries = new HashMap<>();

    /**
     * Populates the in-memory store with sample data.
     * Executed once after the bean is fully initialised.
     */
    @PostConstruct
    public void initData() {
        Country spain = new Country("Spain", 46_754_784, "Madrid", Currency.EUR);
        countries.put(spain.getName().toLowerCase(), spain);

        Country uk = new Country("United Kingdom", 67_081_234, "London", Currency.GBP);
        countries.put(uk.getName().toLowerCase(), uk);

        Country usa = new Country("United States", 331_002_651, "Washington D.C.", Currency.USD);
        countries.put(usa.getName().toLowerCase(), usa);
    }

    /**
     * Finds a country by name (case-insensitive).
     *
     * @param name the country name to look up
     * @return the matching {@link Country}, or {@code null} if not found
     */
    public Country findCountry(String name) {
        return countries.get(name.toLowerCase());
    }
}
