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

package Wii.PPC;

import Wii.Relocators.*;

/**
 *
 * @author spln
 */
public class Assembly {

    //--- Dummy methods
    public static long solveRelocation(RelocationTable table, int entry) {
        long l      = Long.parseLong(table.r_addend[entry].replaceFirst("0x", ""), 16);
        int type    = Integer.parseInt(table.r_info[2][entry].replaceFirst("0x", ""), 16);
        
        switch (type) {
            case R_PPC_NONE         :
                return 0;
            case R_PPC_ADDR32       :
                return l;
            case R_PPC_ADDR24       :
                return ((l & 0xFF_FF_FF) /4) << 2;
            case R_PPC_ADDR16       :
                return (l <= 0xFF_FF) ? l : 0;
            case R_PPC_ADDR16_LO    :
                return l & 0xFF_FF;
            case R_PPC_ADDR16_HI    :
                return l >> 16;
            case R_PPC_ADDR16_HA    :
                return (l >> 16) + 0x80_00;
            case R_PPC_ADDR14: case 0x08: case 0x09:
                return ((l & 0b11_11_11_11_11_11_11) /4) << 2;
            case R_PPC_REL24: default : // ERF ALL OF THESE DON'T WORK...
                return 0;
                
        }       
    }
    
    //--- Variable declaration
    // Relocation type
    public final static int R_PPC_NONE      = 0x00;
    public final static int R_PPC_ADDR32    = 0x01;
    public final static int R_PPC_ADDR24    = 0x02;
    public final static int R_PPC_ADDR16    = 0x03;
    public final static int R_PPC_ADDR16_LO = 0x04;
    public final static int R_PPC_ADDR16_HI = 0x05;
    public final static int R_PPC_ADDR16_HA = 0x06;
    public final static int R_PPC_ADDR14    = 0x07;//~0x09
    public final static int R_PPC_REL24     = 0x0A;
    public final static int R_PPC_REL14     = 0x0B;//~0x0D
    public final static int R_RVL_NONE      = 0xC8;
    public final static int R_RVL_SECT      = 0xC9;
    public final static int R_RVL_STOP      = 0xCA; // Works for REL imports/exports
    public final static int R_RVL_STOP2     = 0xCB; // Works for REL Relocation Table
    
}
