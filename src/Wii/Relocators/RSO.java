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
    along with Wii Relocator SP.  If not, see <http://www.gnu.org/licenses/>.
 */

//----------------------//
// RSO/SEL files class  //
//----------------------//

package Wii.Relocators;

import java.io.*;
import Wii.IO;

/**
 *
 * @author spln
 */
public class RSO extends Relocator {
    
    public RSO(File in) {
        super(in);
    }   
    
    public Export[] getExports() throws IOException {
        exports = (new Exports(exportsOffset, exportsSize, "RSO")).read();
        exportsCount = exports.length;
        for (int i = 0; i < exportsCount; i++) {
            try {
                exports[i].read(fileLocation);
                exports[i].getName(exportsName, fileLocation);
                exports[i].getRelocation(relocation[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return exports;
    }
    
    public Import[] getImports() throws IOException {
        imports = (new Imports(importsOffset, importsSize, "RSO")).read();
        importsCount = imports.length;
        for (int i = 0; i < importsCount; i++) {
            try {
                imports[i].read(fileLocation);
                imports[i].getName(importsName, fileLocation);
                imports[i].getRelocation(relocation[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return imports;
    }
    
    public String[] getInternalsRelocation() throws IOException {
        return relocation[0].read(fileLocation);
    }
    
    public String[] getExternalsRelocation() throws IOException {
        return relocation[1].read(fileLocation);
    }
    
    //--- Overriden methods
    @Override
    public void load() {
        load(0x00, 0x00);
    }
    
    @Override
    public void load(long pos, long dec) {
        // Load RSO header, sections, relocation tables
        IO file;
        try {           
                //--------------------- HEADER ---------------------//                
                file = new IO(fileLocation, "r");
                file.seek(pos);
                /*0x00 (uint32)*/ identifier       =   file.readUint32();
                /*0x04 (uint32)*/ unknown0x04      =   file.readUint32();
                /*0x08 (uint32)*/ sectionCount     =   file.readUint32();
                /*0x0C (uint32)*/ sectionTable     =   file.readUint32()+dec; 
                /*0x10 (uint32)*/ nameOffset       =   file.readUint32()+dec;
                /*0x14 (uint32)*/ nameSize         =   file.readUint32();
                /*0x18 (uint32)*/ version          =   file.readUint32();
                /*0x1C (uint32)*/ unknown0x1C      =   file.readUint32();

                //--- Line 0x20 could be empty
                /*0x20 (uint8) */ hasProlog        =   file.readUint8();
                /*0x21 (uint8) */ hasEpilog        =   file.readUint8();
                /*0x22 (uint8) */ hasUnresolved    =   file.readUint8();
                /*0x23 (uint8) */ hasBss           =   file.readUint8();
                /*0x24 (uint32)*/ prologOffset     =   file.readUint32()+dec;
                /*0x28 (uint32)*/ epilogOffset     =   file.readUint32()+dec;
                /*0x2C (uint32)*/ unresolvedOffset =   file.readUint32()+dec;
    
                //--- Internals/Externals relocation table (irt/ert)
                relocation = new RelocationTable[2];
                /*0x30 (uint32)*/ relocation[0]    =   file.readRelocationTable(RelocationTable.INTERNALS_RELOCATION, dec);
                /*0x34 (uint32)*/
                /*0x38 (uint32)*/ relocation[1]    =   file.readRelocationTable(RelocationTable.EXTERNALS_RELOCATION, dec);
                /*0x3C (uint32)*/
                
                //--- Imports/Exports
                /*0x40 (uint32)*/ exportsOffset    =   file.readUint32()+dec;
                /*0x44 (uint32)*/ exportsSize      =   file.readUint32();
                /*0x48 (uint32)*/ exportsName      =   file.readUint32()+dec;
                /*0x4C (uint32)*/ importsOffset    =   file.readUint32()+dec;
                /*0x50 (uint32)*/ importsSize      =   file.readUint32();
                /*0x54 (uint32)*/ importsName      =   file.readUint32()+dec;
                
                //--- Sections
                section = new Section[(int)sectionCount];
                for (int i = 0; i < sectionCount; i++) {
                    section[i] = new Section(file.readUint32(), file.readUint32(), dec);
                    switch (i) {
                        case 1: section[1].setType(Section.ASM_SECTION)         ;break;
                        case 2: section[2].setType(Section.CTORS_SECTION)       ;break;
                        case 3: section[3].setType(Section.DTORS_SECTION)       ;break;
                        case 4: section[4].setType(Section.CONSTANTS_SECTION)   ;break;
                        case 5: section[5].setType(Section.OBJECTS_SECTION)     ;break;
                        case 6: section[6].setType(Section.BSS_SECTION)         ;break;
                        default: section[i].setType(i /*Section.UNKNOWN_SECTION*/);
                    }
                }
                
            
                //--- Optional
                file.seek(nameOffset);
                nameString = file.readString((int)nameSize);
                file.close();
                
                //--- Get further
                getInternalsRelocation();
                getExternalsRelocation();
                getExports();
                getImports();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
    
    @Override
    public String dump() {
        String str =    " RSO Dump:  <"         + fileLocation.getName()    + "> "+
                        "%n Generated from:  "  + nameString                +
                        "%n "                   + sectionCount              + " sections ";
        
        //--- Sections Dump
        for (int i = 0; i < sectionCount; i++) {
            str +=  "%n "   + String.format("%02X", i)                          +
                    ":  0x" + String.format("%08X", section[i].sectionOffset)   +
                    " ["    + String.format("%08X", section[i].sectionSize)     + "]";
            switch (i) {
                case 1:     str += " // Assembly"       ;break;
                case 2:     str += " // Constructors"   ;break;
                case 3:     str += " // Destructors"    ;break;
                case 4:     str += " // Constants"      ;break;
                case 5:     str += " // Objects"        ;break;
                case 6:     str += " // Bss"            ;break;
                case 7:     str += " // .dwarf (???)"   ;break;
                case 8:     str += " // .line  (???)"   ;break;
            }
        }
        
        //--- Exports Dump
        str += "%n %n Exports:  ";
        for (int i = 0; i < exports.length; i++) {
            str +=  "%n" + String.format("%08X", i)                 + ":" +
                    String.format("%08X", exports[i].sectionType)   + ":" +
                    String.format("%08X", exports[i].contentOffset) + ":" +
                    exports[i].name                                 + ":";
            // Get Relocations
            for (int j = 0; j < exports[i].r_count; j++) {
                str +=  "%n |--- 0x"                    + String.format("%02X", j)      +
                        ":  "   + "r_offset: 0x"        +
                                    String.format("%08X", exports[i].r_offset[j])       +
                        "  "    + "r_info: (0x000000"   +
                                    String.format("%02X", exports[i].relocationId[j])   +
                        ", 0x"  +   String.format("%02X", exports[i].relocationType[j]) +
                        ")  "   + "r_addend: 0x"        +
                                    String.format("%08X", exports[i].r_addend[j]);
            }           
        }
        
        //--- Imports Dump
        str += "%n %n Imports:  ";
        for (int i = 0; i < imports.length; i++) {
            str +=  "%n" + String.format("%08X", i)                 + ":" +
                    String.format("%08X", imports[i].sectionType)   + ":" +
                    String.format("%08X", imports[i].contentOffset) + ":" +
                    imports[i].name                                 + ":";
            // Get Relocations
            for (int j = 0; j < imports[i].r_count; j++) {
                str +=  "%n |--- 0x"                    + String.format("%02X", j)      +
                        ":  "   + "r_offset: 0x"        +
                                    String.format("%08X", imports[i].r_offset[j])       +
                        "  "    + "r_info: (0x000000"   +
                                    String.format("%02X", imports[i].relocationId[j])   +
                        ", 0x"  +   String.format("%02X", imports[i].relocationType[j]) +
                        ")  "   + "r_addend: 0x"        +
                                    String.format("%08X", imports[i].r_addend[j]);
            }           
        }
        
        //--- Relocation Tables Dump
        for (int i = 0; i < relocation.length; i++) {
            switch (i) {
                case 0:     str += "%n %n Internals Relocation Table:  "   ;break;
                case 1:     str += "%n %n Externals Relocation Table:  "   ;break;
                default:    str += "%n %n Relocation Table N°" + i + ":  " ;break;
            }
            // Get Relocations
            for (int j = 0; j < relocation[i].length; j++) {
                str +=  "%n  r_offset: 0x"      + relocation[i].r_offset[j]     +
                        "  r_info: (0x000000"   + relocation[i].r_info[1][j]    +
                        ", 0x"                  + relocation[i].r_info[2][j]    +
                        ")  r_addend: 0x"       + relocation[i].r_addend[j];
            }
        }
        
        return String.format(str);
    }
    
    @Override
    public String toString() {
        return (nameString != null) ? nameString : "<The Nameless One>";
    }
    
    //--- Variables declaration
    public long unknown0x00, unknown0x04, unknown0x1C;
     
} // [END] class RSO

