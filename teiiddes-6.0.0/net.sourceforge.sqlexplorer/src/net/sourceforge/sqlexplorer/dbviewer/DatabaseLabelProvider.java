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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.dbviewer.model.CatalogNode;
import net.sourceforge.sqlexplorer.dbviewer.model.DatabaseNode;
import net.sourceforge.sqlexplorer.dbviewer.model.SchemaNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableNode;
import net.sourceforge.sqlexplorer.dbviewer.model.TableObjectTypeNode;
import net.sourceforge.sqlexplorer.ext.PluginManager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;



public class DatabaseLabelProvider extends LabelProvider  {

	private Map imageCache = new HashMap(10);
	private Map typeCache=new HashMap(10);
	private ImageDescriptor tableDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getTableIcon());
	private ImageDescriptor viewDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getViewIcon());		
	private ImageDescriptor synDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getSynIcon());				
	private ImageDescriptor otherDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getDatabaseNodeIcon());
    // Defect 20940 Needed icon for XML Documents in Database Tree
    private ImageDescriptor docDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getDocumentNodeIcon());
	
	public DatabaseLabelProvider(PluginManager pm){
		typeCache.put(DatabaseNode.class,otherDescriptor);
		typeCache.put(SchemaNode.class,otherDescriptor);
		typeCache.put(CatalogNode.class,otherDescriptor);
		typeCache.put(TableObjectTypeNode.class,otherDescriptor);
		
		Map map=pm.getImageDescriptorsMap();
		if(map!=null){
			typeCache.putAll(map);
		}
	}
							
    @Override
    public Image getImage (Object element) {
        ImageDescriptor descriptor=null;
        if(element instanceof TableNode)
        {
			TableNode tn=(TableNode)element;        	
        	if(tn.isView()) 
	            descriptor = viewDescriptor;
	        else if(tn.isTable()) 
		        descriptor = tableDescriptor;
		    else if(tn.isSynonym()) 
		        descriptor = synDescriptor;
            else if(tn.isDocument()) {
                // Defect 20940 Needed icon for XML Documents in Database Tree
                descriptor=docDescriptor;
            }
        }
        else{
        	descriptor=(ImageDescriptor)typeCache.get(element.getClass());
        	//System.out.println("descriptor "+element.getClass()+" "+descriptor);
        	if(descriptor==null){
        		descriptor=otherDescriptor;
        	}
        	
        }
            
		if(descriptor!=null){
			Image image = (Image)imageCache.get(descriptor);
			if (image == null) {
				image = descriptor.createImage();
				imageCache.put(descriptor, image);
			}
			return image;
		}
		
		return null;
    }
    @Override
    public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

    @Override
    public String getText (Object element) {
        
        return element.toString();
    }
}    
