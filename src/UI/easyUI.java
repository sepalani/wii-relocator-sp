/** Copyright (C) 2013  SPLN (sepalani)
    This file is part of Wii Relocation SP.

    RsoTool is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RsoTool is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Wii Relocator SP.  If not, see <http://www.gnu.org/licenses/>.
 */
package UI;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author spln
 */
public class easyUI {
    
    public static java.io.File getOpenFilename(String[] desc, String[] filter) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        
        if (desc.length == filter.length) {
            for (int i = 0; i < desc.length; i++) {
                fc.addChoosableFileFilter(addFileFilter(desc[i], filter[i]));
            }   
        }
        fc.setAcceptAllFileFilterUsed(true);
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }     
    }
    
    public static java.io.File getSaveFilename(String[] desc, String[] filter) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        
        if (desc.length == filter.length) {
            for (int i = 0; i < desc.length; i++) {
                fc.addChoosableFileFilter(addFileFilter(desc[i], filter[i]));
            }   
        }
        fc.setAcceptAllFileFilterUsed(true);
        
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }     
    }
    
    public static java.io.File getDirectory() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        } else {
            return null;
        }   
    }
    
    public static FileFilter addFileFilter(String desc, String ext) {
        final String d; d = desc;
        final String[] e; e = ext.split("\\|");
        return new FileFilter() {
            @Override
            public String getDescription() {
                return d;
            }
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    for (int i = 0; i < e.length; i++) {
                        if (f.getName().toLowerCase().endsWith(e[i]) || e[i].equals("")) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        };
    }
    
    //--- Easy MessageBox
    public final static int MBIcon_PLAIN        = JOptionPane.PLAIN_MESSAGE;
    public final static int MBIcon_INFORMATION  = JOptionPane.INFORMATION_MESSAGE;
    public final static int MBIcon_WARNING      = JOptionPane.WARNING_MESSAGE;
    public final static int MBIcon_ERROR        = JOptionPane.ERROR_MESSAGE;
    public final static int MBIcon_QUESTION     = JOptionPane.QUESTION_MESSAGE;
    
    public static void messageBox(Component frame, Object message) {
        JOptionPane.showMessageDialog(frame, message);
    }
    
    public static void messageBoxExt(Component frame, Object message, String title, int icon) {
        JOptionPane.showMessageDialog(frame, message, title, icon);
    }
    
    public static String getString(Component frame, String mess, String def) {
        return JOptionPane.showInputDialog(frame, mess, def);
    }
    
    public static String getStringExt(Component frame, String mess, String title, int icon) {
        return JOptionPane.showInputDialog(frame, mess, title, icon);
    }
    
    //--- Parser
    public final static String BIN_PREFIX   = "0b";
    public final static String OCT_PREFIX   = "0o";
    public final static String HEX_PREFIX   = "0x";
    
    public static String[] parseStringRadix(String str) {
        //  Handle " "/"_"
        str = str.replaceAll(" ", "");
        str = str.replaceAll("_", "");
        
        //  Handle sign
        int seek = 0;
        String radix, sign = "";
        if (str.charAt(0) == '-') {
            sign = "-";
            seek += 1;
        }
        
        //  Handle radix
        radix = str.substring(seek, seek+2);
        switch (radix) {
            case BIN_PREFIX:  
                return new String[] { sign + str.substring(seek+2), "2"  };
            case OCT_PREFIX:  
                return new String[] { sign + str.substring(seek+2), "8"  };
            case HEX_PREFIX:  
                return new String[] { sign + str.substring(seek+2), "16" };
            default:  
                return new String[] { sign + str,                   "10" };
        }
    }
    
    public static short parseShort(String str) {
        String[] get = parseStringRadix(str);
        return Short.parseShort(get[0], Integer.parseInt(get[1]));
    }
    
    public static int parseInteger(String str) {
        String[] get = parseStringRadix(str);
        return Integer.parseInt(get[0], Integer.parseInt(get[1]));
    }
    
    public static long parseLong(String str) {
        String[] get = parseStringRadix(str);
        return Long.parseLong(get[0], Integer.parseInt(get[1]));
    }
}
