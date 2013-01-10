/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.deriver;

import java.io.IOException;
import java.util.Collection;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.komodo.teiid.model.vdb.Vdb;
import org.overlord.sramp.derived.AbstractXmlDeriver;
import org.overlord.sramp.derived.ArtifactDeriver;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An {@link ArtifactDeriver} that will create derived content from a vdb.xml file.
 */
public class VdbDeriver extends AbstractXmlDeriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(VdbDeriver.class);

    /**
     * @see org.overlord.sramp.derived.AbstractXmlDeriver#derive(java.util.Collection, org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType, org.w3c.dom.Element, javax.xml.xpath.XPath)
     */
    @Override
    protected void derive(final Collection<BaseArtifactType> derivedArtifacts,
                          final BaseArtifactType artifact,
                          final Element rootElement,
                          final XPath xpath) throws IOException {
        LOGGER.debug("derive:root element={}", rootElement.getLocalName()); //$NON-NLS-1$

        try {
            // root element should be the VDB element
            if (!Vdb.ManifestId.VDB_ELEMENT.equals(rootElement.getLocalName())) {
                throw new IllegalArgumentException("The vdb.xml file is malformed. It does not have a 'vdb' root element."); // TODO i18n this
            }

            processVdb(derivedArtifacts, artifact, rootElement, xpath);
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }

    private void processDataRoles(final Collection<BaseArtifactType> derivedArtifacts,
                                  final BaseArtifactType artifact,
                                  final Element vdb,
                                  final XPath xpath) throws Exception {
        // TODO Auto-generated method stub
        final NodeList dataPolicies = (NodeList)query(xpath, vdb, Vdb.ManifestId.DATA_POLICY, XPathConstants.NODESET);

        if (dataPolicies.getLength() != 0) {
            LOGGER.debug("processing '" + dataPolicies.getLength() + "' data roles"); //$NON-NLS-1$  //$NON-NLS-2$
        }
    }

    private void processEntries(final Collection<BaseArtifactType> derivedArtifacts,
                                final BaseArtifactType artifact,
                                final Element vdb,
                                final XPath xpath) throws Exception {
        // TODO Auto-generated method stub
        final NodeList entries = (NodeList)query(xpath, vdb, Vdb.ManifestId.ENTRY, XPathConstants.NODESET);

        if (entries.getLength() != 0) {
            LOGGER.debug("processing '" + entries.getLength() + "' entries"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void processModels(final Collection<BaseArtifactType> derivedArtifacts,
                               final BaseArtifactType artifact,
                               final Element vdb,
                               final XPath xpath) throws Exception {
        // TODO Auto-generated method stub
        final NodeList models = (NodeList)query(xpath, vdb, Vdb.ManifestId.SCHEMA, XPathConstants.NODESET);

        if (models.getLength() != 0) {
            LOGGER.debug("processing '" + models.getLength() + "' models"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void processProperties(final Collection<BaseArtifactType> derivedArtifacts,
                                   final BaseArtifactType artifact,
                                   final Element vdb,
                                   final XPath xpath) {
        // TODO Auto-generated method stub
        LOGGER.debug("processing properties"); //$NON-NLS-1$
    }

    private void processTranslators(final Collection<BaseArtifactType> derivedArtifacts,
                                    final BaseArtifactType artifact,
                                    final Element vdb,
                                    final XPath xpath) throws Exception {
        // TODO Auto-generated method stub
        final NodeList translators = (NodeList)query(xpath, vdb, Vdb.ManifestId.TRANSLATOR, XPathConstants.NODESET);

        if (translators.getLength() != 0) {
            LOGGER.debug("processing '" + translators.getLength() + "' translators"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void processVdb(final Collection<BaseArtifactType> derivedArtifacts,
                            final BaseArtifactType artifact,
                            final Element vdb,
                            final XPath xpath) throws Exception {
        final String vdbName = vdb.getAttribute(Vdb.ManifestId.Attributes.NAME);
        artifact.setName(vdbName);
        LOGGER.debug("VDB name=" + vdbName); //$NON-NLS-1$
        //
        //        { // set version
        //            String version = vdb.getAttribute(Vdb.ManifestId.Attributes.VERSION);
        //
        //            if (StringUtil.isEmpty(version)) {
        //                version = Integer.toString(Vdb.DEFAULT_VERSION);
        //            } else {
        //                try {
        //                } catch (Exception e) {
        //                    version = Integer.toString(Vdb.DEFAULT_VERSION);                    
        //                }
        //            }
        //
        //            artifact.setVersion(version);
        //        }

        processVdbImports(derivedArtifacts, artifact, vdb, xpath);
        processTranslators(derivedArtifacts, artifact, vdb, xpath);
        processDataRoles(derivedArtifacts, artifact, vdb, xpath);
        processEntries(derivedArtifacts, artifact, vdb, xpath);
        processModels(derivedArtifacts, artifact, vdb, xpath);
        processProperties(derivedArtifacts, artifact, vdb, xpath);
        //
        //        // Pre-set the UUIDs for all the derived artifacts. This is useful if something downstream needs to reference them.
        //        for (DerivedArtifactType derivedArtifact : derivedArtifacts) {
        //            derivedArtifact.setUuid(UUID.randomUUID().toString());
        //        }
    }

    private void processVdbImports(final Collection<BaseArtifactType> derivedArtifacts,
                                   final BaseArtifactType artifact,
                                   final Element vdb,
                                   final XPath xpath) throws Exception {
        // TODO Auto-generated method stub
        final NodeList vdbImports = (NodeList)query(xpath, vdb, Vdb.ManifestId.IMPORT_VDB, XPathConstants.NODESET);

        if (vdbImports.getLength() != 0) {
            LOGGER.debug("processing '" + vdbImports.getLength() + "' VDB imports"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /*
        private void processListeners(Collection<BaseArtifactType> derivedArtifacts,
                                      BaseArtifactType artifact,
                                      Element webXml,
                                      XPath xpath) throws XPathExpressionException {
            NodeList nodes = (NodeList)this.query(xpath, webXml, "./jee:listener", XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Element node = (Element)nodes.item(idx);
                UserDefinedArtifactType listener = new UserDefinedArtifactType();
                listener.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
                listener.setUuid(UUID.randomUUID().toString());
                listener.setUserType("ListenerDeclaration");

                String listenerClass = (String)this.query(xpath, node, "string(./jee:listener-class)", XPathConstants.STRING);
                String displayName = (String)this.query(xpath, node, "string(./jee:display-name)", XPathConstants.STRING);
                if (displayName == null || displayName.trim().length() == 0) {
                    displayName = listenerClass;
                }
                listener.setName(displayName);

                String description = (String)this.query(xpath, node, "string(./jee:description)", XPathConstants.STRING);
                if (description != null && description.trim().length() > 0) {
                    listener.setDescription(description);
                }
                SrampModelUtils.setCustomProperty(listener, "listener-class", listenerClass);
                derivedArtifacts.add(listener);
            }
        }

        private void processFilters(Collection<BaseArtifactType> derivedArtifacts,
                                    BaseArtifactType artifact,
                                    Element webXml,
                                    XPath xpath) throws XPathExpressionException {
            NodeList nodes = (NodeList)this.query(xpath, webXml, "./jee:filter", XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Element node = (Element)nodes.item(idx);
                UserDefinedArtifactType filter = new UserDefinedArtifactType();
                filter.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
                filter.setUuid(UUID.randomUUID().toString());
                filter.setUserType("FilterDeclaration");

                String filterClass = (String)this.query(xpath, node, "string(./jee:listener-class)", XPathConstants.STRING);
                String filterName = (String)this.query(xpath, node, "string(./jee:filter-name)", XPathConstants.STRING);
                String displayName = (String)this.query(xpath, node, "string(./jee:display-name)", XPathConstants.STRING);
                if (displayName == null || displayName.trim().length() == 0) {
                    displayName = filterClass;
                }
                String description = (String)this.query(xpath, node, "string(./jee:description)", XPathConstants.STRING);
                if (description != null && description.trim().length() > 0) {
                    description = null;
                }

                filter.setName(filterName);
                filter.setDescription(description);
                SrampModelUtils.setCustomProperty(filter, "display-name", displayName);
                SrampModelUtils.setCustomProperty(filter, "filter-class", filterClass);
                derivedArtifacts.add(filter);
            }
        }

        private void processFilterMappings(Collection<BaseArtifactType> derivedArtifacts,
                                           BaseArtifactType artifact,
                                           Element webXml,
                                           XPath xpath) throws XPathExpressionException {
            NodeList nodes = (NodeList)this.query(xpath, webXml, "./jee:filter-mapping", XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Element node = (Element)nodes.item(idx);
                UserDefinedArtifactType filterMapping = new UserDefinedArtifactType();
                filterMapping.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
                filterMapping.setUuid(UUID.randomUUID().toString());
                filterMapping.setUserType("FilterMapping");

                String filterName = (String)this.query(xpath, node, "string(./jee:filter-name)", XPathConstants.STRING);
                String urlPattern = (String)this.query(xpath, node, "string(./jee:url-pattern)", XPathConstants.STRING);

                filterMapping.setName(filterName + " Mapping");
                filterMapping.setDescription("Maps URLs of the form '" + urlPattern + "' to filter " + filterName + ".");
                SrampModelUtils.setCustomProperty(filterMapping, "filter-name", filterName);
                SrampModelUtils.setCustomProperty(filterMapping, "url-pattern", urlPattern);

                WebXmlArtifactCollection index = (WebXmlArtifactCollection)derivedArtifacts;
                UserDefinedArtifactType filter = index.lookupFilter(filterName);
                if (filter != null) {
                    SrampModelUtils.addGenericRelationship(filterMapping, "mapsFilter", filter.getUuid());
                }

                derivedArtifacts.add(filterMapping);
            }
        }

        private void processServlets(Collection<BaseArtifactType> derivedArtifacts,
                                     BaseArtifactType artifact,
                                     Element webXml,
                                     XPath xpath) throws XPathExpressionException {
            NodeList nodes = (NodeList)this.query(xpath, webXml, "./jee:servlet", XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Element node = (Element)nodes.item(idx);
                UserDefinedArtifactType servlet = new UserDefinedArtifactType();
                servlet.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
                servlet.setUuid(UUID.randomUUID().toString());
                servlet.setUserType("ServletDeclaration");

                String servletClass = (String)this.query(xpath, node, "string(./jee:listener-class)", XPathConstants.STRING);
                String servletName = (String)this.query(xpath, node, "string(./jee:servlet-name)", XPathConstants.STRING);
                String displayName = (String)this.query(xpath, node, "string(./jee:display-name)", XPathConstants.STRING);
                if (displayName == null || displayName.trim().length() == 0) {
                    displayName = servletClass;
                }
                String description = (String)this.query(xpath, node, "string(./jee:description)", XPathConstants.STRING);
                if (description != null && description.trim().length() > 0) {
                    description = null;
                }

                servlet.setName(servletName);
                servlet.setDescription(description);
                SrampModelUtils.setCustomProperty(servlet, "display-name", displayName);
                SrampModelUtils.setCustomProperty(servlet, "servlet-class", servletClass);
                derivedArtifacts.add(servlet);
            }
        }

        private void processServletMappings(Collection<BaseArtifactType> derivedArtifacts,
                                            BaseArtifactType artifact,
                                            Element webXml,
                                            XPath xpath) throws XPathExpressionException {
            NodeList nodes = (NodeList)this.query(xpath, webXml, "./jee:servlet-mapping", XPathConstants.NODESET);
            for (int idx = 0; idx < nodes.getLength(); idx++) {
                Element node = (Element)nodes.item(idx);
                UserDefinedArtifactType servletMapping = new UserDefinedArtifactType();
                servletMapping.setArtifactType(BaseArtifactEnum.USER_DEFINED_ARTIFACT_TYPE);
                servletMapping.setUuid(UUID.randomUUID().toString());
                servletMapping.setUserType("ServletMapping");

                String servletName = (String)this.query(xpath, node, "string(./jee:servlet-name)", XPathConstants.STRING);
                String urlPattern = (String)this.query(xpath, node, "string(./jee:url-pattern)", XPathConstants.STRING);

                servletMapping.setName(servletName + " Mapping");
                servletMapping.setDescription("Maps URLs of the form '" + urlPattern + "' to servlet " + servletName + ".");
                SrampModelUtils.setCustomProperty(servletMapping, "servlet-name", servletName);
                SrampModelUtils.setCustomProperty(servletMapping, "url-pattern", urlPattern);

                WebXmlArtifactCollection index = (WebXmlArtifactCollection)derivedArtifacts;
                UserDefinedArtifactType servlet = index.lookupServlet(servletName);
                if (servlet != null) {
                    SrampModelUtils.addGenericRelationship(servletMapping, "mapsServlet", servlet.getUuid());
                }

                derivedArtifacts.add(servletMapping);
            }
        }
    */
}
