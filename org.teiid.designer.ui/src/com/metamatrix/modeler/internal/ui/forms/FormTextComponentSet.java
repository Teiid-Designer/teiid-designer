/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class FormTextComponentSet extends SimpleComponentSet {

    FormTextObjectEditor fText;
    ComponentSetMonitor mon;
    private final String addText;
    private final boolean valueClickable;
    final DialogProvider provider;

    public FormTextComponentSet( String id,
                                 String labelName,
                                 String addText,
                                 boolean valueClickable,
                                 DialogProvider provider ) {
        super(id, labelName);
        this.addText = addText;
        this.valueClickable = valueClickable;
        this.provider = provider;
    }

    @Override
    protected void addControls( Composite parent,
                                FormToolkit ftk ) {
        fText = new FormTextObjectEditor(addText, provider.getLaunchButtonText(), valueClickable) {
            @Override
            protected Object changeValue( Object startingValue ) {
                provider.showDialog(fText.getFormText().getShell(), startingValue);

                if (!provider.wasCancelled()) {
                    // not cancelled...
                    Object newVal = provider.getValue();
                    if (!FormUtil.safeEquals(newVal, startingValue)) {
                        // not same, fire updates:
                        ComponentSetEvent componentSetEvent = null;
                        if (mon != null) {
                            componentSetEvent = new ComponentSetEvent(FormTextComponentSet.this, newVal == null, newVal);
                            mon.update(componentSetEvent);
                        } // endif

                        if (componentSetEvent == null || componentSetEvent.doit) {
                            return newVal;
                        } // endif
                    } // endif
                } // endif

                return startingValue;
            }

            @Override
            protected void valueClicked( Object value ) {
                FormTextComponentSet.this.valueClicked(value);
            }

            @Override
            protected String getDisplayString( Object value ) {
                return getUserDisplayString(value);
            }
        }; // endanon
        Control c = fText.addControl(FormUtil.getScrolledForm(parent), parent, ftk);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP);
        c.setLayoutData(twd);
    }

    @Override
    protected void addMonitor( ComponentSetMonitor monitor ) {
        mon = monitor;
    }

    @Override
    protected void removeMonitor( ComponentSetMonitor monitor ) {
        mon = null;
    }

    public boolean isUserSet() {
        return fText.getValue() != null;
    }

    public void setValue( Object o ) {
        fText.setValue(o);
    }

    public void reset() {
        fText.setValue(null);
    }

    //
    // Methods:
    //

    /**
     * Get the string to display to the user for the specified object.
     * 
     * @param o the object to show. Never null.
     */
    protected String getUserDisplayString( Object o ) {
        return o.toString();
    }

    /**
     * What to do when the value has been clicked. This implementation does nothing.
     * 
     * @param value
     */
    protected void valueClicked( Object value ) {
    }

    //
    // Overrides:
    //
    @Override
    public void setEditible( boolean enabled ) {
        super.setEditible(enabled);
        fText.getFormText().setEnabled(true);
        fText.setEditible(enabled);
    }
}
