﻿OIOSAML Library
-----------------------------------------------------------------------------------

This library contains implementation that upholds the OIO-WSTrust spec, and is 
interoperability tested against the java implementation of OIO-WSTrust spec.

-----------------------------------------------------------------------------------

Contents:

Saml2GenevaFix - 	Fix for WCF/Geneva when using IssuedSecurityTokenParameters in your 
			servicebinding with Saml2SecurityToken.

Headers - 		LibertyFrameworkHeader for goverment services.

ClientFactories -	Setup methods for clients.
			WebserviceProviderChannelFactory for setting up communication with a webserviceprovider.
			WSTrustClientFactory for communication with a STS that conforms to the OIOWS-Trust spec.

Binding -		Contains the implementation of the messageprotokol to use.
			Contains bindings for STS and Webserviceproviders.					

-----------------------------------------------------------------------------------