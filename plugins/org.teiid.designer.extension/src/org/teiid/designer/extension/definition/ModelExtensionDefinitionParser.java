/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static org.teiid.designer.extension.ExtensionPlugin.PLUGIN_ID;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
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

    /**
     * The parser of the definition stream.
     */
    private SAXParser parser;

    /**
     * Constructs a parser.
     * 
     * @throws IllegalStateException if there were problems with the model extension definition schema file
     */
    public ModelExtensionDefinitionParser() throws IllegalStateException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);

            this.parser = factory.newSAXParser();
            this.parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$ //$NON-NLS-2$

            // set schema on parser
            final String SCHEMA_FILE = "modelExtension.xsd"; //$NON-NLS-1$
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            URL url = bundle.getEntry(SCHEMA_FILE);

            if (url == null) {
                throw new IllegalStateException(NLS.bind(Messages.definitionSchemaFileNotFoundInWorkspace, SCHEMA_FILE));
            }

            File definitionSchemaFile = new File(FileLocator.toFileURL(url).getFile());

            if (!definitionSchemaFile.exists()) {
                throw new IllegalStateException(NLS.bind(Messages.definitionSchemaFileNotFoundInFilesystem, SCHEMA_FILE));
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
     * @param definitionStream the model extension definition input stream (cannot be <code>null</code>)
     * @param assistant the model extension assistant (cannot be <code>null</code>)
     * @return the model extension definition (never <code>null</code>)
     * @throws Exception if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    public ModelExtensionDefinition parse( InputStream definitionStream,
                                           ModelExtensionAssistant assistant ) throws Exception {
        CoreArgCheck.isNotNull(definitionStream, "definitionStream is null"); //$NON-NLS-1$

        Handler handler = new Handler(assistant);
        this.parser.parse(definitionStream, handler);
        return handler.getModelExtensionDefinition();
    }

    /**
     * The handler used by the parser. Each instance should be only used to parse one file.
     */
    class Handler extends DefaultHandler {

        private String advanced;
        private Collection<String> allowedValues = new ArrayList<String>();

        private final ModelExtensionAssistant assistant;
        private String defaultValue;
        private ModelExtensionDefinition definition;
        private String description;
        private boolean descriptionLanguageCountryMatch;
        private boolean descriptionLanguageMatch;
        private boolean descriptionLocaleMatch;

        private String displayDescription;
        private String displayName;
        private final Stack<String> elements;
        private String fixedValue;
        private String id;
        private String index;
        private String masked;
        private String metaclassName;
        private String metamodelUri;
        private boolean nameLanguageCountryMatch;
        private boolean nameLanguageMatch;
        private boolean nameLocaleMatch;
        private String namespacePrefix;

        private String namespaceUri;
        private ModelExtensionPropertyDefinition propDefn;
        private final Map<String, Collection<ModelExtensionPropertyDefinition>> properties;
        private String required;
        private String type;
        private boolean updateDescription = false;
        private boolean updateName = false;
        private String version;

        /**
         * @param assistant the model extension assistant used by the parser (cannot be <code>null</code>)
         */
        public Handler( ModelExtensionAssistant assistant ) {
            CoreArgCheck.isNotNull(assistant, "assistant is null"); //$NON-NLS-1$

            this.assistant = assistant;
            this.properties = new HashMap<String, Collection<ModelExtensionPropertyDefinition>>();
            this.elements = new Stack<String>();
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

            if (Tags.Elements.DESCRIPTION.equals(getCurrentElement())) {
                if (Tags.Elements.MODEL_EXTENSION.equals(getPreviousElement())) {
                    // model extension definition is not localized
                    this.description = value;
                } else if (Tags.Elements.PROPERTY.equals(getPreviousElement())) {
                    if (this.updateDescription) {
                        this.displayDescription = value;
                    } else {
                        if (DEBUG_MODE) {
                            System.err.println("display description characters not used=" + value); //$NON-NLS-1$
                        }
                    }
                } else {
                    // should not get here
                    assert false : "Unexpected previous tag of " + getPreviousElement() + " while processing the " //$NON-NLS-1$ //$NON-NLS-2$
                            + Tags.Elements.DESCRIPTION + " tag"; //$NON-NLS-1$
                }
            } else if (Tags.Elements.DISPLAY.equals(getCurrentElement())) {
                if (Tags.Elements.PROPERTY.equals(getPreviousElement())) {
                    if (this.updateName) {
                        this.displayName = value;
                    } else {
                        if (DEBUG_MODE) {
                            System.err.println("display name characters not used=" + value); //$NON-NLS-1$
                        }
                    }
                } else {
                    // should not get here
                    assert false : "Unexpected previous tag of " + getPreviousElement() + " while processing the " //$NON-NLS-1$ //$NON-NLS-2$
                            + Tags.Elements.DISPLAY + " tag"; //$NON-NLS-1$
                }
            } else if (Tags.Elements.ALLOWED_VALUE.equals(getCurrentElement())) {
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

            if (Tags.Elements.DISPLAY.equals(localName)) {
                if (DEBUG_MODE) {
                    System.err.println("reset: nameLocaleMatch=" + this.nameLocaleMatch + ", nameLanguageCountryMatch=" //$NON-NLS-1$ //$NON-NLS-2$
                            + this.nameLanguageCountryMatch + ", nameLanguageMatch=" + this.nameLanguageMatch); //$NON-NLS-1$
                }
            } else if (Tags.Elements.DESCRIPTION.equals(localName)) {
                if (DEBUG_MODE) {
                    System.err.println("reset: descriptionLocaleMatch=" + this.descriptionLocaleMatch //$NON-NLS-1$
                            + ", descriptionLanguageCountryMatch=" + this.descriptionLanguageCountryMatch //$NON-NLS-1$
                            + ", descriptionLanguageMatch=" + this.descriptionLanguageMatch); //$NON-NLS-1$
                }
            } else if (Tags.Elements.PROPERTY.equals(localName)) {
                savePropertyDefinition();

                if (DEBUG_MODE) {
                    System.err.println("reset: id=" + this.id + ", displayName=" + this.displayName + ", type=" + this.type //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + ", required=" + this.required + ", defaultValue=" + this.defaultValue + ", fixedValue=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + this.fixedValue + ", advanced=" + this.advanced + ", masked=" + this.masked + ", displayDescription=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + this.displayDescription + ", index=" + this.index); //$NON-NLS-1$
                }

                // reset all property definition related fields
                this.id = null;
                this.displayName = null;
                this.type = null;
                this.required = null;
                this.defaultValue = null;
                this.fixedValue = null;
                this.advanced = null;
                this.masked = null;
                this.index = null;
                this.displayDescription = null;
                this.allowedValues.clear();
                this.propDefn = null;

                // reset locale specific fields
                this.updateDescription = false;
                this.descriptionLocaleMatch = false;
                this.descriptionLanguageCountryMatch = false;
                this.descriptionLanguageMatch = false;
                this.updateName = false;
                this.nameLocaleMatch = false;
                this.nameLanguageCountryMatch = false;
                this.nameLanguageMatch = false;
            } else if (Tags.Elements.EXTENDED_METACLASS.equals(localName)) {
                if (DEBUG_MODE) {
                    System.err.println("reset: metaclassName=" + this.metaclassName); //$NON-NLS-1$
                }

                this.metaclassName = null;
            } else if (Tags.Elements.ALLOWED_VALUE.equals(localName) && !this.allowedValues.isEmpty()) {
                if (DEBUG_MODE) {
                    String DELIM = ", "; //$NON-NLS-1$
                    StringBuilder valuesString = new StringBuilder();

                    for (String allowedValue : this.allowedValues) {
                        valuesString.append(allowedValue).append(DELIM);
                    }

                    System.err.println("allowedValues=" + valuesString.subSequence(0, valuesString.length() - DELIM.length())); //$NON-NLS-1$
                }
            } else if (Tags.Elements.MODEL_EXTENSION.equals(localName)) {
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
        public void error( SAXParseException e ) throws SAXException {
            // overriding this method is needed to stop parsing
            // exception indicates a validation or format problem
            throw e;
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
         * @param localeString the string representing the local of the display string
         * @param displayNameCheck <code>true</code> if the display string is a property definition display name
         */
        private void processLocaleString( String localeString,
                                          boolean displayNameCheck ) {
            Locale locale = null;
            boolean localeMatch = (displayNameCheck ? this.nameLocaleMatch : this.descriptionLocaleMatch);
            boolean languageCountryMatch = (displayNameCheck ? this.nameLanguageCountryMatch : this.descriptionLanguageCountryMatch);
            boolean languageMatch = (displayNameCheck ? this.nameLanguageMatch : this.descriptionLanguageMatch);
            boolean updateNeeded = true;

            if (CoreStringUtil.isEmpty(localeString)) {
                locale = LOCALE;
                localeMatch = true;
            } else {
                locale = I18nUtil.parseLocaleString(localeString);

                if (!localeMatch) {
                    if (LOCALE.equals(locale)) {
                        localeMatch = true;
                        languageCountryMatch = false;
                        languageMatch = false;
                    } else if (!languageCountryMatch && LOCALE.getLanguage().equals(locale.getLanguage())
                            && LOCALE.getCountry().equals(locale.getCountry())) {
                        languageCountryMatch = true;
                        languageMatch = false;
                    } else if (!languageMatch && LOCALE.getLanguage().equals(locale.getLanguage())) {
                        languageMatch = true;
                    } else {
                        updateNeeded = false;
                    }
                } else {
                    updateNeeded = false;
                }
            }

            if (displayNameCheck) {
                this.updateName = updateNeeded;
                this.nameLocaleMatch = localeMatch;
                this.nameLanguageCountryMatch = languageCountryMatch;
                this.nameLanguageMatch = languageMatch;
            } else {
                this.updateDescription = updateNeeded;
                this.descriptionLocaleMatch = localeMatch;
                this.descriptionLanguageCountryMatch = languageCountryMatch;
                this.descriptionLanguageMatch = languageMatch;
            }
        }

        /**
         * Saves the model extension definition that was just parsed.
         */
        private void saveModelExtensionDefinitionProperties() {
            this.assistant.setDefinitionDescription(this.description);
            this.assistant.setDefinitionVersion(this.version);

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
            this.propDefn = this.assistant.createPropertyDefinition(this.id, this.displayName, this.type, this.required,
                                                                    this.defaultValue, this.fixedValue, this.advanced, this.masked,
                                                                    this.index);
            // if necessary set description
            if (!CoreStringUtil.isEmpty(this.displayDescription)) {
                this.assistant.setDescription(this.propDefn, this.displayDescription);
            }

            // if necessary set allowed values
            String[] values = (this.allowedValues.isEmpty() ? null
                                                           : this.allowedValues.toArray(new String[this.allowedValues.size()]));
            if (values != null) {
                this.assistant.setAllowedValues(this.propDefn, values);
            }

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

            if (Tags.Elements.MODEL_EXTENSION.equals(getCurrentElement())) {
                this.namespacePrefix = attributes.getValue(Tags.Attributes.NAMESPACE_PREFIX);
                assert !CoreStringUtil.isEmpty(this.namespacePrefix) : "namespacePrefix is empty"; //$NON-NLS-1$

                this.namespaceUri = attributes.getValue(Tags.Attributes.NAMESPACE_URI);
                assert !CoreStringUtil.isEmpty(this.namespaceUri) : "namespaceUri is empty"; //$NON-NLS-1$

                this.metamodelUri = attributes.getValue(Tags.Attributes.METAMODEL_URI);
                assert !CoreStringUtil.isEmpty(this.metamodelUri) : "metamodelUri is empty"; //$NON-NLS-1$

                this.version = attributes.getValue(Tags.Attributes.VERSION);
                assert !CoreStringUtil.isEmpty(this.version) : "version is empty"; //$NON-NLS-1$

                this.definition = this.assistant.createModelExtensionDefinition(this.namespacePrefix, this.namespaceUri,
                                                                                this.metamodelUri);
            } else if (Tags.Elements.EXTENDED_METACLASS.equals(getCurrentElement())) {
                this.metaclassName = attributes.getValue(Tags.Attributes.NAME);
                assert !CoreStringUtil.isEmpty(this.metaclassName) : "metaclassName is empty"; //$NON-NLS-1$
            } else if (Tags.Elements.PROPERTY.equals(getCurrentElement())) {
                this.id = attributes.getValue(Tags.Attributes.NAME);
                assert !CoreStringUtil.isEmpty(this.id) : "id is empty"; //$NON-NLS-1$

                this.type = attributes.getValue(Tags.Attributes.TYPE);
                assert !CoreStringUtil.isEmpty(this.type) : "type is empty"; //$NON-NLS-1$

                this.required = attributes.getValue(Tags.Attributes.REQUIRED);
                assert !CoreStringUtil.isEmpty(this.required) : "required is empty"; //$NON-NLS-1$

                // optional attributes
                this.defaultValue = attributes.getValue(Tags.Attributes.DEFAULT_VALUE);
                this.fixedValue = attributes.getValue(Tags.Attributes.FIXED_VALUE);

                this.advanced = attributes.getValue(Tags.Attributes.ADVANCED);
                assert !CoreStringUtil.isEmpty(this.advanced) : "advanced is empty"; //$NON-NLS-1$

                this.masked = attributes.getValue(Tags.Attributes.MASKED);
                assert !CoreStringUtil.isEmpty(this.masked) : "masked is empty"; //$NON-NLS-1$

                this.index = attributes.getValue(Tags.Attributes.INDEX);
                assert !CoreStringUtil.isEmpty(this.index) : "index is empty"; //$NON-NLS-1$
            } else if (Tags.Elements.DISPLAY.equals(getCurrentElement())) {
                processLocaleString(attributes.getValue(Tags.Attributes.LOCALE), true);
            } else if (Tags.Elements.DESCRIPTION.equals(getCurrentElement()) && Tags.Elements.PROPERTY.equals(getPreviousElement())) {
                processLocaleString(attributes.getValue(Tags.Attributes.LOCALE), false);
            } else {
                if (DEBUG_MODE) {
                    System.err.println("\n\nstartElement not being process: currentElement=" + getCurrentElement() //$NON-NLS-1$
                            + ", previousElement=" + getPreviousElement()); //$NON-NLS-1$
                }
            }

            super.startElement(uri, localName, qName, attributes);
        }
    }

    /**
     * The model extension definition schema tags.
     */
    private interface Tags {

        /**
         * The model extension definition schema attribute names.
         */
        interface Attributes {
            String ADVANCED = "advanced"; //$NON-NLS-1$
            String DEFAULT_VALUE = "defaultValue"; //$NON-NLS-1$
            String FIXED_VALUE = "fixedValue"; //$NON-NLS-1$
            String INDEX = "index"; //$NON-NLS-1$
            String LOCALE = "locale"; //$NON-NLS-1$
            String MASKED = "masked"; //$NON-NLS-1$
            String METAMODEL_URI = "metamodelUri"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String NAMESPACE_PREFIX = "namespacePrefix"; //$NON-NLS-1$
            String NAMESPACE_URI = "namespaceUri"; //$NON-NLS-1$
            String REQUIRED = "required"; //$NON-NLS-1$
            String TYPE = "type"; //$NON-NLS-1$
            String VERSION = "version"; //$NON-NLS-1$
        }

        /**
         * The model extension definition schema element names.
         */
        interface Elements {
            String ALLOWED_VALUE = "allowedValue"; //$NON-NLS-1$
            String DESCRIPTION = "description"; //$NON-NLS-1$
            String DISPLAY = "display"; //$NON-NLS-1$
            String EXTENDED_METACLASS = "extendedMetaclass"; //$NON-NLS-1$
            String MODEL_EXTENSION = "modelExtension"; //$NON-NLS-1$
            String PROPERTY = "property"; //$NON-NLS-1$
        }
    }
}
