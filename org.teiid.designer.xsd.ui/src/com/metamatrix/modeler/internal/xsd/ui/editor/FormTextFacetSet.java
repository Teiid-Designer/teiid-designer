/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetEvent;
import com.metamatrix.modeler.internal.ui.forms.ComponentSetMonitor;
import com.metamatrix.modeler.internal.ui.forms.DialogProvider;
import com.metamatrix.modeler.internal.ui.forms.FormTextObjectEditor;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

public class FormTextFacetSet extends AbstractFacetSet {

    FormTextObjectEditor fText;
    private final String addText;
    private final boolean valueClickable;
    private final DialogProvider dlp;

    public FormTextFacetSet( String id,
                             String labelName,
                             String addText,
                             boolean valueClickable,
                             final DialogProvider provider ) {
        super(id, labelName, true, false);
        this.addText = addText;
        this.valueClickable = valueClickable;
        this.dlp = provider;
    }

    @Override
    protected void addMainControl( Composite parent,
                                   FormToolkit ftk,
                                   ComponentSetMonitor mon ) {
        fText = new DlgFTObjectEditor(addText, dlp.getLaunchButtonText(), valueClickable, dlp, mon);
        Control c = fText.addControl(FormUtil.getScrolledForm(parent), parent, ftk);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP);
        c.setLayoutData(twd);
    }

    @Override
    protected void setMainValue( Object value ) {
        if (fText != null) {
            fText.setValue(value);
        } // endif
    }

    /**
     * What to do when the value has been clicked. This implementation does nothing.
     * 
     * @param value
     */
    protected void valueClicked( Object value ) {
    }

    @Override
    public void setEditible( boolean enabled ) {
        super.setEditible(enabled);
        fText.getFormText().setEnabled(true);
        fText.setEditible(enabled);
    }

    private final class DlgFTObjectEditor extends FormTextObjectEditor {

        private final DialogProvider provider;
        private final ComponentSetMonitor mon;

        DlgFTObjectEditor( String addText,
                           String changeText,
                           boolean valueClickable,
                           DialogProvider provider,
                           ComponentSetMonitor mon ) {
            super(addText, changeText, valueClickable);
            this.provider = provider;
            this.mon = mon;
        }

        @Override
        protected Object changeValue( Object startingValue ) {
            provider.showDialog(fText.getFormText().getShell(), startingValue);

            if (!provider.wasCancelled()) {
                // not cancelled...
                Object newVal = provider.getValue();
                if (!FormUtil.safeEquals(newVal, startingValue)) {
                    // not same, fire updates:
                    if (mon != null) {
                        mon.update(new ComponentSetEvent(FormTextFacetSet.this, newVal == null, newVal));
                    } // endif

                    return newVal;
                } // endif
            } // endif

            return startingValue;
        }

        @Override
        protected void valueClicked( Object value ) {
            FormTextFacetSet.this.valueClicked(value);
        }
    }
}
