/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.materialization;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.builder.ModelBuildUtil;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelFileUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.UniqueKey;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants;

/**
 * This class is used for managing the creation of the JDG-specific POJO and related content including the
 * materialized source model and table
 * 
 * @author blafond
 *
 */

public class MaterializedModelManager implements ReverseEngConstants {
    private static String RESULT_MSG = TransformationPlugin.Util.getString("MaterializedModelManager.resultStr"); //$NON-NLS-1$
    private static String OK_MSG = TransformationPlugin.Util.getString("MaterializedModelManager.success"); //$NON-NLS-1$
    
    private static IStatus OK_STATUS = new Status(IStatus.OK, TransformationPlugin.PLUGIN_ID,
    		TransformationPlugin.Util.getString("MaterializedModelManager.allInputsOkStatusMessage"));  //$NON-NLS-1$

    private final StringNameValidator nameValidator = new StringNameValidator();


	private ModelResource materializedViewModel;
    private IContainer targetLocation;
    private Table selectedView;
    private String materializedSourceModelName;
    private String sourceTableName;
    private String pojoClassName;
    private String pojoPackageName;
    private String annotationType = PROTOBUF;
    
    private File pojoFileSystemFolder;
    private IContainer pojoWorkspaceFolder;
    
    private String moduleZipFileName;

    private MultiStatus result;
    
    public MaterializedModelManager(EObject selectedView) {
        this.selectedView = (Table)selectedView;
        
        this.sourceTableName = ModelerCore.getModelEditor().getName(this.selectedView);
        this.pojoClassName = this.sourceTableName;
        this.pojoPackageName = DEFAULT_PACKAGE_NAME;
        this.materializedSourceModelName = this.sourceTableName;
        
        try {
            this.materializedViewModel = ModelUtil.getModel(this.selectedView);
			this.targetLocation = materializedViewModel.getCorrespondingResource().getParent();
			this.pojoWorkspaceFolder = this.targetLocation;
			this.moduleZipFileName = this.sourceTableName + "JDGModule";
		} catch (ModelWorkspaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public MultiStatus execute() {
      
        try {  

        	ModelResource newSourceModel = constructRelationalModel();
            
	        selectedView.setMaterialized(true);

            Table matTable = createMaterializedSourceTable( selectedView);
            
            selectedView.setMaterializedTable(matTable);
            
            // Add the schema to the materialized view model
            addValue(newSourceModel, matTable, getModelResourceContents(newSourceModel));
            
            ModelBuildUtil.rebuildImports(materializedViewModel.getEmfResource(), true);
            
            newSourceModel.save(new NullProgressMonitor(), false);
            
        } catch (Exception err) {
            TransformationPlugin.Util.log(err);
        }  
        
        if(result == null) {
            addStatus(IStatus.OK, OK_MSG, null);
        }
        
        return result;
    }
    
    /** 
     * @return Returns the resultPhysicalModel.
     * @since 4.1
     */
    public ModelResource getMaterializedViewModel() {
        return this.materializedViewModel;
    }
    
    private Table createMaterializedSourceTable(Table table) {
        
        //create the new table and the staging table and add them to the result physical model
        final Table newMatTable = (Table)RelationalFactory.eINSTANCE.create(table.eClass() );
        
        //Create a uniqueName
        String name = table.getName();
        String tmp = nameValidator.createValidUniqueName(name);
        if(tmp != null) {
            name = tmp;
        }
        
        //Set table names
        newMatTable.setName(name);
        
        //Copy table values
        copyTableValues(table, newMatTable);
       
        //Iterate over the children of the Materialized View and create corresponding columns, primary keys, and indexes in both
        //the new table and the stage table.
        final Iterator cols = table.getColumns().iterator();
        final HashSet pks = new HashSet();
        while(cols.hasNext() ) {
            final Column nextCol = (Column)cols.next();
            final Column newCol = RelationalFactory.eINSTANCE.createColumn();
            newMatTable.getColumns().add(newCol);
            copyColValues(nextCol, newCol);
            pks.addAll(nextCol.getUniqueKeys() );
        }
        
        final Iterator pksIt = pks.iterator();
        while(pksIt.hasNext() ) {
            final UniqueKey nextKey = (UniqueKey)pksIt.next();  
            final UniqueKey newKey = (UniqueKey)RelationalFactory.eINSTANCE.create(nextKey.eClass() );
            if(nextKey instanceof PrimaryKey) {
                ((BaseTable)newMatTable).setPrimaryKey( (PrimaryKey)newKey);
            }else {
                ((BaseTable)newMatTable).getUniqueConstraints().add((UniqueConstraint) newKey);
            }
            
            copyUniqueKeyValues(nextKey, newKey);
        }
        
        return newMatTable;
    }
    
    private void addValue(final Object owner, final Object value, EList feature) {
        try {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
        } catch (ModelerCoreException err) {
        	TransformationPlugin.Util.log(err);
        }
    }
    
    protected void copyTableValues(final Table orig, final Table copy) {
        copy.setNameInSource( orig.getNameInSource() );
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
                    final String msg = TransformationPlugin.Util.getString("MaterializedModelManager.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
                    throw new ModelerCoreRuntimeException(msg);
                }
            }
        } catch (Exception err) {
            final String msg = TransformationPlugin.Util.getString("MaterializedModelManager.errorSettingUCvals", copy.getName()); //$NON-NLS-1$
            throw new ModelerCoreRuntimeException(msg);
        }
    }

    public Table getVirtualTable() {
    	return this.selectedView;
    }
    
    public IContainer getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(IContainer projectOrFolder) {
		this.targetLocation = projectOrFolder;
	}    
	
	public EList getModelResourceContents(ModelResource mr) {
    	EList eList = null;
    	
    	try {
			eList = mr.getEmfResource().getContents();
		} catch (ModelWorkspaceException err) {
			TransformationPlugin.Util.log(err);
		}
		
		return eList;
    }

	public void setMaterializedViewModel(ModelResource materializedViewModel) {
		this.materializedViewModel = materializedViewModel;
	}
	
	public String getMaterializedSourceModelName() {
		return materializedSourceModelName;
	}
	
	public void setMaterializedSourceModelName(String materializedSourceModelName) {
		this.materializedSourceModelName = materializedSourceModelName;
	}
	
	public IContainer getLocationName() {
		return targetLocation;
	}
	
	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getPojoClassName() {
		return pojoClassName;
	}

	public void setPojoClassName(String pojoClassName) {
		this.pojoClassName = pojoClassName;
	}

	public String getPojoPackageName() {
		return pojoPackageName;
	}

	public void setPojoPackageName(String pojoPackageName) {
		this.pojoPackageName = pojoPackageName;
	}

	public String getAnnotationType() {
		return annotationType;
	}

	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	}

	public File getPojoFileSystemFolder() {
		return pojoFileSystemFolder;
	}

	public void setPojoFileSystemFolder(File pojoFileSystemFolder) {
		this.pojoFileSystemFolder = pojoFileSystemFolder;
	}

	public IContainer getPojoWorkspaceFolder() {
		return pojoWorkspaceFolder;
	}

	public void setPojoWorkspaceFolder(IContainer pojoWorkspaceFolder) {
		this.pojoWorkspaceFolder = pojoWorkspaceFolder;
	}
	
	public String getModuleZipFileName() {
		return moduleZipFileName;
	}

	public void setModuleZipFileName(String moduleZipFileName) {
		this.moduleZipFileName = moduleZipFileName;
	}
	
	public IStatus validate(int pageNumber) {
		if( pageNumber == 1 ) {
			if( this.selectedView == null  ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_noVirtualTablesSelectedError")); //$NON-NLS-1$
			}
			
			if( this.targetLocation == null ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_targetLocationIsNullError")); //$NON-NLS-1$
			}
			
			if( StringUtilities.isEmpty(materializedSourceModelName) ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_sourceModelNameIsUndefined")); //$NON-NLS-1$
			}
            
	        if( this.selectedView.isMaterialized() ) {
	        	return new Status(IStatus.WARNING, TransformationPlugin.PLUGIN_ID, 
	        			TransformationPlugin.Util.getString("MaterializedModelManager_viewAlreadyMaterializedError", selectedView.getName())); //$NON-NLS-1$
	        }
		} else if( pageNumber == 2 ) {
			if( StringUtilities.isEmpty(pojoPackageName) ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_packageNameUndefined")); //$NON-NLS-1$
			}
			
			if( StringUtilities.isEmpty(pojoClassName) ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_classNameUndefined")); //$NON-NLS-1$
			}
			
			if( StringUtilities.isEmpty(moduleZipFileName) ) {
				return new Status(IStatus.ERROR, TransformationPlugin.PLUGIN_ID, 
						TransformationPlugin.Util.getString("MaterializedModelManager_moduleZipFileNameUndefined")); //$NON-NLS-1$
			}
		}
		
		return OK_STATUS;

	}
	
    /**
     * Create a Relational Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    private ModelResource constructRelationalModel() {

        String modelNameWithExt = materializedSourceModelName.trim();

        if (!modelNameWithExt.endsWith(ModelFileUtil.DOT_XMI)) {
        	modelNameWithExt += ModelFileUtil.DOT_XMI;
        }

        IPath relativeModelPath = targetLocation.getProjectRelativePath().append(modelNameWithExt);
        final IFile modelFile = targetLocation.getProject().getFile(relativeModelPath);
        final ModelResource resrc = ModelerCore.create(modelFile);
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri(RelationalPackage.eNS_URI);
            resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
        } catch (ModelWorkspaceException mwe) {
            mwe.printStackTrace();
        }
        return resrc;
    }
}
