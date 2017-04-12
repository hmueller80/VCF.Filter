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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** 
 * Utility for turning genomic regions from a bed file into a list of GenomicElement objects.
 * GenomicElementListParser.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class GenomicElementListParser {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    ArrayList<GenomicElement> genomicElements;
    
    /**
    * Creates new GenomicElementListParser.
    * 
    * @param bedfile the bed file
    * @author Heiko Müller
    * @since 1.0
    */
    public GenomicElementListParser(File bedfile){
        String filename = bedfile.getName();
        if(filename.endsWith(".bed") || filename.endsWith(".hom")){
            genomicElements = new ArrayList<GenomicElement>();
            if(bedfile.getName().endsWith(".bed")){
                FileTypeReader ftr = FileTypeReader.getBedEssentialReader();            
                try{
                    FileReader fr = new FileReader(bedfile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    while((line = br.readLine()) != null){
                        //System.out.println(line);
                        if(!line.startsWith("#")){
                            GenomicElement g = new GenomicElement(line, ftr);
                            if(g.CHR.toUpperCase().startsWith("CHR")){
                                g.CHR = g.CHR.substring(3);
                            }
                            genomicElements.add(g);
                        }
                    }
                    br.close();
                    fr.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }else if(bedfile.getName().endsWith(".hom")){
                FileTypeReader ftr = FileTypeReader.getPLINKReader();            
                try{
                    FileReader fr = new FileReader(bedfile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    br.readLine();//skip header
                    while((line = br.readLine()) != null){
                        if(!line.startsWith("#")){
                            line = parsePlinkLine(line);
                            //System.out.println(line);
                            GenomicElement g = new GenomicElement(line, ftr);
                            if(g.CHR.toUpperCase().startsWith("CHR")){
                                g.CHR = g.CHR.substring(3);
                            }
                            genomicElements.add(g);
                        }
                    }
                    br.close();
                    fr.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    }
    
    /**
    * Creates new GenomicElementListParser.
    * 
    * @param bedfile the bed file
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public GenomicElementListParser(File bedfile, VCFFilter gui){
        String filename = bedfile.getName();
        if(filename.endsWith(".bed") || filename.endsWith(".hom")){
            genomicElements = new ArrayList<GenomicElement>();
            if(bedfile.getName().endsWith(".bed")){
                FileTypeReader ftr = FileTypeReader.getBedEssentialReader();            
                try{
                    FileReader fr = new FileReader(bedfile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    GenomicElement.setWarningCounter(0);
                    while((line = br.readLine()) != null){
                        //System.out.println(line);
                        if(!line.startsWith("#")){
                            GenomicElement g = new GenomicElement(line, ftr, gui);
                            if(g.CHR.toUpperCase().startsWith("CHR")){
                                g.CHR = g.CHR.substring(3);
                            }
                            genomicElements.add(g);
                        }
                    }
                    br.close();
                    fr.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }else if(bedfile.getName().endsWith(".hom")){
                FileTypeReader ftr = FileTypeReader.getPLINKReader();            
                try{
                    FileReader fr = new FileReader(bedfile);
                    BufferedReader br = new BufferedReader(fr);
                    String line = "";
                    br.readLine();//skip header
                    GenomicElement.setWarningCounter(0);
                    while((line = br.readLine()) != null){
                        if(!line.startsWith("#")){
                            line = parsePlinkLine(line);
                            //System.out.println(line);
                            GenomicElement g = new GenomicElement(line, ftr, gui);
                            if(g.CHR.toUpperCase().startsWith("CHR")){
                                g.CHR = g.CHR.substring(3);
                            }
                            genomicElements.add(g);
                        }
                    }
                    br.close();
                    fr.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }else{
            new Warning(gui, "Unsupported list file format. Use only .bed or PLINK .hom files as lists.");
        }
    }

    /**
    * Returns list of parsed GenomicElements.
    * 
    * @return ArrayList&#60;GenomicElement&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<GenomicElement> getGenomicElements() {
        return genomicElements;
    }
    
    private String parsePlinkLine(String line){
        line = line.trim();
        StringBuilder sb = new StringBuilder();
        char[] ca = line.toCharArray();
        String temp = "";
        int whitespacecount = 0;
        for(Character c : ca){
            if(Character.isWhitespace(c)){
                whitespacecount++;
                if(whitespacecount == 1){
                    sb.append(temp + "\t");
                    temp = "";
                }
            }else{
                whitespacecount = 0;
                temp = temp + c;
            }
        }
        return sb.toString();
    }
    
    
    
}
