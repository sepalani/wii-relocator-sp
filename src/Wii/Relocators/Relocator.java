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

//------------------------------------------//
// This class is a kind of template,        //
// use it to make a new child class         //
// when you want to load a relocator file   //
// such as *.rel, *.rso, *.sel files.       //
//------------------------------------------//

package Wii.Relocators;

import java.io.*;

/**
 *
 * @author spln
 */
public class Relocator {
    
    public Relocator(File in) {
        fileLocation = in;
    }    
    
    //--- Override these methods
    public void load() {
        // Method to load a relocator
        // and assign values to variables
    }
    
    public void load(long offset, long dec) {
        // Method to load a relocator
        // from the offset with addresses
        // rectification (dec) and assign
        // values to variables.
        
        // Useful when you open a file
        // within a memory dump.
    }
    
    public String dump() {
        // Method to return a dump
        // of the relocator.
        
        return "";
    }
    
    //--- Variables declaration
    public File fileLocation;
    public Long identifier, version;
    public String nameString;
    public long nameOffset, nameSize;
    
    public Section[] section;
    public long sectionCount, sectionTable;
    public int hasEpilog, hasProlog, hasUnresolved, hasBss;
    public long epilogOffset, prologOffset, unresolvedOffset;
    public RelocationTable[] relocation;
    
    public Import[] imports;
    public Export[] exports;
    public long importsOffset, importsSize, importsName,
                exportsOffset, exportsSize, exportsName;
    public int  importsCount, exportsCount;
    
    //----------------------------------------------//
    // You may want to use the PPC package,         //
    // in order to add some constant values or      //
    // relocation methods.                          //
    //----------------------------------------------//
    
} // [END] class Relocator