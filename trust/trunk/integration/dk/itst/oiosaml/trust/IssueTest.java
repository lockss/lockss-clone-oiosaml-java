package dk.itst.oiosaml.trust;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.wsaddressing.RelatesTo;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponseCollection;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dk.itst.oiosaml.common.OIOSAMLConstants;
import dk.itst.oiosaml.sp.model.OIOAssertion;

public class IssueTest extends AbstractTests {
	@Test
	public void testIssue() throws Exception {
		client.getToken();
	}
	
	@Test
	public void responseMustBeSigned() throws Exception {
		client.getToken();

		assertTrue(client.getLastResponse().isSigned());
		assertTrue(client.getLastResponse().verifySignature(stsCredential.getPublicKey()));
	}
	
	@Test
	public void RSTRMustBeCollection() throws Exception {
		client.getToken();
		OIOSoapEnvelope env = client.getLastResponse();
		assertTrue(env.getBody().getClass().toString(), env.getBody() instanceof RequestSecurityTokenResponseCollection);
	}
	
	@Test
	public void tokenMustBeAssertion() throws Exception {
		Assertion token = client.getToken();
		assertNotNull(token);
	}
	
	@Test
	public void tokenMustBeHolderOfKey() throws Exception {
		Assertion token = client.getToken();
		OIOAssertion assertion = new OIOAssertion(token);
		
		assertTrue(assertion.isHolderOfKey());
	}
	
	@Test
	public void assertionMustBeSignedCorrectly() throws Exception {
		Assertion token = client.getToken();
		OIOAssertion assertion = new OIOAssertion(token);
		
		assertTrue(assertion.verifySignature(stsCredential.getPublicKey()));
	}
	
	@Test
	public void relatesToMustBeSigned() throws Exception {
		client.getToken();
		OIOSoapEnvelope res = client.getLastResponse();
		
		RelatesTo rt = res.getHeaderElement(RelatesTo.class);
		String id = rt.getUnknownAttributes().get(TrustConstants.WSU_ID);
		
		boolean found = false;
		NodeList nl = res.getXMLObject().getDOM().getElementsByTagNameNS(javax.xml.crypto.dsig.XMLSignature.XMLNS, "Reference");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			if (("#" + id).equals(e.getAttribute("URI"))) {
				found = true;
				break;
			}
			
		}
		assertTrue(found);
	}
	
	@Test
	public void responseShouldNotContainFrameworkHeader() throws Exception {
		client.getToken();
		OIOSoapEnvelope res = client.getLastResponse();
		Envelope env = (Envelope) res.getXMLObject();
		assertEquals(0, env.getHeader().getUnknownXMLObjects(new QName("urn:liberty:sb:2006-08", "Framework")).size());
	}
	
	@Test
	public void testClaims() throws Exception {
		Assertion token = client.getToken();
		assertEquals(assertion.getAttributeStatements().get(0).getAttributes().size(), token.getAttributeStatements().get(0).getAttributes().size());
		
		client.setClaimsDialect(TrustConstants.CLAIMS_DIALECT_IDENTITY);
		token = client.getToken();
		assertEquals(0, token.getAttributeStatements().size());
		
		client.addClaim(OIOSAMLConstants.ATTRIBUTE_ASSURANCE_LEVEL_NAME);
		
		token = client.getToken();
		assertEquals(1, token.getAttributeStatements().get(0).getAttributes().size());
	}
}
