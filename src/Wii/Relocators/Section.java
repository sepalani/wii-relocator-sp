/** Copyright (C) 2013  SPLN (sepalani)
    This file is part of Wii Relocator SP.

    Wii Relocator SP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Wii Relocator SP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RsoTool.  If not, see <http://www.gnu.org/licenses/>.
 */

//----------------------//
// Section class        //
//----------------------//

package Wii.Relocators;

import java.io.*;

/**
 *
 * @author spln
 */
public class Section {
    
    public Section(long offset, long size, int type) {
        sectionOffset   = offset;
        sectionSize     = size;
        sectionType     = type;        
    }
    
    public Section(long offset, long size) {
        sectionOffset   = offset;
        sectionSize     = size;
        sectionType     = UNKNOWN_SECTION; 
    }
    
    public Section(long offset, long size, int type, long dec) {
        sectionOffset   = (offset+dec >= 0) ? offset+dec : 0;
        sectionSize     = size;
        sectionType     = type;        
    }
    
    public Section(long offset, long size, long dec) {
        sectionOffset   = (offset+dec >= 0) ? offset+dec : 0;
        sectionSize     = size;
        sectionType     = UNKNOWN_SECTION; 
    }
    
    public void setType(int type) {
        this.sectionType = type;
    }
    
    public int getType() {
        return this.sectionType;
    }
    
    public void extract(File in, File out) throws IOException {
        if (in.length() < this.sectionOffset+this.sectionSize) {
            throw new IOException("File doesn't match with sectionOffset/Size!");
        } else if (!in.exists() || !in.isFile()) {
            throw new IOException("Can't access input file!");
        } else if (out.exists()) {
            throw new IOException("Output file already exists!");
        }
        
        FileInputStream fis = new FileInputStream(in);
        BufferedInputStream bis = new BufferedInputStream(fis);
        FileOutputStream fos = new FileOutputStream(out);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        
        // If section isn't empty
        if (sectionSize > 0) {
            bis.skip(this.sectionOffset);
            for (int i=0; i < this.sectionSize; i++) {
                bos.write(bis.read());
            }
        }
        
        bos.close();
        bis.close();
        
    }
    
    @Override
    public String toString() {
        switch (sectionType) {
            case ASM_SECTION        : return "Assembly";
            case CTORS_SECTION      : return "Constructors";
            case DTORS_SECTION      : return "Destructors";
            case CONSTANTS_SECTION  : return "Constants";
            case OBJECTS_SECTION    : return "Objects";
            case BSS_SECTION        : return "Bss";
            default : return "Section";                             
        }
    }
    
    //--- Section Type
    public static final int UNKNOWN_SECTION    = 0x00;
    public static final int ASM_SECTION        = 0x01;
    public static final int CTORS_SECTION      = 0x02;
    public static final int DTORS_SECTION      = 0x03;
    public static final int CONSTANTS_SECTION  = 0x04;
    public static final int OBJECTS_SECTION    = 0x05;
    public static final int BSS_SECTION        = 0x06;
    
    //--- Variables declaration
    public final long sectionOffset, sectionSize;
    public int sectionType;
    
} // [END] class Section
