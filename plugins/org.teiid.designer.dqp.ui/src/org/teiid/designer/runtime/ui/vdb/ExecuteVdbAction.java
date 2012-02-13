/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.vdb;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

public class ExecuteVdbAction extends Action {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(ExecuteVdbAction.class);
	
	ExecuteVdbWorker worker;
	
	Properties designerProperties;

	/**
	 * @since 5.0
	 */
	public ExecuteVdbAction() {
		super();
		setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.EXECUTE_VDB));
		setToolTipText(DqpUiConstants.UTIL.getString(THIS_CLASS + "tooltip")); //$NON-NLS-1$
		worker = new ExecuteVdbWorker();
	}
	
	/**
	 * @since 5.0
	 */
	public ExecuteVdbAction(Properties properties) {
		this();
		designerProperties = properties;
	}

	@Override
	public void run() {

		ExecuteVdbDialog dialog = new ExecuteVdbDialog(worker.getShell(), designerProperties);

		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			IFile vdb = dialog.getSelectedVdb();
			if (vdb != null) {
				worker.run(vdb);
			}
		}
	}
}
