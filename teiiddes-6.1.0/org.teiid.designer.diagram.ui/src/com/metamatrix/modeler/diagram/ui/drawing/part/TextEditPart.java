/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.drawing.part;

import java.beans.PropertyChangeEvent;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.drawing.model.TextModelNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Dialog;

/**
 * TextEditPart
 */
public class TextEditPart extends DrawingEditPart {
    String newText = null;

    /**
     * Construct an instance of DrawingEditPart.
     */
    public TextEditPart() {
        super();
    }

    /**
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        return getFigureFactory().createFigure((DiagramModelNode)getModel());
    }

    /**
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange( PropertyChangeEvent evt ) {
        String prop = evt.getPropertyName();

        if (prop.equals(DiagramUiConstants.DiagramNodeProperties.SIZE)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.LOCATION)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.PROPERTIES)) {
            refreshVisuals();
        } else if (prop.equals(DiagramUiConstants.DiagramNodeProperties.NAME)) {
            ((Label)getFigure()).setText(((TextModelNode)getModel()).getUserString());
            Label theLabel = (Label)getFigure();
            Dimension newSize = FigureUtilities.getStringExtents(theLabel.getText(), theLabel.getFont());
            // ((Label)getFigure()).setSize(newSize);
            ((TextModelNode)getModel()).setSize(newSize);
            refreshVisuals();
        }
    }

    public void performDirectEdit() {
        newText = null;

        final String oldName = ((TextModelNode)getModel()).getUserString();
        final Dialog dlg = new Dialog(Display.getDefault().getActiveShell(), "Drawing Text Entry") { //$NON-NLS-1$
            @Override
            protected Control createDialogArea( final Composite parent ) {
                final Composite dlgPanel = (Composite)super.createDialogArea(parent);
                WidgetFactory.createLabel(dlgPanel, "Enter text for drawing object:"); //$NON-NLS-1$
                final Text nameText = WidgetFactory.createTextField(dlgPanel, GridData.FILL_HORIZONTAL, oldName);
                nameText.setSelection(0, oldName.length());
                nameText.addModifyListener(new ModifyListener() {
                    public void modifyText( final ModifyEvent event ) {
                        handleModifyText(nameText);
                    }
                });
                return dlgPanel;
            }

            @Override
            protected void createButtonsForButtonBar( final Composite parent ) {
                super.createButtonsForButtonBar(parent);
                getButton(IDialogConstants.OK_ID).setEnabled(true);
            }

            void handleModifyText( Text nameText ) {
                final String newName = nameText.getText();
                final boolean valid = (newName.length() > 0);
                getButton(IDialogConstants.OK_ID).setEnabled(valid);
                if (valid) {
                    TextEditPart.this.newText = newName;
                }
            }
        };

        if (dlg.open() == Window.OK && this.newText != null) {
            if (getModel() != null) {
                if (getModel() instanceof TextModelNode) {
                    ((TextModelNode)getModel()).setUserString(newText);
                }
            }
        }

    }

    @Override
    public void performRequest( Request request ) {
        if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) performDirectEdit();
    }
}
