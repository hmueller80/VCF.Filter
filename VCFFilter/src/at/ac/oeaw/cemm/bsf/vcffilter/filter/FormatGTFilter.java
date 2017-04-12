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

/** 
 * Filter for FORMAT GT field.
 * FormatGTFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatGTFilter extends FormatFilter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    String predicate1 = "";
    String predicate2 = "";
    String predicate3 = "";
    
    /**
    * Creates new FormatGTFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatGTFilter(VCFFormatHeaderLine header){
        super(header);   
        criterion1.setToolTipText("A/C");
        criterion2.setToolTipText("*");
        criterion3.setToolTipText("A/*");
    }
    
    public boolean passesFilter(VariantContext vc){
        //String attribute = vc.getGenotypes().toString(); 
        GenotypesContext gtc = vc.getGenotypes();
        Iterator<Genotype> it = gtc.iterator();        
        while(it.hasNext()){
            String attribute = it.next().getGenotypeString();
            //System.out.println(attribute);
            if(attribute == null){
                return false;
            }        
            if(testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3)){                
                return true;
            }
        }
        return false; 
              
    }
    
    /**
    * Tests if the attribute matches the predicate, which may contain a wild card (*) character.
    * 
    * @param attribute an attribute to be tested
    * @param predicate the predicate desired
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
            //System.out.println(end);
            if(attribute.contains(start) && attribute.contains(end)){
                return true;
            }
        }else{
            return false;
        }
        return false;
    }
    
    public void setPredicate1(){
        this.predicate1 = criterion1.getText().toUpperCase();
        //System.out.println(predicate1);
    }
    
    public void setPredicate2(){
        this.predicate2 = criterion2.getText().toUpperCase();
        //System.out.println(predicate2);
    }
    
    public void setPredicate3(){
        this.predicate3 = criterion3.getText().toUpperCase();
        //System.out.println(predicate3);
    }
    
    
}
