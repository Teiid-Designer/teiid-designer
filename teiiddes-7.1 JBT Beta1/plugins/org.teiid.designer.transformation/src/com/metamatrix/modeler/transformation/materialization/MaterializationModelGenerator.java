/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.materialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.transformation.TransformationPlugin;

public class MaterializationModelGenerator {
    private static String MV_PREFIX = SqlTableAspect.MATERIALIZED_VIEW_PREFIX;
    private static int STARTING_INDEX = 1000000;
    private static String RESULT_MSG = TransformationPlugin.Util.getString("MVModelGenerator.resultStr"); //$NON-NLS-1$
    private static String OK_MSG = TransformationPlugin.Util.getString("MVModelGenerator.success"); //$NON-NLS-1$
    private static String UNDEFINED = TransformationPlugin.Util.getString("MVModelGenerator.undefined"); //$NON-NLS-1$
    
    private static IStatus OK_STATUS = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID,
    		TransformationPlugin.Util.getString("MVModelGenerator.allInputsOkStatusMessage"));  //$NON-NLS-1$
    
    private final ModelEditor modelEditor;
    private final HashMap virtToPhysMappings;
    private final StringNameValidator nameValidator = new StringNameValidator();


	private ModelResource materializedViewModel;
    private Object targetLocation;
    private Collection<EObject> virtualTables;
    
    private int nextIndex;
    private MultiStatus result;
    private Set usedNIS;
    
    public MaterializationModelGenerator() {
        this.modelEditor = ModelerCore.getModelEditor();
        this.virtToPhysMappings = new HashMap();
        this.usedNIS = new HashSet();
        nextIndex = STARTING_INDEX + 1;
    }
    
    public MultiStatus execute() {
    	return execute(this.materializedViewModel, this.targetLocation, this.virtualTables);
    }
    

    public MultiStatus execute(final ModelResource targetResource,
    						   final Object targetLocation,
    						   final Collection<EObject> virtualTables) {
    	CoreArgCheck.isNotNull(targetResource, "targetResource"); //$NON-NLS-1$
    	
    	this.materializedViewModel = targetResource;
        
//    	System.out.println("MVModelGenerator.execute()  target resource = " + targetResource.getItemName());
    	
    	// just a set of names to insure no two views are named the same
        final Collection existingTableNames = new ArrayList();
        
        try {  
//            System.out.println("MVModelGenerator.execute()  create schema for tables = " + targetResource.getItemName());
            
            Schema schema = null;
            
            if( targetLocation != null && targetLocation instanceof Schema) {
            	schema = (Schema)targetLocation;
            } else {
            	schema = createSchema( targetResource.getPath().removeFileExtension().lastSegment() );
            }
            
	        for( EObject next : virtualTables ) {
	            if(next instanceof Table ) {
	            	((Table)next).setMaterialized(true);

                	EObject mvTable = createMaterializedView( (Table)next, schema, existingTableNames);
                	
                	((Table)next).setMaterializedTable((Table)mvTable);
	            }
	        }     
        } catch (Exception err) {
            TransformationPlugin.Util.log(err);
        }  
        
        if(result == null) {
            addStatus(IStatus.OK, OK_MSG, null);
        }
        
        return result;
    }
    
    private Schema createSchema( final String inputModelName ) {
    	
    	try {
			for( Object eObj : materializedViewModel.getEObjects() ) {
				if( eObj instanceof Schema && inputModelName.equalsIgnoreCase(ModelerCore.getModelEditor().getName((EObject)eObj)) ) {
					return (Schema)eObj;
				}
			}
		} catch (ModelWorkspaceException err) {
			TransformationPlugin.Util.log(err);
		}
        
        // Create a new schema with the name of the input model
        final Schema newSchema = RelationalFactory.eINSTANCE.createSchema();
        newSchema.setName(inputModelName);
        newSchema.setNameInSource(inputModelName);

        // Add the schema to the materialized view model
        addValue(this.materializedViewModel, newSchema, getModelResourceContents());
        
        return newSchema;
    }
    
    /** 
     * @return Returns the virtToPhysFullNames.
     * @since 4.1
     */
    public HashMap getVirtToPhysMappings() {
        return this.virtToPhysMappings;
    }
    
    /** 
     * @return Returns the resultPhysicalModel.
     * @since 4.1
     */
    public ModelResource getMaterializedViewModel() {
        return this.materializedViewModel;
    }
    
    private EObject createMaterializedView(Table table, final Schema owner, final Collection existingNames) {
        
        //create the new table and the staging table and add them to the result physical model
        final Table mv = (Table)RelationalFactory.eINSTANCE.create(table.eClass() );
        
        //Create a uniqueName
        String name = table.getName();
        String tmp = nameValidator.createValidUniqueName(name, existingNames);
        if(tmp == null) {
            existingNames.add(name);
        }else {
            name = tmp;
            existingNames.add(name);
        }
        
        //Set table names
        mv.setName(name);
        
        //Copy table values
        copyTableValues(table, mv);
        
        // Increment only after both mat view cache and staging tables
        // have been named.
        ++nextIndex;

        owner.getTables().add(mv);
        
        final Collection newTables = new ArrayList();
        newTables.add(mv);
        this.virtToPhysMappings.put(table, newTables);
        
        
        //Iterate over the children of the Materialized View and create corresponding columns, primary keys, and indexes in both
        //the new table and the stage table.
        final Iterator cols = table.getColumns().iterator();
        final HashSet indexes = new HashSet();
        final HashSet pks = new HashSet();
        while(cols.hasNext() ) {
            final Column nextCol = (Column)cols.next();
            final Column newCol = RelationalFactory.eINSTANCE.createColumn();
            final Column stageCol = RelationalFactory.eINSTANCE.createColumn();
            mv.getColumns().add(newCol);
            copyColValues(nextCol, newCol);
            copyColValues(nextCol, stageCol);   
            indexes.addAll(nextCol.getIndexes() );
            pks.addAll(nextCol.getUniqueKeys() );
        }
        
        final Iterator pksIt = pks.iterator();
        while(pksIt.hasNext() ) {
            final UniqueKey nextKey = (UniqueKey)pksIt.next();  
            final UniqueKey newKey = (UniqueKey)RelationalFactory.eINSTANCE.create(nextKey.eClass() );
            if(nextKey instanceof PrimaryKey) {
                ((BaseTable)mv).setPrimaryKey( (PrimaryKey)newKey);
            }else {
                ((BaseTable)mv).getUniqueConstraints().add( newKey);
            }
            
            copyUniqueKeyValues(nextKey, newKey);
        }
        
        final Iterator indexIt = indexes.iterator();
        while(indexIt.hasNext() ) {
            final Index nextIndex = (Index)indexIt.next();
            final Index newIndex = RelationalFactory.eINSTANCE.createIndex();
            copyIndexValues(nextIndex, newIndex, false, table, mv);
            addValue(this.materializedViewModel, newIndex, getModelResourceContents());
        }
        
        return mv;
    }
    
    protected void copyTableValues(final Table orig, final Table copy) {
        copy.setNameInSource( getUniqueNIS(orig) );
        copy.setCardinality(orig.getCardinality() );
        copy.setSystem(orig.isSystem() );
        // Defect 16941 - the supportsUpdate flag should always be true for a materialization table
        //copy.setSupportsUpdate(orig.isSupportsUpdate() );
        copy.setSupportsUpdate(true);
    }
    
    private void copyColValues(final Column orig, final Column copy) {
        copy.setAutoIncremented(orig.isAutoIncremented() );
        copy.setCaseSensitive(orig.isCaseSensitive() );
        copy.setCharacterSetName(orig.getCharacterSetName() );
        copy.setCollationName(orig.getCollationName() );
        copy.setCurrency(copy.isCurrency() );
        copy.setDefaultValue(orig.getDefaultValue() );
        copy.setFixedLength(orig.isFixedLength() );
        copy.setFormat(orig.getFormat() );
        copy.setLength(orig.getLength() );
        copy.setMaximumValue(orig.getMaximumValue() );
        copy.setMinimumValue(orig.getMinimumValue() );
        copy.setName(orig.getName() );
        copy.setNameInSource(orig.getNameInSource() );
        copy.setNativeType(orig.getNativeType() );
        copy.setNullable(orig.getNullable() );
        copy.setPrecision(orig.getPrecision() );
        copy.setRadix(orig.getRadix() );
        copy.setScale(orig.getScale() );
        copy.setSearchability(orig.getSearchability() );
        copy.setSelectable(orig.isSelectable() );
        copy.setSigned(orig.isSigned() );
        copy.setType(orig.getType() );
        // Defect 16941 - the updateable flag should always be true for the columns of a materialization table
        //copy.setUpdateable(orig.isUpdateable() );
        copy.setUpdateable(true);
    }
    
    private void addStatus(final int severity, final String msg, final Exception e) {
        if(result == null) {
            result = new MultiStatus(TransformationPlugin.PLUGIN_ID, -1, RESULT_MSG, null);
        }
        
        result.add(new Status(severity, TransformationPlugin.PLUGIN_ID, -1, msg, e) );
    }
    
    private void copyUniqueKeyValues(final UniqueKey orig, 
                                     final UniqueKey copy) {

        copy.setName(orig.getName());
        
        copy.setNameInSource(orig.getNameInSource());
        
        try {
            final BaseTable copyOwner = copy.getTable();
            final BaseTable origOwner = orig.getTable();
            final Iterator cols = orig.getColumns().iterator();
            while(cols.hasNext() ) {
                final Column nextOrigCol = (Column)cols.next();
                final int index = origOwner.getColumns().indexOf(nextOrigCol);
                final Column copyCol = (Column)copyOwner.getColumns().get(index);
                if(nextOrigCol.getName().equals(copyCol.getName() ) ) {
                    copy.getColumns().add(copyCol);
                }else {
                    final String msg = TransformationPlugin.Util.getString("MVModelGenerator.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
                    throw new ModelerCoreRuntimeException(msg);
                }
            }
        } catch (Exception err) {
            final String msg = TransformationPlugin.Util.getString("MVModelGenerator.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
            throw new ModelerCoreRuntimeException(msg);
        }
    }
    
    private void copyIndexValues(final Index orig, final Index copy, final boolean isStage, final Table origTable, final Table copyTable) {
        copy.setAutoUpdate(orig.isAutoUpdate() );
        copy.setFilterCondition(orig.getFilterCondition() );
        copy.setName(orig.getName());

        String nameInSource = orig.getNameInSource();
        copy.setNameInSource(((nameInSource == null) || (nameInSource.length() == 0)) ? orig.getName() : nameInSource);
        
        copy.setNullable(orig.isNullable() );
        copy.setUnique(orig.isUnique() );

        // table columns
        List origTableCols = origTable.getColumns();
        List copyTableCols = copyTable.getColumns();
        
        // index columns
        List origIndexCols = orig.getColumns();
        List copyIndexCols = copy.getColumns(); // empty to start
        
        // loop through original index columns. find the appropriate copy column and add to copy index.
        // assume index of original table column is the same index for the same column in the copy table.
        for (int size = origIndexCols.size(), i = 0; i < size; ++i) {
            Column origCol = (Column)origIndexCols.get(i);
            int index = origTableCols.indexOf(origCol);
            
            if (index != -1) {
                copyIndexCols.add(copyTableCols.get(index));
            }
        }
    }

    private String getUniqueNIS(final Table orig) {
    	if (orig.getNameInSource() != null
				&& !orig.getNameInSource().trim().equals(
						StringUtilities.EMPTY_STRING)) {
			final StringBuffer buffer = new StringBuffer(orig.getNameInSource());
			
			//Check to see if we have used this NIS. If so, append an index.
			if (usedNIS.contains(buffer.toString())){
				buffer.append(nextIndex);
			}
			usedNIS.add(buffer.toString());
			return buffer.toString();
		}
    	
    	

        final StringBuffer buffer = new StringBuffer(MV_PREFIX);
        buffer.append(nextIndex);
        
        return buffer.toString();
    }
    
    public EList getModelResourceContents() {
    	EList eList = null;
    	
    	try {
			eList = this.materializedViewModel.getEmfResource().getContents();
		} catch (ModelWorkspaceException err) {
			TransformationPlugin.Util.log(err);
		}
		
		return eList;
    }
    
    public void addValue(final Object owner, final Object value, EList feature) {
        try {
                this.modelEditor.addValue(owner, value, feature);
        } catch (ModelerCoreException err) {
        	TransformationPlugin.Util.log(err);
        }
    }
    
    public void setVirtualTables(Collection<EObject> tables) {
    	this.virtualTables = new ArrayList(tables);
    }
    
    public Collection<EObject> getVirtualTables() {
    	return this.virtualTables;
    }
    
    public Object getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(Object targetLocation) {
		this.targetLocation = targetLocation;
		try {
			if( this.targetLocation instanceof Schema ) {
				this.materializedViewModel = ModelUtil.getModel(this.targetLocation);
			} else if( this.targetLocation instanceof IFile ) {
				this.materializedViewModel = ModelUtil.getModelResource((IFile)this.targetLocation, true);
				this.targetLocation = this.materializedViewModel;
	        } else if( this.targetLocation instanceof ModelResource ) {
	        	this.materializedViewModel = (ModelResource)this.targetLocation;
	        }
		} catch (ModelWorkspaceException err) {
			TransformationPlugin.Util.log(err);
		}
		
	}

	public void setMaterializedViewModel(ModelResource materializedViewModel) {
		this.materializedViewModel = materializedViewModel;
	}
	
	public String getModelName() {
		if( this.materializedViewModel != null ) {
			return this.materializedViewModel.getPath().removeFileExtension().lastSegment();
		}
		return UNDEFINED;
	}
	
	public String getLocationName() {
		if( this.targetLocation != null ) {
			if( this.targetLocation instanceof ModelResource )  {
				return getModelName();
			}
			return ModelerCore.getModelEditor().getName((Schema)this.targetLocation);
		}
		return UNDEFINED;
	}
	
	public IStatus getExecuteStatus() {
		if( this.virtualTables == null || this.virtualTables.isEmpty() ) {
			return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
					TransformationPlugin.Util.getString("MVModelGenerator.noVirtualTablesSelectedError")); //$NON-NLS-1$
		}
		for( EObject vTable : this.virtualTables ) {
            if( !(vTable instanceof Table)  ) {
            	return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
            			TransformationPlugin.Util.getString("MVModelGenerator.nonTableSelectedError",  //$NON-NLS-1$
            					ModelerCore.getModelEditor().getName(vTable)));
            }
            
            if( ((Table)vTable).isMaterialized() ) {
            	return new Status(IStatus.WARNING, TransformationPlugin.PLUGIN_ID, 
            			TransformationPlugin.Util.getString("MVModelGenerator.viewAlreadyMaterializedError", ((Table)vTable).getName())); //$NON-NLS-1$
            }
		}
		
		if( this.targetLocation == null ) {
			return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
					TransformationPlugin.Util.getString("MVModelGenerator.targetLocationIsNullError")); //$NON-NLS-1$
		}
		if( this.materializedViewModel == null ) {
			return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
					TransformationPlugin.Util.getString("MVModelGenerator.materializedViewModelIsNullError")); //$NON-NLS-1$
		}
		
		return OK_STATUS;

	}
}
