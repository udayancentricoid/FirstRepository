# CSSE – Controllo Soggetti Sottoposti ad Embargo

This repository includes source content for a KT presentation on **CSSE (Controllo Soggetti Sottoposti ad Embargo)** and **AML screening in banking**.

The requested PowerPoint `.pptx` binary cannot be reliably generated directly with the available file-writing tools in this environment, so this file provides a complete, professional slide deck outline that can be imported into PowerPoint or used to generate a presentation.

## Suggested Theme
- Primary color: `#0B1F3A` (Dark Blue)
- Secondary color: `#0E7490` (Teal)
- Accent color: `#D4A017` (Gold)
- Background: `#F8FAFC` (Light Gray)
- Fonts: Aptos / Calibri

## Slide Deck

### 1. Title Slide
**Title:** CSSE – Controllo Soggetti Sottoposti ad Embargo  
**Subtitle:** Knowledge Transfer Session – AML Screening in Banking

### 2. Agenda
- Introduction to AML in Banking
- What is CSSE?
- Why Banks Need CSSE / AML
- Regulatory Authorities
- Main Purpose of CSSE Screening
- How CSSE Works – End-to-End Flow
- Where CSSE Is Used in Banking
- Important AML Concepts
- Common Technologies Used
- Real Banking Example
- Key Takeaways

### 3. Anti-Money Laundering (AML) in Banking
AML applications are used by banks and financial institutions to:
- Detect suspicious financial activities
- Prevent money laundering
- Prevent terrorist financing
- Screen customers against sanctions and embargo lists
- Comply with international and local regulations

### 4. What is CSSE?
CSSE checks whether a customer, company, beneficiary, sender, receiver, or transaction is linked to:
- Sanctioned individuals
- Embargoed countries
- Blacklisted organizations
- Politically Exposed Persons (PEPs)
- Terrorist organizations
- Fraud-risk entities

### 5. Why Banks Use CSSE / AML Systems
Banks must comply with AML and sanctions regulations.

If they fail:
- Huge penalties and fines
- Banking license impact
- International transaction restrictions
- Reputation damage
- Legal investigations

### 6. AML Governance in Europe and Italy
In Italy and across the European Union, AML rules are decided and enforced by multiple authorities working together.

### 7. Regulatory Organizations Driving AML / CSSE Compliance
1. FATF
2. OFAC
3. European Union sanctions authorities
4. United Nations Security Council
5. Local Central Banks / Regulators

### 8. Main Purpose of CSSE Screening
The system verifies whether the following match restricted or sanctioned records:
- Customer name
- Company name
- IBAN / Account number
- SWIFT / BIC
- Country
- Beneficiary details
- Transaction references

### 9. Common Lists Used in AML / CSSE
- FBE
- WCL
- IER
- SGR-HEXA
- Moody’s
- CSFR
- Younique category

### 10. How CSSE Works in Banking
Step-by-step flow:
1. Customer Onboarding (KYC)
2. Name Screening
3. Risk Scoring
4. Alert Generation
5. Compliance Investigation
6. Decision / Action

### 11. Step 1 – Customer Onboarding (KYC)
Bank collects:
- Name
- DOB
- Passport
- PAN
- Aadhaar
- Company registration
- Country

### 12. Step 2 – Name Screening
Customer data is compared against:
- OFAC
- UN sanctions lists
- EU sanctions lists
- Local sanctions lists
- Internal watchlists

Matching techniques:
- Fuzzy matching
- Phonetic algorithms
- Transliteration matching

### 13. Step 3 – Risk Scoring
Risk levels:
- Low
- Medium
- High
- Critical

Factors:
- Country risk
- Transaction volume
- Sanctions hit
- PEP status
- Adverse news

### 14. Step 4 – Alert Generation
If a potential match is detected:
- Alert generated
- Compliance team notified

Possible outcomes:
- False positive
- True match
- Escalation
- Freeze transaction

### 15. Step 5 – Compliance Investigation
Compliance officers review:
- Documents
- Customer profile
- Transaction purpose
- Sanctions evidence
- Historical activity

### 16. Step 6 – Final Decision
Bank may:
- Approve
- Reject
- Freeze funds
- Report to regulator

### 17. Where AML / CSSE Is Used in Banking
- Core Banking
- SWIFT Payments
- Trade Finance
- Remittance Systems
- Internet Banking
- Treasury & Forex

### 18. Important AML Concepts
- KYC – Know Your Customer
- CDD – Customer Due Diligence
- EDD – Enhanced Due Diligence
- PEP Screening
- Sanction Screening
- Transaction Monitoring

### 19. Technologies Commonly Used
Backend:
- Java
- Spring Boot
- Oracle DB
- PL/SQL

Messaging:
- Kafka
- MQ

Integration:
- REST / SOAP
- SWIFT interfaces

Matching:
- Fuzzy search
- Soundex
- AI/ML risk scoring

### 20. Real Banking Example
Suppose a customer sends money to another country.

CSSE checks:
- Sender name
- Receiver name
- Bank
- Country
- SWIFT code

If destination country is embargoed:
- Transaction blocked immediately

If beneficiary appears in sanctions list:
- Alert raised
- Compliance review initiated

### 21. Key Takeaways
- CSSE is a critical AML screening module in banking
- It helps identify sanctioned or high-risk entities
- It supports regulatory compliance and risk reduction
- Screening is used during onboarding and transactions
- Alerts are reviewed by compliance teams before final action

### 22. Q&A
**Thank You**

---

## Next Step
To create an actual `.pptx` file, open this outline in Microsoft PowerPoint or Google Slides and apply the suggested theme/colors.
