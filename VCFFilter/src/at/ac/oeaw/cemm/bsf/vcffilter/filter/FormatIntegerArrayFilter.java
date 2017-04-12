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
 * abstract Filter for FORMAT fields of type Integer array.
 * FormatIntegerArrayFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatIntegerArrayFilter extends FormatArrayFilter{
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    Integer predicate1 = Integer.MAX_VALUE;
    Integer predicate2 = Integer.MAX_VALUE;
    Integer predicate3 = Integer.MAX_VALUE;
    String operator1 = ">";
    String operator2 = ">";
    String operator3 = ">";    
    
    
    /**
    * Creates new FormatIntegerArrayFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatIntegerArrayFilter(VCFFormatHeaderLine header){
        super(header); 
        criterion1.setToolTipText("<1");
        criterion2.setToolTipText(">1");
        criterion3.setToolTipText("=1");
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
                        Integer attribute = parseValue(attributes[i]);
                        if(attribute != null){
                            boolean match = testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3);
                            if(match){
                                return true;
                            }
                        }
                    }
                }
            }else{                        
                List<Integer> indices = this.getAlleleIndicesIncludingRefAllele(vc, gt);                
                for(int i : indices){
                    if(i < attributes.length){      
                        Integer attribute = parseValue(attributes[i]);
                        if(attribute != null){
                            boolean match = testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3);
                            if(match){
                                return true;
                            }
                        }
                    }
                }
            }        
        }     
        return false;
    }
    
    private boolean testPredicate(Integer attribute, String operator, Integer predicate){
        if(operator.equals("<")){
            if(attribute.intValue() < predicate.intValue()){
                return true;
            }else{
                return false;
            }
        }
        if(operator.equals("=")){
            if(attribute.intValue() == predicate.intValue()){
                return true;
            }else{
                return false;
            }
        }
        if(operator.equals(">")){
            if(attribute.intValue() > predicate.intValue()){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    
    public void setPredicate1() {
        String c = criterion1.getText();
        if (c.length() == 0) {
            operator1 = Filter.larger;
            predicate1 = Integer.MAX_VALUE;
            return;
        }

        operator1 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate1 = parseValue(x);
        //System.out.println(predicate1);
        if(!isOperator(operator1)){
            operator1 = Filter.larger;
            predicate1 = Integer.MAX_VALUE;
        }
    }

    public void setPredicate2() {
        String c = criterion2.getText();
        if (c.length() == 0) {
            operator2 = Filter.larger;
            predicate2 = Integer.MAX_VALUE;
            return;
        }

        operator2 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate2 = parseValue(x);
        
        if(!isOperator(operator2)){
            operator2 = Filter.larger;
            predicate2 = Integer.MAX_VALUE;
        }
    }

    public void setPredicate3() {
        String c = criterion3.getText();
        if (c.length() == 0) {
            operator3 = Filter.larger;
            predicate3 = Integer.MAX_VALUE;
            return;
        }

        operator3 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate3 = parseValue(x);
        
        if(!isOperator(operator3)){
            operator3 = Filter.larger;
            predicate3 = Integer.MAX_VALUE;
        }
    }
    
    /**
    * Attempts to parse the provided value, which can be "" or "." as an Integer.
    * 
    * @param value String to be parsed
    * @return Integer
    * @author Heiko Müller
    * @since 1.0
    */
    protected Integer parseValue(String value){
        value = value.trim();
        if(value.equals("") || value.equals(".")){
            return null;
        }
        int result = 0;
        try{
            result = Integer.parseInt(value);
        }catch(NumberFormatException nfe){
            //System.out.println("unparsable value " + value);
            nfe.printStackTrace();
        }
        return result;
    } 
}
