/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.mapping.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.teiid.designer.diagram.ui.part.DiagramEditPart;
import org.teiid.designer.mapping.ui.UiConstants;
import org.teiid.designer.mapping.ui.UiPlugin;
import org.teiid.designer.mapping.ui.editor.EditInputSetDialog;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.ui.common.util.UiUtil;


/**
 * Edit input set action dedicated to being used in the mapping transform diagram on selection of InputSetImpl only
 * 
 * @author blafond
 *
 */
public class EditInputSetAction  extends MappingAction {

	public EditInputSetAction() {
        super();
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.EDIT_OBJECT_ICON));
        setText("Edit Input Set");
        setEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    protected void doRun() {
        if (getMappingClassFactory() != null) {
            if (getMappingClassFactory() != null) {
            	InputSet inputSet = null;
            	@SuppressWarnings("unchecked")
				List<DiagramEditPart> selectedEditParts = new ArrayList<DiagramEditPart>(editor.getDiagramViewer().getSelectedEditParts());
            	if( selectedEditParts.size() == 1 ) {
            		if( selectedEditParts.get(0).getModelObject() instanceof InputSet ) {
            			inputSet = (InputSet)selectedEditParts.get(0).getModelObject();
            		}
            	}

            	EditInputSetDialog dialog = new EditInputSetDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(), inputSet);
            	dialog.open();
                	
            }
        }

        setEnabled(true);
    }

}
