package com.example.soapservice.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * GetCountryResponse  –  Model Layer (SOAP response payload)
 * ════════════════════════════════════════════════════════════
 * Represents the XML body of the SOAP response the service sends back.
 * The serialised XML looks like:
 * <pre>{@code
 * <getCountryResponse xmlns="http://example.com/soap/countries">
 *     <country>
 *         <name>Spain</name>
 *         <population>46754784</population>
 *         <capital>Madrid</capital>
 *         <currency>EUR</currency>
 *     </country>
 * </getCountryResponse>
 * }</pre>
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @XmlRootElement}</dt>
 *   <dd>
 *     Makes this class the root element of the response XML document.
 *     {@code name} and {@code namespace} must match the XSD declaration
 *     {@code <xs:element name="getCountryResponse">}.
 *     The JAXB {@code Marshaller} uses these values when serialising the
 *     Java object back to XML inside the SOAP envelope.
 *   </dd>
 *
 *   <dt>{@code @XmlAccessorType(XmlAccessType.FIELD)}</dt>
 *   <dd>Bind fields directly (same rationale as in {@link GetCountryRequest}).</dd>
 *
 *   <dt>{@code @XmlElement(required = true)}</dt>
 *   <dd>
 *     Declares that {@code country} is a mandatory element in the
 *     serialised XML.  If it is {@code null} at serialisation time,
 *     JAXB will include an xsi:nil attribute instead of omitting the
 *     element (required = true prevents element omission in the schema).
 *   </dd>
 * </dl>
 */
@XmlRootElement(name = "getCountryResponse",
                namespace = "http://example.com/soap/countries")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCountryResponse {

    /** The country data returned to the client. */
    @XmlElement(required = true)
    private Country country;

    // ── No-arg constructor required by JAXB ──────────────────────────────────
    public GetCountryResponse() {
    }

    public GetCountryResponse(Country country) {
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
