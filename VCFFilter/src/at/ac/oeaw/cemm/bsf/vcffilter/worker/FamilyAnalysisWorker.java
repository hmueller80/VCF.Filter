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
package at.ac.oeaw.cemm.bsf.vcffilter.worker;

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.GenomicElement;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.Query;
import at.ac.oeaw.cemm.bsf.vcffilter.inheritance.Inheritance;
import at.ac.oeaw.cemm.bsf.vcffilter.inheritance.Relationship;
import at.ac.oeaw.cemm.bsf.vcffilter.inheritance.Relationships;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * Performs family analysis as a SwingWorker.
 * 
 * FamilyAnalysisWorker.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FamilyAnalysisWorker extends VCFFilterWorker{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * Array of VCF files of affected individuals currently highlighted in VCFFilter.
     */
    private File[] affectedVCFFiles;
    
    /**
     * Array of VCF files of unaffected individuals currently highlighted in VCFFilter.
     */
    private File[] unaffectedVCFFiles;
    
    /**
     * Sample names of affected individuals.
     */
    private ArrayList<String> affectedSampleNames;
    
    /**
     * Sample names of unaffected individuals.
     */
    private ArrayList<String> unAffectedSampleNames;    
    
    /**
     * Reference to the output JTextArea.
     */
    private JTextArea outputArea;
    
    /**
     * Reference to the output JProgressBar.
     */
    private JProgressBar progressBar; 
    
    /**
     * Query object for white lists.
     */
    private Query qwhite;
    
    /**
     * Query object for black lists.
     */
    private Query qblack;
    
    /**
     * List of hashes of variants for affected individuals.
     */
    private ArrayList<Hashtable<String, VariantContext>> affectedVariants;
    
    /**
     * List of hashes of variants for unaffected individuals.
     */
    private ArrayList<Hashtable<String, VariantContext>> unaffectedVariants;
    
    /**
     * progress counter.
     */
    private int progressCounter = 0;
    
    /**
     * Index of mother in file array of unaffected individuals (unaffectedVCFFiles).
     */
    //private int motherIndex = -1;
    
    /**
     * Index of father in file array of unaffected individuals (unaffectedVCFFiles).
     */
    //private int fatherIndex = -1;
    
    /**
     * Index of male children in file array of affected individuals (affectedVCFFiles).
     */
    private ArrayList<Integer> maleIndex;   
    
    /**
     * ID of the VCFInfoHeaderLine holding the gene symbol annotation.
     */
    private String genesymbolField;
    
    /**
     * Family relationships.
     */
    private Relationships relationships;
    
    /**
    * Creates new FamilyAnalysisWorker.
    * 
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public FamilyAnalysisWorker(VCFFilter gui){
        super(gui);
        this.affectedVCFFiles = gui.getActiveAffectedVCFFiles();
        this.unaffectedVCFFiles = gui.getActiveUnaffectedVCFFiles();
        filters = gui.getActiveFamilyFilters();
        this.outputArea = gui.getFamilyAnalysisTextArea();
        this.progressBar = gui.getFamilyAnalysisProgressBar();
        this.genesymbolField = gui.getPreferences().getGenesymbolField();
        initProgressBar(progressBar, 0, affectedVCFFiles.length + unaffectedVCFFiles.length + 1);
        
        recurrenceFile = gui.getFamilyRecurrenceFile();
        recurrenceCutoff = gui.getFamilyRecurrenceCutoff();
        initRecurrenceHash();
        
        whiteListFiles = gui.getFamilyActiveWhiteListFiles();
        if(whiteListFiles != null && whiteListFiles.size() > 0){
            qwhite = new Query(whiteListFiles, gui);
        }
        
        blackListFiles = gui.getFilterActiveBlackListFiles();
        if(blackListFiles != null && blackListFiles.size() > 0){
            qblack = new Query(blackListFiles, gui);
        }
        
        relationships = gui.getRelationships();
        
        /*
        String motherSelectedFile = (String) gui.getMother().getSelectedItem();            
        if (!motherSelectedFile.equals("None")) {
            motherIndex = getSampleNameIndex(motherSelectedFile, unaffectedVCFFiles);
        }    
        
        String fatherSelectedFile = (String) gui.getFather().getSelectedItem();    
        if (!fatherSelectedFile.equals("None")) {
            fatherIndex = getSampleNameIndex(fatherSelectedFile, unaffectedVCFFiles);
        }
        */
        
        /*
        List<String> males = gui.getMales();
        maleIndex = new ArrayList<Integer>();
        for (int i = 0; i < males.size(); i++) {
            if (males.size() > 0 && !((String) males.get(i)).equals("None")) {                
                //String sampleName = affectedSampleNames.get(sampleNameIndex);
                maleIndex.add(getSampleNameIndex((String) males.get(i), affectedVCFFiles));
            }
        }
        */
        
        maleIndex = new ArrayList<Integer>();
        for(int i = 0; i < affectedVCFFiles.length; i++){
            Relationship r = relationships.getRelationshipForIndividual(affectedVCFFiles[i].getName());
            if(r != null){
                if(r.isMale()){
                    maleIndex.add(i);
                }
            }
        }
        //System.out.println("male index list " + maleIndex.size());
    }
    
    /**
    * Used for testing if prints are needed. As doInBackground() is completely silent, for testing it is renamed into
    * doInBackground0() and executed as such. This doInBackground0() is renamed to doInBackground() instead
    * as overriding this method is required and executed as an empty block.
    * 
    * @throws Exception might throw Exception
    * @author Heiko Müller
    * @since 1.0
    *///            @Override    
    public Void doInBackground0() throws Exception {
        return null;
    }
    
    /**
    * Called when worker.execute is called. Does the main work.
    * 
    * @throws Exception any exception
    * @author Heiko Müller
    * @since 1.0
    */    // 
    @Override
    public Void doInBackground() throws Exception {
        gui.getFamilyAnalysisRunButton().setEnabled(false);
        progressCounter = 0;
        Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
        Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        gui.setCursor(waitCursor);
        
        boolean allVCFAreSingleSampleFiles = readSampleNames();        
        if (!allVCFAreSingleSampleFiles) {
            outputArea.append("Family analysis requires single sample VCF files\r\n");
            gui.getFamilyAnalysisRunButton().setEnabled(true);
            return null;
        }
        reportSettings(outputArea);    
        ArrayList<VariantContext> candidates = new ArrayList<VariantContext>();
        if(qwhite == null){
            affectedVariants = doVCFFiltering(affectedVCFFiles);  
            unaffectedVariants = doVCFFiltering(unaffectedVCFFiles);  
        }else if(qwhite != null){
            affectedVariants = doVCFQueryFiltering(affectedVCFFiles, qwhite.getFlattenedRegions());  
            unaffectedVariants = doVCFQueryFiltering(unaffectedVCFFiles, qwhite.getFlattenedRegions());         
        }
        
        //set variant hashes
        for (int i = 0; i < affectedVCFFiles.length; i++) {           
            String sampleName = affectedVCFFiles[i].getName();            
            Hashtable<String, VariantContext> motherhash = null;
            Hashtable<String, VariantContext> fatherhash = null;
            Relationship r = relationships.getRelationshipForIndividual(sampleName);
            if(r != null){
                motherhash = getVariantHash(r.getMother());
                fatherhash = getVariantHash(r.getFather());
            }
            if(r != null && motherhash != null){
                r.setMotherHash(motherhash);
            }
            if(r != null && fatherhash != null){
                r.setFatherHash(fatherhash);
            }           
        }
        
        int startsample = 0;
        for (int i = 0; i < relationships.getRelationships().size(); i++) { 
            Relationship r = relationships.getRelationships().get(i);
            if(r.getMotherHash() != null || r.getFatherHash() != null){
                startsample = i;
            }
        }
        for (int i = 0; i < relationships.getRelationships().size(); i++) { 
            Relationship r = relationships.getRelationships().get(i);
            if(r.getMotherHash() != null && r.getFatherHash() != null){
                startsample = i;
            }
        }
        
        //outputArea.append("\r\nDe novo variants ");  
        //ArrayList<VariantContext> dcandidates = Inheritance.dominant(Inheritance.hashToList(0, affectedVariants), affectedVariants, unaffectedVariants, motherIndex, fatherIndex);
        //ArrayList<VariantContext> dcandidates = Inheritance.dominant(Inheritance.hashToList(0, affectedVariants), affectedVariants, unaffectedVariants, -1, -1);
        //ArrayList<VariantContext> dcandidates = Inheritance.dominantDeNovo(affectedVariants, unaffectedVariants);
        //reportAppend(dcandidates, affectedVariants, unaffectedVariants, outputArea);           
        
        
        
        //testing genotype consistency
        /*
        for(int i = 0; i < affectedVCFFiles.length; i++){
            ArrayList<VariantContext> variants = Inheritance.hashToList(i, affectedVariants);
            Relationship r = relationships.getRelationshipForIndividual(affectedVCFFiles[i].getName());
            Hashtable<String, VariantContext> motherhash = getVariantHash(r.getMother());
            Hashtable<String, VariantContext> fatherhash = getVariantHash(r.getFather());
            if(motherhash != null && fatherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, motherhash, fatherhash)){
                        affectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }else if(motherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, motherhash)){
                        affectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }else if(fatherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, fatherhash)){
                        affectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }
        
        }
        
        for(int i = 0; i < unaffectedVCFFiles.length; i++){
            ArrayList<VariantContext> variants = Inheritance.hashToList(i, unaffectedVariants);
            Relationship r = relationships.getRelationshipForIndividual(unaffectedVCFFiles[i].getName());
            Hashtable<String, VariantContext> motherhash = null;
            Hashtable<String, VariantContext> fatherhash = null;
            if(r != null){
                motherhash = getVariantHash(r.getMother());
                fatherhash = getVariantHash(r.getFather());
            }
            if(motherhash != null && fatherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, motherhash, fatherhash)){
                        unaffectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }else if(motherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, motherhash)){
                        unaffectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }else if(fatherhash != null){
                for(VariantContext vc : variants){
                    if(!Inheritance.genotypeIsConsistent(vc, fatherhash)){
                        unaffectedVariants.get(i).remove(vc.getContig() + "_" + vc.getStart());
                    }
                }
            }
        
        }  
        //*/
        
        
        progressCounter++;
        
        
        
        outputArea.append("\r\nRecessive variants\t");
        //ArrayList<VariantContext> al = Inheritance.recessive(0, affectedVariants, unaffectedVariants, motherindex, fatherindex);
        ArrayList<VariantContext> al = Inheritance.recessive(startsample, affectedVariants, unaffectedVariants, -1, -1);
        //ArrayList<VariantContext> al = Inheritance.recessive(0, affectedVariants, unaffectedVariants, relationships);
        al = Inheritance.testGenotypeConsistency(al, relationships.getRelationships().get(startsample));
        reportAppend(al, affectedVariants, unaffectedVariants, outputArea);
        
        outputArea.append("\r\nCompound heterozygous variants\t");
        //al = Inheritance.compoundHeterozygous(0, affectedVariants, unaffectedVariants, motherindex, fatherindex, genesymbolField);
        al = Inheritance.compoundHeterozygous(startsample, affectedVariants, unaffectedVariants, -1, -1, genesymbolField);
        //al = Inheritance.compoundHeterozygous(0, affectedVariants, unaffectedVariants, relationships, genesymbolField);
        al = Inheritance.testGenotypeConsistency(al, relationships.getRelationships().get(startsample));
        al = Inheritance.removeCompoundHeterozygotesWithGenotypesIdenticalToUnaffected(al,unaffectedVariants,genesymbolField);
        reportAppend(al, affectedVariants, unaffectedVariants, outputArea);

        for (int i = 0; i < affectedVCFFiles.length; i++) {           
            //String sampleName = affectedSampleNames.get(maleIndex.get(i));
            String sampleName = affectedVCFFiles[i].getName();
            Hashtable<String, VariantContext> sampleHash = affectedVariants.get(i);
            
            Hashtable<String, VariantContext> motherhash = null;
            Hashtable<String, VariantContext> fatherhash = null;
            Relationship r = relationships.getRelationshipForIndividual(sampleName);
            if(r != null){
                motherhash = r.getMotherHash();
                fatherhash = r.getFatherHash();
            }
            ArrayList<VariantContext> xl = Inheritance.hashToList(i, affectedVariants);                    
      
            if(r != null && r.isMale()){
                outputArea.append("\r\nX-linked variants " + "\t" + sampleName + "\r\n");
                xl = Inheritance.xLinked(xl, affectedVariants, unaffectedVariants, motherhash, fatherhash);   
                xl = Inheritance.testGenotypeConsistency(xl, r);
                reportAppend(xl, affectedVariants, unaffectedVariants, outputArea);   
            }   
        }
            
        outputArea.append("\r\nAutosomal dominant variants\t");  
        //ArrayList<VariantContext> dcandidates = Inheritance.dominant(Inheritance.hashToList(0, affectedVariants), affectedVariants, unaffectedVariants, motherIndex, fatherIndex);
        //ArrayList<VariantContext> dcandidates = Inheritance.dominant(Inheritance.hashToList(0, affectedVariants), affectedVariants, unaffectedVariants, -1, -1);
        ArrayList<VariantContext> dcandidates = Inheritance.dominant(startsample, affectedVariants, unaffectedVariants);
        dcandidates = Inheritance.testGenotypeConsistency(dcandidates, relationships.getRelationships().get(startsample));
        reportAppend(dcandidates, affectedVariants, unaffectedVariants, outputArea); 
        
        outputArea.append("\r\nDe novo variants\t"); 
        ArrayList<VariantContext> denovocandidates = new ArrayList<VariantContext>();        
        for(int i = 0; i < affectedVCFFiles.length; i++){
            ArrayList<VariantContext> variants = Inheritance.hashToList(i, affectedVariants);
            Relationship r = relationships.getRelationshipForIndividual(affectedVCFFiles[i].getName());
            //Hashtable<String, VariantContext> motherhash = getVariantHash(r.getMother());
            //Hashtable<String, VariantContext> fatherhash = getVariantHash(r.getFather());
            //ArrayList<VariantContext> denovo = Inheritance.dominantDeNovo(affectedVariants.get(i), affectedVariants, unaffectedVariants, motherhash, fatherhash);
            ArrayList<VariantContext> denovo = Inheritance.denovo(variants, r, unaffectedVariants);
            denovocandidates = addNonredundant(denovo, denovocandidates);          
        }
        reportAppend(denovocandidates, affectedVariants, unaffectedVariants, outputArea); 
        
        setProgress(progressBar, 0);
        gui.getFamilyAnalysisRunButton().setEnabled(true);
        gui.setFamilyControls(true);
        gui.getFamilyExampleButton().setEnabled(true);
        gui.setCursor(defaultCursor);
        return null;
    }
    
    private ArrayList<VariantContext> addNonredundant(ArrayList<VariantContext> source, ArrayList<VariantContext> target){
        if(target != null && target.size() == 0){
            target.addAll(source);
        }else{
            HashSet<String> hs = new HashSet<String>();
            for(VariantContext vc : target){
                hs.add(vc.getContig() + "_" + vc.getStart());
            }
            for(VariantContext vc : source){
                String key = vc.getContig() + "_" + vc.getStart();
                if(!hs.contains(key)){
                    target.add(vc);
                    hs.add(key);
                }
                
            }
        }
        return target;
    }
    
    private Hashtable<String, VariantContext> getVariantHash(String name){
        if(name == null || name.equals("NA")){
            return null;
        }
        for(int i = 0; i < affectedVCFFiles.length; i++){
            if(name.equals(affectedVCFFiles[i].getName())){
                return affectedVariants.get(i);
            }
        }
        for(int i = 0; i < unaffectedVCFFiles.length; i++){
            if(name.equals(unaffectedVCFFiles[i].getName())){
                return unaffectedVariants.get(i);
            }
        }
        return null;
    }
    
    private ArrayList<Hashtable<String, VariantContext>> doVCFQueryFiltering(File[] files, ArrayList<GenomicElement> regions) {
        ArrayList<Hashtable<String, VariantContext>> result = new ArrayList<Hashtable<String, VariantContext>>();
        for (File f : files) {
            ArrayList<VariantContext> sampleVariants = new ArrayList<VariantContext>();
            if (gui.getCancelFilterWorker()) {
                setProgress(progressBar, 0);
                gui.getFamilyAnalysisRunButton().setEnabled(true);
                return result;
            }
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);            
            VariantContext x = vcf.iterator().next();
            boolean chr = false;
            if(x != null && x.getContig().toUpperCase().startsWith("CHR")){
                chr = true;
            }
            for (GenomicElement g : regions) {
                if(!chr){
                    CloseableIterator<VariantContext> it = vcf.query(g.CHR, g.START, g.END);
                    ArrayList<VariantContext> temp = filterVCFFileIterator(it);
                    sampleVariants.addAll(temp);
                }else{
                    CloseableIterator<VariantContext> it = vcf.query("chr" + g.CHR, g.START, g.END);
                    ArrayList<VariantContext> temp = filterVCFFileIterator(it);
                    sampleVariants.addAll(temp);
                }
            }
            if(qblack != null){
                sampleVariants = filterOnBlackLists(sampleVariants, qblack);
            }
            sampleVariants = filterOnRecurrence(sampleVariants);
            result.add(putInHash(sampleVariants));
        }
        return result;
    }

    private ArrayList<Hashtable<String, VariantContext>> doVCFFiltering(File[] files) {
        ArrayList<Hashtable<String, VariantContext>> result = new ArrayList<Hashtable<String, VariantContext>>();
        for (File f : files) {
            ArrayList<VariantContext> sampleVariants = new ArrayList<VariantContext>();
            if (gui.getCancelFilterWorker()) {
                setProgress(progressBar, 0);
                gui.getFamilyAnalysisRunButton().setEnabled(true);
                return result;
            }
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);
            CloseableIterator<VariantContext> it = vcf.iterator();
            ArrayList<VariantContext> temp = filterVCFFileIterator(it);
            sampleVariants.addAll(temp);
            //System.out.println("before recurrence " + sampleVariants.size());
            if(qblack != null){
                sampleVariants = filterOnBlackLists(sampleVariants, qblack);
            }
            sampleVariants = filterOnRecurrence(sampleVariants);
            //System.out.println("after recurrence " + sampleVariants.size());
            result.add(putInHash(sampleVariants));
        }
        return result;
    }
    
    private Hashtable<String, VariantContext> putInHash(ArrayList<VariantContext> candidates){
        Hashtable<String, VariantContext> result = new Hashtable<String, VariantContext>();
        for(VariantContext v : candidates){
            result.put(v.getContig() + "_" + v.getStart(), v);
        }
        return result;
    }
    
    private int getSampleNameIndex(String f, File[] files) {
        for (int i = 0; i < files.length; i++) {
            if (f.equals(files[i].getName())) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean readSampleNames() {
        affectedSampleNames = new ArrayList<String>();
        unAffectedSampleNames = new ArrayList<String>();
        boolean result = true;
        for (File f : affectedVCFFiles) {
            VCFFileReader vcf = new VCFFileReader(f);
            CloseableIterator<VariantContext> it = vcf.iterator();
            VariantContext vc = it.next();
            Set<String> names = vc.getSampleNames();
            if (names.size() == 1) {
                affectedSampleNames.add(names.iterator().next());
            } else {
                result = false;
            }
        }
        for (File f : unaffectedVCFFiles) {
            VCFFileReader vcf = new VCFFileReader(f);
            CloseableIterator<VariantContext> it = vcf.iterator();
            VariantContext vc = it.next();
            Set<String> names = vc.getSampleNames();
            if (names.size() == 1) {
                unAffectedSampleNames.add(names.iterator().next());
            } else {
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Sets the cancel switch.
     *
     * @param cancel true or false
     */
    public void setCancel(boolean cancel){
        super.setCancel(cancel);
    }
}
