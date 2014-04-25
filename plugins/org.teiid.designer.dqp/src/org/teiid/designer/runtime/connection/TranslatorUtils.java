package org.teiid.designer.runtime.connection;


import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.properties.PropertyDefinition;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.translators.TranslatorProperty;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;


public class TranslatorUtils {

    public static ITeiidServer getDefaultServer() {
        return DqpPlugin.getInstance().getServerManager().getDefaultServer();
    }

    public static PropertyDefinition[] getTranslatorPropertyDefinitions( String translatorName, ITeiidTranslator.TranslatorPropertyType type) {
        if (StringUtilities.isEmpty(translatorName)) {
            throw new IllegalArgumentException();
        }

        ITeiidServer defaultServer = getDefaultServer();

        if ((defaultServer != null) && defaultServer.isConnected()) {
            try {
                ITeiidTranslator translator = defaultServer.getTranslator(translatorName);

                if (translator != null) {
                    if( type == ITeiidTranslator.TranslatorPropertyType.OVERRIDE ) {
                    	return getOverrideDefinitions(defaultServer, translator);
                    } else if( type == ITeiidTranslator.TranslatorPropertyType.IMPORT ) {
                    	return getImportDefinitions(defaultServer, translator);
                    } else if( type == ITeiidTranslator.TranslatorPropertyType.EXTENSION_METADATA ) {
                    	return getExtensionDefinitions(defaultServer, translator);
                    }

                    return new PropertyDefinition[0];
                }
            } catch (Exception e) {
                DqpPlugin.Util.log(IStatus.ERROR, e, DqpPlugin.Util.getString("errorObtainingTranslatorProperties", //$NON-NLS-1$
                                                          translatorName,
                                                          defaultServer.getHost()));
            }
        }

        return null;
    }
    
    private static PropertyDefinition[] getOverrideDefinitions(ITeiidServer defaultServer, ITeiidTranslator translator ) {

        Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
        
        // NOTE: For server versions prior to 8.6, translator.getPropertyDefinitions() will only return property definitions for
        // the resource-adapter properties and NOT the translator.
        // So added the check and ignore getting translator properties if version < 8.6
        
        if( defaultServer.getServerVersion().isGreaterThanOrEqualTo(Version.TEIID_8_6.get())) {
            for (TeiidPropertyDefinition propDefn : translator.getPropertyDefinitions()) {
                TranslatorProperty prop = new TranslatorProperty(propDefn.getPropertyTypeClassName());
                prop.setAdvanced(propDefn.isAdvanced());
                prop.setDescription( propDefn.getDescription());
                prop.setDisplayName( propDefn.getDisplayName());
                prop.setId( propDefn.getName());
                prop.setMasked( propDefn.isMasked());
                prop.setModifiable( propDefn.isModifiable());
                prop.setRequired( propDefn.isRequired());

                prop.setDefaultValue((propDefn.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                                        : propDefn.getDefaultValue().toString());

                if (propDefn.isConstrainedToAllowedValues()) {
                    Collection<?> values = propDefn.getAllowedValues();
                    prop.setAllowedValues(new String[values.size()]);
                    int i = 0;

                    for (Object value : values) {
                        prop.getAllowedValues()[i++] = value.toString();
                    }
                } else {
                    // if boolean type turn into allowed values
                    String type = propDefn.getPropertyTypeClassName();

                    if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
                        prop.setAllowedValues(new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });
                    }
                }

                props.add(prop);
            }
        }

        return props.toArray(new PropertyDefinition[props.size()]);
    }
    
    private static PropertyDefinition[] getImportDefinitions(ITeiidServer defaultServer, ITeiidTranslator translator ) {

        Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
        
        // NOTE: For server versions prior to 8.6, translator.getPropertyDefinitions() will only return property definitions for
        // the resource-adapter properties and NOT the translator.
        // So added the check and ignore getting translator properties if version < 8.6
        
        if( defaultServer.getServerVersion().isGreaterThanOrEqualTo(Version.TEIID_8_6.get())) {
            for (TeiidPropertyDefinition propDefn : translator.getImportPropertyDefinitions()) {
                TranslatorProperty prop = new TranslatorProperty(propDefn.getPropertyTypeClassName());
                prop.setAdvanced(propDefn.isAdvanced());
                prop.setDescription( propDefn.getDescription());
                prop.setDisplayName( propDefn.getDisplayName());
                prop.setId( propDefn.getName());
                prop.setMasked( propDefn.isMasked());
                prop.setModifiable( propDefn.isModifiable());
                prop.setRequired( propDefn.isRequired());

                prop.setDefaultValue((propDefn.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                                        : propDefn.getDefaultValue().toString());

                if (propDefn.isConstrainedToAllowedValues()) {
                    Collection<?> values = propDefn.getAllowedValues();
                    prop.setAllowedValues(new String[values.size()]);
                    int i = 0;

                    for (Object value : values) {
                        prop.getAllowedValues()[i++] = value.toString();
                    }
                } else {
                    // if boolean type turn into allowed values
                    String type = propDefn.getPropertyTypeClassName();

                    if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
                        prop.setAllowedValues(new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });
                    }
                }

                props.add(prop);
            }
        }

        return props.toArray(new PropertyDefinition[props.size()]);
    }
    
    private static PropertyDefinition[] getExtensionDefinitions(ITeiidServer defaultServer, ITeiidTranslator translator ) {

        Collection<PropertyDefinition> props = new ArrayList<PropertyDefinition>();
        
        // NOTE: For server versions prior to 8.6, translator.getPropertyDefinitions() will only return property definitions for
        // the resource-adapter properties and NOT the translator.
        // So added the check and ignore getting translator properties if version < 8.6
        
        if( defaultServer.getServerVersion().isGreaterThanOrEqualTo(Version.TEIID_8_6.get())) {
            for (TeiidPropertyDefinition propDefn : translator.getExtensionPropertyDefinitions()) {
                TranslatorProperty prop = new TranslatorProperty(propDefn.getPropertyTypeClassName());
                prop.setAdvanced(propDefn.isAdvanced());
                prop.setDescription( propDefn.getDescription());
                prop.setDisplayName( propDefn.getDisplayName());
                prop.setId( propDefn.getName());
                prop.setMasked( propDefn.isMasked());
                prop.setModifiable( propDefn.isModifiable());
                prop.setRequired( propDefn.isRequired());

                prop.setDefaultValue((propDefn.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                                        : propDefn.getDefaultValue().toString());

                if (propDefn.isConstrainedToAllowedValues()) {
                    Collection<?> values = propDefn.getAllowedValues();
                    prop.setAllowedValues(new String[values.size()]);
                    int i = 0;

                    for (Object value : values) {
                        prop.getAllowedValues()[i++] = value.toString();
                    }
                } else {
                    // if boolean type turn into allowed values
                    String type = propDefn.getPropertyTypeClassName();

                    if (Boolean.class.getName().equals(type) || Boolean.TYPE.getName().equals(type)) {
                        prop.setAllowedValues(new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() });
                    }
                }

                props.add(prop);
            }
        }

        return props.toArray(new PropertyDefinition[props.size()]);
    }
}
