/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.oeaw.cemm.bsf.vcffilter.filter;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import java.util.Iterator;

/**
 *
 * @author hmueller
 */
public class FormatFloatNumberFilter extends FormatNumberFilter{
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
    
    
    /**
    * Creates new FormatIntegerNumberFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFloatNumberFilter(VCFFormatHeaderLine header) {
        super(header);
        criterion1.setToolTipText("<1.0");
        criterion2.setToolTipText(">1.0");
        criterion3.setToolTipText("=1.0");
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
            //System.out.println("FormatIntegerNumberFilter " + attstring);
            Double attribute = parseNumber(attstring);
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
    protected boolean testPredicate(Double attribute, String operator, Double predicate){
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
        //System.out.println(predicate1);
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
    * Parses the provided String as an Integer.
    * 
    * @param number String to be parsed as number
    * @return Integer
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
