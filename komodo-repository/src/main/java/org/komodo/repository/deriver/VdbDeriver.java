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
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactFactory;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;
import org.overlord.sramp.SrampModelUtils;
import org.overlord.sramp.derived.AbstractXmlDeriver;
import org.overlord.sramp.derived.ArtifactDeriver;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
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
                          final BaseArtifactType vdbArtifact,
                          final Element rootElement,
                          final XPath xpath) throws IOException {
        LOGGER.debug("derive:root element='{}'", rootElement.getLocalName()); //$NON-NLS-1$

        try {
            // root element should be the VDB element
            if (!Vdb.ManifestId.VDB_ELEMENT.equals(rootElement.getLocalName())) {
                throw new IllegalArgumentException("The vdb.xml file is malformed. It does not have a 'vdb' root element."); // TODO i18n this
            }

            processVdb(derivedArtifacts, vdbArtifact, rootElement, xpath);
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }

    private void processDataRoles(final Collection<BaseArtifactType> derivedArtifacts,
                                  final BaseArtifactType vdbArtifact,
                                  final Element vdb,
                                  final XPath xpath) throws Exception {
        // TODO implement processDataRoles
        final NodeList dataPolicies = (NodeList)query(xpath,
                                                      vdb,
                                                      DeriverUtil.getElementQueryString(Vdb.ManifestId.DATA_POLICY),
                                                      XPathConstants.NODESET);

        if (dataPolicies.getLength() != 0) {
            LOGGER.debug("processing '{}' data roles", dataPolicies.getLength()); //$NON-NLS-1$
        }
    }

    private void processEntries(final Collection<BaseArtifactType> derivedArtifacts,
                                final BaseArtifactType vdbArtifact,
                                final Element vdb,
                                final XPath xpath) throws Exception {
        // TODO implement processEntries
        final NodeList entries = (NodeList)query(xpath,
                                                 vdb,
                                                 DeriverUtil.getElementQueryString(Vdb.ManifestId.ENTRY),
                                                 XPathConstants.NODESET);

        if (entries.getLength() != 0) {
            LOGGER.debug("processing '{}' entries", entries.getLength()); //$NON-NLS-1$
        }
    }

    private void processModels(final Collection<BaseArtifactType> derivedArtifacts,
                               final BaseArtifactType vdbArtifact,
                               final Element vdb,
                               final XPath xpath) throws Exception {
        // TODO implement processModels
        final NodeList models = (NodeList)query(xpath,
                                                vdb,
                                                DeriverUtil.getElementQueryString(Vdb.ManifestId.SCHEMA),
                                                XPathConstants.NODESET);

        if (models.getLength() != 0) {
            LOGGER.debug("processing '{}' schemas", models.getLength()); //$NON-NLS-1$
        }
    }

    private void processProperties(final Collection<BaseArtifactType> derivedArtifacts,
                                   final BaseArtifactType vdbArtifact,
                                   final Element vdb,
                                   final XPath xpath) throws Exception {
        final NodeList props = (NodeList)query(xpath, vdb, Vdb.ManifestId.PROPERTY, XPathConstants.NODESET);

        if (props.getLength() != 0) {
            LOGGER.debug("processing '{}' VDB properties", props.getLength()); //$NON-NLS-1$

            for (int j = 0, numProps = props.getLength(); j < numProps; ++j) {
                final Element prop = (Element)props.item(j);
                final String name = prop.getAttribute(Vdb.ManifestId.Attributes.NAME);
                final String value = prop.getAttribute(Vdb.ManifestId.Attributes.VALUE);
                SrampModelUtils.setCustomProperty(vdbArtifact, name, value);
                LOGGER.debug("VDB property '{}' with value '{}'", name, value); //$NON-NLS-1$
            }
        }
    }

    private void processTranslators(final Collection<BaseArtifactType> derivedArtifacts,
                                    final BaseArtifactType vdbArtifact,
                                    final Element vdb,
                                    final XPath xpath) throws Exception {
        final NodeList translators = (NodeList)query(xpath,
                                                     vdb,
                                                     DeriverUtil.getElementQueryString(Vdb.ManifestId.TRANSLATOR),
                                                     XPathConstants.NODESET);

        if (translators.getLength() != 0) {
            LOGGER.debug("processing '{}' translators", translators.getLength()); //$NON-NLS-1$

            for (int i = 0, numTranslators = translators.getLength(); i < numTranslators; ++i) {
                final Element translator = (Element)translators.item(i);
                final UserDefinedArtifactType translatorArtifact = ArtifactFactory.create(Artifact.Type.TRANSLATOR);

                { // name
                    final String name = translator.getAttribute(Translator.ManifestId.Attributes.NAME);
                    translatorArtifact.setName(name);
                    LOGGER.debug("translator name '{}'", name); //$NON-NLS-1$
                }

                { // description
                    final String description = translator.getAttribute(Translator.ManifestId.Attributes.DESCRIPTION);
                    translatorArtifact.setDescription(description);
                    LOGGER.debug("translator description '{}'", description); //$NON-NLS-1$
                }

                { // type
                    final String type = translator.getAttribute(Translator.ManifestId.Attributes.TYPE);
                    SrampModelUtils.setCustomProperty(translatorArtifact, Translator.PropertyName.TYPE, type);
                    LOGGER.debug("translator type '{}'", type); //$NON-NLS-1$
                }

                { // properties
                    final NodeList props = (NodeList)query(xpath, translator, Vdb.ManifestId.PROPERTY, XPathConstants.NODESET);

                    if (props.getLength() != 0) {
                        for (int j = 0, numProps = props.getLength(); j < numProps; ++j) {
                            final Element prop = (Element)props.item(j);
                            final String name = prop.getAttribute(Translator.ManifestId.Attributes.NAME);
                            final String value = prop.getAttribute(Translator.ManifestId.Attributes.VALUE);
                            SrampModelUtils.setCustomProperty(translatorArtifact, name, value);
                            LOGGER.debug("translator property '{}' with value '{}'", name, value); //$NON-NLS-1$
                        }
                    }
                }

                derivedArtifacts.add(translatorArtifact);
            }
        }
    }

    private void processVdb(final Collection<BaseArtifactType> derivedArtifacts,
                            final BaseArtifactType vdbArtifact,
                            final Element vdb,
                            final XPath xpath) throws Exception {
        { // name
            final String name = vdb.getAttribute(Vdb.ManifestId.Attributes.NAME);
            vdbArtifact.setName(name);
            LOGGER.debug("VDB name '{}'", name); //$NON-NLS-1$
        }

        { // description
            final Element element = (Element)query(xpath, vdb, Vdb.ManifestId.DESCRIPTION, XPathConstants.NODE);
            final String description = element.getTextContent();
            vdbArtifact.setDescription(description);
            LOGGER.debug("VDB description '{}'", description); //$NON-NLS-1$
        }

        { // version
            String version = vdb.getAttribute(Vdb.ManifestId.Attributes.VERSION);

            if (StringUtil.isEmpty(version)) {
                version = Integer.toString(Vdb.DEFAULT_VERSION);
            } else {
                try {
                    Integer.parseInt(version);
                } catch (Exception e) {
                    version = Integer.toString(Vdb.DEFAULT_VERSION);
                }
            }

            vdbArtifact.setVersion(version);
            LOGGER.debug("VDB version '{}'", version); //$NON-NLS-1$
        }

        processVdbImports(derivedArtifacts, vdbArtifact, vdb, xpath);
        processTranslators(derivedArtifacts, vdbArtifact, vdb, xpath);
        processDataRoles(derivedArtifacts, vdbArtifact, vdb, xpath);
        processEntries(derivedArtifacts, vdbArtifact, vdb, xpath);
        processModels(derivedArtifacts, vdbArtifact, vdb, xpath);
        processProperties(derivedArtifacts, vdbArtifact, vdb, xpath);
    }

    private void processVdbImports(final Collection<BaseArtifactType> derivedArtifacts,
                                   final BaseArtifactType vdbArtifact,
                                   final Element vdb,
                                   final XPath xpath) throws Exception {
        // TODO implement processVdbImports
        final NodeList vdbImports = (NodeList)query(xpath,
                                                    vdb,
                                                    DeriverUtil.getElementQueryString(Vdb.ManifestId.IMPORT_VDB),
                                                    XPathConstants.NODESET);

        if (vdbImports.getLength() != 0) {
            LOGGER.debug("processing '{}' VDB imports", vdbImports.getLength()); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.derived.AbstractXmlDeriver#query(javax.xml.xpath.XPath, org.w3c.dom.Element, java.lang.String, javax.xml.namespace.QName)
     */
    @Override
    protected Object query(final XPath xpath,
                           final Element context,
                           final String query,
                           final QName returnType) throws XPathExpressionException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query '{}'", query); //$NON-NLS-1$
        }

        return super.query(xpath, context, query, returnType);
    }

}
