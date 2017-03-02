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
package at.ac.oeaw.cemm.bsf.vcffilter.filter;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/** 
 * Filters for chromosomes (contigs).
 * ChromosomeFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class ChromosomeFilter extends StringFilter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /*
    public ChromosomeFilter(String vcfheaderline){
        super(vcfheaderline);
    }
    */
    
    /**
     * Creates new ChromosomeFilter.
     *
     * @param header VCF headerline object
     * @author Heiko Müller
     * @since 1.0
     */
    public ChromosomeFilter(VCFInfoHeaderLine header){
        super(header);
        criterion1.setToolTipText("3");
        criterion2.setToolTipText("X");
        criterion2.setToolTipText("GL*");
    }
    
   @Override
    public boolean passesFilter(VariantContext vc){
        Object o = vc.getContig();
        if(o == null){
            return false;
        }else{
            String attribute = o.toString().toUpperCase();
            if(testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3)){
                return true;
            }else{
                return false;
            }  
        }      
    }
    
    /**
     * Tests whether the attribute matches predicate, which may contain a wild card (*) character.
     *
     * @param attribute an attribute to be tested
     * @param predicate the predicate desired
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    @Override
    protected boolean testPredicate(String attribute, String predicate){
        if(predicate.trim().equals("")){
            return false;
        }
        
        if(predicate.toUpperCase().startsWith("CHR") && !attribute.toUpperCase().startsWith("CHR")){
            predicate = predicate.substring(3);
        }
        
        if(attribute.equals(predicate)){
            return true;
        }else if(predicate.contains("*")){
            String substring = predicate.substring(0, predicate.indexOf("*"));
            if(attribute.startsWith(substring)){
                return true;
            }else{
                return false;
            }            
        }else{
            return false;
        }
    }
    
}
