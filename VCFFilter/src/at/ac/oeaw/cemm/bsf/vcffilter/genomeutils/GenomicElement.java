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
package at.ac.oeaw.cemm.bsf.vcffilter.genomeutils;

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import java.io.Serializable;
import java.util.Vector;
import htsjdk.variant.variantcontext.VariantContext;

/** 
 * Utility handling genomic region overlaps.
 * GenomicElement.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class GenomicElement implements Serializable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /** id of this GenomicElement */
    public String ID = null;
    
    /** contig of this GenomicElement */
    public String CHR = null;
    
    /** strand of this GenomicElement */
    public String STRAND = "";
    
    /** start of this GenomicElement */
    public int START = 0;
    
    /** end of this GenomicElement */
    public int END = 0;
    
    /** measure associated with this GenomicElement */
    public double MEASURE = 0;
    
    /** integral of measures over this GenomicElement */
    public double INTEGRAL = 0;
    
    /** max of measures over this GenomicElement */
    public double MAX = 0;
    
    /** position of this GenomicElement */
    public int POSITION = 0;
    
    /** Vector of miscellaneous features for this GenomicElement */
    public Vector misc = null;
    
    /** StringBuilder */
    public StringBuilder sb = null;
    
    /** annotation */
    public String annotation = null;
    
    /** indicator whether this GenomicElement is masked */
    public boolean MASKED = false;
    
    /** hg19 URL */
    private static final String hg19 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Human&db=hg19&position=";
    
    /** hg18 URL */
    private static final String hg18 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Human&db=hg18&position=";
    
    /** mm10 URL */
    private static final String mm10 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Mouse&db=mm10&position=";
    
    /** mm9 URL */
    private static final String mm9 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Mouse&db=mm9&position=";
    
    /** mm8 URL */
    private static final String mm8 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=mammal&org=Mouse&db=mm8&position=";
    
    /** danRer7 URL */private static final String danRer7 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=vertebrate&org=Zebrafish&db=danRer7&position=";
    private static final String sacCer3 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=other&org=S.+cerevisiae&db=sacCer3&position=";
    
    /** sacCer2 URL */
    private static final String sacCer2 = "http://genome.ucsc.edu/cgi-bin/hgTracks?hgHubConnect.destUrl=..%2Fcgi-bin%2FhgTracks&clade=other&org=S.+cerevisiae&db=sacCer3&position=";
    
    /** Ucsc link */
    private String UCSCLink ="";
    
    /** assembly indicator */
    private String assembly = "";
    
    /** warning counter */
    private static int warningCounter = 0;
    
    /**
     * constructs a GenomicElement object
     * @param line a line of the bed file
     * @param ftr FileTypeReader object
     */
    public GenomicElement(String line, FileTypeReader ftr){
        line = line.trim();
        
        //System.out.println(line);
        if(line.length() > 0){
            //line = line.toLowerCase();
            if(line.indexOf(",") > -1){
                line = line.replaceAll(",", "");
            }
            
            String[] la = line.split("\\s+");
            int colon = line.indexOf(":");
            if(colon > -1 && colon < 10){
                parseColonCoordinates(line);
            }else if(line.indexOf("\t") > -1){ 
                parseTabDelimitedCoordinates(line, ftr);
            }if(la.length >= 3){
                parseWhiteSpaceDelimitedCoordinates(line, ftr);
            }
            if(this.CHR == null){
                System.out.println("couldn't parse " + line);
            }
        }else{
            System.out.println("parsing empty String");
            this.CHR = "undefined";
            this.START = 0;
            this.END = 0;
            this.STRAND = "undefined";
            this.ID = "";
        }
    }
    
    /**
     * constructs a GenomicElement object
     * @param line a line of the bed file
     * @param ftr FileTypeReader object
     * @param gui graphical user interface
     */
    public GenomicElement(String line, FileTypeReader ftr, VCFFilter gui){
        line = line.trim();
        
        //System.out.println(line);
        if(line.length() > 0){
            //line = line.toLowerCase();
            if(line.indexOf(",") > -1){
                line = line.replaceAll(",", "");
            }
            
            String[] la = line.split("\\s+");
            int colon = line.indexOf(":");
            if(colon > -1 && colon < 10){
                parseColonCoordinates(line);
            }else if(line.indexOf("\t") > -1){ 
                parseTabDelimitedCoordinates(line, ftr, gui);
            }if(la.length >= 3){
                parseWhiteSpaceDelimitedCoordinates(line, ftr, gui);
            }
            if(this.CHR == null){
                new Warning(gui, "couldn't parse " + line);
                System.out.println("couldn't parse " + line);
                this.CHR = "undefined";
                this.START = 0;
                this.END = 0;
                this.STRAND = "undefined";
                this.ID = "";
                
            }
        }else{
            new Warning(gui, "parsing empty String ");
            System.out.println("parsing empty String");
            this.CHR = "undefined";
            this.START = 0;
            this.END = 0;
            this.STRAND = "undefined";
            this.ID = "";
        }
    }

    /**
     * constructs a GenomicElement object
     * @param id coordinates
     * @param chr chr
     * @param strand strand
     * @param start start
     * @param end end
     */
    public GenomicElement(String id, String chr, String strand, String start, String end){
        ID = id;
        CHR = chr;
        STRAND = strand;
        START = Integer.parseInt(start);
        END = Integer.parseInt(end);
 
    }
    
    /**
    * Creates new GenomicElement.
    * 
    * @param chr chr
    * @param start start
    * @param end end
    * @author Heiko Müller
    * @since 1.0
    */
    public GenomicElement(String chr, int start, int end){
        ID = "";
        CHR = chr;
        STRAND = "";
        START = start;
        END = end;
    }
    
    /**
    * Creates new GenomicElement.
    * 
    * @param vc VariantContext object
    * @author Heiko Müller
    * @since 1.0
    */
    public GenomicElement(VariantContext vc){
        ID = "";
        CHR = vc.getContig();
        STRAND = "+";
        START = vc.getStart();
        END = vc.getEnd();
 
    }

    /**
     * constructs a GenomicElement object
     * @param id coordiantes
     * @param chr chr
     * @param strand strand
     * @param start start
     * @param end end
     * @param measure measure associated with this region
     */
     public GenomicElement(String id, String chr, String strand, String start, String end, String measure){
        ID = id;
        CHR = chr;
        STRAND = strand;
        START = Integer.parseInt(start);
        END = Integer.parseInt(end);
        MEASURE = Integer.parseInt(measure);

    }

     /**
      * constructs a GenomicElement object
      * @param id coordinates
      * @param chr chr
      * @param strand strand
      * @param start start
      * @param end end
      * @param measure measure associated with this region
      */
     public GenomicElement(String id, String chr, String strand, int start, int end, double measure){
        ID = id;
        CHR = chr;
        STRAND = strand;
        START = start;
        END = end;
        MEASURE = measure;
        POSITION = (START + END)/2;

    }

     /**
      * constructs a GenomicElement object
      * @param chr chr
      * @param start start
      * @param end end
      * @param measure measure associated with this region
      */
     public GenomicElement(String chr, String start, String end, String measure){
        ID = "";
        CHR = chr;
        STRAND = "+";
        START = Integer.parseInt(start);
        END = Integer.parseInt(end);
        MEASURE = Double.parseDouble(measure);

    }

    /**
     * return 5'end
     * @param ge genomic element
     * @return int
     */
    public static int getTSS(GenomicElement ge){
        if(ge.STRAND.equals("+")){
            return ge.START;
        }else if(ge.STRAND.equals("-")){
            return ge.END;
        }else{
            return Integer.MIN_VALUE;
        }
    }

    /**
     * returns center position
     * @return int
     */
    public int getMean(){
        if(POSITION == 0){
            POSITION = (END + START)/2;
        }
        return POSITION;
    }

    /**
     * returns a GenomicElement object with length 1 corresponding to the start position of the input GenomicElement
     * @return GenomicElement
     */
    public GenomicElement getStartCopy(){
        return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.START, this.START, this.MEASURE));
    }

    /**
     * returns a GenomicElement object with length 1 corresponding to the end position of the input GenomicElement
     * @return GenomicElement
     */
    public GenomicElement getEndCopy(){
        return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.END, this.END, this.MEASURE));
    }

    /**
     * returns a GenomicElement object with length 1 corresponding to the mean position of the input GenomicElement
     * @return GenomicElement
     */
    public GenomicElement getMeanCopy(){
        int mean = (START + END)/2;
        return(new GenomicElement(this.ID, this.CHR, this.STRAND, mean, mean, this.MEASURE));
    }

    /**
     * returns a GenomicElement object with length 1 corresponding to the 5prime position of the input GenomicElement
     * @return GenomicElement
     */
    public GenomicElement get5primeCopy(){
        //System.out.println("getting 5'copy ");
        //System.out.println(this.CHR);
        //System.out.println(this.START);
        //System.out.println(this.END);
        //System.out.println(this.STRAND);
        //System.out.println(this.ID);
        if(STRAND.equals("+")){
            return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.START, this.START, this.MEASURE));
        }else if(STRAND.equals("-")){
            return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.END, this.END, this.MEASURE));
        }else{
            return null;
        }
    }

    /**
     * returns a GenomicElement object with length 1 corresponding to the 3prime position of the input GenomicElement
     * @return GenomicElement
     */
    public GenomicElement get3primeCopy(){
        if(STRAND.equals("+")){
            return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.END, this.END, this.MEASURE));
        }else if(STRAND.equals("-")){
            return(new GenomicElement(this.ID, this.CHR, this.STRAND, this.START, this.START, this.MEASURE));
        }else{
            return null;
        }
    }    

    /**
     * init Vector for miscellaneous infos
     */
    public void initMisc(){
        misc = new Vector();
    }

    /**
     * reads out miscellaneous information Vector
     * @param ge genomic element
     * @return String
     */
    public static String getMiscContent(GenomicElement ge){
        StringBuilder result = new StringBuilder();
        if(ge.misc != null){
            for(int i = 0; i < ge.misc.size(); i++){
                result.append(ge.misc.elementAt(i).toString() + "\t");
            }
        }
        return result.toString();
    }

    /**
     * calculates overlap size of two GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return int
     */
    public static int overlapSize(GenomicElement ge1, GenomicElement ge2){
        int result = 0;
        if(ge1.CHR.equals(ge2.CHR)){
            if(ge1.START <= ge2.START && ge2.START < ge1.END && ge2.END >= ge1.END){
                //System.out.println("case1");
                result = ge1.END - ge2.START;
            }else if(ge1.START <= ge2.START && ge2.END <= ge1.END){
                //System.out.println("case2");
                result = ge2.END - ge2.START;
            }else if(ge2.START <= ge1.START && ge2.END >= ge1.END){
                //System.out.println("case3");
                result = ge1.END - ge1.START;
            }else if(ge2.START <= ge1.START && ge2.END <= ge1.END && ge1.START <= ge2.END ){
                //System.out.println("case4");
                result = ge2.END - ge1.START;
            }
        }
        return result;
     }

    /**
     * tests if two GenomicElements overlap
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return boolean
     */
    public static boolean overlaps(GenomicElement ge1, GenomicElement ge2){
        boolean result = false;
        if(ge1.CHR.equals(ge2.CHR)){
            if(ge1.START >= ge2.START && ge1.START < ge2.END){
                //System.out.println("case1");
                result = true;
            }else if(ge2.START >= ge1.START && ge2.START < ge1.END){
                result = true;
            }
        }
        return result;
     }

    /**
     * tests if two GenomicElements overlap or touch each other
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return boolean
     */
    public static boolean overlapsOrTouches(GenomicElement ge1, GenomicElement ge2){
        boolean result = false;
        if(ge1.CHR.equals(ge2.CHR)){
            if(ge1.START >= ge2.START && ge1.START <= ge2.END){
                //System.out.println("case1");
                result = true;
            }else if(ge2.START >= ge1.START && ge2.START <= ge1.END){
                result = true;
            }
        }
        return result;
     }

    /**
     * shrinks two overlapping GenomicElements to two non-overlapping GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement[]
     */
    public static GenomicElement[] shrinkToNonOverlapping(GenomicElement ge1, GenomicElement ge2){
        int overlaptype = overlapType(ge1, ge2);
        GenomicElement[] result = new GenomicElement[2];
        switch(overlaptype){
            case 0: return null;
            case 1: {
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge1.END);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + ge2.START, "" + ge2.END);
                return result;
            }
            case 10: {
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge1.END);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + ge2.START, "" + ge2.END);
                return result;
            }
            case 19: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge1.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 46: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge1.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 73: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + (ge1.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 20: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge1.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 21: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge2.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 47: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge1.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 48: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + (ge2.END - overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 74: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + (ge1.START + overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 75: {
                int overlapsize = overlapSize(ge1, ge2);
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + (ge1.START + overlapsize/2));
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 78: {

                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge2.END);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + result[0].END, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 81: {                
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge2.END);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge2.ID, ge2.CHR, ge2.STRAND, "" + ge1.START, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            default: return null;
        }
    }



    /**
     * returns GenomicElement that holds the regions covered by at least one of two overlapping GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement OR(GenomicElement ge1, GenomicElement ge2){
        int overlaptype = overlapType(ge1, ge2);
        switch(overlaptype){
            case 0: return null;
            case 1: return null;
            case 10: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
            case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
            case 46: return ge2;
            case 73: return ge2;
            case 20: return ge1;
            case 21: return ge1;
            case 47: return ge1;
            case 48: return ge1;
            case 74: return ge2;
            case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
            case 78: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
            case 81: return null;
            default: return null;
        }
    }

     /**
      * returns GenomicElement that holds the regions covered by at least one of two overlapping GenomicElements on the same strand
      * @param ge1 genomic element
      * @param ge2 genomic element
      * @return GenomicElement
      */
    public static GenomicElement OROnSameStrand(GenomicElement ge1, GenomicElement ge2){
        if(ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 46: return ge2;
                case 73: return ge2;
                case 20: return ge1;
                case 21: return ge1;
                case 47: return ge1;
                case 48: return ge1;
                case 74: return ge2;
                case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 78: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions covered by at least one of two overlapping GenomicElements on different strands
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement OROnDifferentStrands(GenomicElement ge1, GenomicElement ge2){
        if(!ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 46: return ge2;
                case 73: return ge2;
                case 20: return ge1;
                case 21: return ge1;
                case 47: return ge1;
                case 48: return ge1;
                case 74: return ge2;
                case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 78: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the region covered by both of two overlapping GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement AND(GenomicElement ge1, GenomicElement ge2){
        int overlaptype = overlapType(ge1, ge2);
        switch(overlaptype){
            case 0: return null;
            case 1: return null;
            case 10: return null;
            case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
            case 46: return ge1;
            case 73: return ge1;
            case 20: return ge2;
            case 21: return ge2;
            case 47: return ge1;
            case 48: return ge2;
            case 74: return ge1;
            case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
            case 78: return null;
            case 81: return null;
            default: return null;
        }
    }

    /**
     * returns GenomicElement that holds the region covered by both of two overlapping GenomicElements on the same strand
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement ANDOnSameStrand(GenomicElement ge1, GenomicElement ge2){
        if(ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return null;
                case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 46: return ge1;
                case 73: return ge1;
                case 20: return ge2;
                case 21: return ge2;
                case 47: return ge1;
                case 48: return ge2;
                case 74: return ge1;
                case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 78: return null;
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the region covered by both of two overlapping GenomicElements on different strands
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement ANDOnDifferentStrands(GenomicElement ge1, GenomicElement ge2){
        if(!ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return null;
                case 19: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.END);
                case 46: return ge1;
                case 73: return ge1;
                case 20: return ge2;
                case 21: return ge2;
                case 47: return ge1;
                case 48: return ge2;
                case 74: return ge1;
                case 75: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.END);
                case 78: return null;
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }

     /**
      * deduplicates a VectorVector&#60;GenomicElement&#62; of GenomicElements
      * removes elements with identical chr, start and end positions and leaves only one copy
      * @param v the Vector object
      * @return Vector&#60;GenomicElement&#62;
      */
    public static Vector<GenomicElement> deduplicate(Vector<GenomicElement> v) {

        for (int i = 0; i < v.size(); i++) {
            for (int j = i + 1; j < v.size(); j++) {
                //System.out.println("overlap type " + overlapType(v.elementAt(i), v.elementAt(j)) + " " + v.elementAt(i).ID + " " +v.elementAt(j).ID);
                if (overlapType(v.elementAt(i), v.elementAt(j)) == 47) {
                    v.removeElementAt(j);
                }
            }
        }
        //System.out.println(overlaps_present);
        return v;
    }

    /**
     * tests if Vector&#60;GenomicElement&#62; contains overlapping GenomicElements
     * @param v the Vector object
     * @return boolean
     */
    public static boolean overlapsPresent(Vector<GenomicElement> v){        
        boolean overlaps_present = false;
        for(int i = 0; i < v.size(); i++){
            for(int j = i + 1; j < v.size(); j++){
                if(overlaps(v.elementAt(i), v.elementAt(j))){
                    overlaps_present = true;                    
                    break;
                }
            }
        }
        //System.out.println(overlaps_present);
        return overlaps_present;
    }

    /**
     * returns GenomicElement that holds the regions covered by exactly one of the overlapping GenomicElements
     * @param v the Vector object
     * @return Vector&#60;GenomicElement&#62;
     */
    public static Vector<GenomicElement> XOR(Vector<GenomicElement> v){
        v = deduplicate(v);
                //for(int i = 0; i < v.size(); i++){
                //    System.out.print(v.elementAt(i).ID + " ");
                //}
                //int counter = 0;
        while(overlapsPresent(v)){
            //counter++;
            GenomicElement ge1 = v.elementAt(0);
            GenomicElement ge2 = v.elementAt(1);
            GenomicElement[] xors = XOR(ge1, ge2);
            //System.out.println("overlap type " + overlapType(ge1, ge2) + " " + ge1.ID + " " + ge2.ID + " cycle " + counter);
            if(xors != null){
                v.removeElementAt(1);
                v.removeElementAt(0);
                for(int i = 0; i < xors.length; i++){
                    v.add(xors[i]);
                    //System.out.println("xor " + (i + 1) + " " + xors[i].ID + " cycle " + counter);
                }
            }else{
                v.removeElementAt(0);
                v.add(ge1);
                //for(int i = 0; i < v.size(); i++){
                //    System.out.print(v.elementAt(i).ID + " ");
                //}
            }
        }
        return v;
    }

    /**
     * Returns GenomicElement that holds the regions covered by exactly one of two overlapping GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement[]
     */
    public static GenomicElement[] XOR(GenomicElement ge1, GenomicElement ge2){
        int overlaptype = overlapType(ge1, ge2);
        GenomicElement[] result = null;
        switch(overlaptype){
            case 0: return null;
            case 1: return null;
            case 10: return null;
            case 19: {
                result = new GenomicElement[2];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 46: {
                result = new GenomicElement[1];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                return result;
            }
            case 73: {
                result = new GenomicElement[2];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 20: {
                result = new GenomicElement[1];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                return result;
            }
            case 21:{
                result = new GenomicElement[2];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 47: return null;
            case 48: {
                result = new GenomicElement[1];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                return result;
            }
            case 74: {
                result = new GenomicElement[1];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                return result;
            }
            case 75: {
                result = new GenomicElement[2];
                result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.START, "" + ge1.START);
                result[0].ID = GenomicElement.getCoordinates(result[0]);
                result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                result[1].ID = GenomicElement.getCoordinates(result[1]);
                return result;
            }
            case 78: return null;
            case 81: return null;
            default: return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions covered by exactly one of two overlapping GenomicElements on the same strand
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement[]
     */
    public static GenomicElement[] XOROnSameStrand(GenomicElement ge1, GenomicElement ge2){
        if(ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            GenomicElement[] result = null;
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return null;
                case 19: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 46: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 73: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 20: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
                case 21:{
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 47: return null;
                case 48: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 74: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
                case 75: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 78: return null;
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }
    
    /**
     * returns GenomicElement that holds the regions covered by exactly one of two overlapping GenomicElements on different strands
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement[]
     */
    public static GenomicElement[] XOROnDifferentStrands(GenomicElement ge1, GenomicElement ge2){
        if(!ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            GenomicElement[] result = null;
            switch(overlaptype){
                case 0: return null;
                case 1: return null;
                case 10: return null;
                case 19: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 46: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 73: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 20: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
                case 21:{
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 47: return null;
                case 48: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 74: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
                case 75: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.END);
                    return result;
                }
                case 78: return null;
                case 81: return null;
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions not covered by one of two overlapping GenomicElements
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement NOT(GenomicElement ge1, GenomicElement ge2){
        int overlaptype = overlapType(ge1, ge2);
        switch(overlaptype){
            case 0: return null;
            case 1: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.START);
            case 10: return null;
            case 19: return null;
            case 46: return null;
            case 73: return null;
            case 20: return null;
            case 21: return null;
            case 47: return null;
            case 48: return null;
            case 74: return null;
            case 75: return null;
            case 78: return null;
            case 81: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.START);
            default: return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions not covered by one of two overlapping GenomicElements on same strand
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement NOTOnSameStrand(GenomicElement ge1, GenomicElement ge2){
        if(ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.START);
                case 10: return null;
                case 19: return null;
                case 46: return null;
                case 73: return null;
                case 20: return null;
                case 21: return null;
                case 47: return null;
                case 48: return null;
                case 74: return null;
                case 75: return null;
                case 78: return null;
                case 81: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.START);
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions not covered by one of two overlapping GenomicElements on different strands
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement NOTOnDifferentStrands(GenomicElement ge1, GenomicElement ge2){
        if(!ge1.STRAND.equals(ge2.STRAND)){
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.END, "" + ge2.START);
                case 10: return null;
                case 19: return null;
                case 46: return null;
                case 73: return null;
                case 20: return null;
                case 21: return null;
                case 47: return null;
                case 48: return null;
                case 74: return null;
                case 75: return null;
                case 78: return null;
                case 81: return new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.START);
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions covered by ge1 but not ge2
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement[] ANOTB(GenomicElement ge1, GenomicElement ge2){
        GenomicElement[] result = null;
        int overlaptype = overlapType(ge1, ge2);
        switch(overlaptype){
            case 0: return null;
            case 1: {
                    result = new GenomicElement[1];
                    result[0] = ge1;
                    return result;
                }
            case 10: {
                    result = new GenomicElement[1];
                    result[0] = ge1;
                    return result;
                }
            case 19: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
            case 46: return null;
            case 73: return null;
            case 20: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    return result;
                }
            case 21: {
                    result = new GenomicElement[2];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                    result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                    return result;
                }
            case 47: return null;
            case 48: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                    return result;
                }
            case 74: return null;
            case 75: {
                    result = new GenomicElement[1];
                    result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                    return result;
                }
            case 78: {
                    result = new GenomicElement[1];
                    result[0] = ge1;
                    return result;
                }
            case 81: {
                    result = new GenomicElement[1];
                    result[0] = ge1;
                    return result;
                }
            default: return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions covered by ge1 but not ge2 on the same strand
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement[] ANOTBOnSameStrand(GenomicElement ge1, GenomicElement ge2){
        if(ge1.STRAND.equals(ge2.STRAND)){
            GenomicElement[] result = null;
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                case 10: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                case 19: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        return result;
                    }
                case 46: return null;
                case 73: return null;
                case 20: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        return result;
                    }
                case 21: {
                        result = new GenomicElement[2];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 47: return null;
                case 48: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 74: return null;
                case 75: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 78: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                }
                case 81: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                default: return null;
            }
        }else{
            return null;
        }
    }

    /**
     * returns GenomicElement that holds the regions covered by ge1 but not ge2 on the different strands
     * @param ge1 genomic element
     * @param ge2 genomic element
     * @return GenomicElement
     */
    public static GenomicElement[] ANOTBOnDifferentStrands(GenomicElement ge1, GenomicElement ge2){
        if(!ge1.STRAND.equals(ge2.STRAND)){
            GenomicElement[] result = null;
            int overlaptype = overlapType(ge1, ge2);
            switch(overlaptype){
                case 0: return null;
                case 1: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                case 10: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                case 19: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        return result;
                    }
                case 46: return null;
                case 73: return null;
                case 20: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        return result;
                    }
                case 21: {
                        result = new GenomicElement[2];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge1.START, "" + ge2.START);
                        result[1] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 47: return null;
                case 48: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 74: return null;
                case 75: {
                        result = new GenomicElement[1];
                        result[0] = new GenomicElement(ge1.ID, ge1.CHR, ge1.STRAND, "" + ge2.END, "" + ge1.END);
                        return result;
                    }
                case 78: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                }
                case 81: {
                        result = new GenomicElement[1];
                        result[0] = ge1;
                        return result;
                    }
                default: return null;
            }
        }else{
            return null;
        }
    }

    
    
    /** calculates type of overlap
     * s1 = start element 1
     * e1 = end element 1
     * s2 = start element 2
     * e2 = end element 2
     * note that most cases listed here are unrealistic but the cases are exhaustive
     * including cases of zero size genomic elements and elements with negative size 
     * where end is smaller than start.
     * 
     * In total there are 81 cases to consider: 
     * comparisons to be made are between s1 vs s2, s1 vs e2, e1 vs s2, and e1 vs e2
     * each comparison can be &#60;, =, or &#62;.
     * Thus the total number of cases is 3^4 = 81.
     *
*case  1	s1&#60;s2	e1&#60;s2	s1&#60;e2	e1&#60;e2
*case  2	s1&#60;s2	e1&#60;s2	s1&#60;e2	e1=e2
*case  3	s1&#60;s2	e1&#60;s2	s1&#60;e2	e1&#62;e2
*case  4	s1&#60;s2	e1&#60;s2	s1=e2	e1&#60;e2
*case  5	s1&#60;s2	e1&#60;s2	s1=e2	e1=e2
*case  6	s1&#60;s2	e1&#60;s2	s1=e2	e1&#62;e2
*case  7	s1&#60;s2	e1&#60;s2	s1&#62;e2	e1&#60;e2
*case  8	s1&#60;s2	e1&#60;s2	s1&#62;e2	e1=e2
*case  9	s1&#60;s2	e1&#60;s2	s1&#62;e2	e1&#62;e2
*case 10	s1&#60;s2	e1=s2	s1&#60;e2	e1&#60;e2
*case 11	s1&#60;s2	e1=s2	s1&#60;e2	e1=e2
*case 12	s1&#60;s2	e1=s2	s1&#60;e2	e1&#62;e2
*case 13	s1&#60;s2	e1=s2	s1=e2	e1&#60;e2
*case 14	s1&#60;s2	e1=s2	s1=e2	e1=e2
*case 15	s1&#60;s2	e1=s2	s1=e2	e1&#62;e2
*case 16	s1&#60;s2	e1=s2	s1&#62;e2	e1&#60;e2
*case 17	s1&#60;s2	e1=s2	s1&#62;e2	e1=e2
*case 18	s1&#60;s2	e1=s2	s1&#62;e2	e1&#62;e2
*case 19	s1&#60;s2	e1&#62;s2	s1&#60;e2	e1&#60;e2
*case 20	s1&#60;s2	e1&#62;s2	s1&#60;e2	e1=e2
*case 21	s1&#60;s2	e1&#62;s2	s1&#60;e2	e1&#62;e2
*case 22	s1&#60;s2	e1&#62;s2	s1=e2	e1&#60;e2
*case 23	s1&#60;s2	e1&#62;s2	s1=e2	e1=e2
*case 24	s1&#60;s2	e1&#62;s2	s1=e2	e1&#62;e2
*case 25	s1&#60;s2	e1&#62;s2	s1&#62;e2	e1&#60;e2
*case 26	s1&#60;s2	e1&#62;s2	s1&#62;e2	e1=e2
*case 27	s1&#60;s2	e1&#62;s2	s1&#62;e2	e1&#62;e2
*case 28	s1=s2	e1&#60;s2	s1&#60;e2	e1&#60;e2
*case 29	s1=s2	e1&#60;s2	s1&#60;e2	e1=e2
*case 30	s1=s2	e1&#60;s2	s1&#60;e2	e1&#62;e2
*case 31	s1=s2	e1&#60;s2	s1=e2	e1&#60;e2
*case 32	s1=s2	e1&#60;s2	s1=e2	e1=e2
*case 33	s1=s2	e1&#60;s2	s1=e2	e1&#62;e2
*case 34	s1=s2	e1&#60;s2	s1&#62;e2	e1&#60;e2
*case 35	s1=s2	e1&#60;s2	s1&#62;e2	e1=e2
*case 36	s1=s2	e1&#60;s2	s1&#62;e2	e1&#62;e2
*case 37	s1=s2	e1=s2	s1&#60;e2	e1&#60;e2
*case 38	s1=s2	e1=s2	s1&#60;e2	e1=e2
*case 39	s1=s2	e1=s2	s1&#60;e2	e1&#62;e2
*case 40	s1=s2	e1=s2	s1=e2	e1&#60;e2
*case 41	s1=s2	e1=s2	s1=e2	e1=e2
*case 42	s1=s2	e1=s2	s1=e2	e1&#62;e2
*case 43	s1=s2	e1=s2	s1&#62;e2	e1&#60;e2
*case 44	s1=s2	e1=s2	s1&#62;e2	e1=e2
*case 45	s1=s2	e1=s2	s1&#62;e2	e1&#62;e2
*case 46	s1=s2	e1&#62;s2	s1&#60;e2	e1&#60;e2
*case 47	s1=s2	e1&#62;s2	s1&#60;e2	e1=e2
*case 48	s1=s2	e1&#62;s2	s1&#60;e2	e1&#62;e2
*case 49	s1=s2	e1&#62;s2	s1=e2	e1&#60;e2
*case 50	s1=s2	e1&#62;s2	s1=e2	e1=e2
*case 51	s1=s2	e1&#62;s2	s1=e2	e1&#62;e2
*case 52	s1=s2	e1&#62;s2	s1&#62;e2	e1&#60;e2
*case 53	s1=s2	e1&#62;s2	s1&#62;e2	e1=e2
*case 54	s1=s2	e1&#62;s2	s1&#62;e2	e1&#62;e2
*case 55	s1&#62;s2	e1&#60;s2	s1&#60;e2	e1&#60;e2
*case 56	s1&#62;s2	e1&#60;s2	s1&#60;e2	e1=e2
*case 57	s1&#62;s2	e1&#60;s2	s1&#60;e2	e1&#62;e2
*case 58	s1&#62;s2	e1&#60;s2	s1=e2	e1&#60;e2
*case 59	s1&#62;s2	e1&#60;s2	s1=e2	e1=e2
*case 60	s1&#62;s2	e1&#60;s2	s1=e2	e1&#62;e2
*case 61	s1&#62;s2	e1&#60;s2	s1&#62;e2	e1&#60;e2
*case 62	s1&#62;s2	e1&#60;s2	s1&#62;e2	e1=e2
*case 63	s1&#62;s2	e1&#60;s2	s1&#62;e2	e1&#62;e2
*case 64	s1&#62;s2	e1=s2	s1&#60;e2	e1&#60;e2
*case 65	s1&#62;s2	e1=s2	s1&#60;e2	e1=e2
*case 66	s1&#62;s2	e1=s2	s1&#60;e2	e1&#62;e2
*case 67	s1&#62;s2	e1=s2	s1=e2	e1&#60;e2
*case 68	s1&#62;s2	e1=s2	s1=e2	e1=e2
*case 69	s1&#62;s2	e1=s2	s1=e2	e1&#62;e2
*case 70	s1&#62;s2	e1=s2	s1&#62;e2	e1&#60;e2
*case 71	s1&#62;s2	e1=s2	s1&#62;e2	e1=e2
*case 72	s1&#62;s2	e1=s2	s1&#62;e2	e1&#62;e2
*case 73	s1&#62;s2	e1&#62;s2	s1&#60;e2	e1&#60;e2
*case 74	s1&#62;s2	e1&#62;s2	s1&#60;e2	e1=e2
*case 75	s1&#62;s2	e1&#62;s2	s1&#60;e2	e1&#62;e2
*case 76	s1&#62;s2	e1&#62;s2	s1=e2	e1&#60;e2
*case 77	s1&#62;s2	e1&#62;s2	s1=e2	e1=e2
*case 78	s1&#62;s2	e1&#62;s2	s1=e2	e1&#62;e2
*case 79	s1&#62;s2	e1&#62;s2	s1&#62;e2	e1&#60;e2
*case 80	s1&#62;s2	e1&#62;s2	s1&#62;e2	e1=e2
*case 81	s1&#62;s2	e1&#62;s2	s1&#62;e2	e1&#62;e2

     * correspondence relationships for realistic cases
     * when overlapType(GenomicElement e) is called for element1 with element2 as parameter or vice versa
     *
     * cases tested:
     *
     * GenomicElement ge = new GenomicElement("ID1", "chr1", "+", "100", "1000");
     *
     *   GenomicElement ge1 = new GenomicElement("ID1", "chr1", "+", "10000", "1000000");
     *   GenomicElement ge2 = new GenomicElement("ID1", "chr1", "+", "1000", "100000");
     *   GenomicElement ge3 = new GenomicElement("ID1", "chr1", "+", "500", "100000");
     *   GenomicElement ge4 = new GenomicElement("ID1", "chr1", "+", "100", "100000");
     *   GenomicElement ge5 = new GenomicElement("ID1", "chr1", "+", "50", "100000");
     *   GenomicElement ge6 = new GenomicElement("ID1", "chr1", "+", "200", "1000");
     *   GenomicElement ge7 = new GenomicElement("ID1", "chr1", "+", "200", "900");
     *   GenomicElement ge8 = new GenomicElement("ID1", "chr1", "+", "100", "1000");
     *   GenomicElement ge9 = new GenomicElement("ID1", "chr1", "+", "100", "900");
     *   GenomicElement ge10 = new GenomicElement("ID1", "chr1", "+", "50", "1000");
     *   GenomicElement ge11 = new GenomicElement("ID1", "chr1", "+", "50", "500");
     *   GenomicElement ge12 = new GenomicElement("ID1", "chr1", "+", "50", "100");
     *   GenomicElement ge13 = new GenomicElement("ID1", "chr1", "+", "50", "60");
     *
     * 	case element1	case element 2
*1	1               81              element 1 upstream of element2, no overlap, no touch
*2	10              78              element 1 upstream of element2, no overlap, touch
*3	19              75              s1&#60;s2	e1&#62;s2	s1&#60;e2	e1&#60;e2 partial overlap with element 1 upstream
*4	46              48              s1=s2	e1&#62;s2	s1&#60;e2	e1&#60;e2 element 1 contained in element2 starts identical
*5	73              21              s1&#62;s2	e1&#62;s2	s1&#60;e2	e1&#60;e2 element 1 contained in element2
*6	20              74              s1&#60;s2	e1&#62;s2	s1&#60;e2	e1=e2 element 2 contained in element1 ends identical
*7	21              73              s1&#60;s2	e1&#62;s2	s1&#60;e2	e1&#62;e2 element 2 contained in element1
*8	47              47              elements are identical and size is &#62; 0
*9	48              46              s1=s2	e1&#62;s2	s1&#60;e2	e1&#62;e2 element 2 contained in element1 starts identical
*10	74              20              s1&#62;s2	e1&#62;s2	s1&#60;e2	e1=e2 element 1 contained in element2 ends identical
*11	75              19              s1&#62;s2	e1&#62;s2	s1&#60;e2	e1&#62;e2 partial overlap with element 2 upstream
*12	78              10              element 2 upstream of element1, no overlap, touch
*13	81              1               element 2 upstream of element1, no overlap, no touch

     *
     * @param ge1 GenomicElement to be tested
     * @param ge2 GenomicElement to be tested
     * @return int code of overlap type
     */

    public static int overlapType(GenomicElement ge1, GenomicElement ge2){
        int result = 0;
        if(ge1.CHR.equals(ge2.CHR)){
            if(ge1.START < ge2.START){
                if(ge1.END < ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 1;
                        }else if(ge1.END == ge2.END){
                            result = 2;
                        }else if(ge1.END > ge2.END){
                            result = 3;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 4;
                        }else if(ge1.END == ge2.END){
                            result = 5;
                        }else if(ge1.END > ge2.END){
                            result = 6;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 7;
                        }else if(ge1.END == ge2.END){
                            result = 8;
                        }else if(ge1.END > ge2.END){
                            result = 9;
                        }
                    }
                }else if(ge1.END == ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 10;
                        }else if(ge1.END == ge2.END){
                            result = 11;
                        }else if(ge1.END > ge2.END){
                            result = 12;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 13;
                        }else if(ge1.END == ge2.END){
                            result = 14;
                        }else if(ge1.END > ge2.END){
                            result = 15;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 16;
                        }else if(ge1.END == ge2.END){
                            result = 17;
                        }else if(ge1.END > ge2.END){
                            result = 18;
                        }
                    }
                }else if(ge1.END > ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 19;
                        }else if(ge1.END == ge2.END){
                            result = 20;
                        }else if(ge1.END > ge2.END){
                            result = 21;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 22;
                        }else if(ge1.END == ge2.END){
                            result = 23;
                        }else if(ge1.END > ge2.END){
                            result = 24;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 25;
                        }else if(ge1.END == ge2.END){
                            result = 26;
                        }else if(ge1.END > ge2.END){
                            result = 27;
                        }
                    }
                }
            }else if(ge1.START == ge2.START){
                if(ge1.END < ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 28;
                        }else if(ge1.END == ge2.END){
                            result = 29;
                        }else if(ge1.END > ge2.END){
                            result = 30;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 31;
                        }else if(ge1.END == ge2.END){
                            result = 32;
                        }else if(ge1.END > ge2.END){
                            result = 33;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 34;
                        }else if(ge1.END == ge2.END){
                            result = 35;
                        }else if(ge1.END > ge2.END){
                            result = 36;
                        }
                    }
                }else if(ge1.END == ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 37;
                        }else if(ge1.END == ge2.END){
                            result = 38;
                        }else if(ge1.END > ge2.END){
                            result = 39;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 40;
                        }else if(ge1.END == ge2.END){
                            result = 41;
                        }else if(ge1.END > ge2.END){
                            result = 42;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 43;
                        }else if(ge1.END == ge2.END){
                            result = 44;
                        }else if(ge1.END > ge2.END){
                            result = 45;
                        }
                    }
                }else if(ge1.END > ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 46;
                        }else if(ge1.END == ge2.END){
                            result = 47;
                        }else if(ge1.END > ge2.END){
                            result = 48;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 49;
                        }else if(ge1.END == ge2.END){
                            result = 50;
                        }else if(ge1.END > ge2.END){
                            result = 51;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 52;
                        }else if(ge1.END == ge2.END){
                            result = 53;
                        }else if(ge1.END > ge2.END){
                            result = 54;
                        }
                    }
                }
            }else if(ge1.START > ge2.START){
                if(ge1.END < ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 55;
                        }else if(ge1.END == ge2.END){
                            result = 56;
                        }else if(ge1.END > ge2.END){
                            result = 57;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 58;
                        }else if(ge1.END == ge2.END){
                            result = 59;
                        }else if(ge1.END > ge2.END){
                            result = 60;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 61;
                        }else if(ge1.END == ge2.END){
                            result = 62;
                        }else if(ge1.END > ge2.END){
                            result = 63;
                        }
                    }
                }else if(ge1.END == ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 64;
                        }else if(ge1.END == ge2.END){
                            result = 65;
                        }else if(ge1.END > ge2.END){
                            result = 66;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 67;
                        }else if(ge1.END == ge2.END){
                            result = 68;
                        }else if(ge1.END > ge2.END){
                            result = 69;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 70;
                        }else if(ge1.END == ge2.END){
                            result = 71;
                        }else if(ge1.END > ge2.END){
                            result = 72;
                        }
                    }
                }else if(ge1.END > ge2.START){
                    if(ge1.START < ge2.END){
                        if(ge1.END < ge2.END){
                            result = 73;
                        }else if(ge1.END == ge2.END){
                            result = 74;
                        }else if(ge1.END > ge2.END){
                            result = 75;
                        }
                    }else if(ge1.START == ge2.END){
                        if(ge1.END < ge2.END){
                            result = 76;
                        }else if(ge1.END == ge2.END){
                            result = 77;
                        }else if(ge1.END > ge2.END){
                            result = 78;
                        }
                    }else if(ge1.START > ge2.END){
                        if(ge1.END < ge2.END){
                            result = 79;
                        }else if(ge1.END == ge2.END){
                            result = 80;
                        }else if(ge1.END > ge2.END){
                            result = 81;
                        }
                    }
                }
            }
        }
        return result;
     }

    /**
     * returns coordinates in colon-minus form
     * @param ge genomic element
     * @return String
     */
    public static String getCoordinates(GenomicElement ge){
        StringBuilder sb = new StringBuilder();
        sb.append(ge.CHR + ":" + ge.START + "-" + ge.END);
        return sb.toString();
    }

    
    /**
     * parseColonCoordinates
     * @param s string of genomic coordinates
     */
    public void parseColonCoordinates(String s){
        //s = s.trim();
        String[] s1 = s.split(":");
        String[] s2 = s1[1].split("-");
        StringBuilder sb = new StringBuilder();
        this.CHR = s1[0].toLowerCase();
        this.START = Integer.parseInt(s2[0]);
        this.END = Integer.parseInt(s2[1]);
    }

    /**
     * parseWhiteSpaceDelimitedCoordinates
     * @param s string of genomic coordinates
     * @param ftr FileTypeReader object
     */
    public void parseWhiteSpaceDelimitedCoordinates(String s, FileTypeReader ftr){
        //s = s.trim();
        String[] s1 = s.split("\\s+");
        if(ftr.chromosome_index > -1){
            this.CHR = s1[ftr.chromosome_index].toLowerCase();
        }
        if(ftr.start_index > -1){
            this.START = Integer.parseInt(s1[ftr.start_index]);
        }
        if(ftr.end_index > -1){
            this.END = Integer.parseInt(s1[ftr.end_index]);
        }
        if(ftr.strand_index > -1){
            this.STRAND = s1[ftr.strand_index];
        }
        if(ftr.name_index > -1){
            this.ID = s1[ftr.name_index];
        }
        if(ftr.measure_index > -1){
            this.MEASURE = Double.parseDouble(s1[ftr.measure_index]);
        }
    }
    
    /**
     * parseWhiteSpaceDelimitedCoordinates. Chromosome names are converted to upper case.
     * @param s string of genomic coordinates
     * @param ftr FileTypeReader object
     * @param gui graphical user interface
     */
    public void parseWhiteSpaceDelimitedCoordinates(String s, FileTypeReader ftr, VCFFilter gui){
        //s = s.trim();
        String[] s1 = s.split("\\s+");
        if(ftr.chromosome_index > -1){
            this.CHR = s1[ftr.chromosome_index].toUpperCase();
        }else{
            warningCounter++;
            new Warning(gui, "Could find chromosome column: Exiting" );
            System.exit(0);            
        }
        if(ftr.start_index > -1){
            try{
                this.START = Integer.parseInt(s1[ftr.start_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region start: " + s1[ftr.start_index] + " " + e.getMessage());
                //System.out.println("Could not parse as start: " + s1[ftr.start_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(ftr.end_index > -1){
            try{
                this.END = Integer.parseInt(s1[ftr.end_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region end: " + s1[ftr.end_index] + " " + e.getMessage());
                //System.out.println("Could not parse as end: " + s1[ftr.end_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(ftr.strand_index > -1){
            this.STRAND = s1[ftr.strand_index];
        }
        if(ftr.name_index > -1){
            this.ID = s1[ftr.name_index];
        }
        if(ftr.measure_index > -1){
            try{
                this.MEASURE = Double.parseDouble(s1[ftr.measure_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region measure: " + s1[ftr.end_index] + " " + e.getMessage());
                //System.out.println("Could not parse as measure: " + s1[ftr.measure_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(END < START){
            warningCounter++;
            new Warning(gui, "Problems in list file: end coordinates < start coordinates detected.");
            if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
            }
        }
    }
    
    /**
     * parseTabDelimitedCoordinates
     * @param s string of genomic coordinates
     * @param ftr FileTypeReader object
     */
    public void parseTabDelimitedCoordinates(String s, FileTypeReader ftr){
        String[] s1 = s.split("\t");
        if(ftr.chromosome_index > -1){
            this.CHR = s1[ftr.chromosome_index].toLowerCase();
        }
        if(ftr.start_index > -1){
            try{
                this.START = Integer.parseInt(s1[ftr.start_index]);
            }catch(NumberFormatException e){
                System.out.println("Could not parse as start: " + s1[ftr.start_index]);
            }
        }
        if(ftr.end_index > -1){
            try{
                this.END = Integer.parseInt(s1[ftr.end_index]);
            }catch(NumberFormatException e){
                System.out.println("Could not parse as end: " + s1[ftr.end_index]);
            }
        }
        if(ftr.strand_index > -1){
            this.STRAND = s1[ftr.strand_index];
        }
        if(ftr.name_index > -1){
            this.ID = s1[ftr.name_index];
        }
        if(ftr.measure_index > -1){
            try{
                this.MEASURE = Double.parseDouble(s1[ftr.measure_index]);
            }catch(NumberFormatException e){
                System.out.println("Could not parse as measure: " + s1[ftr.measure_index]);
            }
        }
    }
    
    /**
     * parseTabDelimitedCoordinates
     * @param s string of genomic coordinates
     * @param ftr FileTypeReader object
     * @param gui graphical user interface
     */
    public void parseTabDelimitedCoordinates(String s, FileTypeReader ftr, VCFFilter gui){
        String[] s1 = s.split("\t");
        if(ftr.chromosome_index > -1){
            this.CHR = s1[ftr.chromosome_index].toUpperCase();
        }
        if(ftr.start_index > -1){
            try{
                this.START = Integer.parseInt(s1[ftr.start_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region start: " + s1[ftr.start_index] + " " + e.getMessage());
                //System.out.println("Could not parse as start: " + s1[ftr.start_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(ftr.end_index > -1){
            try{
                this.END = Integer.parseInt(s1[ftr.end_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region end: " + s1[ftr.end_index] + " " + e.getMessage());
                //System.out.println("Could not parse as end: " + s1[ftr.end_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(ftr.strand_index > -1){
            this.STRAND = s1[ftr.strand_index];
        }
        if(ftr.name_index > -1){
            this.ID = s1[ftr.name_index];
        }
        if(ftr.measure_index > -1){
            try{
                this.MEASURE = Double.parseDouble(s1[ftr.measure_index]);
            }catch(NumberFormatException e){
                warningCounter++;
                new Warning(gui, "Could not parse as genomic region measure: " + s1[ftr.end_index] + " " + e.getMessage());
                //System.out.println("Could not parse as measure: " + s1[ftr.measure_index]);
                if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
                }
            }
        }
        if(END < START){
            warningCounter++;
            new Warning(gui, "Problems in list file: end coordinates < start coordinates detected.");
            if(warningCounter >= 5){
                    new Warning(gui, "Too many problems... Exiting.");
                    System.exit(0);
            }
        } 
    }
    
    /**
     * parseWhiteSpaceDelimitedCoordinates
     * @param sa array of strings of genomic coordinates
     * @param ftr FileTypeReader object
     */
    public void parseWhiteSpaceDelimitedCoordinates(String[] sa, FileTypeReader ftr){
        //s = s.trim();
        //String[] s1 = s.split("\\s+");
        if(ftr.chromosome_index > -1){
            this.CHR = sa[ftr.chromosome_index];
        }
        if(ftr.start_index > -1){
            this.START = Integer.parseInt(sa[ftr.start_index]);
        }
        if(ftr.end_index > -1){
            this.END = Integer.parseInt(sa[ftr.end_index]);
        }
        if(ftr.strand_index > -1){
            this.STRAND = sa[ftr.strand_index];
        }
        if(ftr.name_index > -1){
            this.ID = sa[ftr.name_index];
        }
        if(ftr.measure_index > -1){
            this.MEASURE = Double.parseDouble(sa[ftr.measure_index]);
        }
    }

    /**
     * getter for ID
     * @return return genomic coordinates
     */
    public String getID() {
        if(ID == null){
            ID = GenomicElement.getCoordinates(this);
        }
        return ID;
    }

    /**
     * setter for ID
     * @param ID sets genomic coordinates
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * getter for StringBuilder info
     * @return String
     */
    public String getSb() {
        return sb.toString();
    }

    /**
     * setter for StringBuilder
     * @param sb sets StringBuilder
     */
    public void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    /**
     * getter for USCS hyperlink
     * @return String
     */
    public String getUCSCLink() {
        if(assembly.equals("hg19")){
            UCSCLink = hg19 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("hg18")){
            UCSCLink = hg18 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("mm10")){
            UCSCLink = mm10 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("mm9")){
            UCSCLink = mm9 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("mm8")){
            UCSCLink = mm8 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("danRer7")){
            UCSCLink = danRer7 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("sacCer3")){
            UCSCLink = sacCer3 + CHR + "%3A" + START + "-" + END;
        }else if(assembly.equals("sacCer2")){
            UCSCLink = sacCer2 + CHR + "%3A" + START + "-" + END;
        }
        return UCSCLink;
    }

    /**
     * setter for USCS hyperlink
     * @param UCSCLink sets UCSC hyperlink
     */
    public void setUCSCLink(String UCSCLink) {
        this.UCSCLink = UCSCLink;
    }

    /**
     * getter for assembly
     * @return String
     */
    public String getAssembly() {
        return assembly;
    }

    /**
     * setter for assembly
     * @param assembly UCSCLink 
     */
    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }
    
    /**
    * Returns tab separated coordinates.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCoordinates(){
        return CHR + "\t" + START + "\t" + END;
    }
    
    /**
    * Returns chromosome number without leading "chr".
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getChromosomeNumber(){
        String c = "";
        if(CHR.length() > 3){
            c = CHR.substring(0, 3).toUpperCase();
        }
        if(c.equals("CHR")){
            return CHR.substring(3);
        }
        return CHR;
    }
    
    /**
    * Returns start position.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getStart(){        
        return START;
    }
    
    /**
    * Returns end position.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getEnd(){        
        return END;
    }
    
    /**
    * Sets the warning counter.
    * 
    * @param count max counts for warnings
    * @author Heiko Müller
    * @since 1.0
    */
    public static void setWarningCounter(int count){
        warningCounter = 0;
    }
    
}
