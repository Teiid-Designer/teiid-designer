/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.sqlexplorer.gef.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.gef.commands.AddTablesToSchemaCommand;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class ReverseDatabaseWizard extends Wizard implements IWorkbenchWizard {

    /**
     * @param schema
     */
    public ReverseDatabaseWizard( Schema schema ) {

        this.schema = schema;
    }

    Schema schema;

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        ReverseDatabaseWizard.this.runFinish(monitor);
                    } catch (Exception exception) {
                        // exception.printStackTrace();
                        throw new InvocationTargetException(exception);
                    } finally {
                        monitor.done();
                    }

                }
            });

        } catch (Throwable e) {
        }
        return true;
    }

    /**
     * @param monitor
     */
    protected void runFinish( IProgressMonitor monitor ) throws Exception {
        int tableCount = selTables.selectedList.size();
        monitor.beginTask("Reading meta-data... ", tableCount);
        final ArrayList tableAdapterList = new ArrayList();
        for (int i = 0; i < tableCount; i++) {
            if (monitor.isCanceled()) throw new InterruptedException("Operation was cancelled by user");
            TableNode tn = (TableNode)selTables.selectedList.get(i);

            monitor.subTask(tn.getTableInfo().getQualifiedName());
            TableAdapter tadapt = new TableAdapter(tn);
            tableAdapterList.add(tadapt);
            monitor.worked(1);
        }
        command = new AddTablesToSchemaCommand(schema, tableAdapterList);

    }

    SessionTreeNode session;
    List selectedSchemas;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.workbench = workbench;
        this.selection = selection;
        setNeedsProgressMonitor(true);

    }

    IWorkbench workbench;
    IStructuredSelection selection;

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
    public void addPages() {
        selConnectionPage = new SelectConnection("Select Connection", this);
        addPage(selConnectionPage);
        selSchemaCatalogPage = new SelectSchemaCatalog("Select Schemas or Catalogs", this);
        addPage(selSchemaCatalogPage);
        selTables = new SelectTables("Select tables", this);
        addPage(selTables);
    }

    SelectConnection selConnectionPage = null;
    SelectSchemaCatalog selSchemaCatalogPage = null;
    SelectTables selTables = null;

    /**
     * @param object
     */
    public void setConnection( SessionTreeNode session ) {
        this.session = session;
        selSchemaCatalogPage.updateSessionNode(session);

    }

    /**
     * @return
     */
    public SessionTreeNode getSessionTreeNode() {
        return session;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    /*public boolean canFinish() {
    	
    	return super.canFinish();
    }*/
    public void setSelectedSchemas( List list ) {
        selTables.updateSelectedSchemas(list);
        selectedSchemas = list;
    }

    public List getSelectedSchemas() {
        return selectedSchemas;
    }

    /**
     * @return
     */
    public Command getCommand() {
        return command;
    }

    Command command;

}
