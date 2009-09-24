/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.metamodels.relational.aspects.sql.ProcedureParameterAspect;
import com.metamatrix.metamodels.xmlservice.aspects.sql.XmlInputAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 4.2
 */
public class SetDatatypeAction extends ModelObjectAction {
    private static final String STRING_STRING = "string"; //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public SetDatatypeAction() {
        super(UiPlugin.getDefault());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        determineEnablement();
    }

    @Override
    protected void doRun() {
        Collection selectedEObjects = new ArrayList(SelectionUtilities.getSelectedEObjects(getSelection()));
        
        if (!selectedEObjects.isEmpty()) {
            showDialog(selectedEObjects);
        }
    }
    
    /**
     * @since 4.0
     */
    private void determineEnablement() {
        boolean enable = false;
        
        if (!SelectionUtilities.getSelectedEObjects(getSelection()).isEmpty() ) {
            enable = allSelectedAreSqlColumns(getSelection());
            if( !enable ) {
                enable = allSelectedAreProcedureParameters(getSelection());
            }
            if( !enable ) {
                enable = allSelectedAreXmlInputParameters(getSelection());
            }
        } 

        setEnabled(enable);
    }
    
    private boolean allSelectedAreSqlColumns(ISelection selection) {
        boolean result = true;
        Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
        EObject nextEObj = null;
        MetamodelAspect mmAspect = null;
        while( iter.hasNext()  && result) {
            nextEObj = (EObject)iter.next();
            if( ModelObjectUtilities.isReadOnly(nextEObj))
                result = false;
            if( result ) {
                mmAspect = ModelObjectUtilities.getSqlAspect(nextEObj);
                if( mmAspect == null || !(mmAspect instanceof SqlColumnAspect) ) {
                    result = false;
                } else {
                    // --------------------
                    // Defect 22275 - Needed to do one more check to see if the aspect supports setting datatype.
                    // XML Document attributes do have a sqlColumnAspect but don't support datatypes
                    // --------------------
                    if( !((SqlColumnAspect)mmAspect).canSetDatatype() ) {
                        result = false;
                    }
                }
            }
        }
        
        return result;
    }
    
    private boolean allSelectedAreProcedureParameters(ISelection selection) {
        boolean result = true;
        Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
        EObject nextEObj = null;
        MetamodelAspect mmAspect = null;
        while( iter.hasNext()  && result) {
            nextEObj = (EObject)iter.next();
            if( ModelObjectUtilities.isReadOnly(nextEObj))
                result = false;
            if( result ) {
                mmAspect = ModelObjectUtilities.getSqlAspect(nextEObj);
                if( mmAspect == null || !(mmAspect instanceof ProcedureParameterAspect) ) {
                    result = false;
                } else {
                    // --------------------
                    // Defect 22275 - Needed to do one more check to see if the aspect supports setting datatype.
                    // XML Document attributes do have a sqlColumnAspect but don't support datatypes
                    // --------------------
                    if( !((ProcedureParameterAspect)mmAspect).canSetDatatype() ) {
                        result = false;
                    }
                }
            }
        }
        
        return result;
    }
    
    private boolean allSelectedAreXmlInputParameters(ISelection selection) {
        boolean result = true;
        Iterator iter = SelectionUtilities.getSelectedEObjects(selection).iterator();
        EObject nextEObj = null;
        MetamodelAspect mmAspect = null;
        while( iter.hasNext()  && result) {
            nextEObj = (EObject)iter.next();
            if( ModelObjectUtilities.isReadOnly(nextEObj))
                result = false;
            if( result ) {
                mmAspect = ModelObjectUtilities.getSqlAspect(nextEObj);
                if( mmAspect == null || !(mmAspect instanceof XmlInputAspect) ) {
                    result = false;
                } else {
                    // --------------------
                    // Defect 22275 - Needed to do one more check to see if the aspect supports setting datatype.
                    // XML Document attributes do have a sqlColumnAspect but don't support datatypes
                    // --------------------
                    if( !((XmlInputAspect)mmAspect).canSetDatatype() ) {
                        result = false;
                    }
                }
            }
        }
        
        return result;
    }

    /**
     *  
     * @param theEObjects collection of <code>EObject</code>s; cannot be null or empty.
     * @return
     * @since 4.2
     */
    protected void showDialog(Collection theEObjects) {
        int length = 0;
        EObject eObj = (EObject)theEObjects.iterator().next();
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

        // configure dialog
        DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell, eObj);
        boolean canSetLength = false;
        // If multiple objects selected, then tell the dialog
        if( theEObjects.size() > 1 ) {
            dialog.setMultipleObjects(true);
        } else {
            dialog.setMultipleObjects(false);
        }
        
        
        MetamodelAspect mmAspect = ModelObjectUtilities.getSqlAspect(eObj);
        if( mmAspect instanceof SqlColumnAspect ) {
            SqlColumnAspect sqa = (SqlColumnAspect)mmAspect;
            Object originalValue = sqa.getDatatype(eObj);
            Object[] selection = new Object[] { originalValue };
            dialog.setInitialSelections(selection);
            canSetLength = sqa.canSetLength();
            // show length panel if necessary
            if (canSetLength) {
    	        int initialLength = sqa.getLength(eObj);
    	        if( initialLength >= 0 )
    	            dialog.setInitialLength(initialLength);
    	        dialog.setEditLength(true);
            }

            // show dialog
            int status = dialog.open();

            // process dialog
            if (status == Window.OK) {
                Object newType = null;
                Object[] result = dialog.getResult();

                // return the selected value
                if (result.length > 0) {
                    if (canSetLength) {
                        length = dialog.getLength();
                    }
                    
                    newType = result[0];
                }
                setDatatypesForSqlColumns(theEObjects, (EObject)newType, length, dialog.overrideAllLengths());
            }
        } else if( mmAspect instanceof SqlProcedureParameterAspect ) {
            SqlProcedureParameterAspect ppa = (SqlProcedureParameterAspect)mmAspect;
            Object originalValue = ppa.getDatatype(eObj);
            Object[] selection = new Object[] { originalValue };
            dialog.setInitialSelections(selection);
            canSetLength = ppa.canSetLength();

            // show length panel if necessary
            if (canSetLength) {
                int initialLength = ppa.getLength(eObj);
                if( initialLength >= 0 )
                    dialog.setInitialLength(initialLength);
                dialog.setEditLength(true);
            }
            
            // show dialog
            int status = dialog.open();

            // process dialog
            if (status == Window.OK) {
                Object newType = null;
                Object[] result = dialog.getResult();

                // return the selected value
                if (result.length > 0) {
                    if (canSetLength) {
                        length = dialog.getLength();
                    }
                    
                    newType = result[0];
                }
                setDatatypesForProcedureParameters(theEObjects, (EObject)newType, length, dialog.overrideAllLengths() );
            }
        }

    }
    
    /**
     * Set the datatype for the supplied sqlColumn eobject. 
     * @param eObject the supplied EObject
     * @param datatype the Datatype
     */
    private void setDatatypesForSqlColumns(Collection columns, EObject datatype, int theLength, boolean overrideAllLengths) {
        SqlColumnAspect columnAspect = null;
        EObject nextEObj = null;
        Iterator iter = columns.iterator();
        int length = 0;
        if( typeIsString(datatype) )
            length = theLength;
        
        boolean requiredStart = ModelerCore.startTxn(true,true,"Set Datatype For Columns",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            while( iter.hasNext() ) {
                nextEObj = (EObject)iter.next();
                columnAspect = (SqlColumnAspect)AspectManager.getSqlAspect(nextEObj);
                boolean hasLength = columnAspect.canSetLength() && columnAspect.getLength(nextEObj) > 0;
                
                if (columnAspect.canSetDatatype()) {
                    columnAspect.setDatatype(nextEObj,datatype);
                }
                
                if ( overrideAllLengths && columnAspect.canSetLength()) {
                    columnAspect.setLength(nextEObj, length);
                } else if( !overrideAllLengths && !hasLength && columnAspect.canSetLength() ) {
                    columnAspect.setLength(nextEObj, length);
                }
            }
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    /**
     * Set the datatype for the supplied sqlColumn eobject. 
     * @param eObject the supplied EObject
     * @param datatype the Datatype
     */
    private void setDatatypesForProcedureParameters(Collection columns, EObject datatype, int length, boolean overrideAllLengths) {
        SqlProcedureParameterAspect procedureAspect = null;
        EObject nextEObj = null;
        Iterator iter = columns.iterator();

        
        boolean requiredStart = ModelerCore.startTxn(true,true,"Set Datatype For Columns",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            while( iter.hasNext() ) {
                nextEObj = (EObject)iter.next();
                procedureAspect = (SqlProcedureParameterAspect)AspectManager.getSqlAspect(nextEObj);
                boolean hasLength = procedureAspect.canSetLength() && procedureAspect.getLength(nextEObj) > 0;
                
                if (procedureAspect.canSetDatatype()) {
                    procedureAspect.setDatatype(nextEObj,datatype);
                }
                
                if ( overrideAllLengths && procedureAspect.canSetLength() && procedureAspect.canSetLength()) {
                    procedureAspect.setLength(nextEObj, length);
                } else if( !overrideAllLengths && !hasLength ) {
                    procedureAspect.setLength(nextEObj, length);
                }
            }
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
    
    private boolean typeIsString(Object type) {
        if( type != null && type instanceof XSDSimpleTypeDefinition ) {
            String simpleType = ModelerCore.getWorkspaceDatatypeManager().getRuntimeTypeName((EObject)type);
            if( simpleType.equalsIgnoreCase(STRING_STRING) ) {
                return true;
            }
        }
        return false;
    }
}
