/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import com.metamatrix.core.util.ArgCheck;

/**
 * The <code>ResourceChangeUtilities</code> class contains utility methods for use with
 * {@link org.eclipse.core.resources.IResourceChangeEvent}s.
 */
public class ResourceChangeUtilities {

    public static boolean isProjectRenamed( IResourceChangeEvent theEvent ) {
        boolean result = false;
        IResourceDelta delta = theEvent.getDelta();

        if (delta != null) {
            IResourceDelta[] deltas = delta.getAffectedChildren();

            if (isRename(theEvent, deltas) && isProject(deltas[0])) {
                result = true;
            }
        }

        return result;
    }

    public static boolean isRename( IResourceChangeEvent theEvent,
                                    IResourceDelta[] deltas ) {
        ArgCheck.isNotNull(theEvent);
        ArgCheck.isNotNull(deltas);

        boolean result = false;
        if (isPreEvent(theEvent) || isAutoBuild(theEvent)) {
            return result;
        }

        if (deltas.length == 2) {
            final IResourceDelta d1 = deltas[0];
            final IResourceDelta d2 = deltas[1];
            // the new name is added and moved from
            // the old name is removed and moved to
            // however, the order is unpredictable
            result = ((isAdded(d1) && isMovedFrom(d1) && isRemoved(d2) && isMovedTo(d2)) || (isAdded(d2) && isMovedFrom(d2)
                                                                                             && isRemoved(d1) && isMovedTo(d1)));
        }
        return result;
    }

    public static boolean isAutoBuild( IResourceChangeEvent theEvent ) {
        ArgCheck.isNotNull(theEvent);
        return (isPreAutoBuild(theEvent) || isPostAutoBuild(theEvent));
    }

    public static boolean isPreEvent( IResourceChangeEvent event ) {
        if (event == null) {
            return false;
        }

        if (isPreAutoBuild(event)) {
            return true;
        }

        if (isPreClose(event)) {
            return true;
        }

        if (isPreDelete(event)) {
            return true;
        }

        return false;
    }

    public static boolean isProjectClosing( IResourceChangeEvent theEvent ) {
        IResource resource = theEvent.getResource();
        if (resource instanceof IProject) {
            return isPreClose(theEvent);
        }
        return false;
    }

    public static void debug( IResourceChangeEvent theEvent ) {
        if (theEvent == null) {
            return;
        }

        System.out.println("********************event=" + System.identityHashCode(theEvent)); //$NON-NLS-1$
        System.out.println("resource=" + theEvent.getResource()); //$NON-NLS-1$
        System.out.println("isPre=" + isPreEvent(theEvent)); //$NON-NLS-1$
        System.out.println("delta=" + theEvent.getDelta()); //$NON-NLS-1$
        System.out.println("isPostAutoBuild=" + isPostAutoBuild(theEvent)); //$NON-NLS-1$
        System.out.println("isPostChange=" + isPostChange(theEvent)); //$NON-NLS-1$
        System.out.println("isPreAutoBuild=" + isPreAutoBuild(theEvent)); //$NON-NLS-1$

        if (theEvent.getDelta() != null) {
            System.out.println("affected children count=" + theEvent.getDelta().getAffectedChildren().length); //$NON-NLS-1$
            try {
                theEvent.getDelta().accept(new DebugDeltaVisitor());
            } catch (CoreException theException) {
            }
        }

        System.out.println();
    }

    public static boolean isPostAutoBuild( IResourceChangeEvent theEvent ) {
        return (theEvent.getType() == IResourceChangeEvent.POST_BUILD);
    }

    public static boolean isPostChange( IResourceChangeEvent theEvent ) {
        return (theEvent.getType() == IResourceChangeEvent.POST_CHANGE);
    }

    public static boolean isPreAutoBuild( IResourceChangeEvent theEvent ) {
        return (theEvent.getType() == IResourceChangeEvent.PRE_BUILD);
    }

    public static boolean isPreClose( IResourceChangeEvent theEvent ) {
        return (theEvent.getType() == IResourceChangeEvent.PRE_CLOSE);
    }

    public static boolean isPreDelete( IResourceChangeEvent theEvent ) {
        return (theEvent.getType() == IResourceChangeEvent.PRE_DELETE);
    }

    public static boolean isAdded( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getKind() == IResourceDelta.ADDED);
    }

    public static boolean isChanged( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getKind() == IResourceDelta.CHANGED);
    }

    public static boolean isContentChanged( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getKind() == IResourceDelta.CHANGED)
               && ((theDelta.getFlags() & org.eclipse.core.resources.IResourceDelta.CONTENT) != 0);
    }

    public static boolean isDescriptionChange( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.DESCRIPTION) != 0;
    }

    public static boolean isMarkersChange( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.MARKERS) != 0;
    }

    public static boolean isFile( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getResource().getType() == IResource.FILE);
    }

    public static boolean isFolder( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getResource().getType() == IResource.FOLDER);
    }

    public static boolean isMovedFrom( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.MOVED_FROM) != 0;
    }

    public static boolean isMovedTo( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.MOVED_TO) != 0;
    }

    public static boolean isOpened( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.OPEN) != 0;
    }

    public static boolean isProject( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getResource().getType() == IResource.PROJECT);
    }

    public static boolean isRemoved( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getKind() == IResourceDelta.REMOVED);
    }

    public static boolean isReplaced( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.REPLACED) != 0;
    }

    public static boolean isTypeChange( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.TYPE) != 0;
    }

    public static boolean isSynchChange( IResourceDelta theDelta ) {
        if (theDelta == null) {
            return false;
        }

        return (theDelta.getFlags() & IResourceDelta.SYNC) != 0;
    }

    static class DebugDeltaVisitor implements IResourceDeltaVisitor {

        /**
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        public boolean visit( IResourceDelta theDelta ) {
            System.out.println("resource=" + theDelta.getResource()); //$NON-NLS-1$
            System.out.println("kind=" + theDelta.getKind()); //$NON-NLS-1$
            System.out.println("flags=" + theDelta.getFlags()); //$NON-NLS-1$
            System.out.println("isAdded=" + isAdded(theDelta)); //$NON-NLS-1$
            System.out.println("isChanged=" + isChanged(theDelta)); //$NON-NLS-1$
            System.out.println("isContentChanged=" + isContentChanged(theDelta)); //$NON-NLS-1$
            System.out.println("isDescriptionChange=" + isDescriptionChange(theDelta)); //$NON-NLS-1$
            System.out.println("isFile=" + isFile(theDelta)); //$NON-NLS-1$
            System.out.println("isFolder=" + isFolder(theDelta)); //$NON-NLS-1$
            System.out.println("isMovedFrom=" + isMovedFrom(theDelta)); //$NON-NLS-1$
            System.out.println("isMovedTo=" + isMovedTo(theDelta)); //$NON-NLS-1$
            System.out.println("isOpened=" + isOpened(theDelta)); //$NON-NLS-1$
            System.out.println("isProject=" + isProject(theDelta)); //$NON-NLS-1$
            System.out.println("isRemoved=" + isRemoved(theDelta)); //$NON-NLS-1$
            System.out.println("isReplaced=" + isReplaced(theDelta)); //$NON-NLS-1$
            System.out.println("isTypeChange=" + isTypeChange(theDelta)); //$NON-NLS-1$
            System.out.println("-----\n\n"); //$NON-NLS-1$
            return true;
        }

    }
}
