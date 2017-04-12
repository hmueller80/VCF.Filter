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
import java.util.List;
import java.util.Set;

/**
 * Filter for dbSNP common allele frequency. This is a special filter as it is numeric but allows for
 * String type filter like searches without an operator. This feature is used to filter variants where
 * the common allele frequency is not defined (NA).
 * 
 * CAFFilter.java 04 OCT 2016 
 *
 * 
 * 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class CAFFilter extends ArrayFilter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * Predicate 1.
     */
    Double predicate1 = Double.MAX_VALUE;
    
    /**
     * Predicate 2.
     */
    Double predicate2 = Double.MAX_VALUE;
    
    /**
     * Predicate 3.
     */
    Double predicate3 = Double.MAX_VALUE;
    
    /**
     * Operator 1.
     */
    String operator1 = ">";
    
    /**
     * Operator 2.
     */
    String operator2 = ">";
    
    /**
     * Operator 3.
     */
    String operator3 = ">";
    
    /**
     * Indicator whether filtering for non defined values (missing or .) should be performed.
     */
    boolean hasNAFilterOn = false;
    
    /*
    public CAFFilter(String vcfheaderline){
        super(vcfheaderline);  
        criterion1.setToolTipText("<0.5");
        criterion2.setToolTipText(">0.5");
        criterion3.setToolTipText("NA");
    }
    */
        
    /**
     * Initializes new CAFFilter.
     *
     * @param header VCF headerline object
     * @author Heiko Müller
     * @since 1.0
     */
    public CAFFilter(VCFCompoundHeaderLine header){
        super(header);  
        criterion1.setToolTipText("<0.5");
        criterion2.setToolTipText(">0.5");
        criterion3.setToolTipText("NA");
    }
    
    /**
     * Tests whether variant fulfills search criteria.
     *
     * @param vc the variant being tested
     * @return boolean true if variant passes the filter, false otherwise
     * @author Heiko Müller
     * @since 1.0
     */
    @Override
    public boolean passesFilter(VariantContext vc) {        
        Object o = vc.getAttribute(ID);
        if (o == null) {
            if(testNAFilterIsOn()){
                return true;
            }else{
                return false;
            }
        }
        String attstring = o.toString();
        attstring = parseArrayString(attstring);
        String[] attributes = attstring.split(",");
        Set<String> samples = vc.getSampleNames();
        if(vc.getAlleles().size() != attributes.length){
           if(testNAFilterIsOn()){
                return true;
            }else{
                return false;
            }
        }
        
        if(!arraycontainsref){
            for(String sample : samples){            
                List<Integer> indices = getAlleleIndices(vc, sample);                
                for(int i : indices){
                    if(i < attributes.length){ 
                        if(attributes[i].trim().equals(".") && hasNAFilterOn){
                            return true;
                        }
                        Double attribute = parseValue(attributes[i]);
                        if(attribute != null){
                            boolean match = testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3);
                            if(match){
                                return true;
                            }
                        }
                    }
                }
            } 
        } else{
            for(String sample : samples){            
                List<Integer> indices = this.getAlleleIndicesIncludingRefAllele(vc, sample);                
                for(int i : indices){
                    if(i > -1 && i < attributes.length){   
                        if(attributes[i].trim().equals(".") && hasNAFilterOn){
                            return true;
                        }
                        Double attribute = parseValue(attributes[i]);
                        if(attribute != null){
                            boolean match = testPredicate(attribute, operator1, predicate1) || testPredicate(attribute, operator2, predicate2) || testPredicate(attribute, operator3, predicate3);
                            if(match){
                                return true;
                            }
                        }
                    }else{
                        //return false;
                    }
                }
            }
        }     
        return false;
    }
    
    /**
     * Tests passing of numeric attribute depending on operator and predicate.
     *
     * @param attribute the allele specific common allele frequency
     * @param operator as typed in criteria
     * @param predicate as typed in criteria
     * @return boolean true if variant passes the test, false otherwise
     * @author Heiko Müller
     * @since 1.0
     */
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

    /**
     * Called upon focus loss of the corresponding criterion text field 
     * to parse values, set predicate1, and operator1.
     *
     * @author Heiko Müller
     * @since 1.0
     */
    @Override
    public void setPredicate1() {
        String c = criterion1.getText();
        if (c.length() == 0) {
            operator1 = Filter.larger;
            predicate1 = Double.MAX_VALUE;
            return;
        }

        operator1 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate1 = parseValue(x);
        
        if(!isOperator(operator1)){
            operator1 = Filter.larger;
            predicate1 = Double.MAX_VALUE;
        }
        this.hasNAFilterOn =  testNAFilterIsOn();
    }

    /**
     * Called upon focus loss of the corresponding criterion text field 
     * to parse values, set predicate2, and operator2.
     *
     * @author Heiko Müller
     * @since 1.0
     */
    @Override
    public void setPredicate2() {
        String c = criterion2.getText();
        if (c.length() == 0) {
            operator2 = Filter.larger;
            predicate2 = Double.MAX_VALUE;
            return;
        }

        operator2 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate2 = parseValue(x);
        
        if(!isOperator(operator2)){
            operator2 = Filter.larger;
            predicate2 = Double.MAX_VALUE;
        }
        this.hasNAFilterOn =  testNAFilterIsOn();
    }

    /**
     * Called upon focus loss of the corresponding criterion text field 
     * to parse values, set predicate3, and operator3.
     *
     * @author Heiko Müller
     * @since 1.0
     */
    @Override
    public void setPredicate3() {
        String c = criterion3.getText();
        if (c.length() == 0) {
            operator3 = Filter.larger;
            predicate3 = Double.MAX_VALUE;
            return;
        }

        operator3 = parseOperator(c);
        String x = c.substring(1).trim();
        predicate3 = parseValue(x);
        
        if(!isOperator(operator3)){
            operator3 = Filter.larger;
            predicate3 = Double.MAX_VALUE;
        }
        this.hasNAFilterOn =  testNAFilterIsOn();
                            
                        
    }
    
    /**
     * Parses the value typed in criterion text field into a Double value.
     *
     * @param value as typed in criteria
     * @return Double the parsed value
     * @author Heiko Müller
     * @since 1.0
     */
    protected Double parseValue(String value){
        value = value.trim();
        if(value.equals("") || value.equals(".")){
            return null;
        }  
        if(value.equals("A")){
            return Double.MAX_VALUE;
        }  
        double result = 0;
        try{
            result = Double.parseDouble(value);
        }catch(NumberFormatException nfe){
            //System.out.println("unparsable value " + value);
            nfe.printStackTrace();
        }
        return result;
    } 
    
    /**
     * Parses the operator typed in criterion text field.
     *
     * @param criterion as typed in criteria
     * @return String Filter.smaller, Filter.equal, or Filter.larger
     * @author Heiko Müller
     * @since 1.0
     */
    protected String parseOperator(String criterion){
        //System.out.println("remove digits from " + s);
        if(criterion.startsWith("<")){
            return Filter.smaller;
        }else if(criterion.startsWith("=")){
            return Filter.equal;
        }else if(criterion.startsWith(">")){
            return Filter.larger;
        }else if(criterion.startsWith("NA") || criterion.startsWith("na")){
            return Filter.larger;
        }
        return "";
    }
    
    /**
     * Tests if any of the criteria equals NA.
     *
     * @return boolean true if NA, Na, nA, or na is found, false otherwise.
     * @author Heiko Müller
     * @since 1.0
     */
    private boolean testNAFilterIsOn(){
        return  (criterion1.getText().toUpperCase().equals("NA") || criterion2.getText().toUpperCase().equals("NA") || criterion3.getText().toUpperCase().equals("NA"));
    }
}
