package com.example.soapservice.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Country  –  Model Layer (JAXB-annotated domain object)
 * ═══════════════════════════════════════════════════════
 * Represents a single country returned by the SOAP service.
 * This class is used as a nested element inside {@link GetCountryResponse}.
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @XmlType}</dt>
 *   <dd>
 *     Marks this class as a <em>named complex type</em> in XML Schema
 *     (maps to {@code <xs:complexType name="country">} in countries.xsd).
 *     <ul>
 *       <li>{@code name} – the XML type name used in the generated WSDL/XSD.</li>
 *       <li>{@code namespace} – must match the {@code targetNamespace} of countries.xsd
 *           so Spring-WS can correctly marshal/unmarshal the element.</li>
 *       <li>{@code propOrder} – declares the order in which fields are written
 *           to XML, matching the {@code xs:sequence} order in the schema.</li>
 *     </ul>
 *   </dd>
 *
 *   <dt>{@code @XmlAccessorType(XmlAccessType.FIELD)}</dt>
 *   <dd>
 *     Tells JAXB to bind <em>fields</em> (instance variables) directly,
 *     rather than relying on public getter/setter methods.
 *     This keeps the class concise while still producing the correct XML.
 *   </dd>
 * </dl>
 */
@XmlType(name = "country",
         namespace = "http://example.com/soap/countries",
         propOrder = {"name", "population", "capital", "currency"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Country {

    /** Country name, e.g. "Spain". Mapped to {@code <name>} in XML. */
    private String name;

    /** Population in number of people. Mapped to {@code <population>} in XML. */
    private int population;

    /** Capital city name. Mapped to {@code <capital>} in XML. */
    private String capital;

    /** Official currency. Mapped to {@code <currency>} in XML. */
    private Currency currency;

    // ── Constructors ──────────────────────────────────────────────────────────

    /** No-arg constructor required by JAXB for unmarshalling. */
    public Country() {
    }

    public Country(String name, int population, String capital, Currency currency) {
        this.name       = name;
        this.population = population;
        this.capital    = capital;
        this.currency   = currency;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
