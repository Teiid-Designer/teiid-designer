package net.sourceforge.sqlexplorer.sessiontree.model;

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

import net.sourceforge.sqlexplorer.SqlexplorerImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Defines the icons for the various ISessionTreeNode objects
 */
public class SessionTreeLabelProvider extends LabelProvider {
	private HashMap imageCache = new HashMap(5);
	private ImageDescriptor rootDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getRootIcon());
	private ImageDescriptor sessionDescriptor=ImageDescriptor.createFromURL(SqlexplorerImages.getSessionIcon());	
	//private ImageDescriptor dbDescriptor=ImageDescriptor.createFromURL(JFaceDbcImages.getOpenDBIcon());
	//private ImageDescriptor historyDescriptor=ImageDescriptor.createFromURL(JFaceDbcImages.getHistory());		
	
	@Override
    public Image getImage (Object element) {
		ImageDescriptor descriptor=null;
		if(element instanceof RootSessionTreeNode)
			descriptor = rootDescriptor;
		else if(element instanceof SessionTreeNode){
			descriptor = sessionDescriptor;
		}
		
		//else if (element instanceof DataBaseSessionTreeNode){
		//	descriptor = dbDescriptor;	
		//}
		Image image = (Image)imageCache.get(descriptor);
		if (image == null) {
			image = descriptor.createImage();
			imageCache.put(descriptor, image);
		}
		return image;
    }

    @Override
    public String getText (Object element) {           
       return element.toString();
    }
    @Override
    public void dispose() {
		for (Iterator i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}
}

