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
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.StringUtil;
import org.komodo.repository.RepositoryConstants;
import org.komodo.repository.RepositoryI18n;
import org.komodo.repository.artifact.Artifact;
import org.komodo.repository.artifact.ArtifactFactory;
import org.komodo.teiid.model.Propertied;
import org.komodo.teiid.model.vdb.DataPolicy;
import org.komodo.teiid.model.vdb.Entry;
import org.komodo.teiid.model.vdb.ImportVdb;
import org.komodo.teiid.model.vdb.Permission;
import org.komodo.teiid.model.vdb.Schema;
import org.komodo.teiid.model.vdb.Schema.ManifestId.Attribute;
import org.komodo.teiid.model.vdb.Source;
import org.komodo.teiid.model.vdb.Translator;
import org.komodo.teiid.model.vdb.Vdb;
import org.overlord.sramp.common.SrampModelUtils;
import org.overlord.sramp.common.derived.AbstractXmlDeriver;
import org.overlord.sramp.common.derived.ArtifactDeriver;
import org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.ExtendedArtifactType;
import org.s_ramp.xmlns._2010.s_ramp.Property;
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
     * {@inheritDoc}
     *
     * @see org.overlord.sramp.common.derived.AbstractXmlDeriver#derive(java.util.Collection, org.s_ramp.xmlns._2010.s_ramp.BaseArtifactType, org.w3c.dom.Element, javax.xml.xpath.XPath)
     */
    @Override
    protected void derive(final Collection<BaseArtifactType> derivedArtifacts,
                          final BaseArtifactType artifact,
                          final Element rootElement,
                          final XPath xpath) throws IOException {
        LOGGER.debug("derive:root element='{}'", rootElement.getLocalName()); //$NON-NLS-1$

        if (!(artifact instanceof ExtendedArtifactType)
            || !Artifact.Type.VDB.getName().equals(((ExtendedArtifactType)artifact).getExtendedType())) {
            throw new IllegalArgumentException(I18n.bind(RepositoryI18n.notVdbArtifact, artifact.getName()));
        }

        final ExtendedArtifactType vdbArtifact = (ExtendedArtifactType)artifact;

        try {
            // root element should be the VDB element
            if (!Vdb.ManifestId.VDB_ELEMENT.equals(rootElement.getLocalName())) {
                throw new IllegalArgumentException(I18n.bind(RepositoryI18n.missingVdbRootElement, artifact.getName()));
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
                final ExtendedArtifactType dataPolicyArtifact = ArtifactFactory.create(Artifact.Type.DATA_POLICY);
                derivedArtifacts.add(dataPolicyArtifact);

                { // name
                    final String name = dataPolicy.getAttribute(DataPolicy.ManifestId.Attribute.NAME);
                    dataPolicyArtifact.setName(name);
                }

                { // any authenticated
                    final String anyAuthenticated = dataPolicy.getAttribute(DataPolicy.ManifestId.Attribute.ANY_AUTHENTICATED);
                    SrampModelUtils.setCustomProperty(dataPolicyArtifact,
                                                      DataPolicy.PropertyName.ANY_AUTHENTICATED,
                                                      anyAuthenticated);
                }

                { // create temp tables
                    final String creatable = dataPolicy.getAttribute(DataPolicy.ManifestId.Attribute.TEMP_TABLE_CREATABLE);
                    SrampModelUtils.setCustomProperty(dataPolicyArtifact, DataPolicy.PropertyName.TEMP_TABLE_CREATABLE, creatable);
                }

                // description
                setDescriptionFromElementValue(dataPolicy, DataPolicy.ManifestId.DESCRIPTION, dataPolicyArtifact, xpath);

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
                            final Element permission = (Element)permissions.item(permissionIndex);
                            final ExtendedArtifactType permissionArtifact = ArtifactFactory.create(Artifact.Type.PERMISSION);
                            derivedArtifacts.add(permissionArtifact);

                            { // resource name
                                final Element element = (Element)query(xpath,
                                                                       permission,
                                                                       DeriverUtil.getElementQueryString(Permission.ManifestId.RESOURCE_NAME),
                                                                       XPathConstants.NODE);
                                final String resourceName = element.getTextContent();
                                permissionArtifact.setName(resourceName);
                            }

                            // alterable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.ALTERABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.ALTERABLE,
                                                        xpath);

                            // condition
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.CONDITION,
                                                        permissionArtifact,
                                                        Permission.PropertyName.CONDITION,
                                                        xpath);

                            // creatable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.CREATABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.CREATABLE,
                                                        xpath);

                            // deletable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.DELETABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.DELETABLE,
                                                        xpath);

                            // executable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.EXECUTABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.EXECUTABLE,
                                                        xpath);

                            // languagable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.LANGUAGABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.LANGUAGABLE,
                                                        xpath);

                            // readable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.READABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.READABLE,
                                                        xpath);

                            // updatable
                            setPropertyFromElementValue(permission,
                                                        Permission.ManifestId.UPDATABLE,
                                                        permissionArtifact,
                                                        Permission.PropertyName.UPDATABLE,
                                                        xpath);

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
                                                        Artifact.RelationshipType.DATA_POLICY_PERMISSIONS,
                                                        Artifact.RelationshipType.PERMISSION_DATA_POLICY);
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
                final ExtendedArtifactType entryArtifact = ArtifactFactory.create(Artifact.Type.ENTRY);
                derivedArtifacts.add(entryArtifact);

                { // name
                    final String path = entry.getAttribute(Entry.ManifestId.Attribute.PATH);
                    entryArtifact.setName(path);
                }

                // description
                setDescriptionFromElementValue(entry, Entry.ManifestId.DESCRIPTION, entryArtifact, xpath);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("entry path '{}'", entryArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("entry description '{}'", entryArtifact.getDescription()); //$NON-NLS-1$
                }

                // properties
                processProperties(entryArtifact, entry, xpath);
            }
        }
    }

    private void processProperties(final ExtendedArtifactType artifact,
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
                final ExtendedArtifactType schemaArtifact = ArtifactFactory.create(Artifact.Type.SCHEMA);
                derivedArtifacts.add(schemaArtifact);

                { // name
                    final String path = schema.getAttribute(Schema.ManifestId.Attribute.NAME);
                    schemaArtifact.setName(path);
                }

                // description
                setDescriptionFromElementValue(schema, Schema.ManifestId.DESCRIPTION, schemaArtifact, xpath);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("schema name '{}'", schemaArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("schema description '{}'", schemaArtifact.getDescription()); //$NON-NLS-1$
                }

                { // visible
                    final String visible = schema.getAttribute(Schema.ManifestId.Attribute.VISIBLE);
                    SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.VISIBLE, visible);
                }

                { // type
                    String type = schema.getAttribute(Schema.ManifestId.Attribute.TYPE);

                    if (StringUtil.isEmpty(type)) {
                        type = Schema.DEFAULT_TYPE;
                    }

                    SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.TYPE, type);
                }

                { // metadata
                    final Element element = setPropertyFromElementValue(schema,
                                                                        Schema.ManifestId.METADATA,
                                                                        schemaArtifact,
                                                                        Schema.PropertyName.METADATA,
                                                                        xpath);

                    if (element != null) {
                        final String metadataType = element.getAttribute(Attribute.METADATA_TYPE);
                        SrampModelUtils.setCustomProperty(schemaArtifact, Schema.PropertyName.METADATA_TYPE, metadataType);
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
                        final ExtendedArtifactType sourceArtifact = ArtifactFactory.create(Artifact.Type.SOURCE);
                        derivedArtifacts.add(sourceArtifact);

                        { // name
                            final String name = source.getAttribute(Source.ManifestId.Attribute.NAME);
                            sourceArtifact.setName(name);
                        }

                        { // JNDI name
                            final String jndiName = source.getAttribute(Source.ManifestId.Attribute.JNDI_NAME);
                            SrampModelUtils.setCustomProperty(sourceArtifact, Source.PropertyName.JNDI_NAME, jndiName);
                        }

                        { // translator
                            final String translatorName = source.getAttribute(Source.ManifestId.Attribute.TRANSLATOR_NAME);
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
                                                    Artifact.RelationshipType.SCHEMA_SOURCES,
                                                    Artifact.RelationshipType.SOURCE_SCHEMA);
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
                final ExtendedArtifactType translatorArtifact = ArtifactFactory.create(Artifact.Type.TRANSLATOR);
                derivedArtifacts.add(translatorArtifact);

                { // name
                    final String name = translator.getAttribute(Translator.ManifestId.Attribute.NAME);
                    translatorArtifact.setName(name);
                }

                { // description
                    final String description = translator.getAttribute(Translator.ManifestId.Attribute.DESCRIPTION);
                    translatorArtifact.setDescription(description);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("translator name '{}'", translatorArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("translator description '{}'", translatorArtifact.getDescription()); //$NON-NLS-1$
                }

                { // type
                    final String type = translator.getAttribute(Translator.ManifestId.Attribute.TYPE);
                    SrampModelUtils.setCustomProperty(translatorArtifact, Translator.PropertyName.TYPE, type);
                }

                // properties
                processProperties(translatorArtifact, translator, xpath);
            }
        }
    }

    private void processVdb(final Collection<BaseArtifactType> derivedArtifacts,
                            final ExtendedArtifactType vdbArtifact,
                            final Element vdb,
                            final XPath xpath) throws Exception {
        { // name
            final String name = vdb.getAttribute(Vdb.ManifestId.Attribute.NAME);
            vdbArtifact.setName(name);
        }

        // description
        setDescriptionFromElementValue(vdb, Vdb.ManifestId.DESCRIPTION, vdbArtifact, xpath);

        // version
        setVersionFromAttribueValue(vdb, Vdb.ManifestId.Attribute.VERSION, vdbArtifact, xpath);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("VDB name '{}'", vdbArtifact.getName()); //$NON-NLS-1$
            LOGGER.debug("VDB description '{}'", vdbArtifact.getDescription()); //$NON-NLS-1$
            LOGGER.debug("VDB version '{}'", vdbArtifact.getVersion()); //$NON-NLS-1$
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
                final ExtendedArtifactType vdbImportArtifact = ArtifactFactory.create(Artifact.Type.IMPORT_VDB);
                derivedArtifacts.add(vdbImportArtifact);

                { // name
                    final String name = vdbImport.getAttribute(ImportVdb.ManifestId.Attribute.NAME);
                    vdbImportArtifact.setName(name);
                }

                // version
                setVersionFromAttribueValue(vdbImport, ImportVdb.ManifestId.Attribute.VERSION, vdbImportArtifact, xpath);

                { // import data policies
                    String importDataPolicies = vdbImport.getAttribute(ImportVdb.ManifestId.Attribute.IMPORT_DATA_POLICIES);

                    if (StringUtil.isEmpty(importDataPolicies)) {
                        importDataPolicies = Boolean.toString(ImportVdb.DEFAULT_IMPORT_DATA_POLICIES);
                    }

                    SrampModelUtils.setCustomProperty(vdbImportArtifact,
                                                      ImportVdb.PropertyName.IMPORT_DATA_POLICIES,
                                                      importDataPolicies);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Import VDB name '{}'", vdbImportArtifact.getName()); //$NON-NLS-1$
                    LOGGER.debug("Import VDB version '{}'", vdbImportArtifact.getVersion()); //$NON-NLS-1$

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
     * @see org.overlord.sramp.common.derived.AbstractXmlDeriver#query(javax.xml.xpath.XPath, org.w3c.dom.Element, java.lang.String, javax.xml.namespace.QName)
     */
    @Override
    protected Object query(final XPath xpath,
                           final Element context,
                           final String query,
                           final QName returnType) throws XPathExpressionException {
        LOGGER.debug("executing query '{}'", query); //$NON-NLS-1$
        return super.query(xpath, context, query, returnType);
    }

    private void setDescriptionFromElementValue(final Element parent,
                                                final String elementName,
                                                final BaseArtifactType artifact,
                                                final XPath xpath) throws Exception {
        final Element element = (Element)query(xpath, parent, DeriverUtil.getElementQueryString(elementName), XPathConstants.NODE);

        if (element != null) {
            final String description = element.getTextContent();
            artifact.setDescription(description);
        }
    }

    private Element setPropertyFromElementValue(final Element parent,
                                                final String elementName,
                                                final BaseArtifactType artifact,
                                                final String propertyName,
                                                final XPath xpath) throws Exception {
        final Element element = (Element)query(xpath, parent, DeriverUtil.getElementQueryString(elementName), XPathConstants.NODE);

        if (element != null) {
            final String value = element.getTextContent();
            SrampModelUtils.setCustomProperty(artifact, propertyName, value);
        }

        return element;
    }

    private void setVersionFromAttribueValue(final Element element,
                                             final String attributeName,
                                             final BaseArtifactType artifact,
                                             final XPath xpath) throws Exception {
        String version = element.getAttribute(attributeName);

        if (StringUtil.isEmpty(version)) {
            version = Integer.toString(Vdb.DEFAULT_VERSION);
        } else {
            try {
                Integer.parseInt(version);
            } catch (final Exception e) {
                version = Integer.toString(Vdb.DEFAULT_VERSION);
            }
        }

        artifact.setVersion(version);
    }

}
