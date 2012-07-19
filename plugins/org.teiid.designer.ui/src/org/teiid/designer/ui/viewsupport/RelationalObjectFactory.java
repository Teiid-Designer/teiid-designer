/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelResourceContainerFactory;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.View;
import org.teiid.designer.ui.UiConstants;




/** 
 * @since 8.0
 */
public class RelationalObjectFactory {
    //============================================================================================================================
    // Static Constants
    
    private static final String I18N_PREFIX             = I18nUtil.getPropertyPrefix(RelationalObjectFactory.class);
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
    
    public static final int UNKNOWN = -1;
    
    public static final int BASE_TABLE = 10;
    public static final int VIEW = 11;
    public static final int INDEX = 12;
    public static final int COLUMN = 13;
    public static final int UNIQUE_KEY = 14;
    public static final int PRIMARY_KEY = 15;
    public static final int PROCEDURE = 16;
    public static final int PARAMETER = 17;

    public static final String BASE_TABLE_STRING = "TABLE"; //$NON-NLS-1$
    public static final String VIEW_STRING = "VIEW"; //$NON-NLS-1$
    public static final String INDEX_STRING = "INDEX"; //$NON-NLS-1$
    public static final String COLUMN_STRING = "COLUMN"; //$NON-NLS-1$
    public static final String UNIQUE_KEY_STRING = "UNIQUEKEY"; //$NON-NLS-1$
    public static final String PRIMARY_KEY_STRING = "PRIMARYKEY"; //$NON-NLS-1$
    public static final String SCHEMA_STRING = "SCHEMA"; //$NON-NLS-1$
    public static final String CATALOG_STRING = "CATALOG"; //$NON-NLS-1$
    public static final String PROCEDURE_STRING = "PROCEDURE"; //$NON-NLS-1$
    public static final String PARAMETER_STRING = "PARAMETER"; //$NON-NLS-1$
    
    private static final String GET_MODEL_CONTENTS_ERROR 	= "getModelContentsError"; //$NON-NLS-1$
    private static final String ADD_VALUE_ERROR         	= "addValueError"; //$NON-NLS-1$
    
   
    //============================================================================================================================
    // Static Methods
    
//    private static String getString(final String id) {
//        return UiConstants.Util.getString(I18N_PREFIX + id);
//    }
    
    private static String getString(final String id, Object obj) {
        return UiConstants.Util.getString(I18N_PREFIX + id, obj);
    }
    
    private static String getString(String id, Object value, Object value2) {
        return UiConstants.Util.getString(I18N_PREFIX + id, value, value2);
    }
    
    private static boolean isTransactionable = ModelerCore.getPlugin() != null;
    
    private EObject defaultDatatype;
    private int defaultLength;
    
    private final ModelResource modelResource;
    
    /** 
     * 
     * @since 4.2
     */
    public RelationalObjectFactory(ModelResource modelResource) {
        super();
        this.modelResource = modelResource;
    }
    
    public BaseTable createBaseTable(
    		String name, 
    		String description, 
    		boolean supportsUpdate) {
    	
        BaseTable baseTable = factory.createBaseTable();
        if( baseTable != null ) {
            baseTable.setName(name);
            baseTable.setSupportsUpdate(supportsUpdate);
            
            // SET PARENT
            addValue(modelResource, baseTable, getModelResourceContents(modelResource));
            
            addDescription(baseTable, description);
        }
        
        return baseTable;
    }
    
    public View createView(
    		String name, 
    		String description, 
    		boolean supportsUpdate) {
    	
    	View view = factory.createView();
        if( view != null ) {
        	view.setName(name);
        	view.setSupportsUpdate(supportsUpdate);
        	
        	// SET PARENT
            addValue(modelResource, view, getModelResourceContents(modelResource));
            
            addDescription(view, description);
        }
        
        return view;
    }
    
    public Procedure createProcedure(
    		String name, 
    		String description) {
    	Procedure procedure = factory.createProcedure();
    	
        if( procedure != null ) {
        	procedure.setName(name);
        	
        	// SET PARENT
            addValue(modelResource, procedure, getModelResourceContents(modelResource));

            addDescription(procedure, description);
        }
        
        return procedure;
    }
    
    public Procedure createPushdownFunction(
    		String name, 
    		String description) {
    	Procedure procedure = createProcedure(name, description);
    	procedure.setFunction(true);
        
        return procedure;
    }
    
//    public EObject createIndex(ModelResource modelResource, String name, String description, Object parent) {
//        Index index = factory.createIndex();
//        if( index != null ) {
//            index.setName(name);
//            index.setUnique(row.isUnique());
//            // let's walk through the columnNames for the index
//            // and get the EObject out of the columnMap
//            if( columnMap != null && !columnMap.isEmpty() ) {
//                Iterator iter = row.getColumnNames().iterator();
//                while( iter.hasNext() ) {
//                    String nextName = (String)iter.next();
//                    EObject eObj = (EObject)columnMap.get(nextName);
//                    if( eObj != null ) {
//                    	addValue(index, eObj, index.getColumns());
//                    }
//                }
//            }
//                
//            if( parent instanceof ModelResource ) {
//            	addValue(parent, index, getModelResourceContents(modelResource));
//            } else if( parent instanceof Schema ) {
//                index.setSchema((Schema)parent);
//                addValue(parent, index, ((Schema)parent).getIndexes());
//            } else if( parent instanceof Catalog ) {
//                index.setCatalog((Catalog)parent);
//                addValue(parent, index, ((Catalog)parent).getIndexes());
//            }
//
//            addDescription(modelResource, index, description);
//        }
//        
//        return index;
//    }
    
    public Column createColumn(
    		String name, 
    		String description, 
    		EObject viewOrBaseTable,
    		boolean useDefaultDatatype, 
    		EObject datatype,
    		int length ) {
    	
        Column column = factory.createColumn();
        if( column != null ) {
        	column.setName(name);
            
        	// SET DATATYPE AND LENGTH
            if( datatype != null)
            	column.setType(datatype);
            else {
                if( useDefaultDatatype && defaultDatatype != null ) {
                	column.setType(defaultDatatype);
                }
            }
            if( length > 0 )
            	column.setLength(length);
            else if( useDefaultDatatype ) {
            	column.setLength(defaultLength);
            }
            
            // SET PARENT
            if( viewOrBaseTable instanceof BaseTable ) {
            	addValue(viewOrBaseTable, column, ((BaseTable)viewOrBaseTable).getColumns());
            } else if( viewOrBaseTable instanceof View ) {
            	addValue(viewOrBaseTable, column, ((View)viewOrBaseTable).getColumns());
            }
            
            addDescription(column, description);
        }
        return column;
    }
    
    public  ProcedureParameter createParameter(
    		String name, 
    		String description, 
    		boolean isOutput,
    		Procedure procedure,
    		boolean useDefaultDatatype, 
    		EObject datatype,
    		int length ) {
    	
        ProcedureParameter param = factory.createProcedureParameter();
        if( param != null ) {
            param.setName(name);
            
            // SET DATATYPE AND LENGTH
            if( datatype != null)
            	param.setType(datatype);
            else {
                if( useDefaultDatatype && defaultDatatype != null ) {
                	param.setType(defaultDatatype);
                }
            }
            if( length > 0 )
            	param.setLength(length);
            else if( useDefaultDatatype ) {
            	param.setLength(defaultLength);
            }
            if( isOutput ) {
            	param.setDirection(DirectionKind.RETURN_LITERAL);
            } else {
            	param.setDirection(DirectionKind.IN_LITERAL);
            }
            
            // SET PARENT
            addValue(procedure, param, procedure.getParameters());

            addDescription(param, description);
        }
        
        return param;
    }
    
//    public static EObject createBaseTable(final String name, final boolean supportsUpdate) {
//    	BaseTable bt = factory.createBaseTable();
//    	bt.setName(name);
//    	bt.setSupportsUpdate(supportsUpdate);
//    	return bt;
//    }
    
    private void addDescription(EObject eObject, String description) {
        if( this.modelResource != null && description != null && description.length() > 0 ) {
            final ModelContents contents = ModelerCore.getModelEditor().getModelContents(modelResource);
            Annotation newAnnot = ModelResourceContainerFactory.createNewAnnotation(eObject, contents.getAnnotationContainer(true));
            newAnnot.setDescription(description);
        }
    }
    
    public EList getModelResourceContents(ModelResource resource ) {
    	EList eList = null;
    	
    	try {
			eList = resource.getEmfResource().getContents();
		} catch (ModelWorkspaceException e) {
			 UiConstants.Util.log(IStatus.ERROR, e, getString(GET_MODEL_CONTENTS_ERROR, resource));
		}
		
		return eList;
    }
    
    public void addValue(final Object owner, final Object value, EList feature) {
        try {
            if( isTransactionable ) {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
            } else {
                feature.add(value);
            }
        } catch (ModelerCoreException err) {
            UiConstants.Util.log(IStatus.ERROR, err, getString(ADD_VALUE_ERROR, value, owner));
        }
    }
    
}
