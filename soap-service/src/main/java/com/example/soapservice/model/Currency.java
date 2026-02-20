package com.example.soapservice.model;

/**
 * Currency  –  Model Layer (Enum)
 * ════════════════════════════════
 * A plain Java enum that matches the &lt;xs:simpleType name="currency"&gt;
 * restriction defined in countries.xsd.
 *
 * <p>JAXB can marshal/unmarshal enums automatically: the enum constant
 * name (e.g. {@code GBP}) is written as the XML text value, and the
 * same text is parsed back to the enum constant on the way in.
 *
 * <p>No JAXB annotation is required here because the enum is only ever
 * used as a field inside {@link Country}, which is already annotated
 * with {@code @XmlType}.
 */
public enum Currency {
    GBP,
    EUR,
    USD
}
