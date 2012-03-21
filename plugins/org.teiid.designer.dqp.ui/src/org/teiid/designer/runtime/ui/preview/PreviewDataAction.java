/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.preview;

import java.util.Properties;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

public class PreviewDataAction extends Action {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(PreviewDataAction.class);

	PreviewDataWorker worker;
	
	Properties designerProperties;

	/**
	 * @since 5.0
	 */
	public PreviewDataAction() {
		super();
		setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.PREVIEW_DATA_ICON));
		setToolTipText(DqpUiConstants.UTIL.getString(THIS_CLASS + "tooltip")); //$NON-NLS-1$
		worker = new PreviewDataWorker();
	}
	
	public PreviewDataAction(Properties properties) {
		this();
		this.designerProperties = properties;
	}

	@Override
	public void run() {
		if (!worker.isPreviewPossible()) {
			return;
		}

		PreviewDataDialog dialog = new PreviewDataDialog(worker.getShell(), designerProperties);

		dialog.open();

		if (dialog.getReturnCode() == Window.OK) {
			EObject eObj = dialog.getPreviewableEObject();
			if (eObj != null) {
                worker.run(eObj, false);
			}
		}
	}

}
