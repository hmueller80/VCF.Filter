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
import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import at.ac.oeaw.cemm.bsf.vcffilter.excelparser.RecurrenceParser;
import at.ac.oeaw.cemm.bsf.vcffilter.excelparser.VariantRecurrence;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.GenomicElement;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.Query;
import at.ac.oeaw.cemm.bsf.vcffilter.outputformat.FormatOutputFields;
import at.ac.oeaw.cemm.bsf.vcffilter.outputformat.VariantContextComparator;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import picard.PicardException;

/**
 * Super class of FilterWorker, FamilyAnalysisWorker, and SearchWorker. 
 * Unifies output according to user choices.
 * 
 * VCFFilterWorker.java 04 OCT 2016

 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class VCFFilterWorker extends SwingWorker{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * Reference to main application VCFFilter.
     */
    protected VCFFilter gui;    
    
    /**
     * Visible output fields selected by user.
     */
    protected ArrayList<String> outputFields;      
    
    /**
     * Recurrence hash.
     */
    protected Hashtable<String, VariantRecurrence> recurrenceHash;
    
    /**
     * Recurrence file.
     */
    protected File recurrenceFile;
    
    /**
     * Recurrence cutoff.
     */
    protected int recurrenceCutoff = 5;
    
    /**
     * White list files.
     */
    protected ArrayList<File> whiteListFiles;
    
    /**
     * Black list files.
     */
    protected ArrayList<File> blackListFiles;
    
    /**
     * Active filters.
     */
    protected ArrayList<Filter> filters;
    
    /**
     * Cancel.
     */
    private boolean cancel = false;
    
    /**
     * Output limit.
     */
    protected int outputlimit;
    
     /**
    * Initializes VCFFilterWorker.
    * 
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    protected VCFFilterWorker(VCFFilter gui){
        this.gui = gui;  
        outputFields = gui.getPreferences().getOutputColumns();  
        outputlimit = gui.getPreferences().getOutputlimit();
        //this.filters = gui.getActiveFilters();
    }
    
    /**
    * Called when worker.execute is called. Does the main work.
    * 
    * @throws Exception any exception
    * @author Heiko Müller
    * @since 1.0
    */
    @Override
    protected abstract Void doInBackground() throws Exception;
    
    /*
    protected void report(ArrayList<VariantContext> variants, JTextArea output) {    
        variants.sort(new VariantContextComparator());
        FormatOutputFields fof = new FormatOutputFields(outputFields, gui.getPreferences().getHyperlinks());
        fof.setRecurrenceHash(recurrenceHash);
        output.setText("Variants found: " + variants.size() + "\r\n");
        output.append(fof.getOutputHeader() + "\r\n");
        for (VariantContext v : variants) {                                                        
            output.append(fof.formatOutput(v) + "\r\n");            
        }
    }
    */
    
    /**
    * Writes variant data to provided output in the column order that the user specified.
    * 
    * @param variants list of variants
    * @param output text area where output is written
    * @author Heiko Müller
    * @since 1.0
    */
    protected void reportAppend(ArrayList<VariantContext> variants, JTextArea output) {    
        variants.sort(new VariantContextComparator());
        FormatOutputFields fof = new FormatOutputFields(outputFields, gui.getPreferences().getHyperlinks());
        fof.setRecurrenceHash(recurrenceHash);
        output.append("Variants found: " + variants.size() + "\r\n");
        output.append(fof.getOutputHeader() + "\r\n");        
        //for (VariantContext v : variants) {                                                        
        //    output.append(fof.formatOutput(v) + "\r\n");            
        //}
        for(int i = 0; i < variants.size() && i < outputlimit; i++){
            output.append(fof.formatOutput(variants.get(i)) + "\r\n");        
        }
    }
    
    /**
    * Writes variant data to provided output in the column order that the user specified
    * and adds genotypes of all family members.
    * 
    * @param variants list of variants
    * @param affected list of hashes with variants of affected individuals
    * @param unaffected list of hashes with variants of unaffected individuals
    * @param output text area where output is written
    * @author Heiko Müller
    * @since 1.0
    */
    protected void reportAppend(ArrayList<VariantContext> variants, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, JTextArea output) {    
        variants.sort(new VariantContextComparator());
        FormatOutputFields fof = new FormatOutputFields(outputFields, affected, unaffected, gui.getPreferences().getHyperlinks());
        fof.setRecurrenceHash(recurrenceHash);
        output.append("Variants found: " + variants.size() + "\r\n");
        output.append(fof.getOutputHeader() + "\r\n");
        //for (VariantContext v : variants) {                                                        
        //    output.append(fof.formatOutput(v, affected, unaffected) + "\r\n");            
        //}
        for(int i = 0; i < variants.size() && i < outputlimit; i++){
            output.append(fof.formatOutput(variants.get(i), affected, unaffected) + "\r\n");        
        }
    }
    
    /**
    * Reports filter settings, recurrence file, white list files, and black list files 
    * at the beginning of the analysis.
    * 
    * @param output text area where output is written
    * @author Heiko Müller
    * @since 1.0
    */
    protected void reportSettings(JTextArea output) {
        if(filters != null){
            for(Filter f : filters){
                output.append(f.getSettings() + "\r\n");
            }
        }else{
            output.append("Filters: none" + "\r\n");
        }
        if(recurrenceFile != null){
            output.append("Recurrence file: " + recurrenceFile.getAbsolutePath() + "\r\n");
            output.append("Recurrence cutoff: " + recurrenceCutoff + "\r\n");
        }else{
            output.append("Recurrence file: none" + "\r\n");
        }
        if(whiteListFiles != null){
            for(File f : whiteListFiles){
                output.append("White list file: " + f.getAbsolutePath() + "\r\n");
            }
        }else{
            output.append("White list file: none"  + "\r\n");
        }
        if(blackListFiles != null){
            for(File f : blackListFiles){
                output.append("Black list file: " + f.getAbsolutePath() + "\r\n\r\n");
            }   
        }else{
            output.append("Black list file: none"  + "\r\n\r\n");
        }     
    }
    
    /**
    * Returns the alleles of the first sample in ExAc format.
    * 
    * @param vc VariantContext object
    * @return List&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected List<String> getExacVariantFormat(VariantContext vc){
        Genotype g = vc.getGenotype(0);
        List<Allele> al = g.getAlleles();
        ArrayList<String> result = new ArrayList<String>();
        String base1 = al.get(0).getBaseString();
        String base2 = al.get(1).getBaseString();
        String ref = vc.getReference().getBaseString();
        String c = vc.getContig();
        int p = vc.getStart();        
            if(!base1.equals(ref)){
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base1);
            }
            if(!base2.equals(ref) && !base2.equals(base1)){
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base2);
            }
        return result;
    }
    
    /**
    * Inits progressbar.
    * 
    * @param progressBar JProgressBar
    * @param min min
    * @param max max
    * @author Heiko Müller
    * @since 1.0
    */
    protected void initProgressBar(JProgressBar progressBar, int min, int max){
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }
    
    /**
    * Sets progress.
    * 
    * @param progressBar JProgressBar
    * @param n progress meter
    * @author Heiko Müller
    * @since 1.0
    */
    protected void setProgress(JProgressBar progressBar, int n) {
        progressBar.setValue(n);
    }
    
    /**
    * Inits recurrence hash.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    protected void initRecurrenceHash(){
        if(recurrenceFile == null || !recurrenceFile.exists()){
            return;
        }
        recurrenceHash = new Hashtable<String, VariantRecurrence>();        
        RecurrenceParser p = new RecurrenceParser(recurrenceFile.getAbsolutePath(), true, gui);  
        String chr = "";
        String pos = "";
        Integer freq = -1;
        int counter = 0;
        for(int i = 0; i < p.getVariantRecurrence().size(); i++){
            counter++;
            VariantRecurrence r = p.getVariantRecurrence().get(i);
            
            pos = r.getPos(); 
            try{                
                chr = "" + (int)Integer.parseInt(r.getChr());                               
            }catch(NumberFormatException nfe){                  
                chr = r.getChr();  
            } 
            try{                
                pos = "" + (int)Integer.parseInt(r.getPos());                               
            }catch(NumberFormatException nfe){                  
                pos = r.getPos(); 
            } 
            try{                
                freq = (int)Integer.parseInt(r.getFreq());                               
            }catch(NumberFormatException nfe){                  
                freq = -1;
            }                            
            String ref = r.getRef();  
            String alt = r.getAlt();
            String key = chr + "-" + pos + "-" + ref + "-" + alt;            
            if(!recurrenceHash.containsKey(key)){            
                recurrenceHash.put(key, r);
            }
        }      
    }
    
    /**
    * Filters a list of candidates for matching white lists.
    * 
    * @param candidates list of candidates
    * @param q query object for genomic region intersection
    * @return ArrayList&#60;VariantContext&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected ArrayList<VariantContext> filterOnWhiteLists(ArrayList<VariantContext> candidates, Query q) {   
        if(q != null){
            candidates.sort(new VariantContextComparator());
            ArrayList<VariantContext> result = new ArrayList<VariantContext>();                  
            for(VariantContext vc : candidates){                
                if(q.match(new GenomicElement(vc))){                    
                    result.add(vc);
                }
            } 
            return result;
        }else{
            return candidates;
        }       
    }
    
    /**
    * Filters a list of candidates for matching black lists.
    * 
    * @param candidates list of candidates
    * @param q query object for genomic region intersection
    * @return ArrayList&#60;VariantContext&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected ArrayList<VariantContext> filterOnBlackLists(ArrayList<VariantContext> candidates, Query q) {       
        if(q != null){
            candidates.sort(new VariantContextComparator());
            ArrayList<VariantContext> result = new ArrayList<VariantContext>();            
            for(VariantContext vc : candidates){
                if(!q.match(new GenomicElement(vc))){
                    result.add(vc);
                }
            }  
            return result;
        }else{
            return candidates;
        }         
    }
    
    /**
    * Filters a list of candidates for recurrence.
    * 
    * @param candidates list of candidates
    * @return ArrayList&#60;VariantContext&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected ArrayList<VariantContext> filterOnRecurrence(ArrayList<VariantContext> candidates) {
        if(recurrenceHash == null){
            return candidates;
        }
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        if(gui.getRecurrenceType().equals("total")){
            for(VariantContext v : candidates){
                List<String> keys = getExacVariantFormat(v);
                for(String s : keys){
                    VariantRecurrence r = recurrenceHash.get(s);
                    if(r != null){
                        if(Integer.parseInt(r.getFreq()) <= recurrenceCutoff){
                            result.add(v);
                            break;
                        }
                    }else{
                        result.add(v);
                    }
                }
            }
        }else if(gui.getRecurrenceType().equals("het")){
            for(VariantContext v : candidates){
                List<String> keys = getExacVariantFormat(v);
                for(String s : keys){
                    VariantRecurrence r = recurrenceHash.get(s);
                    if(r != null){
                        if(Integer.parseInt(r.getFreqHet()) <= recurrenceCutoff){
                            result.add(v);
                            break;
                        }
                    }else{
                        result.add(v);
                    }
                }
            }
        }else if(gui.getRecurrenceType().equals("hom")){
            for(VariantContext v : candidates){
                List<String> keys = getExacVariantFormat(v);
                for(String s : keys){
                    VariantRecurrence r = recurrenceHash.get(s);
                    if(r != null){
                        if(Integer.parseInt(r.getFreqHom()) <= recurrenceCutoff){
                            result.add(v);
                            break;
                        }
                    }else{
                        result.add(v);
                    }
                }
            }
        }
        return result;
    }
    
    /**
    * Filters variants in the provided Iterator.
    * 
    * @param it variant iterator
    * @return ArrayList&#60;VariantContext&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected ArrayList<VariantContext> filterVCFFileIterator(CloseableIterator<VariantContext> it){     
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        if(filters == null || filters.size() == 0){
            while(it.hasNext() && !cancel && result.size() < outputlimit){                
                VariantContext vc = it.next();
                result.add(vc);    
                                 
            }
            return result;
        }
        try{
            while(it.hasNext() && !cancel && result.size() < outputlimit){            
                boolean pass = false;
                VariantContext vc = it.next();
                for(Filter filter : filters){
                    pass = filter.passesFilter(vc);   
                    if(filter.getAndNot()){
                        pass = !pass;
                    }
                    if(!pass){
                        break;
                    }
                }
                if(pass){                
                    result.add(vc);                
                }    
            }
        }catch(Exception e){
            new Warning(gui, "filterVCFFileIterator had a problem " + e.getMessage());   
            e.printStackTrace();
        }
        return result;
    }
    
    /**
    * Saves variants as a VCF file and adds filter settings to VCF header.
    * 
    * @param variants list of variants
    * @param filters filter chain
    * @param inputVCF input VCF file
    * @param outputVCF output VCF file
    * @author Heiko Müller
    * @since 1.0
    */
    public void saveAsVCF(ArrayList<VariantContext> variants, ArrayList<Filter> filters, File inputVCF, File outputVCF){
        final VCFFileReader in = new VCFFileReader(inputVCF);
        final VCFHeader header = in.getFileHeader(); 
        final VariantContextWriterBuilder variantContextWriterBuilder = new VariantContextWriterBuilder();         
        SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary(); 
        if (sequenceDictionary == null) { 
                throw new PicardException("The input vcf must have a sequence dictionary in order to create indexed vcf or bcfs."); 
            } 
        variantContextWriterBuilder.setReferenceDictionary(sequenceDictionary);   
        variantContextWriterBuilder.setOption(Options.INDEX_ON_THE_FLY);
        final VariantContextWriter out = variantContextWriterBuilder.setOutputFile(outputVCF).build(); 
        variantContextWriterBuilder.setOutputFileType(VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF);
      
        for (final Filter filter : filters) {             
                header.addMetaDataLine(filter.getHeaderLine());              
        } 
        if(recurrenceFile != null){
            VCFHeaderLine headerLine = new VCFHeaderLine("VCFFilter_recurrence", recurrenceFile.getAbsolutePath() + "<=" + recurrenceCutoff);
            header.addMetaDataLine(headerLine);     
        }
        if(whiteListFiles != null){
            for(File f : whiteListFiles){
                VCFHeaderLine headerLine = new VCFHeaderLine("VCFFilter_whiteList", f.getAbsolutePath());
                header.addMetaDataLine(headerLine);
            }
        }
        if(blackListFiles != null){
            for(File f : blackListFiles){
                VCFHeaderLine headerLine = new VCFHeaderLine("VCFFilter_blackList", f.getAbsolutePath());
                header.addMetaDataLine(headerLine);
            }
        } 
        
        out.writeHeader(header);           
        for(VariantContext v : variants){   
            out.add(v);
        } 
        out.close(); 
        in.close();
    }
    
    /**
     * Sets the cancel switch.
     *
     * @param cancel true or false
     */
    protected void setCancel(boolean cancel){
        this.cancel = cancel;
    }
    
}
