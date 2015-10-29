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
// Export array class   //
//----------------------//

package Wii.Relocators;

import java.util.*;

/**
 *
 * @author spln
 */
public class Exports extends RelocatedData {

    public Exports(long offset, long size, String rel) {
        super(new int[] {});
        this.fileType = rel;
        
        switch (fileType) {
            case relFile:
                this.setEntryProperties(new int[] {4, 4});
                break;
            default:
                this.setEntryProperties(new int[] {4, 4, 4, 4});
        }
        
        this.exportsOffset  = offset;
        this.exportsSize    = size;
    }
    
    public Export[] read() {
        List<Export> list = new ArrayList();
        for (int i = 0, j = 0; j < exportsSize; i++, j += this.entrySize) {
            list.add(new Export(i, exportsOffset+this.entrySize*i, fileType));
        }
        return exp = list.toArray(new Export[list.size()]);
    }
    
    //--- Variables declaration
    public String fileType;
    public long exportsOffset, exportsSize;
    Export[] exp;
    
    // Relocator file
    public final static String rsoFile  = "RSO";
    public final static String relFile  = "REL";
    public final static String selFile  = "SEL"; // same as RSO    
    
}
