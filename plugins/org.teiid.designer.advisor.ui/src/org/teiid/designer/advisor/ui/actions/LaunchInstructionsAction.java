/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;

import com.metamatrix.ui.internal.widget.ListMessageDialog;

public class LaunchInstructionsAction extends Action implements AdvisorUiConstants {

    private List<String> instructions = Collections.EMPTY_LIST;

    /**
     * Construct an instance of NewModelAction.
     */
    public LaunchInstructionsAction() {
        super();
        setText("Define Vdb"); //$NON-NLS-1$
        setToolTipText("Define Vdb Tooltip"); //$NON-NLS-1$
        setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(Images.NEW_VDB));

    }
    
    public LaunchInstructionsAction( String[] instructions ) {
        this();
        this.instructions = new ArrayList<String>(instructions.length);
        for( String inst : instructions ) {
        	this.instructions.add(inst);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {

        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                ListMessageDialog.openInformation(AdvisorUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), 
                		Messages.DeployWarFile_title, null, 
                		Messages.DeployWarFile_instructions, 
                		instructions, null);
            }
        });
    }
}