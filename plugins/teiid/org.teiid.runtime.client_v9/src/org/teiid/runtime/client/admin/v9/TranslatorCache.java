/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.admin.v9;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.cli.Util;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.Admin.TranlatorPropertyType;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminProcessingException;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.jboss.VDBMetadataMapper;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.admin.TeiidTranslator;

public class TranslatorCache implements AdminConstants {

	private AdminConnectionManager manager;
	private ITeiidServer teiidServer;
	
	private Map<String, Translator> translatorByNameMap;
	private Map<String, PropertyDefinition> translatorToPropDefMap;
	
	private Map<String, ITeiidTranslator> teiidTranslatorByNameMap;
	
	public TranslatorCache(ModelControllerClient connection, ITeiidServer teiidServer) {
		super();
		
		this.manager = new AdminConnectionManager(connection, teiidServer.getServerVersion());
		this.teiidServer = teiidServer;
		
		this.translatorByNameMap = new HashMap<String, Translator>();
		this.translatorToPropDefMap = new HashMap<String, PropertyDefinition>();
		this.teiidTranslatorByNameMap = new HashMap<String, ITeiidTranslator>();
	}
	
	public void refresh() throws AdminException {
		this.translatorByNameMap.clear();
		this.translatorToPropDefMap.clear();
		
		Collection<? extends Translator> translators = loadTranslators();
		for( Translator translator : translators ) {
			String translatorName = translator.getName();
			
			this.translatorByNameMap.put(translatorName, translator);
			
			ITeiidTranslator teiidTranslator = null;
            if( this.manager.getTeiidServerVersion().isLessThan(Version.TEIID_8_6)) {
            	Collection<? extends PropertyDefinition> propDefs = getTemplatePropertyDefinitions(translatorName);
            	teiidTranslator =  new TeiidTranslator(translator, propDefs, this.teiidServer);
            } else if( teiidServer.getServerVersion().isLessThan(Version.TEIID_8_7)) {
				Collection<? extends PropertyDefinition> propDefs = getTranslatorPropertyDefinitions(translatorName);
            	teiidTranslator =   new TeiidTranslator(translator, propDefs, teiidServer);
            } else { // TEIID SERVER VERSION 8.7 AND HIGHER
            	Collection<? extends PropertyDefinition> propDefs  = 
            			getTranslatorPropertyDefinitions(translatorName, Admin.TranlatorPropertyType.OVERRIDE);
            	Collection<? extends PropertyDefinition> importPropDefs  = 
            			getTranslatorPropertyDefinitions(translatorName, Admin.TranlatorPropertyType.IMPORT);
            	Collection<? extends PropertyDefinition> extPropDefs  = 
            			getTranslatorPropertyDefinitions(translatorName, Admin.TranlatorPropertyType.EXTENSION_METADATA);
            	teiidTranslator = new TeiidTranslator(translator, propDefs, importPropDefs, extPropDefs, teiidServer);
            }
            
            this.teiidTranslatorByNameMap.put(translatorName, teiidTranslator);
		}
	}
	
    public ITeiidTranslator getTranslator( String name ) {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.teiidTranslatorByNameMap.get(name);
    }

    public Collection<ITeiidTranslator> getTranslators() {
        return Collections.unmodifiableCollection(teiidTranslatorByNameMap.values());
    }
	
	
	public Collection<? extends Translator> loadTranslators() throws AdminException {
        final ModelNode request = this.manager.buildRequest(TEIID, LIST_TRANSLATORS);//$NON-NLS-1$ //$NON-NLS-2$
        try {
            ModelNode outcome = this.manager.execute(request);
            if (Util.isSuccess(outcome)) {
                return this.manager.getDomainAwareList(outcome, VDBMetadataMapper.VDBTranslatorMetaDataMapper.INSTANCE);
            }
        } catch (IOException e) {
        	 throw new AdminComponentException(e);
        }

        return Collections.emptyList();
	}
	

    @Since(Version.TEIID_8_7)
    public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName, TranlatorPropertyType type) throws AdminException {
        BuildPropertyDefinitions builder = new BuildPropertyDefinitions();
        Translator translator = this.translatorByNameMap.get(translatorName);
        if (translator != null) {
            if (translator.getName().equalsIgnoreCase(translatorName)) {
                this.manager.cliCall(READ_TRANSLATOR_PROPERTIES,
                        new String[] {SUBSYSTEM, TEIID},
                        new String[] {TRANSLATOR_NAME, translatorName, TYPE, type.name()},
                        builder);
                return builder.getPropertyDefinitions();
            }
        }
        throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70055, translatorName));
    }
    
    @Deprecated
    public Collection<? extends PropertyDefinition> getTranslatorPropertyDefinitions(String translatorName) throws AdminException{
		BuildPropertyDefinitions builder = new BuildPropertyDefinitions();
        Translator translator = this.translatorByNameMap.get(translatorName);
        if (translator != null) {
			if (translator.getName().equalsIgnoreCase(translatorName)) {
			    
			    List<String> translatorProperties = new ArrayList<String>();
			    translatorProperties.add("translator-name");
			    translatorProperties.add(translatorName);
			    
			    if (this.manager.getTeiidServerVersion().isGreaterThanOrEqualTo(Version.TEIID_8_7)) {
			        translatorProperties.add("type");
			        translatorProperties.add(TranlatorPropertyType.OVERRIDE.name());
			    }
			    
                this.manager.cliCall(READ_TRANSLATOR_PROPERTIES,
                        new String[] {SUBSYSTEM, TEIID},
        		        translatorProperties.toArray(new String[0]),
        		        builder);
        		return builder.getPropertyDefinitions();
			}
		}
		throw new AdminProcessingException(Messages.gs(Messages.TEIID.TEIID70055, translatorName));
    }
    
	public Collection<PropertyDefinition> getTemplatePropertyDefinitions(String templateName) throws AdminException {
        return new ArrayList<PropertyDefinition>();
	}


    @SuppressWarnings("unchecked")
	public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
    	
        Collection<? extends PropertyDefinition> propDefs = getTemplatePropertyDefinitions(templateName);

        Collection<TeiidPropertyDefinition> teiidPropDefns = new ArrayList<TeiidPropertyDefinition>();
        
        for (PropertyDefinition propDefn : propDefs) {
            TeiidPropertyDefinition teiidPropertyDefn = new TeiidPropertyDefinition();
            
            teiidPropertyDefn.setName(propDefn.getName());
            teiidPropertyDefn.setDisplayName(propDefn.getDisplayName());
            teiidPropertyDefn.setDescription(propDefn.getDescription());
            teiidPropertyDefn.setPropertyTypeClassName(propDefn.getPropertyTypeClassName());
            teiidPropertyDefn.setDefaultValue(propDefn.getDefaultValue());
            teiidPropertyDefn.setAllowedValues(propDefn.getAllowedValues());
            teiidPropertyDefn.setModifiable(propDefn.isModifiable());
            teiidPropertyDefn.setConstrainedToAllowedValues(propDefn.isConstrainedToAllowedValues());
            teiidPropertyDefn.setAdvanced(propDefn.isAdvanced());
            teiidPropertyDefn.setRequired(propDefn.isRequired());
            teiidPropertyDefn.setMasked(propDefn.isMasked());
            
            teiidPropDefns.add(teiidPropertyDefn);
        }
        
        return teiidPropDefns;
    }

}
