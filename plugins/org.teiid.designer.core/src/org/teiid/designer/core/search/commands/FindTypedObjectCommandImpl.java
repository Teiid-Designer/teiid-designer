/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.core.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.index.ModelWorkspaceSearchIndexSelector;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect;
import org.teiid.designer.core.search.runtime.SearchRuntimeAdapter;


/**
 * FindRelationshipsCommandImpl.java
 */
public class FindTypedObjectCommandImpl implements FindTypedObjectCommand {

    private static final EObject[] EMPTY_EOBJECT_ARRAY = new EObject[0];
    
    private EClass metaClass;
    
    private EObject datatype;
    private EObject[] subtypes;
    private String runtimeType;

	private Collection recordInfos = Collections.EMPTY_LIST;

    private IndexSelector selector;
    
//    private DatatypeManager dtMgr;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /** 
     * 
     * @since 4.1
     */
    public FindTypedObjectCommandImpl() {
        super();
        this.metaClass = null;
        this.datatype = null;
        this.subtypes = EMPTY_EOBJECT_ARRAY;
        this.runtimeType = null;
        this.selector = null;
//        this.dtMgr = ModelerCore.getDatatypeManager();
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see org.teiid.designer.core.search.commands.FindTypedObjectCommand#getRecordInfo()
     * @since 4.1
     */
    @Override
	public Collection getRecordInfo() {
        return this.recordInfos;
    }
    
    /** 
     * @see org.teiid.designer.core.search.commands.FindTypedObjectCommand#setMetaClass(org.eclipse.emf.ecore.EClass)
     * @since 4.1
     */
    @Override
	public void setMetaClass(EClass metaClass) {
        this.metaClass = metaClass;
    }
    
    /** 
     * @see org.teiid.designer.core.search.commands.FindTypedObjectCommand#setDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.1
     */
    @Override
	public void setDatatype(final EObject datatype) {
        this.datatype = datatype;
    }
    
    /** 
     * @see org.teiid.designer.core.search.commands.FindTypedObjectCommand#setRuntimeType(java.lang.String)
     * @since 4.1
     */
    @Override
	public void setRuntimeType(final String runtimeType) {
        this.runtimeType = runtimeType;
    }
    
    /** 
     * @see org.teiid.designer.core.search.commands.FindTypedObjectCommand#setSubTypes(org.eclipse.emf.ecore.EObject[])
     * @since 4.1
     */
    @Override
	public void setSubTypes(final EObject[] subTypes) {
        this.subtypes = subTypes;
    }

    /**
     * @see org.teiid.designer.core.search.commands.FindRelationshipsCommand#setIndexSelector(org.teiid.designer.core.index.IndexSelector)
     */
    @Override
	public void setIndexSelector(final IndexSelector selector) {
        this.selector = selector;
    }
    
    /** 
     * @see org.teiid.designer.core.search.commands.SearchCommand#canExecute()
     * @since 4.1
     */
    @Override
	public boolean canExecute() {
        return (this.datatype != null || this.runtimeType != null);
    }
    /** 
     * @see org.teiid.designer.core.search.commands.SearchCommand#execute()
     * @since 4.1
     */
    @Override
	public IStatus execute() {
        if (!canExecute()) {
            return null;
        }

        try {
            String typeName        = getDatatypeName(datatype);
            String runtimeTypeName = this.runtimeType;
            String metaclassUri    = getMetaclassUri(metaClass);
            String matchPattern    = this.getMatchPattern(typeName, runtimeTypeName, metaclassUri);
            IEntryResult[] results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), 
                                                          matchPattern.toCharArray(), 
                                                          IndexConstants.RECORD_STRING.RECORD_DELIMITER);
            this.recordInfos = new ArrayList(results.length);
            for(int i=0; i < results.length; i++) {
                IEntryResult result = results[i];
                this.recordInfos.add(SearchRuntimeAdapter.getSearchRecord(result.getWord()));
            }
            
            if (this.subtypes != null && this.subtypes.length > 0) {
                for (int j = 0; j != this.subtypes.length; ++j) {
                    typeName     = getDatatypeName(this.subtypes[j]);
                    matchPattern = this.getMatchPattern(typeName, runtimeTypeName, metaclassUri);
                    results = IndexUtil.queryIndex(getIndexSelector().getIndexes(), 
                                                   matchPattern.toCharArray(), 
                                                   IndexConstants.RECORD_STRING.RECORD_DELIMITER);
                    for(int i=0; i < results.length; i++) {
                        IEntryResult result = results[i];
                        this.recordInfos.add(SearchRuntimeAdapter.getSearchRecord(result.getWord()));
                    }
                }
            }

        } catch (Exception e) {
            ModelerCore.Util.log(e);
            final String msg = ModelerCore.Util.getString("FindTypedObjectCommandImpl.Error_trying_to_execute_command"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID,0,msg,e);
        }
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
    }


    /*
     * Get the match pattern based on the specified criteria used to search for index records
     * of the form:  recordType|objectID|name|fullname|uri|datatypeName|datatypeID|runtimeType|modelPath|metaclassURI|
     */
    private String getMatchPattern(final String typeName, final String runtimeTypeName, final String metaClassUri) {
        String pattern = null;
        
        final String datatypeCriteria    = (typeName != null && typeName.length() != 0) ? 
                                            typeName : 
                                            IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
        final String runtimeTypeCriteria = (runtimeTypeName != null && runtimeTypeName.length() != 0) ? 
                                            runtimeTypeName : 
                                            IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
        final String metaclassCriteria   = (metaClassUri != null && metaClassUri.length() != 0) ? 
                                            metaClassUri : 
                                            IndexConstants.RECORD_STRING.MATCH_CHAR_STRING;
        pattern = CoreStringUtil.Constants.EMPTY_STRING 
                + IndexConstants.SEARCH_RECORD_TYPE.TYPED_OBJECT  // recordType
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // objectID
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // name
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // fullname
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // uri
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + datatypeCriteria                                // datatypeName 
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // datatypeID
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + runtimeTypeCriteria                             // runtimeType 
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + IndexConstants.RECORD_STRING.MATCH_CHAR_STRING  // modelPath
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER
                + metaclassCriteria                               // metaclassURI 
                + IndexConstants.RECORD_STRING.RECORD_DELIMITER;
        
        return pattern;
    }

    
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================
    
    private IndexSelector getIndexSelector() {
        // selector used to lookup relationship indexes     
        this.selector = this.selector != null ?
                                       this.selector :
                                       new ModelWorkspaceSearchIndexSelector();
        return this.selector;
    }

    private static String getDatatypeName(final EObject type) {
        SqlDatatypeAspect sqlAspect = getSqlAspect(type);
        if (sqlAspect != null) {
            return sqlAspect.getName(type);
        }
        return null;
    }

    private static String getMetaclassUri(final EClass eClass) {
        if (eClass != null) {
            return ModelerCore.getModelEditor().getUri(eClass).toString();
        }
        return null;
    }
    
    private static SqlDatatypeAspect getSqlAspect(final EObject obj) {
        if (obj != null && obj instanceof XSDSimpleTypeDefinition) {
            return (SqlDatatypeAspect) ModelerCore.getMetamodelRegistry().getMetamodelAspect(obj,SqlAspect.class);
        }
        return null;
    }   

}
