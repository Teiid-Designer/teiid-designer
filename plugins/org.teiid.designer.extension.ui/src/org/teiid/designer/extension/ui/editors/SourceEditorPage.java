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

    private Text content;
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

        this.content = new Text(body, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        this.content.setBackground(body.getBackground());
        setContent();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#handlePropertyChanged(java.beans.PropertyChangeEvent)
     */
    @Override
    protected void handlePropertyChanged( PropertyChangeEvent e ) {
        if ((this.content != null) && !this.content.isDisposed()) {
            // make sure UI thread
            this.content.getDisplay().syncExec(new Runnable() {

                /**
                 * {@inheritDoc}
                 *
                 * @see java.lang.Runnable#run()
                 */
                @Override
                public void run() {
                    setContent();
                }
            });
        }
    }

    void setContent() {
        this.content.setText(this.writer.writeAsText(getMed()));
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
