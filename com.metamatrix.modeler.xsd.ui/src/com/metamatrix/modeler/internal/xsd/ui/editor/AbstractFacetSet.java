/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;
import com.metamatrix.modeler.internal.ui.forms.FormTextObjectEditor;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.modeler.internal.ui.forms.SimpleComponentSet;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;

public abstract class AbstractFacetSet extends SimpleComponentSet implements FacetSet {
    //
    // Class Constants:
    //
    private static final String LABEL_DEFAULT = GUIFacetHelper.getString("AbstractFacetSet.facetbutton.default"); //$NON-NLS-1$
    private static final String LABEL_FIXED = GUIFacetHelper.getString("AbstractFacetSet.facetbutton.fixed"); //$NON-NLS-1$
    static final String FORM_INHERIT_FROM = GUIFacetHelper.getString("AbstractFacetSet.formInheritFromText"); //$NON-NLS-1$
    static final String FORM_FIXED_BY = GUIFacetHelper.getString("AbstractFacetSet.formFixedByText"); //$NON-NLS-1$
    protected static final String FORM_OVERRIDES = GUIFacetHelper.getString("AbstractFacetSet.formOverrides"); //$NON-NLS-1$

    //
    // Instance variables:
    //
    private FacetValue currentFV;
    private final boolean needsFixed;
    private final boolean needsDescription;
    private int controlCount;
    Button fixed;
    Button dft;
    FormTextObjectEditor desc;
    private FormTextObjectEditor inherit;
    MyListener myList;
    private boolean enabled;

    //
    // Constructors:
    //
    public AbstractFacetSet( String id,
                             String labelName,
                             boolean needsFixed,
                             boolean needsDescription ) {
        super(id, labelName);
        this.needsFixed = needsFixed;
        this.needsDescription = needsDescription;
        controlCount = 4; // starting value.
        if (needsFixed) controlCount++;
        if (needsDescription) controlCount++;
    }

    //
    // Abstract methods:
    //

    /**
     * Add the main control to the parent. This should use the specified Monitor to make changes to the current value.
     * 
     * @param parent
     * @param ftk
     * @param mon The monitor to propagate changes to the value from the main control.
     * @return The Control that was added.
     */
    protected abstract void addMainControl( Composite parent,
                                            FormToolkit ftk,
                                            ComponentSetMonitor mon );

    /**
     * Sets the value of the main control.
     * 
     * @param value The initial value
     */
    protected abstract void setMainValue( Object value );

    //
    // Implementation of abstract methods:
    //
    @Override
    protected final void addControls( Composite parent,
                                      FormToolkit ftk ) {
        // init:
        init();

        // controls:
        addMainControl(parent, ftk, myList);
        if (needsFixed) {
            fixed = ftk.createButton(parent, LABEL_FIXED, SWT.CHECK);
            fixed.addSelectionListener(myList);
        } // endif

        dft = ftk.createButton(parent, LABEL_DEFAULT, SWT.CHECK);
        dft.addSelectionListener(myList);
        dft.setSelection(true);

        if (needsDescription) {
            desc = new FormTextObjectEditor(GUIFacetHelper.FORM_DESCRIPTION_ADD, GUIFacetHelper.FORM_CHANGE, false) {
                @Override
                protected Object changeValue( Object startingValue ) {
                    String description = (String)startingValue;

                    // user wants to edit the description, make it so:
                    String dialogTitle = GUIFacetHelper.getString("AbstractFacetSet.descriptionTitle"); //$NON-NLS-1$
                    String dialogText = ModelerXsdUiConstants.Util.getString("AbstractFacetSet.descriptionText", getLabelText()); //$NON-NLS-1$
                    InputDialog idlg = new InputDialog(desc.getFormText().getShell(), dialogTitle, dialogText, description, null);
                    idlg.setBlockOnOpen(true);
                    idlg.open();
                    String newDesc = idlg.getValue();

                    // if value is null, that was a cancel; don't change anything.
                    if (newDesc != null) {
                        if (newDesc.length() == 0) {
                            // explicitly set blank; clear it out:
                            newDesc = null;
                        } // endif

                        // now, see if desc really changed:
                        if (!FormUtil.safeEquals(description, newDesc, true)) {
                            // change current (we never change lastSet):
                            getCurrentFacetValue().description = newDesc;
                            updateDescription(getCurrentFacetValue());
                            myList.fireUpdate(false);
                            updateDefault();

                            // update with new description:
                            return newDesc;
                        } // endif
                    } // endif

                    // no change:
                    return description;
                }
            }; // endanon
            desc.addControl(FormUtil.getScrolledForm(parent), parent, ftk);
            desc.setValue(currentFV.description);
            TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP);
            desc.getFormText().setLayoutData(twd);
        } // endif

        inherit = new FormTextObjectEditor(null, null, true) {
            @Override
            protected Object changeValue( Object startingValue ) {
                // do nothing; should never be called
                return null;
            }

            @Override
            protected String getDisplayString( Object value ) {
                String rv;
                if (getCurrentFacetValue() != null) {
                    if (getCurrentFacetValue().isFixedByParent()) {
                        // is fixed:
                        rv = FormTextObjectEditor.HTML_LINK_END + FORM_FIXED_BY + FormTextObjectEditor.HTML_LINK_VAL_BEGIN
                             + ((XSDSimpleTypeDefinition)value).getName();
                    } else {
                        // is set or inherited:
                        if (getCurrentFacetValue().isDefault()) {
                            // we are using the default, inherited value:
                            rv = FormTextObjectEditor.HTML_LINK_END + FORM_INHERIT_FROM
                                 + FormTextObjectEditor.HTML_LINK_VAL_BEGIN + ((XSDSimpleTypeDefinition)value).getName();
                        } else {
                            rv = FormTextObjectEditor.HTML_LINK_END + FORM_OVERRIDES + FormTextObjectEditor.HTML_LINK_VAL_BEGIN
                                 + ((XSDSimpleTypeDefinition)value).getName();
                        } // endif
                    } // endif
                } else {
                    rv = null;
                } // endif

                return rv;
            }

            @Override
            protected void valueClicked( Object value ) {
                GUIFacetHelper.showObject((EObject)value);
            }
        }; // endanon
        inherit.setEditible(false); // never editible, so add/change never show.
        inherit.addControl(FormUtil.getScrolledForm(parent), parent, ftk);
        inherit.setValue(currentFV.type);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP);
        inherit.getFormText().setLayoutData(twd);
    }

    private void init() {
        if (myList == null) {
            this.currentFV = new FacetValue();
            myList = new MyListener();
        } // endif
    }

    @Override
    protected void addMonitor( ComponentSetMonitor monitor ) {
        init();
        myList.mon = monitor;
    }

    @Override
    protected void removeMonitor( ComponentSetMonitor monitor ) {
        init();
        myList.mon = null;
    }

    protected FacetValue getCurrentFacetValue() {
        return currentFV;
    }

    public FacetValue getFacetValue() {
        return currentFV.cloneValue();
    }

    public void setValue( Object o ) {
        init();

        if (o instanceof FacetValue) {
            FacetValue fv = (FacetValue)o;
            currentFV.copyValuesOf(fv); // make copy
            updateGUI(currentFV);
        } else {
            // throw the object into the current fv:
            currentFV.defaultValue = o;
            if (o == null) {
                currentFV.clear();
            } // endif
            updateGUI(currentFV);
        } // endif
    }

    void updateGUI( FacetValue fv ) {
        if (GUIFacetHelper.isReady(dft)) {
            // dft is a bellweather indicating whether GUI is constructed and viable.

            // update widget values:
            if (fv != null) {
                setEditible(true);

                // set the properties of each thing:
                if (fv.value != null) {
                    setMainValue(fv.value);
                } else {
                    // try to use super type's value in this case
                    setMainValue(fv.defaultValue);
                } // endif

                if (fixed != null) {
                    fixed.setSelection(fv.isFixedLocal);
                    // fixed.setEnabled(!fv.isFixed);
                } // endif

                dft.setSelection(fv.isDefault());

                updateDescription(fv);

                if (fv.isInherited()) {
                    inherit.setValue(fv.facet.getSimpleTypeDefinition());
                } else {
                    // not inherited:
                    inherit.setValue(null);
                } // endif -- inherited
            } else {
                // facetval was null, revert all fields:
                setEditible(true);

                setMainValue(null);// getDefaultMainValue());

                // this should be default:
                dft.setSelection(true);

                if (fixed != null) {
                    fixed.setSelection(false);
                } // endif

                // ... and don't forget description prompt:
                if (desc != null) {
                    desc.setValue(null);
                } // endif -- description widget present
            } // endif -- fv not null
        } // endif -- widget present
    }

    //
    // Utility methods:
    //
    void updateDescription( FacetValue fv ) {
        if (desc != null && GUIFacetHelper.isReady(desc.getFormText())) {
            if ("".equals(fv.description)) { //$NON-NLS-1$
                // blank description, use null:
                desc.setValue(null);
            } else {
                desc.setValue(fv.description);
            } // endif
            desc.updateText();
            desc.setEditible(enabled && !fv.isFixedByParent());
            // reflow(desc.getFormText(), getCategory());
        } // endif -- description widget present
    }

    void updateDefault() {
        if (GUIFacetHelper.isReady(dft)) {
            dft.setSelection(currentFV.isDefault());
        } // endif
    }

    public boolean isUserSet() {
        return currentFV.isDefault(); // myList.isChanged;
    }

    public void reset() {
        currentFV.resetToDefault();
        currentFV.isFixedLocal = false;
        updateGUI(currentFV);
    }

    //
    // Overrides:
    //
    @Override
    public int getControlCount() {
        return controlCount;
    }

    /** Override super to keep the inhereted-from/fixed-by link active */
    @Override
    public void setEditible( boolean newEnabled ) {
        init();

        enabled = newEnabled && !currentFV.isFixedByParent(); // stay disabled if fixed by parent

        super.setEditible(enabled);

        // update description GUI to look disabled:
        if (desc != null) {
            desc.getFormText().setEnabled(true);
            desc.setEditible(enabled);
        } // endif

        // allow inherit to stay enabled (for navigation):
        if (inherit != null) {
            inherit.getFormText().setEnabled(true);
        } // endif
    }

    //
    // Inner classes:
    //
    class MyListener implements SelectionListener, ComponentSetMonitor {
        public ComponentSetMonitor mon;
        private Object lastVal;

        public void fireUpdate( boolean delete ) {
            if (mon != null) {
                mon.update(new ComponentSetEvent(AbstractFacetSet.this, delete, getCurrentFacetValue()));
            } // endif
        }

        // implementation of listeners:
        public void widgetSelected( SelectionEvent e ) {
            if (e.widget == dft) {
                fireUpdate(true);
                dft.setSelection(true); // should always be true: currentFV.isDefault());
                // defect 18207 -- the current value is null, meaning this facet was deleted
                // only update the GUI after the deletion has happened
                updateGUI(getCurrentFacetValue());
                // also, reset default information:
                getCurrentFacetValue().resetToDefault();
                lastVal = getCurrentFacetValue().defaultValue;

            } else if (e.widget == fixed) {
                getCurrentFacetValue().isFixedLocal = fixed.getSelection();
                fireUpdate(false);
                dft.setSelection(false); // force default off
            } // endif
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
        } // ignore

        public void update( ComponentSetEvent event ) {
            // make sure the value has changed:
            if (event.isDelete || event.value instanceof FacetValue || !FormUtil.safeEquals(event.value, lastVal)) {
                // we want to process this event:
                getCurrentFacetValue().value = event.value;
                lastVal = event.value;
                fireUpdate(event.isDelete);
                updateDefault();
                if (event.isDelete) {
                    lastVal = null;
                } // endif
                updateDescription(getCurrentFacetValue());
            } // endif
        }
    } // endclass MyListener
}
