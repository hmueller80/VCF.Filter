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
 * Filters for real number fields.
 * DoubleNumberFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class DoubleNumberFilter extends NumberFilter {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    Double predicate1 = Double.MAX_VALUE;
    Double predicate2 = Double.MAX_VALUE;
    Double predicate3 = Double.MAX_VALUE;
    String operator1 = ">";
    String operator2 = ">";
    String operator3 = ">";

    /*
    public DoubleNumberFilter(String vcfheaderline) {
        super(vcfheaderline);
        criterion1.setToolTipText("<0.5");
        criterion2.setToolTipText(">0.5");
        criterion3.setToolTipText("=0.5");
    }
    */
    
    /**
    * Creates new DoubleNumberFilter.
    * 
    * @param header input file
    * @author Heiko Müller
    * @since 1.0
    */
    public DoubleNumberFilter(VCFCompoundHeaderLine header){
        super(header);  
        criterion1.setToolTipText("<0.5");
        criterion2.setToolTipText(">0.5");
        criterion3.setToolTipText("=0.5");
    }

    public boolean passesFilter(VariantContext vc) {
        Object o = vc.getAttribute(ID);
        if (o == null) {
            //System.out.println(ID + " not found");
            return false;
        }
        String attstring = o.toString();
        //System.out.println("attribute string: " + attstring);
        Double attribute = parseNumber(attstring);
        
        if(testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3)){
            return true;
        }else{
            return false;
        }
    }
    
    private boolean testPredicate(Double attribute, String operator, Double predicate){
        if(operator.equals("<")){
            if(attribute.doubleValue() < predicate.doubleValue()){
                return true;
            }else{
                return false;
            }
        }
        if(operator.equals("=")){
            if(attribute.doubleValue() == predicate.doubleValue()){
                return true;
            }else{
                return false;
            }
        }
        if(operator.equals(">")){
            if(attribute.doubleValue() > predicate.doubleValue()){
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
            predicate1 = Double.MAX_VALUE;
            return;
        }

        operator1 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate1 = parseNumber(x);
        
        if(!isOperator(operator1)){
            operator1 = Filter.larger;
            predicate1 = Double.MAX_VALUE;
        }
    }

    public void setPredicate2() {
        String c = criterion2.getText();
        if (c.length() == 0) {
            operator2 = Filter.larger;
            predicate2 = Double.MAX_VALUE;
            return;
        }

        operator2 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate2 = parseNumber(x);
        
        if(!isOperator(operator2)){
            operator2 = Filter.larger;
            predicate2 = Double.MAX_VALUE;
        }
    }

    public void setPredicate3() {
        String c = criterion3.getText();
        if (c.length() == 0) {
            operator3 = Filter.larger;
            predicate3 = Double.MAX_VALUE;
            return;
        }

        operator3 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate3 = parseNumber(x);
        
        if(!isOperator(operator3)){
            operator3 = Filter.larger;
            predicate3 = Double.MAX_VALUE;
        }
    }

    /**
    * Attempts to parse provided String as a Double.
    * 
    * @param number String to be parsed as number
    * @return Double
    * @author Heiko Müller
    * @since 1.0
    */
    protected Double parseNumber(String number) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }

}
