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

import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class SelectSchemaCatalog extends WizardPage {
	ReverseDatabaseWizard wizard;
	ListViewer lvLeft;
	ListViewer lvRight;
	ArrayList selectedList=new ArrayList();
	/**
	 * @param pageName
	 */
	protected SelectSchemaCatalog(String pageName,ReverseDatabaseWizard wizard) {
		super(pageName);
		this.setTitle(pageName);
		this.wizard=wizard;
		this.setPageComplete(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite composite) {
		initializeDialogUnits(composite);
		Composite composite1 = new Composite(composite, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite1.setLayout(layout);
		Label label = new Label(composite1, 0);
		label.setText("Database schemas or catalogs");
		new Label(composite1, 0);
		Label label1 = new Label(composite1, 0);
		label1.setText("Schemas or catalogs selected for import");
		List ls=new List(composite1,SWT.MULTI|SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		ls.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL));
		lvLeft=new ListViewer(ls);
		lvLeft.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				Object[] objs=((java.util.List)inputElement).toArray();
				return objs;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {		
			}
		});
		
		lvLeft.setLabelProvider(new LabelProvider());
		Composite cmp2=new Composite(composite1,SWT.NULL);
		cmp2.setLayout(new GridLayout(1, true));
		final List ls2=new List(composite1,SWT.MULTI|SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		ls2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL));
		lvRight=new ListViewer(ls2);
		lvRight.setLabelProvider(new LabelProvider());
		lvRight.setContentProvider(new IStructuredContentProvider(){
			public Object[] getElements(Object inputElement) {
				return ((java.util.List)inputElement).toArray();
			}
			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		}
		);
		lvRight.setInput(selectedList);
		Button toRight = new Button(cmp2, SWT.PUSH);
		toRight.setText(">");
		toRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toRight.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel=((IStructuredSelection)lvLeft.getSelection());
				Iterator it=sel.iterator();
				while(it.hasNext()){
					IDbModel schemaOrCatalog=(IDbModel) it.next();
					if(!selectedList.contains(schemaOrCatalog)){
						selectedList.add(schemaOrCatalog);
					}
				}	
				lvRight.refresh();
				if(ls2.getItemCount()>0)
					setPageComplete(true);
				else
					setPageComplete(false);				
				//wizard.setSelectedSchemas(selectedList);					
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		//d.addSelectionListener(this);
		toRight.setToolTipText("Move selected schemas or catalogs into right list");
		Button allToRight = new Button(cmp2, SWT.PUSH);
		allToRight.setText(">>");
		allToRight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//f.addSelectionListener(this);
		allToRight.setToolTipText("Move all schemas or catalogs into right list");
		allToRight.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				selectedList.clear();
				java.util.List ls=(java.util.List) lvLeft.getInput();
				selectedList.addAll(ls);
				lvRight.refresh();
				if(ls2.getItemCount()>0)
					setPageComplete(true);
				else
					setPageComplete(false);				
				//wizard.setSelectedSchemas(selectedList);					
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		Button allToLeft = new Button(cmp2, SWT.PUSH);
		allToLeft.setText("<<");
		allToLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//g.addSelectionListener(this);
		allToLeft.setToolTipText("Remove all schemas or catalogs from right list");
		
		allToLeft.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				selectedList.clear();
				lvRight.refresh();
				if(ls2.getItemCount()>0)
					setPageComplete(true);
				else
					setPageComplete(false);
				//wizard.setSelectedSchemas(selectedList);
				
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		Button toLeft = new Button(cmp2, SWT.PUSH);
		toLeft.setText("<");
		toLeft.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//e.addSelectionListener(this);
		toLeft.setToolTipText("Remove selected schemas or catalogs from right list");
		toLeft.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				
				IStructuredSelection sel=((IStructuredSelection)lvRight.getSelection());
				Iterator it=sel.iterator();
				while(it.hasNext()){
					IDbModel schemaOrCatalog=(IDbModel) it.next();
					selectedList.remove(schemaOrCatalog);
				}	
				lvRight.refresh();
				if(ls2.getItemCount()>0)
					setPageComplete(true);
				else
					setPageComplete(false);
				//wizard.setSelectedSchemas(selectedList);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		
		super.setControl(composite1);
	}

	/**
	 * @param session
	 */
	public void updateSessionNode(SessionTreeNode session) {
		//session.dbModel.getChildren()
		selectedList.clear();
		lvRight.refresh();
		if(session.dbModel.supportsCatalogs()){
			
			lvLeft.setInput(session.dbModel.getCatalogs());
		}
		else if(session.dbModel.supportsSchemas()){
			lvLeft.setInput(session.dbModel.getSchemas());
		}else{
			lvLeft.setInput(session.dbModel.getCatalogs());
		}
		
		lvLeft.refresh();
		this.setPageComplete(false);
	}
	@Override
    public boolean canFlipToNextPage() {
		return this.isPageComplete();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizardPage#getNextPage()
	 */
	@Override
    public IWizardPage getNextPage() {
		wizard.setSelectedSchemas(selectedList);
		return super.getNextPage();
	}

}
