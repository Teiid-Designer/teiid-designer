/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.internal.Category;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datatools.ui.dialogs.ConnectionProfileTreeProvider;

public class GlobalConnectionTreeProvider extends ConnectionProfileTreeProvider implements IToolTipProvider {
	GlobalConnectionManager manager;
	/**
    * This provider provides content and label information for a combination of connection profiles and
    * default server connections (data sources & resource adapters)
    */
   public GlobalConnectionTreeProvider(GlobalConnectionManager manager) {
       super();
       this.manager = manager;
   }

	@Override
	public Object[] getElements(Object inputElement) {
		if( inputElement instanceof GlobalConnectionManager ) {
			return ((GlobalConnectionManager)inputElement).getRootNodes();
		}
		
		return super.getElements(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if( parentElement instanceof RootConnectionNode ) {
			RootConnectionNode node = (RootConnectionNode)parentElement;
			if( node.isProfile() ) {
				Object[] profileChildren = super.getChildren(ProfileManager.getInstance());
				// Remove the Connection Profile Repositories node
				Collection<Object> result = new ArrayList<Object>();
				for( Object obj : profileChildren ) {
					if( !((Category)obj).getName().equals(UiConstants.CONNECTION_PROFILE_REPOSITORIES)) {
						result.add(obj);
					}
				}
				return result.toArray(new Object[result.size()]);
			} else {
				return manager.getDataSources();
			}
		} else if( parentElement instanceof ICategory ) {
			String categoryName = ((ICategory)parentElement).getName();
			if( categoryName.equals(UiConstants.TEIID_CONNECTIONS) ) {
				// Add teiid folders
				return manager.getTeiidFolders((ICategory)parentElement);
			}
		} else if( parentElement instanceof TeiidConnectionFolder ) {
			// Find profiles of this particular type
			TeiidConnectionFolder folder = (TeiidConnectionFolder)parentElement;
			Object[] allTeiidProfiles =  super.getChildren(folder.getCategory());
			
			return folder.getChildren(allTeiidProfiles);
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return super.getParent(element);
	}

	@Override
	public boolean hasChildren(Object element) {
		if( element instanceof RootConnectionNode ) {
			RootConnectionNode node = (RootConnectionNode)element;
			if( node.isProfile() ) return super.hasChildren(element);
			else {
				return manager.getDataSources().length > 0;
			}
		}
		return super.hasChildren(element);
	}

	@Override
	public String getText(Object element) {
		String text = null;
		if (element instanceof ProfileManager) {
			text = "Profile Manager"; //$NON-NLS-1$
		} else if (element instanceof IConnectionProfile) {
			IConnectionProfile profile = (IConnectionProfile) element;
			text = profile.getName();
		} else if (element instanceof ICategory) {
			text = ((ICategory)element).getName();
			if( text.equals(UiConstants.DATABASE_CONNECTIONS) ) {
				text = "JDBC";
			} else if( text.equals(UiConstants.ODA_CONNECTIONS) ) {
				text = "ODA";
			} else if( text.equals(UiConstants.TEIID_CONNECTIONS) ) {
				text = "Teiid";
			} else if( text.equals(UiConstants.FLAT_FILE_DATA_SOURCE)) {
				text = "Flat File";
			} else if( text.equals(UiConstants.HIVE_DATA_SOURCE)) {
				text = "Hive";
			} else if( text.equals(UiConstants.JDBC_DATA_SOURCE)) {
				text = "JDBC (generic)";
			} else if( text.equals(UiConstants.MONGODB_DATA_SOURCE)) {
				text = "MongoDB";
			} else if( text.equals(UiConstants.WEB_SERVICES_DATA_SOURCE)) {
				text = "Web Services";
			} else if( text.equals(UiConstants.XML_DATA_SOURCE)) {
				text = "XML File";
			}
			return text;
		} if( element instanceof RootConnectionNode ) {
			RootConnectionNode node = (RootConnectionNode)element;
			return node.getName();
		} else if( element instanceof DataSourceItem ) {
			DataSourceItem item = (DataSourceItem)element; 
			return item.getName();
		} else if(element instanceof TeiidConnectionFolder) {
			return ((TeiidConnectionFolder)element).getLabel();
		} else {
			text = super.getText(element);
		}
		
		if (text != null && text.trim().length() > 0) {
			text = TextProcessor.process(text);
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {
		Image image;
		if( element instanceof RootConnectionNode ) {
			RootConnectionNode node = (RootConnectionNode)element;
			if( node.isDataSource() )  {
				image = UiPlugin.getDefault().getImage(UiConstants.IMAGES.DEPLOYED_CONNECTIONS);
			} else {
				image = UiPlugin.getDefault().getImage(UiConstants.IMAGES.LOCAL_CONNECTIONS);
			}
		} else if (element instanceof ICategory || element instanceof TeiidConnectionFolder) {
			image = PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FOLDER);
		} else if (element instanceof IConnectionProfile) {
			image = UiPlugin.getDefault().getImage(UiConstants.IMAGES.CONNECTION);
		} else if( element instanceof DataSourceItem ) {
			image = UiPlugin.getDefault().getImage(UiConstants.IMAGES.TEIID_JDBC_SOURCE);
		} else {
			image = null;
		}
		return image;
	}

	@Override
	public String getToolTipText(Object element) {
		return "< IN WORK .. come back later";
	}
   
   
   
   
}
