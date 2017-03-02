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

import java.io.*;
import java.util.Vector;

/** 
 * Utility for reading bed data from different input file formats.
 * FileTypeReader.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FileTypeReader implements Serializable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * index of column holding chromosome
     */
    public int chromosome_index = -1;
    
    /**
     * index of column holding region start
     */
    public int start_index = -1;
    
    /**
     * the start value of this region
     */
    private int start_value = -1;
    
    /**
     * index of column holding region end
     */
    public int end_index = -1;
    
    /**
     * the end value of this region
     */
    private int end_value = -1;
    
    /**
     * index of column holding strand information
     */
    public int strand_index = -1;
    
    /**
     * index of column holding region name
     */
    public int name_index = -1;
    
    /**
     * index of column holding region measure
     */
    public int measure_index = -1;
    
    /**
     * measure value of this region
     */
    private double measure_value = -1;

    /**
    * Creates new FileTypeReader.
    * 
    * @param filename file to be read
    * @author Heiko Müller
    * @since 1.0
    */
    public FileTypeReader(String filename) {
        defineIndices(filename);
    }

    /**
    * Creates new FileTypeReader.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public FileTypeReader() {
        
    }

    /**
    * Resets all indices to -1.
    *  
    * @author Heiko Müller
    * @since 1.0
    */
    private void resetIndices(){
        chromosome_index = -1;
        start_index = -1;
        start_value = -1;
        end_index = -1;
        end_value = -1;
        strand_index = -1;
        name_index = -1;
        measure_index = -1;
        measure_value = -1;
    }

    /**
     * Tests if file has header line
     * @param filename the file to be tested
     * @return true if number of columns in first row differs from number of columns in second row, false otherwise
     */
    public boolean fileHasHeaderRow(String filename){
        boolean result = false;

        try{
            int current_columnnumber = 0;
            int previous_columnnumber = 0;

            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();//reads first line
            if(line != null){
                previous_columnnumber = (line.split("\t")).length;
            }

            line = br.readLine();//reads second line
            if(line != null){
                current_columnnumber = (line.split("\t")).length;
            }
            if(current_columnnumber != previous_columnnumber){
                    result = true;
            }
            
            fr.close();
            br.close();
         }catch(IOException ioe){
            System.out.println("problems reading file");
         }
        return result;
    }

    /**
     * defines indices for columns holding chromosome name, identifier, strand, start, end, and measure (if any) values
     * assumes that start comes before end comes before measure and has no more than three columns with numeric values
     * @param filename the file to be tested
     */
    public void defineIndices(String filename){
        resetIndices();
        Vector<String[]> v = readFileContent(filename, 1000);
        String[] temp = null;
        
        if(v != null && v.size() > 0){
            for(int i = 0; i < v.elementAt(0).length; i++){
                temp =  v.elementAt(i);
                System.out.println(temp[0]);
                for(int j = 0; j < v.size(); j++){
                    try{
                        Double in = Double.parseDouble(temp[i]);
                        if(start_value == -1 && start_index == -1){
                            start_index = i;
                            start_value = in.intValue();                            
                        }else if(end_value == -1 && i > start_index && start_index > -1){
                            end_index = i;
                            end_value = in.intValue();                            
                        }else if(measure_value == -1 && i > end_index && end_index > -1){
                            measure_index = i;
                            measure_value = in.doubleValue();
                        }
                        
                    }catch(NumberFormatException nfe){
                        if(temp[i].startsWith("chr") && temp[i].indexOf(":") ==  -1 && temp[i].indexOf(" ") == -1){
                            if(chromosome_index == -1){
                                chromosome_index = i;
                            }else if(chromosome_index != i){
                                System.out.println("Could not find chromosome index");
                            }
                        }else if(temp[i].equals("+") || temp[i].equals("-")){
                            if(strand_index == -1){
                                strand_index = i;
                            }else if(strand_index != i){
                                System.out.println("Could not find strand index");
                            }
                        }else{
                            if(name_index == -1){
                                name_index = i;
                            }else if(name_index != i){
                                System.out.println("Could not find name index");
                            }
                        }
                    }
                }
            }
            if(start_value > end_value){
                chromosome_index = -1;
                start_index = -1;
                start_value = -1;
                end_index = -1;
                end_value = -1;
                strand_index = -1;
                name_index = -1;
                measure_index = -1;
                measure_value = -1;
            }
        }else{
            System.out.println("Could not read file");
        }
    }

    /**
     * private method that reads the number of intervals in the bedgraph file
     *
      * @param   filename the path to the bedgraph file to be mapped.
     *  @param   limit limits the number of lines to be read
      * @return number of intervals specified in bedgraph file, used for progress bar
     */
     private int readFileLineNumber(String filename, int limit){
         int result = 0;
        try{
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            while(br.readLine() != null && result < limit){
                result++;
            }
        }catch(IOException ioe){}
        return result;
     }

     /**
     * tests that all String[] objects inside Vector have equal length
     *
      * @param   v Vector to be tested
      * @return boolean true if all String[] have equal length, false otherwise
     */
     private boolean numberOfColumnsAllEqual(Vector<String[]> v){
         boolean result = true;
         if(v.size() > 1){
            for(int i = 1; i < v.size(); i++){
                if(v.elementAt(i - 1).length != v.elementAt(i).length){
                    result = false;
                    break;
                }
            }
         }
         return result;
     }

     /**
      * reads the content of a tab-delimited file up to the number of lines specified as limit
      * returns a Vector holding each row of the file as a String[]
      *
      * @param filename
      * @param limit
      * @return Vector<String[]>
      */
     private Vector<String[]> readFileContent(String filename, int limit){
         System.out.println(filename + " " + limit);
        Vector<String[]> result = new Vector<String[]>();
        int linenumber = 0;
        String line = "";        
        try{
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            if(fileHasHeaderRow(filename)){
                br.readLine();                
            }
            while((line = br.readLine()) != null && linenumber < limit){
                result.add(line.split("\t"));
                linenumber++;
            }

            fr.close();
            br.close();
        }catch(IOException ioe){
            System.out.println("Couldn't read the file");
        }
        if(numberOfColumnsAllEqual(result)){
            return result;
        }else{
            System.out.println("Attention: Number of columns unequal");
            return result;
        }
     }

     /**
      * returns a FileTypeReader object for simple bed files
      * @return FileTypeReader
      */
     public static FileTypeReader getBedReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 0;
          result.start_index = 1;
          result.end_index = 2;
          result.name_index = 3;
          return result;
     }
     
     /**
    * Returns FileTypeReader for essential bed format with just chr, start, end.
    * 
    * @return FileTypeReader 
    * @author Heiko Müller
    * @since 1.0
    */
     public static FileTypeReader getBedEssentialReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 0;
          result.start_index = 1;
          result.end_index = 2;
          result.name_index = -1;
          return result;
     }
     
     /**
    * Returns FileTypeReader for PLINK .hom file.
    * 
    * @return FileTypeReader 
    * @author Heiko Müller
    * @since 1.0
    */
     public static FileTypeReader getPLINKReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 3;
          result.start_index = 6;
          result.end_index = 7;
          result.name_index = -1;
          return result;
     }

     /**
      * Returns a FileTypeReader object for UCSC simpleRepeat files.
      * @return FileTypeReader
      */
     public static FileTypeReader getRepeatNameReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 5;
          result.start_index = 6;
          result.end_index = 7;
          result.name_index = 10;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC simpleRepeat subtype files
      * @return FileTypeReader
      */
     public static FileTypeReader getRepeatSubtypeReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 5;
          result.start_index = 6;
          result.end_index = 7;
          result.name_index = 11;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC simpleRepeat type files
      * @return FileTypeReader
      */
     public static FileTypeReader getRepeatTypeReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 5;
          result.start_index = 6;
          result.end_index = 7;
          result.name_index = 12;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC AllmRNA Accession files
      * @return FileTypeReader
      */
     public static FileTypeReader getAllmRNAAccReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 14;
          result.start_index = 16;
          result.end_index = 17;
          result.name_index = 10;
          result.strand_index = 9;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC CpGislandExtReaderhg18 files
      * @return FileTypeReader
      */
     public static FileTypeReader getCpGislandExtReaderhg18(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 0;
          result.start_index = 1;
          result.end_index = 2;
          result.name_index = 3;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC CpGislandExtReaderhg19 files
      * @return FileTypeReader
      */
     public static FileTypeReader getCpGislandExtReaderhg19(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 1;
          result.start_index = 2;
          result.end_index = 3;
          result.name_index = 4;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC EnsGene files
      * @return FileTypeReader
      */
     public static FileTypeReader getEnsGeneReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 2;
          result.start_index = 4;
          result.end_index = 5;
          result.name_index = 1;
          result.strand_index = 3;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC PhastConsElements files
      * @return FileTypeReader
      */
     public static FileTypeReader getPhastConsElementsReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 1;
          result.start_index = 2;
          result.end_index = 3;
          result.name_index = 4;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC Refgene files
      * @return FileTypeReader
      */
     public static FileTypeReader getRefgeneReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 2;
          result.start_index = 4;
          result.end_index = 5;
          result.name_index = 1;
          result.strand_index = 3;
          result.measure_index = 12;
          return result;
     }

     /**
      * returns a FileTypeReader object for UCSC SimpleRepeat files
      * @return FileTypeReader
      */
     public static FileTypeReader getSimpleRepeatReader(){
          FileTypeReader result = new FileTypeReader();
          result.chromosome_index = 1;
          result.start_index = 2;
          result.end_index = 3;
          result.name_index = 16;
          return result;
     }

     /*
     public static void main(String[] a){
        //FileTypeReader.defineIndices("C:\\Temp\\A3.txt");
         //System.out.println(FileTypeReader.chromosome_index);
         //System.out.println(FileTypeReader.strand_index);
         //System.out.println(FileTypeReader.start_index);
         //System.out.println(FileTypeReader.end_index);
         //System.out.println(FileTypeReader.name_index);
         //System.out.println(FileTypeReader.measure_index);
     }
     * 
     */
}
