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
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.FileTypeReader;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.GenomicElement;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * Performs searching for known variants as a SwingWorker.
 * 
 * SearchWorker.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class SearchWorker extends VCFFilterWorker {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * Array of VCF files currently highlighted in VCFFilter.
     */
    private File[] activeSearchVCFFiles;
    
    /**
     * Reference to the output JTextArea.
     */
    private JTextArea outputArea;
    
    /**
     * Reference to the output JProgressBar.
     */
    private JProgressBar progressBar;
    
    /**
     * Progress counter.
     */
    private int progressCounter = 0;
    
    /**
     * Variants to be searched.
     */
    private ArrayList<Variant> variants;
    
    /**
     * Genomic regions to be searched.
     */
    private ArrayList<GenomicElement> regions;

    /**
    * Creates new SearchWorker.
    * 
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public SearchWorker(VCFFilter gui) {
        super(gui);
        this.activeSearchVCFFiles = gui.getActiveSearchVCFFiles();
        this.outputArea = gui.getSearchTextArea();
        this.progressBar = gui.getSearchProgressBar();
        initProgressBar(progressBar, 0, activeSearchVCFFiles.length);
    }
    
    /**
    * Used for testing if prints are needed. As doInBackground() is completely silent, for testing it is renamed into
    * doInBackground0() and executed as such. This doInBackground0() is renamed to doInBackground() instead
    * as overriding this method is required and executed as an empty block.
    * 
    * @throws Exception any exception
    * @author Heiko Müller
    * @since 1.0
    *///@Override
    public Void doInBackground0() throws Exception {
        return null;
    }

    /**
    * Called when worker.execute is called. Does the main work.
    * 
    * @throws Exception any exception
    * @author Heiko Müller
    * @since 1.0
    */
    @Override
    public Void doInBackground() throws Exception {
        outputArea.setText("Searching...\r\n");
        variants = parseVariantsSearchText();
        regions = parseRegionsSearchText();
        ArrayList<VariantContext> results = new ArrayList<VariantContext>();
        for (File f : activeSearchVCFFiles) {
            if (gui.getCancelSearchWorker()) {
                gui.setFilterProgress(0);
                return null;
            }
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);
            for (Variant v : variants) {
                CloseableIterator<VariantContext> it = vcf.query(v.chr, v.start, v.start);
                while (it.hasNext()) {
                    VariantContext vc = it.next();
                    if(match(v, vc)){
                        results.add(vc);
                    }
                }
            }
            for (GenomicElement r : regions) {
                CloseableIterator<VariantContext> it = vcf.query(r.getChromosomeNumber(), r.getStart(), r.getEnd());
                while (it.hasNext()) {
                    VariantContext vc = it.next();
                    if(match(r, vc)){
                        results.add(vc);
                    }
                }
            }
        }
        reportAppend(results, outputArea);
        setProgress(progressBar, 0);
        gui.getSearchExampleButton().setEnabled(true);
        return null;
    }

    private ArrayList<Variant> parseVariantsSearchText() {
        String text = gui.getVariantsSearchText();
        if(!text.endsWith("\n")){
            text = text + "\n";
        }
        if (text.length() == 0) {
            return new ArrayList<Variant>();
        }
        ArrayList<Variant> result = new ArrayList<Variant>();
        String[] ta = null;
        if (text.indexOf("\r") > -1) {
            ta = text.split("\r");
        } else if (text.indexOf("\n") > -1) {
            ta = text.split("\n");
        }
        if(ta != null){
            for (String s : ta) {
                result.add(new Variant(s));
            }
        }
        return result;
    }
    
    private ArrayList<GenomicElement> parseRegionsSearchText() {
        String text = gui.getRegionsSearchText();
        if(!text.endsWith("\n")){
            text = text + "\n";
        }
        if (text.length() == 0) {
            return new ArrayList<GenomicElement>();
        }
        ArrayList<GenomicElement> result = new ArrayList<GenomicElement>();
        String[] ta = null;
        if (text.indexOf("\r") > -1) {
            ta = text.split("\r");
        } else if (text.indexOf("\n") > -1) {
            ta = text.split("\n");
        }
        if(ta != null){
            for (String s : ta) {
                result.add(new GenomicElement(s, FileTypeReader.getBedEssentialReader(), gui));
            }
        }
        return result;
    }
    
    private boolean match(Variant v, VariantContext vc){
        if(vc == null || v == null){
            return false;
        }
        List<Allele> refs = vc.getAlternateAlleles();
        for(Allele a : refs){
            if(a.getBaseString().equals(v.alt)){
                return true;
            }
        }
        return false;
    }
    
    private boolean match(GenomicElement r, VariantContext vc){
        if(vc == null || r == null){
            return false;
        }        
        return true;
    }

}
