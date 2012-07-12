/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;

/**
 * FakeNotificationListener
 */
public class FakeNotificationListener implements ModelWorkspaceNotificationListener {
    
    private int addCnt;
    
    private int removeCnt;
    
    private int moveCnt;
    
    private int renameCnt;    
    
    private int closingCnt;    
    
    private int openCnt;    
    

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyAdd(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyAdd(ModelWorkspaceNotification notification) {
        addCnt++;
    }
    
    int addCount() {
        return addCnt;
    }
    
    int removeCount() {
        return removeCnt;
    }
    
    int moveCount() {
        return moveCnt;
    }
    
    int renameCount() {
        return renameCnt;
    }    
    
    int closingCount() {
        return closingCnt;
    }    
    
    int openCount() {
        return openCnt;
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyRemove(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyRemove(ModelWorkspaceNotification notification) {
        removeCnt++;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyMove(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyMove(ModelWorkspaceNotification notification) {
        moveCnt++;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyRename(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyRename(ModelWorkspaceNotification notification) {
        renameCnt++;
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChange(ModelWorkspaceNotification notification) {

    }
    
    /** 
     * @see org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyReloaded(org.teiid.designer.core.workspace.ModelWorkspaceNotification)
     * @since 4.2
     */
    public void notifyReloaded(ModelWorkspaceNotification notification) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification notification) {

    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof FakeNotificationListener) {
            FakeNotificationListener listener = (FakeNotificationListener) other;
            if(this.addCnt == listener.addCnt && this.moveCnt == listener.moveCnt && this.removeCnt == listener.removeCnt && this.renameCnt == listener.renameCnt) {
                return true;
            }            
        }
        return false;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyClosing(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyClosing(ModelWorkspaceNotification notification) {
        closingCnt++;

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyOpen(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyOpen(ModelWorkspaceNotification notification) {
        openCnt++;
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.workspace.ModelWorkspaceNotificationListener#notifyClean(org.eclipse.core.resources.IProject)
     */
    public void notifyClean(IProject proj) {
    	
    }

}
