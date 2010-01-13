/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;

public class TreeLayout {

    static final String CLASS_NAME = "MmTreeLayout"; //$NON-NLS-1$
    public static final int COMPACT_METHOD = 0;
    public static final int SEPARATE_METHOD = 1;

    public static final int LIST_ORDER = 0;
    public static final int POSITION_ORDER = 1;

    public static final int ORIENTATION_ROOT_LEFT = 0;
    public static final int ORIENTATION_ROOT_TOP = 1;
    public static final int ORIENTATION_ROOT_RIGHT = 2;
    public static final int ORIENTATION_ROOT_BOTTOM = 3;

    public static final int ERROR_TREE_HAS_NO_ROOT = 1;
    public static final int ERROR_ROOT_NOT_MANAGED = 2;
    public static final int ERROR_NOT_A_TREE = 3;

    private LayoutNode modelRoot = null;
    private int _depth = -1;
    private int _order = 1;
    private int _orientation = 0;
    private int _method = 0;
    private boolean _useObjectsSizes = false;
    private boolean _fixedSpacing = true;
    private double _fixedXSpacing = 500.0;
    private double _fixedYSpacing = 400.0;
    private transient Hashtable _compToNode;
    private transient TreeNode[] rawTreeNodes;

    private LayoutNode[] nodeArray;
    private int startX = 10;
    private int startY = 10;
    private int startW = 10;
    private int startH = 10;

    public TreeLayout() {
        _compToNode = new Hashtable();
        rawTreeNodes = null;
    }

    public TreeLayout( List newNodes,
                       int startX,
                       int startY,
                       int startW,
                       int startH ) {
        super();
        _compToNode = new Hashtable();
        rawTreeNodes = null;
        this.startX = startX;
        this.startY = startY;
        this.startW = startW;
        this.startH = startH;
        nodeArray = LayoutUtilities.getLayoutNodeArray(newNodes);
    }

    public int run() {

        int i = buildTree();

        if (i != 0) return i;

        TreeNode treenode = (TreeNode)_compToNode.get(modelRoot.getModelNode());
        if (treenode == null) return ERROR_ROOT_NOT_MANAGED;

        TreeNode[] treenodes = new TreeNode[_depth + 1];
        treenode.setNeighbors(treenodes);

        double d = 0.0;
        double d_85_ = 0.0;
        double d_86_ = 0.0;
        double d_87_ = 1.0;
        double d_88_ = 0.0;
        double[] ds = new double[_depth + 1];

        if (_method == 0) {
            double[] ds_89_ = new double[_depth + 1];
            double[] ds_90_ = new double[_depth + 1];
            treenode.calculatePosition(ds_89_, ds_90_);
            treenode.setEven();
            for (int i_91_ = 0; i_91_ <= _depth; i_91_++)
                d_86_ = Math.max(d_86_, ds_90_[i_91_]);
            if (_useObjectsSizes) {
                treenode.getMaximumWidths(ds);
                for (int i_92_ = 0; i_92_ <= _depth; i_92_++) {
                    d_88_ = ds[i_92_];
                    ds[i_92_] += d - 0.5 * d_88_;
                    d += d_88_;
                    d_85_ = Math.max(d_85_, ds_89_[i_92_]);
                }
            }
        } else {
            double[] ds_93_ = new double[1];
            double[] ds_94_ = new double[1];
            treenode.calculateSeparatePosition(ds_93_, ds_94_);
            d_86_ = ds_94_[0];
            if (_useObjectsSizes) {
                treenode.getMaximumWidths(ds);
                for (int i_95_ = 0; i_95_ <= _depth; i_95_++) {
                    d_88_ = ds[i_95_];
                    ds[i_95_] += d - 0.5 * d_88_;
                    d += d_88_;
                }
                d_85_ = ds_93_[0];
            }
        }

        double d_96_ = 0.0;
        double d_97_ = 0.0;
        double d_98_ = startW;
        double d_99_ = startH;
        double d_100_ = startX;
        double d_101_ = startY;
        if (_useObjectsSizes) {
            d_96_ = 1.0;
            d_97_ = 1.0;
        }
        if (_orientation == ORIENTATION_ROOT_TOP || _orientation == ORIENTATION_ROOT_BOTTOM) {
            d_88_ = d_98_;
            d_98_ = d_99_;
            d_99_ = d_88_;
            d_88_ = d_100_;
            d_100_ = d_101_;
            d_101_ = d_88_;
        }
        if (_orientation == ORIENTATION_ROOT_RIGHT || _orientation == ORIENTATION_ROOT_BOTTOM) {
            d_100_ += d_98_;
            d_101_ += d_99_;
            d_96_ = -d_96_;
            d_97_ = -d_97_;
            d_87_ = -1.0;
        }
        if (_fixedSpacing) {
            if (_orientation == ORIENTATION_ROOT_TOP || _orientation == ORIENTATION_ROOT_BOTTOM) {
                d_98_ = d_87_ * _fixedYSpacing;
                d_99_ = d_87_ * _fixedXSpacing;
            } else {
                d_98_ = d_87_ * _fixedXSpacing;
                d_99_ = d_87_ * _fixedYSpacing;
            }
        } else {
            d_98_ = d_87_ * (d_98_ - d) / (_depth + 1);
            d_99_ = d_87_ * (d_99_ - d_85_) / d_86_;
        }
        d_100_ += 0.5 * d_98_;
        d_101_ -= 0.5 * d_99_;
        for (int i_102_ = 0; i_102_ <= _depth; i_102_++)
            ds[i_102_] = d_100_ + d_96_ * ds[i_102_] + d_98_ * i_102_;

        int i_103_ = nodeArray.length;
        if (_orientation == ORIENTATION_ROOT_RIGHT || _orientation == ORIENTATION_ROOT_LEFT) {
            for (int i_104_ = 0; i_104_ < i_103_; i_104_++) {
                LayoutNode modelNode = nodeArray[i_104_];
                treenode = rawTreeNodes[i_104_];
                if (treenode._level != -1) {
                    int newX = (int)ds[treenode._level];
                    int newY = (int)(treenode._absolutePosition * d_97_ + treenode._relativePosition * d_99_ + d_101_);
                    modelNode.setCenterXY(newX, newY);
                }
            }
        } else {
            for (int i_105_ = 0; i_105_ < i_103_; i_105_++) {
                LayoutNode modelNode = nodeArray[i_105_];
                treenode = rawTreeNodes[i_105_];
                if (treenode._level != -1) {
                    int newX = (int)ds[treenode._level];
                    int newY = (int)(treenode._absolutePosition * d_97_ + treenode._relativePosition * d_99_ + d_101_);
                    modelNode.setCenterXY(newY, newX);
                }
            }
        }
        // if (this.getPropagate())
        // this.propagate();

        // 
        LayoutUtilities.justifyAllToCorner(nodeArray);

        return 0;
    }

    private int buildTree() {
        _compToNode.clear();

        int i = nodeArray.length;

        rawTreeNodes = new TreeNode[i];
        for (int iiNode = 0; iiNode < i; iiNode++) {
            LayoutNode modelNode = nodeArray[iiNode];
            rawTreeNodes[iiNode] = new TreeNode(modelNode);
            _compToNode.put(modelNode.getModelNode(), rawTreeNodes[iiNode]);
        }

        if (_order == LIST_ORDER) {
            for (int i_107_ = 0; i_107_ < i; i_107_++)
                rawTreeNodes[i_107_]._orderVar = i_107_;
        } else if (_orientation == ORIENTATION_ROOT_LEFT) {
            for (int i_108_ = 0; i_108_ < i; i_108_++)
                rawTreeNodes[i_108_]._orderVar = nodeArray[i_108_].getCenterY();
        } else if (_orientation == ORIENTATION_ROOT_TOP) {
            for (int i_109_ = 0; i_109_ < i; i_109_++)
                rawTreeNodes[i_109_]._orderVar = nodeArray[i_109_].getCenterX();
        } else if (_orientation == ORIENTATION_ROOT_RIGHT) {
            for (int i_110_ = 0; i_110_ < i; i_110_++)
                rawTreeNodes[i_110_]._orderVar = -nodeArray[i_110_].getCenterY();
        } else {
            for (int i_111_ = 0; i_111_ < i; i_111_++)
                rawTreeNodes[i_111_]._orderVar = -nodeArray[i_111_].getCenterX();
        }

        if (_orientation == ORIENTATION_ROOT_LEFT || _orientation == ORIENTATION_ROOT_RIGHT) {
            for (int i_112_ = 0; i_112_ < i; i_112_++) {
                rawTreeNodes[i_112_]._width = nodeArray[i_112_].getWidth();
                rawTreeNodes[i_112_]._height = nodeArray[i_112_].getHeight();
            }
        } else {
            for (int i_113_ = 0; i_113_ < i; i_113_++) {
                rawTreeNodes[i_113_]._width = nodeArray[i_113_].getHeight();
                rawTreeNodes[i_113_]._height = nodeArray[i_113_].getWidth();
            }
        }

        // Here's where we add links to each node.
        // need to get all connection edit parts, get end components (nodes) and add links to nodes.
        //        
        // lxcomponents = lxcomponents[0].getGraph().getComponents();
        //        
        // for (int iNode = 0; iNode < lxcomponents.length; iNode++) {
        // LayoutNode lxcomponent = lxcomponents[iNode];
        // if (lxcomponent instanceof LxAbstractLink) {
        // LxAbstractLink lxabstractlink = (LxAbstractLink)lxcomponent;
        // TreeNode treenode = (TreeNode)_compToNode.get(lxabstractlink.getHandle1().getComponent());
        // TreeNode currentTreeNode = (TreeNode)_compToNode.get(lxabstractlink.getHandle2().getComponent());
        // if (treenode != null && currentTreeNode != null) {
        // treenode.addLinkToNode(currentTreeNode);
        // currentTreeNode.addLinkToNode(treenode);
        // }
        // }
        // }

        for (int iiNode = 0; iiNode < i; iiNode++) {
            LayoutNode sourceLayoutNode = nodeArray[iiNode];
            DiagramModelNode sourceNode = sourceLayoutNode.getModelNode();
            Vector sourceConnections = sourceNode.getSourceConnections();
            TreeNode sourceTreeNode = (TreeNode)_compToNode.get(sourceNode);
            if (!sourceConnections.isEmpty()) {
                Iterator iter = sourceConnections.iterator();
                NodeConnectionModel nextConnection = null;
                while (iter.hasNext()) {
                    nextConnection = (NodeConnectionModel)iter.next();
                    TreeNode targetTreeNode = (TreeNode)_compToNode.get(nextConnection.getTargetNode());
                    if (sourceTreeNode != null && targetTreeNode != null) {
                        sourceTreeNode.addLinkToNode(targetTreeNode);
                        targetTreeNode.addLinkToNode(sourceTreeNode);
                    }
                }
            }
        }

        if (modelRoot == null) return ERROR_TREE_HAS_NO_ROOT;

        TreeNode treenode = (TreeNode)_compToNode.get(modelRoot.getModelNode());
        if (treenode == null) return ERROR_ROOT_NOT_MANAGED;

        _depth = -1;

        try {
            _depth = treenode.setParent(null);
        } catch (NotATreeException notatreeexception) {
            return ERROR_NOT_A_TREE;
        }

        return 0;
    }

    public boolean getFixedSpacing() {
        return _fixedSpacing;
    }

    public double getFixedXSpacing() {
        return _fixedXSpacing;
    }

    public double getFixedYSpacing() {
        return _fixedYSpacing;
    }

    public int getMethod() {
        return _method;
    }

    public int getOrder() {
        return _order;
    }

    public int getOrientation() {
        return _orientation;
    }

    public LayoutNode getRoot() {
        return modelRoot;
    }

    public boolean getUseObjectsSizes() {
        return _useObjectsSizes;
    }

    public void setFixedSpacing( boolean bool ) {
        _fixedSpacing = bool;
    }

    public void setFixedXSpacing( double d ) {
        _fixedXSpacing = d;
    }

    public void setFixedYSpacing( double d ) {
        _fixedYSpacing = d;
    }

    public void setMethod( int i ) {
        _method = i;
    }

    public void setOrder( int i ) {
        _order = i;
    }

    public void setOrientation( int i ) {
        _orientation = i;
    }

    public void setRoot( LayoutNode modelNode ) {
        modelRoot = modelNode;
    }

    public void setUseObjectsSizes( boolean bool ) {
        _useObjectsSizes = bool;
    }

    public void setFinalNodePositions() {
        for (int i = 0; i < nodeArray.length; i++) {
            nodeArray[i].setFinalPosition();
        }
    }

    public int getCurrentWidth() {
        // Walk through springNodes and get the total width
        double currentWidth = 0;
        double nextXPlusW = 0;
        for (int i = 0; i < nodeArray.length; i++) {
            nextXPlusW = (nodeArray[i].getCenterX() + nodeArray[i].getWidth() / 2);
            currentWidth = Math.max(currentWidth, nextXPlusW);
        }

        return (int)currentWidth;
    }

    public int getCurrentHeight() {
        return LayoutUtilities.getCurrentHeight(nodeArray);
    }

    private class TreeNode {
        int _level;
        double _orderVar;
        double _width;
        double _height;
        double _absolutePosition;
        private double _absoluteSpaceLeft;
        double _relativePosition;
        private double _relativeSpaceLeft;
        // private final LayoutNode _modelNode;
        private TreeNode _parent;
        private final Vector _children;
        private TreeNode _upNeighbor;
        private TreeNode _downNeighbor;
        private boolean _upSibling;
        private boolean _downSibling;

        public TreeNode( LayoutNode modelNode ) {
            _level = -1;
            _orderVar = 0.0;
            _absolutePosition = 0.0;
            _absoluteSpaceLeft = 0.0;
            _relativePosition = 0.0;
            _relativeSpaceLeft = 0.0;
            // _modelNode = modelNode;
            _parent = null;
            _children = new Vector();
            _upNeighbor = null;
            _downNeighbor = null;
            _upSibling = false;
            _downSibling = false;
            _width = 0.0;
            _height = 0.0;
        }

        public void calculatePosition( double[] ds,
                                       double[] ds_0_ ) {
            int i = _children.size();
            if (i == 0) {
                _absolutePosition = ds[_level] + 0.5 * _height;
                ds[_level] += _height;
                _relativePosition = ds_0_[_level] + 1.0;
                ds_0_[_level] = _relativePosition;
            } else {
                double d = 0.0;
                double d_1_ = 0.0;
                for (int i_2_ = 0; i_2_ < i; i_2_++) {
                    TreeNode treenode_3_ = (TreeNode)_children.get(i_2_);
                    treenode_3_.calculatePosition(ds, ds_0_);
                    d = treenode_3_._absolutePosition;
                    d_1_ = treenode_3_._relativePosition;
                }
                int[] is = new int[1];
                double[] ds_4_ = new double[2];
                for (int i_5_ = i - 2; i_5_ >= 0; i_5_--) {
                    TreeNode treenode_6_ = (TreeNode)_children.get(i_5_);
                    is[0] = _level;
                    treenode_6_.calculateSpaceDown(is, ds_4_);
                    double d_7_ = ds_4_[0];
                    double d_8_ = ds_4_[1] - 1.0;
                    if (d_7_ < 0.0) d_7_ = 0.0;
                    if (d_8_ < 0.0) d_8_ = 0.0;
                    if (d_7_ > 0.0 || d_8_ > 0.0) {
                        treenode_6_.shiftDown(ds, ds_0_, d_7_, d_8_);
                        treenode_6_._absoluteSpaceLeft = d_7_;
                        treenode_6_._relativeSpaceLeft = d_8_;
                    }
                }
                TreeNode treenode_9_ = (TreeNode)_children.get(0);
                _absolutePosition = 0.5 * (treenode_9_._absolutePosition + d);
                _relativePosition = 0.5 * (treenode_9_._relativePosition + d_1_);
                double d_10_ = treenode_9_._absoluteSpaceLeft;
                double d_11_ = treenode_9_._relativeSpaceLeft;
                for (int i_12_ = 1; i_12_ < i && treenode_9_._absoluteSpaceLeft > 0.0; i_12_++) {
                    treenode_9_._absoluteSpaceLeft -= d_10_;
                    if (treenode_9_._absoluteSpaceLeft < 0.0) treenode_9_._absoluteSpaceLeft = 0.0;
                    treenode_9_ = (TreeNode)_children.get(i_12_);
                }
                treenode_9_ = (TreeNode)_children.get(0);
                for (int i_13_ = 1; i_13_ < i && treenode_9_._relativeSpaceLeft > 0.0; i_13_++) {
                    treenode_9_._relativeSpaceLeft -= d_11_;
                    if (treenode_9_._relativeSpaceLeft < 0.0) treenode_9_._relativeSpaceLeft = 0.0;
                    treenode_9_ = (TreeNode)_children.get(i_13_);
                }
                double d_14_ = ds[_level] + 0.5 * _height - _absolutePosition;
                double d_15_ = ds_0_[_level] + 1.0 - _relativePosition;
                if (d_14_ < 0.0) d_14_ = 0.0;
                if (d_15_ < 0.0) d_15_ = 0.0;
                if (d_14_ > 0.0 || d_15_ > 0.0) shiftDown(ds, ds_0_, d_14_, d_15_);
                else {
                    ds[_level] = _absolutePosition + 0.5 * _height;
                    ds_0_[_level] = _relativePosition;
                }
            }
        }

        public void calculateSeparatePosition( double[] ds,
                                               double[] ds_16_ ) {
            int i = _children.size();
            if (i == 0) {
                _absolutePosition = ds[0] + 0.5 * _height;
                ds[0] += _height;
                _relativePosition = ds_16_[0] + 1.0;
                ds_16_[0] = _relativePosition;
            } else {
                double d = ds[0];
                double d_17_ = 0.0;
                double d_18_ = 0.0;
                for (int i_19_ = 0; i_19_ < i; i_19_++) {
                    TreeNode treenode_20_ = (TreeNode)_children.get(i_19_);
                    treenode_20_.calculateSeparatePosition(ds, ds_16_);
                    d_17_ = treenode_20_._absolutePosition;
                    d_18_ = treenode_20_._relativePosition;
                }
                TreeNode treenode_21_ = (TreeNode)_children.get(0);
                _absolutePosition = 0.5 * (treenode_21_._absolutePosition + d_17_);
                _relativePosition = 0.5 * (treenode_21_._relativePosition + d_18_);
                d -= _absolutePosition - 0.5 * _height;
                if (d > 0.0) shiftSeparateDown(ds, d);
                ds[0] = Math.max(_absolutePosition + 0.5 * _height, ds[0]);
            }
        }

        private void calculateSpaceDown( int[] is,
                                         double[] ds ) {
            double d = -1.0;
            double d_22_ = -1.0;
            if (_level > is[0]) {
                is[0] = _level;
                if (_downNeighbor != null) {
                    d = (_downNeighbor._absolutePosition - _absolutePosition - 0.5 * (_height + _downNeighbor._height));
                    d_22_ = _downNeighbor._relativePosition - _relativePosition;
                    if (d <= 0.0 && d_22_ <= 1.0) {
                        ds[0] = d;
                        ds[1] = d_22_;
                        return;
                    }
                }
            }
            int i = _children.size();
            for (int i_23_ = i - 1; i_23_ >= 0; i_23_--) {
                TreeNode treenode_24_ = (TreeNode)_children.get(i_23_);
                treenode_24_.calculateSpaceDown(is, ds);
                if (ds[0] >= 0.0 && (ds[0] < d || d < 0.0)) d = ds[0];
                if (ds[1] >= 1.0 && (ds[1] < d_22_ || d_22_ < 0.0)) d_22_ = ds[1];
                if (d <= 0.0 && d_22_ <= 1.0) {
                    ds[0] = d;
                    ds[1] = d_22_;
                    return;
                }
            }
            ds[0] = d;
            ds[1] = d_22_;
        }

        private double absoluteSetEven( double d,
                                        int i ) {
            double d_25_ = 0.0;
            double d_26_ = Math.min(d, _absoluteSpaceLeft);
            if (_downSibling && _downNeighbor._absoluteSpaceLeft > 0.0) d_25_ = _downNeighbor.absoluteSetEven(d_26_, i + 1);
            d_25_ += (d_26_ - d_25_) / (i + 1);
            if (d_25_ > 0.0) shiftUpAbsolute(d_25_);
            _absoluteSpaceLeft = 0.0;
            return d_25_;
        }

        private double relativeSetEven( double d,
                                        int i ) {
            double d_27_ = 0.0;
            double d_28_ = Math.min(d, _relativeSpaceLeft);
            if (_downSibling && _downNeighbor._relativeSpaceLeft > 0.0) d_27_ = _downNeighbor.relativeSetEven(d_28_, i + 1);
            d_27_ += (d_28_ - d_27_) / (i + 1);
            if (d_27_ > 0.0) shiftUpRelative(d_27_);
            _relativeSpaceLeft = 0.0;
            return d_27_;
        }

        void getMaximumWidths( double[] ds ) {
            ds[_level] = Math.max(ds[_level], _width);
            int i = _children.size();
            for (int i_29_ = 0; i_29_ < i; i_29_++)
                ((TreeNode)_children.get(i_29_)).getMaximumWidths(ds);
        }

        public void setEven() {
            int i = _children.size();
            for (int i_30_ = 0; i_30_ < i; i_30_++) {
                TreeNode treenode_31_ = (TreeNode)_children.get(i_30_);
                treenode_31_.setEven();
                if (treenode_31_._absoluteSpaceLeft > 0.0) treenode_31_.absoluteSetEven(treenode_31_._absoluteSpaceLeft, 1);
                if (treenode_31_._relativeSpaceLeft > 0.0) treenode_31_.relativeSetEven(treenode_31_._relativeSpaceLeft, 1);
            }
        }

        public void addLinkToNode( TreeNode treenode_32_ ) {
            int i = _children.size();
            int i_33_;
            for (i_33_ = 0; i_33_ < i && (((TreeNode)_children.get(i_33_))._orderVar < treenode_32_._orderVar); i_33_++) {
                /* empty */
            }
            if (i_33_ >= i || (TreeNode)_children.get(i_33_) != treenode_32_) _children.add(i_33_, treenode_32_);
        }

        public void setNeighbors( TreeNode[] treenodes ) {
            if (treenodes[_level] != null) {
                _upNeighbor = treenodes[_level];
                _upSibling = _upNeighbor._parent == _parent;
                _upNeighbor._downNeighbor = this;
                _upNeighbor._downSibling = _upSibling;
            }
            treenodes[_level] = this;
            int i = _children.size();
            for (int i_34_ = 0; i_34_ < i; i_34_++)
                ((TreeNode)_children.get(i_34_)).setNeighbors(treenodes);
        }

        public int setParent( TreeNode treenode_35_ ) throws NotATreeException {
            if (_parent != null) throw new NotATreeException();
            _parent = treenode_35_;
            if (treenode_35_ != null) _level = treenode_35_._level + 1;
            else _level = 0;
            _children.remove(treenode_35_);
            int i = _children.size();
            int i_36_ = _level;
            for (int i_37_ = 0; i_37_ < i; i_37_++)
                i_36_ = Math.max(i_36_, ((TreeNode)_children.get(i_37_)).setParent(this));
            return i_36_;
        }

        private void shiftDown( double[] ds,
                                double[] ds_47_,
                                double d,
                                double d_48_ ) {
            _absolutePosition += d;
            _relativePosition += d_48_;
            ds[_level] = Math.max(ds[_level], _absolutePosition + 0.5 * _height);
            ds_47_[_level] = Math.max(ds_47_[_level], _relativePosition);
            int i = _children.size();
            for (int i_49_ = 0; i_49_ < i; i_49_++)
                ((TreeNode)_children.get(i_49_)).shiftDown(ds, ds_47_, d, d_48_);
        }

        private void shiftSeparateDown( double[] ds,
                                        double d ) {
            _absolutePosition += d;
            ds[0] = Math.max(ds[0], _absolutePosition + 0.5 * _height);
            int i = _children.size();
            for (int i_50_ = 0; i_50_ < i; i_50_++)
                ((TreeNode)_children.get(i_50_)).shiftSeparateDown(ds, d);
        }

        private void shiftUpAbsolute( double d ) {
            _absolutePosition -= d;
            int i = _children.size();
            for (int i_51_ = 0; i_51_ < i; i_51_++)
                ((TreeNode)_children.get(i_51_)).shiftUpAbsolute(d);
        }

        private void shiftUpRelative( double d ) {
            _relativePosition -= d;
            int i = _children.size();
            for (int i_52_ = 0; i_52_ < i; i_52_++)
                ((TreeNode)_children.get(i_52_)).shiftUpRelative(d);
        }
    }

    private static class NotATreeException extends Exception {
        /**
         */
        private static final long serialVersionUID = 1L;

        public NotATreeException() {
            /* empty */
        }
    }
}
