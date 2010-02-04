/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.internal.ui.forms.ComponentCategory;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiPlugin;

public class SimpleDatatypeEditorPanel extends Composite {
    //
    // Instance variables:
    //
    private FormToolkit toolkit;
    private FacetModel mdl;

    //
    // Constructors:
    //
    public SimpleDatatypeEditorPanel(Composite parent) {
        super(parent, SWT.NONE);
        initModel();
        initGUI();
    }

    private void initModel() {
        mdl = new FacetModel();
    }

    private void initGUI() {
        setLayoutData(new GridData(GridData.FILL_BOTH));
        setLayout(new FillLayout());

        Display display = getDisplay();
        if (ModelerXsdUiPlugin.getDefault() != null) {
            toolkit = ModelerXsdUiPlugin.getDefault().getFormToolkit(display);
        } else {
            toolkit = new FormToolkit(display);
        } // endif

        FormUtil.tweakColors(toolkit, display);
        final ScrolledForm form = toolkit.createScrolledForm(this);
        // tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = form.getHorizontalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif
        bar = form.getVerticalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif
        form.setLayoutData(new GridData(GridData.FILL_BOTH));

        TableWrapLayout twl = new TableWrapLayout();
        form.getBody().setLayout(twl);
//        twl.numColumns = 2;
        form.setText(GUIFacetHelper.getString("SimpleDatatypeEditor.title")); //$NON-NLS-1$

        ComponentCategory[] ccats = mdl.getCategories();
        for (int i = 0; i < ccats.length; i++) {
            ComponentCategory category = ccats[i];
            Composite s = category.addFormControl(form, toolkit);
            TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB,TableWrapData.FILL);
            s.setLayoutData(twd);
            category.setEnabled(false);
        } // endfor
        
        form.reflow(true);
    }

    //
    // Methods:
    //
    public void setReadOnly(boolean readOnly) {
        if (mdl.getSimpleType() == null) {
            // force readonly to true if no type set
            readOnly = true;
        } // endif

        if (readOnly != mdl.isReadOnly()) {
            // only set if different:
            mdl.setGUIReadOnly(readOnly);
        } // endif
    }

    public XSDSimpleTypeDefinition getInput() {
        return mdl.getSimpleType();
    }

    public FacetModel getModel() {
        return mdl;
    }

    public void setSchema(XSDSchema schema) {
        mdl.setSchema(schema);
    }

    public void setInput(XSDSimpleTypeDefinition def) {
        // TODO this should resize, not individual things, if possible
        // ... that is, resize after everything has been set
        //  ? How to deal with changes (desription, tables, etc)?
        mdl.setSimpleType(def);
    }
    
    //
    // Main/testing:
    //
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Simple Datatypes Editor as a Form"); //$NON-NLS-1$
        shell.setLayout(new FillLayout());

        SimpleDatatypeEditorPanel ft = new SimpleDatatypeEditorPanel(shell);
        System.out.println(ft);
//        LayoutDebugger.debugLayout(ft);

        shell.setBounds(100, 100, 650, 550);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
