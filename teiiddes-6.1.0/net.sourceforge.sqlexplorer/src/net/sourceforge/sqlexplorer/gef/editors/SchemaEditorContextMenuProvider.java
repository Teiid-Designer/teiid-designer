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

package net.sourceforge.sqlexplorer.gef.editors;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.gef.model.Note;
import net.sourceforge.sqlexplorer.gef.model.Schema;
import net.sourceforge.sqlexplorer.gef.wizards.ReverseDatabaseWizard;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.actions.ActionFactory;

public class SchemaEditorContextMenuProvider extends ContextMenuProvider {
	ActionRegistry actionregistry;
	SchemaEditor editorPart;
	/**
	 * @param viewer
	 */
	public SchemaEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry actionregistry,SchemaEditor editorPart) {
		super(viewer);
		this.actionregistry=actionregistry;
		this.editorPart=editorPart;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
    public void buildContextMenu(IMenuManager imenumanager) {
		GEFActionConstants.addStandardActionGroups(imenumanager);
		
		IAction action;
		
		
		
		action = actionregistry.getAction(ActionFactory.UNDO.getId());
		imenumanager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = actionregistry.getAction(ActionFactory.REDO.getId());
		imenumanager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = actionregistry.getAction(ActionFactory.PASTE.getId());
		if (action!=null && action.isEnabled())
			imenumanager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = actionregistry.getAction(ActionFactory.DELETE.getId());
		if (action!=null &&action.isEnabled())
			imenumanager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = actionregistry.getAction(GEFActionConstants.DIRECT_EDIT);
		if (action!=null && action.isEnabled())
			imenumanager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		action = actionregistry.getAction(GEFActionConstants.ALIGN_LEFT);
		
		MenuManager submenu = new MenuManager(Messages.getString("SchemaEditorContextMenuProvider.Align..._1")); //$NON-NLS-1$
		
		if (action!=null && action.isEnabled())
			submenu.add(action);

		action = actionregistry.getAction(GEFActionConstants.ALIGN_CENTER);
		if (action!=null && action.isEnabled())
			submenu.add(action);

		action = actionregistry.getAction(GEFActionConstants.ALIGN_RIGHT);
		if (action!=null && action.isEnabled())
			submenu.add(action);

		action = actionregistry.getAction(GEFActionConstants.ALIGN_TOP);
		if (action!=null && action.isEnabled())
			submenu.add(action);

		action = actionregistry.getAction(GEFActionConstants.ALIGN_MIDDLE);
		if (action!=null && action.isEnabled())
			submenu.add(action);

		action = actionregistry.getAction(GEFActionConstants.ALIGN_BOTTOM);
		if (action!=null && action.isEnabled())
			submenu.add(action);

		if (!submenu.isEmpty())
			imenumanager.appendToGroup(GEFActionConstants.GROUP_REST, submenu);

		action = actionregistry.getAction(ActionFactory.SAVE.getId());
		imenumanager.appendToGroup(GEFActionConstants.GROUP_SAVE, action);
		
		
		action=actionregistry.getAction(ActionFactory.PRINT.getId());
		if(action!=null)
			imenumanager.appendToGroup(GEFActionConstants.GROUP_PRINT,action);
		
		action=new SelectionAction(editorPart){

			@Override
            protected boolean calculateEnabled() {
				
				return true;
			}
			@Override
            public void run(){	
					
				ReverseDatabaseWizard reverseDatabaseWizard = new ReverseDatabaseWizard(editorPart.getSchema());
				reverseDatabaseWizard.init(SchemaEditorContextMenuProvider.this.editorPart.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection)getSelection());
				WizardDialog resizableWizardDialog = new WizardDialog(SchemaEditorContextMenuProvider.this.editorPart.getSite().getShell(), reverseDatabaseWizard){
					@Override
                    protected int getShellStyle() {
						
						return super.getShellStyle()|SWT.RESIZE;
					}

				};
				
				resizableWizardDialog.open();
				Command command=reverseDatabaseWizard.getCommand();
				execute(command);
			}
			@Override
            public String getText(){
				return Messages.getString("SchemaEditorContextMenuProvider.Reverse_Database_2"); //$NON-NLS-1$
			}
		};
		imenumanager.add(action);
		final Schema sc=editorPart.getSchema();
		action=new SelectionAction(editorPart){

			@Override
            protected boolean calculateEnabled() {
		
				return true;
			}
			@Override
            public void run(){	
			
				Command command=new Command(){
					Note nt=null;
					@Override
                    public void redo(){
						execute();
					}
					@Override
                    public void execute(){
						nt=new Note();
						nt.setLocation(new Point(10,20));
						nt.setSize(new Dimension(80,40));
						nt.setParent(sc);
						sc.addChild(nt);
					}
					@Override
                    public void undo(){
						sc.removeChild(nt);
					}
				};
				execute(command);
			}
			@Override
            public String getText(){
				return Messages.getString("SchemaEditorContextMenuProvider.Add_Note_3"); //$NON-NLS-1$
			}
		};
		imenumanager.add(action);
		action=new SelectionAction(editorPart){
			@Override
            public String getText(){
				return Messages.getString("SchemaEditorContextMenuProvider.Export..._4"); //$NON-NLS-1$
			}
			@Override
            protected boolean calculateEnabled() {
				return true;
			}
			@Override
            public void run(){
				
				FileDialog fd=new FileDialog(editorPart.getGraphicalViewer().getControl().getShell(),SWT.SAVE);
				fd.setFilterExtensions(new String[]{"*.jpg","*.bmp"});//$NON-NLS-1$ //$NON-NLS-2$
				//fd.setFilterNames(new String[]{"bmp"});
				String path=fd.open();
				if(path==null)
					return;
				if(!path.endsWith(".bmp")&& !path.endsWith(".jpg")){//$NON-NLS-1$ //$NON-NLS-2$
					path=path+".jpg";//$NON-NLS-1$
				}
				try{
					GraphicalViewer gv=editorPart.getGraphicalViewer();
					LayerManager lm = (LayerManager)gv.getEditPartRegistry().get(LayerManager.ID);
					IFigure f = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);

					Dimension sz=f.getSize();
					Image image = new Image(gv.getControl().getDisplay(), sz.width, sz.height);
					GC gc = new GC(image);
					SWTGraphics graphics = new SWTGraphics(gc);
					//content.paint(graphics);
					f.paint(graphics);
					graphics.dispose();
					gc.dispose();
					ImageLoader imageLoader = new ImageLoader();
					imageLoader.logicalScreenWidth=sz.width;
					imageLoader.logicalScreenHeight=sz.height;
					imageLoader.data = new ImageData[] { image.getImageData()};
					if(path.endsWith(".bmp"))//$NON-NLS-1$
						imageLoader.save(path, SWT.IMAGE_BMP);
					else if(path.endsWith(".jpg"))//$NON-NLS-1$
						imageLoader.save(path, SWT.IMAGE_JPEG);
					image.dispose();
				}catch(Throwable e){
					SQLExplorerPlugin.error("Error saving bitmap file",e);//$NON-NLS-1$
				}


			}
		};
		imenumanager.add(action);
	}

}
