/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui.dialogs;

import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Dialog;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.xml.ui.ModelerXmlUiConstants;


/**
 * This dialog prompts the user to inform them that there are Simple Types in the document they are creating that are not
 * Enterprise Types.
 *
 * @since 8.0
 */
public class ConvertSimpleTypesToEnteriseTypesDialog extends Dialog implements ModelerXmlUiConstants {

    private CLabel messageLabel;
    private Set xsdResources;

    public ConvertSimpleTypesToEnteriseTypesDialog( Shell shell,
                                                    Set xsdResources ) {
        super(shell, Util.getString("ConvertSimpleTypesToEnterpriseTypesDialog.dialogTitle")); //$NON-NLS-1$
        setCenterOnDisplay(true);
        this.xsdResources = xsdResources;
    }

    @Override
    protected Control createDialogArea( Composite parent ) {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        c.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        StyledText txtInfo = new StyledText(c, SWT.WRAP | SWT.READ_ONLY);

        StringBuffer fBuf = new StringBuffer();
        Iterator fileIter = this.xsdResources.iterator();

        while (fileIter.hasNext()) {
            XSDResourceImpl xsdResource = (XSDResourceImpl)fileIter.next();
            IPath resourcePath = null;
            ModelResource modelRsrc = ModelUtilities.getModelResource(xsdResource, true);
            if (modelRsrc != null) {
                resourcePath = modelRsrc.getPath();
            }
            String fileName = CoreStringUtil.Constants.EMPTY_STRING;
            if (resourcePath != null) {
                fileName = resourcePath.makeRelative().toOSString();
            } else {
                fileName = Util.getString("ConvertSimpleTypesToEnterpriseTypesDialog.unableToLoadResource") + xsdResource.getURI().toString();//$NON-NLS-1$
            }

            fBuf.append("\n"); //$NON-NLS-1$
            fBuf.append("\t"); //$NON-NLS-1$
            fBuf.append(fileName);
            if (fileIter.hasNext()) {
                fBuf.append(","); //$NON-NLS-1$
            }
        }
        Object[] params = new Object[] {fBuf.toString()};

        StringBuffer sbuf = new StringBuffer(Util.getString("ConvertSimpleTypesToEnterpriseTypesDialog.text", params)); //$NON-NLS-1$

        txtInfo.setText(sbuf.toString());
        txtInfo.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        txtInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtInfo.setEnabled(false);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        c.setLayoutData(gridData);

        this.messageLabel = WidgetFactory.createLabel(c);
        this.messageLabel.setText(" "); //$NON-NLS-1$
        GridData messageData = new GridData(GridData.FILL_BOTH);
        messageData.grabExcessHorizontalSpace = true;
        messageData.grabExcessVerticalSpace = true;
        this.messageLabel.setLayoutData(messageData);
        return c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        this.close();
    }

    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        if (id == 1) {
            label = IDialogConstants.NO_LABEL;
        } else {
            label = IDialogConstants.YES_LABEL;
            defaultButton = true;
        }
        return super.createButton(parent, id, label, defaultButton);
    }
}
