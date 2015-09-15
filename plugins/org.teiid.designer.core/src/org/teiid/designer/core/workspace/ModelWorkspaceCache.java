/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.util.HashMap;
import java.util.Map;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.OverflowingLRUCache;


/**
 * The cache of model elements to their respective info.
 *
 * @since 8.0
 */
public class ModelWorkspaceCache {
//    public static final int PKG_CACHE_SIZE = 500;
    public static final int OPENABLE_CACHE_SIZE = 2000;
    
    /**
     * Active ModelWorkspace
     */
    protected ModelWorkspaceInfo workspaceInfo;
    
    /**
     * Cache of open projects and package fragment roots.
     */
    protected Map<ModelWorkspaceItem, ModelWorkspaceItemInfo> projectAndRootCache;

    /**
     * Cache of open compilation unit and class files
     */
    protected OverflowingLRUCache<ModelWorkspaceItem, ModelWorkspaceItemInfo> openableCache;

    /**
     * Cache of open children of openable ModelWorkspaceItem instances
     */
    protected Map<ModelWorkspaceItem, ModelWorkspaceItemInfo> childrenCache;
    
    public ModelWorkspaceCache() {
        this.projectAndRootCache = new HashMap(50);
//        this.pkgCache = new HashMap(PKG_CACHE_SIZE);
        this.openableCache = new ModelWorkspaceItemCache(OPENABLE_CACHE_SIZE);
        this.childrenCache = new HashMap<ModelWorkspaceItem, ModelWorkspaceItemInfo>(OPENABLE_CACHE_SIZE*20); // average 20 chilren per openable
    }
    
    public int pkgSize() {
        return 0;
//        return this.pkgCache.size();
    }
        
    /**
     *  Returns the info for the element.
     * @param item
     * @return item info
     */
    public ModelWorkspaceItemInfo getInfo(ModelWorkspaceItem item) {
        CoreArgCheck.isNotNull(item);
        switch (item.getItemType()) {
            case ModelWorkspaceItem.MODEL_WORKSPACE:
                return this.workspaceInfo;
            case ModelWorkspaceItem.MODEL_PROJECT:
            case ModelWorkspaceItem.MODEL_FOLDER:
                return this.projectAndRootCache.get(item);
            case ModelWorkspaceItem.MODEL_RESOURCE:
                return this.openableCache.get(item);
            default:
                return this.childrenCache.get(item);
        }
    }
    
    /**
     *  Returns the info for this element without
     *  disturbing the cache ordering.
     */
    protected Object peekAtInfo(ModelWorkspaceItem item) {
        CoreArgCheck.isNotNull(item);
        switch (item.getItemType()) {
            case ModelWorkspaceItem.MODEL_WORKSPACE:
                return this.workspaceInfo;
            case ModelWorkspaceItem.MODEL_PROJECT:
            case ModelWorkspaceItem.MODEL_FOLDER:
                return this.projectAndRootCache.get(item);
//            case ModelWorkspaceItem.MODEL_PACKAGE_FRAGMENT:
//                return this.pkgCache.get(item);
            case ModelWorkspaceItem.MODEL_RESOURCE:
                return this.openableCache.get(item);
            default:
                return this.childrenCache.get(item);
        }
    }
    
    /**
     * Remember the info for the element.
     */
    protected void putInfo(ModelWorkspaceItem item, ModelWorkspaceItemInfo info) {
        CoreArgCheck.isNotNull(item);
        CoreArgCheck.isNotNull(info);
        switch (item.getItemType()) {
            case ModelWorkspaceItem.MODEL_WORKSPACE:
                this.workspaceInfo = (ModelWorkspaceInfo) info;
                break;
            case ModelWorkspaceItem.MODEL_PROJECT:
            case ModelWorkspaceItem.MODEL_FOLDER:
                this.projectAndRootCache.put(item, info);
                break;
            case ModelWorkspaceItem.MODEL_RESOURCE:
                this.openableCache.put(item,info);
                break;
            default:
                this.childrenCache.put(item, info);
        }
    }
    /**
     * Removes the info of the element from the cache.
     */
    protected void removeInfo(ModelWorkspaceItem item) {
        CoreArgCheck.isNotNull(item);
        switch (item.getItemType()) {
            case ModelWorkspaceItem.MODEL_WORKSPACE:
                this.workspaceInfo = null;
                break;
            case ModelWorkspaceItem.MODEL_PROJECT:
            case ModelWorkspaceItem.MODEL_FOLDER:
                this.projectAndRootCache.remove(item);
                break;
//            case ModelWorkspaceItem.MODEL_PACKAGE_FRAGMENT:
//                this.pkgCache.remove(item);
//                break;
            case ModelWorkspaceItem.MODEL_RESOURCE:
                this.openableCache.remove(item);
                break;
            default:
                this.childrenCache.remove(item);
        }
    }
    
    public void clear(){
        childrenCache.clear();
        openableCache.clear();
        projectAndRootCache.clear();
        workspaceInfo = null;
    }
}
