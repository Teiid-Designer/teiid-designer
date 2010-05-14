/*
 * Copyright � 2006 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.model;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.sqlexplorer.dbviewer.DetailManager;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.eclipse.swt.widgets.Composite;


/** 
 * @since 5.0.1
 */
public class SystemContainer implements IDbModel {

    private static final String SYSTEM_PREFIX = "System."; 
    
    private ArrayList children = new ArrayList();
    private IDbModel parent;
    private String name = "System";
    
    private HashMap containerMap = new HashMap();
    private SystemContainer systemProcedures;

    /** 
     * 
     * @since 5.0.1
     */
    public SystemContainer(IDbModel s,String schemaPattern, SQLConnection conn) {
        parent = s;
        
        systemProcedures = new SystemContainer(this, "Procedure");
        children.add(systemProcedures);
    }
    
    private SystemContainer(IDbModel s, String name) {
        this.name = name;
        this.parent = s;
    }
    
    public boolean isSystemObject(IDbModel obj) {

        String name = obj.toString();
        if ( name.startsWith(SYSTEM_PREFIX) ) {
            if ( obj instanceof ProcedureNode ) {
                systemProcedures.addChild(obj);
                return true;
            } else if ( obj instanceof TableNode ) {
                if ( obj.toString().startsWith(SYSTEM_PREFIX) ) {
                    String typeName = ((TableNode) obj).getTableInfo().getType(); 
                    SystemContainer container = (SystemContainer) containerMap.get(typeName);
                    if ( container == null ) {
                        container = new SystemContainer(this, typeName);
                        containerMap.put(typeName, container);
                        children.add(container);
                    }
    
                    container.addChild(obj);
                    return true;
                }
            }            
        }
        
        return false;
    }
    
    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getChildren()
     * @since 5.0.1
     */
    public Object[] getChildren() {
        return children.toArray();
    }

    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getComposite(net.sourceforge.sqlexplorer.dbviewer.DetailManager)
     * @since 5.0.1
     */
    public Composite getComposite(DetailManager dm) {
        return null;
    }

    /** 
     * @see net.sourceforge.sqlexplorer.dbviewer.model.IDbModel#getParent()
     * @since 5.0.1
     */
    public Object getParent() {
        return parent;
    }

    /**
     *  
     * @param obj
     * @since 5.0.1
     */
    void addChild(Object obj) {
        this.children.add(obj);
    }
    
    /**
     *  
     * @see java.lang.Object#toString()
     * @since 5.0.1
     */
    @Override
    public String toString() {
        return name;
    }
    
}

