/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

/**
 * DiagramLayout
 */
public class DiagramLayout {
    public static final int SUCCESSFUL = 0;
    private List nodes;
    private int padding = 10;
    private int startX = 0;
    private int startY = 0;
    
    private int vGap = 0;
    private int hGap = 0;
    private int width = 0;
    private int height = 0;
    private int xOrigin = 0;
    private int yOrigin = 0;
    
    
    public DiagramLayout() {
        nodes = new ArrayList();
    }
    
    public DiagramLayout(List newNodes) {
        nodes = new ArrayList(newNodes);
    }
    
    public void add(Object newNode) {
        if( !nodes.contains(newNode) )
            nodes.add(newNode);
    }
    
    public void remove(Object newNode) {
        nodes.remove(newNode);
    }
    
    public List getComponents() {
        return nodes;
    }
    
    protected int run() {
        // Default Implementation
        return SUCCESSFUL;
    }
    
    protected int getComponentCount() {
        return nodes.size();
    }
    
    /**
     * @return current padding between objects
     */
    public int getPadding() {
        return padding;
    }

    /**
     * @return starting x value
     */
    public int getStartX() {
        return startX;
    }

    /**
     * @return starting y value
     */
    public int getStartY() {
        return startY;
    }

    /**
     * @param i
     */
    public void setPadding(int i) {
        padding = i;
    }

    /**
     * @param i
     */
    public void setStartX(int i) {
        startX = i;
    }

    /**
     * @param i
     */
    public void setStartY(int i) {
        startY = i;
    }
    
    public void clear() {
        nodes.clear();
    }
    
    public void setStartLocation(int x, int y ) {
        setStartX(x);
        setStartY(y);
    }
    
    
    public void setOrigin(int x, int y ) {
        setXOrigin(x);
        setYOrigin(y);
    }

    /**
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return
     */
    public int getHGap() {
        return hGap;
    }

    /**
     * @return
     */
    public int getVGap() {
        return vGap;
    }

    /**
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return
     */
    public int getXOrigin() {
        return xOrigin;
    }

    /**
     * @return
     */
    public int getYOrigin() {
        return yOrigin;
    }

    /**
     * @param i
     */
    public void setHeight(int i) {
        height = i;
    }

    /**
     * @param i
     */
    public void setHGap(int i) {
        hGap = i;
    }

    /**
     * @param i
     */
    public void setVGap(int i) {
        vGap = i;
    }

    /**
     * @param i
     */
    public void setWidth(int i) {
        width = i;
    }

    /**
     * @param i
     */
    public void setXOrigin(int i) {
        xOrigin = i;
    }

    /**
     * @param i
     */
    public void setYOrigin(int i) {
        yOrigin = i;
    }
    
    public DiagramModelNode[] getNodeArray() {
        DiagramModelNode[] nodeArray = new DiagramModelNode[getComponentCount()];
        DiagramModelNode nextNode = null;
        Iterator iter = getComponents().iterator();
        int count = 0;
        while( iter.hasNext() ) {
            nextNode = (DiagramModelNode)iter.next();
            nodeArray[count] = nextNode;
            count++;
        }
        return nodeArray;
    }
}
