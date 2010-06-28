package org.teiid.designer.ddl.importer.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class DdlImportWizard extends Wizard implements IImportWizard {

    public DdlImportWizard() {
    }

    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {
    }

    @Override
    public boolean performFinish() {
        return false;
    }
}
