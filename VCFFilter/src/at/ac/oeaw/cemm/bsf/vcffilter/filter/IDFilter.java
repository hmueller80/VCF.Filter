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
import htsjdk.variant.vcf.VCFCompoundHeaderLine;

/** 
 * Filters for default ID field (dbSNP rs12345).
 * IDFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class IDFilter extends StringFilter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
     
    /**
    * Creates new IDFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public IDFilter(VCFCompoundHeaderLine header){
        super(header);   
        criterion1.setToolTipText("rs*");
        criterion2.setToolTipText("rs12345");
        criterion3.setToolTipText("*");
    }
    
    public boolean passesFilter(VariantContext vc){
        Object o = vc.getID();
        if(o == null){
            return false;
        }
        String attribute = o.toString().toUpperCase();        
        if(testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3)){
            return true;
        }else{
            return false;
        }        
    }
    
    /**
    * Tests if attribute matches predicate, which may contain a wild card (*) character.
    * 
    * @param attribute attribute to be tested
    * @param predicate desired predicate
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    protected boolean testPredicate(String attribute, String predicate){   
        if(predicate.trim().equals("")){
            return false;
        }
        
        if(attribute.equals(predicate)){
            return true;
        }else if(predicate.equals("*")){
            if(!attribute.equals("")){
                return true;
            }else{
                return false;
            }            
        }else if(predicate.contains("*")){
            String start = predicate.substring(0, predicate.indexOf("*"));
            String end = predicate.substring(predicate.indexOf("*") + 1, predicate.length());
            if(attribute.contains(start) && attribute.contains(end)){
                return true;
            }
        }else{
            return false;
        }
        return false;
    }
    
}
