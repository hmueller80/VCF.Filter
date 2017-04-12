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
import java.util.List;

/**
 *
 * @author hmueller
 */
public class FormatFloatArrayFilter extends FormatArrayFilter{
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
    * Creates new FormatIntegerArrayFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFloatArrayFilter(VCFFormatHeaderLine header){
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
            attstring = parseArrayString(attstring);
            String[] attributes = attstring.split(",");
            if(!arraycontainsref){                
                List<Integer> indices = getAlleleIndices(vc, gt);                
                for(int i : indices){
                    if(i < attributes.length){                        
                        Double attribute = parseValue(attributes[i]);
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
        }     
        return false;
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
        predicate1 = parseValue(x);
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
        predicate2 = parseValue(x);
        
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
        predicate3 = parseValue(x);
        
        if(!isOperator(operator3)){
            operator3 = Filter.larger;
            predicate3 = Double.MAX_VALUE;
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
    protected Double parseValue(String value){
        value = value.trim();
        if(value.equals("") || value.equals(".")){
            return null;
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
}
