/*
*     This file is part of the VCF.Filter project (https://biomedical-sequencing.at/VCFFilter/).
*     VCF.Filter permits graphical filtering of VCF files on cutom annotations and standard VCF fields, pedigree analysis, and cohort searches.
* %%
*     Copyright © 2016, 2017  Heiko Müller (hmueller@cemm.oeaw.ac.at)
* %%
* 
*     VCF.Filter is free software: you can redistribute it and/or modify
*     it under the terms of the GNU General Public License as published by
*     the Free Software Foundation, either version 3 of the License, or
*     (at your option) any later version.
* 
*     This program is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*     GNU General Public License for more details.
* 
*     You should have received a copy of the GNU General Public License
*     along with VCF.Filter.  If not, see <http://www.gnu.org/licenses/>.
* 
*     VCF.Filter  Copyright © 2016, 2017  Heiko Müller (hmueller@cemm.oeaw.ac.at)
*     This program comes with ABSOLUTELY NO WARRANTY;
*     This is free software, and you are welcome to redistribute it
*     under certain conditions; 
*     For details interrogate the About section in the File menu. 
*/
package at.ac.oeaw.cemm.bsf.vcffilter.preferences;

import at.ac.oeaw.cemm.bsf.vcffilter.NoExampleVCFFileException;
import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import at.ac.oeaw.cemm.bsf.vcffilter.outputformat.OutputOrder;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.ExampleFileFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.FilterDefaults;
import htsjdk.variant.vcf.VCFFileReader;
//import htsjdk.tribble.TribbleException;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/** 
 * VCFFilterPreferences wizard.
 * VCFFilterPreferences.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class VCFFilterPreferences extends javax.swing.JDialog {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The directory where the VCFFilter.jar resides.
     */
    public static String userdir = null;
    
    /**
     * The user login name.
     */
    public static String username = null;
    
    /**
     * The VCFFilter_preferences.ini file.
     * Might also be called VCFFilter_preferences_$username.ini.
     * VCFFilter_preferences_$username.ini has preference over VCFFilter_preferences.ini.
     */
    public static File iniFile;
    
    /**
     * The .ini file content.
     */
    private static String iniFileContent;
    
    /**
     * The sample VCF file used to initialize VCFInfoHeaderLine fields.
     */
    private static File sampleVCFFile;  
    
    /**
     * Hashtable for .ini file keys and values.
     */
    private Hashtable<String, String> ht;//hash for ini file keys and values
    
    /**
     * The default directory to initialize various JFileChooser default directories.
     */
    public static File DefaultDir = null;    
    
    /**
     * The current look and feel.
     */
    public static String LookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    
    /**
     * WindowsLookAndFeel fully qualified name.
     */
    public static final String windowsLookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    
    /**
     * MotifLookAndFeel fully qualified name.
     */
    public static final String motifLookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    
    /**
     * MetalLookAndFeel fully qualified name.
     */
    public static final String metalLookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
    //public static final String gtkLookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"; 
    
    /**
     * NimbusLookAndFeel fully qualified name.
     */
    public static final String nimbusLookAndFeel = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
    
    /**
     * WindowsClassicLookAndFeel fully qualified name.
     */
    public static final String windowsclassicLookAndFeel = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
    
    /**
     * List of VCFInfoHeaderLines read from the sample VCF file.
     */
    private ArrayList<VCFInfoHeaderLine> infoHeaderLines;
    
    /**
     * Hashtable for Filter default settings.
     */
    private Hashtable<String, FilterDefaults> filterDefaultsHash;
    
    /**
     * List of recurrence files.
     */
    private ArrayList<File> recurrenceFiles;
    
    /**
     * List of white list files.
     */
    private ArrayList<File> whiteListFiles;
    
    /**
     * List of black list files.
     */
    private ArrayList<File> blackListFiles;
    
    /**
     * ID of the VCFInfoHeaderLine containing the gene symbol annotation of variants.
     */
    private String genesymbolField;
    
    /**
     * List of Hyperlinks.
     */
    private ArrayList<Hyperlink> hyperlinks;
    
    /**
     * Vertical gap used in adding/removing hyperlink preferences.
     */
    private int verticalGap = 80;
    
    /**
     * Horizontal gap used in adding/removing hyperlink preferences.
     */
    private int horizontalGap = 15;
    
    /**
     * Reference to the VCFFilter instance.
     */
    private VCFFilter gui;
    
    /**
     * output limit.
     */
    private int outputlimit = 500000;

    /**
    * Creates new VCFFilterPreferences.
    * 
    * @param parent parent frame
    * @param modal is dialog modal
    * @author Heiko Müller
    * @since 1.0
    */
    public VCFFilterPreferences(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        buttonGroup1.add(jRadioButton3);
        buttonGroup1.add(jRadioButton4);
        buttonGroup1.add(jRadioButton5);
        
        ExampleFileFilter vcfFilter = new ExampleFileFilter(new String[]{"vcf", "gz"}, "Load vcf files");
        jFileChooser1.addChoosableFileFilter(vcfFilter);
        jFileChooser1.setAcceptAllFileFilterUsed(false);  
        ExampleFileFilter tsvFilter = new ExampleFileFilter(new String[]{"tsv"}, "Load tsv files");
        jFileChooser2.addChoosableFileFilter(tsvFilter);
        jFileChooser2.setAcceptAllFileFilterUsed(false);
        ExampleFileFilter bedFilter = new ExampleFileFilter(new String[]{"bed"}, "Load bed files");
        jFileChooser4.addChoosableFileFilter(bedFilter);
        jFileChooser4.setAcceptAllFileFilterUsed(false);        
        jFileChooser5.addChoosableFileFilter(bedFilter);
        jFileChooser5.setAcceptAllFileFilterUsed(false);
        centerWindow(this);

    }
    
    /**
    * Inits the list of Hyperlinks.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initHyperlinks(){
        if(hyperlinks  == null || hyperlinks.size() == 0){
            hyperlinks = new ArrayList<Hyperlink>();      
            Hyperlink test = new Hyperlink("ID", "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?searchType=adhoc_search&type=rs&rs=", "ID", "", this);             
            hyperlinks.add(test);
        }
        int counter = 0;
        for(Hyperlink h : hyperlinks){
            h.setPreferences(this);
            String id = h.getID();
            h.initSearchFields();
            h.setSelectedItem(id);
            addToMyLayout(jPanel20, h, horizontalGap, counter * verticalGap + 10, 0, 0);
            counter++;
        }
        initRemoveHyperlinks();
    }
    
    /**
    * Inits the remove Hyperlink JCombobox.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initRemoveHyperlinks(){            
        jComboBox3.removeAllItems();        
        for(Hyperlink h : hyperlinks){
            jComboBox3.addItem(h.getID());
        }        
    }
    
    /**
    * Inits Hyperlink preferences layout.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void layoutHyperlinks(){
        jPanel20.removeAll();
        int counter = 0;
        for(Hyperlink h : hyperlinks){
            addToMyLayout(jPanel20, h, horizontalGap, counter * verticalGap + 10, 0, 0);
            counter++;
        }
        initRemoveHyperlinks();
    }
    
    /**
    * Inits the Filter defaults hash according to .ini file entries.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initFilterDefaultsHash() {
        filterDefaultsHash = new Hashtable<String, FilterDefaults>();
        if(infoHeaderLines != null){
            jComboBox1.removeAllItems();
            Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
            while(it.hasNext()){
                VCFInfoHeaderLine hl = it.next();
                String filterid = hl.getID();    
                FilterDefaults fd = new FilterDefaults();
                fd.setID(filterid);                
                            
                if(ht != null){
                    if (ht.containsKey(filterid + ".andnot")) {
                         Boolean b = new Boolean(ht.get(filterid + ".andnot"));
                         fd.setAndnot(b.booleanValue());
                    }
                    if (ht.containsKey(filterid + ".operator1")) {
                         String s = (ht.get(filterid + ".operator1"));
                         fd.setOperator1(s);
                    }
                    if (ht.containsKey(filterid + ".criterion1")) {
                         String s = (ht.get(filterid + ".criterion1"));
                         fd.setCriterion1(s);
                    }
                    if (ht.containsKey(filterid + ".operator2")) {
                         String s = (ht.get(filterid + ".operator2"));
                         fd.setOperator2(s);
                    }
                    if (ht.containsKey(filterid + ".criterion2")) {
                         String s = (ht.get(filterid + ".criterion2"));
                         fd.setCriterion2(s);
                    }
                    if (ht.containsKey(filterid + ".operator3")) {
                         String s = (ht.get(filterid + ".operator3"));
                         fd.setOperator3(s);
                    }
                    if (ht.containsKey(filterid + ".criterion3")) {
                         String s = (ht.get(filterid + ".criterion3"));
                         fd.setCriterion3(s);
                    }
                }
                
                if(filterDefaultsHash.containsKey(fd.getID())){
                    filterDefaultsHash.remove(fd.getID());
                    filterDefaultsHash.put(fd.getID(), fd);
                }else{
                    filterDefaultsHash.put(fd.getID(), fd);
                } 
                jComboBox1.addItem(fd.getID());    
                
            }
        }
    }

    /**
    * Inits the VCFInfoHeaderLines collection.
    * For consistency, default VCF fields (chrom, pos, ref, alt, id, qual, filter) 
    * are added to the collection to enable filtering on them even though they are not INFO fields.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initVCFHeaderLinesCollection() throws NoExampleVCFFileException{
        if(sampleVCFFile != null && sampleVCFFile.exists()){
            VCFFileReader vcf = new VCFFileReader(sampleVCFFile);
            Iterator<VCFInfoHeaderLine> it = vcf.getFileHeader().getInfoHeaderLines().iterator();
            infoHeaderLines = new ArrayList<VCFInfoHeaderLine>();
            while(it.hasNext()){
                infoHeaderLines.add(it.next());
            } 
            VCFInfoHeaderLine chr = new VCFInfoHeaderLine("CHROM", 1, VCFHeaderLineType.String, "Filters for chromosome id");
            VCFInfoHeaderLine pos = new VCFInfoHeaderLine("POS", 1, VCFHeaderLineType.Integer, "Filters for variant position");
            VCFInfoHeaderLine id = new VCFInfoHeaderLine("ID", 1, VCFHeaderLineType.String, "Filters dbSNP rs ids");            
            VCFInfoHeaderLine ref = new VCFInfoHeaderLine("REF", 1, VCFHeaderLineType.String, "Filters for reference allele bases");
            VCFInfoHeaderLine alt = new VCFInfoHeaderLine("ALT", 1, VCFHeaderLineType.String, "Filters for alternative allele bases");
            VCFInfoHeaderLine qual = new VCFInfoHeaderLine("QUAL", 1, VCFHeaderLineType.Float, "Filters for call quality");
            VCFInfoHeaderLine filt = new VCFInfoHeaderLine("FILTER", 1, VCFHeaderLineType.String, "Filters for call quality");
            infoHeaderLines.add(0, chr);
            infoHeaderLines.add(1, pos);
            infoHeaderLines.add(2, id);
            infoHeaderLines.add(3, ref);
            infoHeaderLines.add(4, alt);            
            infoHeaderLines.add(5, qual);
            infoHeaderLines.add(6, filt);
        }else{
            throw new NoExampleVCFFileException("Example VCF file not defined or doesn't exist.");
        }
 
    }

    /**
    * Inits the available output list according to the VCFInfoHeaderLine collection.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initAvailableOutputList() {
        DefaultListModel listModel = new DefaultListModel();
        if(infoHeaderLines != null){            
            Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
            while(it.hasNext()){ 
                listModel.addElement(it.next().getID());
            }
        }         
        jList1.setTransferHandler(new at.ac.oeaw.cemm.bsf.vcffilter.preferences.ListItemTransferHandler());
        jList1.setDropMode(DropMode.INSERT);
        jList1.setDragEnabled(true);
        jList1.setModel(listModel);
        jList1.setToolTipText("Drag right to add output, up or down to change order.");
    }

    /**
    * Inits the selected output list according to user choices reflected in the .ini file.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initSelectedOutputList() {
        DefaultListModel listModel = new DefaultListModel();
        Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
        ArrayList<OutputOrder> outorder = new ArrayList<OutputOrder>();
        while(it.hasNext()){
            VCFInfoHeaderLine hl = it.next();
            String filterid = hl.getID();
            if (ht.containsKey(filterid + ".outputOrder")) {
                     String s = (ht.get(filterid + ".outputOrder"));
                     Integer i = Integer.parseInt(s);                     
                     if(i != null && i > 0){
                         outorder.add(new OutputOrder(filterid, i));                            
                     }                     
                }
        }  
        Collections.sort(outorder);
        for(OutputOrder oo : outorder){
            String filterid = oo.getId();
            listModel.addElement(filterid);
            if(listContains(jList1, filterid)){
                removeListElement(jList1, filterid);
            }
        }
        jList2.setTransferHandler(new at.ac.oeaw.cemm.bsf.vcffilter.preferences.ListItemTransferHandler());
        jList2.setDropMode(DropMode.INSERT);
        jList2.setDragEnabled(true);
        jList2.setModel(listModel);
        jList2.setToolTipText("Drag left to remove from output, up or down to change order.");
    }
    
    /**
    * Moves all available output to visible output when nothing else is specified in the preferences.ini.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void makeAllOutputVisible(){
        DefaultListModel model1 = (DefaultListModel)jList1.getModel();
        DefaultListModel model2 = (DefaultListModel)jList2.getModel();
        for(int i = 0; i < model1.getSize(); i++){            
            model2.addElement(model1.get(i));
        }
        model1 = new DefaultListModel();
        jList1.setModel(model1);
    }
    
    /**
    * Adds the chromosome filter to loaded filters when nothing else is specified in the preferences.ini.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void makeDefaultFiltersVisible(){
        DefaultListModel model1 = (DefaultListModel)jList3.getModel();
        DefaultListModel model2 = new DefaultListModel();
        for(int i = 0; i < model1.getSize(); i++){               
            if(model1.getElementAt(i).toString().equals("CHROM")){
                model2.addElement("CHROM");
                model1.removeElementAt(i);
            }
        }
        jList4.setModel(model2);
        jList4.setTransferHandler(new at.ac.oeaw.cemm.bsf.vcffilter.preferences.ListItemTransferHandler());
        jList4.setDropMode(DropMode.INSERT);
        jList4.setDragEnabled(true);        
        jList4.setToolTipText("Drag left to remove from loaded filters.");
        
    }

    /**
    * Inits the list of available filters according to the VCFInfoHeaderLine collection.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initAvailableFiltersList() {
        if(infoHeaderLines != null){
            DefaultListModel listModel = new DefaultListModel();
            Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
            while(it.hasNext()){
                listModel.addElement(it.next().getID());
            }
            //int[] selected = new int[4];    
            
            jList3.setTransferHandler(new at.ac.oeaw.cemm.bsf.vcffilter.preferences.ListItemTransferHandler());
            jList3.setDropMode(DropMode.INSERT);
            jList3.setDragEnabled(true);
            jList3.setModel(listModel);
            jList3.setToolTipText("Drag right to add to loaded filters.");
        }
    }

    /**
    * Inits the list of selected filters according user choices reflected in the .ini file.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initSelectedFiltersList() {
        DefaultListModel listModel = new DefaultListModel();
        Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
        while(it.hasNext()){
            VCFInfoHeaderLine hl = it.next();
            String filterid = hl.getID();
            if (ht.containsKey(filterid + ".loaded")) {
                     String s = (ht.get(filterid + ".loaded"));
                     if(new Boolean(s).booleanValue()){
                            listModel.addElement(filterid);
                            if(listContains(jList3, filterid)){
                                removeListElement(jList3, filterid);
                            }
                     }                     
                }
        }  
        jList4.setTransferHandler(new at.ac.oeaw.cemm.bsf.vcffilter.preferences.ListItemTransferHandler());
        jList4.setDropMode(DropMode.INSERT);
        jList4.setDragEnabled(true);
        jList4.setModel(listModel);
        jList4.setToolTipText("Drag left to remove from loaded filters.");
    }
    
    /**
    * Inits the JCombobox for choosing the gene symbol annotation.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initGenesymbolCombobox() {
        List<VCFInfoHeaderLine> l = getAvailableFilters();        
        for (VCFInfoHeaderLine hl : l) {
            String s = hl.toString();            
            jComboBox2.addItem(hl.getID());            
        }
    }
    
    /**
    * Removes a String element from a JList.
    * 
    * @param list
    * @param s
    * @author Heiko Müller
    * @since 1.0
    */
    private void removeListElement(JList list, String s){
        ListModel lm = list.getModel();
        DefaultListModel lm2 = new DefaultListModel();
        
        for(int i = 0; i < lm.getSize(); i++){
            if(!((String)lm.getElementAt(i)).equals(s)){
                lm2.addElement(lm.getElementAt(i));
            }
        }
        list.setModel(lm2);
    }
    
    /**
    * Returns JList elements as an ArrayList.
    * 
    * @param list
    * @return ArrayList&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<String> getListElements(JList list){        
        ListModel lm = list.getModel();
        ArrayList<String> result = new ArrayList<String>();        
        for(int i = 0; i < lm.getSize(); i++){
            result.add((String)lm.getElementAt(i));
        }
        return result;
    }
    
    /**
    * Returns the list of visible output columns.
    * 
    * @return ArrayList&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<String> getOutputColumns(){ 
        return getListElements(jList2);        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser2 = new javax.swing.JFileChooser();
        jFileChooser3 = new javax.swing.JFileChooser();
        jFileChooser4 = new javax.swing.JFileChooser();
        jFileChooser5 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel12 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel13 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jPanel15 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField8 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox();
        jButton9 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        jFileChooser2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser2ActionPerformed(evt);
            }
        });

        jFileChooser3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser3ActionPerformed(evt);
            }
        });

        jFileChooser4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser4ActionPerformed(evt);
            }
        });

        jFileChooser5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser5ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("VCFFilter preferences");
        setResizable(false);

        jPanel3.setToolTipText("Files");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Files loaded at startup"));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Example VCF file");

        jButton4.setText("Open");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);

        jLabel14.setForeground(new java.awt.Color(255, 0, 0));
        jLabel14.setText("File is needed for VCFFilter startup.");

        jLabel10.setText("File must be indexed and contain a valid VCF header.");

        jLabel11.setText("Header lines will be used to create appropriate filters.");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jButton4)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setPreferredSize(new java.awt.Dimension(460, 82));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel15.setText("VCF directory");

        jButton6.setText("Open");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextField6.setEditable(false);

        jLabel16.setText("Define this directory to avoid browsing the file system tree.");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jButton6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel13.setPreferredSize(new java.awt.Dimension(460, 105));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Recurrence files");

        jButton5.setText("Open");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTextField2.setEditable(false);

        jLabel12.setText("Files contain variant recurrence information in your cohort.");

        jLabel13.setText("You can create and load these files later.");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jButton5)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel14.setPreferredSize(new java.awt.Dimension(460, 82));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel20.setText("White lists");

        jButton1.setText("Open");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField7.setEditable(false);

        jLabel21.setText("Matching variants are kept.");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator4)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        jPanel15.setPreferredSize(new java.awt.Dimension(460, 76));

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel22.setText("Black lists");

        jButton2.setText("Open");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField8.setEditable(false);

        jLabel23.setText("Matching variants are discarded.");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jButton2)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.getAccessibleContext().setAccessibleName("");
        jPanel7.getAccessibleContext().setAccessibleDescription("");

        jTabbedPane1.addTab("Files", jPanel3);
        jPanel3.getAccessibleContext().setAccessibleDescription("");

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Define filters loaded at startup"));

        jScrollPane3.setViewportView(jList3);

        jScrollPane4.setViewportView(jList4);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Available filters");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel7.setText("Loaded filters");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );

        jScrollPane4.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Filters", jPanel4);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Define filter criteria loaded at startup"));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("Available filters");

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jCheckBox1.setText("And Not");

        jLabel1.setText("or");

        jLabel9.setText("or");

        jButton7.setText("Save");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel17.setText("Filter is numeric: ");

        jLabel18.setText("For numeric filters you need to define operators (>, <, =)");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel9))
                .addGap(42, 42, 42)
                .addComponent(jButton7)
                .addGap(18, 18, 18)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Filter defaults", jPanel5);

        jPanel2.setToolTipText("Drag items to change output format and order");
        jPanel2.setPreferredSize(new java.awt.Dimension(506, 559));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Define output columns and output order "));

        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jList2);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Available output");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Visible output");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 131, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        jScrollPane1.getAccessibleContext().setAccessibleName("");
        jScrollPane2.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Output format", jPanel2);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Annotation fields"));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel24.setText("Gene symbol field");

        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jLabel25.setText("Field must be defined for compound heterozygous variant searches.");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(401, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Annotations", jPanel16);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Hyperlinks"));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add/Remove hyperlinks"));

        jButton8.setText("Add");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Remove");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton9)
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 393, Short.MAX_VALUE)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hyperlinks", jPanel19);

        jPanel1.setPreferredSize(new java.awt.Dimension(506, 559));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Requires restart")));

        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Windows");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        jRadioButton2.setText("Motif");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        jRadioButton3.setText("Metal");
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });

        jRadioButton4.setText("Nimbus");
        jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton4ItemStateChanged(evt);
            }
        });

        jRadioButton5.setText("Windows classic");
        jRadioButton5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton5ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1))
                .addContainerGap(324, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton5)
                .addContainerGap(315, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Application design", jPanel1);

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Define output limit"));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel27.setText("Limit output to this number of variants:");

        jTextField9.setText("500000");

        jLabel28.setForeground(new java.awt.Color(255, 0, 51));
        jLabel28.setText("Hint: Setting this limit too high may lead to OutOfMemory errors.");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(jLabel28)
                .addContainerGap(396, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Output limit", jPanel22);

        jButton3.setText("Close");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/oracle-java-question-mark.jpg"))); // NOI18N
        jLabel26.setText(" Mouse over help");
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel26MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jLabel26))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * JFileChooser.APPROVE_SELECTION event handler for jFileChooser1.
    * Sets the sample VCF file and sets the default directory for jFileChooser3 
    * (that is used to define the default directory) 
    * to the directory the sample VCF file resides in.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            sampleVCFFile = jFileChooser1.getSelectedFile();
            jTextField1.setText(sampleVCFFile.getAbsolutePath()); 
            
            try{
                VCFFileReader vcf = new VCFFileReader(sampleVCFFile);                        
            //}catch(htsjdk.tribble.TribbleException te){
            }catch(Exception te){
                sampleVCFFile = null;
                new Warning(this, te.getMessage());
                System.exit(0);
            }
            
            DefaultDir = jFileChooser1.getCurrentDirectory();
            jFileChooser3.setSelectedFile(DefaultDir);
            jTextField6.setText(DefaultDir.getAbsolutePath());
            initVCFHeaderLinesCollection();
        }
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler for jFileChooser2.
    * Sets the list of recurrence files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser2ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {            
            recurrenceFiles = new ArrayList<File>();            
            File[] f = jFileChooser2.getSelectedFiles();
            for(int i = 0; i < f.length; i++){
                recurrenceFiles.add(f[i]);                
            }            
            setTextField(jTextField2, recurrenceFiles);
        }
    }//GEN-LAST:event_jFileChooser2ActionPerformed

    /**
    * JRadioButton1 event handler.
    * Sets look and feel according to user choice.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        // TODO add your handling code here:
        setLookAndFeel();
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    /**
    * JRadioButton2 event handler.
    * Sets look and feel according to user choice.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        // TODO add your handling code here:
        setLookAndFeel();
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    /**
    * JRadioButton3 event handler.
    * Sets look and feel according to user choice.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
        // TODO add your handling code here:
        setLookAndFeel();
    }//GEN-LAST:event_jRadioButton3ItemStateChanged

    /**
    * JRadioButton4 event handler.
    * Sets look and feel according to user choice.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton4ItemStateChanged
        // TODO add your handling code here:
        setLookAndFeel();
    }//GEN-LAST:event_jRadioButton4ItemStateChanged

    /**
    * JRadioButton5 event handler.
    * Sets look and feel according to user choice.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton5ItemStateChanged
        // TODO add your handling code here:
        setLookAndFeel();
    }//GEN-LAST:event_jRadioButton5ItemStateChanged

    /**
    * JButton1 event handler.
    * Close button event.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(sampleVCFFile == null){
            new Warning(this, "You must define a sample VCF file with a valid VCF header for the program to start.");
            System.exit(0);
        }
        setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler for jFileChooser3.
    * Sets the default directory that many other file choosers are using as current directory.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser3ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            DefaultDir = jFileChooser3.getSelectedFile();
            jTextField6.setText(DefaultDir.getAbsolutePath());
        }
    }//GEN-LAST:event_jFileChooser3ActionPerformed

    /**
    * JButton7 event handler.
    * Save filter defaults event.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        //Save Filter defaults
        String filterid = (String)jComboBox1.getSelectedItem();
        VCFInfoHeaderLine hl = getInfoHeaderLine(filterid);
        boolean andnot = jCheckBox1.isSelected();
        String criterion1 = jTextField3.getText();
        String criterion2 = jTextField4.getText();
        String criterion3 = jTextField5.getText();
        String operator1 = jTextField3.getText();
        String operator2 = jTextField4.getText();
        String operator3 = jTextField5.getText();
        if(hl.getType().equals(VCFHeaderLineType.Float) || hl.getType().equals(VCFHeaderLineType.Integer)){
            if(criterion1.length() > 1){
                criterion1 = jTextField3.getText().substring(1);
            }
            if(criterion2.length() > 1){
                criterion2 = jTextField4.getText().substring(1);
            }
            if(criterion3.length() > 1){
                criterion3 = jTextField5.getText().substring(1);
            }
            if(operator1.length() > 1){
                operator1 = jTextField3.getText().substring(0, 1); 
                operator1 = isOperator(operator1);
            }
            if(operator2.length() > 1){
                operator2 = jTextField4.getText().substring(0, 1);
                operator2 = isOperator(operator2);
            }
            if(operator3.length() > 1){
                operator3 = jTextField5.getText().substring(0, 1);
                operator3 = isOperator(operator3);
            }
            FilterDefaults fd = new FilterDefaults(filterid, andnot, criterion1, criterion2, criterion3, operator1, operator2, operator3, true);
            if(filterDefaultsHash.containsKey(fd.getID())){
                filterDefaultsHash.remove(fd.getID());
                filterDefaultsHash.put(fd.getID(), fd);
            }else{
                filterDefaultsHash.put(fd.getID(), fd);
            }
        }else{
            FilterDefaults fd = new FilterDefaults(filterid, andnot, criterion1, criterion2, criterion3);
            if(filterDefaultsHash.containsKey(fd.getID())){
                filterDefaultsHash.remove(fd.getID());
                filterDefaultsHash.put(fd.getID(), fd);
            }else{
                filterDefaultsHash.put(fd.getID(), fd);
            }
        }     
        jLabel19.setForeground(Color.BLUE);
        jLabel19.setText("Values saved successfully.");
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
    * jComboBox1 event handler.
    * Choose filter for defining default settings event.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        VCFInfoHeaderLine hl = getInfoHeaderLine((String)jComboBox1.getSelectedItem());
        if(hl != null){ 
            if( hl.getType().equals(VCFHeaderLineType.Float) || hl.getType().equals(VCFHeaderLineType.Integer)){
                jLabel17.setText("Filter is numeric: Yes");
                jLabel19.setForeground(Color.RED);
                jLabel19.setText("Values unsaved.");
            }else{
                jLabel17.setText("Filter is numeric: No");
                jLabel19.setForeground(Color.RED);
                jLabel19.setText("Values unsaved.");
            }
            FilterDefaults fd = filterDefaultsHash.get(hl.getID());
            if(fd != null){
                jCheckBox1.setSelected(fd.isAndnot());
                jTextField3.setText(fd.getOperator1() + fd.getCriterion1());
                jTextField4.setText(fd.getOperator2() + fd.getCriterion2());
                jTextField5.setText(fd.getOperator3() + fd.getCriterion3());
            }else{
                jCheckBox1.setSelected(false);
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField5.setText("");
            }
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    /**
    * jButton6 event handler.
    * Show file chooser for defining default directory.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        jFileChooser3.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser3.showOpenDialog(this);
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
    * jButton5 event handler.
    * Show file chooser for recurrence files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        jFileChooser2.setMultiSelectionEnabled(true);
        if(DefaultDir != null){
            jFileChooser2.setCurrentDirectory(new File(userdir + File.separator + "recurrence_files"));
        }
        jFileChooser2.showOpenDialog(this);
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
    * jButton4 event handler.
    * Show file chooser for sample VCF file.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        if(userdir != null){
            jFileChooser1.setCurrentDirectory(new File(userdir + File.separator + "VCFData"));
        }
        jFileChooser1.showOpenDialog(this);
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
    * jButton1 event handler.
    * Show file chooser for white list files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jFileChooser4.setMultiSelectionEnabled(true);
        if(DefaultDir != null){
            jFileChooser4.setCurrentDirectory(new File(userdir + File.separator + "list_files"));
        }
        jFileChooser4.showOpenDialog(this);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * jButton2 event handler.
    * Show file chooser for black list files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jFileChooser5.setMultiSelectionEnabled(true);
        if(DefaultDir != null){
            jFileChooser5.setCurrentDirectory(new File(userdir + File.separator + "list_files"));
        }
        jFileChooser5.showOpenDialog(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler for jFileChooser4.
    * Sets the list of white list files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser4ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {            
            whiteListFiles = new ArrayList<File>();            
            File[] f = jFileChooser4.getSelectedFiles();
            for(int i = 0; i < f.length; i++){
                whiteListFiles.add(f[i]);                
            }            
            setTextField(jTextField7, whiteListFiles);
        }
    }//GEN-LAST:event_jFileChooser4ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler for jFileChooser5.
    * Sets the list of black list files.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser5ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {            
            blackListFiles = new ArrayList<File>();            
            File[] f = jFileChooser5.getSelectedFiles();
            for(int i = 0; i < f.length; i++){
                blackListFiles.add(f[i]);                
            }            
            setTextField(jTextField8, blackListFiles);
        }
    }//GEN-LAST:event_jFileChooser5ActionPerformed

    /**
    * JComboBox2 event handler.
    * Choice of the gene symbol annotation field.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        // TODO add your handling code here:
        genesymbolField = infoHeaderLines.get(jComboBox2.getSelectedIndex()).getID();
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    /**
    * JButton8 event handler.
    * Add Hyperlink event.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:      
        if(hyperlinks != null && hyperlinks.size() < 5){
            Hyperlink h = new Hyperlink("", "", "", "", this); 
            //Hyperlink h2 = new Hyperlink("dbSNP", "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?", "ID", "", this);
            addToMyLayout(jPanel20, h, horizontalGap, hyperlinks.size() * verticalGap + 10, 0, 0);
            hyperlinks.add(h);
            initRemoveHyperlinks();
        }
           
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
    * Mouse over help event. Displays the help dialog depending on selected tab index.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jLabel26MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseEntered
        // TODO add your handling code here:
        if(jTabbedPane1.getSelectedIndex() == 0){
            new FileHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 1){
            new FilterHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 2){
            new FilterDefaultsHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 3){
            new OutputFormatHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 4){
            new AnnotationsHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 5){
            new HyperlinksHelpDialog(gui, true);
        }else if(jTabbedPane1.getSelectedIndex() == 6){
            new ApplicationDesignHelpDialog(gui, true);
        }
    }//GEN-LAST:event_jLabel26MouseEntered

    /**
    * JButton9 event handler.
    * Remove Hyperlink event.
    * 
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        hyperlinks.remove(jComboBox3.getSelectedIndex());
        layoutHyperlinks();
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
    * Layout of Hyperlinks.
    * 
    * @param panel
    * @param f
    * @param hgap
    * @param vgap
    * @param hcgap
    * @param vcgap
    * @author Heiko Müller
    * @since 1.0
    */
    private void addToMyLayout(JPanel panel, Hyperlink f, int hgap, int vgap, int hcgap, int vcgap) {

        javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(hgap, hgap, hgap)
                        .addComponent(f)
                        .addContainerGap(hcgap, Short.MAX_VALUE))
        );
        jPanelLayout.setVerticalGroup(
                jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelLayout.createSequentialGroup()
                        .addGap(vgap, vgap, vgap)
                        .addComponent(f)
                        .addContainerGap(vcgap, Short.MAX_VALUE))
        );

        pack();
    }
    
    /**
    * Returns the VCFInfoHeaderLine with provided id.
    * 
    * @param id ID of the annotation
    * @return VCFInfoHeaderLine
    * @author Heiko Müller
    * @since 1.0
    */
    public VCFInfoHeaderLine getInfoHeaderLine(String id){
        Iterator<VCFInfoHeaderLine> it = infoHeaderLines.iterator();
        while(it.hasNext()){
            VCFInfoHeaderLine hl = it.next();
            if(hl.getID().equals(id)){
                return hl;
            }
        }
        return null;
    }
    
    /**
    * Tests if provided String is an operator.
    * 
    * @param o
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    private String isOperator(String o){
        if(o.equals(">")){
            return ">";
        }else if(o.equals("<")){
            return "<";
        }
        else if(o.equals("=")){
            return "=";
        }else{
            return "=";
        }
    }
    
    /**
    * Chooses the ini file to be read depending on the presence/absence of 
    * VCFFilter_preferences.ini and VCFFilter_preferences_$username.ini.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public static void readIni() {
        //VCFFileReader vcf = new VCFFileReader(new File("C:\\Temp\\1149.vcf.gz"));
        userdir = System.getProperties().getProperty("user.dir");  
        userdir = new File(userdir).toURI().getPath(); 
        username = UserName.getUserName(); 
        iniFile = new File(userdir + File.separator + "VCFFilter_preferences_" + username + ".ini");
        if(iniFile == null || !iniFile.exists()){
            iniFile = new File(userdir + File.separator + "VCFFilter_preferences.ini");
            if(!iniFile.exists()){
                iniFile = null;
            }
        }else{        
            if(iniFile != null && iniFile.exists()){
                readIni(iniFile.getAbsolutePath());
            }else{
                iniFileContent = "";
            }
        }     
    }
    
    /**
    * Reads the .ini file.
    * 
    * @param filename ini file name
    * @author Heiko Müller
    * @since 1.0
    */
    public static void readIni(String filename) {
        Vector<String> result = new Vector<String>();
        String line = null;
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                //if(line != null){
                result.addElement(line);
                //}
            }
        }//end try
        catch (IOException ioe) {
            ioe.printStackTrace();
            //userdir = System.getProperties().getProperty("user.dir");
            //userdir = new File(userdir).toURI().getPath();
        }//end catch
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.size(); i++) {
            sb.append(result.elementAt(i).trim() + "\n");
        }
        iniFileContent = sb.toString();
        //parseIni(sb.toString());
    }

    /**
    * Parses the .ini file.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void parseIni() throws NoExampleVCFFileException{        
        String[] lines = iniFileContent.split("\n");
        ht = new Hashtable<String, String>();
        for (int i = 0; i < lines.length; i++) {
            //System.out.println(lines[i]);
            if (!(lines[i].startsWith("[") || lines[i].startsWith("#")) && lines[i].length() > 0) {
                String[] parameter = new String[2];
                parameter[0] = lines[i].substring(0, lines[i].indexOf("="));
                parameter[1] = lines[i].substring(lines[i].indexOf("=") + 1);
                //System.out.println(parameter[0]);
                //System.out.println(parameter[1]);
                ht.put(parameter[0], parameter[1]);
            }
        }

        if (ht.containsKey("sampleVCFFile")) {
            String temp = ht.get("sampleVCFFile");
            if (temp.length() > 0 && !temp.equals("null")) {
                sampleVCFFile = new File(temp);
                jTextField1.setText(sampleVCFFile.getAbsolutePath());
                jFileChooser1.setSelectedFile(sampleVCFFile);
                initVCFHeaderLinesCollection();
            }else{
                sampleVCFFile = null;
            }
        }

        if (ht.containsKey("recurrenceFile")) {
            if(recurrenceFiles == null){
                recurrenceFiles = new ArrayList<File>();
            }
            String temp = ht.get("recurrenceFile");
            if (temp.length() > 0 && !temp.equals("null")) {
                String[] sa = temp.split(";");
                if(sa != null && sa.length >= 1){
                    for(String s : sa){
                        File f = new File(s.trim());
                        if(f.exists()){
                            recurrenceFiles.add(f);
                        }
                    }                    
                }
                setTextField(jTextField2, recurrenceFiles);              
            }else{
                recurrenceFiles = null;
            }
        }
        
        if (ht.containsKey("whiteListFile")) {
            if(whiteListFiles == null){
                whiteListFiles = new ArrayList<File>();
            }
            String temp = ht.get("whiteListFile");
            if (temp.length() > 0 && !temp.equals("null")) {
                String[] sa = temp.split(";");
                if(sa != null && sa.length >= 1){
                    for(String s : sa){
                        File f = new File(s.trim());
                        if(f.exists()){
                            whiteListFiles.add(f);
                        }
                    }                    
                }
                setTextField(jTextField7, whiteListFiles);              
            }else{
                whiteListFiles = null;
            }
        }
        
        if (ht.containsKey("genesymbolField")) {
            String temp = ht.get("genesymbolField");
            if (temp != null && temp.length() > 0 && !temp.equals("null")) {
                genesymbolField = temp;
                //System.out.println(genesymbolField);
                int index = getVCFInfoHeaderLineIndexByID(genesymbolField);
                initGenesymbolCombobox();
                if(index >= 0 && index < infoHeaderLines.size()){
                    jComboBox2.setSelectedIndex(index);        
                }        
            }else{
                genesymbolField = null;
            }
        }
        
        if (ht.containsKey("outputlimit")) {
            String temp = ht.get("outputlimit");
            if (temp != null && temp.length() > 0 && !temp.equals("null")) {
                try{
                    outputlimit = Integer.parseInt(temp);
                    jTextField9.setText("" + outputlimit);
                }catch(NumberFormatException nfe){
                    outputlimit = 500000;
                    jTextField9.setText("500000");
                }                        
            }else{
                outputlimit = 500000;
                jTextField9.setText("500000");
            }
        }
        
        if (ht.containsKey("blackListFile")) {
            if(blackListFiles == null){
                blackListFiles = new ArrayList<File>();
            }
            String temp = ht.get("blackListFile");
            if (temp.length() > 0 && !temp.equals("null")) {
                String[] sa = temp.split(";");
                if(sa != null && sa.length >= 1){
                    for(String s : sa){
                        File f = new File(s.trim());
                        if(f.exists()){
                            blackListFiles.add(f);
                        }
                    }                    
                }
                setTextField(jTextField8, blackListFiles);              
            }else{
                blackListFiles = null;
            }
        }
        
        if (ht.containsKey("DefaultDir")) {
            String temp = ht.get("DefaultDir");
            if (temp.length() > 0) {
                DefaultDir = new File(temp);
                jTextField6.setText(DefaultDir.getAbsolutePath());
                jFileChooser3.setSelectedFile(DefaultDir);
            }
        }

        if (ht.containsKey("LookAndFeel")) {
            String temp = ht.get("LookAndFeel");
            if (temp.equals(windowsLookAndFeel) || temp.equals(motifLookAndFeel)
                    || temp.equals(metalLookAndFeel) || temp.equals(nimbusLookAndFeel) || temp.equals(windowsclassicLookAndFeel)) {
                LookAndFeel = temp;
                //setUIManager();
            }
            if (LookAndFeel.equals(windowsLookAndFeel)) {
                jRadioButton1.setSelected(true);

            }
            if (LookAndFeel.equals(motifLookAndFeel)) {
                jRadioButton2.setSelected(true);
            }
            if (LookAndFeel.equals(metalLookAndFeel)) {
                jRadioButton3.setSelected(true);
            }
            if (LookAndFeel.equals(nimbusLookAndFeel)) {
                jRadioButton4.setSelected(true);
            }
            if (LookAndFeel.equals(windowsclassicLookAndFeel)) {
                jRadioButton5.setSelected(true);
            } 
        }
        
        ArrayList<String> links = getHyperlinkIDs(ht);
        hyperlinks = new ArrayList<Hyperlink>();
        for(String s : links){
            String id = ht.get("Hyperlink_" + s + ".id");
            String left = ht.get("Hyperlink_" + s + ".left");
            //String search = ht.get("Hyperlink_" + s + ".search");
            String right = ht.get("Hyperlink_" + s + ".right");
            if(id != null){   
                if(left == null){left = "";}
                if(right == null){right = "";}
                //if(search == null){search = "";}
                hyperlinks.add(new Hyperlink(id, left, id, right, this));
            }            
        } 
        Collections.sort(hyperlinks);
        
    }
    

    /**
    * Saves the .ini file.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void saveIni() {
        StringBuffer sb = new StringBuffer();

        sb.append("[Sample VCF file]\r\n");
        if (sampleVCFFile != null) {
            sb.append("sampleVCFFile=" + sampleVCFFile.toURI().getPath()  + "\r\n\r\n");
        } else {
            sb.append("sampleVCFFile=null" + "\r\n\r\n");
        }

        sb.append("[Recurrence file]\r\n");
        if (recurrenceFiles != null) {
            sb.append("recurrenceFile=");
            for(File f : recurrenceFiles){
                sb.append(f.toURI().getPath() + "; ");
            }
            sb.append("\r\n\r\n");
        } else {
            sb.append("recurrenceFile=null" + "\r\n\r\n");
        }
        
        sb.append("[White list file]\r\n");
        if (whiteListFiles != null) {
            sb.append("whiteListFile=");
            for(File f : whiteListFiles){
                sb.append(f.toURI().getPath() + "; ");
            }
            sb.append("\r\n\r\n");
        } else {
            sb.append("whiteListFile=null" + "\r\n\r\n");
        }
        
        sb.append("[Black list file]\r\n");
        if (blackListFiles != null) {
            sb.append("blackListFile=");
            for(File f : blackListFiles){
                sb.append(f.toURI().getPath() + "; ");
            }
            sb.append("\r\n\r\n");
        } else {
            sb.append("blackListFile=null" + "\r\n\r\n");
        }

        sb.append("[Default directory]\r\n");
        if (DefaultDir != null) {
            sb.append("DefaultDir=" + DefaultDir.toURI().getPath() + "\r\n\r\n");
        } else {
            sb.append("DefaultDir=null\r\n\r\n");
        }
        
        sb.append("[Gene symbol field]\r\n");
        if (genesymbolField != null) {
            sb.append("genesymbolField=" + genesymbolField + "\r\n\r\n");
        } else {
            sb.append("genesymbolField=null\r\n\r\n");
        }
        
        sb.append("[Output limit]\r\n");
        try{
            outputlimit = Integer.parseInt(jTextField9.getText());
        }catch(NumberFormatException nfe){
            outputlimit = 500000;
        }
        sb.append("outputlimit=" + outputlimit + "\r\n\r\n");
        

        for (VCFInfoHeaderLine h : infoHeaderLines) {
            if(filterDefaultsHash != null){
                FilterDefaults fd = filterDefaultsHash.get(h.getID());
                if(fd != null){
                    sb.append("[" + h.getID() + "]\r\n");
                    sb.append(h.getID() + "=" +  h.toString() + "\r\n");
                    sb.append(h.getID() + ".loaded=" +  listContains(jList4, h.getID()) + "\r\n");
                    sb.append(h.getID() + ".andnot=" +  fd.isAndnot() + "\r\n");
                    sb.append(h.getID() + ".operator1=" +  fd.getOperator1() + "\r\n");
                    sb.append(h.getID() + ".criterion1=" +  fd.getCriterion1() + "\r\n");
                    sb.append(h.getID() + ".operator2=" +  fd.getOperator2() + "\r\n");
                    sb.append(h.getID() + ".criterion2=" +  fd.getCriterion2() + "\r\n");
                    sb.append(h.getID() + ".operator3=" +  fd.getOperator3() + "\r\n");
                    sb.append(h.getID() + ".criterion3=" +  fd.getCriterion3() + "\r\n");
                    sb.append(h.getID() + ".outputOrder=" +  (listIndex(jList2, h.getID()) + 1) + "\r\n\r\n");
                }
            }else{
                sb.append("[" + h.getID() + "]\r\n");
                sb.append(h.getID() + "=" +  h.toString() + "\r\n");
                sb.append(h.getID() + ".loaded=" +  false + "\r\n");
                sb.append(h.getID() + ".andnot=" +  false + "\r\n");
                sb.append(h.getID() + ".operator1=" + "\r\n");
                sb.append(h.getID() + ".criterion1=" + "\r\n");
                sb.append(h.getID() + ".operator2=" + "\r\n");
                sb.append(h.getID() + ".criterion2=" + "\r\n");
                sb.append(h.getID() + ".operator3=" + "\r\n");
                sb.append(h.getID() + ".criterion3=" + "\r\n");
                sb.append(h.getID() + ".outputOrder=0" + "\r\n\r\n");
            }
        }

        sb.append("[LookAndFeel]\r\n");
        sb.append("LookAndFeel=" + LookAndFeel + "\r\n\r\n");
        
        //sb.append("[Hyperlinks]\r\n");
        if (hyperlinks != null) {
            for (Hyperlink h : hyperlinks){
                sb.append("[Hyperlink_" + h.getID() + "]\r\n"); 
                sb.append("Hyperlink_" + h.getID() + ".id=" + h.getID() + "\r\n");
                sb.append("Hyperlink_" + h.getID() + ".left=" + h.getLeft() + "\r\n");
                //sb.append("Hyperlink_" + h.getID() + ".search=" + h.getSearch() + "\r\n");
                sb.append("Hyperlink_" + h.getID() + ".right=" + h.getRight() + "\r\n");
                sb.append("\r\n\r\n");

            }
        }

        if (iniFile != null) {
            try {
                saveIniBak();
                FileWriter fw = new FileWriter(iniFile);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }else{
            try {
                FileWriter fw = new FileWriter(userdir + File.separator + "VCFFilter_preferences.ini");
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

    }
    
    /**
    * Saves the .ini.bak backup file.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void saveIniBak(){
        if (iniFile != null){
            if(iniFile.exists()){
                iniFile.renameTo(new File(iniFile.getAbsolutePath() + ".bak"));
            }
        }
    }
    
    /**
    * Tests if JList contains a given entry.
    * 
    * @param list
    * @param entry
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean listContains(JList list, String entry){
        ListModel listModel = (DefaultListModel)list.getModel();
        for(int i = 0; i < listModel.getSize(); i++){
            if(entry.equals(listModel.getElementAt(i).toString())){
                return true;
            }
        }
        return false;
    }
    
    /**
    * Returns the list of Hyperlink IDs defined in the .ini file.
    * 
    * @param hash
    * @return ArrayList&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<String> getHyperlinkIDs(Hashtable<String, String> hash){
        ArrayList<String> result = new ArrayList<String>();
        Set<String> keys = hash.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String next = it.next();
            if(next.startsWith("Hyperlink_")){
                String id = next.substring(10, next.indexOf("."));
                if(!result.contains(id)){
                    //System.out.println(id);
                    result.add(id);
                }
            }
        }
        return result;
    }
    
    /**
    * Returns the index of a given JList entry.
    * 
    * @param list
    * @param entry
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    private int listIndex(JList list, String entry){
        ListModel listModel = list.getModel();
        for(int i = 0; i < listModel.getSize(); i++){
            if(entry.equals(listModel.getElementAt(i).toString())){
                return i;
            }
        }
        return -1;
    }

    /**
    * Sets current look and feel according to user choice.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void setLookAndFeel() {
        if(jRadioButton1.isSelected()) {
            LookAndFeel = windowsLookAndFeel;
        }else if (jRadioButton2.isSelected()) {
            LookAndFeel = motifLookAndFeel;
        }else if (jRadioButton3.isSelected()) {
            LookAndFeel = metalLookAndFeel;
        }else if (jRadioButton4.isSelected()) {
            LookAndFeel = nimbusLookAndFeel;
        }else if (jRadioButton5.isSelected()) {
            LookAndFeel = windowsclassicLookAndFeel;
        }
    }

    /**
    * Getter for the VCFHeaderLines collection.
    * 
    * @return Collection&#60;VCFInfoHeaderLine&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public Collection<VCFInfoHeaderLine> getInfoHeaderLines() {
        initVCFHeaderLinesCollection();
        return infoHeaderLines;
    }

    /**
    * Getter for the sample VCF file.
    * 
    * @return File
    * @author Heiko Müller
    * @since 1.0
    */
    public File getSampleVCFFile() {
        return sampleVCFFile;
    } 

    /**
    * Setter for the sample VCF file.
    * 
    * @param sampleVCFFile the VCF file used to read available annotations
    * @author Heiko Müller
    * @since 1.0
    */
    public void setSampleVCFFile(File sampleVCFFile) {
        this.sampleVCFFile = sampleVCFFile;
    }
    
    
    
    /**
    * Getter for the default directory.
    * 
    * @return File
    * @author Heiko Müller
    * @since 1.0
    */
    public File getDefaultDir() {
        return DefaultDir;
    }

    /**
    * Getter for the default directory.
    * 
    * @param DefaultDir the directory opened by default by a FileChooser
    * @author Heiko Müller
    * @since 1.0
    */
    public void setDefaultDir(File DefaultDir) {
        this.DefaultDir = DefaultDir;
    }
    
    

    /**
    * Getter for the user directory.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public static String getUserdir() {
        return userdir;
    }
    
    /**
    * Getter for the filter defaults hash.
    * 
    * @return Hashtable&#60;String, FilterDefaults&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public Hashtable<String, FilterDefaults> getFilterDefaultsHash() {
        return filterDefaultsHash;
    }
    
    /**
    * Getter for the recurrence file list.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getRecurrenceFiles() {
        return recurrenceFiles;
    }
    
    /**
    * Getter for the white list file list.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getWhiteListFiles(){
        return whiteListFiles;
    }
    
    /**
    * Getter for the black list file list.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getBlackListFiles(){
        return blackListFiles;
    }

    /**
    * Setter for the recurrence file list.
    * 
    * @param recurrenceFiles the list of recurrence files
    * @author Heiko Müller
    * @since 1.0
    */
    public void setRecurrenceFiles(ArrayList<File> recurrenceFiles) {
        this.recurrenceFiles = recurrenceFiles;
    }

    /**
    * Setter for the white list file list.
    * 
    * @param whiteListFiles  the list of white list files
    * @author Heiko Müller
    * @since 1.0
    */
    public void setWhiteListFiles(ArrayList<File> whiteListFiles) {
        this.whiteListFiles = whiteListFiles;
    }

    /**
    * Setter for the black list file list.
    * 
    * @param blackListFiles the list of black list files
    * @author Heiko Müller
    * @since 1.0
    */
    public void setBlackListFiles(ArrayList<File> blackListFiles) {
        this.blackListFiles = blackListFiles;
    }

    /**
    * Getter for the gene symbol annotation field.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getGenesymbolField() {
        return genesymbolField;
    }    
    
    /**
    * Called when user chooses Save setting in the VCFFilter Menu.
    * Replaces current filters preferences with the filters loaded in VCFFilter.
    * 
    * @param filters filters loaded on the GUI tab that has the focus
    * @author Heiko Müller
    * @since 1.0
    */
    public void saveFilterSettings(ArrayList<Filter> filters){
        clearLoadedFilters();
        DefaultListModel lm4 = new DefaultListModel();
        for(Filter f : filters){
            filterDefaultsHash.get(f.getID()).set(f);
            lm4.addElement(f.getID());
        } 
        jList4.setModel(lm4);
    }
    
    /**
    * Called when user chooses Save setting in the VCFFilter Menu.
    * Clears current loaded filters preferences.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void clearLoadedFilters(){
        DefaultListModel lm3 = (DefaultListModel)jList3.getModel();
        DefaultListModel lm4 = (DefaultListModel)jList4.getModel();
        for(int i = 0; i < lm4.getSize(); i++){
            Object o = lm4.getElementAt(i);
            lm3.addElement(o);            
        }
        jList3.setModel(lm3);
        jList4.setModel(new DefaultListModel());        
    }
    
    /**
    * Returns the VCFInfoHeaderLines for the current loaded filters preferences.
    * 
    * @return List&#60;VCFInfoHeaderLine&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public List<VCFInfoHeaderLine> getLoadedFilters(){
        ListModel model = jList4.getModel();
        int number = jList4.getModel().getSize();
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0; i < number; i++){
            temp.add((String)model.getElementAt(i));
        }
        ArrayList<VCFInfoHeaderLine> result = new ArrayList<VCFInfoHeaderLine>();
        for(String s : temp){
            VCFInfoHeaderLine hl = getVCFInfoHeaderLineByID(s);
            if(hl != null){
                result.add(hl);
            }
        }
        return result;
    }
    
    /**
    * Returns the VCFInfoHeaderLines for the current non-loaded filters preferences.
    * 
    * @return List&#60;VCFInfoHeaderLine&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public List<VCFInfoHeaderLine> getNonLoadedFilters(){
        ListModel model = jList3.getModel();
        int number = jList3.getModel().getSize();
        ArrayList<String> temp = new ArrayList<String>();
        for(int i = 0; i < number; i++){
            temp.add((String)model.getElementAt(i));
        }
        ArrayList<VCFInfoHeaderLine> result = new ArrayList<VCFInfoHeaderLine>();
        for(String s : temp){
            VCFInfoHeaderLine hl = getVCFInfoHeaderLineByID(s);
            if(hl != null){
                result.add(hl);
            }
        }
        return result;
    }
    
    /**
    * Returns the VCFInfoHeaderLines for the available filters.
    * 
    * @return List&#60;VCFInfoHeaderLine&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public List<VCFInfoHeaderLine> getAvailableFilters(){        
        return infoHeaderLines;
    }
    
    /**
    * Returns the VCFInfoHeaderLine with the provided id.
    * 
    * @param id the ID of the annotation
    * @return VCFInfoHeaderLine
    * @author Heiko Müller
    * @since 1.0
    */
    public VCFInfoHeaderLine getVCFInfoHeaderLineByID(String id){
        for(VCFInfoHeaderLine v : infoHeaderLines){
            if(v.getID().equals(id)){
                return v;
            }
        }
        return null;
    }
    
    /**
    * Returns the VCFInfoHeaderLine index in the infoHeaderLines object corresponding to the provided id.
    * 
    * @param id the ID of the annotation
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getVCFInfoHeaderLineIndexByID(String id){
        int counter = 0;
        if(id == null){
            return -1;
        }
        for(VCFInfoHeaderLine v : infoHeaderLines){
            if(v.getID().equals(id)){
                return counter;
            }
            counter++;
        }
        return -1;
    }

    /**
    * Makes the current look and feel effective.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void setUIManager() {
        try {
            UIManager.setLookAndFeel(LookAndFeel);
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (UnsupportedLookAndFeelException ulaf) {
            ulaf.printStackTrace();
        }
    }
    
    /**
    * Centers the preferences window on the screen.
    * 
    * @param frame the frame to be centered
    * @author Heiko Müller
    * @since 1.0
    */
    public void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    /**
    * Sets the JTextField text according to provided list of files.
    * 
    * @param jt the text field
    * @param files
    * @author Heiko Müller
    * @since 1.0
    */
    private void setTextField(JTextField jt, ArrayList<File> files){
        StringBuilder sb = new StringBuilder();
        for(File f : files){
            sb.append(f.getName() + "; ");
        }
        jt.setText(sb.toString());
    }
    
    /**
    * Returns the number of output fields.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getNumberOfOutputFields(){
        return jList2.getModel().getSize();
    }

    /**
    * Sets the reference to the VCFFilter instance.
    * 
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public void setGui(VCFFilter gui) {
        this.gui = gui;
    }

    /**
    * Getter for the list of Hyperlinks.
    * 
    * @return ArrayList&#60;Hyperlink&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<Hyperlink> getHyperlinks() {
        return hyperlinks;
    }

    /**
    * Getter for the output limit.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getOutputlimit() {        
        try{
            outputlimit = Integer.parseInt(jTextField9.getText());
            return outputlimit;
        }catch(NumberFormatException nfe){
            return 500000;
        }        
    }
    
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VCFFilterPreferences.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VCFFilterPreferences.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VCFFilterPreferences.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VCFFilterPreferences.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VCFFilterPreferences dialog = new VCFFilterPreferences(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JFileChooser jFileChooser3;
    private javax.swing.JFileChooser jFileChooser4;
    private javax.swing.JFileChooser jFileChooser5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
