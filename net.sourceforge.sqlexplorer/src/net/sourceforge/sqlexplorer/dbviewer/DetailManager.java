package net.sourceforge.sqlexplorer.dbviewer;

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
 
import java.util.ArrayList;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbviewer.details.ColumnsDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.FKDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.IDetailLogDisplay;
import net.sourceforge.sqlexplorer.dbviewer.details.IndexDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.PKDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.PreviewDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.RowCountDetail;
import net.sourceforge.sqlexplorer.dbviewer.details.StatusLineLogDisplay;
import net.sourceforge.sqlexplorer.dbviewer.model.IDbModel;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.ext.IActivablePanel;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


 
public class DetailManager {
	public DetailManager(TreeViewer tv,Composite c,IPreferenceStore store, SessionTreeNode sessionTreeNode){
		parent=new Composite(c,SWT.NULL);
//		this.tv=tv;
		this.store=store;
		parent.setLayout(sLayout);
		this.sessionTreeNode=sessionTreeNode;
	}
	private Composite parent;
	private Composite overParent;
	private Composite overTableObjectTypeParent;
	private Composite tableNodeComposite;
	//private Composite tableObjectTypeComposite=null;	
	private StackLayout sLayout=new StackLayout();
	//private Composite child=null;
	ColumnsDetail colDetail;
	IndexDetail indDetail;
	PKDetail pkDetail;
	FKDetail fkDetail;

	PreviewDetail previewDetail;
	RowCountDetail rowCountDetail;
	TabFolder tabFolder;
	private StatusLineManager slm;
	IDetailLogDisplay detailLog;
	private IPreferenceStore store;
	public Composite getComposite(){return parent;}
	private SessionTreeNode sessionTreeNode;
//	private TreeViewer tv;
	ArrayList addedTabs =new ArrayList();
	IActivablePanel[] panels=null;
	private Composite extComposite;
	private Text objectCountTextWidget;
	public void activate(IDbModel m){
		if(extComposite!=null){
			try{
				extComposite.dispose();
				extComposite=null;
			}catch(Throwable e){
			}
		}
		panels=null;
		if(!addedTabs.isEmpty()){
			for(int i=0;i<addedTabs.size();i++)
				((TabItem)addedTabs.get(i)).dispose();
			addedTabs.clear();
		}
		if(m instanceof TableNode){
			if(tableNodeComposite==null)
				createTableNodeComposite();	
			TableNode tn=(TableNode)m;		
			panels=SQLExplorerPlugin.getDefault().pluginManager.getAddedPanels(sessionTreeNode,tn);
			if(panels!=null && panels.length>0){	
				for(int i=0;i<panels.length;i++){
					
					TabItem itemTab=new TabItem(tabFolder,SWT.NULL);
					itemTab.setText(panels[i].getText()); 
					itemTab.setControl(panels[i].create(sessionTreeNode,tn,tabFolder));
					addedTabs.add(itemTab);
				}
			}
			colDetail.setNode(tn);
			indDetail.setNode(tn);
			pkDetail.setNode(tn);
			fkDetail.setNode(tn);
			previewDetail.setNode(tn);
			rowCountDetail.setNode(tn);
			
			//colDetail.activate();
			int sel=tabFolder.getSelectionIndex();
			if(sel==0)
				colDetail.activate();
			else if(sel==1)
				indDetail.activate();
			else if(sel==2)
				pkDetail.activate();
			else if(sel==3)
				fkDetail.activate();
			else if(sel==4)
				previewDetail.activate();		
			else if(sel==5)
				rowCountDetail.activate();
			else{
				try{
					panels[sel-6].activate();
				}catch(Throwable e){
					SQLExplorerPlugin.error("error activating plugin ",e); //$NON-NLS-1$
				}
				
			}
			sLayout.topControl=overParent;//tableNodeComposite;
		}
		else{
			Composite cmp=null;
			try{
				//MessageDialog.openInformation(null,"prima di chiamare get composite","");
				cmp=m.getComposite(this);
				//MessageDialog.openInformation(null,"prima di chiamare activate","");
				//m.activate(this);
			}catch(Throwable e){
				SQLExplorerPlugin.error("Error Activating the plugin details ",e); //$NON-NLS-1$
			}
			extComposite=cmp;
			if(cmp!=null){
				sLayout.topControl=cmp;
			}
			else {
                if(objectCountTextWidget==null){
                    createTableObjectTypeComposite();   
                }
                Object obj[]=m.getChildren();
                int count=0;
                if(obj!=null)
                    count=obj.length;
                objectCountTextWidget.setText(""+count); //$NON-NLS-1$
                sLayout.topControl=overTableObjectTypeParent;
            }
		}
		parent.layout();
	}/**
	 * 
	 */
	private void createTableObjectTypeComposite() {
		overTableObjectTypeParent=new Composite(parent,SWT.NULL);
		overTableObjectTypeParent.setLayout(new FillLayout());
		objectCountTextWidget=new Text(overTableObjectTypeParent,SWT.BORDER);
		objectCountTextWidget.setEditable(false);
		overTableObjectTypeParent.layout();
		//itemTab.setControl(textWidget);
		
	}
	
	
	private void createTableNodeComposite(){
		overParent=new Composite(parent,SWT.NULL);
		overParent.setLayout(new FillLayout());
		slm=new StatusLineManager();
		detailLog=new StatusLineLogDisplay(slm);
		
		
		GridData gid;
		GridLayout layout;
	
		// Define layout.
		layout = new GridLayout();
		gid = new GridData();
		layout.marginHeight = layout.marginWidth = 
		layout.horizontalSpacing = layout.verticalSpacing = 0;
		
		overParent.setLayout(layout);
		gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
		gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
		
		
		Composite _parent=new Composite(overParent,SWT.NULL);
		_parent.setLayout(new FillLayout());
		_parent.setLayoutData(gid);
			
		
		tableNodeComposite=new Composite(_parent,SWT.NULL);
		
		
		tableNodeComposite.setLayout(new FillLayout());
		tabFolder=new TabFolder(tableNodeComposite,SWT.NULL);
		final TabItem tabItem1=new TabItem(tabFolder,SWT.NULL);
		tabItem1.setText(Messages.getString("Columns_1")); //$NON-NLS-1$
		tabItem1.setToolTipText(Messages.getString("Show_Columns_2"));	 //$NON-NLS-1$
		colDetail=new ColumnsDetail(tabItem1,tabFolder,detailLog);
		
		final TabItem tabItem2=new TabItem(tabFolder,SWT.NULL);		
		tabItem2.setText(Messages.getString("Indexes_3")); //$NON-NLS-1$
		tabItem2.setToolTipText(Messages.getString("Show_Indexes_4"));		 //$NON-NLS-1$
		indDetail=new IndexDetail(tabItem2,tabFolder,detailLog);	
		
		final TabItem tabItem3=new TabItem(tabFolder,SWT.NULL);
		tabItem3.setText(Messages.getString("Primary_Key_5")); //$NON-NLS-1$
		tabItem3.setToolTipText(Messages.getString("Show_Primary_Key_Info_6"));		 //$NON-NLS-1$
		pkDetail=new PKDetail(tabItem3,tabFolder,detailLog);	
		
		final TabItem tabItem4=new TabItem(tabFolder,SWT.NULL);		
		tabItem4.setText(Messages.getString("Foreign_Key_9")); //$NON-NLS-1$
		tabItem4.setToolTipText(Messages.getString("Show_Foreign_Key_Info_10")); //$NON-NLS-1$
		fkDetail=new FKDetail(tabItem4,tabFolder,detailLog);

		final TabItem tabItem5=new TabItem(tabFolder,SWT.NULL);		
		tabItem5.setText(Messages.getString("Preview_7")); //$NON-NLS-1$
		tabItem5.setToolTipText(Messages.getString("Show_data_preview_8")); //$NON-NLS-1$
		previewDetail=new PreviewDetail(tabItem5,tabFolder,detailLog,store);
		
		final TabItem tabItem6=new TabItem(tabFolder,SWT.NULL);		
		tabItem6.setText(Messages.getString("DetailManager.Row_Count_2"));  //$NON-NLS-1$
		tabItem6.setToolTipText(Messages.getString("DetailManager.Display_Row_Count_3"));  //$NON-NLS-1$
		rowCountDetail=new RowCountDetail(tabItem6,tabFolder,detailLog);
		
		tabFolder.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e){
				detailLog.setMessage("");				 //$NON-NLS-1$
				if(e.item.equals(tabItem1))
					colDetail.activate();				
				else if(e.item.equals(tabItem2))
					indDetail.activate();
				else if(e.item.equals(tabItem3))
					pkDetail.activate();
				else if(e.item.equals(tabItem4)) 
					fkDetail.activate();
				else if(e.item.equals(tabItem5))
					previewDetail.activate();
				else if(e.item.equals(tabItem6))
					rowCountDetail.activate();
				else if(panels!=null){
					int index=tabFolder.indexOf((TabItem)e.item);
					panels[index-6].activate();
				}
					
			}
			public void widgetDefaultSelected(SelectionEvent e){}
		} );
		
		slm.createControl(overParent);
		gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		slm.getControl().setLayoutData(gid);	
		overParent.layout();
	
	}
	/**
	 * Returns the sLayout.
	 * @return StackLayout
	 */
	public StackLayout getSLayout() {
		return sLayout;
	}

	/**
	 * Returns the store.
	 * @return PreferenceStore
	 */
	public IPreferenceStore getStore() {
		return store;
	}

	/**
	 * Sets the sLayout.
	 * @param sLayout The sLayout to set
	 */
	public void setSLayout(StackLayout sLayout) {
		this.sLayout = sLayout;
	}
	/**
	 * 
	 */
	public void clear() {
		sLayout.topControl=null;
		parent.layout();
		
	}

}
