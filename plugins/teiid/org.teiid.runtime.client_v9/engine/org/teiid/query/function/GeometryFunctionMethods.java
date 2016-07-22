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

import java.sql.Blob;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.GeometryType;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.annotation.Updated;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.FunctionMethod.PushDown;
import org.teiid.query.function.metadata.FunctionCategoryConstants;
import org.teiid.query.util.CommandContext;
import org.teiid.translator.SourceSystemFunctions;

@Since(Version.TEIID_8_10)
public class GeometryFunctionMethods {

    @TeiidFunction( name = SourceSystemFunctions.ST_ASTEXT, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    @Updated(version=Version.TEIID_8_11)
    public static ClobType asText(GeometryType geometry) throws Exception {
        return GeometryUtils.geometryToClob(geometry, false);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_ASEWKT, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    @Updated(version=Version.TEIID_8_11)
    public static ClobType asEwkt(GeometryType geometry) throws Exception {
        return GeometryUtils.geometryToClob(geometry, true);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_ASBINARY, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static BlobType asBlob(GeometryType geometry) {
        Blob b = geometry.getReference();
        return new BlobType(b);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_ASGEOJSON, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true )
    public static ClobType asGeoJson(GeometryType geometry) throws Exception {
        return GeometryUtils.geometryToGeoJson(geometry);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_ASGML, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true )
    public static ClobType asGml(CommandContext context, GeometryType geometry) throws Exception {
        return GeometryUtils.geometryToGml(context, geometry, true);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_ASKML, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true )
    public static ClobType asKml(CommandContext context, GeometryType geometry) throws Exception {
        return GeometryUtils.geometryToGml(context, geometry, false);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMTEXT, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true )
    public static GeometryType geomFromText(ClobType wkt) throws Exception {
        return GeometryUtils.geometryFromClob(wkt);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMTEXT, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static GeometryType geomFromText(ClobType wkt, int srid) throws Exception {
        return GeometryUtils.geometryFromClob(wkt, srid, false);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMWKB, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, alias = "ST_GEOMFROMBINARY" )
    public static GeometryType geoFromBlob(BlobType wkb) throws Exception {
        return GeometryUtils.geometryFromBlob(wkb);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMWKB, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true, alias = "ST_GEOMFROMBINARY" )
    public static GeometryType geoFromBlob(BlobType wkb, int srid) throws Exception {
        return GeometryUtils.geometryFromBlob(wkb, srid);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMGEOJSON, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true )
    public static GeometryType geomFromGeoJson(ClobType clob) throws Exception {
        return GeometryUtils.geometryFromGeoJson(clob);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMGEOJSON, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true )
    public static GeometryType geomFromGeoJson(ClobType clob, int srid) throws Exception {
        return GeometryUtils.geometryFromGeoJson(clob, srid);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMGML, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true )
    public static GeometryType geomFromGml(ClobType gml) throws Exception {
        return GeometryUtils.geometryFromGml(gml, null);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_GEOMFROMGML, category = FunctionCategoryConstants.GEOMETRY, pushdown = PushDown.CAN_PUSHDOWN, nullOnNull = true )
    public static GeometryType geomFromGml(ClobType gml, int srid) throws Exception {
        return GeometryUtils.geometryFromGml(gml, srid);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_INTERSECTS, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean intersects(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.intersects(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_CONTAINS, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean contains(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.contains(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_CROSSES, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean crosses(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.crosses(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_DISJOINT, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean disjoint(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.disjoint(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_DISTANCE, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Double distance(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.distance(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_OVERLAPS, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean overlaps(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.overlaps(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_TOUCHES, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean touches(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.touches(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_SRID, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static int getSrid(GeometryType geom1) {
        return geom1.getSrid();
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_SETSRID, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static GeometryType setSrid(GeometryType geom1, int srid) {
        GeometryType gt = new GeometryType();
        gt.setReference(geom1.getReference());
        gt.setSrid(srid);
        return gt;
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_EQUALS, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    public static Boolean equals(GeometryType geom1, GeometryType geom2) throws Exception {
        return GeometryUtils.equals(geom1, geom2);
    }

    @TeiidFunction( name = SourceSystemFunctions.ST_TRANSFORM, category = FunctionCategoryConstants.GEOMETRY, nullOnNull = true, pushdown = PushDown.CAN_PUSHDOWN )
    @Since(Version.TEIID_8_11)
    public static GeometryType transform(CommandContext context, GeometryType geom, int srid) throws Exception {
        return GeometryTransformUtils.transform(context, geom, srid);
    }
}
