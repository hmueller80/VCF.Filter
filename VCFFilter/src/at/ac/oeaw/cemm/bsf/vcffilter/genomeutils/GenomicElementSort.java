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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

/** 
 * Sorter for GenomicElement objects.
 * GenomicElementSort.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class GenomicElementSort extends at.ac.oeaw.cemm.bsf.vcffilter.genomeutils.Sort implements Serializable{

    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /** center */
    public static final String center= "center";
    
    /** start */
    public static final String start = "start";
    
    /** end */
    public static final String end = "end";


    /**
     * sortByPosition sort list of GenomicElements by chromosome and then by start or end or center
     * @param ge genomic element
     * @param sortPosition sort position (one of: start center, end)
     * @return ArrayList&#60;GenomicElement&#62;
     */
    public ArrayList<GenomicElement> sortByPosition(ArrayList<GenomicElement> ge, String sortPosition){
        Hashtable<String, ArrayList<GenomicElement>> htal = new Hashtable<String, ArrayList<GenomicElement>>();
        //System.out.println("ge length " + ge.size());
        for (int i = 0; i < ge.size(); i++){
            if(htal.containsKey(ge.get(i).CHR)){
                htal.get(ge.get(i).CHR).add(ge.get(i));
            }else{
                ArrayList<GenomicElement> v = new ArrayList<GenomicElement>();
                v.add(ge.get(i));
                htal.put(ge.get(i).CHR, v);
            }
        }
        ArrayList<GenomicElement> list = new ArrayList<GenomicElement>();
        Set keys = htal.keySet();
        Object[] oa = keys.toArray();
        String[] skeys = new String[oa.length];
        for (int i = 0; i < oa.length; i++){
            skeys[i] = (String)oa[i];
        }
        skeys = shellsort(skeys);
        for (int i = 0; i < skeys.length; i++){
            Object[] geoa = (htal.get((String)skeys[i])).toArray();
            //geoa = shellsort(geoa);
            if(sortPosition.equals(center)){
                geoa = shellsortByMean(geoa);
            }else if(sortPosition.equals(start)){
                geoa = shellsortByStart(geoa);
            }else if(sortPosition.equals(end)){
                geoa = shellsortByEnd(geoa);
            }
            for (int j = 0; j < geoa.length; j++){
                list.add((GenomicElement)geoa[j]);
            }
        }
        return list;
    }

    /**
     * shell sort for 1D GenomicElement array
     *
     * @param data object to be sorted
     * @return sorted (ascending) GenomicElement array
     */
    public Object[] shellsortByMean(Object[] data) {
        int i, j, h;
        GenomicElement v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = (GenomicElement)data[i - 1];
                j = i;
                while (j > h && ((GenomicElement)data[j - h - 1]).getMean() > v.getMean()) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D GenomicElement array
     *
     * @param data object to be sorted
     * @return sorted (ascending) GenomicElement array
     */
    public Object[] shellsortByStart(Object[] data) {
        int i, j, h;
        GenomicElement v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = (GenomicElement)data[i - 1];
                j = i;
                while (j > h && ((GenomicElement)data[j - h - 1]).START > v.START) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D GenomicElement array
     *
     * @param data object to be sorted
     * @return sorted (ascending) GenomicElement array
     */
    public Object[] shellsortByEnd(Object[] data) {
        int i, j, h;
        GenomicElement v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = (GenomicElement)data[i - 1];
                j = i;
                while (j > h && ((GenomicElement)data[j - h - 1]).END > v.END) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * tests if list of GenomicElements is sorted by chr and start position
     * @param ge genomic element
     * @return boolean
     */
    public boolean isSorted(ArrayList<GenomicElement> ge){
        boolean sorted = true;
        for (int i = 1; i < ge.size(); i++){
            if(ge.get(i-1).CHR.equals(ge.get(i).CHR) && ge.get(i-1).START > ge.get(i).START){
                sorted = false;
                break;
            }
        }
        return sorted;
    }
}
