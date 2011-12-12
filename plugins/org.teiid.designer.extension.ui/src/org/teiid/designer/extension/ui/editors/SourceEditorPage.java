/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_SOURCE_PAGE;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.ui.Messages;

/**
 * The readonly text viewer for the MED.
 */
public class SourceEditorPage extends MedEditorPage {

    private Text txtContent;
    private final ModelExtensionDefinitionWriter writer;

    /**
     * @param medEditor the MED editor this page belongs to (cannot be <code>null</code>)
     */
    protected SourceEditorPage( ModelExtensionDefinitionEditor medEditor ) {
        super(medEditor, MED_SOURCE_PAGE, Messages.medEditorSourcePageTitle);
        this.writer = new ModelExtensionDefinitionWriter();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#createBody(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.ui.forms.widgets.FormToolkit)
     */
    @Override
    protected void createBody( Composite body,
                               FormToolkit toolkit ) {
        body.setLayout(new GridLayout());
        body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        body.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        this.txtContent = new Text(body, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        this.txtContent.setBackground(body.getBackground());
        this.txtContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ((GridData)this.txtContent.getLayoutData()).widthHint = 600;
        ((GridData)this.txtContent.getLayoutData()).minimumWidth = 200;
        setContent();
    }

    String getMedText() {
        return this.writer.writeAsText(getMed());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#handleMedReloaded()
     */
    @Override
    public void handleMedReloaded() {
        // make sure GUI has been constructed before reloading
        if (this.txtContent != null) {
            setContent();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#handlePropertyChanged(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handlePropertyChanged( PropertyChangeEvent e ) {
        setContent();
    }

    void setContent() {
        // make sure UI thread
        if ((this.txtContent != null) && !this.txtContent.isDisposed()) {
            final Text txtMed = this.txtContent;

            this.txtContent.getDisplay().syncExec(new Runnable() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    txtMed.setText(getMedText());
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#setResourceReadOnly(boolean)
     */
    @Override
    protected void setResourceReadOnly( boolean readOnly ) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#updateAllMessages()
     */
    @Override
    protected void updateAllMessages() {
        // nothing to do
    }

}
