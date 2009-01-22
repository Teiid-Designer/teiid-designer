/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.viewsupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.util.ModelStatisticsVisitor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * ModelStatisticsDialog is a dialog for displaying the results of a ModelStatisticsVisitor.
 */
public class ModelStatisticsDialog extends Dialog {

    private static final String TITLE = UiConstants.Util.getString("ModelStatisticsDialog.title"); //$NON-NLS-1$
    private static final String LF = "\r\n"; //$NON-NLS-1$

    private ModelStatisticsVisitor visitor;
    private ModelResource modelResource;
    private StyledText text;
    private HashMap typeMap;

    /**
     * Construct an instance of ModelStatisticsDialog.
     */
    public ModelStatisticsDialog(Shell shell, ModelStatisticsVisitor visitor, ModelResource resource) {
        super(shell);
        this.visitor = visitor;
        this.modelResource = resource;
        buildTypeMap();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        //add controls to composite as necessary
        
        text = new StyledText(composite, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 400;
        gd.heightHint = 400;
        text.setLayoutData(gd);
        
        text.setEditable(false);
        text.setWordWrap(false);
        text.setTabs(10);
        
        String metamodelName = null;
        try {
            metamodelName = this.modelResource.getPrimaryMetamodelDescriptor().getDisplayName();
        } catch (Exception e) {
            UiConstants.Util.log(e);
        }
        
        // create the header and a StyleRange to make the header bold
        String header1 = UiConstants.Util.getString("ModelStatisticsDialog.header1", this.modelResource.getItemName()); //$NON-NLS-1$
        StyleRange headerRange = new StyleRange();
        headerRange.start = 0;
        headerRange.length = header1.length();
        headerRange.fontStyle = SWT.BOLD;

        // write the header
        StringBuffer buff = new StringBuffer(header1);
        buff.append(LF);
        buff.append(LF);
        buff.append('\t');
        buff.append(UiConstants.Util.getString("ModelStatisticsDialog.header2", metamodelName)); //$NON-NLS-1$
        buff.append(LF);
        buff.append(LF);
        
        // sort the class names, which are the keys of the typeMap
        TreeSet sortedNames = new TreeSet(this.typeMap.keySet());
        for ( Iterator iter = sortedNames.iterator() ; iter.hasNext() ; ) {
            EClass eClass = (EClass) this.typeMap.get(iter.next());
            int count = visitor.getCount(eClass);
            buff.append('\t');
            buff.append(count);
            buff.append('\t');
            if ( count == 1 ) {
                buff.append(StringUtil.computeDisplayableForm(eClass.getName()));
            } else {
                buff.append(StringUtil.computePluralForm(StringUtil.computeDisplayableForm(eClass.getName())));
            }
            buff.append(LF);
        }
        
        text.setText(buff.toString());
        text.setStyleRange(headerRange);
        
        return composite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
        super.getShell().setText(TITLE);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(
            parent,
            IDialogConstants.OK_ID,
            IDialogConstants.OK_LABEL,
            true);
    }
    
    /**
     * Create a map of class names to classes in the visitor.  This is done so we can sort
     * the class names before writing the report.
     */
    private void buildTypeMap() {
        this.typeMap = new HashMap();
        for ( Iterator iter = this.visitor.getEClassesFound().iterator() ; iter.hasNext() ; ) {
            EClass eClass = (EClass) iter.next();
            typeMap.put(eClass.getName(), eClass);
        }
    }

}
