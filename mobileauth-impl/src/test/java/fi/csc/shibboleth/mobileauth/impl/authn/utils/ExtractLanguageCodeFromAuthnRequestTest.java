/*
 * The MIT License
 * Copyright (c) 2016 CSC - IT Center for Science, http://www.csc.fi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fi.csc.shibboleth.mobileauth.impl.authn.utils;

import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.impl.AuthnRequestBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import fi.csc.shibboleth.mobileauth.api.authn.context.MobileContext;
import fi.csc.shibboleth.mobileauth.impl.authn.ExtractLanguageCodeFromAuthnRequest;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

@SuppressWarnings("rawtypes")
public class ExtractLanguageCodeFromAuthnRequestTest {

    private ExtractLanguageCodeFromAuthnRequest action;
    
    final private String DEFAULT_LANG = "fi";
    
    private String ns;
    
    private String langTag;
    
    private ProfileRequestContext<?, ?> profileCtx;
    
    private MobileContext mobCtx;
    
    private AuthnRequest authreq;
	
    private MessageContext messageContext;
    
    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        
	action = new ExtractLanguageCodeFromAuthnRequest();
        action.setNs(ns);
        action.setLangTag(langTag);
        
        profileCtx = new ProfileRequestContext();
        profileCtx.getSubcontext(AuthenticationContext.class, true);
        authreq = new AuthnRequestBuilder().buildObject();
        messageContext = new MessageContext<>();
        messageContext.setMessage(authreq);
        profileCtx.setInboundMessageContext(messageContext);
    }
    
    @BeforeTest
    public void initTest() {
        ns = "urn:kapa:SAML:2.0:extensions";
        langTag = "kapa";    
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testSuccessLangExtract() throws ComponentInitializationException, ParserConfigurationException {
        action.setHttpServletRequest(new MockHttpServletRequest());
        ((MockHttpServletRequest) action.getHttpServletRequest()).setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
        
        messageContext = new MessageContext<>();
        authreq = new AuthnRequestBuilder().buildObject();
        
        messageContext.setMessage(authreq);
        profileCtx.setInboundMessageContext(messageContext);
        
      	action.initialize();

        action.execute(profileCtx);

        ActionTestingSupport.assertProceedEvent(profileCtx);

        Assert.assertNotNull(profileCtx.getInboundMessageContext());
        Assert.assertEquals(profileCtx.getInboundMessageContext().getMessage(), authreq);
        
        final AuthenticationContext authContext = profileCtx.getSubcontext(AuthenticationContext.class);
        mobCtx = authContext.getSubcontext(MobileContext.class, true);
        Assert.assertEquals(mobCtx.getLang(), DEFAULT_LANG);
        
    }
    
    @Test
    public void testDefaultLang() throws ComponentInitializationException {
	mobCtx = new MobileContext();
        
        Assert.assertNotNull(mobCtx.getLang());
        Assert.assertEquals(mobCtx.getLang(), DEFAULT_LANG);
        
    }
    
    @Test
    public void testFailLangExtract() {
        // TODO        
    }

}
