/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

abstract class AbstractFeatureTask extends AbstractTask {

    private static final String NAME_DELIMITER = "."; //$NON-NLS-1$

    private static final String FEATURE_ELEM = "feature"; //$NON-NLS-1$
    private static final String FEATURE_PFX = FEATURE_ELEM + NAME_DELIMITER;
    private static final String PLUGIN_ELEM = FEATURE_PFX + "plugin"; //$NON-NLS-1$
    private static final String ID_ATTR = "id"; //$NON-NLS-1$
    private static final String VERSION_ATTR = "version"; //$NON-NLS-1$

    protected class InternalName {
        public static final String ID = FEATURE_PFX + ID_ATTR;
        public static final String VERSION = FEATURE_PFX + VERSION_ATTR;
        public static final String PLUGINS = PLUGIN_ELEM + NAME_DELIMITER + ID_ATTR;
        public static final String ECLIPSE_ID_REPLACER_PLUGIN_IDS = "eclipse.idReplacer.pluginIds"; //$NON-NLS-1$
    }

    protected AbstractFeatureTask() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see tools.metamatrix.pakkage.ant.AbstractTask#loadProperties(java.lang.String, java.util.Map)
     */
    @Override
    protected void loadProperties( String folder,
                                   final Map<String, String> properties ) throws Exception {
        File file = new File("feature.xml"); //$NON-NLS-1$
        InputStream stream = new FileInputStream(file);
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(new DefaultHandler() {

            private String elemName = ""; //$NON-NLS-1$
            private StringBuilder plugins = new StringBuilder();
            private StringBuilder eclpiseIdReplacerPluginIds = new StringBuilder();
            private String version;

            @Override
            public void endDocument() {
                properties.put(InternalName.PLUGINS, plugins.toString());
                properties.put(InternalName.ECLIPSE_ID_REPLACER_PLUGIN_IDS, eclpiseIdReplacerPluginIds.toString());
            }

            @Override
            public void endElement( String uri,
                                    String localName,
                                    String name ) {
                int ndx = elemName.lastIndexOf(NAME_DELIMITER);
                elemName = (ndx < 0 ? "" : elemName.substring(0, ndx)); //$NON-NLS-1$
            }

            @Override
            public void startElement( String uri,
                                      String localName,
                                      String name,
                                      Attributes attributes ) {
                elemName += (elemName.length() > 0 ? NAME_DELIMITER + localName : localName);
                if (FEATURE_ELEM.equals(elemName)) {
                    properties.put(InternalName.ID, attributes.getValue(ID_ATTR));
                    version = attributes.getValue(VERSION_ATTR);
                    properties.put(InternalName.VERSION, version);
                } else if (PLUGIN_ELEM.equals(elemName)) {
                    assert version != null;
                    if (plugins.length() > 0) {
                        plugins.append(PATH_DELIMITER);
                        eclpiseIdReplacerPluginIds.append(',');
                    }
                    String id = attributes.getValue(ID_ATTR);
                    plugins.append("../").append(id); //$NON-NLS-1$
                    eclpiseIdReplacerPluginIds.append(id).append(':');
                    eclpiseIdReplacerPluginIds.append(attributes.getValue(VERSION_ATTR)).append(',').append(version);
                }
            }
        });
        try {
            xmlReader.parse(new InputSource(stream));
        } finally {
            stream.close();
        }
    }
}
