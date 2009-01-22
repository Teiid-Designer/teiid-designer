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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.util.NewModelObjectHelperManager;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.wizards.ICheckboxTreeViewerListenerController;
import com.metamatrix.modeler.internal.ui.wizards.StructuralCopyModelFeaturePopulator;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.ui.internal.widget.InheritanceCheckboxTreeViewer;

/**
 * TransformationCopyModelFeaturePopulator
 */
public class TransformationCopyModelFeaturePopulator 
        extends StructuralCopyModelFeaturePopulator implements UiConstants {
            
    //////////////////////////////////////////////////////////////////////////////////////
    // Instance variables
    //////////////////////////////////////////////////////////////////////////////////////
    private Stack /*<List of EObject>*/ copies;
    private Stack /*<Integer>*/ curParentIndex;
    private Stack /*<List of Boolean>*/ skippingDescendants;
    private List /*<EObject>*/ copiedSiblings;
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Constructors
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructor
     * 
     * @param sourceFile        the source file
     * @param listenerController    controller for checkbox selection changes made
     *                              in the tree viewer
     */     
    public TransformationCopyModelFeaturePopulator(IFile sourceFile,
            ICheckboxTreeViewerListenerController listenerController) {
        super(sourceFile, listenerController);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Instance methods
    //////////////////////////////////////////////////////////////////////////////////////

    /**
     * Overridden method from {@link StructuralCopyModelFeaturePopulator}.  Copy
     * selected nodes of the source model to the target.  Overridden because also
     * have to do transformations on tables and procedures.
     * 
     * @param sourceModelResource modelResource containing the old information
     * @param targetResource        the target
     * @param viewer                the tree viewer;  root is the ModelResource
     * @param extraProperties       optional properties to tweak creation of objects.
     *   Currently, this only deals with whether virtual tables have their supportsUpdate
     *   properties cleared.  See TransformationNewModelObjectHelper for details.
     * @param copyAllDescriptions   option to copy or supress coying all descriptions
     * @param monitor               a progress monitor
     */ 
    @Override
    public void copyModel(ModelResource sourceModelResource, ModelResource targetModelResource, InheritanceCheckboxTreeViewer viewer, 
            Map extraProperties, boolean copyAllDescriptions, IProgressMonitor monitor) throws ModelerCoreException {

        //Since we cannot modify the original list of children, we must make
        //a deep copy of the children.
                
        List /*<EObject>*/ sourceFirstLevelChildren = sourceModelResource.getEObjects();
        
        // JIRA Issue JBEDSP-249 - Remove the JDBC Source object when copying from Source to Virtual model
        // Remove the JDBC Source object
        int jdbcSourceIndex = -1;
        for( int i=0; i<sourceFirstLevelChildren.size(); i++ ) {
            EObject nextObj = (EObject)sourceFirstLevelChildren.get(i);
            if( ModelObjectUtilities.isJdbcSource(nextObj) ) {
                jdbcSourceIndex = i;
                break;
            }
        }
        if( jdbcSourceIndex > -1) {
            sourceFirstLevelChildren.remove(jdbcSourceIndex);
        }
        // JIRA Issue JBEDSP-257
        // Add Descriptions to the sourceFirstLevelChildren if includeDescriptions = true
        if( copyAllDescriptions ) {
            List /*<EObject>*/ allRootContents = sourceModelResource.getEmfResource().getContents();
            for( Iterator iter = allRootContents.iterator(); iter.hasNext(); ) {
                EObject nextChild = (EObject)iter.next();
                if( nextChild instanceof AnnotationContainer ) {
                    sourceFirstLevelChildren.add(nextChild);
                    break;
                }
            }
        }
        
        Collection /*<EObject>*/ sourceFirstLevelChildrenCopies = null;
        final Map originalsToCopies = new HashMap();
        try {
            sourceFirstLevelChildrenCopies = modelEditor.copyAll(
                    sourceFirstLevelChildren, originalsToCopies);
        } catch (ModelerCoreException ex) {
            throw ex;
        }
        //What we really need is not a map of originals to copies, but a map of
        //copies to originals.  So create it.
        final Map copiesToOriginals = invertMap(originalsToCopies);
        
        //Allow for any nodes already inserted into the target              
        List /*<EObject>*/targetFirstLevelChildren = targetModelResource.getEmfResource().getContents();
        int numInitialFirstLevelNodes = targetFirstLevelChildren.size();

        //Add the selected nodes to the target              
        targetModelResource.getEmfResource().getContents().addAll(sourceFirstLevelChildrenCopies);

        // delete things if needed:
        if (viewer != null) {
            //Get tree structure containing just the needed nodes.
            DefaultMutableTreeNode neededNodesRoot = getParedTreeRoot(viewer, sourceModelResource);

            //Delete unselected nodes.  Now has to be done after the fact.
            adjustIndexOfFirstLevelChildren(neededNodesRoot, numInitialFirstLevelNodes);
            insertInitialFirstLevelChildren(neededNodesRoot, targetFirstLevelChildren,
                    numInitialFirstLevelNodes);
            targetFirstLevelChildren = new ArrayList(targetModelResource.getEmfResource().getContents());
            deleteUnneededNodes(neededNodesRoot, targetFirstLevelChildren, numInitialFirstLevelNodes);
        } // endif

        //Do the transformations where needed
        List copiedFirstLevelChildrenList = 
            targetModelResource.getEmfResource().getContents();
        List modifiedCopiedFirstLevelChildrenList = new ArrayList(
                copiedFirstLevelChildrenList.size() - numInitialFirstLevelNodes);
        //Create a list of the first level nodes in the target, excluding nodes
        //that had already been inserted                
        for (int i = numInitialFirstLevelNodes; i < copiedFirstLevelChildrenList.size(); i++) {
            modifiedCopiedFirstLevelChildrenList.add(copiedFirstLevelChildrenList.get(i));
        }
        doTransformations(copiesToOriginals, modifiedCopiedFirstLevelChildrenList, extraProperties);
    }
    
    private Map invertMap(Map originalsToCopies) {
        Map copiesToOriginals = new HashMap();
        Iterator it = originalsToCopies.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry)it.next();
            copiesToOriginals.put(me.getValue(), me.getKey());
        }
        return copiesToOriginals;
    }
        
    /**
     * Do the transformations where needed.
     * 
     * @param copiesToOriginals     map of copied nodes to their originals
     * @param copiedFirstLevelEObjects  the first level nodes of the target
     * @param clearSupportsUpdate whether to clear (set to false) the supportsUpdate
     *   property of any copied tables.
     */
    private void doTransformations(
            Map /*<EObject copied node to EObject original node>*/ copiesToOriginals, 
            List /*<EObject>*/ copiedFirstLevelEObjects, Map extraProperties) {
        
        // Commit the current transaction at this point.  This ensures that the new target 
        // attributes can be found when the relational-procedure sql is validated.
        ModelerCore.commitTxn();
        
        // Start a new transaction to set the new SQL
        /*
         *  jh mod for 19567:
         *      Check all the places this class is used to verify that we NEVER need this txn 
         *      to be undoable.
         */
        ModelerCore.startTxn(false, false, "NewModel Transform - set the SQL",null);  //$NON-NLS-1$
        
        copies = new Stack();
        curParentIndex = new Stack();
        skippingDescendants = new Stack();
        copiedSiblings = copiedFirstLevelEObjects;
        boolean done = false;
//        long starttime = System.currentTimeMillis();
//        System.out.println("started making transformations at "+starttime);
        while (!done) {
            copies.push(copiedSiblings);
//System.err.println("pushed copies, size is now " + copies.size());                
            List /*<Boolean>*/ descendantsToSkip = 
                    new ArrayList(copiedSiblings.size());
            
            //See if any of current level of siblings need to have a transformation done.  
            //Also, flag any nodes that had a transformation done-- we do not have to check
            //their descendants.
            
            int index = 0;
            while ((index < copiedSiblings.size()) && (!done)) {
                EObject curCopy = (EObject)copiedSiblings.get(index);
                EObject curOriginal = (EObject)copiesToOriginals.get(curCopy);
                boolean originalIsTable = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isTable(curOriginal);
                boolean copyIsTable = TransformationHelper.isVirtualSqlTable(curCopy);
                if (originalIsTable && (!copyIsTable)) {
                    Util.log(IStatus.ERROR, Util.getString("TransformationCopyModelFeaturePopulator.doTransformationsInconsistencyError")); //$NON-NLS-1$
                    done = true;
                } else if (copyIsTable && (!originalIsTable)) {
                    Util.log(IStatus.ERROR, Util.getString("TransformationCopyModelFeaturePopulator.doTransformationsInconsistencyError")); //$NON-NLS-1$
                    done = true;
                } else {
                    boolean originalIsProcedure = false;
                    boolean copyIsProcedure = false;
                    if (!originalIsTable) {
                        originalIsProcedure = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isProcedure(curOriginal);
                        copyIsProcedure = TransformationHelper.isSqlProcedure(
                                curCopy);
                    }
                    if (originalIsProcedure && (!copyIsProcedure)) {
                        Util.log(IStatus.ERROR, Util.getString("TransformationCopyModelFeaturePopulator.doTransformationsInconsistencyError")); //$NON-NLS-1$
                        done = true;
                    } else if (copyIsProcedure && (!originalIsProcedure)) {
                        Util.log(IStatus.ERROR, Util.getString("TransformationCopyModelFeaturePopulator.doTransformationsInconsistencyError")); //$NON-NLS-1$
                        done = true;
                    } else {
                        boolean doTransformation = (originalIsTable ||
                                originalIsProcedure);
                        Boolean skipDescendants = new Boolean(doTransformation);
                        descendantsToSkip.add(skipDescendants);
                        
                        if (originalIsProcedure) {
                            removeOutParameters(curCopy);
                        }
                        
                        if (doTransformation) {
                            try {
                                NewModelObjectHelperManager.helpCreate(curCopy, extraProperties);
                            } catch (ModelerCoreException err) {
                                Util.log(IStatus.ERROR, Util.getString("TransformationCopyModelFeaturePopulator.doTransformationsHelpCreateError")); //$NON-NLS-1$
                            }
                            TransformationHelper.createTransformation(curCopy, curOriginal);
                        }
                    }
                }
                index++;
//                System.out.println("made transformation #"+index);
            }
            skippingDescendants.push(descendantsToSkip);
            
            //Find the next node to process.
            
            boolean descendantsFound = getNextDescendantNodeOfSiblings(0, descendantsToSkip);
            
            while ((!done) && (!descendantsFound)) {
                
                //No descendant node found.  See if we can find a sibling of an ancestor that
                //has descendants.
                
                if (copies.size() == 1) {
                    done = true;
                } else {
                    Integer prevChildIndexInteger = (Integer)curParentIndex.pop();
                    int prevChildIndex = prevChildIndexInteger.intValue();
//System.err.println("popped from curParentIndex, size is now " + curParentIndex.size());
                    copies.pop();
                    copiedSiblings = (List)copies.peek();
//System.err.println("popped from copies, size is now " + copies.size());
                    skippingDescendants.pop();
                    descendantsToSkip = (List)skippingDescendants.peek();
                    index = prevChildIndex + 1;
//System.err.println("index set to value incremented from previous top of curParentIndex: " + index);
                    descendantsFound = getNextDescendantNodeOfSiblings(index,
                            descendantsToSkip); 
                }
            }
        }
//        System.out.println("finished making transformations at "+new Date()+"; took "+(System.currentTimeMillis()-starttime));
    }
    
    private void removeOutParameters(EObject curCopy) {
        SqlProcedureAspect procedureAspect = (SqlProcedureAspect)AspectManager.getSqlAspect(curCopy);
                                                               
        // Get the Parameter attributes from the group
        List procParams = procedureAspect.getParameters(curCopy);
        
        for(int i=0;i<procParams.size();i++) {
            EObject paramObject = (EObject) procParams.get(i);
            SqlAspect sqlAspect = com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(paramObject);
            if(sqlAspect instanceof SqlProcedureParameterAspect) {
                SqlProcedureParameterAspect paramAspect = (SqlProcedureParameterAspect) sqlAspect;
                int direction = paramAspect.getType(paramObject);
                switch(direction) {
                    case MetadataConstants.PARAMETER_TYPES.INOUT_PARM:
                        paramAspect.setDirection(paramObject, MetadataConstants.PARAMETER_TYPES.IN_PARM);
                        break;
                    case MetadataConstants.PARAMETER_TYPES.OUT_PARM:
                        try {
                            modelEditor.delete(paramObject);
                        } catch (ModelerCoreException err) {
                        }
                        break;
                    case MetadataConstants.PARAMETER_TYPES.RETURN_VALUE:
                        try {
                            modelEditor.delete(paramObject);
                        } catch (ModelerCoreException err) {
                        }
                        break;
                }
            }
        }
    }
    
    private boolean getNextDescendantNodeOfSiblings(int startingIndex,
            List /*<Boolean>*/ descendantsToSkip) {
        boolean descendantsFound = false;
        int index = startingIndex;
        int numCopiedSiblings = copiedSiblings.size();
        
        while ((index < numCopiedSiblings) && (!descendantsFound)) {
            Boolean curSkipDescendantsBoolean = (Boolean)descendantsToSkip.get(index);
            boolean curSkipDescendants = curSkipDescendantsBoolean.booleanValue();
            if (!curSkipDescendants) {
                EObject curCopy = (EObject)copiedSiblings.get(index);
                copiedSiblings = curCopy.eContents();
                numCopiedSiblings = copiedSiblings.size();
//System.err.println("setting copiedSiblings to children of " + curCopy + ", numCopiedSiblings is " + numCopiedSiblings);                           
                curParentIndex.push(new Integer(index));
//String str = "";
//for (int i = 0; i < curParentIndex.size(); i++) {
// Integer tempInteger = (Integer)curParentIndex.get(i);
// str += " " + tempInteger.toString();
//}                         
//System.err.println("pushed value of " + index + " onto curParentIndex, values are now " + str);
                descendantsFound = true;
            }
            index++;
        }
        return descendantsFound;
    }
}
