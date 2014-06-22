package com.lcdfx.pipoint.renderer.dlna.xml;

import java.util.Set;

import javax.xml.transform.Source;

import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.lastchange.LastChangeParser;
import org.teleal.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

public class RenderingControlLastChangeParser extends LastChangeParser {
//    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/AVT_RCS";
    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/RCS";
    public static final String SCHEMA_RESOURCE = "org/teleal/cling/support/renderingcontrol/metadata-1.0-rcs.xsd";

    @Override
    protected String getNamespace() {
        return NAMESPACE_URI;
    }

    @Override
    protected Source[] getSchemaSources() {
//		return new Source[]{new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(SCHEMA_RESOURCE);
    	return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Set<Class<? extends EventedValue>> getEventedVariables() {
        return RenderingControlVariable.ALL;
    }
}
