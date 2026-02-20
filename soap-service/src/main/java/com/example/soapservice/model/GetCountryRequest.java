package com.example.soapservice.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * GetCountryRequest  –  Model Layer (SOAP request payload)
 * ══════════════════════════════════════════════════════════
 * Represents the XML body of an incoming SOAP request.
 * The client sends a message whose body looks like:
 * <pre>{@code
 * <getCountryRequest xmlns="http://example.com/soap/countries">
 *     <name>Spain</name>
 * </getCountryRequest>
 * }</pre>
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @XmlRootElement}</dt>
 *   <dd>
 *     Marks this class as the <em>root element</em> of an XML document.
 *     <ul>
 *       <li>{@code name} – the local XML element name, matching
 *           {@code <xs:element name="getCountryRequest">} in the schema.</li>
 *       <li>{@code namespace} – must match the {@code targetNamespace} of
 *           countries.xsd so JAXB generates the correct namespace-qualified
 *           element.</li>
 *     </ul>
 *     When Spring-WS receives a SOAP message it calls the JAXB
 *     {@code Unmarshaller} to convert the XML body into this Java object,
 *     then passes it to the matching {@code @PayloadRoot} method.
 *   </dd>
 *
 *   <dt>{@code @XmlAccessorType(XmlAccessType.FIELD)}</dt>
 *   <dd>Bind fields directly instead of via getters/setters (see {@link Country}).</dd>
 *
 *   <dt>{@code @XmlElement(required = true)}</dt>
 *   <dd>
 *     Marks the {@code name} field as a required XML element.
 *     JAXB will throw a validation error if the element is absent during
 *     unmarshalling (assuming schema validation is enabled).
 *   </dd>
 * </dl>
 */
@XmlRootElement(name = "getCountryRequest",
                namespace = "http://example.com/soap/countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryRequest {

    /** The name of the country to look up, e.g. "Spain". */
    @XmlElement(required = true)
    private String name;

    // ── No-arg constructor required by JAXB ──────────────────────────────────
    public GetCountryRequest() {
    }

    public GetCountryRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
