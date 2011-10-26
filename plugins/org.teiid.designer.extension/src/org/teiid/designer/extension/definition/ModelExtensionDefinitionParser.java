/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.Translation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;

/**
 * The <code>ModelExtensionDefinitionParser</code> parses model extension definition input streams. Each input stream is validated
 * against a model extension definition schema.
 */
public class ModelExtensionDefinitionParser {

    /**
     * <code>true</code> if debug messages should be output when parsing.
     */
    private static final boolean DEBUG_MODE = false;

    /**
     * The locale used when no locale is found for translatable text.
     */
    static final Locale LOCALE = Locale.getDefault();

    private final File definitionSchemaFile;

    private Handler handler;

    /**
     * The parser of the definition stream.
     */
    private SAXParser parser;

    /**
     * Constructs a parser.
     * 
     * @param medSchema the model extension definition schema file used during validation (cannot be <code>null</code> and must
     *            exist)
     * @throws IllegalStateException if there were problems with the model extension definition schema file
     */
    public ModelExtensionDefinitionParser( File medSchema ) throws IllegalStateException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);

            this.parser = factory.newSAXParser();
            this.parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$ //$NON-NLS-2$

            this.definitionSchemaFile = medSchema;

            if (definitionSchemaFile == null || !definitionSchemaFile.exists()) {
                throw new IllegalStateException(Messages.definitionSchemaFileNotFoundInFilesystem);
            }

            this.parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", definitionSchemaFile); //$NON-NLS-1$
        } catch (Exception e) {
            IllegalStateException error = null;

            if (e instanceof IllegalStateException) {
                error = (IllegalStateException)e;
            } else {
                error = new IllegalStateException(e);
            }

            throw error;
        }
    }

    /**
     * @return the error messages output from the last parse operation or <code>null</code> if
     *         {@link #parse(InputStream, ModelExtensionAssistant))} has not been called
     */
    public Collection<String> getErrors() {
        if (this.handler == null) {
            return null;
        }

        return this.handler.getErrors();
    }

    /**
     * @return the fatal error messages output from the last parse operation or <code>null</code> if
     *         {@link #parse(InputStream, ModelExtensionAssistant))} has not been called
     */
    public Collection<String> getFatalErrors() {
        if (this.handler == null) {
            return null;
        }

        return this.handler.getFatalErrors();
    }

    /**
     * @return the information messages output from the last parse operation or <code>null</code> if
     *         {@link #parse(InputStream, ModelExtensionAssistant))} has not been called
     */
    public Collection<String> getInfos() {
        if (this.handler == null) {
            return null;
        }

        return this.handler.getInfos();
    }

    /**
     * @return the warning messages output from the last parse operation or <code>null</code> if
     *         {@link #parse(InputStream, ModelExtensionAssistant))} has not been called
     */
    public Collection<String> getWarnings() {
        if (this.handler == null) {
            return null;
        }

        return this.handler.getWarnings();
    }

    /**
     * @param definitionStream the model extension definition input stream (cannot be <code>null</code>)
     * @param assistant the model extension assistant (cannot be <code>null</code>)
     * @return the model extension definition (never <code>null</code>)
     * @throws Exception if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    public ModelExtensionDefinition parse( InputStream definitionStream,
                                           ModelExtensionAssistant assistant ) throws Exception {
        CoreArgCheck.isNotNull(definitionStream, "definitionStream is null"); //$NON-NLS-1$

        this.handler = new Handler(assistant);
        this.parser.parse(definitionStream, handler);
        return this.handler.getModelExtensionDefinition();
    }

    /**
     * The handler used by the parser. Each instance should be only used to parse one file.
     */
    class Handler extends DefaultHandler {

        private final Collection<String> fatals;
        private final Collection<String> errors;
        private final Collection<String> infos;
        private final Collection<String> warnings;

        private String advanced;
        private Set<String> allowedValues = new HashSet<String>();

        private final ModelExtensionAssistant assistant;
        private String defaultValue;
        private ModelExtensionDefinition definition;
        private String description;
        private final Stack<String> elements;
        private String fixedValue;
        private String id;
        private String index;
        private String masked;
        private String metaclassName;
        private String metamodelUri;
        private String namespacePrefix;
        private String namespaceUri;
        private ModelExtensionPropertyDefinition propDefn;
        private final Map<String, Collection<ModelExtensionPropertyDefinition>> properties;
        private String required;
        private String type;
        private String version;

        private Set<Translation> descriptions = new HashSet<Translation>();
        private String currentDescriptionLocale;
        private String currentDescriptionText;

        private Set<Translation> displayNames = new HashSet<Translation>();
        private String currentDisplayNameLocale;
        private String currentDisplayNameText;

        /**
         * @param assistant the model extension assistant used by the parser (cannot be <code>null</code>)
         */
        public Handler( ModelExtensionAssistant assistant ) {
            CoreArgCheck.isNotNull(assistant, "assistant is null"); //$NON-NLS-1$

            this.assistant = assistant;
            this.properties = new HashMap<String, Collection<ModelExtensionPropertyDefinition>>();
            this.elements = new Stack<String>();
            this.fatals = new ArrayList<String>();
            this.errors = new ArrayList<String>();
            this.infos = new ArrayList<String>();
            this.warnings = new ArrayList<String>();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters( char[] ch,
                                int start,
                                int length ) throws SAXException {
            String value = new String(ch, start, length);

            if (ExtensionConstants.Elements.DESCRIPTION.equals(getCurrentElement())) {
                if (ExtensionConstants.Elements.MODEL_EXTENSION.equals(getPreviousElement())) {
                    // model extension definition is not localized
                    this.description = value;
                } else if (ExtensionConstants.Elements.PROPERTY.equals(getPreviousElement())) {
                    this.currentDescriptionText = value;
                } else {
                    // should not get here
                    assert false : "Unexpected previous tag of " + getPreviousElement() + " while processing the " //$NON-NLS-1$ //$NON-NLS-2$
                            + ExtensionConstants.Elements.DESCRIPTION + " tag"; //$NON-NLS-1$
                }
            } else if (ExtensionConstants.Elements.DISPLAY.equals(getCurrentElement())) {
                if (ExtensionConstants.Elements.PROPERTY.equals(getPreviousElement())) {
                    this.currentDisplayNameText = value;
                } else {
                    // should not get here
                    assert false : "Unexpected previous tag of " + getPreviousElement() + " while processing the " //$NON-NLS-1$ //$NON-NLS-2$
                            + ExtensionConstants.Elements.DISPLAY + " tag"; //$NON-NLS-1$
                }
            } else if (ExtensionConstants.Elements.ALLOWED_VALUE.equals(getCurrentElement())) {
                this.allowedValues.add(value);
            } else {
                if (DEBUG_MODE) {
                    System.err.println("characters not processed=" + value); //$NON-NLS-1$
                }
            }

            super.characters(ch, start, length);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public void endElement( String uri,
                                String localName,
                                String qName ) throws SAXException {
            if (DEBUG_MODE) {
                System.err.println("endElement: localName=" + localName + ", qName=" + qName); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (ExtensionConstants.Elements.DISPLAY.equals(localName)) {
                if (this.currentDisplayNameLocale != null) {
                    if (DEBUG_MODE) {
                        System.err.println("Adding display name with locale of " + this.currentDisplayNameLocale + " and text of " //$NON-NLS-1$ //$NON-NLS-2$
                                + this.currentDisplayNameText);
                    }

                    Locale locale = I18nUtil.parseLocaleString(this.currentDisplayNameLocale);
                    this.displayNames.add(new Translation(locale, this.currentDisplayNameText));
                }

                this.currentDisplayNameLocale = null;
                this.currentDisplayNameText = null;
            } else if (ExtensionConstants.Elements.DESCRIPTION.equals(localName)) {
                if (this.currentDescriptionLocale != null) {
                    if (DEBUG_MODE) {
                        System.err.println("Adding description with locale of " + this.currentDescriptionLocale + " and text of " //$NON-NLS-1$ //$NON-NLS-2$
                                + this.currentDescriptionText);
                    }

                    Locale locale = I18nUtil.parseLocaleString(this.currentDescriptionLocale);
                    this.descriptions.add(new Translation(locale, this.currentDescriptionText));
                }

                this.currentDescriptionLocale = null;
                this.currentDescriptionText = null;
            } else if (ExtensionConstants.Elements.PROPERTY.equals(localName)) {
                savePropertyDefinition();

                if (DEBUG_MODE) {
                    System.err.println("saved property: id=" + this.id + ", type=" + this.type + ", required=" + this.required //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + ", defaultValue=" + this.defaultValue + ", fixedValue=" + this.fixedValue + ", advanced=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + this.advanced + ", masked=" + this.masked + ", index=" + this.index); //$NON-NLS-1$ //$NON-NLS-2$
                }

                // reset all property definition related fields
                this.id = null;
                this.type = null;
                this.required = null;
                this.defaultValue = null;
                this.fixedValue = null;
                this.advanced = null;
                this.masked = null;
                this.index = null;
                this.allowedValues.clear();
                this.propDefn = null;
                this.descriptions.clear();
                this.displayNames.clear();

                this.currentDescriptionLocale = null;
                this.currentDescriptionText = null;
                this.currentDisplayNameLocale = null;
                this.currentDisplayNameText = null;
            } else if (ExtensionConstants.Elements.EXTENDED_METACLASS.equals(localName)) {
                if (this.metaclassName != null && !this.metaclassName.isEmpty()) {
                    this.definition.addMetaclass(this.metaclassName);
                }

                if (DEBUG_MODE) {
                    System.err.println("reset: metaclassName=" + this.metaclassName); //$NON-NLS-1$
                }

                this.metaclassName = null;
            } else if (ExtensionConstants.Elements.ALLOWED_VALUE.equals(localName) && !this.allowedValues.isEmpty()) {
                if (DEBUG_MODE) {
                    String DELIM = ", "; //$NON-NLS-1$
                    StringBuilder valuesString = new StringBuilder();

                    for (String allowedValue : this.allowedValues) {
                        valuesString.append(allowedValue).append(DELIM);
                    }

                    System.err.println("allowedValues=" + valuesString.subSequence(0, valuesString.length() - DELIM.length())); //$NON-NLS-1$
                }
            } else if (ExtensionConstants.Elements.MODEL_EXTENSION.equals(localName)) {
                if (this.definition != null)
                    this.definition.setDescription(this.description);

                saveModelExtensionDefinitionProperties();

                if (DEBUG_MODE) {
                    System.err.println("reset: namespacePrefix=" + this.namespacePrefix + ", namespaceUri=" + this.namespaceUri //$NON-NLS-1$ //$NON-NLS-2$
                            + ", metamodelUri=" + this.metamodelUri + ", version=" + this.version + ", description=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + this.description);
                }
            }

            if (DEBUG_MODE) {
                System.err.println("endElement: currentElement=" + getCurrentElement() + ", previousElement=" //$NON-NLS-1$ //$NON-NLS-2$
                        + getPreviousElement());
            }

            this.elements.pop();

            super.endElement(uri, localName, qName);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
         */
        @Override
        public void error( SAXParseException e ) {
            this.errors.add(e.getLocalizedMessage());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
         */
        @Override
        public void fatalError( SAXParseException e ) {
            this.fatals.add(e.getLocalizedMessage());
        }

        /**
         * @return the element currently being parsed
         */
        private String getCurrentElement() {
            if (this.elements.empty()) {
                return null;
            }

            return this.elements.peek();
        }

        /**
         * @return the error messages output from the last parse operation (never <code>null</code> but can be empty)
         */
        Collection<String> getErrors() {
            return this.errors;
        }

        /**
         * @return the fatal error messages output from the last parse operation (never <code>null</code> but can be empty)
         */
        Collection<String> getFatalErrors() {
            return this.fatals;
        }

        /**
         * @return the information messages output from the last parse operation (never <code>null</code> but can be empty)
         */
        Collection<String> getInfos() {
            return this.infos;
        }

        ModelExtensionDefinition getModelExtensionDefinition() {
            return this.definition;
        }

        /**
         * @return the previously found element or <code>null</code>
         */
        private String getPreviousElement() {
            if (this.elements.size() > 1) {
                return this.elements.get(this.elements.size() - 2);
            }

            return null;
        }

        /**
         * @return the warning messages output from the last parse operation (never <code>null</code> but can be empty)
         */
        Collection<String> getWarnings() {
            return this.warnings;
        }

        /**
         * Saves the model extension definition that was just parsed.
         */
        private void saveModelExtensionDefinitionProperties() {
            for (Map.Entry<String, Collection<ModelExtensionPropertyDefinition>> entry : this.properties.entrySet()) {
                String extendedMetaclassName = entry.getKey();

                for (ModelExtensionPropertyDefinition propDefn : entry.getValue()) {
                    this.assistant.addPropertyDefinition(extendedMetaclassName, propDefn);
                }
            }
        }

        /**
         * Saves the property definition that was just parsed.
         */
        private void savePropertyDefinition() {
            this.propDefn = this.assistant.createPropertyDefinition(this.id, this.type, this.required, this.defaultValue,
                                                                    this.fixedValue, this.advanced, this.masked, this.index,
                                                                    this.allowedValues, this.descriptions, this.displayNames);

            // add all the property definition
            assert this.metaclassName != null : "metaclassName is null"; //$NON-NLS-1$
            Collection<ModelExtensionPropertyDefinition> props = this.properties.get(this.metaclassName);

            if (props == null) {
                props = new ArrayList<ModelExtensionPropertyDefinition>();
                this.properties.put(this.metaclassName, props);
            }

            props.add(this.propDefn);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#skippedEntity(java.lang.String)
         */
        @Override
        public void skippedEntity( String name ) {
            this.infos.add(NLS.bind(Messages.parserFoundUnparsedEntityDeclaration, name));
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String,
         *      org.xml.sax.Attributes)
         */
        @Override
        public void startElement( String uri,
                                  String localName,
                                  String qName,
                                  Attributes attributes ) throws SAXException {
            if (DEBUG_MODE) {
                System.err.println("startElement: uri" + uri + ", localName=" + localName + ", qName=" + qName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            this.elements.push(localName);

            if (ExtensionConstants.Elements.MODEL_EXTENSION.equals(getCurrentElement())) {
                this.namespacePrefix = attributes.getValue(ExtensionConstants.Attributes.NAMESPACE_PREFIX);
                this.namespaceUri = attributes.getValue(ExtensionConstants.Attributes.NAMESPACE_URI);
                this.metamodelUri = attributes.getValue(ExtensionConstants.Attributes.METAMODEL_URI);
                this.version = attributes.getValue(ExtensionConstants.Attributes.VERSION);
                this.definition = this.assistant.createModelExtensionDefinition(this.namespacePrefix, this.namespaceUri,
                                                                                this.metamodelUri, this.description, this.version);
            } else if (ExtensionConstants.Elements.EXTENDED_METACLASS.equals(getCurrentElement())) {
                this.metaclassName = attributes.getValue(ExtensionConstants.Attributes.NAME);
            } else if (ExtensionConstants.Elements.PROPERTY.equals(getCurrentElement())) {
                this.id = attributes.getValue(ExtensionConstants.Attributes.NAME);
                this.type = attributes.getValue(ExtensionConstants.Attributes.TYPE);
                this.required = attributes.getValue(ExtensionConstants.Attributes.REQUIRED);

                // optional attributes
                this.defaultValue = attributes.getValue(ExtensionConstants.Attributes.DEFAULT_VALUE);
                this.fixedValue = attributes.getValue(ExtensionConstants.Attributes.FIXED_VALUE);
                this.advanced = attributes.getValue(ExtensionConstants.Attributes.ADVANCED);
                this.masked = attributes.getValue(ExtensionConstants.Attributes.MASKED);
                this.index = attributes.getValue(ExtensionConstants.Attributes.INDEX);
            } else if (ExtensionConstants.Elements.DISPLAY.equals(getCurrentElement())) {
                this.currentDisplayNameLocale = attributes.getValue(ExtensionConstants.Attributes.LOCALE);
            } else if (ExtensionConstants.Elements.DESCRIPTION.equals(getCurrentElement())
                    && ExtensionConstants.Elements.PROPERTY.equals(getPreviousElement())) {
                this.currentDescriptionLocale = attributes.getValue(ExtensionConstants.Attributes.LOCALE);
            } else {
                if (DEBUG_MODE) {
                    System.err.println("\n\nstartElement not being process: currentElement=" + getCurrentElement() //$NON-NLS-1$
                            + ", previousElement=" + getPreviousElement()); //$NON-NLS-1$
                }
            }

            super.startElement(uri, localName, qName, attributes);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
         */
        @Override
        public void warning( SAXParseException e ) {
            this.warnings.add(e.getLocalizedMessage());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.xml.sax.helpers.DefaultHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String,
         *      java.lang.String)
         */
        @Override
        public void unparsedEntityDecl( String name,
                                        String publicId,
                                        String systemId,
                                        String notationName ) {
            this.infos.add(NLS.bind(Messages.parserFoundUnparsedEntityDeclaration, name));
        }

    }

}
