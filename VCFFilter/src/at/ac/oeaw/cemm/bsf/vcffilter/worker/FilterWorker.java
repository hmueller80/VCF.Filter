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
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.GenomicElement;
import at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.Query;
import at.ac.oeaw.cemm.bsf.vcffilter.vcftoimage.Hilbert;
import htsjdk.samtools.util.CloseableIterator;
//import htsjdk.tribble.TribbleException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFFileReader;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * Performs filter analysis as a SwingWorker.
 *
 * FilterWorker.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FilterWorker extends VCFFilterWorker {

    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * Array of VCF files currently highlighted in VCFFilter.
     */
    private File[] selectedVCFFiles;

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
     * Creates new FilterWorker.
     *
     * @param gui graphical user interface
     * @author Heiko Müller
     * @since 1.0
     */
    public FilterWorker(VCFFilter gui) {
        super(gui);
        this.selectedVCFFiles = gui.getActiveVCFFiles();
        filters = gui.getActiveFilters();
        this.outputArea = gui.getFilterTextArea();
        this.progressBar = gui.getFilterProgressBar();
        initProgressBar(progressBar, 0, selectedVCFFiles.length);
        recurrenceFile = gui.getFilterRecurrenceFile();
        recurrenceCutoff = gui.getFilterRecurrenceCutoff();
        initRecurrenceHash();
        whiteListFiles = gui.getFilterActiveWhiteListFiles();
        if (whiteListFiles != null && whiteListFiles.size() > 0) {
            qwhite = new Query(whiteListFiles, gui);
        }
        blackListFiles = gui.getFilterActiveBlackListFiles();
        if (blackListFiles != null && blackListFiles.size() > 0) {
            qblack = new Query(blackListFiles, gui);
        }

    }

    /**
     * Used for testing if prints are needed. As doInBackground() is completely
     * silent, for testing it is renamed into doInBackground0() and executed as
     * such. This doInBackground0() is renamed to doInBackground() instead as
     * overriding this method is required and executed as an empty block.
     *
     * @throws Exception any exception
     * @author Heiko Müller
     * @since 1.0
     *///@Override
    protected Void doInBackground0() throws Exception {
        return null;
    }

    /**
     * Called when worker.execute is called. Does the main work.
     *
     * @throws Exception any exception
     * @author Heiko Müller
     * @since 1.0
     *///   
    @Override
    public Void doInBackground() throws Exception {
        gui.getFilterRunButton().setEnabled(false);
        if (gui.getIsVcfOutput()) {
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            gui.setCursor(waitCursor);
            int progressCounter = 0;
            reportSettings(outputArea);
            VariantContext candidate = null;
            doFilteringToFile();
            setProgress(progressBar, 0);
            gui.getFilterRunButton().setEnabled(true);
            gui.getExampleButton().setEnabled(true);
            gui.setCursor(defaultCursor);

        } else {
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            gui.setCursor(waitCursor);
            int progressCounter = 0;
            reportSettings(outputArea);
            ArrayList<VariantContext> candidates = new ArrayList<VariantContext>();
            if (qwhite == null && qblack == null) {
                candidates = doVCFFiltering();
            } else if (qwhite != null && qblack == null) {
                candidates = doVCFQueryFiltering(qwhite.getFlattenedRegions());
            } else if (qwhite == null && qblack != null) {
                candidates = doVCFFiltering();
                candidates = filterOnBlackLists(candidates, qblack);
            } else {
                candidates = doVCFQueryFiltering(qwhite.getFlattenedRegions());
                candidates = filterOnBlackLists(candidates, qblack);
            }
            candidates = filterOnRecurrence(candidates);
            reportAppend(candidates, outputArea);
            //if(gui.getjRadioButton2().isSelected()){
            //    boolean writeok = false;
            //    if(!gui.getVcfOutputFile().exists()){
            //        writeok = gui.getVcfOutputFile().createNewFile();
            //    } 
            //    if(writeok){
            //        saveAsVCF(candidates, filters, selectedVCFFiles[0], gui.getVcfOutputFile());
            //    }
            //}
            setProgress(progressBar, 0);
            gui.getFilterRunButton().setEnabled(true);

            if (gui.showHilbertCurve()) {
                Hilbert.setContigSizes(getContigSizes());
                Hilbert.setX(getXIndex());
                Hilbert.setY(getYIndex());
                Hilbert h = new Hilbert(8, candidates);
                h.show();
            }
            gui.getExampleButton().setEnabled(true);
            gui.setCursor(defaultCursor);
        }
        return null;
    }

    private ArrayList<VariantContext> doVCFQueryFiltering(ArrayList<GenomicElement> regions) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        int progressCounter = 0;
        for (File f : this.selectedVCFFiles) {
            if (gui.getCancelFilterWorker()) {
                setProgress(progressBar, 0);
                gui.getFilterRunButton().setEnabled(true);
                return result;
            }
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);
            VariantContext x = vcf.iterator().next();
            boolean chr = false;
            if (x != null && x.getContig().toUpperCase().startsWith("CHR")) {
                chr = true;
            }
            for (GenomicElement g : regions) {
                ArrayList<VariantContext> temp = null;
                try {
                    if (!chr) {
                        CloseableIterator<VariantContext> it = vcf.query(g.CHR, g.START, g.END);
                        temp = filterVCFFileIterator(it);
                    } else {
                        CloseableIterator<VariantContext> it = vcf.query("chr" + g.CHR, g.START, g.END);
                        temp = filterVCFFileIterator(it);
                    }
                    //}catch(TribbleException te){    
                } catch (Exception te) {
                    te.printStackTrace();
                    new Warning(gui, "Problem at position " + g.CHR + ":" + g.START + " " + te.getMessage());
                    return new ArrayList<VariantContext>();
                }
                if (result.size() + temp.size() < outputlimit) {
                    result.addAll(temp);
                } else {
                    new Warning(gui, "More than " + outputlimit + " (outputlimit) variants found. Output will be incomplete. To increase the output limit go to File -> Preferences -> Output limit or use more stringent filters.");
                    result.addAll(temp);
                    return result;
                }
            }
        }
        return result;
    }

    private ArrayList<VariantContext> doVCFFiltering() {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        int progressCounter = 0;
        for (File f : this.selectedVCFFiles) {
            if (gui.getCancelFilterWorker()) {
                setProgress(progressBar, 0);
                gui.getFilterRunButton().setEnabled(true);
                return result;
            }
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);
            ArrayList<VariantContext> temp = null;
            try {
                CloseableIterator<VariantContext> it = vcf.iterator();
                temp = filterVCFFileIterator(it);
                //}catch(TribbleException te){
            } catch (Exception te) {
                te.printStackTrace();
                new Warning(gui, "Problem iterating over variants " + te.getMessage());
                return new ArrayList<VariantContext>();
            }
            if (result.size() + temp.size() < outputlimit) {
                result.addAll(temp);
            } else {
                new Warning(gui, "More than " + outputlimit + " (outputlimit) variants found. Output will be incomplete.");
                result.addAll(temp);
                return result;
            }
        }
        return result;
    }

    private int[] getContigSizes() {
        VCFFileReader vcf = new VCFFileReader(selectedVCFFiles[0]);
        List<VCFContigHeaderLine> l = vcf.getFileHeader().getContigLines();
        int[] result = new int[l.size()];
        for (VCFContigHeaderLine h : l) {
            result[h.getContigIndex()] = h.getSAMSequenceRecord().getSequenceLength();
        }
        return result;
    }

    private int getXIndex() {
        VCFFileReader vcf = new VCFFileReader(selectedVCFFiles[0]);
        List<VCFContigHeaderLine> l = vcf.getFileHeader().getContigLines();
        for (VCFContigHeaderLine h : l) {
            if (h.getID().equals("X")) {
                return (h.getContigIndex());
            }
        }
        return -1;
    }

    private int getYIndex() {
        VCFFileReader vcf = new VCFFileReader(selectedVCFFiles[0]);
        List<VCFContigHeaderLine> l = vcf.getFileHeader().getContigLines();
        for (VCFContigHeaderLine h : l) {
            if (h.getID().equals("Y")) {
                return (h.getContigIndex());
            }
        }
        return -1;
    }

    private boolean isSingleSampleVCF(File f) {
        VCFFileReader vcf = new VCFFileReader(f);
        VariantContext vc = vcf.iterator().next();
        if (vc != null) {
            Set<String> s = vc.getSampleNames();
            if (s != null && s.size() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the cancel switch
     *
     * @param cancel true or false
     */
    public void setCancel(boolean cancel) {
        super.setCancel(cancel);
    }

    private void doFilteringToFile() {
        VariantContextWriter out = getVariantContextWriter(filters, this.selectedVCFFiles[0], gui.getVcfOutputFile());
        VariantContext currentVariant = null;
        int progressCounter = 0;
        reportOutputHeader(outputArea);
        int variantCount = 0;
        for (File f : this.selectedVCFFiles) {
            progressCounter++;
            setProgress(progressBar, progressCounter);
            VCFFileReader vcf = new VCFFileReader(f);
            try {
                CloseableIterator<VariantContext> it = vcf.iterator();
                while (it.hasNext() && !cancel) {
                    currentVariant = it.next();                    
                    if (currentVariantPassesFilterChain(currentVariant) && filterOnRecurrence(currentVariant) && currentVariantPassesListFilters(currentVariant)) {
                        variantCount++;
                        if(variantCount < outputlimit){
                            reportAppend(currentVariant, outputArea);
                        }else if(variantCount == outputlimit){
                            reportAppend(currentVariant, outputArea);
                            new Warning(gui, "Output limit for text output exceeded. Consult output VCF file for remaining variants." );
                        }
                        out.add(currentVariant);
                    }
                }
            } catch (Exception te) {
                out.close();
                te.printStackTrace();
                new Warning(gui, "Problem iterating over variants " + te.getMessage());
                return;
                //return new ArrayList<VariantContext>();
            }
        }
        out.close();
    }

    private boolean currentVariantPassesListFilters(VariantContext vc) {
        if (qwhite == null && qblack == null) {
            return true;
        } else if (qwhite != null && qblack == null) {
            GenomicElement ge = new GenomicElement(vc);
            return qwhite.match(ge);
        } else if (qwhite == null && qblack != null) {
            GenomicElement ge = new GenomicElement(vc);
            return !qblack.match(ge);
        } else {
            GenomicElement ge = new GenomicElement(vc);
            boolean w = qwhite.match(ge);
            boolean b = !qblack.match(ge);
            if (w && b) {
                return true;
            }
        }
        return false;
    }

    private boolean currentVariantPassesFilterChain(VariantContext vc) {
        if (filters == null || filters.size() == 0) {
            return true;
        }
        try {
            boolean pass = false;
            for (Filter filter : filters) {
                pass = filter.passesFilter(vc);
                if (filter.getAndNot()) {
                    pass = !pass;
                }
                if (!pass) {
                    return false;
                }
            }
            if (pass) {
                return true;
            }
        } catch (Exception e) {
            new Warning(gui, "filterVCFFileIterator had a problem " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
