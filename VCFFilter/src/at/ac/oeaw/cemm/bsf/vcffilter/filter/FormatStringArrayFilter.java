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

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** 
 * Filter for FORMAT fields of type String array.
 * FormatStringArrayFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatStringArrayFilter extends FormatArrayFilter{
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    String predicate1 = "";
    String predicate2 = "";
    String predicate3 = "";
    
    /**
    * Parses FormatStringArrayFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatStringArrayFilter(VCFFormatHeaderLine header){
        super(header);   
        criterion1.setToolTipText("*");
        criterion2.setToolTipText("A*c");
        criterion3.setToolTipText("ABC");
    }
    
    public boolean passesFilter(VariantContext vc) {    
        GenotypesContext gtc = vc.getGenotypes();
        Iterator<Genotype> it = gtc.iterator();        
        while(it.hasNext()){ 
            Genotype gt = it.next();
            Object o = gt.getAnyAttribute(ID);
            if(o == null){
                return false;
            }              
            String attstring = o.toString();
            attstring = parseArrayString(attstring);
            String[] attributes = attstring.split(",");
            if(!arraycontainsref){                
                List<Integer> indices = getAlleleIndices(vc, gt);                
                for(int i : indices){
                    if(i < attributes.length){                        
                        String attribute = parseValue(attributes[i]);
                        boolean match = testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3);
                        if(match){
                            return true;
                        }
                    }
                }
            }else{                        
                List<Integer> indices = this.getAlleleIndicesIncludingRefAllele(vc, gt);                
                for(int i : indices){
                    if(i < attributes.length){      
                        String attribute = parseValue(attributes[i]);
                        boolean match = testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3);
                        if(match){
                            return true;
                        }
                    }
                }
            }        
        }     
        return false;
    }
    
    private boolean testPredicate(String attribute, String predicate){
        if(predicate.trim().equals("")){
            return false;
        }
        
        if(attribute.equals(predicate)){
            return true;
        }else{
            return false;
        }
    }
    
    public void setPredicate1(){
        this.predicate1 = criterion1.getText().toUpperCase();
    }
    
    public void setPredicate2(){
        this.predicate2 = criterion2.getText().toUpperCase();
    }
    
    public void setPredicate3(){
        this.predicate3 = criterion3.getText().toUpperCase();
    }
    
    /**
    * Returns the trimmed toUpper() version of the provided value.
    * 
    * @param value String to be trimmed and capitalized
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    protected String parseValue(String value){        
        return value.trim().toUpperCase();
    }  
}
