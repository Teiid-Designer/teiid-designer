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
package net.sourceforge.sqlexplorer.plugin.editors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public class SQLEditorInput implements IStorageEditorInput, IPersistableElement {
    private IStorage fStorage;
    File fFile;
    private String fName;
    private SessionTreeNode sessionNode;

    public SQLEditorInput( String name ) {
        fName = name;
        createStorage();
    }

    public SQLEditorInput( File file ) {
        fFile = file;
        createStorage();
    }

    private void createStorage() {
        fStorage = new IStorage() {
            public InputStream getContents() {
                try {
                    return fFile != null ? new FileInputStream(fFile) : getClearStream();
                } catch (IOException e) {
                    return getClearStream();
                }
            }

            private InputStream getClearStream() {
                return new ByteArrayInputStream(new byte[0]);
            }

            public IPath getFullPath() {
                return null;
            }

            public String getName() {
                return SQLEditorInput.this.getName();
            }

            public boolean isReadOnly() {
                return false;
            }

            public Object getAdapter( Class adapter ) {
                return null;
            }
        };
    }

    public boolean exists() {
        return fFile != null ? fFile.exists() : false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        String result = "";

        if (this.fFile == null) {
            if (this.sessionNode == null) {
                if (this.fName != null) {
                    result = this.fName;
                }
            } else {
                result = this.sessionNode.getShortName();
            }
        } else {
            result = this.fFile.getName();
        }

        return result;
    }

    public IPersistableElement getPersistable() {
        return fFile != null ? this : null;
    }

    public String getToolTipText() {
        String result = "";

        if (this.sessionNode != null) {
            result = this.sessionNode.getFullName();
        } else if (this.fFile != null) {
            result = this.fFile.getAbsolutePath();
        } else if (this.fName != null) {
            result = this.fName;
        }

        return result;
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }

    public IStorage getStorage() {
        return fStorage;
    }

    public File getFile() {
        return fFile;
    }

    public String getFactoryId() {
        return "net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInputFactory";
    }

    public void saveState( IMemento memento ) {
        if (fFile == null) return;
        memento.putString("path", fFile.getAbsolutePath());
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) return true;
        if (!(obj instanceof SQLEditorInput)) return false;
        SQLEditorInput input = (SQLEditorInput)obj;

        // return true if the session is the same. the eclipse framework will use the same
        // editor (not open a new one) if it finds an existing editor of the right type having
        // an input equal to the one being opened
        if ((this.sessionNode != null) && (input.getSessionNode() != null)) {
            if (this.sessionNode.getAlias().getName().equals(input.getSessionNode().getAlias().getName())) {
                return true;
            }
        }

        return fFile == null && input.fFile == null ? fName == input.fName || fName != null && fName.equals(input.fName) : fFile == input.fFile
                                                                                                                           || fFile != null
                                                                                                                           && fFile.equals(input.fFile);
    }

    /**
     * @return
     */
    public SessionTreeNode getSessionNode() {
        return sessionNode;
    }

    /**
     * @param node
     */
    public void setSessionNode( SessionTreeNode node ) {
        sessionNode = node;
    }

}
