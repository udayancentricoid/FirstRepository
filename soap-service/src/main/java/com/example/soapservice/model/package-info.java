/**
 * package-info.java  –  JAXB Package-Level Annotation
 * ══════════════════════════════════════════════════════
 * Declares the default XML namespace and element form for all JAXB
 * classes in the {@code com.example.soapservice.model} package.
 *
 * <h2>Why this file is needed</h2>
 * The XSD schema uses {@code elementFormDefault="qualified"}, which means
 * every element in the schema (including child elements like {@code <name>}
 * inside {@code <getCountryRequest>}) must carry the target namespace
 * {@code http://example.com/soap/countries}.
 *
 * Without this annotation, JAXB would map child fields (e.g. the
 * {@code name} field in {@link com.example.soapservice.model.GetCountryRequest})
 * to an element with an empty namespace {@code {""} name}, causing a
 * mismatch during unmarshalling and leaving the field as {@code null}.
 *
 * <h2>Annotations explained</h2>
 *
 * <dl>
 *   <dt>{@code @XmlSchema}</dt>
 *   <dd>
 *     Sets package-wide defaults for JAXB XML binding:
 *     <ul>
 *       <li>{@code namespace} – the default XML namespace applied to all
 *           elements in this package, matching {@code targetNamespace} in
 *           {@code countries.xsd}.</li>
 *       <li>{@code elementFormDefault = XmlNsForm.QUALIFIED} – all elements
 *           must be namespace-qualified in XML instance documents, consistent
 *           with {@code elementFormDefault="qualified"} in the XSD.</li>
 *     </ul>
 *   </dd>
 * </dl>
 */
@XmlSchema(
        namespace = "http://example.com/soap/countries",
        elementFormDefault = XmlNsForm.QUALIFIED
)
package com.example.soapservice.model;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
