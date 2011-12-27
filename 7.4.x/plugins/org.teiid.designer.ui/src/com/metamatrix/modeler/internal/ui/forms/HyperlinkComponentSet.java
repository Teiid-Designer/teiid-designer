/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.forms;

import java.io.File;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class HyperlinkComponentSet extends SimpleComponentSet {

    String urlText;
    private FormTextObjectEditor linker;
    private ComponentSetMonitor mon;
    private boolean modifyResource;

    public HyperlinkComponentSet( String id,
                                  String text ) {
        super(id, null);
        urlText = text;
    }

    public HyperlinkComponentSet( String id,
                                  String text,
                                  boolean modifyResource ) {
        super(id, null);
        urlText = text;
        this.modifyResource = modifyResource;
    }

    protected void valueClicked( Object value ) {
        if (mon != null) {

            // if the resource will be modified, check whether it is read only.
            if (modifyResource && value instanceof EObject) {
                EObject eObj = (EObject)value;
                String filePath = eObj.eResource().getURI().toFileString();
                // Defect 24344 - the filePath may be NULL because some base types may be basic XSD schema types that are NOT in
                // modifyable resources (i.e. in BuildInDatatypes.xsd, etc...)
                
                if( filePath != null ) {
                	File file = new File(filePath);
	                if (!file.canRead()) {
	                    // Prompt whether to set the resource to writable
	                    // if( MessageDialog.openConfirm(null, UiPlugin.Util.getString("HyperlinkComponentSet.readOnlyTitle"),
	                    // UiPlugin.Util.getString("HyperlinkComponentSet.resourceReadOnlyMessage", filePath))) { //$NON-NLS-1$ //$NON-NLS-2$
	                    // file.setWritable(true);
	                    // }else {
	                    return;
                    }
                }
            }

            mon.update(new ComponentSetEvent(this, false, value));
        } // endif
    }

    public String getUrlText() {
        return this.urlText;
    }

    public void setUrlText( String urlText ) {
        this.urlText = urlText;
        if (linker != null) {
            linker.updateText();
        } // endif
    }

    @Override
    protected void addControls( Composite parent,
                                FormToolkit ftk ) {
        linker = new FormTextObjectEditor(null, null, true) {
            @Override
            protected String getDisplayString( Object value ) {
                return urlText;
            }

            @Override
            protected void valueClicked( Object value ) {
                HyperlinkComponentSet.this.valueClicked(value);
            }
        }; // endanon
        linker.addControl(FormUtil.getScrolledForm(parent), parent, ftk);
        TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 1);
        linker.getFormText().setLayoutData(twd);
        setEditible(false);
    }

    /** does nothing */
    @Override
    protected void addMonitor( ComponentSetMonitor monitor ) {
        mon = monitor;
    }

    /** does nothing */
    @Override
    protected void removeMonitor( ComponentSetMonitor monitor ) {
        mon = null;
    }

    public boolean isUserSet() {
        if (linker != null) {
            return linker.getValue() != null;
        } // endif

        return false;
    }

    public void setValue( Object o ) {
        if (linker != null) {
            linker.setValue(o);
        } // endif
    }

    /** does nothing */
    public void reset() {
    }

    @Override
    public void setEditible( boolean enabled ) {
        if (linker != null) {
        	linker.getFormText().setEnabled(true);
        } // endif
    }
}
