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
import org.komodo.repository.RepositoryConstants;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactFactory;
import org.komodo.teiid.model.Propertied;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;
import org.overlord.sramp.SrampModelUtils;
import org.overlord.sramp.derived.AbstractXmlDeriver;
import org.overlord.sramp.derived.ArtifactDeriver;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Property;
import org.s_ramp.xmlns._2010.s_ramp.UserDefinedArtifactType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An {@link ArtifactDeriver} that will create derived content from a vdb.xml file.
 */
public class VdbDeriver extends AbstractXmlDeriver implements RepositoryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(VdbDeriver.class);

    /**
     * The delimeter that separates mapped role names in the data policy property value. Value is {@value}.
     */
    public static final char ROLE_NAME_DELIMETER = ',';

    /**
     * @see org.overlord.sramp.derived.AbstractXmlDeriver#derive(java.util.Collection, org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType, org.w3c.dom.Element, javax.xml.xpath.XPath)
     */
    @Override
    protected void derive(final Collection<BaseArtifactType> derivedArtifacts,
                          final BaseArtifactType artifact,
                          final Element rootElement,
                          final XPath xpath) throws IOException {
        LOGGER.debug("derive:root element='{}'", rootElement.getLocalName()); //$NON-NLS-1$

        if (!(artifact instanceof UserDefinedArtifactType)
            || !Artifact.Type.VDB.getName().equals(((UserDefinedArtifactType)artifact).getUserType())) {
            throw new IllegalArgumentException("Artifact is not a VDB"); // TODO i18n this
        }

        final UserDefinedArtifactType vdbArtifact = (UserDefinedArtifactType)artifact;

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
        final NodeList dataPolicies = (NodeList)query(xpath,
                                                      vdb,
                                                      DeriverUtil.getElementQueryString(Vdb.ManifestId.DATA_POLICY),
                                                      XPathConstants.NODESET);

        if (dataPolicies.getLength() != 0) {
            LOGGER.debug("processing '{}' data policies", dataPolicies.getLength()); //$NON-NLS-1$

            for (int dataPolicyIndex = 0, numDataPolicies = dataPolicies.getLength(); dataPolicyIndex < numDataPolicies; ++dataPolicyIndex) {
                final Element dataPolicy = (Element)dataPolicies.item(dataPolicyIndex);
                final UserDefinedArtifactType dataPolicyArtifact = ArtifactFactory.create(Artifact.Type.DATA_POLICY);
                derivedArtifacts.add(dataPolicyArtifact);

                { // name
                    final String name = dataPolicy.getAttribute(DataPolicy.ManifestId.Attributes.NAME);
                    dataPolicyArtifact.setName(name);
                }

                { // any authenticated
                    final String anyAuthenticated = dataPolicy.getAttribute(DataPolicy.ManifestId.Attributes.ANY_AUTHENTICATED);
                    SrampModelUtils.setCustomProperty(dataPolicyArtifact,
                                                      DataPolicy.PropertyName.ANY_AUTHENTICATED,
                                                      anyAuthenticated);
                }

                { // create temp tables
                    final String creatable = dataPolicy.getAttribute(DataPolicy.ManifestId.Attributes.TEMP_TABLE_CREATABLE);
                    SrampModelUtils.setCustomProperty(dataPolicyArtifact, DataPolicy.PropertyName.TEMP_TABLE_CREATABLE, creatable);
                }

                { // description
                    final Element element = (Element)query(xpath,
                                                           dataPolicy,
                                                           DeriverUtil.getElementQueryString(DataPolicy.ManifestId.DESCRIPTION),
                                                           XPathConstants.NODE);

                    if (element != null) {
                        final String description = element.getTextContent();
                        dataPolicyArtifact.setDescription(description);
                    }
                }

                { // mapped role names
                    final NodeList roleNames = (NodeList)query(xpath,
                                                               dataPolicy,
                                                               DeriverUtil.getElementQueryString(DataPolicy.ManifestId.ROLE_NAME),
                                                               XPathConstants.NODESET);

                    if (roleNames.getLength() != 0) {
                        LOGGER.debug("processing '{}' mapped role names for data policy '{}'", //$NON-NLS-1$
                                     roleNames.getLength(),
                                     dataPolicyArtifact.getName());

                        // combine role names into one string
                        final StringBuilder mappedNames = new StringBuilder();

                        for (int roleNameIndex = 0, numRoleNames = roleNames.getLength(); roleNameIndex < numRoleNames; ++roleNameIndex) {
                            if (roleNameIndex != 0) {
                                mappedNames.append(ROLE_NAME_DELIMETER);
                            }

                            final Element roleName = (Element)roleNames.item(roleNameIndex);
                            final String name = roleName.getTextContent();
                            mappedNames.append(name);

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("found mapped role name '{}' for data policy '{}'", dataPolicyArtifact.getName()); //$NON-NLS-1$
                            }
                        }

                        SrampModelUtils.setCustomProperty(dataPolicyArtifact,
                                                          DataPolicy.PropertyName.ROLE_NAMES,
                                                          mappedNames.toString());
                    }
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("data policy name '{}'", dataPolicyArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("data policy description '{}'", dataPolicyArtifact.getDescription()); //$NON-NLS-1$                  

                    for (final Property prop : dataPolicyArtifact.getProperty()) {
                        LOGGER.debug("data policy property '{}' with value '{}'", prop.getPropertyName(), prop.getPropertyValue()); //$NON-NLS-1$
                    }
                }

                { // permissions
                    final NodeList permissions = (NodeList)query(xpath,
                                                                 dataPolicy,
                                                                 DeriverUtil.getElementQueryString(DataPolicy.ManifestId.PERMISSION),
                                                                 XPathConstants.NODESET);

                    if (permissions.getLength() != 0) {
                        LOGGER.debug("processing '{}' data permissions for data policy '{}'", //$NON-NLS-1$
                                     permissions.getLength(),
                                     dataPolicyArtifact.getName());

                        for (int permissionIndex = 0, numPermissions = permissions.getLength(); permissionIndex < numPermissions; ++permissionIndex) {
                            final Element permisson = (Element)permissions.item(permissionIndex);
                            final UserDefinedArtifactType permissionArtifact = ArtifactFactory.create(Artifact.Type.PERMISSION);
                            derivedArtifacts.add(permissionArtifact);

                            { // resource name
                                final String resourceName = permisson.getAttribute(Permission.ManifestId.RESOURCE_NAME);
                                permissionArtifact.setName(resourceName);
                            }

                            { // alterable
                                final String alterable = permisson.getAttribute(Permission.ManifestId.ALTERABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact,
                                                                  Permission.PropertyName.ALTERABLE,
                                                                  alterable);
                            }

                            { // creatable
                                final String creatable = permisson.getAttribute(Permission.ManifestId.CREATABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact,
                                                                  Permission.PropertyName.CREATABLE,
                                                                  creatable);
                            }

                            { // deletable
                                final String deletable = permisson.getAttribute(Permission.ManifestId.DELETABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact,
                                                                  Permission.PropertyName.DELETABLE,
                                                                  deletable);
                            }

                            { // executable
                                final String executable = permisson.getAttribute(Permission.ManifestId.EXECUTABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact,
                                                                  Permission.PropertyName.EXECUTABLE,
                                                                  executable);
                            }

                            { // readable
                                final String readable = permisson.getAttribute(Permission.ManifestId.READABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact, Permission.PropertyName.READABLE, readable);
                            }

                            { // updatable
                                final String updatable = permisson.getAttribute(Permission.ManifestId.UPDATABLE);
                                SrampModelUtils.setCustomProperty(permissionArtifact,
                                                                  Permission.PropertyName.UPDATABLE,
                                                                  updatable);
                            }

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("permission resource name '{}'", permissionArtifact.getName()); //$NON-NLS-1$

                                // properties
                                for (final Property prop : permissionArtifact.getProperty()) {
                                    LOGGER.debug("Source property '{}' with value '{}'", //$NON-NLS-1$
                                                 prop.getPropertyName(),
                                                 prop.getPropertyValue());
                                }
                            }

                            // add the relationships
                            DeriverUtil.addRelationship(dataPolicyArtifact,
                                                        permissionArtifact,
                                                        RelationshipType.DATA_POLICY_PERMISSIONS,
                                                        RelationshipType.PERMISSION_DATA_POLICY);
                        }
                    }
                }
            }
        }
    }

    private void processEntries(final Collection<BaseArtifactType> derivedArtifacts,
                                final BaseArtifactType vdbArtifact,
                                final Element vdb,
                                final XPath xpath) throws Exception {
        final NodeList entries = (NodeList)query(xpath,
                                                 vdb,
                                                 DeriverUtil.getElementQueryString(Vdb.ManifestId.ENTRY),
                                                 XPathConstants.NODESET);

        if (entries.getLength() != 0) {
            LOGGER.debug("processing '{}' entries", entries.getLength()); //$NON-NLS-1$

            for (int entryIndex = 0, numEntries = entries.getLength(); entryIndex < numEntries; ++entryIndex) {
                final Element entry = (Element)entries.item(entryIndex);
                final UserDefinedArtifactType entryArtifact = ArtifactFactory.create(Artifact.Type.ENTRY);
                derivedArtifacts.add(entryArtifact);

                { // name
                    final String path = entry.getAttribute(Entry.ManifestId.Attributes.PATH);
                    entryArtifact.setName(path);
                }

                { // description
                    final String description = entry.getAttribute(Entry.ManifestId.DESCRIPTION);
                    entryArtifact.setDescription(description);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("entry path '{}'", entryArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("entry description '{}'", entryArtifact.getDescription()); //$NON-NLS-1$
                }

                // properties
                processProperties(entryArtifact, entry, xpath);
            }
        }
    }

    private void processProperties(final UserDefinedArtifactType artifact,
                                   final Element element,
                                   final XPath xpath) throws Exception {
        final NodeList props = (NodeList)query(xpath,
                                               element,
                                               DeriverUtil.getElementQueryString(Propertied.ManifestId.PROPERTY),
                                               XPathConstants.NODESET);

        if (props.getLength() != 0) {
            LOGGER.debug("processing '{}' properties", props.getLength()); //$NON-NLS-1$

            for (int propIndex = 0, numProps = props.getLength(); propIndex < numProps; ++propIndex) {
                final Element prop = (Element)props.item(propIndex);
                final String name = prop.getAttribute(Propertied.ManifestId.Attributes.NAME);
                final String value = prop.getAttribute(Propertied.ManifestId.Attributes.VALUE);
                SrampModelUtils.setCustomProperty(artifact, name, value);
            }

            if (LOGGER.isDebugEnabled()) {
                for (final Property prop : artifact.getProperty()) {
                    LOGGER.debug("artifact '{}' has property '{}' with value '{}'", //$NON-NLS-1$
                                 new Object[] {artifact.getName(), prop.getPropertyName(), prop.getPropertyValue()});
                }
            }
        }
    }

    private void processSchemas(final Collection<BaseArtifactType> derivedArtifacts,
                                final BaseArtifactType vdbArtifact,
                                final Element vdb,
                                final XPath xpath) throws Exception {
        final NodeList schemas = (NodeList)query(xpath,
                                                 vdb,
                                                 DeriverUtil.getElementQueryString(Vdb.ManifestId.SCHEMA),
                                                 XPathConstants.NODESET);

        if (schemas.getLength() != 0) {
            LOGGER.debug("processing '{}' schemas", schemas.getLength()); //$NON-NLS-1$

            for (int schemaIndex = 0, numSchemas = schemas.getLength(); schemaIndex < numSchemas; ++schemaIndex) {
                final Element schema = (Element)schemas.item(schemaIndex);
                final UserDefinedArtifactType schemaArtifact = ArtifactFactory.create(Artifact.Type.SCHEMA);
                derivedArtifacts.add(schemaArtifact);

                { // name
                    final String path = schema.getAttribute(Schema.ManifestId.Attributes.NAME);
                    schemaArtifact.setName(path);
                }

                { // description
                    final Element element = (Element)query(xpath,
                                                           schema,
                                                           DeriverUtil.getElementQueryString(Schema.ManifestId.DESCRIPTION),
                                                           XPathConstants.NODE);

                    if (element != null) {
                        final String description = element.getTextContent();
                        schemaArtifact.setDescription(description);
                    }
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("schema name '{}'", schemaArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("schema description '{}'", schemaArtifact.getDescription()); //$NON-NLS-1$
                }

                { // visible
                    final String visible = schema.getAttribute(Schema.ManifestId.Attributes.VISIBLE);
                    SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.TYPE, visible);
                }

                { // type
                    String type = schema.getAttribute(Schema.ManifestId.Attributes.TYPE);

                    if (StringUtil.isEmpty(type)) {
                        type = Schema.DEFAULT_TYPE;
                    }

                    SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.TYPE, type);
                }

                { // metadata
                    final Element element = (Element)query(xpath,
                                                           schema,
                                                           DeriverUtil.getElementQueryString(Schema.ManifestId.METADATA),
                                                           XPathConstants.NODE);

                    if (element != null) {
                        final String metadata = element.getTextContent();
                        SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.METADATA, metadata);

                        { // metadata type
                            final String metadataType = element.getAttribute(Schema.ManifestId.MetadataAttributes.TYPE);
                            SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.METADATA_TYPE, metadataType);
                        }
                    }
                }

                // properties
                processProperties(schemaArtifact, schema, xpath);

                // sources
                final NodeList sources = (NodeList)query(xpath,
                                                         schema,
                                                         DeriverUtil.getElementQueryString(Schema.ManifestId.SOURCE),
                                                         XPathConstants.NODESET);

                if (sources.getLength() != 0) {
                    LOGGER.debug("processing '{}' sources for schema '{}'", sources.getLength(), schemaArtifact.getName()); //$NON-NLS-1$

                    for (int sourceIndex = 0, numSources = sources.getLength(); sourceIndex < numSources; ++sourceIndex) {
                        final Element source = (Element)sources.item(sourceIndex);
                        final UserDefinedArtifactType sourceArtifact = ArtifactFactory.create(Artifact.Type.SOURCE);
                        derivedArtifacts.add(sourceArtifact);

                        { // name
                            final String name = source.getAttribute(Source.ManifestId.Attributes.NAME);
                            sourceArtifact.setName(name);
                        }

                        { // JNDI name
                            final String jndiName = source.getAttribute(Source.ManifestId.Attributes.JNDI_NAME);
                            SrampModelUtils.setCustomProperty(sourceArtifact, Source.PropertyName.JNDI_NAME, jndiName);
                        }

                        { // translator
                            final String translatorName = source.getAttribute(Source.ManifestId.Attributes.TRANSLATOR_NAME);
                            SrampModelUtils.setCustomProperty(sourceArtifact, Source.PropertyName.TRANSLATOR_NAME, translatorName);
                        }

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("schema source name '{}'", sourceArtifact.getName()); //$NON-NLS-1$

                            // properties
                            for (final Property prop : sourceArtifact.getProperty()) {
                                LOGGER.debug("Source property '{}' with value '{}'", //$NON-NLS-1$
                                             prop.getPropertyName(),
                                             prop.getPropertyValue());
                            }
                        }

                        // add the relationships
                        DeriverUtil.addRelationship(schemaArtifact,
                                                    sourceArtifact,
                                                    RelationshipType.SCHEMA_SOURCES,
                                                    RelationshipType.SOURCE_SCHEMA);
                    }
                }
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

            for (int translatorIndex = 0, numTranslators = translators.getLength(); translatorIndex < numTranslators; ++translatorIndex) {
                final Element translator = (Element)translators.item(translatorIndex);
                final UserDefinedArtifactType translatorArtifact = ArtifactFactory.create(Artifact.Type.TRANSLATOR);
                derivedArtifacts.add(translatorArtifact);

                { // name
                    final String name = translator.getAttribute(Translator.ManifestId.Attributes.NAME);
                    translatorArtifact.setName(name);
                }

                { // description
                    final String description = translator.getAttribute(Translator.ManifestId.Attributes.DESCRIPTION);
                    translatorArtifact.setDescription(description);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("translator name '{}'", translatorArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("translator description '{}'", translatorArtifact.getDescription()); //$NON-NLS-1$
                }

                { // type
                    final String type = translator.getAttribute(Translator.ManifestId.Attributes.TYPE);
                    SrampModelUtils.setCustomProperty(translatorArtifact, Translator.PropertyName.TYPE, type);
                }

                // properties
                processProperties(translatorArtifact, translator, xpath);
            }
        }
    }

    private void processVdb(final Collection<BaseArtifactType> derivedArtifacts,
                            final UserDefinedArtifactType vdbArtifact,
                            final Element vdb,
                            final XPath xpath) throws Exception {
        { // name
            final String name = vdb.getAttribute(Vdb.ManifestId.Attributes.NAME);
            vdbArtifact.setName(name);
        }

        { // description
            final Element element = (Element)query(xpath,
                                                   vdb,
                                                   DeriverUtil.getElementQueryString(Vdb.ManifestId.DESCRIPTION),
                                                   XPathConstants.NODE);

            if (element != null) {
                final String description = element.getTextContent();
                vdbArtifact.setDescription(description);
            }
        }

        { // version
            String version = vdb.getAttribute(Vdb.ManifestId.Attributes.VERSION);

            if (StringUtil.isEmpty(version)) {
                version = Integer.toString(Vdb.DEFAULT_VERSION);
            } else {
                try {
                    Integer.parseInt(version);
                } catch (final Exception e) {
                    version = Integer.toString(Vdb.DEFAULT_VERSION);
                }
            }

            vdbArtifact.setVersion(version);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("VDB name '{}'", vdbArtifact.getName()); //$NON-NLS-1$
            LOGGER.debug("VDB description '{}'", vdbArtifact.getDescription()); //$NON-NLS-1$
            LOGGER.debug("VDB verson '{}'", vdbArtifact.getVersion()); //$NON-NLS-1$
        }

        processProperties(vdbArtifact, vdb, xpath);

        // derive artifacts
        processVdbImports(derivedArtifacts, vdbArtifact, vdb, xpath);
        processTranslators(derivedArtifacts, vdbArtifact, vdb, xpath);
        processDataRoles(derivedArtifacts, vdbArtifact, vdb, xpath);
        processEntries(derivedArtifacts, vdbArtifact, vdb, xpath);
        processSchemas(derivedArtifacts, vdbArtifact, vdb, xpath);
    }

    private void processVdbImports(final Collection<BaseArtifactType> derivedArtifacts,
                                   final BaseArtifactType vdbArtifact,
                                   final Element vdb,
                                   final XPath xpath) throws Exception {
        final NodeList vdbImports = (NodeList)query(xpath,
                                                    vdb,
                                                    DeriverUtil.getElementQueryString(Vdb.ManifestId.IMPORT_VDB),
                                                    XPathConstants.NODESET);

        if (vdbImports.getLength() != 0) {
            LOGGER.debug("processing '{}' VDB imports", vdbImports.getLength()); //$NON-NLS-1$

            for (int vdbImportIndex = 0, numVdbImports = vdbImports.getLength(); vdbImportIndex < numVdbImports; ++vdbImportIndex) {
                final Element vdbImport = (Element)vdbImports.item(vdbImportIndex);
                final UserDefinedArtifactType vdbImportArtifact = ArtifactFactory.create(Artifact.Type.IMPORT_VDB);
                derivedArtifacts.add(vdbImportArtifact);

                { // name
                    final String name = vdbImport.getAttribute(ImportVdb.ManifestId.Attributes.NAME);
                    vdbImportArtifact.setName(name);
                }

                { // version
                    String version = vdbImport.getAttribute(ImportVdb.ManifestId.Attributes.VERSION);

                    if (StringUtil.isEmpty(version)) {
                        version = Integer.toString(Vdb.DEFAULT_VERSION);
                    } else {
                        try {
                            Integer.parseInt(version);
                        } catch (final Exception e) {
                            version = Integer.toString(Vdb.DEFAULT_VERSION);
                        }
                    }

                    vdbImportArtifact.setVersion(version);
                }

                { // import data policies
                    final String importDataPolicies = vdbImport.getAttribute(ImportVdb.ManifestId.Attributes.IMPORT_DATA_POLICIES);
                    SrampModelUtils.setCustomProperty(vdbImportArtifact,
                                                      ImportVdb.PropertyName.IMPORT_DATA_POLICIES,
                                                      importDataPolicies);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Import VDB name '{}'", vdbImportArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("Import VDB verson '{}'", vdbImportArtifact.getVersion()); //$NON-NLS-1$

                    // properties
                    for (final Property prop : vdbImportArtifact.getProperty()) {
                        LOGGER.debug("Import VDB property '{}' with value '{}'", prop.getPropertyName(), prop.getPropertyValue()); //$NON-NLS-1$
                    }
                }
            }
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
        LOGGER.debug("executing query '{}'", query); //$NON-NLS-1$
        return super.query(xpath, context, query, returnType);
    }

}
