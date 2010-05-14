/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relational.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.View;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.relational.ui.textimport.ColumnRowObject;
import com.metamatrix.modeler.internal.relational.ui.textimport.IndexRowObject;
import com.metamatrix.modeler.internal.relational.ui.textimport.RelationalRowFactory;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject;
import com.metamatrix.modeler.tools.textimport.ui.wizards.IRowObject;



/** 
 * @since 4.2
 */
public class RelationalObjectProcessor extends AbstractObjectProcessor {
    //============================================================================================================================
    // Static Constants

    private static final String XMI_EXTENSION           = "xmi";//$NON-NLS-1$
    
    private static final String I18N_PREFIX             = "RelationalObjectProcessor"; //$NON-NLS-1$
    private static final String SEPARATOR               = "."; //$NON-NLS-1$
    public static final String RELATIONAL_PACKAGE_URI	= RelationalPackage.eNS_URI;
    public static final RelationalFactory factory = RelationalFactory.eINSTANCE;
    
    public static final int UNKNOWN = RelationalRowFactory.UNKNOWN;
    public static final int SCHEMA = RelationalRowFactory.SCHEMA;
    public static final int CATALOG = RelationalRowFactory.CATALOG;
    public static final int BASE_TABLE = RelationalRowFactory.BASE_TABLE;
    public static final int VIEW = RelationalRowFactory.VIEW;
    public static final int INDEX = RelationalRowFactory.INDEX;
    public static final int COLUMN = RelationalRowFactory.COLUMN;
    
    

    //============================================================================================================================
    // Static Methods
    
    private List otherModifiedResources = new ArrayList();
    private IProgressMonitor monitor;
    
    /** 
     * 
     * @since 4.2
     */
    public RelationalObjectProcessor() {
        super();
    }
    
    
    public Collection createRowObjsFromStrings(Collection rowStrings) {
        Iterator iter = rowStrings.iterator();
        String nextStr = null;
        
        RelationalRowFactory factory = new RelationalRowFactory();
        
        Collection stringRows = new ArrayList();
        IRowObject nextRow = null;
        while( iter.hasNext() ) {
            nextStr = (String)iter.next();
            nextRow = factory.createRowObject(nextStr);
            if( nextRow != null && nextRow.isValid() )
            	stringRows.add(nextRow);
            else {
            	logParsingError(nextStr);
            }
        }
        return stringRows;
    }
    
    public void generateObjsFromRowObjs(ModelResource targetResource, Object location, Collection rowObjects,
                                        boolean useStringDefaultDatatype, EObject defaultDatatype, int defaultLength) {
        
        int iRow = 0;
        Iterator iter = rowObjects.iterator();
        AbstractRowObject nextRow = null;
        EObject newObject = null;
        Object finalLocation = null;
        HashMap columnMap = new HashMap();
        EObject lastEContainer = null;
        
        String sSize = Integer.toString(rowObjects.size());
        
        while( iter.hasNext() ) {
        	iRow++;
            nextRow = (AbstractRowObject)iter.next();
            
        	finalLocation = location;
        	
        	if( monitor != null ) {
        		monitor.worked(1);
				monitor.subTask(UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + "incrementalProgress", Integer.toString(iRow), sSize, nextRow.getName())); //$NON-NLS-1$
        	}
        	
        	// if row has a location defined
        	if( nextRow.getLocation() != null ) {
        		String rowLocation = nextRow.getLocation();
        		
        		finalLocation = getOrCreateLocation(rowLocation, factory);
        		
    			if( finalLocation != null ) {
                    ModelResource actualModelResource = targetResource;
                    if(finalLocation instanceof ModelResource) {
                        actualModelResource = (ModelResource)finalLocation;
                    } else {
                        // get the seg index to the model
                        int modelSegIndex = getModelPathIndex(rowLocation);
                        IPath locPath = new Path(rowLocation);
                        actualModelResource = getModelResource(locPath.uptoSegment(modelSegIndex).toOSString());
                    }
    				if( !targetResource.equals(actualModelResource) && !otherModifiedResources.contains(actualModelResource)) {
    					otherModifiedResources.add(actualModelResource);
    				}
    			} else {
    				// If we couldn't create one, we use the selected existing one, just to be safe.
    				finalLocation = location;
    			}
        	}
        	
        	
        	switch( nextRow.getObjectType() ) {
	        	case SCHEMA: {
	                newObject = createEObject(factory, finalLocation, nextRow, null, false, null, 0);
	        	} break;
	        	
	        	case CATALOG: {
	                newObject = createEObject(factory, finalLocation, nextRow, null, false, null, 0);
	        	} break;
	        	
	        	case VIEW:
	        	case BASE_TABLE: {
	                newObject = createEObject(factory, finalLocation, nextRow, null, 
                            useStringDefaultDatatype, defaultDatatype, defaultLength);
	                // New table or view, so any indexes for a new table will not use "old" columns.
	                columnMap.clear();
	                lastEContainer = newObject;
	        	} break;
	        	case INDEX: {
	        		newObject = createEObject(factory, finalLocation, nextRow, columnMap, 
                            useStringDefaultDatatype, defaultDatatype, defaultLength);
	        		lastEContainer = newObject;
	        	} break;
	        	case COLUMN: {
	        		newObject = createEObject(factory, lastEContainer, nextRow, null, 
                            useStringDefaultDatatype, defaultDatatype, defaultLength);
	        		columnMap.put(nextRow.getName(), newObject);
	        	} break;
	        	case UNKNOWN:
	        	default: {
	        		
	        	} break;

        	}
            
            if( monitor.isCanceled() ) {
            	break;
            }
        }

    }
    
    private EObject createEObject(RelationalFactory factory, Object location, IRowObject someRow, HashMap columnMap,
                                  boolean useStringDefaultDatatype, EObject defaultDatatype, int defaultLength) {
        EObject newEObject = null;
        ModelResource modelResrc = null;
        if(location instanceof ModelResource) {
            modelResrc = (ModelResource)location;
        } else if(location instanceof EObject) {
            modelResrc =  ModelerCore.getModelEditor().findModelResource((EObject)location);
        }
        
        switch(someRow.getObjectType()) {
	        case CATALOG: {
	            Catalog catalog = (Catalog)createCatalog(someRow.getName());
		        if( catalog != null ) {
	                newEObject = catalog;
		            if( location instanceof ModelResource ) {
		            	addValue(location, catalog, getModelResourceContents((ModelResource)location));
		            }
		            if(modelResrc!=null) {
		                createAnnotation(modelResrc, catalog, someRow.getDescription());
                    }
		        }
	
	        } break;
            
	        case SCHEMA: {
	        	Catalog cat = null;
	        	if( location instanceof Catalog)
	        		cat = (Catalog)location;
	        	
	            Schema schema = (Schema)createSchema(someRow.getName(), cat);
		        if( schema != null ) {
	                newEObject = schema;
	                
		            if( location instanceof ModelResource ) {
		            	addValue(location, schema, getModelResourceContents((ModelResource)location));
		            } else if( location instanceof Catalog ) {
		            	addValue(location, schema, ((Catalog)location).getSchemas());
		            }

                    if(modelResrc!=null) {
                        createAnnotation(modelResrc, schema, someRow.getDescription());
                    }
		        }
	
	        } break;
	        
            case BASE_TABLE: {
                BaseTable bt = (BaseTable)createBaseTable(someRow.getName(), false);
    	        if( bt != null ) {
                    newEObject = bt;
                    
    	            if( location instanceof ModelResource ) {
    	            	addValue(location, bt, getModelResourceContents((ModelResource)location));
    	            } else if( location instanceof Schema ) {
    	            	addValue(location, bt, ((Schema)location).getTables());
    	            } else if( location instanceof Catalog ) {
    	            	addValue(location, bt, ((Catalog)location).getTables());
    	            }

                    if(modelResrc!=null) {
                        createAnnotation(modelResrc, bt, someRow.getDescription());
                    }
    	        }

            } break;
            
            case VIEW: {
                
            } break;
            
            case INDEX: {
                IndexRowObject row = (IndexRowObject)someRow;
                Index index = factory.createIndex();
                if( index != null ) {
                    newEObject = index;
                    index.setName(row.getName());
                    index.setUnique(row.isUnique());
                    // let's walk through the columnNames for the index
                    // and get the EObject out of the columnMap
                    if( columnMap != null && !columnMap.isEmpty() ) {
                        Iterator iter = row.getColumnNames().iterator();
                        while( iter.hasNext() ) {
                            String nextName = (String)iter.next();
                            EObject eObj = (EObject)columnMap.get(nextName);
                            if( eObj != null ) {
                            	addValue(index, eObj, index.getColumns());
                            }
                        }
                    }
                        
                    if( location instanceof ModelResource ) {
                    	addValue(location, index, getModelResourceContents((ModelResource)location));
                    } else if( location instanceof Schema ) {
                        index.setSchema((Schema)location);
                        addValue(location, index, ((Schema)location).getIndexes());
                    } else if( location instanceof Catalog ) {
                        index.setCatalog((Catalog)location);
                        addValue(location, index, ((Catalog)location).getIndexes());
                    }
                    if(modelResrc!=null) {
                        createAnnotation(modelResrc, index, row.getDescription());
                    }
                }
            } break;
            
            
            case COLUMN: {
                ColumnRowObject row = (ColumnRowObject)someRow;
                Column col = factory.createColumn();
                if( col != null ) {
                    newEObject = col;
                    col.setName(row.getName());
                    
                    if( row.getDatatype() != null)
                        col.setType(row.getDatatype());
                    else {
                        if( useStringDefaultDatatype &&
                            defaultDatatype != null ) {
                            col.setType(defaultDatatype);
                        }
                    }
                    if( row.getLength() > 0 )
                        col.setLength(row.getLength());
                    else if( useStringDefaultDatatype ) {
                        col.setLength(defaultLength);
                    }
                    
                    if( location instanceof BaseTable ) {
                    	addValue(location, col, ((BaseTable)location).getColumns());
                    } else if( location instanceof View ) {
                    	addValue(location, col, ((View)location).getColumns());
                    }
                    
                    if(modelResrc!=null) {
                        createAnnotation(modelResrc, col, row.getDescription());
                    }
                }
            } break;
            
            default:
            break;
        }
        
        return newEObject;
    }
    
    public static EObject createBaseTable(final String name, final boolean supportsUpdate) {
    	BaseTable bt = factory.createBaseTable();
    	bt.setName(name);
    	bt.setSupportsUpdate(supportsUpdate);
    	return bt;
    }
    
    public static EObject createProcedure(final String name) {
    	Procedure proc = factory.createProcedure();
    	proc.setName(name);
    	return proc;
    }

    public static EObject createSchema(final String name, Catalog catalog) {
    	Schema newSchema = factory.createSchema();
    	newSchema.setName(name);
    	if( catalog != null )
    		newSchema.setCatalog(catalog);
    	return newSchema;
    }
    
    private EObject createSchema(Object parent, String folderName, RelationalFactory factory) {
    	Schema newSchema = factory.createSchema();
    	newSchema.setName(folderName);
        
    	if( parent instanceof Catalog ) {
            addValue(parent, newSchema, ((Catalog)parent).getSchemas());
    	} else if( parent instanceof ModelResource ) {
            addValue(parent, newSchema, getModelResourceContents((ModelResource)parent));
    	}
    	
    	return newSchema;
    }
    
    public static EObject createCatalog(final String name) {
    	Catalog newCatalog = factory.createCatalog();
    	newCatalog.setName(name);
    	return newCatalog;
    }
    
    private void createAnnotation(ModelResource targetResource, EObject eObject, String description) {
        if( description != null && description.length() > 0 ) {
            final ModelContents contents = ModelerCore.getModelEditor().getModelContents(targetResource);
            Annotation newAnnot = ModelResourceContainerFactory.createNewAnnotation(eObject, contents.getAnnotationContainer(true));
            newAnnot.setDescription(description);
        }
    }

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	public static boolean isSchema(final Object obj) {
		return obj instanceof Schema;
	}
	
	public static boolean isCatalog(final Object obj) {
		return obj instanceof Catalog;
	}
	
	public static EList getTablesEList(final Object obj) {
		if( isSchema(obj) ) {
			return ((Schema)obj).getTables();
		} else if( isCatalog(obj) ) {
			((Catalog)obj).getTables();
		}
		
		return null;
	}
	
	public List getOtherModifiedResources() {
		return otherModifiedResources;
	}
	
    /**
     * handler for Create Relationships Model Button pressed
     */
    private ModelResource createRelationalModel(IResource targetRes, String sNewRelationshipModelName ) {
        ModelResource mrRelationshipModel = constructRelationalModel(targetRes , sNewRelationshipModelName );
        
        // Save Relationship Model
        try {
            if( mrRelationshipModel != null ) {
                mrRelationshipModel.save( null, false );
                
            }
        } catch (ModelWorkspaceException mwe) {
        	UiConstants.Util.log( mwe );
        }
        
        return mrRelationshipModel;
    }
    
    /**
     * Create a Relationships Model with the supplied name, in the desired project
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    public ModelResource constructRelationalModel( IResource targetRes, String sModelName ) {
        IPath relativeModelPath = targetRes.getProjectRelativePath().append( sModelName ).addFileExtension(XMI_EXTENSION);
        final IFile modelFile = targetRes.getProject().getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
        try {
            resrc.getModelAnnotation().setPrimaryMetamodelUri( RELATIONAL_PACKAGE_URI );
            resrc.getModelAnnotation().setModelType(ModelType.PHYSICAL_LITERAL);
            ModelUtilities.initializeModelContainers(resrc, "Create Model Containers", this); //$NON-NLS-1$ 
        } catch ( ModelWorkspaceException mwe ) {
            mwe.printStackTrace();
        }

        return resrc;
    }

    /**
     * Find the model (if one exists) along the provided path string.  Return the index of the path segment
     * that is the model.  If no model was found, return value is -1.
     * @param path the path string which may have a model in it.
     * @return the path segment index of the model, -1 if none found.
     */
    private int getModelPathIndex(String pathStr) {
        int modelIndex = -1;
        // Walk the segments one at a time, starting at last one.
        ModelResource mr = null;
        IPath path = new Path(pathStr);
        int nSegs = path.segmentCount();
        if( nSegs > 1 ) {
            // First find and open the project (first segment) and open it.
            String projSeg = path.segment(0);
            
            // Check if Project exists - if it doesnt, create it.
            IProject existProj = ResourcesPlugin.getWorkspace().getRoot().getProject(projSeg);
            if( !existProj.exists() ) {
                existProj = createProject(existProj, new NullProgressMonitor());
            }
            
            // If project exists, continue processing
            if( existProj.exists() ) {
                if( !existProj.isOpen() ) {
                    try {
                        existProj.open(new NullProgressMonitor());
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }
                for(int i=nSegs; i>1; i--) {
                    IPath workingPath = path.uptoSegment(i);
                    String osPathStr = workingPath.toOSString();
                    if(osPathStr!=null && osPathStr.length()>0) {
                        mr = getModelResource(osPathStr);
                    }
                    if(mr!=null) {
                        modelIndex = i;
                        break;
                    }
                }
            }
        }
        return modelIndex;
    }
    
    private Object getOrCreateLocation(String location, RelationalFactory factory) {
    	ModelResource mr = null;
        
        IPath locPath = new Path(location);
        int nSegs = locPath.segmentCount();
        
        int modelSegIndex = getModelPathIndex(location);
        
        // If model was found along the path, use it
        if(modelSegIndex!=-1) {
            mr = getModelResource(locPath.uptoSegment(modelSegIndex).toOSString());
            // Provided path goes beyond the Model - need to create schema under it.
            if(nSegs>modelSegIndex) {
                // If we are here, then we need to create folders
                int nFolders = nSegs - modelSegIndex;
                EObject folderEObject = null;
                Object parent = mr;
                for( int i=0; i<nFolders; i++ ) {
                    // First find folder EObject
                    String sFolderPath = locPath.uptoSegment(modelSegIndex+1 + i).toOSString();
                    folderEObject = getEObject(sFolderPath);
                    if( folderEObject == null ) {
                        // Create a Schema here (Assume it's a schema not a catalog)
                        folderEObject = createSchema(parent, locPath.segment(modelSegIndex+i), factory);
                        if( i == nFolders - 1)
                            return folderEObject;
                        parent = folderEObject;
                    } else {
                        parent = folderEObject;
                        if( i == nFolders - 1)
                            return folderEObject;
                    }
                }
            // Provided path stops at the Model, just return the Model.
            } else {
                return mr;
            }
        // Model not found, assume a new model is to be created under project
        } else {
            String projSeg = locPath.segment(0);
            // Check if Project exists
            IProject existProj = ResourcesPlugin.getWorkspace().getRoot().getProject(projSeg);
            
            if( !existProj.exists() )
                existProj = createProject(existProj, new NullProgressMonitor());
            
            if( existProj.exists() ) {
                if( !existProj.isOpen() ) {
                    try {
                        existProj.open(new NullProgressMonitor());
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }
                // We shouldn't have to create one and we should expect the model to not exist
                IPath modelPath = new Path(locPath.segment(1)).addFileExtension(XMI_EXTENSION);
                if( !existProj.exists(modelPath) ) { //new Path(locPath.segment(1)) )) {
                    // Need to create model here with this name
                    mr = createRelationalModel(existProj, locPath.segment(1));
                } else {
                    mr = getModelResource(locPath.uptoSegment(2).toOSString());
                }
                
                if( nSegs == 2 )
                    return mr;
                
                // If we are here, then we need to create folders
                int nFolders = nSegs - 2;
                EObject folderEObject = null;
                Object parent = mr;
                for( int i=0; i<nFolders; i++ ) {
                    // First find folder EObject
                    String sFolderPath = locPath.uptoSegment(3 + i).toOSString();
                    folderEObject = getEObject(sFolderPath);
                    if( folderEObject == null ) {
                        // Create a Schema here (Assume it's a schema not a catalog)
                        folderEObject = createSchema(parent, locPath.segment(2+i), factory);
                        if( i == nFolders - 1)
                            return folderEObject;
                        parent = folderEObject;
                    } else {
                        parent = folderEObject;
                        if( i == nFolders - 1)
                            return folderEObject;
                    }
                }
            }
            
        }
        
    	// We shouldn't get here, so log a message
    	
    	UiConstants.Util.log(IStatus.ERROR, "Problems creating non existing folder or model for new relationship.  Path = " + location); //$NON-NLS-1$
    	return null;
    }
    
    
}
