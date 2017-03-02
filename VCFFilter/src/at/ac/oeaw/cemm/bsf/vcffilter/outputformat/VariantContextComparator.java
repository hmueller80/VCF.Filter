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
package at.ac.oeaw.cemm.bsf.vcffilter.outputformat;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Comparator;

/** 
 * Comparator for VariantContext objects.
 * VariantContextComparator.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class VariantContextComparator implements Comparator<VariantContext>{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
    * Utility to guide VariantContext sort order.
    * VariantContext objects are sorted according to contig in numerical order for autosomes
    * and in String order for sex chromosomes and other non-numerical contigs.
    * 
    * VariantContext objects with identical contigs are sorted in numerical order by start position.
    * 
    *
    * @author Heiko Mueller
    * @param vc1 VariantContext one
    * @param vc2 VariantContext two
    * @return int
    * @since 1.0
    */
    public int compare(VariantContext vc1, VariantContext vc2){
        String c1 = vc1.getContig();
        String c2 = vc2.getContig();       
        if(c1.equals(c2)){
            int p1 = vc1.getStart();
            int p2 = vc2.getStart();
            if(p1 < p2){
                return -1;
            }else if(p1 > p2){
                return 1;
            }else{
                return 0;
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
