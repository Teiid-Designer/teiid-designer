/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.modelgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.resource.xmi.MtkXmiResourceImpl;
import com.metamatrix.vdb.edit.VdbEditPlugin;


/** 
 * @since 4.1
 */
public class MaterializedViewModelGenerator {
    private static String XMI = "xmi"; //$NON-NLS-1$
    private static String MV_PREFIX = SqlTableAspect.MATERIALIZED_VIEW_PREFIX;
    private static int STARTING_INDEX = 1000000;
    private static String STAGING_SUFFIX = SqlTableAspect.STAGING_TABLE_SUFFIX;
    private static String RESULT_MSG = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.resultStr"); //$NON-NLS-1$
    private static String OK_MSG = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.success"); //$NON-NLS-1$
    private static String PHYS_SUFFIX = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.physSuffix"); //$NON-NLS-1$
    
    private final ModelEditor modelEditor;
    private final ResourceSet tempRsrcSet = new ResourceSetImpl();
    private final HashMap virtToPhysMappings;
    private final StringNameValidator nameValidator = new StringNameValidator();
    
    private Resource materializedViewModel;
    private boolean isInitialized;
    private int nextIndex;
    private MultiStatus result;
    private Set previousModelNames;
    private Set usedNIS;
    
    public MaterializedViewModelGenerator() {
        this.modelEditor = ModelerCore.getModelEditor();
        this.virtToPhysMappings = new HashMap();
        this.previousModelNames = new HashSet();
        this.usedNIS = new HashSet();
        isInitialized = false;
        nextIndex = STARTING_INDEX + 1;
    }
    
    /**
     * This method will iterate over the contents of the input model and create a physical table for any
     * tables that exist in the input resource that represent a materializable table.  
     * @param inputResource - May not be null and should be an instance of Virtual Relational Model 
     *  - this contraint is not enforced, but no results are created for other model types.
     * @param reuseExistingMVModel will use existing materialized view model if true.  If false, materialized view model
     *         will be cleared for each execution.
     * @param mvModelName - optional name to use for creation of materialized view model
     * @param mvModelFolderUri the folder where the Materialization model will be created
     * @return IStatus detailing the results of the execute.
     * 
     * @since 4.1
     */
    public MultiStatus execute(final Resource inputResource,
                               final boolean reuseExistingMVModel,
                               final String mvModelName,
                               final URI mvModelFolderUri) {
        //If we are not reusing existing result models, set them to null now and set isInitialized to false.
        if(!reuseExistingMVModel) {
            materializedViewModel = null;
            virtToPhysMappings.clear();
            previousModelNames.clear();
            isInitialized = false;
        }
        
        //Input resource may not be null
        if(inputResource == null) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.nullInputRsrc"); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
            return result;
        }
        
        // Retrieve the Model name
        final String inputModelName = modelEditor.getModelName(inputResource);
        
        //Retrieve the ModelAnnotation for the given resource
        final ModelAnnotation ma = getModelAnnotation(inputResource);
        if(ma == null) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.nullModelAnnotation"); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
            return result;
        }
        
        //If the input resource is not a virtual Relational model
        final int modelType = ma.getModelType().getValue();
        final String primaryMetamodel = ma.getPrimaryMetamodelUri();
        if(modelType !=  ModelType.VIRTUAL || !RelationalPackage.eNS_URI.equals(primaryMetamodel) ) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.wrongModelType"); //$NON-NLS-1$
            addStatus(IStatus.OK, msg, null);
            return result;
        }
        
        final Iterator allContents = inputResource.getAllContents();
        final Collection existingTableNames = new ArrayList();
        while(allContents.hasNext() ) {
            final Object next = allContents.next();
            if(next instanceof Table && ((Table)next).isMaterialized() ) {
                if(!isInitialized) {
                    URI uri = (mvModelFolderUri == null) ? inputResource.getURI() : mvModelFolderUri;
                    initialize(uri, inputModelName, mvModelName);
                }
                
                try {
                    final Schema schema = createSchema( inputModelName );
                    createMaterializedView( (Table)next, schema, existingTableNames);
                } catch (Exception err) {
                    final String fullName = modelEditor.getModelRelativePathIncludingModel( (EObject)next).toString();
                    final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.exceptionCreatingMV", fullName); //$NON-NLS-1$
                    addStatus(IStatus.ERROR, msg, err);
                }
            }
        }     
                
        if(result == null) {
            addStatus(IStatus.OK, OK_MSG, null);
        }
        
        return result;
    }
    
    private ModelAnnotation getModelAnnotation(final Resource rsrc) {
        if(rsrc instanceof MtkXmiResourceImpl) {
            return ((MtkXmiResourceImpl)rsrc).getModelAnnotation();
        }
        
        final Iterator contents = rsrc.getAllContents();
        while(contents.hasNext() ) {
            final Object next = contents.next();
            if(next instanceof ModelAnnotation) {
                return (ModelAnnotation)next;
            }
        }
        
        return null;        
    }
    
    private Schema createSchema( final String inputModelName ) {
        if(!isInitialized) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.uninitializedError"); //$NON-NLS-1$
            throw new MetaMatrixRuntimeException(msg);
        }
        
        // Check if we already created a schema with the name of the input model
        final List roots = this.materializedViewModel.getContents();
        for (final Iterator iter = roots.iterator(); iter.hasNext();) {
            final EObject eObject = (EObject)iter.next();
            if (eObject instanceof Schema) {
                final Schema existingSchema = (Schema)eObject;
                if (existingSchema.getName().equals(inputModelName)) {
                    return existingSchema;
                }
            }
        }
        
        // Create a new schema with the name of the input model
        final Schema newSchema = RelationalFactory.eINSTANCE.createSchema();
        newSchema.setName(inputModelName);
        newSchema.setNameInSource(inputModelName);

        // Add the schema to the materialized view model
        this.materializedViewModel.getContents().add(newSchema);
       
        return newSchema;
    }
    
    /**
     * This method will iterate over the contents of the input model and create a physical table for any
     * tables that exist in the input resource that represent a materializable table.  It will not reuse
     * existing result models for subsequent calls to execute so the result models should be captured 
     * between calls.
     * @param inputResource - May not be null and should be an instance of Virtual Relational Model 
     *  - this contraint is not enforced, but no results are created for other model types.
     * @return IStatus detailing the results of the execute.
     * @since 4.1
     */
    protected MultiStatus execute(final Resource inputResource) {
        return execute(inputResource, false, null, null);        
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
    public Resource getMaterializedViewModel() {
        return this.materializedViewModel;
    }
    
    private void createMaterializedView(Table table, final Schema owner, final Collection existingNames) {
        if(!isInitialized) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.uninitializedError"); //$NON-NLS-1$
            throw new MetaMatrixRuntimeException(msg);
        }
        
        //create the new table and the staging table and add them to the result physical model
        final Table mv = (Table)RelationalFactory.eINSTANCE.create(table.eClass() );
        final Table st = (Table)RelationalFactory.eINSTANCE.create(table.eClass() );
        
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
        st.setName(name + STAGING_SUFFIX);
        mv.setName(name);
        
        //Copy table values
        copyTableValues(table, mv, false);
        copyTableValues(table, st, true);
        
        // Increment only after both mat view cache and staging tables
        // have been named.
        ++nextIndex;

        owner.getTables().add(mv);
        owner.getTables().add(st);
        
        final Collection newTables = new ArrayList();
        newTables.add(st);
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
            st.getColumns().add(stageCol);
            copyColValues(nextCol, newCol);
            copyColValues(nextCol, stageCol);   
            indexes.addAll(nextCol.getIndexes() );
            pks.addAll(nextCol.getUniqueKeys() );
        }
        
        final Iterator pksIt = pks.iterator();
        while(pksIt.hasNext() ) {
            final UniqueKey nextKey = (UniqueKey)pksIt.next();  
            final UniqueKey newKey = (UniqueKey)RelationalFactory.eINSTANCE.create(nextKey.eClass() );
            final UniqueKey stageKey = (UniqueKey)RelationalFactory.eINSTANCE.create(nextKey.eClass() );
            if(nextKey instanceof PrimaryKey) {
                ((BaseTable)mv).setPrimaryKey( (PrimaryKey)newKey);
                ((BaseTable)st).setPrimaryKey( (PrimaryKey)stageKey);
            }else {
                ((BaseTable)mv).getUniqueConstraints().add( newKey);
                ((BaseTable)st).getUniqueConstraints().add( stageKey);
                
            }
            
            copyUniqueKeyValues(nextKey, newKey, false);
            copyUniqueKeyValues(nextKey, stageKey, true);
            
        }
        
        final Iterator indexIt = indexes.iterator();
        while(indexIt.hasNext() ) {
            final Index nextIndex = (Index)indexIt.next();
            final Index newIndex = RelationalFactory.eINSTANCE.createIndex();
            final Index stageIndex = RelationalFactory.eINSTANCE.createIndex();
            copyIndexValues(nextIndex, stageIndex, true, table, st);
            copyIndexValues(nextIndex, newIndex, false, table, mv);
            this.materializedViewModel.getContents().add(newIndex);
            this.materializedViewModel.getContents().add(stageIndex);            
        }
        
    }
    
    protected void copyTableValues(final Table orig, final Table copy, final boolean isStage) {
        copy.setNameInSource( getUniqueNIS(isStage, orig) );
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
            result = new MultiStatus(VdbEditPlugin.PLUGIN_ID, -1, RESULT_MSG, null);
        }
        
        result.add(new Status(severity, VdbEditPlugin.PLUGIN_ID, -1, msg, e) );
    }
    
    private void copyUniqueKeyValues(final UniqueKey orig, 
                                     final UniqueKey copy,
                                     final boolean isStage) {
        if (isStage) {
            copy.setName(orig.getName() + STAGING_SUFFIX);
        } else {
            copy.setName(orig.getName());
        }
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
                    final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
                    throw new MetaMatrixRuntimeException(msg);
                }
            }
        } catch (Exception err) {
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
            throw new MetaMatrixRuntimeException(msg);
        }
    }
    
    private void copyIndexValues(final Index orig, final Index copy, final boolean isStage, final Table origTable, final Table copyTable) {
        copy.setAutoUpdate(orig.isAutoUpdate() );
        copy.setFilterCondition(orig.getFilterCondition() );
        copy.setName(orig.getName());

        String nameInSource = orig.getNameInSource();
        copy.setNameInSource(((nameInSource == null) || (nameInSource.length() == 0)) ? orig.getName() : nameInSource);

        if (isStage) {
            copy.setName(copy.getName() + STAGING_SUFFIX);
            copy.setNameInSource(copy.getNameInSource() + STAGING_SUFFIX);
        }
        
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
    
    private void initialize(final URI mvModelUri, final String inputModelName, String mvModelName) {
        if(mvModelName == null) {
            mvModelName = inputModelName + PHYS_SUFFIX;
        }
        
        this.materializedViewModel = createResource(mvModelUri, mvModelName);        
        this.isInitialized = this.materializedViewModel != null;        
    }
    
    private Resource createResource(final URI mvModelFolderUri, final String name) {
        try {
            ArgCheck.isNotNull(mvModelFolderUri);
            ArgCheck.isNotNull(name);
            
            URI uri = mvModelFolderUri.appendSegment(name).appendFileExtension(XMI);
            
            final Resource rsrc = new MtkXmiResourceImpl(uri);
            tempRsrcSet.getResources().add(rsrc);
            
            final ModelContents newContents = new ModelContents(rsrc);
            final ModelAnnotation ma = newContents.getModelAnnotation();
            ma.setPrimaryMetamodelUri(RelationalPackage.eNS_URI);            
            ma.setModelType(ModelType.MATERIALIZATION_LITERAL);
            final String descr = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.physDescr"); //$NON-NLS-1$
            ma.setDescription(descr);
            
            return rsrc;
        } catch (Exception err) {
            //just return null and log a warning.
            Object[] params = new Object[] {mvModelFolderUri, name};
            final String msg = VdbEditPlugin.Util.getString("MaterializedViewModelGenerator.unexpectedException1", params); //$NON-NLS-1$
            addStatus(IStatus.WARNING, msg, err);
            return null;
        }
    }
    
    private String getUniqueNIS(final boolean isST, final Table orig) {
    	if (orig.getNameInSource() != null
				&& !orig.getNameInSource().trim().equals(
						StringUtil.Constants.EMPTY_STRING)) {
			final StringBuffer buffer = new StringBuffer(orig.getNameInSource());
			
			if (isST) {
				buffer.append(STAGING_SUFFIX);
			}
			
			//Check to see if we have used this NIS. If so, append an index.
			if (usedNIS.contains(buffer.toString())){
				buffer.append(nextIndex);
			}
			usedNIS.add(buffer.toString());
			return buffer.toString();
		}
    	
    	

        final StringBuffer buffer = new StringBuffer(MV_PREFIX);
        buffer.append(nextIndex);
        
        if(isST) {
            buffer.append(STAGING_SUFFIX);
        }
        return buffer.toString();
    }
}
