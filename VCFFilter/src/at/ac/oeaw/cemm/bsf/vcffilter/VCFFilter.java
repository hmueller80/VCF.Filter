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
package at.ac.oeaw.cemm.bsf.vcffilter;

import at.ac.oeaw.cemm.bsf.vcffilter.noprefsstartup.StartupWizard;
import at.ac.oeaw.cemm.bsf.vcffilter.preferences.VCFFilterPreferences;
import at.ac.oeaw.cemm.bsf.vcffilter.worker.RecurrenceCalculationWorker;
import at.ac.oeaw.cemm.bsf.vcffilter.worker.FilterWorker;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.FilterFactory;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.ExampleFileFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.FilterDefaults;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.FilterSettings;
import at.ac.oeaw.cemm.bsf.vcffilter.index.IndexedVCFFileWriter;
import at.ac.oeaw.cemm.bsf.vcffilter.inheritance.Relationship;
import at.ac.oeaw.cemm.bsf.vcffilter.inheritance.Relationships;
import at.ac.oeaw.cemm.bsf.vcffilter.worker.FamilyAnalysisWorker;
import at.ac.oeaw.cemm.bsf.vcffilter.worker.SearchWorker;
import htsjdk.variant.variantcontext.VariantContext;
//import htsjdk.tribble.TribbleException;
import htsjdk.variant.vcf.VCFCompoundHeaderLine;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class of VCFFilter application. Renders graphical user interface.
 * VCFFilter.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class VCFFilter extends javax.swing.JFrame {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The version number of this class.
     */
    static ImageIcon icon;

    /**
     * VCF files loaded in the Filter tab.
     */
    private File[] selectedVCFFiles;
    
    /**
     * VCF files highlighted in the Filter tab. Only these files will be searched.
     */
    private File[] activeVCFFiles;
    
    /**
     * VCF files of affected individuals loaded in the Family analysis tab.
     */
    private File[] selectedAffectedVCFFiles;
    
    /**
     * VCF files of affected individuals highlighted in Family analysis tab. Only these files will be searched.
     */
    private File[] activeAffectedVCFFiles;
    
    /**
     * VCF files of unaffected individuals loaded in the Family analysis tab.
     */
    private File[] selectedUnaffectedVCFFiles;
    
    /**
     * VCF files of unaffected individuals highlighted in Family analysis tab. Only these files will be searched.
     */
    private File[] activeUnaffectedVCFFiles;
    
    /**
     * VCF files loaded in the Search tab
     */
    private File[] selectedSearchVCFFiles;
    
    /**
     * VCF files highlighted in the Search tab. Only these files will be searched.
     */
    private File[] activeSearchVCFFiles;
    
    /**
     * List of loaded filters in the Filter tab.
     */
    private ArrayList<Filter> filters;
    
    /**
     * List of filters with valid search criteria in the Filter tab.
     * This list is usually identical to the filters list or a subset of it.
     */
    private ArrayList<Filter> activeFilters;
    
    /**
     * List of loaded filters in the Family analysis tab.
     */
    private ArrayList<Filter> familyFilters;
    
    /**
     * List of filters with valid search criteria in the Family analysis tab.
     * This list is usually identical to the familyFilters list or a subset of it.
     */
    private ArrayList<Filter> activeFamilyFilters;
    
    /**
     * List of recurrence files loaded in the Filter tab.
     */
    private ArrayList<File> filterRecurrenceFiles;
    
    /**
     * Recurrence file highlighted in the Filter tab.
     */
    private File filterRecurrenceFile;
    
    /**
     * List of recurrence files loaded in the Family analysis tab.
     */
    private ArrayList<File> familyRecurrenceFiles;
    
    /**
     * Recurrence file highlighted in the Family analysis tab.
     */
    private File familyRecurrenceFile;
    
    /**
     * List of white list files loaded in the Filter tab.
     */
    private ArrayList<File> filterWhiteListFiles;
    
    /**
     * List of white list files highlighted in the Filter tab.
     */
    private ArrayList<File> filterActiveWhiteListFiles;
    
    /**
     * List of black list files loaded in the Filter tab.
     */
    private ArrayList<File> filterBlackListFiles;
    
    /**
     * List of black list files highlighted in the Filter tab.
     */
    private ArrayList<File> filterActiveBlackListFiles;
    
    /**
     * List of white list files loaded in the Family analysis tab.
     */
    private ArrayList<File> familyWhiteListFiles;
    
    /**
     * List of white list files highlighted in the Family analysis tab.
     */
    private ArrayList<File> familyActiveWhiteListFiles;
    
    /**
     * List of black list files loaded in the Family analysis tab.
     */
    private ArrayList<File> familyBlackListFiles;   
    
    /**
     * List of black list files highlighted in the Family analysis tab.
     */
    private ArrayList<File> familyActiveBlackListFiles;
    
    /**
     * Indicator whether Cancel button was pressed in the Filter tab.
     */
    private boolean cancelFilterWorker = false;
    
    /**
     * Indicator whether Cancel button was pressed in the Family analysis tab.
     */    
    private boolean cancelFamilyWorker = false;
    
    /**
     * Indicator whether Cancel button was pressed in the Search tab.
     */ 
    private boolean cancelSearchWorker = false;

    /**
     * Reference to the VCFFilterPreferences object.
     */ 
    private VCFFilterPreferences PREFERENCES; 
    
    /**
     * The file where VCF output will be written to.
     */ 
    private File vcfOutputFile;
    
    /**
     * Determines the vertical gap between loaded filters.
     */ 
    private int filterVerticalGap = 34;
    
    /**
     * Determines the horizontal size of JFileChoosers.
     */ 
    private int fileChooserHorizontalSize = 800;
    
    /**
     * Determines the vertical size of JFileChoosers.
     */ 
    private int fileChooserVerticalSize = 600;
    
    /**
     * FilterWorker.
     */ 
    private FilterWorker filterWorker;
    
    /**
     * FamilyWorker.
     */ 
    private FamilyAnalysisWorker familyWorker;
    
    /**
     * Relationships.
     */ 
    private Relationships relationships;
    
    /**
     * determines what recurrence type to filter on
     */ 
    private String recurrenceType = "total";    
    

    /**
     * Creates new form VCFFilter
     */
    public VCFFilter() {   
        //ClassLoader cl = this.getClass().getClassLoader();
        //cl.getResource("/lib/htsjdk_631.jar");
        //cl.getResource("/lib/picard-2.5.0-gradle-4-gc6e4bce-SNAPSHOT.jar");
        //try{
        //    Class c = cl.loadClass("htsjdk.tribble.TribbleException");
        //    System.out.println("TribbleException loaded");
        //}catch(Exception e){
        //    System.out.println("TribbleException not loaded");
        //}
        
           
        PREFERENCES.readIni();
        if(PREFERENCES.iniFile != null && PREFERENCES.iniFile.exists()){            
            PREFERENCES = new VCFFilterPreferences(this, true);              
            PREFERENCES.readIni(PREFERENCES.iniFile.getAbsolutePath());  
            try{
                PREFERENCES.parseIni();
            }catch(NoExampleVCFFileException nve){
                StartupWizard wizard = new StartupWizard(this, true);
                wizard.setVisible(true);
                reInit();  
            }
            
            try{       
                UIManager.setLookAndFeel(PREFERENCES.LookAndFeel);                 
            }catch(ClassNotFoundException cnfe){
                cnfe.printStackTrace();
            }catch(InstantiationException ie){
                ie.printStackTrace();
            }catch(IllegalAccessException iae){
                iae.printStackTrace();
            }catch(UnsupportedLookAndFeelException ulaf){
                ulaf.printStackTrace();
            }   
            PREFERENCES = new VCFFilterPreferences(this, true);  
            PREFERENCES.parseIni();
            if(PREFERENCES.getSampleVCFFile() != null && PREFERENCES.getSampleVCFFile().exists()){
                filterRecurrenceFiles = PREFERENCES.getRecurrenceFiles();
                PREFERENCES.initVCFHeaderLinesCollection();
                PREFERENCES.initAvailableFiltersList();
                PREFERENCES.initSelectedFiltersList();            
                PREFERENCES.initFilterDefaultsHash();
                PREFERENCES.initAvailableOutputList();
                PREFERENCES.initSelectedOutputList();                
                if(PREFERENCES.getNumberOfOutputFields() == 0){
                    PREFERENCES.makeAllOutputVisible();
                }
                PREFERENCES.initGenesymbolCombobox();
                PREFERENCES.setGui(this);
                PREFERENCES.initHyperlinks();
            }else{            
                //PREFERENCES.setGui(this);
                //PREFERENCES.setVisible(true);
                
                StartupWizard wizard = new StartupWizard(this, true);
                wizard.setVisible(true);
                reInit();        
            }
        }else{            
            //PREFERENCES = new VCFFilterPreferences(this, true);
            //PREFERENCES.setVisible(true);
            //PREFERENCES.saveIni();
            //new Warning(this, "Please restart the application now.");
            //System.exit(0);
            
            StartupWizard wizard = new StartupWizard(this, true);
            wizard.setVisible(true);
            reInit();            
            
        }      
 
        initComponents();
        initFilterTab();        
        initFamilyTab();
        initSearchTab();
        //icon = new ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/NCicon.GIF"));        
        //icon = new ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/logoTH.jpg"));
        
        icon = new ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/logoVCFF.png"));
        //icon = new ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/VCF_Filter_v3-2_large.png"));
        
        this.setIconImage(icon.getImage());
        pack();
        centerWindow(this);
        if(filters == null || filters.size() == 0){
            new Warning(this, "Open a filter scenario to load filters.");
        }
    }
    
    private void reInit(){
        PREFERENCES.readIni();
        if(PREFERENCES.iniFile != null && PREFERENCES.iniFile.exists()){ 
            PREFERENCES = new VCFFilterPreferences(this, true);              
            PREFERENCES.readIni(PREFERENCES.iniFile.getAbsolutePath());  
            PREFERENCES.parseIni();
            
            try{       
                UIManager.setLookAndFeel(PREFERENCES.LookAndFeel);                 
            }catch(ClassNotFoundException cnfe){
                cnfe.printStackTrace();
            }catch(InstantiationException ie){
                ie.printStackTrace();
            }catch(IllegalAccessException iae){
                iae.printStackTrace();
            }catch(UnsupportedLookAndFeelException ulaf){
                ulaf.printStackTrace();
            }   
            PREFERENCES = new VCFFilterPreferences(this, true);  
            PREFERENCES.parseIni();
            if(PREFERENCES.getSampleVCFFile() != null && PREFERENCES.getSampleVCFFile().exists()){
                filterRecurrenceFiles = PREFERENCES.getRecurrenceFiles();
                PREFERENCES.initVCFHeaderLinesCollection();
                PREFERENCES.initAvailableFiltersList();
                PREFERENCES.initSelectedFiltersList();            
                PREFERENCES.initFilterDefaultsHash();
                PREFERENCES.initAvailableOutputList();
                PREFERENCES.initSelectedOutputList();                
                if(PREFERENCES.getNumberOfOutputFields() == 0){
                    PREFERENCES.makeAllOutputVisible();
                }
                PREFERENCES.initGenesymbolCombobox();
                PREFERENCES.setGui(this);
                PREFERENCES.initHyperlinks();
            }else{            
                PREFERENCES.setGui(this);
                PREFERENCES.setVisible(true);
            }
        }
    }
    
    /**
    * Centers this frame on the screen according to screen resolution.
    *
    * @author Heiko Müller
    * @param frame the frame to be centered
    * @since 1.0
    */
    public void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    /**
    * Initializes the Filter tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initFilterTab() {
        ExampleFileFilter vcfFilter = new ExampleFileFilter(new String[]{"vcf", "gz"}, "VCF files");
        jFileChooser1.addChoosableFileFilter(vcfFilter);
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser14.addChoosableFileFilter(vcfFilter);
        jFileChooser14.setAcceptAllFileFilterUsed(false);
        setVCFDefaultDir(jFileChooser1);
        setVCFDefaultDir(jFileChooser14);        
        ExampleFileFilter tsvFilter = new ExampleFileFilter(new String[]{"tsv"}, "Recurrence files in tsv format");
        jFileChooser2.addChoosableFileFilter(tsvFilter);
        jFileChooser2.setAcceptAllFileFilterUsed(false);
        jFileChooser17.addChoosableFileFilter(tsvFilter);
        jFileChooser17.setAcceptAllFileFilterUsed(false);
        setRecurrenceDefaultDir(jFileChooser2);
        setRecurrenceDefaultDir(jFileChooser17);
        ExampleFileFilter bedFilter = new ExampleFileFilter(new String[]{"bed", "hom"}, "bed and PLINK hom files");
        jFileChooser10.addChoosableFileFilter(bedFilter);
        jFileChooser10.setAcceptAllFileFilterUsed(false);   
        jFileChooser11.addChoosableFileFilter(bedFilter);
        jFileChooser11.setAcceptAllFileFilterUsed(false); 
        jFileChooser15.addChoosableFileFilter(bedFilter);
        jFileChooser15.setAcceptAllFileFilterUsed(false); 
        jFileChooser16.addChoosableFileFilter(bedFilter);
        jFileChooser16.setAcceptAllFileFilterUsed(false); 
        setListDefaultDir(jFileChooser10);
        setListDefaultDir(jFileChooser11);
        setListDefaultDir(jFileChooser15);
        setListDefaultDir(jFileChooser16);
        ExampleFileFilter fscFilter = new ExampleFileFilter(new String[]{"fsc"}, "Filter scenario");
        jFileChooser12.addChoosableFileFilter(fscFilter);
        jFileChooser12.setAcceptAllFileFilterUsed(false); 
        jFileChooser13.addChoosableFileFilter(fscFilter);
        jFileChooser13.setAcceptAllFileFilterUsed(false);
        setScenarioDefaultDir(jFileChooser12);
        setScenarioDefaultDir(jFileChooser13);   
        ExampleFileFilter vcfonlyFilter = new ExampleFileFilter(new String[]{"vcf", "gz"}, "VCF files");        
        jFileChooser9.addChoosableFileFilter(vcfonlyFilter);
        jFileChooser9.setAcceptAllFileFilterUsed(false); 
        ExampleFileFilter vcfindex = new ExampleFileFilter(new String[]{"vcf", "gz"}, "VCF files");        
        jFileChooser26.addChoosableFileFilter(vcfindex);
        jFileChooser26.setAcceptAllFileFilterUsed(false); 
        jFileChooser26.setMultiSelectionEnabled(true);
        setVCFDefaultDir(jFileChooser26);
        ExampleFileFilter bedFilter2 = new ExampleFileFilter(new String[]{"bed"}, "bed files");
        jFileChooser27.addChoosableFileFilter(bedFilter2);
        jFileChooser27.setAcceptAllFileFilterUsed(false); 
        jFileChooser27.setMultiSelectionEnabled(false);
        setVCFDefaultDir(jFileChooser27);
        
        setFileChooserDimension(jFileChooser1);
        setFileChooserDimension(jFileChooser14);
        setFileChooserDimension(jFileChooser2);
        setFileChooserDimension(jFileChooser17);
        setFileChooserDimension(jFileChooser10);
        setFileChooserDimension(jFileChooser11);
        setFileChooserDimension(jFileChooser15);
        setFileChooserDimension(jFileChooser16);
        setFileChooserDimension(jFileChooser12);
        setFileChooserDimension(jFileChooser13);
        setFileChooserDimension(jFileChooser9);
        
        setVCFDefaultDir(jFileChooser9);
        buttonGroup1.add(jRadioButton1); 
        buttonGroup1.add(jRadioButton2); 
        buttonGroup1.add(jRadioButton3);    
        buttonGroup1.add(jRadioButton4);        
        jRadioButton1.setSelected(true);
        
        List<VCFCompoundHeaderLine> filtersToLoad = PREFERENCES.getLoadedFilters();
        filters = new ArrayList<Filter>();
        int index = 0;
        for(VCFCompoundHeaderLine s : filtersToLoad){
            Filter f = FilterFactory.getFilter(s);
            setFilterDefaults(f);
            f.setGui(this);
            f.setIndex(index);            
            filters.add(f);
            index++;
        }
        for(int i = 0; i < filters.size() && i < 10; i++){
            addToMyLayout(jPanel1, filters.get(i), 10, 10 + i*filterVerticalGap, 0, 0);
        }            
        initAddFilters();
        initRemoveFilters();
        
        
        
        this.filterRecurrenceFiles = PREFERENCES.getRecurrenceFiles();
        this.filterWhiteListFiles = PREFERENCES.getWhiteListFiles();
        this.filterBlackListFiles = PREFERENCES.getBlackListFiles();
        setListValues(jList7, PREFERENCES.getWhiteListFiles(), true, "Only matching variants will be kept.");
        setListValues(jList8, PREFERENCES.getBlackListFiles(), true, "All matching variants will be discarded.");
        setListValues(jList9, PREFERENCES.getRecurrenceFiles(), true, "Variants exceeding cutoff will be discarded.");
        jLabel9.setEnabled(false);
        jTextField3.setEnabled(false);
    }

    /**
    * Initializes the Family analysis tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initFamilyTab() {
        ExampleFileFilter vcfFilter = new ExampleFileFilter(new String[]{"vcf", "gz"}, "VCF files");
        jFileChooser4.addChoosableFileFilter(vcfFilter);
        jFileChooser5.addChoosableFileFilter(vcfFilter);
        jFileChooser4.setAcceptAllFileFilterUsed(false);
        jFileChooser5.setAcceptAllFileFilterUsed(false);
        jFileChooser18.addChoosableFileFilter(vcfFilter);
        jFileChooser18.setAcceptAllFileFilterUsed(false);
        jFileChooser19.addChoosableFileFilter(vcfFilter);
        jFileChooser19.setAcceptAllFileFilterUsed(false);
        setVCFDefaultDir(jFileChooser4);
        setVCFDefaultDir(jFileChooser5);
        setVCFDefaultDir(jFileChooser18);
        setVCFDefaultDir(jFileChooser19);
        
        ExampleFileFilter bedFilter = new ExampleFileFilter(new String[]{"bed", "hom"}, "bed and PLINK hom files");
        jFileChooser20.addChoosableFileFilter(bedFilter);
        jFileChooser20.setAcceptAllFileFilterUsed(false);   
        jFileChooser21.addChoosableFileFilter(bedFilter);
        jFileChooser21.setAcceptAllFileFilterUsed(false); 
        setListDefaultDir(jFileChooser20);
        setListDefaultDir(jFileChooser21);
        
        ExampleFileFilter tsvFilter = new ExampleFileFilter(new String[]{"tsv"}, "Recurrence files in tsv");
        jFileChooser22.addChoosableFileFilter(tsvFilter);
        jFileChooser22.setAcceptAllFileFilterUsed(false);
        setRecurrenceDefaultDir(jFileChooser22);
        
        ExampleFileFilter fscFilter = new ExampleFileFilter(new String[]{"fsc"}, "Filter scenario");
        jFileChooser24.addChoosableFileFilter(fscFilter);
        jFileChooser24.setAcceptAllFileFilterUsed(false);  
        jFileChooser25.addChoosableFileFilter(fscFilter);
        jFileChooser25.setAcceptAllFileFilterUsed(false);
        setScenarioDefaultDir(jFileChooser24);
        setScenarioDefaultDir(jFileChooser25);
        
        setFileChooserDimension(jFileChooser4);
        setFileChooserDimension(jFileChooser5);
        setFileChooserDimension(jFileChooser18);
        setFileChooserDimension(jFileChooser19);
        setFileChooserDimension(jFileChooser20);
        setFileChooserDimension(jFileChooser21);
        setFileChooserDimension(jFileChooser22);
        setFileChooserDimension(jFileChooser24);
        setFileChooserDimension(jFileChooser25);       
        
        List<VCFCompoundHeaderLine> filtersToLoad = PREFERENCES.getLoadedFilters();
        familyFilters = new ArrayList<Filter>();
        int idx = 0;
        for(VCFCompoundHeaderLine s : filtersToLoad){
            Filter f = FilterFactory.getFilter(s);
            f.setIndex(idx);
            f.setGui(this);
            setFilterDefaults(f);
            familyFilters.add(f);
            idx++;
        }

        for(int i = 0; i < familyFilters.size() && i < 10; i++){
            addToMyLayout(jPanel14,familyFilters.get(i), 10, 10 + i*filterVerticalGap, 0, 0);
        }

        initAddFamilyFilters();
        initRemoveFamilyFilters();
        
        this.familyRecurrenceFiles = PREFERENCES.getRecurrenceFiles();
        this.familyWhiteListFiles = PREFERENCES.getWhiteListFiles();
        this.familyBlackListFiles = PREFERENCES.getBlackListFiles();
        setListValues(jList2, this.familyWhiteListFiles, true, "Only matching variants will be kept.");
        setListValues(jList10, this.familyBlackListFiles, true, "All matching variants will be discarded.");
        setListValues(jList11, this.familyRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        jLabel7.setEnabled(false);
        jTextField4.setEnabled(false);
        
        buttonGroup2.add(jRadioButton5);
        buttonGroup2.add(jRadioButton6);
    }

    /**
    * Initializes the Search tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initSearchTab() {
        ExampleFileFilter vcfFilter = new ExampleFileFilter(new String[]{"vcf", "gz"}, "VCF files");
        jFileChooser3.addChoosableFileFilter(vcfFilter);
        jFileChooser3.setAcceptAllFileFilterUsed(false);
        jFileChooser23.addChoosableFileFilter(vcfFilter);
        jFileChooser23.setAcceptAllFileFilterUsed(false);
        setVCFDefaultDir(jFileChooser3);
        setVCFDefaultDir(jFileChooser23);
        setFileChooserDimension(jFileChooser3);
        setFileChooserDimension(jFileChooser23);        
    }
    
    /**
    * Initializes the Add filters JcomboBox displayed on the Filter tab 
    * according to the list of filters as defined in the VCFFilterPreferences object.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initAddFilters() {
        List<VCFCompoundHeaderLine> l = PREFERENCES.getAvailableFilters();        
        for (VCFCompoundHeaderLine hl : l) {
            String s = hl.toString();
            if (s.length() > 150) {
                jComboBox1.addItem(s.substring(0, 150));
            } else {
                jComboBox1.addItem(s);
            }
        }
    }
    
    /**
    * Initializes the Remove filters JcomboBox displayed on the Filter tab 
    * according to the list of filters currently loaded.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initRemoveFilters() {
        jComboBox2.removeAllItems();
        for (Filter f : filters) {
            if (f.getHeaderline().length() > 150) {
                jComboBox2.addItem(f.getHeaderline().substring(0, 150));
            } else {
                jComboBox2.addItem(f.getHeaderline());
            }
        }
    }
    
    /**
    * Initializes the Add filters JcomboBox displayed on the Family analysis tab 
    * according to the list of filters as defined in the VCFFilterPreferences object.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initAddFamilyFilters() {
        List<VCFCompoundHeaderLine> l = PREFERENCES.getAvailableFilters();        
        for (VCFCompoundHeaderLine hl : l) {
            String s = hl.toString();
            if (s.length() > 150) {
                jComboBox3.addItem(s.substring(0, 150));
            } else {
                jComboBox3.addItem(s);
            }
        }
        //List<VCFFormatHeaderLine> l = PREFERENCES.getAvailableFilters();  
    }

    /**
    * Initializes the Remove filters JcomboBox displayed on the Family analysis tab 
    * according to the list of filters currently loaded.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initRemoveFamilyFilters() {
        jComboBox4.removeAllItems();
        for (Filter f : familyFilters) {
            if (f.getHeaderline().length() > 150) {
                jComboBox4.addItem(f.getHeaderline().substring(0, 150));
            } else {
                jComboBox4.addItem(f.getHeaderline());
            }
        }
    } 
    
    /**
    * Initializes JPanel displaying loaded filters.
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
    private void addToMyLayout(JPanel panel, Filter f, int hgap, int vgap, int hcgap, int vcgap) {

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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser2 = new javax.swing.JFileChooser();
        jFileChooser3 = new javax.swing.JFileChooser();
        jFileChooser4 = new javax.swing.JFileChooser();
        jFileChooser5 = new javax.swing.JFileChooser();
        jFileChooser6 = new javax.swing.JFileChooser();
        jFileChooser7 = new javax.swing.JFileChooser();
        jFileChooser8 = new javax.swing.JFileChooser();
        jFileChooser9 = new javax.swing.JFileChooser();
        jFileChooser10 = new javax.swing.JFileChooser();
        jFileChooser11 = new javax.swing.JFileChooser();
        jFileChooser12 = new javax.swing.JFileChooser();
        jFileChooser13 = new javax.swing.JFileChooser();
        jFileChooser14 = new javax.swing.JFileChooser();
        jFileChooser15 = new javax.swing.JFileChooser();
        jFileChooser16 = new javax.swing.JFileChooser();
        jFileChooser17 = new javax.swing.JFileChooser();
        jFileChooser18 = new javax.swing.JFileChooser();
        jFileChooser19 = new javax.swing.JFileChooser();
        jFileChooser20 = new javax.swing.JFileChooser();
        jFileChooser21 = new javax.swing.JFileChooser();
        jFileChooser22 = new javax.swing.JFileChooser();
        jFileChooser23 = new javax.swing.JFileChooser();
        jFileChooser24 = new javax.swing.JFileChooser();
        jFileChooser25 = new javax.swing.JFileChooser();
        jFileChooser26 = new javax.swing.JFileChooser();
        jFileChooser27 = new javax.swing.JFileChooser();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem44 = new javax.swing.JMenuItem();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jPopupMenu3 = new javax.swing.JPopupMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jPopupMenu4 = new javax.swing.JPopupMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItem36 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jPopupMenu5 = new javax.swing.JPopupMenu();
        jMenuItem12 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem13 = new javax.swing.JMenuItem();
        jPopupMenu6 = new javax.swing.JPopupMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem15 = new javax.swing.JMenuItem();
        jPopupMenu7 = new javax.swing.JPopupMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jPopupMenu8 = new javax.swing.JPopupMenu();
        jMenuItem18 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem19 = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMenuItem38 = new javax.swing.JMenuItem();
        jMenuItem39 = new javax.swing.JMenuItem();
        jPopupMenu9 = new javax.swing.JPopupMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem21 = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMenuItem40 = new javax.swing.JMenuItem();
        jMenuItem41 = new javax.swing.JMenuItem();
        jPopupMenu10 = new javax.swing.JPopupMenu();
        jMenuItem22 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem23 = new javax.swing.JMenuItem();
        jPopupMenu11 = new javax.swing.JPopupMenu();
        jMenuItem24 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem25 = new javax.swing.JMenuItem();
        jPopupMenu12 = new javax.swing.JPopupMenu();
        jMenuItem26 = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItem27 = new javax.swing.JMenuItem();
        jPopupMenu13 = new javax.swing.JPopupMenu();
        jMenuItem28 = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItem29 = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMenuItem42 = new javax.swing.JMenuItem();
        jMenuItem43 = new javax.swing.JMenuItem();
        jPopupMenu14 = new javax.swing.JPopupMenu();
        jMenuItem30 = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItem32 = new javax.swing.JMenuItem();
        jPopupMenu15 = new javax.swing.JPopupMenu();
        jMenuItem31 = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMenuItem33 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel22 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton16 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jPanel25 = new javax.swing.JPanel();
        jButton21 = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList();
        jPanel26 = new javax.swing.JPanel();
        jButton22 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList();
        jPanel27 = new javax.swing.JPanel();
        jButton23 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        jList9 = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel9 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel24 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jPanel18 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel20 = new javax.swing.JPanel();
        jComboBox5 = new javax.swing.JComboBox();
        jComboBox6 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane13 = new javax.swing.JScrollPane();
        jList10 = new javax.swing.JList();
        jScrollPane14 = new javax.swing.JScrollPane();
        jList11 = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jProgressBar2 = new javax.swing.JProgressBar();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jPanel13 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jProgressBar3 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem35 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem34 = new javax.swing.JMenuItem();

        jFileChooser1.setMultiSelectionEnabled(true);
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

        jFileChooser3.setMultiSelectionEnabled(true);
        jFileChooser3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser3ActionPerformed(evt);
            }
        });

        jFileChooser4.setMultiSelectionEnabled(true);
        jFileChooser4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser4ActionPerformed(evt);
            }
        });

        jFileChooser5.setMultiSelectionEnabled(true);
        jFileChooser5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser5ActionPerformed(evt);
            }
        });

        jFileChooser6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser6ActionPerformed(evt);
            }
        });

        jFileChooser7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser7ActionPerformed(evt);
            }
        });

        jFileChooser8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser8ActionPerformed(evt);
            }
        });

        jFileChooser9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser9ActionPerformed(evt);
            }
        });

        jFileChooser10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser10ActionPerformed(evt);
            }
        });

        jFileChooser11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser11ActionPerformed(evt);
            }
        });

        jFileChooser12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser12ActionPerformed(evt);
            }
        });

        jFileChooser13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser13ActionPerformed(evt);
            }
        });

        jFileChooser14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser14ActionPerformed(evt);
            }
        });

        jFileChooser15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser15ActionPerformed(evt);
            }
        });

        jFileChooser16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser16ActionPerformed(evt);
            }
        });

        jFileChooser17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser17ActionPerformed(evt);
            }
        });

        jFileChooser18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser18ActionPerformed(evt);
            }
        });

        jFileChooser19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser19ActionPerformed(evt);
            }
        });

        jFileChooser20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser20ActionPerformed(evt);
            }
        });

        jFileChooser21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser21ActionPerformed(evt);
            }
        });

        jFileChooser22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser22ActionPerformed(evt);
            }
        });

        jFileChooser23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser23ActionPerformed(evt);
            }
        });

        jFileChooser24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser24ActionPerformed(evt);
            }
        });

        jFileChooser25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser25ActionPerformed(evt);
            }
        });

        jFileChooser26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser26ActionPerformed(evt);
            }
        });

        jFileChooser27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser27ActionPerformed(evt);
            }
        });

        jPopupMenu1.setLabel("");

        jMenuItem1.setText("Save As");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        jMenuItem2.setText("Copy All");
        jMenuItem2.setToolTipText("");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem2);

        jMenuItem44.setText("Save as pass list");
        jMenuItem44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem44ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem44);

        jMenuItem3.setText("Save As");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem3);

        jMenuItem4.setText("Copy All");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jPopupMenu2.add(jMenuItem4);

        jMenuItem5.setText("Save As");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem5);

        jMenuItem6.setText("Copy All");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jPopupMenu3.add(jMenuItem6);

        jMenuItem10.setText("Add more");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jPopupMenu4.add(jMenuItem10);
        jPopupMenu4.add(jSeparator2);

        jMenuItem11.setText("Clear");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jPopupMenu4.add(jMenuItem11);
        jPopupMenu4.add(jSeparator14);

        jMenuItem36.setText("Remove selected files");
        jMenuItem36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem36ActionPerformed(evt);
            }
        });
        jPopupMenu4.add(jMenuItem36);

        jMenuItem37.setText("Keep selected files");
        jMenuItem37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem37ActionPerformed(evt);
            }
        });
        jPopupMenu4.add(jMenuItem37);

        jMenuItem12.setText("Add more");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jPopupMenu5.add(jMenuItem12);
        jPopupMenu5.add(jSeparator3);

        jMenuItem13.setText("Clear");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jPopupMenu5.add(jMenuItem13);

        jMenuItem14.setText("Add more");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jPopupMenu6.add(jMenuItem14);
        jPopupMenu6.add(jSeparator4);

        jMenuItem15.setText("Clear");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jPopupMenu6.add(jMenuItem15);

        jMenuItem16.setText("Add more");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jPopupMenu7.add(jMenuItem16);
        jPopupMenu7.add(jSeparator5);

        jMenuItem17.setText("Clear");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jPopupMenu7.add(jMenuItem17);

        jMenuItem18.setText("Add more");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jPopupMenu8.add(jMenuItem18);
        jPopupMenu8.add(jSeparator6);

        jMenuItem19.setText("Clear");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jPopupMenu8.add(jMenuItem19);
        jPopupMenu8.add(jSeparator15);

        jMenuItem38.setText("Remove selected files");
        jMenuItem38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem38ActionPerformed(evt);
            }
        });
        jPopupMenu8.add(jMenuItem38);

        jMenuItem39.setText("Keep selected files");
        jMenuItem39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem39ActionPerformed(evt);
            }
        });
        jPopupMenu8.add(jMenuItem39);

        jMenuItem20.setText("Add more");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jPopupMenu9.add(jMenuItem20);
        jPopupMenu9.add(jSeparator7);

        jMenuItem21.setText("Clear");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jPopupMenu9.add(jMenuItem21);
        jPopupMenu9.add(jSeparator16);

        jMenuItem40.setText("Remove selected files");
        jMenuItem40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem40ActionPerformed(evt);
            }
        });
        jPopupMenu9.add(jMenuItem40);

        jMenuItem41.setText("Keep selected files");
        jMenuItem41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem41ActionPerformed(evt);
            }
        });
        jPopupMenu9.add(jMenuItem41);

        jMenuItem22.setText("Add more");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jPopupMenu10.add(jMenuItem22);
        jPopupMenu10.add(jSeparator8);

        jMenuItem23.setText("Clear");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jPopupMenu10.add(jMenuItem23);

        jMenuItem24.setText("Add more");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jPopupMenu11.add(jMenuItem24);
        jPopupMenu11.add(jSeparator9);

        jMenuItem25.setText("Clear");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jPopupMenu11.add(jMenuItem25);

        jMenuItem26.setText("Add more");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jPopupMenu12.add(jMenuItem26);
        jPopupMenu12.add(jSeparator10);

        jMenuItem27.setText("Clear");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jPopupMenu12.add(jMenuItem27);

        jMenuItem28.setText("Add more");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jPopupMenu13.add(jMenuItem28);
        jPopupMenu13.add(jSeparator11);

        jMenuItem29.setText("Clear");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jPopupMenu13.add(jMenuItem29);
        jPopupMenu13.add(jSeparator17);

        jMenuItem42.setText("Remove selected files");
        jMenuItem42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem42ActionPerformed(evt);
            }
        });
        jPopupMenu13.add(jMenuItem42);

        jMenuItem43.setText("Keep selected files");
        jMenuItem43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem43ActionPerformed(evt);
            }
        });
        jPopupMenu13.add(jMenuItem43);

        jMenuItem30.setText("Clear");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jPopupMenu14.add(jMenuItem30);
        jPopupMenu14.add(jSeparator12);

        jMenuItem32.setText("Load examples");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jPopupMenu14.add(jMenuItem32);

        jMenuItem31.setText("Clear");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jPopupMenu15.add(jMenuItem31);
        jPopupMenu15.add(jSeparator13);

        jMenuItem33.setText("Load examples");
        jMenuItem33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem33ActionPerformed(evt);
            }
        });
        jPopupMenu15.add(jMenuItem33);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("VCFFilter");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filters"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 77, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filter options"));

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Single filter options"));

        jButton3.setText("Add");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Remove");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addGap(43, 43, 43))
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filter scenarios"));

        jButton17.setText("Open filter scenario");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Save filter scenario");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Files"));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));
        jPanel4.setPreferredSize(new java.awt.Dimension(330, 205));

        jButton2.setText("Open VCF files");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jList1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output and controls"));

        jRadioButton1.setText("Text");
        jRadioButton1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton1StateChanged(evt);
            }
        });

        jRadioButton3.setText("Calculate recurrence");
        jRadioButton3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton3StateChanged(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 0, 0));
        jButton1.setText("Run");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton13.setText("Cancel");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jLabel9.setText("Recurrence <=:");
        jLabel9.setToolTipText("number of alleles in cohort");

        jTextField3.setText("5");

        jRadioButton2.setText("VCF file");
        jRadioButton2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton2StateChanged(evt);
            }
        });

        jRadioButton4.setText("Hilbert curve");
        jRadioButton4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRadioButton4StateChanged(evt);
            }
        });

        jButton16.setText("Example");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jLabel1.setText("in");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "total", "het", "hom" }));
        jComboBox7.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox7ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton3)
                            .addComponent(jRadioButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(12, 12, 12)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton1)
                    .addComponent(jButton16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Inclusion lists"));
        jPanel25.setPreferredSize(new java.awt.Dimension(170, 199));

        jButton21.setText("Open");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jList7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList7MouseClicked(evt);
            }
        });
        jList7.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList7ValueChanged(evt);
            }
        });
        jScrollPane10.setViewportView(jList7);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jButton21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Exclusion lists"));

        jButton22.setText("Open");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jList8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList8MouseClicked(evt);
            }
        });
        jList8.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList8ValueChanged(evt);
            }
        });
        jScrollPane11.setViewportView(jList8);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jButton22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Recurrence"));
        jPanel27.setPreferredSize(new java.awt.Dimension(170, 99));

        jButton23.setText("Open");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jList9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList9.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList9MouseClicked(evt);
            }
        });
        jList9.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList9ValueChanged(evt);
            }
        });
        jScrollPane12.setViewportView(jList9);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jButton23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 145, Short.MAX_VALUE)
            .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
        );

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setToolTipText("Right click for popup menu");
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Filter variants", jPanel7);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filters"));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 79, Short.MAX_VALUE)
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filter options"));

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Single filter options"));

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

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Filter scenarios"));

        jButton19.setText("Open filter scenario");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("Save  filter scenario");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton20)
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Files"));

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Affected"));

        jButton10.setText("Open VCF files");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jList4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList4MouseClicked(evt);
            }
        });
        jList4.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList4ValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(jList4);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Unaffected"));
        jPanel18.setPreferredSize(new java.awt.Dimension(330, 205));

        jButton11.setText("Open VCF files");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jList5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList5MouseClicked(evt);
            }
        });
        jList5.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList5ValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(jList5);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane9.setBorder(null);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Relations"));

        jComboBox5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox5ItemStateChanged(evt);
            }
        });

        jComboBox6.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox6ItemStateChanged(evt);
            }
        });

        jLabel2.setText("Mother");

        jLabel3.setText("Father");

        jButton5.setText("View all relationships");
        jButton5.setToolTipText("");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jRadioButton5.setSelected(true);
        jRadioButton5.setText("male");

        jRadioButton6.setText("female");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(0, 39, Short.MAX_VALUE)
                        .addComponent(jRadioButton5)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton6)
                        .addGap(56, 56, 56))))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(1, 1, 1)
                .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel3)
                .addGap(1, 1, 1)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton5)
                    .addComponent(jRadioButton6))
                .addGap(19, 19, 19)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane9.setViewportView(jPanel20);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Lists and controls"));

        jList2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Inclusion lists"));
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jList2.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList2ValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jList2);

        jList10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Exclusion lists"));
        jList10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList10MouseClicked(evt);
            }
        });
        jList10.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList10ValueChanged(evt);
            }
        });
        jScrollPane13.setViewportView(jList10);

        jList11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Recurrence files"));
        jList11.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList11MouseClicked(evt);
            }
        });
        jList11.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList11ValueChanged(evt);
            }
        });
        jScrollPane14.setViewportView(jList11);

        jLabel7.setText("Recurrence <=:");
        jLabel7.setToolTipText("number of alleles in cohort");

        jTextField4.setText("5");

        jButton12.setBackground(new java.awt.Color(255, 0, 0));
        jButton12.setText("Run");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setText("Cancel");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton25.setText("Example");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jLabel4.setText("in");

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "total", "het", "hom" }));
        jComboBox8.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox8ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane14))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addGap(12, 12, 12)))
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane13)))
                .addGap(17, 17, 17))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton12)
                            .addComponent(jButton14)
                            .addComponent(jButton25)))
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane9)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jTextArea3.setToolTipText("Right click for popup menu");
        jTextArea3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea3MouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(jTextArea3);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Family analysis", jPanel9);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "VCF files"));

        jButton6.setText("Open VCF files");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jScrollPane4.setToolTipText("Load VCF files you want to search in for variants or genomic regions.");

        jList3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList3.setToolTipText("Load VCF files you want to search in for variants and/or genomic regions.");
        jList3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList3MouseClicked(evt);
            }
        });
        jList3.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList3ValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(jList3);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jScrollPane4.getAccessibleContext().setAccessibleName("");
        jScrollPane4.getAccessibleContext().setAccessibleDescription("");

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Controls"));

        jButton7.setBackground(new java.awt.Color(255, 0, 0));
        jButton7.setText("Search");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton15.setText("Cancel");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton24.setText("Example");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jButton7)
                .addGap(18, 18, 18)
                .addComponent(jButton15)
                .addGap(18, 18, 18)
                .addComponent(jButton24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Variants in ExAC format (14-106203380-C-T)"));

        jScrollPane15.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jTextArea4.setToolTipText("Look up these variants in the loaded VCF files.");
        jTextArea4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextArea4CaretUpdate(evt);
            }
        });
        jTextArea4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea4MouseClicked(evt);
            }
        });
        jScrollPane15.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane15)
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Genomic regions (chr1:100000-200000)"));
        jPanel19.setPreferredSize(new java.awt.Dimension(390, 124));

        jScrollPane16.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextArea5.setColumns(20);
        jTextArea5.setRows(5);
        jTextArea5.setToolTipText("Look up variants in these regions in the loaded VCF files.");
        jTextArea5.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                jTextArea5CaretUpdate(evt);
            }
        });
        jTextArea5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea5MouseClicked(evt);
            }
        });
        jScrollPane16.setViewportView(jTextArea5);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane16)
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(3, 3, 3))
        );

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setToolTipText("Right click for popup menu");
        jTextArea2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea2MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTextArea2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Search variants", jPanel8);

        jMenu1.setMnemonic(KeyEvent.VK_F);
        jMenu1.setText("File");

        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/heart.png"))); // NOI18N
        jMenuItem7.setMnemonic(KeyEvent.VK_P);
        jMenuItem7.setText("Preferences");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/Save.png"))); // NOI18N
        jMenuItem9.setMnemonic(KeyEvent.VK_S);
        jMenuItem9.setText("Save settings");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);
        jMenu1.add(jSeparator1);

        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/exit.png"))); // NOI18N
        jMenuItem8.setMnemonic(KeyEvent.VK_E);
        jMenuItem8.setText("Exit");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuBar1.add(jMenu1);

        jMenu3.setMnemonic(KeyEvent.VK_I);
        jMenu3.setText("Index");

        jMenuItem35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/idx.png"))); // NOI18N
        jMenuItem35.setMnemonic(KeyEvent.VK_I);
        jMenuItem35.setText("Index text VCF file");
        jMenuItem35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem35ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem35);

        jMenuBar1.add(jMenu3);

        jMenu2.setMnemonic(KeyEvent.VK_A);
        jMenu2.setText("About");
        jMenu2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu2ActionPerformed(evt);
            }
        });

        jMenuItem34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/at/ac/oeaw/cemm/bsf/vcffilter/icon/about.png"))); // NOI18N
        jMenuItem34.setMnemonic(KeyEvent.VK_B);
        jMenuItem34.setText("About VCFFilter");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem34);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Loads VCF files on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            this.selectedVCFFiles = jFileChooser1.getSelectedFiles();
            setFileList(); 
            setFilterRunButtonState();
        }
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    /**
    * Open VCF files button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser1.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser1.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser1.showOpenDialog(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
    * Run button event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        cancelFilterWorker = false;
        jTextArea1.setText("");
        setActiveFilter();
        boolean indexTestPassed = setActiveFileList(); 
        if(indexTestPassed && activeVCFFiles != null && activeVCFFiles.length > 0){            
            if(!testHeaderLinesConsistency(activeVCFFiles[0])){
                new Warning(this, "Example VCF file and test VCF file have divergent headers. Missing header lines in test VCF file will be ignored.");
            }                    
        }
        jProgressBar1.setValue(0);
        
        if (activeVCFFiles != null && activeVCFFiles.length > 0 && jRadioButton1.isSelected() && indexTestPassed) {
            jButton16.setEnabled(false);
            filterWorker = new FilterWorker(this);
            filterWorker.execute();

            /*
            try{
                filterWorker.doInBackground0();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            //*/
        } else if (activeVCFFiles != null && activeVCFFiles.length > 0 && jRadioButton2.isSelected() && indexTestPassed) {
            if(activeVCFFiles.length > 1){
                new Warning(this, "Please highlight one VCF input file at the time as VCF merging is not supported.");
                return;
            }else{
                jFileChooser9.showSaveDialog(this);
                if(vcfOutputFile == null){
                    new Warning(this, "No output file defined.");
                    return;
                }
            }
            jButton16.setEnabled(false);
            filterWorker = new FilterWorker(this);
            filterWorker.execute();

            /*
            try{
                fw.doInBackground0();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            //*/
        } else if (activeVCFFiles != null && activeVCFFiles.length > 0 && jRadioButton3.isSelected() && indexTestPassed) {
            //if(jList9.getSelectedIndex() > 0){
                //RecurrenceCalculationWorker fw = new RecurrenceCalculationWorker(activeVCFFiles, activeFilters, this, filterRecurrenceFiles.get(jList9.getSelectedIndex() - 1));
            jButton16.setEnabled(false);
            RecurrenceCalculationWorker fw = new RecurrenceCalculationWorker(activeVCFFiles, activeFilters, this);
            fw.execute();
            //}
            //*
            //try{
            //    fw.doInBackground0();
            //}catch(Exception e){
            //    e.printStackTrace();
            //}
            //*/
        }else if (activeVCFFiles != null && activeVCFFiles.length > 0 && jRadioButton4.isSelected() && indexTestPassed) {
            jButton16.setEnabled(false);
            filterWorker = new FilterWorker(this);
            filterWorker.execute();

            /*
            try{
                fw.doInBackground0();
                
            }catch(Exception e){
                e.printStackTrace();
            }
            //*/
        }else{
            if (activeVCFFiles == null || activeVCFFiles.length == 0) {
                new Warning(this, "No input files selected.");
            } else if (activeFilters == null || activeFilters.size() == 0) {
                new Warning(this, "No active filters detected. Add some filters and search criteria.");
            } else if (filterRecurrenceFile == null ) {
                new Warning(this, "No recurrence file selected.");
            }
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * Add filter button event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(filters.size() < 10){
            int i = jComboBox1.getSelectedIndex();
            Filter f = FilterFactory.getFilter(PREFERENCES.getAvailableFilters().get(i));
            setFilterDefaults(f);   
            f.setIndex(filters.size());
            f.setGui(this);
            addToMyLayout(jPanel1, f, 10, filters.size() * filterVerticalGap + 10, 0, 0);
            filters.add(f);
            initRemoveFilters();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
    * Remove filter button event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        int selected = jComboBox2.getSelectedIndex();
        String selectedHeader = (String) jComboBox2.getItemAt(selected);
        ArrayList<String> remaining = new ArrayList<String>();
        ArrayList<Filter> remainingFilters = new ArrayList<Filter>();
        for (int i = 0; i < jComboBox2.getItemCount(); i++) {
            if (i != selected) {
                remaining.add((String) jComboBox2.getItemAt(i));
                remainingFilters.add(filters.get(i));
            }
        }        
        jPanel1.removeAll();
        filters = remainingFilters;
        for (int i = 0; i < remaining.size() && i < 10; i++) {
            Filter f = filters.get(i);
            addToMyLayout(jPanel1, f, 10, i * filterVerticalGap + 10, 0, 0);
        }
        initRemoveFilters();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Loads recurrence files list on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser2ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser2.getSelectedFiles(); 
            filterRecurrenceFiles = new ArrayList<File>();
            for(File x : f){
                filterRecurrenceFiles.add(x);
            } 
            setListValues(jList9, filterRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser2ActionPerformed

    /**
    * JRadioButton event handler on Filter tab.
    * Called upon selecting filter VCF files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton1StateChanged
        // TODO add your handling code here:
        if(jRadioButton1.isSelected()){
            setFilterRunButtonState();
        }
    }//GEN-LAST:event_jRadioButton1StateChanged

    /**
    * Open VCF files button event handler on Search tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser3.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser3.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser3.showOpenDialog(this);
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Search tab.
    * Loads VCF files list on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser3ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            this.selectedSearchVCFFiles = jFileChooser3.getSelectedFiles();
            setSearchFileList();
            setSearchRunButtonState();
        }
    }//GEN-LAST:event_jFileChooser3ActionPerformed

    /**
    * Run button event handler on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        cancelSearchWorker = false;        
        boolean indexTestPassed = setActiveSearchFileList();
        if( activeSearchVCFFiles != null && activeSearchVCFFiles.length > 0 && indexTestPassed){ 
            if (!jTextArea4.getText().equals("") || !jTextArea5.getText().equals("")) {
                getSearchExampleButton().setEnabled(false);
                SearchWorker sw = new SearchWorker(this);
                sw.execute();
                /*
                try{
                    sw.doInBackground0();
                }catch(Exception e){
                    e.printStackTrace();
                }
                //*/  
            }else{
                new Warning(this, "No search criteria found.");
            }
        }else{
            new Warning(this, "Found no files to be searched.");
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
    * Add filter button event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here
        if(familyFilters.size() < 10){
            int i = jComboBox3.getSelectedIndex();
            Filter f = FilterFactory.getFilter(PREFERENCES.getAvailableFilters().get(i));
            setFilterDefaults(f);
            f.setIndex(familyFilters.size());
            f.setGui(this);
            addToMyLayout(jPanel14, f, 10, familyFilters.size() * filterVerticalGap + 10, 0, 0);
            familyFilters.add(f);
            initRemoveFamilyFilters();
        }
        setFamilyRunButtonState();
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
    * Remove filter button event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        int selected = jComboBox4.getSelectedIndex();
        String selectedHeader = (String) jComboBox4.getItemAt(selected);
        ArrayList<String> remaining = new ArrayList<String>();
        ArrayList<Filter> remainingFilters = new ArrayList<Filter>();
        for (int i = 0; i < jComboBox4.getItemCount(); i++) {
            if (i != selected) {
                remaining.add((String) jComboBox4.getItemAt(i));
                remainingFilters.add(familyFilters.get(i));
            }
        }        
        jPanel14.removeAll();
        familyFilters = remainingFilters;
        for (int i = 0; i < remaining.size(); i++) {
            Filter f = familyFilters.get(i);
            addToMyLayout(jPanel14, f, 10, i * filterVerticalGap + 10, 0, 0);
        }
        initRemoveFamilyFilters();
    }//GEN-LAST:event_jButton9ActionPerformed

    /**
    * Open affected VCF files button event handler on Family analysis tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser4.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser4.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser4.showOpenDialog(this);
    }//GEN-LAST:event_jButton10ActionPerformed

    /**
    * Open unaffected VCF files button event handler on Family analysis tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser5.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser5.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser5.showOpenDialog(this);
    }//GEN-LAST:event_jButton11ActionPerformed

    /**
    * Run button event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        cancelFamilyWorker = false;
        
        try {
                jTextArea3.setText("");
                int files = jList4.getModel().getSize();
                int[] selected = new int[files];
                for(int i = 0; i < files; i++){
                    selected[i] = i;
                }
                jList4.setSelectedIndices(selected);
                int files2 = jList5.getModel().getSize();
                int[] selected2 = new int[files2];
                for(int i = 0; i < files2; i++){
                    selected2[i] = i;
                }
                jList5.setSelectedIndices(selected2);
                setActiveFamilyFilter();
                boolean indexTest1Passed = setActiveAffectedFileList();                   
                boolean indexTest2Passed = setActiveUnaffectedFileList();
                if (testSelectionConsistency()) {                
                    if (activeFamilyFilters.size() >= 0 && activeAffectedVCFFiles != null && activeAffectedVCFFiles.length > 0 && activeUnaffectedVCFFiles != null && activeUnaffectedVCFFiles.length > 0 && indexTest1Passed && indexTest2Passed) {
                        if(PREFERENCES.getGenesymbolField() == null){
                            new Warning(this, "Wrong gene symbol field defined in Preferences -> Annotations. Compound heterozygous variant search isn't possible and won't be performed.");
                        }else{
                            VCFFileReader vcf = new VCFFileReader(activeAffectedVCFFiles[0]);
                            VariantContext vc = vcf.iterator().next();
                            Object o = vc.getAttribute(PREFERENCES.getGenesymbolField());
                            if(o == null){
                                new Warning(this, "Wrong gene symbol field defined in Preferences -> Annotations. Compound heterozygous variant search isn't possible and won't be performed.");
                            }
                        }
                        getFamilyExampleButton().setEnabled(false);
                        familyWorker = new FamilyAnalysisWorker(this);
                        familyWorker.execute();
                        /*
                         try{
                            //faw.doInBackground0();
                             familyWorker.doInBackground0();
                         }catch(Exception e){
                            e.printStackTrace();
                         }
                         //*/
                    } else {
                        if (activeAffectedVCFFiles == null || activeAffectedVCFFiles.length == 0) {
                            new Warning(this, "No affected input files selected.");
                        } else if (activeUnaffectedVCFFiles == null || activeUnaffectedVCFFiles.length == 0) {
                            new Warning(this, "No unaffected input files selected.");
                        } else if (activeFamilyFilters == null || activeFamilyFilters.size() == 0) {
                            new Warning(this, "No active filters detected. Add some filters and search criteria.");
                        }
                    }
                } else {
                    new Warning(this, "Inconsistent selection of family relations");
                }
        } catch (NullPointerException npe) {
            new Warning(this, "There are problems with the current file selection.");
        }
        
    }//GEN-LAST:event_jButton12ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Search tab.
    * Loads affected VCF files list on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser4ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            this.selectedAffectedVCFFiles = jFileChooser4.getSelectedFiles();
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setAffectedFileList();
            //setMaleFileList();
            setFamilyRunButtonState();
            //updateRelationships();
        }
        
    }//GEN-LAST:event_jFileChooser4ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Search tab.
    * Loads unaffected VCF files list on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser5ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            this.selectedUnaffectedVCFFiles = jFileChooser5.getSelectedFiles();
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setUnaffectedFileList();
            setFamilyRunButtonState();
            //updateRelationships();
        }
        
    }//GEN-LAST:event_jFileChooser5ActionPerformed

    /**
    * Cancel button event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        //jTextArea1.append("Cancelling after current file.\r\n");
        cancelFilterWorker = true;
        if(filterWorker != null){
            filterWorker.setCancel(true);
        }
        jButton16.setEnabled(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    /**
    * Cancel button event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        //jTextArea3.append("Cancelling after current file.\r\n");
        cancelFamilyWorker = true;
        if(familyWorker != null){
            familyWorker.setCancel(true);
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    /**
    * Cancel button event handler on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        jTextArea2.append("Cancelling after current file.\r\n");
        cancelSearchWorker = true;
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
    * Recurrence calculation JRadioButton event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton3StateChanged
        // TODO add your handling code here:
        if (jRadioButton3.isSelected()) {
            if(filterIsPresent("AF")){
                int size = jComboBox2.getModel().getSize();
                ComboBoxModel m = jComboBox2.getModel();
                for(int i = 0; i < size; i++){
                    String h = m.getElementAt(i).toString();
                    if(h.startsWith("INFO=<ID=AF,")){
                        m.setSelectedItem(m.getElementAt(i));
                        removeAFFilter();
                        break;
                    }
                }
            }             
            setFilterRunButtonState();        
        }
    }//GEN-LAST:event_jRadioButton3StateChanged

    /**
    * Show pop up menu for output area event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu1.show(jTextArea1, p.x, p.y);
        }
    }//GEN-LAST:event_jTextArea1MouseClicked

    /**
    * Pop up menu for output area Save As menu item event handler on Filter tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser6.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser6.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser6.showSaveDialog(this);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
    * Pop up menu for output area Copy menu item event handler on Filter tab.
    * Copies output text to clipboard.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        jTextArea1.requestFocusInWindow();
        jTextArea1.selectAll();
        StringSelection stringSelection = new StringSelection (jTextArea1.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Saves output to file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser6ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser6.getSelectedFile();
            save(f, jTextArea1.getText());            
        }
    }//GEN-LAST:event_jFileChooser6ActionPerformed

    /**
    * Pop up menu for output area Save As menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser7.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser7.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser7.showSaveDialog(this);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Saves output to file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser7ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser7.getSelectedFile();
            save(f, jTextArea3.getText());            
        }
    }//GEN-LAST:event_jFileChooser7ActionPerformed

    /**
    * Pop up menu for output area Save As menu item event handler on Search tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser8.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser8.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser8.showSaveDialog(this);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Search tab.
    * Saves output to file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser8ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser8.getSelectedFile();
            save(f, jTextArea2.getText());            
        }
    }//GEN-LAST:event_jFileChooser8ActionPerformed

    /**
    * Pop up menu for output area Copy menu item event handler on Family analysis tab.
    * Copies output text to clipboard.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        jTextArea3.requestFocusInWindow();
        jTextArea3.selectAll();
        StringSelection stringSelection = new StringSelection (jTextArea3.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    /**
    * Pop up menu for output area Copy menu item event handler on Search tab.
    * Copies output text to clipboard.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        jTextArea2.requestFocusInWindow();
        jTextArea2.selectAll();
        StringSelection stringSelection = new StringSelection (jTextArea2.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
        clpbrd.setContents (stringSelection, null);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
    * Show pop up menu for output area event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea3MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu2.show(jTextArea3, p.x, p.y);
        }
    }//GEN-LAST:event_jTextArea3MouseClicked

    /**
    * Show pop up menu for output area event handler on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea2MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu3.show(jTextArea2, p.x, p.y);
        }
    }//GEN-LAST:event_jTextArea2MouseClicked

    /**
    * Close application event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        PREFERENCES.saveIni();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    /**
    * Show preferences menu item event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        PREFERENCES.setVisible(true);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    /**
    * Exit menu item event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        PREFERENCES.saveIni();
        System.exit(0);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    /**
    * Open recurrence files button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
        jFileChooser2.setMultiSelectionEnabled(true);
        if(jFileChooser2.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser2.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser2.showOpenDialog(this);
    }//GEN-LAST:event_jButton23ActionPerformed

    /**
    * Select recurrence file event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList9ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList9ValueChanged
        // TODO add your handling code here:
        if(jList9.getSelectedIndex() == 0){
            jLabel9.setEnabled(false);
            jTextField3.setEnabled(false);
            filterRecurrenceFile = null;
        }else{
            int i = jList9.getSelectedIndex();
            jLabel9.setEnabled(true);
            jTextField3.setEnabled(true);
            if(filterRecurrenceFiles != null && i > 0 && i <= filterRecurrenceFiles.size()){
                filterRecurrenceFile = filterRecurrenceFiles.get(i - 1);
            }
        }
    }//GEN-LAST:event_jList9ValueChanged

    /**
    * Open white list files button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        //white lists open, jfileChooser10
        jFileChooser10.setMultiSelectionEnabled(true);
        if(jFileChooser10.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser10.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser10.showOpenDialog(this);
    }//GEN-LAST:event_jButton21ActionPerformed

    /**
    * Open black list files button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
        //black lists open, jfileChooser11
        jFileChooser11.setMultiSelectionEnabled(true);
        if(jFileChooser11.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser11.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser11.showOpenDialog(this);
    }//GEN-LAST:event_jButton22ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Loads white list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser10ActionPerformed
        // TODO add your handling code here:
        //white lists open
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser10.getSelectedFiles(); 
            filterWhiteListFiles = new ArrayList<File>();
            for(File x : f){
                filterWhiteListFiles.add(x);
            }
            setListValues(jList7, filterWhiteListFiles, true, "Only matching variants will be kept.");
        }
    }//GEN-LAST:event_jFileChooser10ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Loads black list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser11ActionPerformed
        // TODO add your handling code here:
        //black lists open
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser11.getSelectedFiles(); 
            filterBlackListFiles = new ArrayList<File>();
            for(File x : f){
                filterBlackListFiles.add(x);
            }
            setListValues(jList8, filterBlackListFiles, true, "All matching variants will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser11ActionPerformed

    /**
    * Select white list files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList7ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList7ValueChanged
        // TODO add your handling code here:
        int[] selected = jList7.getSelectedIndices();
        if(selectionContains(selected, 0)){            
            filterActiveWhiteListFiles = null;
            if(selected.length > 1){
                new Warning(this, "None selected along with valid white list files.");
            }
        }else{            
            filterActiveWhiteListFiles = new ArrayList<File>();
            for(int i = 0; i < selected.length; i++){
                filterActiveWhiteListFiles.add(filterWhiteListFiles.get(selected[i] - 1));
            }
        }
    }//GEN-LAST:event_jList7ValueChanged

    /**
    * Select black list files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList8ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList8ValueChanged
        // TODO add your handling code here:
        int[] selected = jList8.getSelectedIndices();
        if(selectionContains(selected, 0)){            
            filterActiveBlackListFiles = null;
            if(selected.length > 1){
                new Warning(this, "None selected along with valid black list files.");
            }
        }else{            
            filterActiveBlackListFiles = new ArrayList<File>();
            for(int i = 0; i < selected.length; i++){
                filterActiveBlackListFiles.add(filterBlackListFiles.get(selected[i] - 1));
            }
        }
    }//GEN-LAST:event_jList8ValueChanged

    /**
    * Open Filter scenario button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        //open filter scenario
        jFileChooser12.setMultiSelectionEnabled(false);
        if(jFileChooser12.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser12.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser12.showOpenDialog(this);
    }//GEN-LAST:event_jButton17ActionPerformed

    /**
    * Save Filter scenario button event handler on Filter tab.
    * Displays corresponding JFileChooser.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        //save filter scenario
        if(jFileChooser13.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser13.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser13.showSaveDialog(this);
    }//GEN-LAST:event_jButton18ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Opens filter scenario.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser12ActionPerformed
        // TODO add your handling code here:
        //open filter scenario
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser12.getSelectedFile(); 
            loadFilterScenario(f);
        }
    }//GEN-LAST:event_jFileChooser12ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Saves filter scenario.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser13ActionPerformed
        // TODO add your handling code here:
        //save filter scenario
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser13.getSelectedFile(); 
            saveFilterScenario(f);
        }
    }//GEN-LAST:event_jFileChooser13ActionPerformed

    /**
    * Save settings menu item event handler.
    * Saves current filters, white list files, black list files, and recurrence files to PREFERENCES.
    * When Filter tab is selected, Filter tab related values are saved.
    * When Family analysis tab is seletced, Family analysis tab related values are saved.
    * Upon application exit, the values are written to the preferences.ini file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        //Save settings
        
        if(jTabbedPane1.getSelectedIndex() == 0){
            PREFERENCES.saveFilterSettings(filters);
            PREFERENCES.setWhiteListFiles(filterWhiteListFiles);
            PREFERENCES.setBlackListFiles(filterBlackListFiles);
            PREFERENCES.setRecurrenceFiles(filterRecurrenceFiles);
        }else{
            PREFERENCES.saveFilterSettings(familyFilters);
            PREFERENCES.setWhiteListFiles(familyWhiteListFiles);
            PREFERENCES.setBlackListFiles(familyBlackListFiles);
            PREFERENCES.setRecurrenceFiles(familyRecurrenceFiles);
        } 
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    /**
    * Show pop up menu for white list files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList7MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu5.show(jList7, p.x, p.y);
        }
    }//GEN-LAST:event_jList7MouseClicked

    /**
    * Show pop up menu for black list files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList8MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu6.show(jList8, p.x, p.y);
        }
    }//GEN-LAST:event_jList8MouseClicked

    /**
    * Show pop up menu for VCF files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu4.show(jList1, p.x, p.y);
        }
    }//GEN-LAST:event_jList1MouseClicked

    /**
    * Show pop up menu for recurrence files event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList9MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu7.show(jList9, p.x, p.y);
        }
    }//GEN-LAST:event_jList9MouseClicked

    /**
    * Show pop up menu for affected VCF files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList4MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu8.show(jList4, p.x, p.y);
        }
    }//GEN-LAST:event_jList4MouseClicked

    /**
    * Show pop up menu for unaffected VCF files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList5MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu9.show(jList5, p.x, p.y);
        }
    }//GEN-LAST:event_jList5MouseClicked

    /**
    * Show pop up menu for white list files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu10.show(jList2, p.x, p.y);
        }
    }//GEN-LAST:event_jList2MouseClicked

    /**
    * Show pop up menu for black list files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList10MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu11.show(jList10, p.x, p.y);
        }
    }//GEN-LAST:event_jList10MouseClicked

    /**
    * Show pop up menu for recurrence files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList11MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu12.show(jList11, p.x, p.y);
        }
    }//GEN-LAST:event_jList11MouseClicked

    /**
    * Show pop up menu for VCF files event handler on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList3MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu13.show(jList3, p.x, p.y);
        }
    }//GEN-LAST:event_jList3MouseClicked

    /**
    * Add more VCF files pop up menu item event handler on Filter tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        //Add more VCF files to filter tab
        jFileChooser14.setMultiSelectionEnabled(true);
        if(jFileChooser14.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser14.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }        
        jFileChooser14.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Adds more VCF files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser14ActionPerformed
        // TODO add your handling code here:
        //Add more VCF files to filter tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] temp = jFileChooser14.getSelectedFiles();            
            this.selectedVCFFiles = combineFileArrays(this.selectedVCFFiles, temp);
            setAllItemsSelected(jList1);
            setFileList();
            //extendFileList();
            setFilterRunButtonState();
        }
    }//GEN-LAST:event_jFileChooser14ActionPerformed

    /**
    * Clear VCF files pop up menu item event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        //Clear VCF files from filter tab
        this.selectedVCFFiles = null;
        this.activeVCFFiles = null;
        setFileList();
        setFilterRunButtonState();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    /**
    * Add more white list files pop up menu item event handler on Filter tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        //Add more white list files to filter tab
        jFileChooser15.setMultiSelectionEnabled(true);
        if(jFileChooser15.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser15.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }        
        jFileChooser15.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Adds more white list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser15ActionPerformed
        // TODO add your handling code here:
        //Add more white list files to filter tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser15.getSelectedFiles(); 
            if(filterWhiteListFiles == null){
                filterWhiteListFiles = new ArrayList<File>();
            }
            for(File x : f){
                filterWhiteListFiles.add(x);
            }
            setListValues(jList7, filterWhiteListFiles, true, "Only matching variants will be kept.");
        }
    }//GEN-LAST:event_jFileChooser15ActionPerformed

    /**
    * Clear white list files pop up menu item event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        //Clear white list files from filter tab
        filterWhiteListFiles = null;
        setListValues(jList7, filterWhiteListFiles, true, "Only matching variants will be kept.");
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    /**
    * Add more black list files pop up menu item event handler on Filter tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // TODO add your handling code here:
        //Add black list files to filter tab
        jFileChooser16.setMultiSelectionEnabled(true);
        if(jFileChooser16.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser16.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser16.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Adds more black list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser16ActionPerformed
        // TODO add your handling code here:
        //Add black list files to filter tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser16.getSelectedFiles(); 
            if(filterBlackListFiles == null){
                filterBlackListFiles = new ArrayList<File>();
            }
            for(File x : f){
                filterBlackListFiles.add(x);
            }
            setListValues(jList8, filterBlackListFiles, true, "All matching variants will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser16ActionPerformed

    /**
    * Clear black list files pop up menu item event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // TODO add your handling code here:
        //Clear black list files on filter tab
        filterBlackListFiles = null;
        setListValues(jList8, filterBlackListFiles, true, "All matching variants will be discarded.");
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    /**
    * Add more recurrence files pop up menu item event handler on Filter tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // TODO add your handling code here:
        //Add more recurrence files to filter tab
        jFileChooser17.setMultiSelectionEnabled(true);
        if(jFileChooser17.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser17.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser17.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Adds more recurrence files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser17ActionPerformed
        // TODO add your handling code here:
        //Add more recurrence files to filter tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser17.getSelectedFiles(); 
            if(filterRecurrenceFiles == null){
                filterRecurrenceFiles = new ArrayList<File>();
            }
            for(File x : f){
                filterRecurrenceFiles.add(x);
            } 
            setListValues(jList9, filterRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser17ActionPerformed

    /**
    * Clear recurrence files pop up menu item event handler on Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // TODO add your handling code here:
        filterRecurrenceFiles = null;       
        filterRecurrenceFile = null;
        setListValues(jList9, filterRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        jLabel9.setEnabled(false);
        jTextField3.setEnabled(false);
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    /**
    * Add more affected VCF files pop up menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
        //Add more affected VCF files
        jFileChooser18.setMultiSelectionEnabled(true);
        if(jFileChooser18.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser18.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser18.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Adds more affected VCF files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser18ActionPerformed
        // TODO add your handling code here:
        //Add more affected VCF files
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] temp = jFileChooser18.getSelectedFiles();
            this.selectedAffectedVCFFiles = combineFileArrays(this.selectedAffectedVCFFiles, temp);
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setAffectedFileList();
            //setMaleFileList();
            setFamilyRunButtonState();
            //updateRelationships();
        }
        
    }//GEN-LAST:event_jFileChooser18ActionPerformed

    /**
    * Clear affected VCF files pop up menu item event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // TODO add your handling code here:
        //Clear affected VCF files
        this.selectedAffectedVCFFiles = null;
        this.activeAffectedVCFFiles = null;
        if(this.relationships != null){
            this.relationships.dispose();
            this.relationships = null;
        }
        setAffectedFileList();
        //setMaleFileList();
        setFamilyRunButtonState();
        //updateRelationships();
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    /**
    * Add more unaffected VCF files pop up menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        // TODO add your handling code here:
        //Add more unaffected VCF files
        jFileChooser19.setMultiSelectionEnabled(true);
        if(jFileChooser19.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser19.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser19.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Adds more unaffected VCF files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser19ActionPerformed
        // TODO add your handling code here:
        //Add more unaffected VCF files
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {            
            File[] temp = jFileChooser19.getSelectedFiles();
            this.selectedUnaffectedVCFFiles = combineFileArrays(this.selectedUnaffectedVCFFiles, temp);
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setUnaffectedFileList();
            setFamilyRunButtonState();
            //updateRelationships();
        }
        
    }//GEN-LAST:event_jFileChooser19ActionPerformed

    /**
    * Clear unaffected VCF files pop up menu item event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // TODO add your handling code here:
        //Clear unaffected VCF files
        this.selectedUnaffectedVCFFiles = null;
        this.activeUnaffectedVCFFiles = null;
        if(this.relationships != null){
            this.relationships.dispose();
            this.relationships = null;
        }
        setUnaffectedFileList();
        setFamilyRunButtonState();
        //updateRelationships();
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    /**
    * Add more white list files pop up menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // TODO add your handling code here:
        //Add more white lists to family analysis tab
        jFileChooser20.setMultiSelectionEnabled(true);
        if(jFileChooser20.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser20.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser20.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Adds more white list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser20ActionPerformed
        // TODO add your handling code here:
        //Add more white lists to family analysis tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser20.getSelectedFiles(); 
            if(familyWhiteListFiles == null){
                familyWhiteListFiles = new ArrayList<File>();
            }
            for(File x : f){
                familyWhiteListFiles.add(x);
            }
            setListValues(jList2, familyWhiteListFiles, true, "Only matching variants will be kept.");
        }
    }//GEN-LAST:event_jFileChooser20ActionPerformed

    /**
    * Clear white list files pop up menu item event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        // TODO add your handling code here:
        //Clear white lists from family analysis tab
        familyWhiteListFiles = null;
        setListValues(jList2, familyWhiteListFiles, true, "Only matching variants will be kept.");
    }//GEN-LAST:event_jMenuItem23ActionPerformed
    
    /**
    * Add more black list files pop up menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        // TODO add your handling code here:
        //Add more black lists to family analysis tab
        jFileChooser21.setMultiSelectionEnabled(true);
        if(jFileChooser21.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser21.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser21.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Adds more black list files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser21ActionPerformed
        // TODO add your handling code here:
        //Add more black lists to family analysis tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser21.getSelectedFiles(); 
            if(familyBlackListFiles == null){
                familyBlackListFiles = new ArrayList<File>();
            }
            for(File x : f){
                familyBlackListFiles.add(x);
            }
            setListValues(jList10, familyBlackListFiles, true, "Matching variants will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser21ActionPerformed

    /**
    * Clear black list files pop up menu item event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        // TODO add your handling code here:
        //Clear black lists from family analysis tab
        familyBlackListFiles = null;
        setListValues(jList10, familyBlackListFiles, true, "Matching variants will be discarded.");
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    /**
    * Add more recurrence files pop up menu item event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        // TODO add your handling code here:
        //Add more recurrence files to family analysis tab
        jFileChooser22.setMultiSelectionEnabled(true);
        if(jFileChooser22.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser22.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser22.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Adds more recurrence files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser22ActionPerformed
        // TODO add your handling code here:
        //Add more recurrence files to family analysis tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] f = jFileChooser22.getSelectedFiles(); 
            if(familyRecurrenceFiles == null){
                familyRecurrenceFiles = new ArrayList<File>();
            }
            for(File x : f){
                familyRecurrenceFiles.add(x);
            } 
            setListValues(jList11, familyRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        }
    }//GEN-LAST:event_jFileChooser22ActionPerformed

    /**
    * Clear recurrence files pop up menu item event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        // TODO add your handling code here:
        //Clear recurrence files from family analysis tab
        familyRecurrenceFiles = null;
        familyRecurrenceFile = null;
        setListValues(jList11, familyRecurrenceFiles, true, "Variants exceeding cutoff will be discarded.");
        jLabel7.setEnabled(false);
        jTextField4.setEnabled(false);
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    /**
    * Select white list files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList2ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList2ValueChanged
        // TODO add your handling code here:
        //Family white list selection change
        int[] selected = jList2.getSelectedIndices();
        if(selectionContains(selected, 0)){            
            familyActiveWhiteListFiles = null;
            if(selected.length > 1){
                new Warning(this, "None selected along with valid white list files.");
            }
        }else{            
            familyActiveWhiteListFiles = new ArrayList<File>();
            for(int i = 0; i < selected.length; i++){
                familyActiveWhiteListFiles.add(familyWhiteListFiles.get(selected[i] - 1));
            }
        }
    }//GEN-LAST:event_jList2ValueChanged

    /**
    * Select black list files event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList10ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList10ValueChanged
        // TODO add your handling code here:
        //Family black list selection change
        int[] selected = jList10.getSelectedIndices();
        if(selectionContains(selected, 0)){            
            familyActiveBlackListFiles = null;
            if(selected.length > 1){
                new Warning(this, "None selected along with valid black list files.");
            }
        }else{            
            familyActiveBlackListFiles = new ArrayList<File>();
            for(int i = 0; i < selected.length; i++){
                familyActiveBlackListFiles.add(familyBlackListFiles.get(selected[i] - 1));
            }
        }
    }//GEN-LAST:event_jList10ValueChanged

    /**
    * Select recurrence file event handler on Family analysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList11ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList11ValueChanged
        // TODO add your handling code here:
        //Family recurrence file selection change
        if(jList11.getSelectedIndex() == 0){
            jLabel7.setEnabled(false);
            jTextField4.setEnabled(false);
            familyRecurrenceFile = null;
        }else{
            int i = jList11.getSelectedIndex();
            jLabel7.setEnabled(true);
            jTextField4.setEnabled(true);
            if(familyRecurrenceFiles != null && i > 0 && i <= familyRecurrenceFiles.size()){
                familyRecurrenceFile = familyRecurrenceFiles.get(i - 1);
            }
        }
    }//GEN-LAST:event_jList11ValueChanged

    /**
    * Add more VCF files pop up menu item event handler on Search tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        // TODO add your handling code here:
        //Add more VCF files to search tab
        jFileChooser23.setMultiSelectionEnabled(true);
        if(jFileChooser23.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser23.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser23.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Search tab.
    * Adds more VCF files.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser23ActionPerformed
        // TODO add your handling code here:
        //Add more VCF files to search tab
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] temp = jFileChooser23.getSelectedFiles();
            this.selectedSearchVCFFiles = combineFileArrays(this.selectedSearchVCFFiles, temp);
            setSearchFileList();
            setSearchRunButtonState();
        }
    }//GEN-LAST:event_jFileChooser23ActionPerformed

    /**
    * Clear VCF files pop up menu item event handler on Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        // TODO add your handling code here:
        //Clear VCF files from search tab
        this.selectedSearchVCFFiles = null;
        this.activeSearchVCFFiles = null;
        setSearchFileList();
        setSearchRunButtonState();
    }//GEN-LAST:event_jMenuItem29ActionPerformed

    /**
    * Open Filter scenario button event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        //open family analysis filter scenario
        jFileChooser24.setMultiSelectionEnabled(false);
        if(jFileChooser24.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser24.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser24.showOpenDialog(this);
        setFamilyRunButtonState();
    }//GEN-LAST:event_jButton19ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Loads filter scenario.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser24ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser24.getSelectedFile(); 
            loadFamilyAnalysisFilterScenario(f);
        }
    }//GEN-LAST:event_jFileChooser24ActionPerformed

    /**
    * Save Filter scenario button event handler on Family analysis tab.
    * Displays corresponding JFileChooser
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser25.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){            
            jFileChooser25.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser25.showSaveDialog(this);
    }//GEN-LAST:event_jButton20ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Family analysis tab.
    * Saves loaded filters as scenario .fsc file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser25ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser25.getSelectedFile(); 
            saveFamilyAnalysisFilterScenario(f);
        }
    }//GEN-LAST:event_jFileChooser25ActionPerformed

    /**
    * JFileChooser.APPROVE_SELECTION event handler on Filter tab.
    * Sets the file of the VCF file output. Enforces .vcf ending.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser9ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            vcfOutputFile = jFileChooser9.getSelectedFile();
            if(!vcfOutputFile.getName().endsWith(".vcf.gz")){
                jButton1.setEnabled(false);
                vcfOutputFile = new File(vcfOutputFile.getAbsolutePath() + ".vcf.gz");                
            }
        }else{
            vcfOutputFile = null;
        }    
    }//GEN-LAST:event_jFileChooser9ActionPerformed

    /**
    * Show popup menu for variants search in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea4MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu14.show(jTextArea4, p.x, p.y);
        }
    }//GEN-LAST:event_jTextArea4MouseClicked

    /**
    * Show popup menu for regions search in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea5MouseClicked
        // TODO add your handling code here:
        if(evt.getButton() == MouseEvent.BUTTON3){
            Point p = evt.getPoint();           
            jPopupMenu15.show(jTextArea5, p.x, p.y);
        }
    }//GEN-LAST:event_jTextArea5MouseClicked

    /**
    * Clear variants search input area in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        // TODO add your handling code here:
        jTextArea4.setText("");        
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    /**
    * Clear regions search input area in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        // TODO add your handling code here:
        jTextArea5.setText("");
    }//GEN-LAST:event_jMenuItem31ActionPerformed

    /**
    * Show example variants in variants search input area in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        // TODO add your handling code here:
        jTextArea4.setText("");
        jTextArea4.append("12-132313098-G-GGCTGCCGCT\n");
        jTextArea4.append("4-101108877-T-C\n");
        jTextArea4.append("20-9547018-C-G\n");
        jTextArea4.append("7-143929717-G-A\n");
        jTextArea4.append("7-143929720-T-C\n");
        jTextArea4.append("1-152278049-A-C\n");
        jTextArea4.append("8-100844594-TAG-T\n");
        jTextArea4.append("7-127999645-C-T\n");
        jTextArea4.append("1-150199039-CTCCTCT-C\n");
        jTextArea4.append("8-143959174-T-C\n"); 
        
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    /**
    * Show example genomic regions in regions search input area in Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem33ActionPerformed
        // TODO add your handling code here:
        jTextArea5.setText("");
        jTextArea5.append("12	132313098	132314098\n");
        jTextArea5.append("chr12	132313098	132314098\n");
        jTextArea5.append("Chr12	132313098	132314098\n");
        jTextArea5.append("cHr12	132313098	132314098\n");
        jTextArea5.append("12:132313098-132314098\n");
        jTextArea5.append("chr12:132313098-132314098\n");
        jTextArea5.append("Chr12:132313098-132314098\n");
        jTextArea5.append("cHr12:132313098-132314098\n");
        jTextArea5.append("12 132313098 132314098\n");
        jTextArea5.append("chr12 132313098 132314098\n");
        jTextArea5.append("Chr12 132313098 132314098\n");
        jTextArea5.append("cHr12 132313098 132314098\n");

    }//GEN-LAST:event_jMenuItem33ActionPerformed

    private void jMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu2ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jMenu2ActionPerformed

    /**
    * Show About Dialog.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        // TODO add your handling code here:        
        (new About(this, true)).setVisible(true);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    
    /**
    * jList1 value changed event handler. jList1 holds the VCF files loaded for filtering in the Filter tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
        setFilterRunButtonState();
    }//GEN-LAST:event_jList1ValueChanged

    
    /**
    * jRadioButton2 state changed event handler. If jRadioButton2 is selected output will be written to VCF file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton2StateChanged
        // TODO add your handling code here:
        if(jRadioButton2.isSelected()){
            setFilterRunButtonState();
        }
    }//GEN-LAST:event_jRadioButton2StateChanged

    /**
    * jRadioButton4 state changed event handler. If jRadioButton4 is selected output will be drawn to Hilbert curve.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jRadioButton4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRadioButton4StateChanged
        // TODO add your handling code here:
        if(jRadioButton4.isSelected()){
            setFilterRunButtonState();
        }
    }//GEN-LAST:event_jRadioButton4StateChanged

    /**
    * jList3 value changed event handler. jList3 holds the VCF files loaded for searching in the Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList3ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList3ValueChanged
        // TODO add your handling code here:
        setSearchRunButtonState();
    }//GEN-LAST:event_jList3ValueChanged

    /**
    * jTextArea4 caret update  event handler. jTextArea4 holds the variants in ExAC format to be used for searching in the Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea4CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextArea4CaretUpdate
        // TODO add your handling code here:
        setSearchRunButtonState();
    }//GEN-LAST:event_jTextArea4CaretUpdate

    /**
    * jTextArea5 caret update  event handler. jTextArea5 holds the genomic regions to be used for searching in the Search tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jTextArea5CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jTextArea5CaretUpdate
        // TODO add your handling code here:
        setSearchRunButtonState();
    }//GEN-LAST:event_jTextArea5CaretUpdate

    /**
    * jList4 value changed event handler. jList4 holds the affected VCF files loaded in the FamilyAnalysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jList4ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList4ValueChanged
        // TODO add your handling code here:
        if(jList4.getSelectedIndices().length == 1){
            setFamilyControls(true);
            if(relationships == null){
                relationships = new Relationships(this, true, selectedAffectedVCFFiles, selectedUnaffectedVCFFiles);
            }
            Relationship r = getRelationship((String)jList4.getSelectedValue());
            if(r == null && selectedUnaffectedVCFFiles != null && selectedAffectedVCFFiles != null){
                r = new Relationship((String)jList4.getSelectedValue(), selectedUnaffectedVCFFiles, selectedAffectedVCFFiles);
                r.setMother("None");
                r.setFather("None");
                relationships.addRelationship(r);
            }else if(selectedAffectedVCFFiles != null && selectedUnaffectedVCFFiles != null){
                for(int i = 0; i < selectedAffectedVCFFiles.length; i++){
                    if(!r.isValuePresent(selectedAffectedVCFFiles[i].getName())){
                        r.addSelectableValue(selectedAffectedVCFFiles[i].getName());
                    }
                }
                for(int i = 0; i < selectedUnaffectedVCFFiles.length; i++){
                    if(!r.isValuePresent(selectedUnaffectedVCFFiles[i].getName())){
                        r.addSelectableValue(selectedUnaffectedVCFFiles[i].getName());
                    }
                }
            }
            if(jComboBox5.getModel().getSize() > 0 && jComboBox6.getModel().getSize() > 0){                
                    jComboBox5.setSelectedItem(r.getMother());              
                    jComboBox6.setSelectedItem(r.getFather()); 
                    if(r.isMale()){
                        jRadioButton5.setSelected(true);
                        jRadioButton6.setSelected(false);
                    }else{
                        jRadioButton5.setSelected(false);
                        jRadioButton6.setSelected(true);
                    }
            }
        }else{
            setFamilyControls(false);
        }
        setFamilyRunButtonState();
    }//GEN-LAST:event_jList4ValueChanged

    /**
    * jComboBox5 item state changed event handler. The selected VCF file in jComboBox5 defines the mother of the affected individual selected in jList4 in the FamilyAnalysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jComboBox5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox5ItemStateChanged
        // TODO add your handling code here:
        if(jList4.getSelectedIndices().length > 1){
            jList4.setSelectedIndex(0);
            new Warning(this, "Please select a single affected individual to set relationships.");
        }else if(jList4.getSelectedIndices().length == 1){
            String individual = (String)jList4.getSelectedValue();
            Relationship r = getRelationship(individual);
            if(r != null){
                r.setMother((String)jComboBox5.getSelectedItem());
            }
        }
    }//GEN-LAST:event_jComboBox5ItemStateChanged

    /**
    * jComboBox6 item state changed event handler. The selected VCF file in jComboBox6 defines the father of the affected individual selected in jList4 in the FamilyAnalysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jComboBox6ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox6ItemStateChanged
        // TODO add your handling code here:
        if(jList4.getSelectedIndices().length > 1){
            jList4.setSelectedIndex(0);
            new Warning(this, "Please select a single affected individual to set relationships.");
        }else if(jList4.getSelectedIndices().length == 1){
            String individual = (String)jList4.getSelectedValue();
            Relationship r = getRelationship(individual);
            if(r != null){
                r.setFather((String)jComboBox6.getSelectedItem());
            }
        }
    }//GEN-LAST:event_jComboBox6ItemStateChanged

    /**
    * jButton5 action performed event handler. jButton5 opens the Relationships viewer in the FamilyAnalysis tab.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(relationships != null){
            if(jList4.getSelectedIndices().length == 1){
                Relationship r = relationships.getRelationshipForIndividual((String)jList4.getSelectedValue());
                
                if(r != null){
                    r.setMother((String)jComboBox5.getSelectedItem());
                    r.setFather((String)jComboBox6.getSelectedItem());
                    r.setMale(jRadioButton5.isSelected());
                }
            }
            relationships.setVisible(true);
        }else{
            relationships = new Relationships(this, true);
            if(selectedUnaffectedVCFFiles != null && selectedAffectedVCFFiles != null){
                DefaultListModel lm = (DefaultListModel)jList4.getModel();
                for(int i = 0; i < lm.getSize(); i++){
                    Relationship  r = new Relationship((String)lm.get(i), selectedUnaffectedVCFFiles, selectedAffectedVCFFiles);
                    r.setMother("None");
                    r.setFather("None");
                    r.setMale(true);
                    relationships.addRelationship(r);
                }
            }
            relationships.setVisible(true);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
    * Index text VCF action performed event handler. Opens FileChooser for text VCF file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jMenuItem35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem35ActionPerformed
        // TODO add your handling code here:
        jFileChooser26.showOpenDialog(this);
    }//GEN-LAST:event_jMenuItem35ActionPerformed

    /**
    * Index text VCF action performed event handler. Opens FileChooser for text VCF file.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jFileChooser26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser26ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File[] files = jFileChooser26.getSelectedFiles();
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            for(File f : files){                
                if(f.getName().endsWith(".vcf") || f.getName().endsWith(".vcf.gz")){
                    this.setCursor(waitCursor);
                    try{
                        IndexedVCFFileWriter.index(f, false);                   
                    }catch(Exception e){                        
                        new Warning(this, "This file could not be indexed. " + f.getName() + " " + e.getMessage());  
                        e.printStackTrace();
                    }
                }else{
                    this.setCursor(defaultCursor);
                    new Warning(this, "Input file must be VCF text (.vcf) or compressed VCF text (.vcf.gz)");
                }
            }  
            this.setCursor(defaultCursor);
        }  
    }//GEN-LAST:event_jFileChooser26ActionPerformed

    /**
    * Example Filter Run action performed event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        jButton1.setEnabled(false);
        File example = PREFERENCES.getSampleVCFFile();
        if(example != null && example.exists()){
            this.selectedVCFFiles = new File[1];
            this.selectedVCFFiles[0] = PREFERENCES.getSampleVCFFile();
            setFileList(); 
            setFilterRunButtonState();

            VCFInfoHeaderLine header = PREFERENCES.getInfoHeaderLine("CHROM"); 
            if(header != null){
                int i = jComboBox1.getSelectedIndex();
                //Filter f = FilterFactory.getFilter(PREFERENCES.getAvailableFilters().get(i));
                filters = new ArrayList<Filter>();  
                jPanel1.removeAll();
                initAddFilters();
                initRemoveFilters(); 
                Filter f = FilterFactory.getFilter(header);
                setFilterDefaults(f);   
                f.setIndex(filters.size());
                f.setGui(this);
                f.getCriterion1().setText("1");        
                f.getCriterion2().setText("X");    
                f.setPredicate1();
                f.setPredicate2();                
                addToMyLayout(jPanel1, f, 10, filters.size() * filterVerticalGap + 10, 0, 0);                
                filters.add(f);
                initAddFilters();
                initRemoveFilters();        
                jButton1ActionPerformed(evt);
                //filters = new ArrayList<Filter>();   
                //jPanel1.removeAll();
                //initRemoveFilters();     
            }else{
                new Warning(this, "Example VCF file must contain valid header lines.");
                return;
            }
        }else{
            new Warning(this, "Please define the path to the example VCF file in the VCFFilter Preferences (File -> Preferences -> Files tab).");
            return;
        } 
        jButton1.setEnabled(true);
        
    }//GEN-LAST:event_jButton16ActionPerformed

    /**
    * Example Search Run action performed event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
        jButton7.setEnabled(false);
        this.selectedSearchVCFFiles = new File[1];
        this.selectedSearchVCFFiles[0] = PREFERENCES.getSampleVCFFile();
        if(this.selectedSearchVCFFiles[0] != null && this.selectedSearchVCFFiles[0].exists()){
            try{
                setSearchFileList();
                setSearchRunButtonState();
                jTextArea4.setText("1-10720305-A-G");
                jTextArea5.setText("chr1:10720000-10730000");
                jButton7ActionPerformed(evt);
            }catch(Exception e){
                new Warning(this, e.getMessage());
            }
        }else{
            new Warning(this, "Please define the path to the example VCF file in the VCFFilter Preferences (File -> Preferences -> Files tab).");
        }
        jButton7.setEnabled(true);
        
    }//GEN-LAST:event_jButton24ActionPerformed

    /**
    * Example Family analysis Run action performed event handler.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
        jButton12.setEnabled(false);
        File example = PREFERENCES.getSampleVCFFile();
        if(example != null && example.exists()){
            File dir = example.getParentFile();
            File[] files = dir.listFiles();
            File child1 = null;
            File child2 = null;
            File mother = null;
            File father = null;
            for(File f : files){
                if(f.getName().equals("child1.vcf_indexed.vcf")){
                    child1 = f;
                }else if(f.getName().equals("child2.vcf_indexed.vcf")){
                    child2 = f;
                }else if(f.getName().equals("father.vcf_indexed.vcf")){
                    father = f;
                }else if(f.getName().equals("mother.vcf_indexed.vcf")){
                    mother = f;
                }                
            }
            if(child1 == null || child2 == null || mother == null || father == null){
                new Warning(this, "Couldn't localize family analysis example files.");
                return;
            }
            
            this.selectedAffectedVCFFiles = new File[2];
            this.selectedAffectedVCFFiles[0] = child1;
            this.selectedAffectedVCFFiles[1] = child2;
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setAffectedFileList();          
            setFamilyRunButtonState();
            this.selectedUnaffectedVCFFiles = new File[2];
            this.selectedUnaffectedVCFFiles[0] = mother;
            this.selectedUnaffectedVCFFiles[1] = father;
            if(this.relationships != null){
                this.relationships.dispose();
                this.relationships = null;
            }
            setUnaffectedFileList();
            setFamilyRunButtonState();
            
            relationships = new Relationships(this, true);
            if(selectedUnaffectedVCFFiles != null && selectedAffectedVCFFiles != null){
                DefaultListModel lm = (DefaultListModel)jList4.getModel();
                for(int i = 0; i < lm.getSize(); i++){
                    Relationship  r = new Relationship((String)lm.get(i), selectedUnaffectedVCFFiles, selectedAffectedVCFFiles);
                    r.setMother("mother.vcf_indexed.vcf");
                    r.setFather("father.vcf_indexed.vcf");
                    r.setMale(true);
                    relationships.addRelationship(r);
                }
            }
            relationships.setVisible(true);
            jButton12ActionPerformed(evt);
        }
        jButton12.setEnabled(true);
        
          
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jComboBox7ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox7ItemStateChanged
        // TODO add your handling code here:
        recurrenceType = jComboBox7.getSelectedItem().toString();
    }//GEN-LAST:event_jComboBox7ItemStateChanged

    private void jComboBox8ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox8ItemStateChanged
        // TODO add your handling code here:
        recurrenceType = jComboBox8.getSelectedItem().toString();
    }//GEN-LAST:event_jComboBox8ItemStateChanged

    private void jMenuItem36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem36ActionPerformed
        // TODO add your handling code here:
        //remove selected VCF files in Filter tab
        File[] temp = new File[selectedVCFFiles.length - activeVCFFiles.length];
        int count = 0;
        for(int i = 0; i < selectedVCFFiles.length; i++){
            if(!containsFile(activeVCFFiles, selectedVCFFiles[i])){
                temp[count] = selectedVCFFiles[i];
                count++;
            }
        }        
        selectedVCFFiles = temp;            
        setFileList();
        setFilterRunButtonState();       
    }//GEN-LAST:event_jMenuItem36ActionPerformed

    
    
    private void jMenuItem37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem37ActionPerformed
        // TODO add your handling code here:
        //keep selected VCF files in Filter tab
        selectedVCFFiles = new File[activeVCFFiles.length];
        for(int i = 0; i < activeVCFFiles.length; i++){
            selectedVCFFiles[i] = activeVCFFiles[i];
        }        
        setFileList();
        setFilterRunButtonState();   
    }//GEN-LAST:event_jMenuItem37ActionPerformed

    private void jMenuItem38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem38ActionPerformed
        // TODO add your handling code here:
        //remove selected affected VCF files in Family tab
        int[] selected = jList4.getSelectedIndices();        
        activeAffectedVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){            
            activeAffectedVCFFiles[count] = selectedAffectedVCFFiles[selected[i]];
            count++;            
        }
        File[] temp = new File[selectedAffectedVCFFiles.length - activeAffectedVCFFiles.length];
        count = 0;
        for(int i = 0; i < selectedAffectedVCFFiles.length; i++){
            if(!containsFile(activeAffectedVCFFiles, selectedAffectedVCFFiles[i])){
                temp[count] = selectedAffectedVCFFiles[i];
                count++;
            }
        }        
        selectedAffectedVCFFiles = temp;            
        setAffectedFileList();            
        setFamilyRunButtonState();
    }//GEN-LAST:event_jMenuItem38ActionPerformed

    private void jMenuItem39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem39ActionPerformed
        // TODO add your handling code here:
        //keep selected affected VCF files in Family tab
        int[] selected = jList4.getSelectedIndices();        
        activeAffectedVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){
            activeAffectedVCFFiles[count] = selectedAffectedVCFFiles[selected[i]];
            count++;
    
        }        
        selectedAffectedVCFFiles = new File[activeAffectedVCFFiles.length];
        for(int i = 0; i < activeAffectedVCFFiles.length; i++){
            selectedAffectedVCFFiles[i] = activeAffectedVCFFiles[i];
        }        
        setAffectedFileList();            
        setFamilyRunButtonState();  
    }//GEN-LAST:event_jMenuItem39ActionPerformed

    private void jMenuItem40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem40ActionPerformed
        // TODO add your handling code here:
        //remove selected unaffected VCF files in Family tab
        int[] selected = jList5.getSelectedIndices();        
        activeUnaffectedVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){            
            activeUnaffectedVCFFiles[count] = selectedUnaffectedVCFFiles[selected[i]];
            count++;            
        }
        File[] temp = new File[selectedUnaffectedVCFFiles.length - activeUnaffectedVCFFiles.length];
        count = 0;
        for(int i = 0; i < selectedUnaffectedVCFFiles.length; i++){
            if(!containsFile(activeUnaffectedVCFFiles, selectedUnaffectedVCFFiles[i])){
                temp[count] = selectedUnaffectedVCFFiles[i];
                count++;
            }
        }        
        selectedUnaffectedVCFFiles = temp;            
        setUnaffectedFileList();            
        setFamilyRunButtonState();
    }//GEN-LAST:event_jMenuItem40ActionPerformed

    private void jMenuItem41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem41ActionPerformed
        // TODO add your handling code here:
        //keep selected unaffected VCF files in Family tab
        int[] selected = jList5.getSelectedIndices();        
        activeUnaffectedVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){            
            activeUnaffectedVCFFiles[count] = selectedUnaffectedVCFFiles[selected[i]];
            count++;            
        }
        selectedUnaffectedVCFFiles = new File[activeUnaffectedVCFFiles.length];
        for(int i = 0; i < activeUnaffectedVCFFiles.length; i++){
            selectedUnaffectedVCFFiles[i] = activeUnaffectedVCFFiles[i];
        }        
        setUnaffectedFileList();           
        setFamilyRunButtonState(); 
    }//GEN-LAST:event_jMenuItem41ActionPerformed

    private void jMenuItem42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem42ActionPerformed
        // TODO add your handling code here:
        //remove selected search VCF files in Search tab
        int[] selected = jList3.getSelectedIndices();        
        activeSearchVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){            
            activeSearchVCFFiles[count] = selectedSearchVCFFiles[selected[i]];
            count++;            
        }
        File[] temp = new File[selectedSearchVCFFiles.length - activeSearchVCFFiles.length];
        count = 0;
        for(int i = 0; i < selectedSearchVCFFiles.length; i++){
            if(!containsFile(activeSearchVCFFiles, selectedSearchVCFFiles[i])){
                temp[count] = selectedSearchVCFFiles[i];
                count++;
            }
        }        
        selectedSearchVCFFiles = temp;            
        setSearchFileList();
        setSearchRunButtonState();    
    }//GEN-LAST:event_jMenuItem42ActionPerformed

    private void jMenuItem43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem43ActionPerformed
        // TODO add your handling code here:
        //keep selected VCF files in Search tab
        int[] selected = jList3.getSelectedIndices();        
        activeSearchVCFFiles = new File[selected.length];
        int count = 0;
        for(int i = 0; i < selected.length; i++){            
            activeSearchVCFFiles[count] = selectedSearchVCFFiles[selected[i]];
            count++;            
        }
        selectedSearchVCFFiles = new File[activeSearchVCFFiles.length];
        for(int i = 0; i < activeSearchVCFFiles.length; i++){
            selectedSearchVCFFiles[i] = activeSearchVCFFiles[i];
        }        
        setSearchFileList();
        setSearchRunButtonState();   
    }//GEN-LAST:event_jMenuItem43ActionPerformed

    private void jMenuItem44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem44ActionPerformed
        // TODO add your handling code here:
        if(jFileChooser27.getCurrentDirectory() == null && PREFERENCES.getDefaultDir() != null){
            jFileChooser27.setCurrentDirectory(PREFERENCES.getDefaultDir());
        }
        jFileChooser27.showSaveDialog(this);
    }//GEN-LAST:event_jMenuItem44ActionPerformed

    private void jFileChooser27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser27ActionPerformed
        // TODO add your handling code here:
        if (evt.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File f = jFileChooser27.getSelectedFile();
            saveAsBed(f, jTextArea1.getText());            
        }
    }//GEN-LAST:event_jFileChooser27ActionPerformed

    private void jList5ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList5ValueChanged
        // TODO add your handling code here:
        int selected[] = jList5.getSelectedIndices();
        if(selected != null && selected.length > 0){
            activeUnaffectedVCFFiles = new File[selected.length];
            for(int i = 0; i < selected.length; i++){
                activeUnaffectedVCFFiles[i] = selectedUnaffectedVCFFiles[selected[i]];
            }
        }
        setFamilyRunButtonState();
    }//GEN-LAST:event_jList5ValueChanged

    private boolean containsFile(File[] array, File f){
        for(File a : array){
            if(a.getAbsolutePath().equals(f.getAbsolutePath())){                
                return true;
            }
        }
        return false;
    }
    
    /**
    * Combines two file arrays into one array of dimension a1.length + a2.lenghth.
    *
    * @author Heiko Müller
    * @param a1 first file array
    * @param a2 second file array
    * @return File[] 
    * @since 1.0
    */
    private File[] combineFileArrays(File[] a1, File[] a2){
        if(a1 != null && a2 != null){
            File[] result = new File[a1.length + a2.length];
            ArrayList<File> al = new ArrayList<File>();
            for(int i = 0; i < a1.length; i++){
                al.add(a1[i]);
            }
            for(int i = 0; i < a2.length; i++){
                al.add(a2[i]);
            }
            for(int i = 0; i < result.length; i++){
                result[i] = al.get(i);
            }
            return result;
        }else if(a1 != null && a2 == null){
            return a1;
        }else if(a1 == null && a2 != null){
            return a2;
        }else{
            return null;
        }        
    }
    
    /**
    * Saves text to file.
    *
    * @author Heiko Müller
    * @param f output file
    * @param text to be saved 
    * @since 1.0
    */
    private void save(File f, String text){
        try{
            String path = f.getAbsolutePath();
            if(!path.endsWith(".tsv")){
                path = path + ".tsv";
            }
            FileWriter fwr = new FileWriter(path);
            BufferedWriter br = new BufferedWriter(fwr);
            br.write(text);
            br.flush();
            br.close();            
        }catch(IOException ioe){
            ioe.printStackTrace();
        }        
    }
    
    private void saveAsBed(File f, String text){
        try{
            String path = f.getAbsolutePath();
            if(!path.endsWith(".bed")){
                path = path + ".bed";
            }
            String[] lines = text.split("\n");
            String line = "";
            int counter = 0;
            while(!lines[counter].startsWith("Variants found:")){
                counter++;
            }
            counter++;
            counter++;
            FileWriter fwr = new FileWriter(path);
            BufferedWriter br = new BufferedWriter(fwr);
            for(int i = counter; i < lines.length; i++){
                String[] coords = lines[i].split("\t");
                if(coords.length > 1){
                    br.write(coords[0] + "\t" + coords[1] + "\t" + coords[1] + "\n");
                }
            }
            br.flush();
            br.close();            
        }catch(IOException ioe){
            ioe.printStackTrace();
        }        
    }
    
    /**
    * Saves filter scenario to file. Forces the file to have .fsc extension.
    *
    * @author Heiko Müller
    * @param file output file
    * @since 1.0
    */
    private void saveFilterScenario(File file){
        try{
            String path = file.getAbsolutePath(); 
            if(!path.endsWith(".fsc")){
                path = path + ".fsc";
            }
            FileWriter fwr = new FileWriter(path);
            BufferedWriter br = new BufferedWriter(fwr);
            for(Filter f : filters){
                br.write(f.getSettings() + "\r\n");
            }            
            br.flush();
            br.close();            
        }catch(IOException ioe){
            ioe.printStackTrace();
        }        
    }
    
    /**
    * Saves filter scenario to file. Forces the file to have .fsc extension.
    *
    * @author Heiko Müller
    * @param file output file
    * @since 1.0
    */
    private void saveFamilyAnalysisFilterScenario(File file){
        try{
            String path = file.getAbsolutePath(); 
            if(!path.endsWith(".fsc")){
                path = path + ".fsc";
            }
            FileWriter fwr = new FileWriter(path);
            BufferedWriter br = new BufferedWriter(fwr);
            for(Filter f : familyFilters){
                br.write(f.getSettings() + "\r\n");
            }            
            br.flush();
            br.close();            
        }catch(IOException ioe){
            ioe.printStackTrace();
        }        
    }
    
    /**
    * Loads filter scenario to Filter tab. 
    *
    * @author Heiko Müller
    * @param file input file
    * @since 1.0
    */
    private void loadFilterScenario(File file){
        try{
            FilterFactory ff = new FilterFactory(this);
            filters = new ArrayList<Filter>();
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int idx = 0;
            while((line = br.readLine()) != null){
                FilterSettings fsc = new FilterSettings(line);
                if(fsc.getId() != null){
                    Filter f = ff.getFilter(fsc);
                    if(f != null){
                        f.setIndex(idx);
                        f.setGui(this);
                        filters.add(f);
                        idx++;
                    }
                }
            }            
            jPanel1.removeAll();
            if(filters.size() > 0){
                for(int i = 0; i < filters.size() && i < 10; i++){
                    addToMyLayout(jPanel1, filters.get(i), 10, 10 + i*filterVerticalGap, 0, 0);
                }  
                initAddFilters();
                initRemoveFilters();
            }else{
                new Warning(this, "No valid filters found in this scenario for current set of VCF header lines.");
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
            new Warning(this, "Problems reading scenario file.");
        }        
    }
    
    /**
    * Loads filter scenario to Family analysis tab. 
    *
    * @author Heiko Müller
    * @param file input file
    * @since 1.0
    */
    private void loadFamilyAnalysisFilterScenario(File file){
        try{
            FilterFactory ff = new FilterFactory(this);
            familyFilters = new ArrayList<Filter>();
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int idx = 0;
            while((line = br.readLine()) != null){
                FilterSettings fsc = new FilterSettings(line);
                if(fsc.getId() != null){
                    Filter f = ff.getFilter(fsc);
                    if(f != null){
                        f.setIndex(idx);
                        f.setGui(this);
                        familyFilters.add(f);
                        idx++;
                    }
                }
            }
            jPanel14.removeAll();
            if(familyFilters.size() > 0){
                for(int i = 0; i < familyFilters.size() && i < 10; i++){
                    addToMyLayout(jPanel14, familyFilters.get(i), 10, 10 + i*filterVerticalGap, 0, 0);
                }  
                initAddFamilyFilters();
                initRemoveFamilyFilters();
            }else{
                new Warning(this, "No valid filters found in this scenario for current set of VCF header lines.");
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
            new Warning(this, "Problems reading scenario file.");
        }        
    }
    
    /**
    * Tests if the array contains a given value. Returns true if the value is found, false otherwise.
    *
    * @author Heiko Müller
    * @param indices input integer array
    * @param value to be tested for presence
    * @return boolean
    * @since 1.0
    */
    private boolean selectionContains(int[] indices, int value){
        for(int i : indices){
            if(i == value){
                return true;
            }
        }
        return false;
    }
    
    /**
    * Removes Allele frequency filter (AF) from Filter tab to ensure proper calculation of recurrence values.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void removeAFFilter(){
        int selected = jComboBox2.getSelectedIndex();
        String selectedHeader = (String) jComboBox2.getItemAt(selected);
        ArrayList<String> remaining = new ArrayList<String>();
        for (int i = 0; i < jComboBox2.getItemCount(); i++) {
            if (i != selected) {
                remaining.add((String)jComboBox2.getItemAt(i));
            }
        }
        jPanel1.removeAll();
        filters = new ArrayList<Filter>();
        for (int i = 0; i < remaining.size(); i++) {
            //int index = FilterFactory.getIndex(remaining.get(i));
            //Filter f = FilterFactory.getFilter(FilterFactory.infofields[index]);
            Filter f = FilterFactory.getFilter(remaining.get(i), PREFERENCES);
            setFilterDefaults(f);
            addToMyLayout(jPanel1, f, 10, i * filterVerticalGap + 10, 0, 0);
            filters.add(f);

        }
        initRemoveFilters();
    }
    
    /**
    * Adds selected VCF files to JList on Filter tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void setFileList() {
        if (selectedVCFFiles != null && selectedVCFFiles.length > 0) {
            String[] files = new String[selectedVCFFiles.length];
            DefaultListModel listModel = new DefaultListModel();
            int[] selected = new int[selectedVCFFiles.length];
            for (int i = 0; i < selectedVCFFiles.length; i++) {
                //System.out.println("setting file " + selectedVCFFiles[i].getName());
                files[i] = selectedVCFFiles[i].getName();
                listModel.addElement(selectedVCFFiles[i].getName());
                selected[i] = i;
            }
            jList1.setModel(listModel);
            jList1.setSelectedIndices(selected);
            jList1.setToolTipText("Select files to be filtered.");
        }else{            
            DefaultListModel listModel = new DefaultListModel();           
            jList1.setModel(listModel);            
            jList1.setToolTipText("Select files to be filtered.");
        }
    }    
      
    public void setAllItemsSelected(JList jl){
        int[] selected = new int[jl.getModel().getSize()];
        for (int i = 0; i < selected.length; i++) {                
                selected[i] = i;
        }
        jl.setSelectedIndices(selected);    
    }
    
    /**
    * Adds selected recurrence files to JList on Filter tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void setRecurrenceFileList() {
        if (jFileChooser2.getSelectedFiles() != null) {
            String[] files = new String[jFileChooser2.getSelectedFiles().length];
            DefaultListModel listModel = new DefaultListModel();
            int[] selected = new int[jFileChooser2.getSelectedFiles().length + 1];
            listModel.addElement("None");   
            selected[0] = 0;
            for (int i = 0; i < files.length; i++) {                
                listModel.addElement(jFileChooser2.getSelectedFiles()[i].getName());                
            }
            jList9.setModel(listModel);
            jList9.setSelectedIndices(selected);
            jList9.setToolTipText("Select recurrence file to be used for filtering.");
        }
    }

    /**
    * Adds selected VCF files to JList on Search tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void setSearchFileList() {
        if (selectedSearchVCFFiles != null && selectedSearchVCFFiles.length > 0) {
            String[] files = new String[selectedSearchVCFFiles.length];
            DefaultListModel listModel = new DefaultListModel();
            int[] selected = new int[selectedSearchVCFFiles.length];
            for (int i = 0; i < selectedSearchVCFFiles.length; i++) {
                files[i] = selectedSearchVCFFiles[i].getName();
                listModel.addElement(selectedSearchVCFFiles[i].getName());
                selected[i] = i;
            }
            jList3.setModel(listModel);
            jList3.setSelectedIndices(selected);
            jList3.setToolTipText("Select files to be searched.");
        }else{
            DefaultListModel listModel = new DefaultListModel();
            jList3.setModel(listModel);
            jList3.setToolTipText("Select files to be searched.");
        }
    }

    /**
    * Adds selected affected VCF files to JList on Family analysis tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void setAffectedFileList() {      
        if (selectedAffectedVCFFiles != null && selectedAffectedVCFFiles.length > 0) {
            int[] selected = new int[selectedAffectedVCFFiles.length];            
            String[] files = new String[selectedAffectedVCFFiles.length];
            DefaultListModel listModel = new DefaultListModel();            
            
            for (int i = 0; i < selectedAffectedVCFFiles.length; i++) {
                files[i] = selectedAffectedVCFFiles[i].getName();
                listModel.addElement(selectedAffectedVCFFiles[i].getName());
                selected[i] = i;

            }
            jList4.setModel(listModel);
            jList4.setSelectedIndices(selected);
            jList4.setToolTipText("Select single file to relationships.");
            setRelationshipFiles();
        }else{
            DefaultListModel listModel = new DefaultListModel();
            jList4.setModel(listModel);            
            jList4.setToolTipText("Select single file to relationships.");
        }
    }
    
    private void setUnaffectedFileList() {
        if (selectedUnaffectedVCFFiles != null && selectedUnaffectedVCFFiles.length > 0) {
            String[] files = new String[selectedUnaffectedVCFFiles.length];
            DefaultListModel listModel = new DefaultListModel();
            int[] selected = new int[selectedUnaffectedVCFFiles.length];
            for (int i = 0; i < selectedUnaffectedVCFFiles.length; i++) {                
                listModel.addElement(selectedUnaffectedVCFFiles[i].getName());
                selected[i] = i;  
                files[i] = selectedUnaffectedVCFFiles[i].getName();
            }            
            jList5.setModel(listModel);
            jList5.setSelectedIndices(selected);
            jList5.setToolTipText("Select files to be searched."); 
            setRelationshipFiles();
        }else{ 
            DefaultListModel listModel = new DefaultListModel();
            jList5.setModel(listModel);
            jList5.setToolTipText("Select files to be searched.");
        }
    }

    /**
    * Adds selected unaffected VCF files to JList on Family analysis tab.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void setUnaffectedFileList0() {
        if (selectedUnaffectedVCFFiles != null && selectedUnaffectedVCFFiles.length > 0 && selectedAffectedVCFFiles != null && selectedAffectedVCFFiles.length > 0) {
            String[] files1 = new String[selectedUnaffectedVCFFiles.length + selectedAffectedVCFFiles.length];            
            jComboBox5.removeAllItems();
            jComboBox6.removeAllItems();
            jComboBox5.addItem("None");
            jComboBox6.addItem("None");
            jComboBox5.setSelectedIndex(0);
            jComboBox6.setSelectedIndex(0);
            DefaultListModel listModel = new DefaultListModel();
            int[] selected = new int[selectedUnaffectedVCFFiles.length];
            for (int i = 0; i < selectedUnaffectedVCFFiles.length; i++) {
                files1[i] = selectedUnaffectedVCFFiles[i].getName();
                listModel.addElement(selectedUnaffectedVCFFiles[i].getName());
                selected[i] = i;
                jComboBox5.addItem(selectedUnaffectedVCFFiles[i].getName());
                jComboBox6.addItem(selectedUnaffectedVCFFiles[i].getName());
            }
            for (int i = 0; i < selectedAffectedVCFFiles.length; i++) {
                files1[i + selectedUnaffectedVCFFiles.length] = selectedAffectedVCFFiles[i].getName();
                //listModel.addElement(selectedAffectedVCFFiles[i].getName());
                //selected[i] = i;
                jComboBox5.addItem(selectedAffectedVCFFiles[i].getName());
                jComboBox6.addItem(selectedAffectedVCFFiles[i].getName());
            }
            jList5.setModel(listModel);
            jList5.setSelectedIndices(selected);
            jList5.setToolTipText("Select files to be searched.");
        }else{
            jComboBox5.removeAllItems();
            jComboBox6.removeAllItems();
            DefaultListModel listModel = new DefaultListModel();
            jList5.setModel(listModel);
            jList5.setToolTipText("Select files to be searched.");
        }
        
    }
    
    private void setRelationshipFiles() {
        if (selectedUnaffectedVCFFiles != null && selectedUnaffectedVCFFiles.length > 0 && selectedAffectedVCFFiles != null && selectedAffectedVCFFiles.length > 0) {
            String[] files1 = new String[selectedUnaffectedVCFFiles.length + selectedAffectedVCFFiles.length];            
            jComboBox5.removeAllItems();
            jComboBox6.removeAllItems();
            jComboBox5.addItem("None");
            jComboBox6.addItem("None");
            jComboBox5.setSelectedIndex(0);
            jComboBox6.setSelectedIndex(0);
            for (int i = 0; i < selectedUnaffectedVCFFiles.length; i++) {
                files1[i] = selectedUnaffectedVCFFiles[i].getName();
                jComboBox5.addItem(selectedUnaffectedVCFFiles[i].getName());
                jComboBox6.addItem(selectedUnaffectedVCFFiles[i].getName());
            }
            for (int i = 0; i < selectedAffectedVCFFiles.length; i++) {
                files1[i + selectedUnaffectedVCFFiles.length] = selectedAffectedVCFFiles[i].getName();
                jComboBox5.addItem(selectedAffectedVCFFiles[i].getName());
                jComboBox6.addItem(selectedAffectedVCFFiles[i].getName());
            }
        }else{
            jComboBox5.removeAllItems();
            jComboBox6.removeAllItems();
        }
        if(relationships != null){
            int limit = relationships.getRelationships().size();
            for(int l = 0; l < limit; l++){
                Relationship r = relationships.getRelationships().get(l);
                for(int k = 0; k < jList4.getModel().getSize(); k++){
                    String individual = r.getIndividual();
                    if(contains(individual, jList4)){
                        JComboBox jbm = r.getMotherFileDropdownList();
                        JComboBox jbf = r.getFatherFileDropdownList();
                        jbm.removeAllItems();
                        jbf.removeAllItems();
                        for(int i = 0; i < jList5.getModel().getSize(); i++){
                            jbm.addItem(jList5.getModel().getElementAt(i).toString());
                            jbf.addItem(jList5.getModel().getElementAt(i).toString());
                        }
                        for(int i = 0; i < jList4.getModel().getSize(); i++){
                            jbm.addItem(jList4.getModel().getElementAt(i).toString());
                            jbf.addItem(jList4.getModel().getElementAt(i).toString());
                        }
                    }else{
                        relationships.removeRelationshipForIndividual(individual);                    
                        limit--;
                    }
                }

            }
        }
    }
    
    
    
    
    /**
    * Adds highlighted VCF files on the Filter tab to the variable File[] activeVCFFiles.
    *
    * @return boolean true if all VCF files have index, false otherwise
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean setActiveFileList() {
        boolean result = true;
        if (selectedVCFFiles != null && selectedVCFFiles.length > 0) {
            int[] selectedFiles = jList1.getSelectedIndices();
            if (selectedFiles != null && selectedFiles.length > 0) {
                activeVCFFiles = new File[selectedFiles.length];
                for (int i = 0; i < selectedFiles.length; i++) {
                    activeVCFFiles[i] = selectedVCFFiles[selectedFiles[i]];
                    if(i == 0){
                        try{
                            VCFFileReader vcf = new VCFFileReader(activeVCFFiles[i]);                        
                        //}catch(TribbleException te){
                        }catch(Exception te){
                            result = false;
                            new Warning(this, te.getMessage());
                        }
                    }
                    
                }
            }
        }
        return result;
    }
            
    /**
    * Adds highlighted VCF files on the Search tab to the variable File[] activeSearchVCFFiles.
    *
    * @return boolean true if all VCF files have index, false otherwise
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean setActiveSearchFileList() {
        boolean result = true;
        if (selectedSearchVCFFiles != null && selectedSearchVCFFiles.length > 0) {
            int[] selectedFiles = jList3.getSelectedIndices();
            if (selectedFiles != null && selectedFiles.length > 0) {
                activeSearchVCFFiles = new File[selectedFiles.length];
                for (int i = 0; i < selectedFiles.length && i < 3; i++) {
                    activeSearchVCFFiles[i] = selectedSearchVCFFiles[selectedFiles[i]];
                    try{
                        VCFFileReader vcf = new VCFFileReader(activeSearchVCFFiles[i]);                        
                    //}catch(TribbleException te){
                    }catch(Exception te){
                        result = false;
                        new Warning(this, te.getMessage());
                        //return result;
                    }
                }
            }
        }
        return result;
    }

    /**
    * Adds highlighted affected VCF files on the Family analysis tab to the variable File[] activeAffectedVCFFiles.
    *
    * @return boolean true if all VCF files have index, false otherwise
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean setActiveAffectedFileList() {
        boolean result = true;
        if (selectedAffectedVCFFiles != null && selectedAffectedVCFFiles.length > 0) {
            int[] selectedFiles = jList4.getSelectedIndices();
            if (selectedFiles != null && selectedFiles.length > 0) {
                activeAffectedVCFFiles = new File[selectedFiles.length];
                for (int i = 0; i < selectedFiles.length; i++) {
                    activeAffectedVCFFiles[i] = selectedAffectedVCFFiles[selectedFiles[i]];
                    try{
                        VCFFileReader vcf = new VCFFileReader(activeAffectedVCFFiles[i]);                        
                    //}catch(TribbleException te){
                    }catch(Exception te){
                        result = false;
                        new Warning(this, te.getMessage());
                    }
                }
            }
        }
        return result;
    }

    /**
    * Adds highlighted unaffected VCF files on the Family analysis tab to the variable File[] activeUnaffectedVCFFiles.
    *
    * @return boolean true if all VCF files have index, false otherwise
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean setActiveUnaffectedFileList() {
        boolean result = true;
        if (selectedUnaffectedVCFFiles != null && selectedUnaffectedVCFFiles.length > 0) {
            int[] selectedFiles = jList5.getSelectedIndices();
            if (selectedFiles != null && selectedFiles.length > 0) {
                activeUnaffectedVCFFiles = new File[selectedFiles.length];
                for (int i = 0; i < selectedFiles.length; i++) {
                    activeUnaffectedVCFFiles[i] = selectedUnaffectedVCFFiles[selectedFiles[i]];
                    try{
                        VCFFileReader vcf = new VCFFileReader(activeUnaffectedVCFFiles[i]);                        
                    //}catch(TribbleException te){
                    }catch(Exception te){
                        result = false;
                        new Warning(this, te.getMessage());
                    }
                }
            }
        }
        return result;
    }

    /*
    * Adds affected VCF files on the Family analysis tab to Male children JList.
    *
    * @author Heiko Müller
    * @since 1.0
    
    private void setMaleFileList() {
        if (selectedAffectedVCFFiles != null && selectedAffectedVCFFiles.length > 0) {
            String[] files = new String[selectedAffectedVCFFiles.length + 1];
            DefaultListModel listModel = new DefaultListModel();
            listModel.addElement("None");
            int[] selected = new int[selectedAffectedVCFFiles.length + 1];
            selected[0] = 0;
            for (int i = 0; i < selectedAffectedVCFFiles.length; i++) {
                files[i] = selectedAffectedVCFFiles[i].getName();
                listModel.addElement(selectedAffectedVCFFiles[i].getName());
                //selected[i] = i;
            }
            jList6.setModel(listModel);
            jList6.setSelectedIndices(selected);
            jList6.setToolTipText("Select files that are male.");
        }else{
            DefaultListModel listModel = new DefaultListModel();
            jList6.setModel(listModel);
            jList6.setToolTipText("Select files that are male.");
        }
    }
    */
    
    /**
    * Adds a list of file names to a JList and sets the JList's tooltip text.
    *
    * @param jl the JList data are added to
    * @param values the File values being added
    * @param none indicator whether "None" should be added as a list value
    * @param tooltip the tooltip text displayed by the JList
    * @author Heiko Müller
    * @since 1.0
    */
    private void setListValues(JList jl, ArrayList<File> values, boolean none, String tooltip) {
        if (values != null && values.size() > 0) {  
            if(none){
                DefaultListModel listModel = new DefaultListModel();
                listModel.addElement("None");
                int[] selected = new int[values.size() + 1];
                selected[0] = -1;
                for (int i = 0; i < values.size(); i++) {                
                    listModel.addElement(values.get(i).getName());  
                    selected[i + 1] = i + 1;
                }
                jl.setModel(listModel);
                jl.setSelectedIndices(selected);
                jl.setToolTipText(tooltip);
            }else{  
                DefaultListModel listModel = new DefaultListModel();          
                jl.setModel(listModel);                
                jl.setToolTipText(tooltip);
            }
        }else{
            DefaultListModel listModel = new DefaultListModel();          
            jl.setModel(listModel);                
            jl.setToolTipText(tooltip);
        }
    }

    /**
    * Tests if the VCF files belonging to the selected mother, father, and male children are highlighted
    * in the corresponding open VCF files JLists. If they weren't, they wouldn't be undergoing filtering
    * and the analysis would be invalid. Returns true if the file selection is consistent. False otherwise.
    *
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean testSelectionConsistency() {
        /*
        String mother = (String) jComboBox5.getSelectedItem();
        String father = (String) jComboBox6.getSelectedItem();
        List<Object> males = jList6.getSelectedValuesList();
        if (mother != null && !mother.equals("None")) {
            if (!contains(mother, activeUnaffectedVCFFiles)) {
                new Warning(this, "Mother selection inconsistent. Corresponding VCF file must be highlighted.");
                return false;
            }
        }
        if (father != null && !father.equals("None")) {
            if (!contains(father, activeUnaffectedVCFFiles)) {
                new Warning(this, "Father selection inconsistent. Corresponding VCF file must be highlighted.");
                return false;
            }
        }
        if (males != null && males.size() > 0 && !(((String) males.get(0)).equals("None"))) {
            if (!contains(males, activeAffectedVCFFiles)) {
                new Warning(this, "Male selection inconsistent. Corresponding VCF files must be highlighted.");
                return false;
            }
        }
        */
        return true;
    }

    /**
    * Tests if a file with a given name is present in an array of files.
    * Returns true if the file name is found in the array of files.
    * False otherwise.
    *
    * @param f the file name to be tested
    * @param flist the File[] to be searched
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean contains(String f, File[] flist) {
        for (int i = 0; i < flist.length; i++) {
            if (f.equals(flist[i].getName())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean contains(String f, JList l) {        
        for (int i = 0; i < l.getModel().getSize(); i++) {
            if (f.equals((String)l.getModel().getElementAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
    * Tests if the file name objects listed in List<Object> v correspond to a file in an array of files.
    * Returns false if at least one file name is not present as a files in the file array.
    * True if all file names are contained in the file array.
    *
    * @param v the list name to be tested
    * @param flist the File[] to be searched
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean contains(List<Object> v, File[] flist) {
        for (int i = 0; i < v.size(); i++) {
            if (!((String) v.get(i)).equals("None")) {
                if (!contains((String) v.get(i), flist)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
    * Puts filters with valid search criteria on the Filter tab to the activeFilters variable.
    * Only these active filters will be used for filtering.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void setActiveFilter() {
        if(filters != null){
            activeFilters = new ArrayList<Filter>();
            for (Filter f : filters) {
                if (f.filterIsActive()) {
                    activeFilters.add(f);
                }else{
                    //System.out.println("Filter " + f.getID() + " inactive");
                }
            }
        }
    }

    /**
    * Puts filters with valid search criteria on the Family analysis tab to the activeFamilyFilters variable.
    * Only these active filters will be used for filtering.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    private void setActiveFamilyFilter() {
        activeFamilyFilters = new ArrayList<Filter>();
        for (Filter f : familyFilters) {
            if (f.filterIsActive()) {
                activeFamilyFilters.add(f);
            }
        }
    }

    /**
    * Sets JProgressBar min and max values on the Filter tab.
    * @param min min
    * @param max max
    * @author Heiko Müller
    * @since 1.0
    */
    public void initFilterProgressBar(int min, int max) {
        jProgressBar1.setMinimum(min);
        jProgressBar1.setMaximum(max);

    }

    /**
    * Sets JProgressBar progress value on the Filter tab.
    * 
    * @param n the progress value
    * @author Heiko Müller
    * @since 1.0
    */
    public void setFilterProgress(int n) {
        jProgressBar1.setValue(n);
    }

    /**
    * Returns the JProgressBar progress on the Filter tab.
    * 
    * @return JProgressBar
    * @author Heiko Müller
    * @since 1.0
    */
    public JProgressBar getFilterProgressBar() {
        return jProgressBar1;
    }

    /**
    * Sets JProgressBar min and max values on the Family analysis tab.
    * 
    * @param min min 
    * @param max max
    * @author Heiko Müller
    * @since 1.0
    */
    public void initFamilyAnalysisProgressBar(int min, int max) {
        jProgressBar2.setMinimum(min);
        jProgressBar2.setMaximum(max);

    }

    /**
    * Sets JProgressBar progress value on the Family analysis tab.
    * 
    * @param n the progress value
    * @author Heiko Müller
    * @since 1.0
    */
    public void setFamilyAnalysisProgress(int n) {
        jProgressBar2.setValue(n);
    }

    /**
    * Returns the JProgressBar progress on the Family analysis tab.
    * 
    * @return JProgressBar
    * @author Heiko Müller
    * @since 1.0
    */
    public JProgressBar getFamilyAnalysisProgressBar() {
        return jProgressBar2;
    }

    /**
    * Returns the JProgressBar progress on the Search tab.
    * 
    * @return JProgressBar
    * @author Heiko Müller
    * @since 1.0
    */
    public JProgressBar getSearchProgressBar() {
        return jProgressBar3;
    }
    
    /**
    * Sets JProgressBar min and max values on the Search tab.
    * 
    * @param min min
    * @param max max
    * @author Heiko Müller
    * @since 1.0
    */
    public void initSearchProgressBar(int min, int max) {
        jProgressBar3.setMinimum(min);
        jProgressBar3.setMaximum(max);

    }
    
    /**
    * Sets JProgressBar progress value on the Search tab.
    * 
    * @param n the progress value
    * @author Heiko Müller
    * @since 1.0
    */
    public void setSearchProgress(int n) {
        jProgressBar3.setValue(n);
    }

    /**
    * Returns the output panel of the Filter tab.
    * 
    * @return JTextArea
    * @author Heiko Müller
    * @since 1.0
    */
    public JTextArea getFilterTextArea() {
        return jTextArea1;
    }

    /**
    * Returns the output panel of the Search tab.
    * 
    * @return JTextArea
    * @author Heiko Müller
    * @since 1.0
    */
    public JTextArea getSearchTextArea() {
        return jTextArea2;
    }

    /**
    * Returns the output panel of the Family analysis tab.
    * 
    * @return JTextArea
    * @author Heiko Müller
    * @since 1.0
    */
    public JTextArea getFamilyAnalysisTextArea() {
        return jTextArea3;
    }

    /**
    * Returns the list of white list files on the Filter tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getWhiteListFiles() {
        return filterWhiteListFiles;
    }

    /**
    * Returns the list of black list files on the Filter tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getFilterBlackListFiles() {
        return filterBlackListFiles;
    }

    /**
    * Returns the list of highlighted white list files on the Filter tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getFilterActiveWhiteListFiles() {
        return filterActiveWhiteListFiles;
    }

    /**
    * Returns the list of highlighted black list files on the Filter tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getFilterActiveBlackListFiles() {
        return filterActiveBlackListFiles;
    }

    /**
    * Returns the recurrence file on the Family analysis tab.
    * 
    * @return File
    * @author Heiko Müller
    * @since 1.0
    */
    public File getFamilyRecurrenceFile() {
        return familyRecurrenceFile;
    }

    /**
    * Returns the list of highlighted white list files on the Family analysis tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getFamilyActiveWhiteListFiles() {
        return familyActiveWhiteListFiles;
    }

    /**
    * Returns the list of highlighted black list files on the Family analysis tab.
    * 
    * @return ArrayList&#60;File&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<File> getFamilyActiveBlackListFiles() {
        return familyActiveBlackListFiles;
    }

    /**
    * Returns the array of highlighted VCF files on the Filter tab.
    * 
    * @return File[]
    * @author Heiko Müller
    * @since 1.0
    */
    public File[] getActiveVCFFiles() {
        return activeVCFFiles;
    }

    /**
    * Returns the list of active filters on the Filter tab.
    * 
    * @return ArrayList&#60;Filter&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<Filter> getActiveFilters() {
        return activeFilters;
    }

    /**
    * Returns the array of highlighted affected VCF files on the Family analysis tab.
    * 
    * @return File[]
    * @author Heiko Müller
    * @since 1.0
    */
    public File[] getActiveAffectedVCFFiles() {
        return activeAffectedVCFFiles;
    }

    /**
    * Returns the array of highlighted unaffected VCF files on the Family analysis tab.
    * 
    * @return File[]
    * @author Heiko Müller
    * @since 1.0
    */
    public File[] getActiveUnaffectedVCFFiles() {
        return activeUnaffectedVCFFiles;
    }

    /**
    * Returns the list of active filters on the Family analysis tab.
    * 
    * @return ArrayList&#60;Filter&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<Filter> getActiveFamilyFilters() {
        return activeFamilyFilters;
    }

    /**
    * Returns the array of highlighted VCF files on the Search tab.
    * 
    * @return File[]
    * @author Heiko Müller
    * @since 1.0
    */
    public File[] getActiveSearchVCFFiles() {
        return activeSearchVCFFiles;
    }

    /**
    * Returns the JTextArea holding the recurrence cutoff value on the Family analysis tab.
    * 
    * @return JTextArea
    * @author Heiko Müller
    * @since 1.0
    */
    public JTextArea getjTextArea4() {
        return jTextArea4;
    }
    
    /**
    * Returns the variants to be searched in Search tab.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getVariantsSearchText() {
        return jTextArea4.getText();
    }
    
    /**
    * Returns the regions to be searched in Search tab..
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getRegionsSearchText() {
        return jTextArea5.getText();
    }

    /**
    * Returns the Run button on the Filter tab.
    * 
    * @return JButton
    * @author Heiko Müller
    * @since 1.0
    */
    public JButton getFilterRunButton() {
        return jButton1;
    }

    /**
    * Returns the Run button on the Family analysis tab.
    * 
    * @return JButton
    * @author Heiko Müller
    * @since 1.0
    */
    public JButton getFamilyAnalysisRunButton() {
        return jButton12;
    }

    /**
    * Returns the VCF output file.
    * 
    * @return File
    * @author Heiko Müller
    * @since 1.0
    */
    public File getVcfOutputFile() {
        return vcfOutputFile;
    }

    /**
    * Returns the VCF output JRadioButton on the Filter tab.
    * 
    * @return JRadioButton
    * @author Heiko Müller
    * @since 1.0
    */
    public JRadioButton getjRadioButton2() {
        return jRadioButton2;
    }
    
    /**
    * Sets the filter default search criteria as defined in the PREFERENCES.
    * 
    * @param f the filter to be processed
    * @author Heiko Müller
    * @since 1.0
    */
    private void setFilterDefaults(Filter f) {
        Hashtable<String, FilterDefaults> defaults = PREFERENCES.getFilterDefaultsHash();
        String id = f.getID();
        if(defaults != null){
            if(defaults.containsKey(id)){
                FilterDefaults fd = defaults.get(id);                
                f.setCriterion1(fd.getOperator1() + fd.getCriterion1());
                f.setCriterion2(fd.getOperator2() + fd.getCriterion2());
                f.setCriterion3(fd.getOperator3() + fd.getCriterion3());
                f.setAndnot(fd.isAndnot());
                f.setPredicate1();
                f.setPredicate2();
                f.setPredicate3();
            }            
        }
    }

    /**
    * Returns true if the Cancel button has been pressed on the Filter tab.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean getCancelFilterWorker() {
        return cancelFilterWorker;
    }
    
    /**
    * Returns true if the Cancel button has been pressed on the Search tab.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean getCancelSearchWorker() {
        return cancelSearchWorker;
    }
    
    /**
    * Returns true if the Cancel button has been pressed on the Family analysis tab.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean getCancelFamilyWorker() {
        return cancelFamilyWorker;
    }
    
    /**
    * Returns the JComboBox for Mother VCF file selection on Family analysis tab.
    * 
    * @return JComboBox
    * @author Heiko Müller
    * @since 1.0
    */
    public JComboBox getMother(){
        return jComboBox5;
    }
    
    /**
    * Returns the JComboBox for Father VCF file selection on Family analysis tab.
    * 
    * @return JComboBox
    * @author Heiko Müller
    * @since 1.0
    */
    public JComboBox getFather(){
        return jComboBox6;
    }
    
    /**
    * Returns the List of male affected individuals on Family analysis tab.
    * 
    * @return ArrayList&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<String> getMales(){
        //return jList6;
        ArrayList<String> males = new ArrayList<String>();        
        if(relationships != null){
                
            for(Relationship r : relationships.getRelationships()){
                if(r.isMale()){
                    males.add(r.getIndividual());
                }
            }
        }
        return males;
    }
    
    
    /**
    * Returns the JList for affected VCF file highlighting on Family analysis tab.
    * 
    * @return JList
    * @author Heiko Müller
    * @since 1.0
    */
    public JList getAffectedList(){
        return jList4;
    }
    
    /**
    * Returns the JList for unaffected VCF file highlighting on Family analysis tab.
    * 
    * @return JList
    * @author Heiko Müller
    * @since 1.0
    */
    public JList getUnAffectedList(){
        return jList5;
    }
    
    /**
    * Returns the highlighted recurrence file on the Filter tab.
    * 
    * @return File
    * @author Heiko Müller
    * @since 1.0
    */
    public File getFilterRecurrenceFile(){
        return filterRecurrenceFile;
    }
    
    /**
    * Returns recurrence cutoff on the Filter tab.
    * If value is unparsable, 0 is returned.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getFilterRecurrenceCutoff(){
        String s = jTextField3.getText();
        try{
            return Integer.parseInt(s);
        }catch(NumberFormatException nfe){
            jTextField3.setText("0");
            return 0;
        }
    }
    
    /**
    * Returns recurrence cutoff on the Family analysis tab.
    * If value is unparsable, 0 is returned.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getFamilyRecurrenceCutoff() {
        String s = jTextField4.getText();
        try{
            return Integer.parseInt(s);
        }catch(NumberFormatException nfe){
            jTextField4.setText("0");
            return 0;
        }        
    }
    
    /**
    * Reports whether Hilbert curve should be shown.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean showHilbertCurve(){
        return jRadioButton4.isSelected();
    }
    
    
    /**
    * Tests if a filter with a given ID is loaded on the Filter tab.
    * Returns true if filter is found. False otherwise.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */    
    private boolean filterIsPresent(String filterID){
        for(Filter f : filters){
            if(f.getID().equals(filterID)){
                return true;
            }
        }
        return false;
    }
    
    /**
    * Returns the PREFERENCES.
    * 
    * @return VCFFilterPreferences
    * @author Heiko Müller
    * @since 1.0
    */ 
    public VCFFilterPreferences getPreferences(){
        return PREFERENCES;
    }

    /**
    * Setter for PREFERENCES.
    * 
    * @param PREFERENCES VCFFilterPreferences
    * @author Heiko Müller
    * @since 1.0
    */ 
    public void setPREFERENCES(VCFFilterPreferences PREFERENCES) {
        this.PREFERENCES = PREFERENCES;
    }
    
    
    
    /**
    * Sets the recurrence files default directory for JFileChoosers.
    * 
    * @param fc the JFileChooser being processed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setRecurrenceDefaultDir(JFileChooser fc){
        String userdir = null;
        if(PREFERENCES != null){
            userdir = PREFERENCES.getUserdir();
        }
        if(userdir != null){
            File recurrenceDir = new File(userdir + File.separator + "recurrence_files");
            if(!recurrenceDir.exists()){
                boolean created = recurrenceDir.mkdir();
                if(created){
                    fc.setCurrentDirectory(recurrenceDir);
                }else{
                    fc.setCurrentDirectory(new File(userdir));
                }
            }else{
                fc.setCurrentDirectory(recurrenceDir);
            }
        }
    }
    
    /**
    * Sets the filter scenario files default directory for JFileChoosers.
    * 
    * @param fc the JFileChooser being processed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setScenarioDefaultDir(JFileChooser fc){
        String userdir = null;
        if(PREFERENCES != null){
            userdir = PREFERENCES.getUserdir();
        }
        if(userdir != null){
            File scenarioDir = new File(userdir + File.separator + "scenario_files");
            if(!scenarioDir.exists()){
                boolean created = scenarioDir.mkdir();
                if(created){
                    fc.setCurrentDirectory(scenarioDir);
                }else{
                    fc.setCurrentDirectory(new File(userdir));
                }
            }else{
                fc.setCurrentDirectory(scenarioDir);
            }
        }
    }
    
    /**
    * Sets the white/black list files default directory for JFileChoosers.
    * 
    * @param fc the JFileChooser being processed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setListDefaultDir(JFileChooser fc){
        String userdir = null;
        if(PREFERENCES != null){
            userdir = PREFERENCES.getUserdir();
        }
        if(userdir != null){
            File listDir = new File(userdir + File.separator + "list_files");
            if(!listDir.exists()){
                boolean created = listDir.mkdir();
                if(created){
                    fc.setCurrentDirectory(listDir);
                }else{
                    fc.setCurrentDirectory(new File(userdir));
                }
            }else{
                fc.setCurrentDirectory(listDir);
            }
        }
    }
    
    /**
    * Sets VCF files default directory for JFileChoosers.
    * 
    * @param fc the JFileChooser being processed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setVCFDefaultDir(JFileChooser fc){
        File defaultDir = PREFERENCES.getDefaultDir();
        if(defaultDir != null && defaultDir.exists()){
            fc.setCurrentDirectory(defaultDir);
            return;
        }
        String userdir = null;
        if(PREFERENCES != null){
            userdir = PREFERENCES.getUserdir();
        }
        if(userdir != null){
            File dataDir = new File(userdir + File.separator + "VCFData");
            if(!dataDir.exists()){
                boolean created = dataDir.mkdir();
                if(created){
                    fc.setCurrentDirectory(dataDir);
                }else{
                    fc.setCurrentDirectory(new File(userdir));
                }
            }else{
                fc.setCurrentDirectory(dataDir);
            }
        }
    }
    
    /**
    * Sets the preferred size of JFileChoosers.
    * 
    * @param fc the JFileChooser being processed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setFileChooserDimension(JFileChooser fc){
        fc.setPreferredSize(new Dimension(fileChooserHorizontalSize, fileChooserVerticalSize));
    }
    
    /**
    * Returns a new instance of VCFFilter().
    * 
    * @return VCFFilter
    * @author Heiko Müller
    * @since 1.0
    */ 
    public static VCFFilter getInstance(){
        return new VCFFilter();
    }
    
    /**
    * Removes a filter from the VCFFilter GUI in the Filter tab.
    * 
    * @param index - The index of the filter to be removed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    public void removeFilter(int index){
        int selected = index;        
        ArrayList<String> remaining = new ArrayList<String>();
        ArrayList<Filter> remainingFilters = new ArrayList<Filter>();
        for (int i = 0; i < jComboBox2.getItemCount(); i++) {
            if (i != selected) {
                remaining.add((String) jComboBox2.getItemAt(i));
                remainingFilters.add(filters.get(i));
            }
        }        
        jPanel1.removeAll();
        filters = remainingFilters;
        int idx = 0;
        for (int i = 0; i < remaining.size() && i < 10; i++) {
            Filter f = filters.get(i);
            f.setIndex(idx);
            f.setGui(this);
            addToMyLayout(jPanel1, f, 10, i * filterVerticalGap + 10, 0, 0);
            idx++;
        }
        initRemoveFilters();
    }
    
    /**
    * Removes a filter from the VCFFilter GUI in the FamilyAnalysis tab.
    * 
    * @param index - The index of the filter to be removed.
    * @author Heiko Müller
    * @since 1.0
    */ 
    public void removeFamilyFilter(int index){
        int selected = index;
        String selectedHeader = (String) jComboBox4.getItemAt(selected);
        ArrayList<String> remaining = new ArrayList<String>();
        ArrayList<Filter> remainingFilters = new ArrayList<Filter>();
        for (int i = 0; i < jComboBox4.getItemCount(); i++) {
            if (i != selected) {
                remaining.add((String) jComboBox4.getItemAt(i));
                remainingFilters.add(familyFilters.get(i));
            }
        }        
        jPanel14.removeAll();
        familyFilters = remainingFilters;
        int idx = 0;
        for (int i = 0; i < remaining.size(); i++) {
            Filter f = familyFilters.get(i);
            f.setIndex(idx);
            f.setGui(this);
            addToMyLayout(jPanel14, f, 10, i * filterVerticalGap + 10, 0, 0);
            idx++;
        }
        initRemoveFamilyFilters();
    }
    
    /**
    * Returns the tab that is currently selected (Filter tab, FamilyAnalysis tab, or Search tab).
    * 
    * @return int - The index of the selected tab.
    * @author Heiko Müller
    * @since 1.0
    */ 
    public int getSelectedTabIndex(){
        return jTabbedPane1.getSelectedIndex();
    }
    
    /**
    * Sets the color of the Filter tab Run button.
    * 
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setFilterRunButtonState(){         
        setActiveFilter();   
        boolean indexTestPassed = true;        
        indexTestPassed = setActiveFileList();    
         
        if(jRadioButton1.isSelected() || jRadioButton3.isSelected() || jRadioButton4.isSelected()){
            if (activeVCFFiles != null && activeVCFFiles.length > 0 && indexTestPassed) {
                jButton1.setBackground(Color.green);
            }else{
                jButton1.setBackground(Color.red);
            }
        }else if(jRadioButton2.isSelected()){        
            if (activeVCFFiles != null && activeVCFFiles.length == 1 && indexTestPassed) {
                jButton1.setBackground(Color.green);
            }else{
                jButton1.setBackground(Color.red);
            }
        }
        
        if(jRadioButton3.isSelected() && (PREFERENCES.getGenesymbolField() == null || PREFERENCES.getGenesymbolField().equals("CHROM"))){
            jButton1.setBackground(Color.red);
            new Warning(this, "Please choose a valid gene symbol annotation field in File -> Preferences -> Annotations.");
        }
        
        if(filters != null){
            for(Filter f : filters){
                f.setIDLabelState();
            }
        }
    }
    
    /**
    * Sets the color of the FamilyAnalysis tab Run button.
    * 
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setFamilyRunButtonState(){    
        if(familyFilters != null){
            for(Filter f : familyFilters){
                f.setIDLabelState();
            }
        }
        
        if(!setActiveAffectedFileList()) {
            //System.out.println("setActiveAffectedFileList");
            jButton12.setBackground(Color.red);
            return;
        }         
        if(!setActiveUnaffectedFileList()) {
            //System.out.println("setActiveUnaffectedFileList");
            jButton12.setBackground(Color.red);
            return;
        }        
        setActiveFamilyFilter();
        //if(activeFamilyFilters == null || activeFamilyFilters.size() == 0) {
        //    jButton12.setBackground(Color.red);
        //    return;
        //}        
        if(activeAffectedVCFFiles == null || activeAffectedVCFFiles.length == 0) {
            //System.out.println("affected undefined");
            jButton12.setBackground(Color.red);
            return;
        }        
        if(activeUnaffectedVCFFiles == null || activeUnaffectedVCFFiles.length == 0) {
            //System.out.println("unaffected undefined");
            jButton12.setBackground(Color.red);
            return;
        }     
        if(!testSelectionConsistency()) {
            jButton12.setBackground(Color.red);
            return;
        }  
        
        if(relationships == null || relationships.getRelationships() == null || relationships.getRelationships().size() == 0){
            jButton12.setBackground(Color.red);
            return;
        }
        jButton12.setBackground(Color.green);
    }
    
    /**
    * Sets the color of the Search tab Run button.
    * 
    * @author Heiko Müller
    * @since 1.0
    */ 
    private void setSearchRunButtonState(){     
        setActiveSearchFileList();
        //boolean indexTestPassed = setActiveSearchFileList();
        //if( activeSearchVCFFiles != null && activeSearchVCFFiles.length > 0 && indexTestPassed){ //creates GUI deadlock
        if( activeSearchVCFFiles != null && activeSearchVCFFiles.length > 0){ 
            if (!jTextArea4.getText().equals("") || !jTextArea5.getText().equals("")) {
                jButton7.setBackground(Color.green);
            }else{
                jButton7.setBackground(Color.red);
            }           
        }else{
            jButton7.setBackground(Color.red);
        }
    }
    
    /**
    * Returns the defined relationships in the FamilyAnalysis tab.
    * 
    * @return Relationships
    * @author Heiko Müller
    * @since 1.0
    */ 
    public Relationships getRelationships(){
        return relationships;
    }
    
    /**
    * Returns the defined Relationship for a given individual in the FamilyAnalysis tab.
    * 
    * @param filename - the name of the affected individuals VCF file
    * @return Relationship
    * @author Heiko Müller
    * @since 1.0
    */ 
    public Relationship getRelationship(String filename){
        if(relationships == null){
            return null;
        }
        for(int i = 0; i < relationships.getRelationships().size(); i++){
            Relationship r = relationships.getRelationships().get(i);
            if(r.getIndividual().equals(filename)){
                return r;
            }
        }
        return null;
    }
    
    /**
    * Triggers jList4 value changed event to update the graphical display of defined relationships.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void updateRelationships(){
        jList4ValueChanged(new javax.swing.event.ListSelectionEvent(jList4, 0,0,true));
    }
    
    /**
    * Sets family controls used to define relationships to enabled/disabled.
    * 
    * @param enabled true or false
    * @author Heiko Müller
    * @since 1.0
    */
    public void setFamilyControls(boolean enabled){
        jLabel2.setEnabled(enabled);
        jLabel3.setEnabled(enabled);
        jComboBox5.setEnabled(enabled);
        jComboBox6.setEnabled(enabled);
        jRadioButton5.setEnabled(enabled);
        jRadioButton6.setEnabled(enabled);
        jButton5.setEnabled(enabled);
    }
    
    public JButton getExampleButton(){
        return jButton16;
    }
    
    public JButton getFamilyExampleButton(){
        return jButton25;
    }
    
    public JButton getSearchExampleButton(){
        return jButton24;
    }
    
    public String getRecurrenceType(){
        return recurrenceType;
    }
    
    private boolean testHeaderLinesConsistency(File vcffile) {
        VCFFileReader vcf = new VCFFileReader(vcffile);
        Iterator<VCFInfoHeaderLine> it = vcf.getFileHeader().getInfoHeaderLines().iterator();
        while(it.hasNext()){
            String id = it.next().getID();
            VCFInfoHeaderLine hl = PREFERENCES.getInfoHeaderLine(id);
            if(hl == null){
                return false;
            }
        }
        Iterator<VCFFormatHeaderLine> it2 = vcf.getFileHeader().getFormatHeaderLines().iterator();
        while(it2.hasNext()){
            String id = it2.next().getID();
            VCFFormatHeaderLine hl = PREFERENCES.getFormatHeaderLine(id);
            if(hl == null){
                return false;
            }
        }
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try
        {
            /*
            final ClassLoader wcl = VCFFilter.class.getClassLoader();           
            Enumeration<URL> en=wcl.getResources("META-INF");                       
            
            URL u2 = wcl.getResource("htsjdk/tribble/TribbleException.class");
            System.out.println(u2.toString());
            
            final URLClassLoader ucl = new URLClassLoader(urls, wcl);
            */

              EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
              eq.invokeAndWait(new Runnable() {
                public void run() {
                  //Thread.currentThread().setContextClassLoader(ucl);
                  new VCFFilter().setVisible(true);
                  
                }
             });
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        
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
            java.util.logging.Logger.getLogger(VCFFilter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VCFFilter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VCFFilter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VCFFilter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(new Runnable() {
        //    public void run() {
         //       new VCFFilter().setVisible(true);
         //   }
        //});
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser10;
    private javax.swing.JFileChooser jFileChooser11;
    private javax.swing.JFileChooser jFileChooser12;
    private javax.swing.JFileChooser jFileChooser13;
    private javax.swing.JFileChooser jFileChooser14;
    private javax.swing.JFileChooser jFileChooser15;
    private javax.swing.JFileChooser jFileChooser16;
    private javax.swing.JFileChooser jFileChooser17;
    private javax.swing.JFileChooser jFileChooser18;
    private javax.swing.JFileChooser jFileChooser19;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JFileChooser jFileChooser20;
    private javax.swing.JFileChooser jFileChooser21;
    private javax.swing.JFileChooser jFileChooser22;
    private javax.swing.JFileChooser jFileChooser23;
    private javax.swing.JFileChooser jFileChooser24;
    private javax.swing.JFileChooser jFileChooser25;
    private javax.swing.JFileChooser jFileChooser26;
    private javax.swing.JFileChooser jFileChooser27;
    private javax.swing.JFileChooser jFileChooser3;
    private javax.swing.JFileChooser jFileChooser4;
    private javax.swing.JFileChooser jFileChooser5;
    private javax.swing.JFileChooser jFileChooser6;
    private javax.swing.JFileChooser jFileChooser7;
    private javax.swing.JFileChooser jFileChooser8;
    private javax.swing.JFileChooser jFileChooser9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JList jList10;
    private javax.swing.JList jList11;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JList jList5;
    private javax.swing.JList jList7;
    private javax.swing.JList jList8;
    private javax.swing.JList jList9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem44;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
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
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu10;
    private javax.swing.JPopupMenu jPopupMenu11;
    private javax.swing.JPopupMenu jPopupMenu12;
    private javax.swing.JPopupMenu jPopupMenu13;
    private javax.swing.JPopupMenu jPopupMenu14;
    private javax.swing.JPopupMenu jPopupMenu15;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JPopupMenu jPopupMenu3;
    private javax.swing.JPopupMenu jPopupMenu4;
    private javax.swing.JPopupMenu jPopupMenu5;
    private javax.swing.JPopupMenu jPopupMenu6;
    private javax.swing.JPopupMenu jPopupMenu7;
    private javax.swing.JPopupMenu jPopupMenu8;
    private javax.swing.JPopupMenu jPopupMenu9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
