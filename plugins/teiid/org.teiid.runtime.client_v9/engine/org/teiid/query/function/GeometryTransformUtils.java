/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.function;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;
import org.teiid.CommandContext;
import org.teiid.core.types.GeometryType;
import org.teiid.runtime.client.Messages;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Wrapper around proj4j library to transform geometries to different coordinate
 * systems (ST_Transform).
 */
public class GeometryTransformUtils {

    /**
     * Convert geometry to a different coordinate system. Geometry must have valid
     * SRID.
     *
     * @param ctx Command context used to lookup proj4 parameters from table.
     * @param geom Geometry to transform.
     * @param srid Target SRID; must exist in SPATIAL_REF_SYS table.
     * @return Reprojected geometry.
     * @throws Exception
     */
    public static GeometryType transform(CommandContext ctx, GeometryType geom, int srid) throws Exception {
        Geometry jtsGeomSrc = GeometryUtils.getGeometry(geom);

        Geometry jtsGeomTgt = transform(ctx, jtsGeomSrc, srid);

        return GeometryUtils.getGeometryType(jtsGeomTgt, srid);
    }

    /**
     * Convert the raw geometry to the target srid coordinate system.
     * @param ctx Command context used to lookup proj4 parameters from table.
     * @param jtsGeomSrc Geometry to transform.
     * @param srid Target SRID; must exist in SPATIAL_REF_SYS table.
     * @return
     * @throws Exception
     */
    static Geometry transform(CommandContext ctx, Geometry jtsGeomSrc, int srid) throws Exception {
        String srcParam = lookupProj4Text(ctx, jtsGeomSrc.getSRID());
        String tgtParam = lookupProj4Text(ctx, srid);

        Geometry jtsGeomTgt = transform(jtsGeomSrc, srcParam, tgtParam);
        return jtsGeomTgt;
    }

    /**
     * Lookup proj4 parameters in SPATIAL_REF_SYS using SRID as key.
     *
     * @param ctx
     * @param srid
     * @return
     * @throws Exception
     */
    public static String lookupProj4Text(CommandContext ctx, int srid) throws Exception {
        /*
         * TODO : Teiid 9.0
         * Designer will need to handle this in a different way since we do not want to
         * query the teiid server just to retrieve the String value for the given srid. We
         * should be able to interrogate the spatial_ref_sys csv file for example.
         */
        throw new UnsupportedOperationException("Geometry lookup facilities disabled at this time"); //$NON-NLS-1$
//        String projText;
//        PreparedStatement pstmt = null;
//        ResultSet rs = null;
//
//        try {
//            TeiidConnection conn = ctx.getConnection();
//            pstmt = conn.prepareStatement("select proj4text from spatial_ref_sys where srid = ?"); //$NON-NLS-1$
//            pstmt.setInt(1, srid);
//            rs = pstmt.executeQuery();
//            if (!rs.next()) {
//                throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID31162, srid));
//            }
//            projText = rs.getString(1);
//        } catch (SQLException e) {
//            throw new TeiidClientException(e, Messages.gs(Messages.TEIID.TEIID31163));
//        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//            } catch (Exception e) {
//                // ignore
//            }
//            try {
//                if (pstmt != null) {
//                    pstmt.close();
//                }
//            } catch (Exception e) {
//                // ignore
//            }
//        }
//
//        return projText;
    }

    /**
     * Convert geometry to different coordinate system given the source/target
     * proj4 parameters. Presumably these were pulled from SPATIAL_REF_SYS.
     *
     * @param geom
     * @param srcParams
     * @param tgtParams
     * @return
     * @throws Exception
     */
    public static Geometry transform(Geometry geom, String srcParams, String tgtParams) throws Exception {

        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory crsFactory = new CRSFactory();

        CoordinateReferenceSystem srcCrs = crsFactory.createFromParameters(null, srcParams);
        CoordinateReferenceSystem tgtCrs = crsFactory.createFromParameters(null, tgtParams);

        CoordinateTransform coordTransform = ctFactory.createTransform(srcCrs, tgtCrs);

        return transformGeometry(coordTransform, geom);
    }

    protected static Geometry transformGeometry(CoordinateTransform ct, Geometry geom) throws Exception {
        if (geom instanceof Polygon) {
            return transformPolygon(ct, (Polygon)geom);
        } else if (geom instanceof Point) {
            return transformPoint(ct, (Point)geom);
        } else if (geom instanceof LinearRing) {
            return transformLinearRing(ct, (LinearRing)geom);
        } else if (geom instanceof LineString) {
            return transformLineString(ct, (LineString)geom);
        } else if (geom instanceof MultiPolygon) {
            return transformMultiPolygon(ct, (MultiPolygon)geom);
        } else if (geom instanceof MultiPoint) {
            return transformMultiPoint(ct, (MultiPoint)geom);
        } else if (geom instanceof MultiLineString) {
            return transformMultiLineString(ct, (MultiLineString)geom);
        } else if (geom instanceof GeometryCollection) {
            return transformGeometryCollection(ct, (GeometryCollection)geom);
        } else {
            throw new Exception(Messages.gs(Messages.TEIID.TEIID31164, geom.getGeometryType()));
        }
    }

    /**
     * Convert proj4 coordinates to JTS coordinates.
     *
     * @param projCoords
     * @return
     */
    protected static Coordinate[] convert(ProjCoordinate[] projCoords) {
        Coordinate[] jtsCoords = new Coordinate[projCoords.length];
        for (int i = 0; i < projCoords.length; ++i) {
            jtsCoords[i] = new Coordinate(projCoords[i].x, projCoords[i].y);
        }
        return jtsCoords;
    }

    /**
     * Convert JTS coordinates to proj4j coordinates.
     *
     * @param jtsCoords
     * @return
     */
    protected static ProjCoordinate[] convert(Coordinate[] jtsCoords) {
        ProjCoordinate[] projCoords = new ProjCoordinate[jtsCoords.length];
        for (int i = 0; i < jtsCoords.length; ++i) {
            projCoords[i] = new ProjCoordinate(jtsCoords[i].x, jtsCoords[i].y);
        }
        return projCoords;
    }

    protected static Coordinate[] transformCoordinates(CoordinateTransform ct, Coordinate[] in) {
        return convert(transformCoordinates(ct, convert(in)));
    }

    protected static ProjCoordinate[] transformCoordinates(CoordinateTransform ct, ProjCoordinate[] in) {
        ProjCoordinate[] out = new ProjCoordinate[in.length];
        for (int i = 0; i < in.length; ++i) {
            out[i] = ct.transform(in[i], new ProjCoordinate());
        }
        return out;
    }

    protected static Polygon transformPolygon(CoordinateTransform ct, Polygon polygon) {
        return polygon.getFactory().createPolygon(transformCoordinates(ct, polygon.getCoordinates()));
    }

    protected static Geometry transformPoint(CoordinateTransform ct, Point point) {
        return point.getFactory().createPoint(transformCoordinates(ct, point.getCoordinates())[0]);
    }

    protected static Geometry transformLinearRing(CoordinateTransform ct, LinearRing linearRing) {
        return linearRing.getFactory().createLinearRing(transformCoordinates(ct, linearRing.getCoordinates()));
    }

    protected static Geometry transformLineString(CoordinateTransform ct, LineString lineString) {
        return lineString.getFactory().createLineString(transformCoordinates(ct, lineString.getCoordinates()));
    }

    protected static Geometry transformMultiPolygon(CoordinateTransform ct, MultiPolygon multiPolygon) {
        Polygon[] polygon = new Polygon[multiPolygon.getNumGeometries()];
        for (int i = 0; i < polygon.length; ++i) {
            polygon[i] = multiPolygon.getFactory().createPolygon(transformCoordinates(ct,
                                                                                      multiPolygon.getGeometryN(i).getCoordinates()));
        }
        return multiPolygon.getFactory().createMultiPolygon(polygon);
    }

    protected static Geometry transformMultiPoint(CoordinateTransform ct, MultiPoint multiPoint) {
        return multiPoint.getFactory().createMultiPoint(transformCoordinates(ct, multiPoint.getCoordinates()));
    }

    protected static Geometry transformMultiLineString(CoordinateTransform ct, MultiLineString multiLineString) {
        LineString[] lineString = new LineString[multiLineString.getNumGeometries()];
        for (int i = 0; i < lineString.length; ++i) {
            lineString[i] = multiLineString.getFactory().createLineString(transformCoordinates(ct,
                                                                                               multiLineString.getGeometryN(i).getCoordinates()));
        }
        return multiLineString.getFactory().createMultiLineString(lineString);
    }

    protected static Geometry transformGeometryCollection(CoordinateTransform ct, GeometryCollection geometryCollection)
        throws Exception {
        Geometry[] geometry = new Geometry[geometryCollection.getNumGeometries()];
        for (int i = 0; i < geometry.length; ++i) {
            geometry[i] = transformGeometry(ct, geometryCollection.getGeometryN(i));
        }
        return geometryCollection.getFactory().createGeometryCollection(geometry);
    }
}
