/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.relational.ui.wizards.RelationalObjectProcessor;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractObjectProcessor;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.textimport.VirtualTableRowObject;
import com.metamatrix.modeler.transformation.ui.util.TransformationDiagramUtil;



/** 
 * @since 4.2
 */
public class VirtualRelationalObjectProcessor extends AbstractObjectProcessor {
    private static final String I18N_PREFIX             = "VirtualRelationalObjectProcessor"; //$NON-NLS-1$
    private static final String SEPARATOR               = "."; //$NON-NLS-1$

    //============================================================================================================================
    // Static Methods
    
    private IProgressMonitor monitor;
    
    /** 
     * 
     * @since 4.2
     */
    public VirtualRelationalObjectProcessor() {
        super();
    }
    
    
    public Collection createRowObjsFromStrings(Collection rowStrings) {
        Iterator iter = rowStrings.iterator();
        String nextStr = null;
        
        Collection stringRows = new ArrayList();
        VirtualTableRowObject nextRow = null;
        while( iter.hasNext() ) {
            nextStr = (String)iter.next();
            nextRow = new VirtualTableRowObject(nextStr);
            if( nextRow.isValid() )
            	stringRows.add(nextRow);
            else {
            	logParsingError(nextStr);
            }
        }
        return stringRows;
    }

    public void generateObjsFromRowObjs(Object targetResource, Object location, Collection tableRows) {
        int iRow = 0;
        Iterator iter = tableRows.iterator();
        EObject bt = null;
        VirtualTableRowObject nextRow = null;
        
        String sSize = Integer.toString(tableRows.size());
        
        while( iter.hasNext() ) {
        	iRow++;

            nextRow = (VirtualTableRowObject)iter.next();
            
        	if( monitor != null ) {
        		monitor.worked(1);
				monitor.subTask(UiConstants.Util.getString(I18N_PREFIX + SEPARATOR + "incrementalProgress", Integer.toString(iRow), sSize, nextRow.getName())); //$NON-NLS-1$
        	}
            
            bt = createTable( location, nextRow);
            
            createTransformation(bt, nextRow.getSelectSql(), targetResource);
            createAnnotation(targetResource, bt, nextRow.getDescription());
            if( monitor != null && monitor.isCanceled() ) {
            	break;
            }
        }

    }
    
    private void createAnnotation(Object targetResource, EObject eObject, String description) {
        if( description != null && description.length() > 0 ) {
            ModelContents contents = null;
            if(targetResource instanceof ModelResource) {
                contents = ModelerCore.getModelEditor().getModelContents((ModelResource)targetResource);
            }else if (targetResource instanceof Resource) {
                contents = ModelerCore.getModelEditor().getModelContents((Resource)targetResource);
            }
            Annotation newAnnot = ModelResourceContainerFactory.createNewAnnotation(eObject, contents.getAnnotationContainer(true));
            newAnnot.setDescription(description);
        }
    }
    
    private EObject createTable(Object location, VirtualTableRowObject tableRow) {
    	EObject vTarget = null;
    	String selectSql = tableRow.getSelectSql();
    	// Create Procedure if supplied SQL is for a procedure
    	if(selectSql!=null && selectSql.trim().toUpperCase().startsWith("CREATE")) {    //$NON-NLS-1$
    		vTarget = RelationalObjectProcessor.createProcedure(tableRow.getName());
    		TransformationHelper.createProcResultSet(vTarget);
        // Create BaseTable
    	} else {
    		vTarget = RelationalObjectProcessor.createBaseTable(tableRow.getName(), false);
    	}

        if( vTarget != null ) {
            if( location instanceof ModelResource ) {
            	addValue(location, vTarget, getModelResourceContents((ModelResource)location));
            } else if( RelationalObjectProcessor.isSchema(location) ) {
            	addValue(location, vTarget, RelationalObjectProcessor.getTablesEList(location));
            } else if( RelationalObjectProcessor.isCatalog(location) ) {
            	addValue(location, vTarget, RelationalObjectProcessor.getTablesEList(location));
            } else if( location instanceof Resource  ) {
                addValue(location, vTarget, ((Resource)location).getContents());
            }
        }
        
        return vTarget;
    }
    
    private void createTransformation(EObject baseTable, String selectSql, Object resource) {
        TransformationHelper.createTransformation(baseTable, selectSql);
        if(resource instanceof ModelResource) {
            TransformationDiagramUtil.createTransformationDiagram(baseTable, (ModelResource)resource, true);
        }
    }

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
