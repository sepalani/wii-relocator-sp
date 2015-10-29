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

package UI;

// import Wii.PPC.PowerPC;
import Wii.Relocators.*;

import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *
 * @author spln
 */
public class FileTree extends JTree implements TreeSelectionListener, MouseListener {
    
    public FileTree(Relocator rel) {
        //--- Create Nodes
        super(new DefaultMutableTreeNode(rel));
        this.
        root    = (DefaultMutableTreeNode) this.getModel().getRoot();
        relFile = (Relocator) root.getUserObject();
        
        sectionsNode = new DefaultMutableTreeNode("Sections");
        for (int i = 0; i < (int)rel.sectionCount; i++) {
            sectionsNode.add(new DefaultMutableTreeNode(rel.section[i]));
        }
        
        exportsNode = new DefaultMutableTreeNode("Exports");
        for (int i = 0; i < rel.exportsCount; i++){
            exportsNode.add(new DefaultMutableTreeNode(rel.exports[i]));
        }
        
        importsNode = new DefaultMutableTreeNode("Imports");
        for (int i = 0; i < rel.importsCount; i++){
            importsNode.add(new DefaultMutableTreeNode(rel.imports[i]));
        }
        
        root.add(sectionsNode);
        root.add(exportsNode);
        root.add(importsNode);
        
        //--- Create Popup Menu
        // Section
        popupSection            = new JPopupMenu("42");
        itemSectionExtract      = new JMenuItem("Extract Section");
        itemSectionExtract.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemSectionExtract();
            }
        });
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
        popupSection.add(itemSectionExtract);
        popupSection.add(itemSectionExtractAll);
        popupSection.add(itemSectionResolve);
        
        // Export
        popupExport             = new JPopupMenu("21");
        popupExportFromFile     = new JMenu("Extract from file");
        popupExportFromSection  = new JMenu("Extract from section");
        popupExport.add(popupExportFromSection);
        popupExport.add(popupExportFromFile);
        
        // Exports
        popupExports            = new JPopupMenu("18");
        itemExportsExtractAll   = new JMenuItem("Extract All...");
        itemExportsExtractAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jMenuItemExportsExtractAll();
                }
        });
        popupExports.add(itemExportsExtractAll);
        
        // Import
        popupImport             = new JPopupMenu("23");
        popupImportFromFile     = new JMenu("Extract from file");
        popupImport.add(popupImportFromFile);
        
        // Imports
        popupImports            = new JPopupMenu("18");
        itemImportsExtractAll   = new JMenuItem("Extract All...");
        itemImportsExtractAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jMenuItemImportsExtractAll();
                }
        });
        popupImports.add(itemImportsExtractAll);
    }
    
    public void showMenu(MouseEvent e) {
        JPopupMenu popup;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        if (node.getUserObject().getClass() == Section.class) {
            popup = sectionMenu();
        } else if (node.getUserObject().getClass() == Export.class) {
            popup = exportMenu();
        } else if (node.getUserObject().getClass() == Import.class) {
            popup = importMenu();
        } else if (node.getUserObject().getClass() == String.class) {
            String str = (String) node.getUserObject();
            switch (str) {
                case "Exports":
                    popup = importsMenu();
                    break;
                case "Imports":
                    popup = exportsMenu();
                    break;
                default:
                    return;
            }
        } else {
            return;
        }
        
        popup.setInvoker(this);
        popup.show(this, e.getX(), e.getY());
    }
    
    //--- Menu Item
    
    //<editor-fold defaultstate="collapsed" desc="[TREE] Popup Section          - Items Event">
    public void jMenuItemSectionExtract() {
        //--- Get File
        File out = easyUI.getSaveFilename(new String[] {"Binary file"}, new String[] {".bin"});
        if (out == null) {
            easyUI.messageBoxExt(this, "File error!", "Failed to save file", easyUI.MBIcon_ERROR);
            return;
        } else if (out.exists()) {
            easyUI.messageBoxExt(this, "File already exists!", "Failed to save file", easyUI.MBIcon_INFORMATION);
            return;
        }
        
        //--- Extract Section
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Section section = (Section) node.getUserObject();
        try {
            section.extract(relFile.fileLocation, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        easyUI.messageBoxExt(this, "Section extracted!", "Done", easyUI.MBIcon_INFORMATION);
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
        easyUI.messageBoxExt(this, "Data extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemSectionResolve() {
        // Get section type
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Section section = (Section) node.getUserObject();
        long type = section.sectionType;
        
        // Get exports and adding them
        int count = 0;
        for (int i = 0; i < relFile.exports.length; i++) {
            if (relFile.exports[i].sectionType == type) {
                node.add(new DefaultMutableTreeNode(relFile.exports[i]));
                count++;
            }
        }
        if (count == 0) node.add(new DefaultMutableTreeNode("<null>"));
        easyUI.messageBoxExt(this, "Resolved: "+count, "Resolve Section", easyUI.MBIcon_INFORMATION);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="[TREE] Popup Import/Export    - Items Event">
    public void jMenuItemImportExtractFromFile(int relId) {
        System.out.println("--- Import Extract From File ---");
        long dec, from;
        //  Get address rectification
        try {
            dec = easyUI.parseLong(easyUI.getString(this, "Address rectification", "0x00"));
            from = -dec;
        } catch (NumberFormatException | NullPointerException e) {
            easyUI.messageBoxExt(this, "Isn't a valide number!", "Input error", easyUI.MBIcon_ERROR);
            e.printStackTrace();
            return;
        }
        
        //  Get File
        File out = easyUI.getSaveFilename(new String[] {"Binary file"}, new String[] {".bin"});
            if (out == null) {
                easyUI.messageBoxExt(this, "File error!", "Failed to save file", easyUI.MBIcon_ERROR);
                return;
            } else if (out.exists()) {
                easyUI.messageBoxExt(this, "File already exists!", "Failed to save file", easyUI.MBIcon_INFORMATION);
                return;
            }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Import imp  = (Import) node.getUserObject();
        try {
            imp.extractFromFile(relFile.fileLocation, out, from, relId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        easyUI.messageBoxExt(this, "Import extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemExportExtractFromFile(int relId) {
        System.out.println("--- Export Extract From File ---");
        long dec, from;
        //  Get address rectification
        try {
            dec = easyUI.parseLong(easyUI.getString(this, "Address rectification", "0x00"));
            from = -dec;
        } catch (NumberFormatException | NullPointerException e) {
            easyUI.messageBoxExt(this, "Isn't a valide number!", "Input error", easyUI.MBIcon_ERROR);
            e.printStackTrace();
            return;
        }
        
        //  Get File
        File out = easyUI.getSaveFilename(new String[] {"Binary file"}, new String[] {".bin"});
            if (out == null) {
                easyUI.messageBoxExt(this, "File error!", "Failed to save file", easyUI.MBIcon_ERROR);
                return;
            } else if (out.exists()) {
                easyUI.messageBoxExt(this, "File already exists!", "Failed to save file", easyUI.MBIcon_INFORMATION);
                return;
            }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Export exp  = (Export) node.getUserObject();
        try {
            exp.extractFromFile(relFile.fileLocation, out, from, relId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        easyUI.messageBoxExt(this, "Export extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemExportExtractFromSection() {
        //--- Get File
        File out = easyUI.getSaveFilename(new String[] {"Binary file"}, new String[] {".bin"});
        if (out == null) {
            easyUI.messageBoxExt(this, "File error!", "Failed to save file", easyUI.MBIcon_ERROR);
            return;
        } else if (out.exists()) {
            easyUI.messageBoxExt(this, "File already exists!", "Failed to save file", easyUI.MBIcon_INFORMATION);
            return;
        }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Export exp = (Export) node.getUserObject();
        try {
            exp.extractFromSection(relFile.section[(int) exp.sectionType],
                    relFile.fileLocation, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        easyUI.messageBoxExt(this, "Export extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="[TREE] Popup Imports/Exports  - Items Event">
    public void jMenuItemImportsExtractAll() {
        long dec, from;
        //  Get address rectification
        try {
            dec = easyUI.parseLong(easyUI.getString(this, "Address rectification", "0x00"));
            from = -dec;
        } catch (NumberFormatException | NullPointerException e) {
            easyUI.messageBoxExt(this, "Isn't a valide number!", "Input error", easyUI.MBIcon_ERROR);
            e.printStackTrace();
            return;
        }
        
        //  Get directory
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
        String errList = "%n Failed:  %n";
        if (nodeInfo.getClass() == Import.class) {
            for (int i = 0; i < leaf.length; i++) {
                Import imp  = (Import) leaf[i].getUserObject();
                String name = imp.toString();
                File out    = new File(path+name+".bin");
                for (int j = 1; out.exists(); j++) {
                    out = new File(path+name+j+".bin");
                }
                System.out.println("---" + imp.toString());
                
                try {
                    imp.extractFromFile(relFile.fileLocation, out, from);
                } catch (java.lang.OutOfMemoryError | IOException e) {
                    System.out.println("|!| FAIL |!|");
                    errList += " - " + imp.toString() + " %n";
                    e.printStackTrace();
                }
                System.out.println("Done!");
            }
        } else {
            System.out.println("Isn't import !");
        }
        
        if (!errList.equals("%n Failed:  %n")) {
            easyUI.messageBoxExt(this, String.format(errList), "Error", easyUI.MBIcon_ERROR);
        }
        easyUI.messageBoxExt(this, "Data extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
    }
    
    public void jMenuItemExportsExtractAll() {
        long dec, from;
        //  Get address rectification
        try {
            dec = easyUI.parseLong(easyUI.getString(this, "Address rectification", "0x00"));
            from = -dec;
        } catch (NumberFormatException | NullPointerException e) {
            easyUI.messageBoxExt(this, "Isn't a valide number!", "Input error", easyUI.MBIcon_ERROR);
            e.printStackTrace();
            return;
        }
        
        //  Get directory
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
        String errList = "%n Failed:  %n";
        if (nodeInfo.getClass() == Export.class) {
            for (int i = 0; i < leaf.length; i++) {
                Export exp  = (Export) leaf[i].getUserObject();
                String name = exp.toString();
                File out    = new File(path+name+".bin");
                for (int j = 1; out.exists(); j++) {
                    out = new File(path+name+j+".bin");
                }
                System.out.println("---" + exp.toString());
                
                try {
                    exp.extractFromFile(relFile.fileLocation, out, from);
                } catch (java.lang.OutOfMemoryError | IOException e) {
                    System.out.println("|!| FAIL |!|");
                    errList += " - " + exp.toString() + " %n";
                    e.printStackTrace();
                }
                System.out.println("Done!");
            }
        } else {
            System.out.println("Isn't import !");
        }
        
        if (!errList.equals("%n Failed:  %n")) {
            easyUI.messageBoxExt(this, String.format(errList), "Error", easyUI.MBIcon_ERROR);
        }
        easyUI.messageBoxExt(this, "Data extracted successfuly!", "Done", easyUI.MBIcon_INFORMATION);
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
    
    private JPopupMenu exportsMenu() {
        return popupExports;
    }
    
    private JPopupMenu exportMenu() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Export exp = (Export) node.getUserObject();
        popupExportFromFile.removeAll();
        popupExportFromSection.removeAll();
        
        JMenuItem item2 = new JMenuItem("All");
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemExportExtractFromSection();
            }
        });
        popupExportFromSection.add(item2);
        
        for (int i = 0; i < exp.r_count; i++) {
            String strRel = "Relocation N°"+i;
            
            JMenuItem item1 = new JMenuItem(strRel);
            final int loop_i = new Integer(i);
            item1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jMenuItemExportExtractFromFile(loop_i);
                }
            });
            
            popupExportFromFile.add(item1);
            //popupExportFromFile.setEnabled(false);
        }
        return popupExport;
    }
    
    private JPopupMenu importsMenu() {
        return popupImports;
    }
    
    private JPopupMenu importMenu() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
        Import imp = (Import) node.getUserObject();
        popupImportFromFile.removeAll();
        for (int i = 0; i < imp.r_count; i++) {
            String strRel = "Relocation N°"+i;
            
            JMenuItem item1 = new JMenuItem(strRel);
            final int loop_i = new Integer(i);
            item1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jMenuItemImportExtractFromFile(loop_i);
                }
            });
            
            popupImportFromFile.add(item1);
            //popupImportFromFile.setEnabled(false);
        }
        return popupImport;
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
                
                //--- Exports description
                case "Exports":
                    str =   "<html>Exports:  <br/><br/>" +
                            "Offset:  0x"       + String.format("%08X", relFile.exportsOffset)  + "<br/>" +
                            "Size:  0x"         + String.format("%08X", relFile.exportsSize)    + "<br/>" +
                            "nameOffset:  0x"   + String.format("%08X", relFile.exportsName)    + "<br/>" +
                            "Count:  0x"        + String.format("%08X", relFile.exportsCount)   + "<br/>" +
                            "</html>";
                    ;break;
                
                //--- Imports description
                case "Imports":
                    str =   "<html>Imports:  <br/><br/>" +
                            "Offset:  0x"       + String.format("%08X", relFile.importsOffset)  + "<br/>" +
                            "Size:  0x"         + String.format("%08X", relFile.importsSize)    + "<br/>" +
                            "nameOffset:  0x"   + String.format("%08X", relFile.importsName)    + "<br/>" +
                            "Count:  0x"        + String.format("%08X", relFile.importsCount)   + "<br/>" +
                            "</html>";
                    ;break;
                  
                    
            }
            
        } else if (nodeInfo.getClass() == Section.class) {
            //--- Section description
            Section prop = (Section)nodeInfo;
            str = "<html>Section:  <br/><br/>" +
                  "Offset:  0x" + String.format("%08X", prop.sectionOffset) + "<br/>" +
                  "Size:  0x"   + String.format("%08X", prop.sectionSize)   + "<br/>" +
                  "</html>";
            
        } else if (nodeInfo.getClass() == Import.class) {
            //--- Import description
            Import prop = (Import)nodeInfo;
            str  =  "<html>Import:  <br/><br/>" +
                    "File Offset:  0x"      + String.format("%08X", prop.position)          + "<br/>" +
                    "Import ID:  0x"        + String.format("%08X", prop.ID)                + "<br/><br/>" +
                    "nameOffset:  0x"       + String.format("%08X", prop.nameOffset)        + "<br/>" +
                    "sectionType:  0x"      + String.format("%08X", prop.sectionType)       + "<br/>" +
                    "contentOffset:  0x"    + String.format("%08X", prop.contentOffset)     + "<br/>" +
                    "RelocationCount:  0x"  + String.format("%08X", prop.r_count)     + "<br/><br/>";
                    for (int i = 0; i < prop.r_count; i++) {
                        str += "Relocation N°"  + i + ":  <br/>" +
                               "r_offset:  0x"  + String.format("%08X", prop.r_offset[i])       + "<br/>" +
                               "r_info:  (0x"   + String.format("%06X", prop.relocationId[i])   + ", 0x"  +
                                                  String.format("%02X", prop.relocationType[i]) + ") <br/>" +
                               "r_addend:  0x"  + String.format("%08X", prop.r_addend[i])       + "<br/>" +
                               "<br/>";
                    }
                    str += "</html>";
            
        } else if (nodeInfo.getClass() == Export.class) {
            //--- Export description
            Export prop = (Export)nodeInfo;
            str  =  "<html>Export:  <br/><br/>" +
                    "File Offset:  0x"      + String.format("%08X", prop.position)          + "<br/>" +
                    "Export ID:  0x"        + String.format("%08X", prop.ID)                + "<br/><br/>" +
                    "nameOffset:  0x"       + String.format("%08X", prop.nameOffset)        + "<br/>" +
                    "unknownAddress:  0x"   + String.format("%08X", prop.address)           + "<br/>" +
                    "sectionType:  0x"      + String.format("%08X", prop.sectionType)       + "<br/>" +
                    "contentOffset:  0x"    + String.format("%08X", prop.contentOffset)     + "<br/>" +
                    "RelocationCount:  0x"  + String.format("%08X", prop.r_count)     + "<br/><br/>";
                    for (int i = 0; i < prop.r_count; i++) {
                        str += "Relocation N°"  + i + ":  <br/>" +
                               "r_offset:  0x"  + String.format("%08X", prop.r_offset[i])       + "<br/>" +
                               "r_info:  (0x"   + String.format("%06X", prop.relocationId[i])   + ", 0x"  +
                                                  String.format("%02X", prop.relocationType[i]) + ") <br/>" +
                               "r_addend:  0x"  + String.format("%08X", prop.r_addend[i])       + "<br/>" +
                               "<br/>";
                    }
                    str += "</html>";
            
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
    Relocator relFile;
    DefaultMutableTreeNode root, sectionsNode, exportsNode, importsNode;
    JLabel properties;
    JPopupMenu popupSection, popupExport, popupExports, popupImport, popupImports;
    JMenu popupExportFromFile, popupExportFromSection, popupImportFromFile;
    JMenuItem itemSectionExtract, itemSectionExtractAll, itemSectionResolve,
            itemExportsExtractAll, itemImportsExtractAll;
    easyUI eUI = new easyUI();
    
}
