/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.layout.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.draw2d.geometry.Rectangle;

import com.metamatrix.modeler.diagram.ui.connection.NodeConnectionModel;
import com.metamatrix.modeler.diagram.ui.layout.LayoutNode;
import com.metamatrix.modeler.diagram.ui.model.DiagramModelNode;
import com.metamatrix.modeler.diagram.ui.util.DiagramUiUtilities;

public class Spring {
    private double[] delta;
    private double[] deltaX;
    private double[] deltaY;
    private double[] deltaXX;
    private double[] deltaYY;
    private double[] deltaXY;
    private double[][] partialDeltaX = null;
    private double[][] partialDeltaY = null;
    private double[][] partialDeltaZ = null;
    private double[][] KL = null;
    private int linkCount;
    private boolean[] fixed = null;
    private NodeConnectionModel[][] Connections = null;
    public double[] centerX = null;
    public double[] centerY = null;
    private LayoutNode[] springNodes;
    private int nbnodes_;
    private double _epsilonFactor = 1.5;
    private double _edgeLength = 1000.0;
    private int _repaintPeriod = 1;
    private boolean _autoEdgeLength = true;
    //    private boolean _fixSelected = true;
    private boolean _widthIgnored = false;
    private boolean _heightIgnored = false;
    private Rectangle _bounds = null;
    private HashMap _nodeConstraints = null;
    private WeakHashMap _linkConstraints = null;

    private SpringLayout layoutManager = null;

    /**
     * Construct an instance of Spring.
     * 
     */
    public Spring(SpringLayout newLayoutManager) {
        super();
        layoutManager = newLayoutManager;
    }

    public void setRectangle(Rectangle rectangle2d) {
        _bounds = rectangle2d;
    }

    public Rectangle setRectangle() {
        return _bounds;
    }

    public void setEdgeLength(double d) {
        _edgeLength = d;
    }

    public double getEdgeLength() {
        return _edgeLength;
    }

    public void setAutoEdgeLength(boolean bool) {
        _autoEdgeLength = bool;
    }

    public boolean getAutoEdgeLength() {
        return _autoEdgeLength;
    }

    //    public void setFixSelected(boolean bool) {
    //        _fixSelected = bool;
    //    }
    //
    //    public boolean getFixSelected() {
    //        return _fixSelected;
    //    }

    public void setWidthIgnored(boolean bool) {
        _widthIgnored = bool;
    }

    public boolean isWidthIgnored() {
        return _widthIgnored;
    }

    public void setHeightIgnored(boolean bool) {
        _heightIgnored = bool;
    }

    public boolean isHeightIgnored() {
        return _heightIgnored;
    }

    public void setRepaintPeriod(int i) {
        _repaintPeriod = i;
    }

    public int getRepaintPeriod() {
        return _repaintPeriod;
    }

    public void setEpsilon(double d) {
        _epsilonFactor = d;
    }

    public double getEpsilon() {
        return _epsilonFactor;
    }

    public void setNodeConstraints(HashMap hashmap) {
        _nodeConstraints = hashmap;
    }

    public HashMap getNodeConstraints() {
        return _nodeConstraints;
    }

    public void setLinkConstraints(WeakHashMap weakhashmap) {
        _linkConstraints = weakhashmap;
    }

    public WeakHashMap getLinkConstraints() {
        return _linkConstraints;
    }

    public String compute(LayoutNode[] theSpringNodes, int i) {
        springNodes = theSpringNodes;

        nbnodes_ = i;

        if (i == 0)
            return "There's no component to layout"; //$NON-NLS-1$

        double[] ds = new double[i];
        double[] ds_0_ = new double[i];
        boolean[] bools = new boolean[i];

        centerX = ds;
        centerY = ds_0_;
        fixed = bools;

        for (int i_2_ = 0; i_2_ < i; i_2_++) {
            LayoutNode nextNode = springNodes[i_2_];
            ds[i_2_] = nextNode.getCenterX();
            ds_0_[i_2_] = nextNode.getCenterY();
        }

        makeConnections();

        if (!findDistances())
            return "Error:  This algorithm should not be run on a non-connected graph!"; //$NON-NLS-1$

        boolean bool = true;
        for (int i_3_ = 0; bool && i_3_ < i; i_3_++)
            bool &= bools[i_3_];

        if (bool)
            return "All the components are fixed"; //$NON-NLS-1$

        find_l_and_k();

        calculateDelta();

        double[] ds_4_ = delta;
        int[] is = new int[i];
        double d_Epsilon = _epsilonFactor * (i + linkCount);
        int i_5_ = 0;
        int i_6_ = 0;
        int i_7_ = 0;
        for (;;) {
            double d_8_ = 0.0;
            for (int i_9_ = 0; i_9_ < i; i_9_++) {
                double d_10_ = ds_4_[i_9_];
                int i_11_ = is[i_9_];
                if (d_10_ > d_8_)
                    d_8_ = d_10_;
                if (i_11_ > i_5_)
                    i_5_ = i_11_;
            }
            d_8_ = 1.0 / d_8_;
            int i_12_ = 0;
            double d_13_ = 0.0;
            double d_14_ = 1.0 / i_5_;
            for (int i_15_ = i_12_; i_15_ < i; i_15_++) {
                double d_16_ =
                    (i_5_ != 0
                        ? 0.5 * (ds_4_[i_15_] * d_8_ + 1.0 - is[i_15_] * d_14_)
                        : ds_4_[i_15_] * d_8_);
                if (d_16_ > d_13_) {
                    i_12_ = i_15_;
                    d_13_ = d_16_;
                }
            }
            if (ds_4_[i_12_] <= d_Epsilon || i_6_ > 100)
                break;
            if (!bools[i_12_]) {
                for (int i_17_ = 0; ds_4_[i_12_] > d_Epsilon && i_17_ < 10; i_17_++)
                    MoveToNewPosition(i_12_);
            }

            if (++i_7_ == springNodes.length) {
                i_7_ = 0;
                if (_repaintPeriod > 0 && i_6_ % _repaintPeriod == _repaintPeriod - 1)
                    setPositions();
                i_6_++;
            }
            is[i_12_]++;
        }

        partialDeltaX = null;
        partialDeltaY = null;
        partialDeltaZ = null;
        KL = null;
        delta = null;
        deltaX = null;
        deltaY = null;
        deltaXX = null;
        deltaYY = null;
        deltaXY = null;

        return null;
    }

    private void makeConnections() {
        Hashtable hashtable = new Hashtable();

        DiagramModelNode[] allDiagramNodes =
            DiagramUiUtilities.getNodeArray(layoutManager.getDiagramNode().getChildren());

        // Make connections List
        List allConnections = new ArrayList();
        for (int iNode = 0; iNode < allDiagramNodes.length; iNode++) {
            allConnections.addAll(allDiagramNodes[iNode].getSourceConnections());
        }
        
        int nSpringNodes = springNodes.length;
        
        for (int iNode = 0; iNode < nSpringNodes; iNode++) {
            hashtable.put(springNodes[iNode].getModelNode().getModelObject(), new Integer(iNode));
        }

        // Set up Connection Array                
        NodeConnectionModel[][] connectionArray = new NodeConnectionModel[nSpringNodes][nSpringNodes];
        linkCount = 0;

        NodeConnectionModel nextConnection = null;
        
        Iterator iter = allConnections.iterator();
        
        while( iter.hasNext() ) {

            nextConnection = (NodeConnectionModel)iter.next();

            Object sourceNode = hashtable.get(((DiagramModelNode)nextConnection.getSourceNode()).getModelObject());

            if (sourceNode != null) {
                Object targetNode = hashtable.get(((DiagramModelNode)nextConnection.getTargetNode()).getModelObject());

                if (targetNode != null) {
                    int sourceId = ((Integer)sourceNode).intValue();
                    int targetId = ((Integer)targetNode).intValue();
                    connectionArray[sourceId][targetId] = nextConnection;
                    connectionArray[targetId][sourceId] = nextConnection;
                    
                    linkCount++;
                }
            }
        }
        
        Connections = connectionArray;
    }

    private void setPositions() {
        for (int i = 0; i < nbnodes_; i++)
            springNodes[i].setCenterXY((int)centerX[i], (int)centerY[i]);
    }

    private boolean findDistances() {
        LayoutNode[] theNodes = springNodes;
        int i = nbnodes_;
        boolean[] bools = fixed;
        double[] ds = new double[i];
        double[][] ds_24_ = new double[i][i];
        if (_widthIgnored) {
            if (_heightIgnored) {
                for (int i_25_ = 0; i_25_ < i; i_25_++)
                    ds[i_25_] = 0.5;
            } else {
                for (int i_26_ = 0; i_26_ < i; i_26_++)
                    ds[i_26_] = theNodes[i_26_].getHeight() * 0.7;
            }
        } else if (_heightIgnored) {
            for (int i_27_ = 0; i_27_ < i; i_27_++)
                ds[i_27_] = theNodes[i_27_].getWidth() * 0.7;
        } else {
            for (int i_28_ = 0; i_28_ < i; i_28_++) {
                LayoutNode nextNode = theNodes[i_28_];
                ds[i_28_] = ((nextNode.getWidth() + nextNode.getHeight()) * 0.25);
            }
        }
        for (int i_29_ = 0; i_29_ < i; i_29_++) {
            Object modelObject = theNodes[i_29_].getModelNode().getModelObject();
            SpringNodeConstraints springnodeconstraints =
                ((SpringNodeConstraints)_nodeConstraints.get(modelObject));
            if (springnodeconstraints != null) {
                ds[i_29_] += springnodeconstraints.getWeight();
                bools[i_29_] |= springnodeconstraints.isFixed();
            }
        }

        for (int i_30_ = 0; i_30_ < i; i_30_++) {
            for (int i_31_ = i_30_ + 1; i_31_ < i; i_31_++) {
                NodeConnectionModel connectionModel = Connections[i_30_][i_31_];
                if (connectionModel != null) {
                    SpringLinkConstraints springlinkconstraints =
                        ((SpringLinkConstraints)_linkConstraints.get(connectionModel));
                    if (springlinkconstraints != null)
                        ds_24_[i_30_][i_31_] += springlinkconstraints.getWeight();
                    ds_24_[i_31_][i_30_] = ds_24_[i_30_][i_31_];
                }
            }
        }

        boolean[] bools_32_ = new boolean[i];
        boolean[] bools_33_ = new boolean[i];
        int[] is = new int[linkCount << 1];
        double[][] ds_34_ = new double[i][i];

        //        int[][] is_35_ = new int[i][i];

        for (int i_36_ = 0; i_36_ < i; i_36_++) {
            System.arraycopy(bools_33_, 0, bools_32_, 0, i);
            int i_37_ = 0;
            int i_38_ = 0;
            bools_32_[i_36_] = true;
            double[] ds_39_ = ds_34_[i_36_];
            for (int i_40_ = 0; i_40_ < i; i_40_++) {
                if (Connections[i_36_][i_40_] != null) {
                    is[i_37_++] = i_40_;
                    is[i_37_++] = i_36_;
                    bools_32_[i_40_] = true;
                }
            }
            while (i_37_ > i_38_) {
                int i_41_ = is[i_38_++];
                int i_42_ = is[i_38_++];
                ds_39_[i_41_] = (ds_34_[i_42_][i_36_] + ds_24_[i_41_][i_42_] + ds[i_41_] + ds[i_42_]);
                ds_34_[i_41_][i_36_] = ds_39_[i_41_];
                for (int i_43_ = 0; i_43_ < nbnodes_; i_43_++) {
                    //                    LayoutNode nextNode = theNodes[i_43_];
                    if (Connections[i_41_][i_43_] != null && !bools_32_[i_43_]) {
                        is[i_37_++] = i_43_;
                        is[i_37_++] = i_41_;
                        bools_32_[i_43_] = true;
                    }
                }
            }
        }
        
        boolean bool = true;
        
        for (int i_44_ = 0; i_44_ < i; i_44_++) {
            double[] ds_45_ = ds_34_[i_44_];
            for (int i_46_ = i_44_ + 1; i_46_ < i; i_46_++) {
                if (ds_45_[i_46_] == 0.0) {
                    bool = false;
                    ds_45_[i_46_] = 1.7976931348623157E308;
                    ds_45_[i_44_] = 1.7976931348623157E308;
                }
            }
        }
        KL = ds_34_;
        partialDeltaZ = ds_24_;
        Connections = null;
        return bool;
    }

    private void find_l_and_k() {
        int i = nbnodes_;
        double[][] ds = KL;
        double d = 0.0;
        double d_47_ = ds[0][0];
        for (int i_48_ = 0; i_48_ < i; i_48_++) {
            double[] ds_49_ = ds[i_48_];
            for (int i_50_ = i_48_ + 1; i_50_ < i; i_50_++) {
                double d_51_ = ds_49_[i_50_];
                if (d_47_ < d_51_ && d_51_ < 1.7976931348623157E308)
                    d_47_ = d_51_;
                d += d_51_;
            }
        }
        d /= i;
        double d_52_;
        if (_autoEdgeLength && _bounds != null)
            d_52_ = (Math.sqrt(_bounds.width * _bounds.height / 2.2) / d_47_);
        else
            d_52_ = _edgeLength / d_47_;
        for (int i_53_ = 0; i_53_ < i; i_53_++) {
            double[] ds_54_ = ds[i_53_];
            for (int i_55_ = i_53_ + 1; i_55_ < i; i_55_++) {
                double d_56_ = ds_54_[i_55_];
                if (d_56_ < 1.7976931348623157E308)
                    ds[i_55_][i_53_] = d * d / (d_56_ * d_56_);
                else
                    ds[i_55_][i_53_] = 0.0;
                ds_54_[i_55_] *= d_52_;
            }
        }
    }

    private void calculateDelta() {
        int i = nbnodes_;
        double[] ds = centerX;
        double[] ds_57_ = centerY;
        double[] ds_58_ = new double[i];
        double[] ds_59_ = new double[i];
        double[] ds_60_ = new double[i];
        double[] ds_61_ = new double[i];
        double[] ds_62_ = new double[i];
        double[] ds_63_ = new double[i];
        double[][] ds_64_ = new double[i][i];
        double[][] ds_65_ = new double[i][i];
        double[][] ds_66_ = partialDeltaZ;
        double[][] ds_67_ = KL;
        for (int i_68_ = 0; i_68_ < i; i_68_++) {
            double d = 0.0;
            double d_69_ = 0.0;
            double d_70_ = 0.0;
            double d_71_ = 0.0;
            double d_72_ = 0.0;
            double d_73_ = ds[i_68_];
            double d_74_ = ds_57_[i_68_];
            double[] ds_75_ = ds_67_[i_68_];
            double[] ds_76_ = ds_64_[i_68_];
            double[] ds_77_ = ds_65_[i_68_];
            double[] ds_78_ = ds_66_[i_68_];
            for (int i_79_ = 0; i_79_ < i_68_; i_79_++) {
                d += ds_76_[i_79_];
                d_69_ += ds_77_[i_79_];
                d_70_ += ds_64_[i_79_][i_68_];
                d_71_ += ds_65_[i_79_][i_68_];
                d_72_ += ds_66_[i_79_][i_68_];
            }
            for (int i_80_ = i_68_ + 1; i_80_ < i; i_80_++) {
                double d_81_ = d_73_ - ds[i_80_];
                double d_82_ = d_74_ - ds_57_[i_80_];
                double d_83_ = d_81_ * d_81_;
                double d_84_ = d_82_ * d_82_;
                double d_85_ = d_83_ + d_84_;
                double d_86_ = Math.sqrt(d_85_);
                double d_87_ = ds_67_[i_80_][i_68_];
                double d_88_ = d_87_ * ds_75_[i_80_] / d_86_;
                double d_89_ = d_88_ / d_85_;
                double d_90_ = d_87_ - d_88_;
                double d_91_ = d_81_ * d_90_;
                double d_92_ = d_82_ * d_90_;
                double d_93_ = d_87_ - d_89_ * d_84_;
                double d_94_ = d_87_ - d_89_ * d_83_;
                double d_95_ = d_89_ * d_81_ * d_82_;
                d += d_91_;
                d_69_ += d_92_;
                d_70_ += d_93_;
                d_71_ += d_94_;
                d_72_ += d_95_;
                ds_64_[i_80_][i_68_] = -d_91_;
                ds_65_[i_80_][i_68_] = -d_92_;
                ds_76_[i_80_] = d_93_;
                ds_77_[i_80_] = d_94_;
                ds_78_[i_80_] = d_95_;
            }
            ds_58_[i_68_] = Math.sqrt(d * d + d_69_ * d_69_);
            ds_59_[i_68_] = d;
            ds_60_[i_68_] = d_69_;
            ds_61_[i_68_] = d_70_;
            ds_62_[i_68_] = d_71_;
            ds_63_[i_68_] = d_72_;
        }
        delta = ds_58_;
        deltaX = ds_59_;
        deltaY = ds_60_;
        deltaXX = ds_61_;
        deltaYY = ds_62_;
        deltaXY = ds_63_;
        partialDeltaX = ds_64_;
        partialDeltaY = ds_65_;
    }

    private void MoveToNewPosition(int i) {
        int i_96_ = nbnodes_;
        double[][] ds = partialDeltaX;
        double[][] ds_97_ = partialDeltaY;
        double[][] ds_98_ = partialDeltaZ;
        double[][] ds_99_ = KL;
        double[] ds_100_ = centerX;
        double[] ds_101_ = centerY;
        double[] ds_102_ = ds_99_[i];
        double[] ds_103_ = delta;
        double[] ds_104_ = deltaX;
        double[] ds_105_ = deltaY;
        double[] ds_106_ = deltaXX;
        double[] ds_107_ = deltaYY;
        double[] ds_108_ = deltaXY;
        double[] ds_109_ = partialDeltaX[i];
        double[] ds_110_ = partialDeltaY[i];
        double[] ds_111_ = partialDeltaZ[i];
        double d = ds_104_[i];
        double d_112_ = ds_105_[i];
        double d_113_ = ds_106_[i];
        double d_114_ = ds_107_[i];
        double d_115_ = ds_108_[i];
        double d_116_ = 1.0 / (d_113_ * d_114_ - d_115_ * d_115_);
        double d_117_ = ds_100_[i] + (d_112_ * d_115_ - d * d_114_) * d_116_;
        double d_118_ = ds_101_[i] + (d * d_115_ - d_112_ * d_113_) * d_116_;
        ds_100_[i] = d_117_;
        ds_101_[i] = d_118_;
        d = 0.0;
        d_112_ = 0.0;
        d_113_ = 0.0;
        d_114_ = 0.0;
        d_115_ = 0.0;
        for (int i_119_ = 0; i_119_ < i; i_119_++) {
            double d_120_ = d_117_ - ds_100_[i_119_];
            double d_121_ = d_118_ - ds_101_[i_119_];
            double d_122_ = d_120_ * d_120_;
            double d_123_ = d_121_ * d_121_;
            double d_124_ = d_122_ + d_123_;
            double d_125_ = Math.sqrt(d_124_);
            double d_126_ = ds_102_[i_119_];
            double d_127_ = d_126_ * ds_99_[i_119_][i] / d_125_;
            double d_128_ = d_127_ / d_124_;
            double d_129_ = d_126_ - d_127_;
            double d_130_ = d_120_ * d_129_;
            double d_131_ = d_121_ * d_129_;
            double d_132_ = d_126_ - d_128_ * d_123_;
            double d_133_ = d_126_ - d_128_ * d_122_;
            double d_134_ = d_128_ * d_120_ * d_121_;
            double d_135_ = ds_104_[i_119_] + ds_109_[i_119_] - d_130_;
            double d_136_ = ds_105_[i_119_] + ds_110_[i_119_] - d_131_;
            d += d_130_;
            d_112_ += d_131_;
            d_113_ += d_132_;
            d_114_ += d_133_;
            d_115_ += d_134_;
            ds_109_[i_119_] = d_130_;
            ds_110_[i_119_] = d_131_;
            ds[i_119_][i] = d_132_;
            ds_97_[i_119_][i] = d_133_;
            ds_98_[i_119_][i] = d_134_;
            ds_103_[i_119_] = Math.sqrt(d_135_ * d_135_ + d_136_ * d_136_);
            ds_104_[i_119_] = d_135_;
            ds_105_[i_119_] = d_136_;
            ds_106_[i_119_] = ds_106_[i_119_] - partialDeltaX[i_119_][i] + d_132_;
            ds_107_[i_119_] = ds_107_[i_119_] - partialDeltaY[i_119_][i] + d_133_;
            ds_108_[i_119_] = ds_108_[i_119_] - partialDeltaZ[i_119_][i] + d_134_;
        }
        for (int i_137_ = i + 1; i_137_ < i_96_; i_137_++) {
            double d_138_ = d_117_ - ds_100_[i_137_];
            double d_139_ = d_118_ - ds_101_[i_137_];
            double d_140_ = d_138_ * d_138_;
            double d_141_ = d_139_ * d_139_;
            double d_142_ = d_140_ + d_141_;
            double d_143_ = Math.sqrt(d_142_);
            double d_144_ = ds_99_[i_137_][i];
            double d_145_ = d_144_ * ds_102_[i_137_] / d_143_;
            double d_146_ = d_145_ / d_142_;
            double d_147_ = d_144_ - d_145_;
            double d_148_ = d_138_ * d_147_;
            double d_149_ = d_139_ * d_147_;
            double d_150_ = d_144_ - d_146_ * d_141_;
            double d_151_ = d_144_ - d_146_ * d_140_;
            double d_152_ = d_146_ * d_138_ * d_139_;
            double d_153_ = ds_104_[i_137_] - partialDeltaX[i_137_][i] - d_148_;
            double d_154_ = ds_105_[i_137_] - partialDeltaY[i_137_][i] - d_149_;
            d += d_148_;
            d_112_ += d_149_;
            d_113_ += d_150_;
            d_114_ += d_151_;
            d_115_ += d_152_;
            ds[i_137_][i] = -d_148_;
            ds_97_[i_137_][i] = -d_149_;
            ds_109_[i_137_] = d_150_;
            ds_110_[i_137_] = d_151_;
            ds_111_[i_137_] = d_152_;
            ds_103_[i_137_] = Math.sqrt(d_153_ * d_153_ + d_154_ * d_154_);
            ds_104_[i_137_] = d_153_;
            ds_105_[i_137_] = d_154_;
            ds_106_[i_137_] += d_150_ - ds_109_[i_137_];
            ds_107_[i_137_] += d_151_ - ds_110_[i_137_];
            ds_108_[i_137_] += d_152_ - ds_111_[i_137_];
        }
        ds_103_[i] = Math.sqrt(d * d + d_112_ * d_112_);
        ds_104_[i] = d;
        ds_105_[i] = d_112_;
        ds_106_[i] = d_113_;
        ds_107_[i] = d_114_;
        ds_108_[i] = d_115_;
    }

    //    private void CheckPositions() {
    //        boolean bool = false;
    //        while (!bool) {
    //            bool = true;
    //            for (int i = 0; i < nbnodes_; i++) {
    //                double d = centerX[i];
    //                double d_155_ = centerY[i];
    //                for (int i_156_ = i + 1; i_156_ < nbnodes_; i_156_++) {
    //                    double d_157_ = centerX[i_156_];
    //                    double d_158_ = centerY[i_156_];
    //                    if (d == d_157_ && d_155_ == d_158_) {
    //                        double d_159_ = (1.4 * Math.random() - 0.7) * KL[i][i_156_];
    //                        double d_160_ = (1.4 * Math.random() - 0.7) * KL[i][i_156_];
    //                        bool = false;
    //                    }
    //                }
    //            }
    //        }
    //    }
}
