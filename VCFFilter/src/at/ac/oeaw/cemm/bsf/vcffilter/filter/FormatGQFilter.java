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
 * Filter for FORMAT GQ field.
 * FormatGQFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatGQFilter extends FormatFilter{
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
    * Creates new FormatGQFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatGQFilter(VCFFormatHeaderLine header){
        super(header);   
        criterion1.setToolTipText("=99");
        criterion2.setToolTipText(">50");
        criterion3.setToolTipText("=0");
    }
    
    public boolean passesFilter(VariantContext vc){
        //String attribute = vc.getGenotypes().toString(); 
        GenotypesContext gtc = vc.getGenotypes();
        Iterator<Genotype> it = gtc.iterator();        
        while(it.hasNext()){
            Integer attribute = it.next().getGQ();
            //System.out.println(attribute);
            if(attribute == null){
                return false;
            }        
            if(testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3)){
                return true;
            }
        }
        return false; 
              
    }
    
    /**
    * Tests whether the attribute matches the predicate given the operator.
    * 
    * @param attribute attribute to be tested
    * @param operator operator
    * @param predicate desired predicate
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    protected boolean testPredicate(Integer attribute, String operator, Integer predicate){
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
        predicate1 = parseNumber(x);
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
        predicate2 = parseNumber(x);
        
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
        predicate3 = parseNumber(x);
        
        if(!isOperator(operator3)){
            operator3 = Filter.larger;
            predicate3 = Integer.MAX_VALUE;
        }
    }

    /**
    * Parses the provided String as an Integer.
    * 
    * @param number String to be parsed as number
    * @return Integer
    * @author Heiko Müller
    * @since 1.0
    */
    protected Integer parseNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }
    
}
