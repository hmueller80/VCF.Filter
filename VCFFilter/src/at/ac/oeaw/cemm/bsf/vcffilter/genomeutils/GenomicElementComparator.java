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

import java.util.Comparator;

/** 
 * Comparator for GenomicElement objects.
 * GenomicElementComparator.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class GenomicElementComparator implements Comparator<GenomicElement>{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    public int compare(GenomicElement g1, GenomicElement g2){
        String c1 = g1.CHR;
        String c2 = g2.CHR;       
        if(c1.equals(c2)){
            int s1 = g1.START;
            int s2 = g2.START;
            if(s1 < s2){
                return -1;
            }else if(s1 > s2){
                return 1;
            }else{
                int e1 = g1.END;
                int e2 = g2.END;
                if(e1 < e2){
                    return -1;
                }else if(e1 > e2){
                    return 1;
                }else{
                    return 0;
                }
            }
        }else{
            try{
                int cn1 = Integer.parseInt(c1);
                int cn2 = Integer.parseInt(c2);
                if(cn1 < cn2){
                    return -1;
                }else if(cn1 > cn2){
                    return 1;
                }else{
                    return 0;
                }
            }catch(NumberFormatException nfe){
                return c1.compareTo(c2);
            }
        }       
    }
}
