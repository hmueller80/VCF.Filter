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
import at.ac.oeaw.cemm.bsf.vcffilter.excelparser.VariantRecurrence;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Performs recurrence counting as a SwingWorker.
 * 
 * RecurrenceCalculationWorker.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class RecurrenceCalculationWorker extends SwingWorker {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * The filters with valid search criteria.
     */
    private ArrayList<Filter> activeFilters;
    
    /**
     * Reference to the output area.
     */
    private JTextArea jTextArea1;
    
    /**
     * Reference to the output JProgressBar.
     */
    private JProgressBar jProgressBar1;
    
    /**
     * Array of VCF files currently highlighted in VCFFilter.
     */
    private File[] selectedVCFFiles;
    
    /**
     * Reference to main application VCFFilter.
     */
    private VCFFilter gui;
    //List outputfields;   
    
    /**
     * Gene symbol annotation field.
     */
    private String geneSymbolField;
    
    
    /**
     * The recurrence hash.
     */
    private Hashtable<String, VariantRecurrence> recurrenceHash;

     /**
    * Creates new RecurrenceCalculationWorker.
    * 
    * @param selectedVCFFiles array of highlighted VCF files
    * @param activeFilters filter chain
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public RecurrenceCalculationWorker(File[] selectedVCFFiles, ArrayList<Filter> activeFilters, VCFFilter gui) {
        this.selectedVCFFiles = selectedVCFFiles;
        this.activeFilters = activeFilters;
        this.gui = gui;     
        geneSymbolField = gui.getPreferences().getGenesymbolField();
    }
    
    /**
    * Used for testing if prints are needed. As doInBackground() is completely silent, for testing it is renamed into
    * doInBackground0() and executed as such. This doInBackground0() is renamed to doInBackground() instead
    * as overriding this method is required and executed as an empty block.
    * 
    * @throws Exception any exception
    * @author Heiko Müller
    * @since 1.0
    *///    @Override
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
        gui.getFilterTextArea().setText("Calculating variant recurrence. This may take a while. Please be patient.\r\n");
        recurrenceHash = new Hashtable<String, VariantRecurrence>();
        int counter = 0;
        gui.initFilterProgressBar(0, selectedVCFFiles.length);

        for (File f : this.selectedVCFFiles) {
            if(gui.getCancelFilterWorker()){                    
                    gui.setFilterProgress(0);
                    return null;
            }
            counter++;
            gui.setFilterProgress(counter);
            //System.out.println("opening file " + f.getAbsolutePath());
            VCFFileReader vcf = new VCFFileReader(f);
            CloseableIterator<VariantContext> it = vcf.iterator();  
            filterVCFFile(it);
        }
        dump(recurrenceHash);
        gui.setFilterProgress(0);
        gui.getExampleButton().setEnabled(true);
        return null;

    }

    private void filterVCFFile(CloseableIterator<VariantContext> it) {
        while (it.hasNext()) {
            boolean pass = false;
            VariantContext vc = it.next();
            //System.out.println(vc.toString());
            if(activeFilters.size() == 0){
                pass = true;
            }else{
                for (Filter filter : activeFilters) {
                    pass = filter.passesFilter(vc);
                    if (filter.getAndNot()) {
                        pass = !pass;
                    }
                    if (!pass) {
                        break;
                    }
                }
            }
            if (pass) {
                String chr = vc.getContig();
                int pos = vc.getStart();
                List<Allele> la = vc.getGenotype(0).getAlleles();
                la = removeRedundantAlleles(la);
                String ref = vc.getReference().getBaseString();
                String gene = "";
                try{
                    gene = vc.getAttribute(geneSymbolField).toString();
                }catch(Exception e){
                    gene = "not annotated";
                    //new Warning(gui, "Having problems reading the gene symbol field:\r\n " + e.getMessage() + " \r\nPlease define the gene symbol annotation field in Preferences -> Annotations.");
                    //return;
                }
                String sample = vc.getSampleNames().iterator().next();
                Genotype gt = vc.getGenotype(0);
                for(Allele a : la){
                    String alt = a.getBaseString();
                    if(!alt.equals(ref)){
                        if(vc.getGenotype(0).isHomVar()){
                            String key = chr + "-" + pos + "-" + ref + "-" + alt;
                            if(!recurrenceHash.containsKey(key)){
                                VariantRecurrence vcr = new VariantRecurrence();
                                vcr.setChr(chr);
                                vcr.setPos(pos + "");
                                vcr.setRef(ref);
                                vcr.setAlt(alt);
                                vcr.setGene(gene);                                
                                ArrayList<String> l = new ArrayList<String>();
                                l.add(sample);                            
                                vcr.setSamplesHom(l);
                                ArrayList<String> l2 = new ArrayList<String>();                                                         
                                vcr.setSamplesHet(l2);
                                recurrenceHash.put(key, vcr);
                            }else{
                                recurrenceHash.get(key).getSamplesHom().add(sample);
                            }
                        }else{
                            String key = chr + "-" + pos + "-" + ref + "-" + alt;
                            if(!recurrenceHash.containsKey(key)){
                                VariantRecurrence vcr = new VariantRecurrence();
                                vcr.setChr(chr);
                                vcr.setPos(pos + "");
                                vcr.setRef(ref);
                                vcr.setAlt(alt);
                                vcr.setFreqHom("1");
                                vcr.setFreq("1");
                                vcr.setGene(gene);                            
                                ArrayList<String> l = new ArrayList<String>();                                                       
                                vcr.setSamplesHom(l);
                                ArrayList<String> l2 = new ArrayList<String>();
                                l2.add(sample);     
                                vcr.setSamplesHet(l2);
                                recurrenceHash.put(key, vcr);
                            }else{
                                recurrenceHash.get(key).getSamplesHet().add(sample);
                            }
                        }
                    }
                }
            }
        }

    }
    
    private List<Allele> removeRedundantAlleles(List<Allele> al){
        ArrayList<Allele> result = new ArrayList<Allele>();
        Set<String> s = new HashSet<String>();
        for(Allele a : al){
            if(!s.contains(a.getBaseString())){
                result.add(a);
                s.add(a.getBaseString());
            }
        }
        return result;
    }
    
    private void dump(Hashtable<String, VariantRecurrence> hash){
        Iterator it = hash.keySet().iterator();
        JTextArea t = gui.getFilterTextArea();
        t.append(VariantRecurrence.getHeader() + "\r\n");
        while(it.hasNext()){
            VariantRecurrence vcr = hash.get(it.next());
            //t.append(vcr.getChr() + "\t" + vcr.getPos() + "\t" + vcr.getRef() + "\t" + vcr.getAlt() + "\t" + vcr.getGene() + "\t" + vcr.getSamplesHet().size() + "\t" + concatenate(vcr.getSamplesHet()) + "\t" + vcr.getSamplesHom().size() + "\t" + concatenate(vcr.getSamplesHom()) + "\r\n");
            t.append(vcr.toString() + "\r\n");
        }
    }
    
    private String concatenate(ArrayList<String> l){
        StringBuilder sb = new StringBuilder();
        for(String s : l){
            sb.append(s + ";");
        }
        String result = sb.toString();
        if(result.length() >= 1){
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
