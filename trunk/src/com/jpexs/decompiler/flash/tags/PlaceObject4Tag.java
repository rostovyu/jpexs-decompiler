/*
 *  Copyright (C) 2010-2013 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.tags;

import com.jpexs.decompiler.flash.Configuration;
import com.jpexs.decompiler.flash.EndOfStreamException;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.SWFInputStream;
import com.jpexs.decompiler.flash.SWFOutputStream;
import com.jpexs.decompiler.flash.abc.CopyOutputStream;
import com.jpexs.decompiler.flash.tags.base.CharacterIdTag;
import com.jpexs.decompiler.flash.tags.base.Container;
import com.jpexs.decompiler.flash.tags.base.PlaceObjectTypeTag;
import com.jpexs.decompiler.flash.types.CLIPACTIONS;
import com.jpexs.decompiler.flash.types.CXFORM;
import com.jpexs.decompiler.flash.types.CXFORMWITHALPHA;
import com.jpexs.decompiler.flash.types.MATRIX;
import com.jpexs.decompiler.flash.types.RGBA;
import com.jpexs.decompiler.flash.types.filters.FILTER;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Same as PlaceObject3Tag except additional AMF data
 *
 * @author JPEXS
 */
public class PlaceObject4Tag extends CharacterIdTag implements Container, PlaceObjectTypeTag {

    /**
     * @since SWF 5 has clip actions (sprite characters only)
     */
    public boolean placeFlagHasClipActions;
    /**
     * Has clip depth
     */
    public boolean placeFlagHasClipDepth;
    /**
     * Has name
     */
    public boolean placeFlagHasName;
    /**
     * Has ratio
     */
    public boolean placeFlagHasRatio;
    /**
     * Has color transform
     */
    public boolean placeFlagHasColorTransform;
    /**
     * Has matrix
     */
    public boolean placeFlagHasMatrix;
    /**
     * Places a character
     */
    public boolean placeFlagHasCharacter;
    /**
     * Defines a character to be moved
     */
    public boolean placeFlagMove;
    /**
     * Has class name or character ID of bitmap to place. If
     * PlaceFlagHasClassName, use ClassName. If PlaceFlagHasCharacter, use
     * CharacterId
     */
    public boolean placeFlagHasImage;
    /**
     * Has class name of object to place
     */
    public boolean placeFlagHasClassName;
    /**
     * Enables bitmap caching
     */
    public boolean placeFlagHasCacheAsBitmap;
    /**
     * Has blend mode
     */
    public boolean placeFlagHasBlendMode;
    /**
     * Has filter list
     */
    public boolean placeFlagHasFilterList;
    /**
     * Has opaque background. SWF 11 and higher.
     */
    public boolean placeFlagOpaqueBackground;
    /**
     * Has visibility flag. SWF 11 and higher.
     */
    public boolean placeFlagHasVisible;
    /**
     * Depth of character
     */
    public int depth;
    /**
     * If PlaceFlagHasClassName or (PlaceFlagHasImage and
     * PlaceFlagHasCharacter), Name of the class to place
     */
    public String className;
    /**
     * If PlaceFlagHasCharacter, ID of character to place
     */
    public int characterId;
    /**
     * If PlaceFlagHasMatrix, Transform matrix data
     */
    public MATRIX matrix;
    /**
     * If PlaceFlagHasColorTransform, Color transform data
     */
    public CXFORMWITHALPHA colorTransform;
    /**
     * If PlaceFlagHasRatio, Ratio
     */
    public int ratio;
    /**
     * If PlaceFlagHasName, Name of character
     */
    public String name;
    /**
     * If PlaceFlagHasClipDepth, Clip depth
     */
    public int clipDepth;
    /**
     * If PlaceFlagHasFilterList, List of filters on this object
     */
    public List<FILTER> surfaceFilterList;
    /**
     * If PlaceFlagHasBlendMode, Blend mode
     */
    public int blendMode;
    /**
     * If PlaceFlagHasCacheAsBitmap, 0 = Bitmap cache disabled, 1-255 = Bitmap
     * cache enabled
     */
    public int bitmapCache;
    /**
     * @since SWF 5 If PlaceFlagHasClipActions, Clip Actions Data
     */
    public CLIPACTIONS clipActions;
    /**
     * If PlaceFlagHasVisible, 0 = Place invisible, 1 = Place visible
     */
    public int visible;
    /**
     * If PlaceFlagHasVisible, Background color
     */
    public RGBA backgroundColor;
    // FIXME bug found in ecoDrive.swf, 
    private boolean bitmapCacheBug;
    private int reserved;
    public static final int ID = 94;
    public byte amfData[];  //TODO: Parse AMF data?

    @Override
    public List<FILTER> getFilters() {
        if (placeFlagHasFilterList) {
            return surfaceFilterList;
        } else {
            return null;
        }
    }

    @Override
    public int getClipDepth() {
        if (placeFlagHasClipDepth) {
            return clipDepth;
        }
        return -1;
    }

    /**
     * Gets data bytes
     *
     * @param version SWF version
     * @return Bytes of data
     */
    @Override
    public byte[] getData(int version) {
        if (Configuration.DISABLE_DANGEROUS) {
            return super.getData(version);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        if (Configuration.DEBUG_COPY) {
            os = new CopyOutputStream(os, new ByteArrayInputStream(super.data));
        }
        SWFOutputStream sos = new SWFOutputStream(os, version);
        try {
            sos.writeUB(1, placeFlagHasClipActions ? 1 : 0);
            sos.writeUB(1, placeFlagHasClipDepth ? 1 : 0);
            sos.writeUB(1, placeFlagHasName ? 1 : 0);
            sos.writeUB(1, placeFlagHasRatio ? 1 : 0);
            sos.writeUB(1, placeFlagHasColorTransform ? 1 : 0);
            sos.writeUB(1, placeFlagHasMatrix ? 1 : 0);
            sos.writeUB(1, placeFlagHasCharacter ? 1 : 0);
            sos.writeUB(1, placeFlagMove ? 1 : 0);
            sos.writeUB(1, reserved);
            sos.writeUB(1, placeFlagOpaqueBackground ? 1 : 0); //SWF11
            sos.writeUB(1, placeFlagHasVisible ? 1 : 0); //SWF11
            sos.writeUB(1, placeFlagHasImage ? 1 : 0);
            sos.writeUB(1, placeFlagHasClassName ? 1 : 0);
            sos.writeUB(1, placeFlagHasCacheAsBitmap ? 1 : 0);
            sos.writeUB(1, placeFlagHasBlendMode ? 1 : 0);
            sos.writeUB(1, placeFlagHasFilterList ? 1 : 0);
            sos.writeUI16(depth);
            if (placeFlagHasClassName) {
                sos.writeString(className);
            }
            if (placeFlagHasCharacter) {
                sos.writeUI16(characterId);
            }
            if (placeFlagHasMatrix) {
                sos.writeMatrix(matrix);
            }
            if (placeFlagHasColorTransform) {
                sos.writeCXFORMWITHALPHA(colorTransform);
            }
            if (placeFlagHasRatio) {
                sos.writeUI16(ratio);
            }
            if (placeFlagHasName) {
                sos.writeString(name);
            }
            if (placeFlagHasClipDepth) {
                sos.writeUI16(clipDepth);
            }
            if (placeFlagHasFilterList) {
                sos.writeFILTERLIST(surfaceFilterList);
            }
            if (placeFlagHasBlendMode) {
                sos.writeUI8(blendMode);
            }
            if (placeFlagHasCacheAsBitmap) {
                if (!bitmapCacheBug) {
                    sos.writeUI8(bitmapCache);
                }
            }
            if (placeFlagHasVisible) {
                sos.writeUI8(visible);
            }
            if (placeFlagOpaqueBackground) {
                sos.writeRGBA(backgroundColor);
            }
            if (placeFlagHasClipActions) {
                sos.writeCLIPACTIONS(clipActions);
            }
            sos.close();
        } catch (IOException ex) {
        }
        return baos.toByteArray();
    }

    /**
     * Constructor
     *
     * @param swf
     * @param data Data bytes
     * @param version SWF version
     * @param pos
     * @throws IOException
     */
    public PlaceObject4Tag(SWF swf, byte data[], int version, long pos) throws IOException {
        super(swf, ID, "PlaceObject4", data, pos);
        SWFInputStream sis = new SWFInputStream(new ByteArrayInputStream(data), version);
        placeFlagHasClipActions = sis.readUB(1) == 1;
        placeFlagHasClipDepth = sis.readUB(1) == 1;
        placeFlagHasName = sis.readUB(1) == 1;
        placeFlagHasRatio = sis.readUB(1) == 1;
        placeFlagHasColorTransform = sis.readUB(1) == 1;
        placeFlagHasMatrix = sis.readUB(1) == 1;
        placeFlagHasCharacter = sis.readUB(1) == 1;
        placeFlagMove = sis.readUB(1) == 1;
        reserved = (int) sis.readUB(1);
        placeFlagOpaqueBackground = sis.readUB(1) == 1; //SWF11
        placeFlagHasVisible = sis.readUB(1) == 1;       //SWF11
        placeFlagHasImage = sis.readUB(1) == 1;
        placeFlagHasClassName = sis.readUB(1) == 1;
        placeFlagHasCacheAsBitmap = sis.readUB(1) == 1;
        placeFlagHasBlendMode = sis.readUB(1) == 1;
        placeFlagHasFilterList = sis.readUB(1) == 1;

        depth = sis.readUI16();
        if (placeFlagHasClassName) {
            className = sis.readString();
        }
        if (placeFlagHasCharacter) {
            characterId = sis.readUI16();
        }
        if (placeFlagHasMatrix) {
            matrix = sis.readMatrix();
        }
        if (placeFlagHasColorTransform) {
            colorTransform = sis.readCXFORMWITHALPHA();
        }
        if (placeFlagHasRatio) {
            ratio = sis.readUI16();
        }
        if (placeFlagHasName) {
            name = sis.readString();
        }
        if (placeFlagHasClipDepth) {
            clipDepth = sis.readUI16();
        }
        if (placeFlagHasFilterList) {
            surfaceFilterList = sis.readFILTERLIST();
        }
        if (placeFlagHasBlendMode) {
            blendMode = sis.readUI8();
        }
        bitmapCacheBug = false;
        if (placeFlagHasCacheAsBitmap) {
            try {
                bitmapCache = sis.readUI8();
            } catch (EndOfStreamException eex) {
                bitmapCacheBug = true;
                bitmapCache = 1;
            }
        }

        if (placeFlagHasVisible) {
            visible = sis.readUI8();
        }
        if (placeFlagOpaqueBackground) {
            backgroundColor = sis.readRGBA();
        }

        if (placeFlagHasClipActions) {
            clipActions = sis.readCLIPACTIONS();
        }
        amfData = sis.readBytes(sis.available());
    }

    /**
     * Returns all sub-items
     *
     * @return List of sub-items
     */
    @Override
    public List<Object> getSubItems() {
        List<Object> ret = new ArrayList<>();
        if (placeFlagHasClipActions) {
            ret.addAll(clipActions.clipActionRecords);
        }
        return ret;
    }

    /**
     * Returns number of sub-items
     *
     * @return Number of sub-items
     */
    @Override
    public int getItemCount() {
        if (!placeFlagHasClipActions) {
            return 0;
        }
        return clipActions.clipActionRecords.size();
    }

    @Override
    public Set<Integer> getNeededCharacters() {
        Set<Integer> ret = new HashSet<>();
        if (placeFlagHasCharacter) {
            ret.add(characterId);
        }
        return ret;
    }

    @Override
    public int getCharacterId() {
        if (placeFlagHasCharacter) {
            return characterId;
        } else {
            return -1;
        }
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public MATRIX getMatrix() {
        if (placeFlagHasMatrix) {
            return matrix;
        } else {
            return null;
        }
    }

    @Override
    public String getInstanceName() {
        if (placeFlagHasName) {
            return name;
        }
        return null;
    }

    @Override
    public CXFORM getColorTransform() {
        return null;
    }

    @Override
    public CXFORMWITHALPHA getColorTransformWithAlpha() {
        if (placeFlagHasColorTransform) {
            return colorTransform;
        } else {
            return null;
        }
    }

    @Override
    public int getBlendMode() {
        return blendMode;
    }

    @Override
    public String getClassName() {
        if (placeFlagHasClassName) {
            return className;
        }
        return null;
    }

    @Override
    public boolean cacheAsBitmap() {
        return placeFlagHasCacheAsBitmap;
    }

    @Override
    public boolean isVisible() {
        if (placeFlagHasVisible) {
            return visible == 1;
        }
        return true;
    }

    @Override
    public RGBA getBackgroundColor() {
        if (placeFlagOpaqueBackground) {
            return backgroundColor;
        }
        return null;
    }

    @Override
    public boolean flagMove() {
        return placeFlagMove;
    }

    @Override
    public int getRatio() {
        if (!placeFlagHasRatio) {
            return -1;
        }
        return ratio;
    }

    @Override
    public void setInstanceName(String name) {
        placeFlagHasName = true;
        this.name = name;
    }

    @Override
    public void setClassName(String className) {
        placeFlagHasClassName = true;
        this.className = className;
    }

    @Override
    public String toString() {
        if (name != null) {
            return super.toString() + " (" + name + ")";
        } else {
            return super.toString();
        }
    }

    @Override
    public CLIPACTIONS getClipActions() {
        if (placeFlagHasClipActions) {
            return clipActions;
        } else {
            return null;
        }
    }
}