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
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;
import java.util.ArrayList;


/** 
 * Permits fast searching of overlaps between GenomicElements.
 * Query.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Query implements Serializable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * The File holding the regions of interest
     */
    private File Regionsfile = null;

    /**
     * A Hashtable of Vector objects where each Vector holds the genomic elements (as GenomicElement objects,
     * sorted by start position)
     * found on a specific chromosome. The keys of this Hashtable are the chromosome names.
     */
    private Hashtable<String, Vector<GenomicElement>> Regions = null;

    /**
     * A Hashtable of Integer objects holding the index of the last overlapping region element
     * in the previous range query for a specific chromosome. This also means that queries must be sequential along the chromosome.
     * Random access queries are not supported.
     * The index refers to the Vector objects stored in the Hashtable "Regions".
     * The keys of this Hashtable are the chromosome names.
     *
     */
    private Hashtable<String, Integer> Indices_range = null;

    /**
     * A Hashtable of Integer objects holding the index of the last left neighbor region element
     * for the previous neighbors query for a specific chromosome. This also means that queries must be sequential along the chromosome.
     * Random access queries are not supported.
     * The index refers to the Vector objects stored in the Hashtable "Regions".
     * The keys of this Hashtable are the chromosome names.
     *
     */
    private Hashtable<String, Integer> Indices_left_neighbor = null;

    /**
     * A Hashtable of Integer objects holding the index of the last right neighbor region element
     * for the previous neighbors query for a specific chromosome. This also means that queries must be sequential along the chromosome.
     * Random access queries are not supported.
     * The index refers to the Vector objects stored in the Hashtable "Regions".
     * The keys of this Hashtable are the chromosome names.
     *
     */
    private Hashtable<String, Integer> Indices_right_neighbor = null;

    /**
     * A Hashtable of Integer objects holding the start of the last query.
     * The keys of this Hashtable are the chromosome names.
     * If the last query was further downstream than the current query,
     * no overlaps can be found unless indices in Hashtable Indices are reset to 0.
     * This makes sure that random access queries will return correct results,
     * but they will be very slow.
     *
     */
    private Hashtable<String, Integer> LastQuery = null;
    
    
    ArrayList<GenomicElement> flattenedRegions;


    /**
     * Creates an instance of RangeQuery.
     * 
     */
    public Query() {

    }
    
    /**
    * Creates new Query.
    * 
    * @param bedfiles array of bed file filenames
    * @author Heiko Müller
    * @since 1.0
    */
    public Query(File[] bedfiles) {
        ArrayList<GenomicElement> regions = new ArrayList<GenomicElement>();
        for(int i = 0; i < bedfiles.length; i++){
            GenomicElementListParser bfr = new GenomicElementListParser(bedfiles[i]);
            ArrayList<GenomicElement> content = bfr.getGenomicElements();
            regions.addAll(content);
        }        
        regions = flatten(regions); 
        flattenedRegions = regions;
        initAnnotations(regions);        
    }
    
    /**
    * Creates new Query.
    * 
    * @param bedfiles  list of bed file filenames
    * @author Heiko Müller
    * @since 1.0
    */
    public Query(ArrayList<File> bedfiles) {
        if(bedfiles != null && bedfiles.size() > 0){
            ArrayList<GenomicElement> regions = new ArrayList<GenomicElement>();
            for(File f : bedfiles){
                GenomicElementListParser bfr = new GenomicElementListParser(f);
                ArrayList<GenomicElement> content = bfr.getGenomicElements();
                regions.addAll(content);
            }        
            regions = flatten(regions);  
            flattenedRegions = regions;
            initAnnotations(regions);  
        }      
    }
    
    /**
    * Creates new Query.
    * 
    * @param bedfiles list of bed file filenames
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public Query(ArrayList<File> bedfiles, VCFFilter gui) {
        if(bedfiles != null && bedfiles.size() > 0){
            ArrayList<GenomicElement> regions = new ArrayList<GenomicElement>();
            for(File f : bedfiles){
                GenomicElementListParser bfr = new GenomicElementListParser(f, gui);
                ArrayList<GenomicElement> content = bfr.getGenomicElements();
                regions.addAll(content);
            }        
            regions = flatten(regions);  
            flattenedRegions = regions;
            initAnnotations(regions);  
        }      
    }
    
    /**
    * Takes a list of regions and generates a non-overlapping list of regions covered by any of the input regions.
    * 
    * @param regions list of genomic regions
    * @return ArrayList&#60;GenomicElement&#60;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<GenomicElement> flatten(ArrayList<GenomicElement> regions){
        regions.sort(new GenomicElementComparator());
        ArrayList<GenomicElement> result = new ArrayList<GenomicElement>();
        while(regions.size() > 0){
            GenomicElement g1 = regions.get(0);
            regions.remove(0);                       
            while(regions.size() > 0 && GenomicElement.overlapsOrTouches(g1, regions.get(0))){
                if(g1.END < regions.get(0).END){
                    g1.END = regions.get(0).END;
                }
                regions.remove(0);
            }
            result.add(g1);               
        }
        return result;
    }

    /**
    * Takes a list of regions and generates a non-overlapping list of regions covered by any of the input regions. 
    * Then complements the list.
    * 
    * Currently not working correctly as contigs with 0 input regions are missing in output. 
    * Proper implementation should read the contigs from the source VCF file.
    * Useful for black list filtering.
    * 
    * @param regions list of genomic regions
    * @return ArrayList&#60;GenomicElement&#60;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<GenomicElement> complement(ArrayList<GenomicElement> regions){
        regions.sort(new GenomicElementComparator());
        ArrayList<GenomicElement> result = new ArrayList<GenomicElement>();
        for(int i = 0; i < regions.size() - 1; i++){
            if(result.size() == 0){
                GenomicElement g1 = regions.get(i);
                GenomicElement g3 = new GenomicElement(g1.CHR, 1, g1.START - 1);
                result.add(g3);
            }else{
                GenomicElement g1 = regions.get(i);
                GenomicElement g2 = regions.get(i + 1);
                if(g1.CHR.equals(g2.CHR)){
                    GenomicElement g3 = new GenomicElement(g1.CHR, g1.END + 1, g2.START - 1);
                    result.add(g3);
                }else{
                    GenomicElement g3 = new GenomicElement(g1.CHR, g1.END + 1, Integer.MAX_VALUE);
                    GenomicElement g4 = new GenomicElement(g2.CHR, 1, g2.START - 1);
                    result.add(g3);
                    result.add(g4);
                }
            }  
        }
        return result;
    }

     /**
     * private method that inits the Regions and Indices Hashtables.
     * The region elements for each chromosome are added as GenomicElement objects
     * to separate Vector objects that can be retrieved by chromosome name.
     * The index of the last overlapping region element is stored in the Indices Hashtable.
     *
     * @param   in     the annotations as GenomicElement containing ArrayList
     */
     public void initAnnotations(ArrayList<GenomicElement> in){
         Regions = new Hashtable<String, Vector<GenomicElement>>();
         Indices_range = new Hashtable<String, Integer>();
         LastQuery = new Hashtable<String, Integer>();
         Indices_left_neighbor = new Hashtable<String, Integer>();
         Indices_right_neighbor = new Hashtable<String, Integer>();
            for(int i = 0; i < in.size(); i++){

                GenomicElement ge = in.get(i);
                if(Regions.containsKey(ge.CHR)){
                    Vector<GenomicElement> v = Regions.get(ge.CHR);
                    GenomicElement temp = v.elementAt(v.size() - 1);
                    if(ge.START >= temp.START){
                            Regions.get(ge.CHR).add(ge);
                        }else{
                            System.out.println("Problem with " + temp.CHR + " " + temp.START + " " + temp.ID);
                            System.out.println("Regions must be sorted by chr and start position! Exiting... Correct file ");
                            break;
                        }
                }else{
                    Vector<GenomicElement> v = new Vector<GenomicElement>();
                    v.add(ge);
                    Regions.put(ge.CHR, v);
                    Indices_range.put(ge.CHR, new Integer(0));
                    Indices_left_neighbor.put(ge.CHR, new Integer(0));
                    Indices_right_neighbor.put(ge.CHR, new Integer(0));
                }
            }
     }

     

     /**
     * private method that inits the Regions and Indices Hashtables.
     * The region elements for each chromosome are added as GenomicElement objects
     * to separate Vector objects that can be retrieved by chromosome name.
     * The index of the last overlapping region element is stored in the Indices Hashtable.
     *
     * @param in     the annotations as GenomicElement containing ArrayList
     * @param point     start point
     */
     public void initPointAnnotations(ArrayList<GenomicElement> in, String point){
         Regions = new Hashtable<String, Vector<GenomicElement>>();
         Indices_range = new Hashtable<String, Integer>();
         LastQuery = new Hashtable<String, Integer>();
         Indices_left_neighbor = new Hashtable<String, Integer>();
         Indices_right_neighbor = new Hashtable<String, Integer>();
            for(int i = 0; i < in.size(); i++){
                //GenomicElement ge = in.get(i);
                GenomicElement ge = in.get(i).get3primeCopy();
                if(Regions.containsKey(ge.CHR)){
                    Vector<GenomicElement> v = Regions.get(ge.CHR);
                    GenomicElement temp = v.elementAt(v.size() - 1);
                    //if(point.equals("5prime")){
                    if(ge.START > temp.START){
                        Regions.get(ge.CHR).add(ge);
                    }else if(ge.START == temp.START){
                        continue;
                        //System.out.println("skipping duplicate");
                    }else if(ge.START < temp.START){
                        System.out.println("Problem with " + temp.CHR + " " + temp.START + " " + temp.ID);
                        System.out.println("Regions must be sorted by chr and start position! Exiting... Correct file ");
                        break;
                    }
                    //}
                }else{
                    Vector<GenomicElement> v = new Vector<GenomicElement>();
                    v.add(ge);
                    Regions.put(ge.CHR, v);
                    Indices_range.put(ge.CHR, new Integer(0));
                    Indices_left_neighbor.put(ge.CHR, new Integer(0));
                    Indices_right_neighbor.put(ge.CHR, new Integer(0));
                }
            }
     }


     /**
     * Returns the GenomicElements that overlap with the query interval stacked in a Vector object
      *
     * @param query defines the range being interrogated
     * @return Vector holding all GenomicElements overlapping with query range, Vector size() == 0 if no overlap exists
     */

     public Vector<GenomicElement> rangeQuery(GenomicElement query){
         if(LastQuery.containsKey(query.CHR)){
            if(LastQuery.get(query.CHR).intValue() > query.START){
                System.out.println("Sort queries by chr and start position to speed up querying.");
                resetIndices(query.CHR);
                LastQuery.remove(query.CHR);
                LastQuery.put(query.CHR, new Integer(query.START));
            }
         }else{
            LastQuery.put(query.CHR, new Integer(0));
         }

         Vector<GenomicElement> result = new Vector<GenomicElement>();
         if(Indices_range.containsKey(query.CHR)){
            Vector<GenomicElement> v = Regions.get(query.CHR);
            GenomicElement region = null;
            int index = Indices_range.get(query.CHR).intValue();

            for(int i = index; i < v.size(); i++){
                region = v.elementAt(i);
                if(query.START >= region.END){
                    region = null;
                    continue;
                }else if(query.END <= region.START){
                    region = null;
                    break;
                }else{
                    if(result.size() == 0){//do this only with first region matching query
                        Indices_range.remove(query.CHR);
                        Indices_range.put(query.CHR, new Integer(i));
                    }
                    result.add(region);
                }
            }
        }
        return result;
     }
     
     /**
    * Tests for matching regions in Regions.
    * 
    * @param query the query region
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
     public boolean match(GenomicElement query){
         //System.out.println("query chr " + query.CHR);
         if(LastQuery.containsKey(query.CHR)){
            if(LastQuery.get(query.CHR).intValue() > query.START){
                System.out.println("Sort queries by chr and start position to speed up querying.");
                resetIndices(query.CHR);
                LastQuery.remove(query.CHR);
                LastQuery.put(query.CHR, new Integer(query.START));
            }
         }else{
            LastQuery.put(query.CHR, new Integer(0));
         }

         Vector<GenomicElement> result = new Vector<GenomicElement>();
         if(Indices_range.containsKey(query.CHR)){
            Vector<GenomicElement> v = Regions.get(query.CHR);
            GenomicElement region = null;
            int index = Indices_range.get(query.CHR).intValue();

            for(int i = index; i < v.size(); i++){
                region = v.elementAt(i);
                //if(query.START >= region.END){
                if(query.START > region.END){
                    region = null;
                    continue;
                //}else if(query.END <= region.START){
                }else if(query.END < region.START){
                    region = null;
                    break;
                }else{
                    if(result.size() == 0){//do this only with first region matching query
                        Indices_range.remove(query.CHR);
                        Indices_range.put(query.CHR, new Integer(i));
                    }
                    result.add(region);
                }
            }
        }else{
             //System.out.println("contig " + query.CHR + " not found in index hash.");
         }
         if(result.size() > 0){
             return true;
         }else{
             return false;
         }        
     }

     /**
     * Returns the GenomicElements that overlap with the query interval stacked in a Vector object
      *
     * @param query defines the range being interrogated
     * @return Vector holding all GenomicElements overlapping with query range, Vector size() == 0 if no overlap exists
     */
     public GenomicElement[] neighborsQuery(GenomicElement query){

         if(LastQuery.containsKey(query.CHR)){
            if(LastQuery.get(query.CHR).intValue() > query.START){
                System.out.println("Sort queries by chr and start position to speed up querying.");
                resetIndices(query.CHR);
                LastQuery.remove(query.CHR);
                LastQuery.put(query.CHR, new Integer(query.START));
            }
         }else{
            LastQuery.put(query.CHR, new Integer(0));
         }

         //Vector<GenomicElement> result = new Vector<GenomicElement>();
         GenomicElement[] result = new GenomicElement[2];
         if(Indices_left_neighbor.containsKey(query.CHR)){
            Vector<GenomicElement> v = Regions.get(query.CHR);
            GenomicElement region1 = null;
            GenomicElement region2 = null;
            int index = Indices_left_neighbor.get(query.CHR).intValue();

            for(int i = index; i < v.size() - 1 && v.size() > 1; i++){
                region1 = v.elementAt(i);
                region2 = v.elementAt(i + 1);
                if(query.START >= region1.END && query.START >= region2.END){
                    continue;
                }else if(query.START >= region1.END && query.START <= region2.END){
                    result[0] = region1;
                    Indices_left_neighbor.remove(query.CHR);
                    Indices_left_neighbor.put(query.CHR, new Integer(i));
                    break;
                }else if(query.START < region1.END && query.START < region2.END){
                    if(query.START < v.elementAt(0).END){
                        break;
                    }else if(i >= 2){
                        i = i - 2;
                    }else{
                        break;
                    }
                }
            }
            if(result[0] == null && query.START > v.elementAt(v.size() - 1).END){
                result[0] = v.elementAt(v.size() - 1);
            }
        }
         if(Indices_right_neighbor.containsKey(query.CHR)){
             //System.out.println("Chr index found");
            Vector<GenomicElement> v = Regions.get(query.CHR);
            GenomicElement region1 = null;
            GenomicElement region2 = null;
            int index = Indices_right_neighbor.get(query.CHR).intValue();
            //System.out.println("use index " + index);
            for(int i = index; i < v.size() - 1 && v.size() > 1; i++){

                region1 = v.elementAt(i);
                region2 = v.elementAt(i + 1);
                //System.out.println(region1.ID + " " + region2.ID);
                if(query.END > region1.START && query.END > region2.START){
                    continue;
                }else if(query.END >= region1.START && query.END <= region2.START){
                    result[1] = region2;
                    Indices_right_neighbor.remove(query.CHR);
                    Indices_right_neighbor.put(query.CHR, new Integer(i));
                    break;
                }else if(query.END < region1.START && query.END < region2.START){
                    if(query.END > v.elementAt(v.size() - 1).START){
                        break;
                    }else
                        //if(i >= 2){
                        //i = i - 2;
                    //}else{
                        //break;
                    if(query.END < v.elementAt(0).START){
                        result[1] = v.elementAt(0);
                        Indices_right_neighbor.remove(query.CHR);
                        Indices_right_neighbor.put(query.CHR, new Integer(0));
                    }
                }
            }
        }
        return result;
     }

     /**
     * Returns the GenomicElements that overlap with the query interval stacked in a Vector object
      *
     * @param query defines the range being interrogated
     * @return Vector holding all GenomicElements overlapping with query range, Vector size() == 0 if no overlap exists
     */

     public GenomicElement[] neighborsStartQuery(GenomicElement query){

         if(LastQuery.containsKey(query.CHR)){
            if(LastQuery.get(query.CHR).intValue() > query.START){
                System.out.println("Sort queries by chr and start position to speed up querying.");
                resetIndices(query.CHR);
                LastQuery.remove(query.CHR);
                LastQuery.put(query.CHR, new Integer(query.START));
            }
         }else{
            LastQuery.put(query.CHR, new Integer(0));
         }

         //Vector<GenomicElement> result = new Vector<GenomicElement>();
         GenomicElement[] result = new GenomicElement[2];
         if(Indices_left_neighbor.containsKey(query.CHR)){
            Vector<GenomicElement> v = Regions.get(query.CHR);
            GenomicElement region1 = null;
            GenomicElement region2 = null;
            int index = Indices_left_neighbor.get(query.CHR).intValue();

            for(int i = index; i < v.size() - 1 && v.size() > 1; i++){
                region1 = v.elementAt(i);
                region2 = v.elementAt(i + 1);
                if(query.START >= region1.START && query.START >= region2.START){
                    continue;
                }else if(query.START >= region1.START && query.START <= region2.START){
                    result[0] = region1;
                    result[1] = region2;
                    Indices_left_neighbor.remove(query.CHR);
                    Indices_left_neighbor.put(query.CHR, new Integer(i));
                    //Indices_right_neighbor.remove(query.CHR);
                    //Indices_right_neighbor.put(query.CHR, new Integer(i + 1));
                    break;
                }else if(query.START < region1.START && query.START < region2.START){
                    break;
                    //if(query.START < v.elementAt(0).START){
                    //    break;
                    //}else if(i >= 2){
                    //    i = i - 2;
                    //}else{
                    //    break;
                    //}
                }
            }
            if(result[0] == null && query.START >= v.elementAt(v.size() - 1).START){
                result[0] = v.elementAt(v.size() - 1);
            }
            if(result[1] == null && query.START <= v.elementAt(0).START){
                result[1] = v.elementAt(0); 
            }
        }
        return result;
     }

     /**
     * Resets indices for a given chr to 0
     *
      * @param chr chromosome
     */
     private void resetIndices(String chr){
         if(Indices_range.containsKey(chr)){
            Indices_range.remove(chr);
            Indices_range.put(chr, new Integer(0));
         }
         if(Indices_left_neighbor.containsKey(chr)){
            Indices_left_neighbor.remove(chr);
            Indices_left_neighbor.put(chr, new Integer(0));
         }
         if(Indices_right_neighbor.containsKey(chr)){
            Indices_right_neighbor.remove(chr);
            Indices_right_neighbor.put(chr, new Integer(0));
         }
     }     

     /**
      * getter for Regions Hashtable
      * @return Hashtable&#60;String, Vector&#60;GenomicElement&#62;&#62;
      */
     public Hashtable<String, Vector<GenomicElement>> getAnnotations(){
        return Regions;
     }

     /**
    * Returns flattened regions.
    * 
    * @return ArrayList&#60;GenomicElement&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<GenomicElement> getFlattenedRegions() {
        return flattenedRegions;
    }
    
    /**
    * Returns flattened regions complement.
    * 
    * @return ArrayList&#60;GenomicElement&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<GenomicElement> getFlattenedRegionsComplement() {
        return complement(flattenedRegions);
    }
         
}
