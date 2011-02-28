/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 4.2
 */
public class TransformationSelectionHelper {

    public static final int COUNT_SINGLE = 1;
    public static final int COUNT_MULTIPLE_SAME = -1;
    public static final int COUNT_MULTIPLE_MIXED = 2;
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_TARGET_TABLE = 0;
    public static final int TYPE_TARGET_CHILD = 1;
    public static final int TYPE_SOURCE_TABLE = 2;
    public static final int TYPE_SOURCE_CHILD = 3;
    public static final int TYPE_INPUT_SET = 4;
    public static final int TYPE_RESULT_SET = 5;
    public static final int TYPE_SOURCE_RESULT_SET = 6;
    public static final int TYPE_SQL_TRANSFORMATION_ROOT = 9;
    public static final int TYPE_SQL_TRANSFORMATION_ROOT_EXTRA = 10;
    public static final int TYPE_DIAGRAM = 11;
    public static final int TYPE_XQUERY_TRANSFORMATION_ROOT = 12;
    
    private EObject transformationRoot;
    private ISelection selection = null;
    private int type = TYPE_UNKNOWN;
    private int count = 0;
    private int countType = 1;
    /** 
     * 
     * @since 4.2
     */
    public TransformationSelectionHelper(EObject tRoot, ISelection selection) {
        super();
        this.transformationRoot = tRoot;
        this.selection = selection;
        init();
    }
    
    private void init() {
        setType();
    }

    /** 
     * @return Returns the selection.
     * @since 4.2
     */
    public ISelection getSelection() {
        return this.selection;
    }
    
    public EObject getSingleEObject() {
        if( SelectionUtilities.isSingleSelection(selection) ) {
            count = 1;
            countType = COUNT_SINGLE;
            EObject eObj = SelectionUtilities.getSelectedEObject(selection);
            if( eObj != null ) {
                return eObj;
            }
        }
        return null;
    }
    
    public int getType() {
        return type;
    }
    
    private void setType() {
        type = TYPE_UNKNOWN;
        
        if( SelectionUtilities.isSingleSelection(selection) ) {
            count = 1;
            countType = COUNT_SINGLE;
            EObject eObj = SelectionUtilities.getSelectedEObject(selection);
            if( eObj != null ) {
                type = getEObjectType(eObj);
            }
        } else {
            List allSelected = SelectionUtilities.getSelectedEObjects(selection);

            count = allSelected.size();
            if( count > 0 ) {
                EObject nextEObj = (EObject)allSelected.get(0);
                int firstType = getEObjectType(nextEObj);
                int nextType = TYPE_UNKNOWN;
                for(int i= 1; i<count; i++ ) {
                    nextEObj = (EObject)allSelected.get(i);
                    nextType = getEObjectType(nextEObj);
                    if( nextType != firstType ) {
                        type = TYPE_UNKNOWN;
                        countType = COUNT_MULTIPLE_MIXED;
                        break;
                    } 
                    type = firstType;
                    countType = COUNT_MULTIPLE_SAME;
                }
            }
        }
    }

    /** 
     * @return Returns the count.
     * @since 4.2
     */
    public int getCount() {
        return this.count;
    }
    /** 
     * @return Returns the countType.
     * @since 4.2
     */
    public int getCountType() {
        return this.countType;
    }
    
    public boolean isVirtual() {
        boolean isVirtual = false;
        if( SelectionUtilities.isSingleSelection(selection) ) {
            EObject eObj = SelectionUtilities.getSelectedEObject(selection);
            if( eObj != null ) {
                isVirtual = ModelObjectUtilities.isVirtual(eObj);
            }
        } else if( SelectionUtilities.isMultiSelection(selection)) {
            isVirtual = true;
            Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
            while( iter.hasNext() && isVirtual ) {
                isVirtual = ModelObjectUtilities.isVirtual((EObject)iter.next());
            }
        }
        
        return isVirtual;
    }
    
    private EObject getMappingRootTarget(EObject eObj) {
        if(eObj!=null && TransformationHelper.isSqlTransformationMappingRoot(eObj))  {
            return ((SqlTransformationMappingRoot)eObj).getTarget();
        }
        return null;
    }
    
    public int getEObjectType(EObject eObj) {
        int eObjType = TYPE_UNKNOWN;
        if( TransformationHelper.isSqlTransformationMappingRoot(eObj) ) {
            if( eObj == transformationRoot )
                eObjType = TYPE_SQL_TRANSFORMATION_ROOT;
            else
                eObjType = TYPE_SQL_TRANSFORMATION_ROOT_EXTRA;
        } else if( (TransformationHelper.isSqlTable(eObj) || 
                    TransformationHelper.isSqlProcedure(eObj) ||
                    TransformationHelper.isSqlProcedureResultSet(eObj)) && transformationRoot != null) {
            // is it the target?
            EObject target = getMappingRootTarget(transformationRoot);
            // if result set, target should be it's parent
            if( target != null && TransformationHelper.isSqlProcedureResultSet(target))
                target = target.eContainer();
            
            if( target != null && target == eObj ) {
                eObjType = TYPE_TARGET_TABLE;
            } else if( TransformationHelper.isSqlProcedureResultSet(eObj) &&
                       ModelObjectUtilities.isVirtual(eObj) ) {
                EObject rsContainer = eObj.eContainer();
                if( rsContainer == target )
                    eObjType = TYPE_RESULT_SET;
                else
                    eObjType = TYPE_SOURCE_RESULT_SET;
            }  else if( TransformationHelper.isSqlInputSet(eObj)) {
                eObjType = TYPE_INPUT_SET;
            } else  {
                eObjType = TYPE_SOURCE_TABLE;
            }
            
//            if( eObjType == TYPE_SOURCE_TABLE ) {
//                if((TransformationHelper.isSqlProcedure(eObj) ||
//                    TransformationHelper.isSqlProcedureResultSet(eObj)) &&
//                    ModelObjectUtilities.isVirtual(eObj)) {
//                    eObjType = TYPE_SOURCE_TABLE;
//                }
//            } 
        } else if( transformationRoot != null && 
                   (TransformationHelper.isSqlColumn(eObj) ||
                    TransformationHelper.isSqlProcedureParameter(eObj)) ) {
            // check if column in target group
            EObject eContainer = eObj.eContainer();
            EObject target = getMappingRootTarget(transformationRoot);
            EObject secondaryTarget = TransformationHelper.getTransformationTarget(transformationRoot);
            if( target != null && target == eContainer ) {
                eObjType = TYPE_TARGET_CHILD;
            } else if(secondaryTarget != null && secondaryTarget == eContainer) {
                eObjType = TYPE_TARGET_CHILD;
            } else {
                eObjType = TYPE_SOURCE_CHILD;
            }
        } else {
            // Don't know what it is, so ask someone else
            int umlType = RelationalUmlEObjectHelper.getEObjectType(eObj);
            if( umlType == RelationalUmlEObjectHelper.UML_ASSOCIATION ||
                umlType == RelationalUmlEObjectHelper.UML_GENERALIZATION ||
                umlType == RelationalUmlEObjectHelper.UML_OPERATION ||
                umlType == RelationalUmlEObjectHelper.UML_ATTRIBUTE ) {
                eObjType = TYPE_SOURCE_CHILD;
                // defect 15917 - Allow more pop-up menu options on Access Pattern nodes in target table.
                // check if column in target group: (copied from above block)
                if (transformationRoot != null) {
                    EObject eContainer = eObj.eContainer();
                    EObject target = getMappingRootTarget(transformationRoot);
                    EObject secondaryTarget = TransformationHelper.getTransformationTarget(transformationRoot);
                    if ((target != null && target == eContainer)
                     || (secondaryTarget != null && secondaryTarget == eContainer)) {
                        eObjType = TYPE_TARGET_CHILD;
                    } // endif -- target/secTarget is eObj's container
                } // endif -- root not null
            } else if( eObj instanceof Diagram ) {
                eObjType = TYPE_DIAGRAM;
            }
        }
        
        return eObjType;
    }
}
