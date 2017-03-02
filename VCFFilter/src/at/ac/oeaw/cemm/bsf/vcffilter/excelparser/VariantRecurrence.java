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
package at.ac.oeaw.cemm.bsf.vcffilter.excelparser;

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import java.util.ArrayList;

/**
 * Stores recurrence information of a given variant in a given cohort.
 * 
 * VariantRecurrence.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class VariantRecurrence {  
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1;
    
    /**
     * The input format expected by the constructor.
     */
    public static final String FormatInfo = "Tab separated fields: "
            + "chr; position; ref-allele; "
            + "alt-allele; genesymbol; frequency-heterozygous; frequency-homozygous; "
            + "frequency-total; heterozygous samples ([IBD_110, IBD_232]; "
            + "homozygous samples ([IBD_111, IBD_233]";
    
    /**
     * The variant contig.
     */
    private String chr;
    
    /**
     * The variant start position.
     */
    private String pos;    
    
    /**
     * The variant reference allele.
     */
    private String ref;
    
    /**
     * The variant alternative allele.
     */
    private String alt;
    
    /**
     * The variant gene symbol.
     */
    private String gene;
    
    /**
     * The variant frequency in heterozygous state.
     */
    private String freqHet;
    
    /**
     * The variant frequency in homozygous state.
     */
    private String freqHom;
    
    /**
     * The variant frequency in total (het + hom).
     */
    private String freq;
    
    /**
     * Sample names with variant in heterozygous state;
     */
    private ArrayList<String> samplesHet;  
    
    /**
     * Sample names with variant in homozygous state;
     */
    private ArrayList<String> samplesHom; 
     
    /** Creates a new instance of VariantRecurrence.*/
    public VariantRecurrence(){
    
    }
    
    /** Creates a new instance of VariantRecurrence.
     * 
     * @param line a line read from a recurrence file with tab separated columns.
     * @author Heiko Müller
     * @since 1.0
     */
    public VariantRecurrence(String line){
        if(line == null){
            return;
        }
        String[] la = line.split("\t");
        if(la.length >= 10){
            this.chr = la[0];
            this.pos = la[1];
            this.ref = la[2];
            this.alt = la[3];
            this.gene = la[4];
            this.freqHet = la[5];
            this.freqHom = la[6];
            this.freq = la[7];
            samplesHet = new ArrayList<String>();
            String temp = la[8];
            temp = temp.substring(1, temp.length() - 1);
            String[] s = temp.split(",");
            for(int i = 0; i < s.length; i++){
                samplesHet.add(s[i]);
            }
            samplesHom = new ArrayList<String>();
            temp = la[9];
            temp = temp.substring(1, temp.length() - 1);
            s = temp.split(",");
            for(int i = 0; i < s.length; i++){
                samplesHom.add(s[i]);
            }
        }else{
        
        }
    }
    
    /** Creates a new instance of VariantRecurrence.
     * 
     * @param line a line read from a recurrence file with tab separated columns.
     * @param gui the graphical user interface.
     * @author Heiko Müller
     * @since 1.0
     */
    public VariantRecurrence(String line, VCFFilter gui){
        if(line == null){
            return;
        }
        String[] la = line.split("\t");
        if(la.length >= 10){
            try{
                this.chr = la[0];
                this.pos = la[1];
                this.ref = la[2];
                this.alt = la[3];
                this.gene = la[4];
                this.freqHet = la[5];
                this.freqHom = la[6];
                this.freq = la[7];
                samplesHet = new ArrayList<String>();
                String temp = la[8];
                temp = temp.substring(1, temp.length() - 1);
                String[] s = temp.split(",");
                for(int i = 0; i < s.length; i++){
                    samplesHet.add(s[i]);
                }
                samplesHom = new ArrayList<String>();
                temp = la[9];
                temp = temp.substring(1, temp.length() - 1);
                s = temp.split(",");
                for(int i = 0; i < s.length; i++){
                    samplesHom.add(s[i]);
                }
            }catch(Exception e){
                new Warning(gui, "Problems parsing recurrence file. " + e.getMessage() + " Exiting.");
                System.exit(0);
            }
        }else{
            new Warning(gui, "Too few columns. 10 expected in recurrence file. Exiting.");
            System.exit(0);
        }
    }
    
    /** Getter for chromosome field.
     * 
     * @return String the variant contig
     * @author Heiko Müller
     * @since 1.0
     */
    public String getChr() {
        return chr;
    }
    
    /** Getter for position field.
     * 
     * @return String the variant start position
     * @author Heiko Müller
     * @since 1.0
     */
    public String getPos() {
        return pos;
    }

    /** Getter for the reference allele field.
     * 
     * @return String the reference allele base string
     * @author Heiko Müller
     * @since 1.0
     */
    public String getRef() {
        return ref;
    }

    /** Getter for the alternative allele field.
     * 
     * @return String the alternative allele base string
     * @author Heiko Müller
     * @since 1.0
     */
    public String getAlt() {
        return alt;
    }

    /** Getter for the gene symbol field.
     * 
     * @return String the gene symbol annotation of a variant
     * @author Heiko Müller
     * @since 1.0
     */
    public String getGene() {
        return gene;
    }

    /** Getter for the total frequency field calculated as the number samples where
     * the variant occurs in heterozygous state plus twice the number of samples where the variant 
     * occurs in homozygous state.
     * 
     * @return String the total variant frequency in the cohort
     * @author Heiko Müller
     * @since 1.0
     */
    public String getFreq() {
        return freq;
    }

    /** Getter for the names of samples where the variant occurs in heterozygous state.
     * 
     * @return ArrayList&#60;String&#62; heterozygous samples list
     * @author Heiko Müller
     * @since 1.0
     */
    public ArrayList<String> getSamplesHet() {
        return samplesHet;
    }
    
    /** Getter for the names of samples where the variant occurs in homozygous state.
     * 
     * @return ArrayList&#60;String&#62; homozygous samples list
     * @author Heiko Müller
     * @since 1.0
     */
    public ArrayList<String> getSamplesHom() {
        return samplesHom;
    }
    
    /** Getter for the header names of the recurrence file.
     * 
     * @return String header names
     * @author Heiko Müller
     * @since 1.0
     */
    public static String getHeader(){
        return "CHR" + "\t" + "POSITION" + "\t" + "REF" + "\t" + "ALT" + "\t" + "GENE" + "\t" + "FREQ HET" + "\t" + "FREQ HOM" + "\t" + "FREQ TOTAL" + "\t" + "SAMPLES HET" + "\t" + "SAMPLES HOM"; 
    }
    
    /** Returns the String representation of this VariantRecurrence object.
     * 
     * @return String 
     * @author Heiko Müller
     * @since 1.0
     */
    public String toString(){
        freqHet = "" + samplesHet.size();
        freqHom = "" + 2 * samplesHom.size();        
        freq = "" + (Integer.parseInt(freqHet) + Integer.parseInt(freqHom));        
        return chr + "\t" + pos + "\t" + ref + "\t" + alt + "\t" + gene + "\t" + freqHet + "\t" + freqHom + "\t" + freq + "\t" + samplesHet.toString() + "\t" + samplesHom.toString(); 
    }

    /** Setter for chromosome field.
     * 
     * @param chr the variant contig
     * @author Heiko Müller
     * @since 1.0
     */
    public void setChr(String chr) {
        this.chr = chr;
    }

    /** Setter for position field.
     * 
     * @param pos the variant start position
     * @author Heiko Müller
     * @since 1.0
     */
    public void setPos(String pos) {
        this.pos = pos;
    }

    /** Setter for reference allele base string field.
     * 
     * @param ref the variant's ref allele base string
     * @author Heiko Müller
     * @since 1.0
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /** Setter for alternative allele base string field.
     * 
     * @param alt the variant's alt allele base string
     * @author Heiko Müller
     * @since 1.0
     */
    public void setAlt(String alt) {
        this.alt = alt;
    }

    /** Setter for gene symbol field.
     * 
     * @param gene the variant's gene symbol annotation
     * @author Heiko Müller
     * @since 1.0
     */
    public void setGene(String gene) {
        this.gene = gene;
    }

    /** Setter for the total variant frequency.
     * 
     * @param freq sets total variant allele count
     * @author Heiko Müller
     * @since 1.0
     */
    public void setFreq(String freq) {
        this.freq = freq;
    }

    /** Setter for the list of samples in heterozygous state.
     * 
     * @param samples  sets list of heterozygous samples
     * @author Heiko Müller
     * @since 1.0
     */
    public void setSamplesHet(ArrayList<String> samples) {
        this.samplesHet = samples;
    }
    
    /** Setter for the list of samples in homozygous state.
     * 
     * @param samples  sets list of homozygous samples
     * @author Heiko Müller
     * @since 1.0
     */
    public void setSamplesHom(ArrayList<String> samples) {
        this.samplesHom = samples;
    }

    /** Getter for the frequency of samples in heterozygous state.
     * 
     * @return String number of samples in heterozygous state
     * @author Heiko Müller
     * @since 1.0
     */
    public String getFreqHet() {
        return freqHet;
    }

    /** Setter for the frequency of samples in heterozygous state.
     * 
     * @param freqHet number of samples in heterozygous state
     * @author Heiko Müller
     * @since 1.0
     */
    public void setFreqHet(String freqHet) {
        this.freqHet = freqHet;
    }

    /** Getter for the frequency of samples in homozygous state.
     * 
     * @return String number of samples in homozygous state
     * @author Heiko Müller
     * @since 1.0
     */
    public String getFreqHom() {
        return freqHom;
    }

    /** Setter for the frequency of samples in homozygous state.
     * 
     * @param freqHom number of samples in homozygous state
     * @author Heiko Müller
     * @since 1.0
     */
    public void setFreqHom(String freqHom) {
        this.freqHom = freqHom;
    }
    
    /** Utility method for reporting recurrence values in output.
     * 
     * @return String object representation reported in output
     * @author Heiko Müller
     * @since 1.0
     */
    public String dump(){
        return "[" + freq + ";" + freqHet + ";" + freqHom + "]";
    }
    
    /** Utility method for reporting recurrence values in output.
     * 
     * @return String object representation reported in output (3 columns)
     * @author Heiko Müller
     * @since 1.0
     */
    public String dumpSeparate(){
        return  freq + "\t" + freqHet + "\t" + freqHom;
    }
}
