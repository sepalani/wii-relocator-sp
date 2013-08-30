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

package UI;

import java.io.*;
import Wii.IO;
import Wii.PPC.PowerPC;
import Wii.Relocators.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 *
 * @author spln
 */
public class RelocationTree extends JTree implements TreeSelectionListener, MouseListener {
    
    public RelocationTree(Relocator rel) {
        //--- Create Nodes
        super(new DefaultMutableTreeNode(rel));
        root    = (DefaultMutableTreeNode) this.getModel().getRoot();
        relFile = (Relocator) root.getUserObject();
        
        sectionsNode = new DefaultMutableTreeNode("Sections");
        for (int i = 0; i < (int)rel.sectionCount; i++) {
            sectionsNode.add(new DefaultMutableTreeNode(rel.section[i]));
        }
        
        internalsNode = new DefaultMutableTreeNode("Internals");
        for (int i = 0; i < rel.relocation[0].r_offset.length; i++) {
            internalsNode.add(new DefaultMutableTreeNode(
                    new RelocationEntry(rel, rel.relocation[0], i)));
        }
        
        externalsNode = new DefaultMutableTreeNode("Externals");
        for (int i = 0; i < rel.relocation[1].r_offset.length; i++) {
            externalsNode.add(new DefaultMutableTreeNode(
                    new RelocationEntry(rel, rel.relocation[1], i)));
        }
        
        root.add(sectionsNode);
        root.add(internalsNode);
        root.add(externalsNode);
        
        //--- Creates Popup Menu
        // Section
        popupSection            = new JPopupMenu("42");
        itemSectionResolve      = new JMenuItem("Resolve Section");
        itemSectionResolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemSectionResolve();
            }
        });
        itemSectionExtractAll   = new JMenuItem("Extract All");
        itemSectionExtractAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemSectionExtractAll();
            }
        });
        popupSection.add(itemSectionResolve);
        popupSection.add(itemSectionExtractAll);
        
        // Entry
        popupEntry                  = new JPopupMenu("21");
        itemEntryExtractFromSection = new JMenuItem("Extract from section");
        itemEntryExtractFromSection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemEntryExtractFromSection();
            }
        });
        itemEntryExtractFromFile    = new JMenuItem("Extract from file");
        itemEntryExtractFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemEntryExtractFromFile();
            }
        });
        popupEntry.add(itemEntryExtractFromSection);
        popupEntry.add(itemEntryExtractFromFile);
        
    }    
    
    public void showMenu(MouseEvent e) {
        JPopupMenu popup;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        if (node.getUserObject().getClass() == RelocationEntry.class) {
            popup   = entryMenu();
        } else if (node.getUserObject().getClass() == Section.class) {
            popup   = sectionMenu();
        } else {
            return;
        }
        
        popup.setInvoker(this);
        popup.show(this, e.getX(), e.getY());
    }
    
    //--- Menu Item
    
    //<editor-fold defaultstate="collapsed" desc="[TREE] Popup Section      - Items Event">  
    public void jMenuItemSectionResolve() {
        // Get section type
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Section section = (Section) node.getUserObject();
        long type = section.sectionType;
        
        // Get exports and adding them
        int count = 0;
        RelocationTable table = relFile.relocation[0];
        for (int i = 0; i < table.r_offset.length; i++) {
            if (Long.parseLong(table.r_info[1][i], 16) == type) {
                node.add(new DefaultMutableTreeNode(
                    new RelocationEntry(relFile, table, i)));
                count++;
            }
        }
        if (count == 0) node.add(new DefaultMutableTreeNode("<null>"));
        easyUI.messageBoxExt(this, "Resolved: "+count, "Resolve Section", eUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemSectionExtractAll() {
        File dir = easyUI.getDirectory();
        if (!dir.exists() || !dir.isDirectory()) {
            easyUI.messageBoxExt(this, "Can't access folder!", "Folder error", easyUI.MBIcon_ERROR);
            return;
        }
        String path = dir.getAbsolutePath() + File.separator;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        DefaultMutableTreeNode[] leaf = new DefaultMutableTreeNode[node.getLeafCount()];
        int count = node.getChildCount();
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                leaf[i] = (DefaultMutableTreeNode) node.getFirstLeaf();
                System.out.println(leaf[0].getUserObject().toString());
                node = leaf[0];
            } else {
                leaf[i] = node.getNextSibling();
                node = leaf[i];
                // System.out.println("I == "+i);
                System.out.println(leaf[i].getUserObject().toString());
            }   
        }
        
        Object nodeInfo = leaf[0].getUserObject();
        try {
            if (nodeInfo.getClass() == Export.class) {
                for (int i = 0; i < leaf.length; i++) {
                    Export exp  = (Export) leaf[i].getUserObject();
                    String name = exp.toString();
                    File out    = new File(path+name+".bin");
                    for (int j = 1; out.exists(); j++) {
                        out = new File(path+name+j+".bin");
                    } 
                    exp.extractFromSection(relFile.section[(int) exp.sectionType],
                            relFile.fileLocation, out);
                }
            } else if (nodeInfo.getClass() == RelocationEntry.class) {
                for (int i = 0; i < leaf.length; i++) {
                    RelocationEntry relEntry  = (RelocationEntry) leaf[i].getUserObject();
                    String name = relEntry.toString();
                    File out    = new File(path+name+".bin");
                    for (int j = 1; out.exists(); j++) {
                        out = new File(path+name+j+".bin");
                    } 
                    relEntry.extractFromSection(relFile.fileLocation, out);
                    System.out.println("Extracting "+i+":  "+out.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        eUI.messageBoxExt(this, "Data extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="[TREE] Popup Entry        - Items Event">
    public void jMenuItemEntryExtractFromSection() {
        //--- Get File
        File out = eUI.getSaveFilename(new String[] {"Binary file"}, new String[] {".bin"});
        if (out == null) {
            eUI.messageBoxExt(this, "File error!", "Failed to save file", easyUI.MBIcon_ERROR);
            return;
        } else if (out.exists()) {
            eUI.messageBoxExt(this, "File already exists!", "Failed to save file", easyUI.MBIcon_INFORMATION);
            return;
        }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        RelocationEntry relEntry = (RelocationEntry) node.getUserObject();
        try {
            relEntry.extractFromSection(relFile.fileLocation, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        eUI.messageBoxExt(this, "Data extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemEntryExtractFromFile() {
        
    }
    //</editor-fold>
    
    //--- Private methods
    private JPopupMenu sectionMenu() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        if (node.isLeaf()) {
            itemSectionResolve.setEnabled(true);
            itemSectionExtractAll.setEnabled(false);
        } else {
            itemSectionResolve.setEnabled(false);
            itemSectionExtractAll.setEnabled(true);
        }
        return popupSection;
    }
    
    private JPopupMenu entryMenu() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        itemEntryExtractFromFile.setEnabled(false);
        if (((RelocationEntry) node.getUserObject()).isInternals) {
            itemEntryExtractFromSection.setEnabled(true);
        } else {
            itemEntryExtractFromSection.setEnabled(false);
        }
        return popupEntry;
    }
    
    //--- Overriden methods
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.getLastSelectedPathComponent();
        if (node == null) { return; }
        
        Object nodeInfo = node.getUserObject();
        String str = "<Properties>";
        if (nodeInfo.getClass() == RSO.class) {
            //--- Root description
            str = "<RSO>";
            
        } else if (nodeInfo.getClass() == String.class) {
            
            switch((String) nodeInfo) {
                //--- Section Table description
                case "Sections":
                    str =   "<html>Section Table:  <br/><br/>" +
                            "Offset:  0x"   + String.format("%08X", relFile.sectionTable) + "<br/>" +
                            "Count:  0x"    + String.format("%08X", relFile.sectionCount) + "</html>";
                    ;break;
                
                //--- Internals Relocation Table description
                case "Internals":
                    str =   "<html>Internals Relocation:  <br/><br/>" +
                            "Offset:  0x"   + String.format("%08X", relFile.relocation[0].tableOffset)      + "<br/>" +
                            "Size:  0x"     + String.format("%08X", relFile.relocation[0].tableSize)        + "<br/>" +
                            "Count:  0x"    + String.format("%08X", relFile.relocation[0].tableSize / 12)   + "</html>";
                    ;break;
                
                //--- Externals Relocation Table description
                case "Externals":
                    str =   "<html>Externals Relocation:  <br/><br/>" +
                            "Offset:  0x"   + String.format("%08X", relFile.relocation[1].tableOffset)      + "<br/>" +
                            "Size:  0x"     + String.format("%08X", relFile.relocation[1].tableSize)        + "<br/>" +
                            "Count:  0x"    + String.format("%08X", relFile.relocation[1].tableSize / 12)   + "</html>";
                    ;break;
            }
            
        } else if (nodeInfo.getClass() == Section.class) {
            //--- Section description
            Section prop = (Section)nodeInfo;
            str = "<html>Section:  <br/><br/>" +
                  "Offset:  0x" + String.format("%08X", prop.sectionOffset) + "<br/>" +
                  "Size:  0x"   + String.format("%08X", prop.sectionSize)   + "<br/>" +
                  "</html>";
            
        } else if (nodeInfo.getClass() == RelocationEntry.class) {
            //--- Entry description
            RelocationEntry prop = (RelocationEntry) nodeInfo;
            str =   "<html>Relocation Entry:  <br/><br/>" +
                    "r_offset:  0x"  + String.format("%08X", prop.r_offset)       + "<br/>" +
                    "r_info:  (0x"   + String.format("%06X", prop.relocationId)   + ", 0x"  +
                                       String.format("%02X", prop.relocationType) + ") <br/>" +
                    "r_addend:  0x"  + String.format("%08X", prop.r_addend)       + "<br/>" +
                    "<br/>";
        }
        
        properties.setText(str);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Mouse Events">
    @Override
    public void mousePressed(MouseEvent e) {
       
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        TreePath path = this.getPathForLocation(e.getX(), e.getY());
        if (path == null)    return;
        this.setSelectionPath(path);
        if (SwingUtilities.isRightMouseButton(e)) {
           showMenu(e);
       }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
       
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
       
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
       
    }
    //</editor-fold>
    
    //--- Variables declaration
    easyUI eUI = new easyUI();
    Relocator relFile;
    DefaultMutableTreeNode root, sectionsNode, internalsNode, externalsNode;
    JLabel properties;
    JPopupMenu popupSection, popupEntry;
    JMenuItem itemSectionResolve, itemSectionExtractAll, itemEntryExtractFromSection, itemEntryExtractFromFile;
}

class RelocationEntry {
    
    public RelocationEntry(Relocator rel, RelocationTable table, int entry) {
        this.relFile        = rel;
        this.r_offset       = Long.parseLong(table.r_offset[entry], 16);
        this.relocationId   = Long.parseLong(table.r_info[1][entry], 16);
        this.relocationType = Long.parseLong(table.r_info[2][entry], 16);
        this.r_addend       = Long.parseLong(table.r_addend[entry], 16);
        this.ID             = entry;
        
        switch (table.relocationType) {
            case 0:  // Internals = Exports
                this.isInternals    = true;
                this.name           = "";
                for (int i = 0; i < rel.exports.length; i++) {
                    if (rel.exports[i].contentOffset == this.r_addend
                            && rel.exports[i].sectionType == this.relocationId) {
                        this.name = rel.exports[i].name;
                    }
                }
                
                if (this.name.equals("")) {
                    switch ((int) this.relocationId) {
                        case 0x01:  this.name = "<Entry:  Assembly>"    ;break;
                        case 0x02:  this.name = "<Entry:  Constructors>";break;
                        case 0x03:  this.name = "<Entry:  Destructors>" ;break;
                        case 0x04:  this.name = "<Entry:  Constants>"   ;break;
                        case 0x05:  this.name = "<Entry:  Objects>"     ;break;
                        case 0x06:  this.name = "<Entry:  Bss>"         ;break;
                        default:  this.name = "<Entry:  " + this.relocationId + ">";                             
                    }
                }
                break;
                
            case 1:  // Externals = Imports
                this.isInternals = false;
                this.name = relFile.imports[(int) this.relocationId].name;
                break;
        }
    }
    
    public void extractFromSection(File in, File out) throws IOException {
        if (!isInternals)    throw new IOException("Can't reach import section!");
        Section section = relFile.section[(int) this.relocationId];
        long offset = section.sectionOffset + this.r_addend;
        if (in.length() < offset)  {
            throw new IOException("File doesn't match with sectionOffset/Size!");
        } else if (!in.exists() || !in.isFile()) {
            throw new IOException("Can't access input file!");
        } else if (out.exists()) {
            throw new IOException("Output file already exists!");
        } else if (this.relocationType != 0x01) {
            //easyUI.messageBoxExt(null, "Relocation type unsupported",
            //        "May not be able to resolve relocation type:  "+this.relocationType, easyUI.MBIcon_WARNING);
            //System.out.println("Relocation type unsupported : May not be able to resolve relocation type.");
        }
        
        int[] buff;
        switch ((int) section.sectionType) {
            case 0x01:  // Binary
                buff = PowerPC.extractBinary(in, offset);
                ;break;
            default:  // Object 
                buff = PowerPC.extractObject(in, offset);
        }
        
        FileOutputStream fos        = new FileOutputStream(out);
        BufferedOutputStream bos    = new BufferedOutputStream(fos);
        for (int i = 0; i < buff.length; i++) {
            bos.write(buff[i]);
        }
        
        bos.close();
        fos.close();
    }
    
    public void extractFromFile(File in, File out) throws IOException {
        
    } 
    
    //--- Overriden methods
    @Override
    public String toString() {
        return this.name;
    }
    
    //--- Variable declarations
    public int ID;
    public boolean isInternals;
    public Relocator relFile;
    public String name;
    public long r_offset, r_addend, relocationId, relocationType;    
}