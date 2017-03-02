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

/** 
 * Stores default settings of Filters.
 * FilterDefaults.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FilterDefaults {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    String ID;
    String criterion1 = "";
    String criterion2 = "";
    String criterion3 = "";
    String operator1 = "";
    String operator2 = "";
    String operator3 = "";
    boolean numeric = false;
    boolean andnot = false;
    
    /**
    * Creates new FilterDefaults.
    * 
    * @param id ID of the annotation
    * @param andnot whether filter is exclusive/inclusive
    * @param criterion1 text to be set in criterion1 field
    * @param criterion2 text to be set in criterion2 field
    * @param criterion3 text to be set in criterion3 field
    * @param operator1 operator to be set in criterion1 field
    * @param operator2 operator to be set in criterion2 field
    * @param operator3 operator to be set in criterion3 field
    * @param numeric whether filter is numeric
    * @author Heiko Müller
    * @since 1.0
    */
    public FilterDefaults(String id, boolean andnot, String criterion1, String criterion2, String criterion3, String operator1, String operator2, String operator3, boolean numeric){
        this.ID = id;
        this.andnot = andnot;
        this.criterion1 = criterion1;
        this.criterion2 = criterion2;
        this.criterion3 = criterion3;
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.operator3 = operator3;
        this.numeric = numeric;
        testOperators();
        
    }
    
    /**
    * Creates new FilterDefaults.
    * 
    * @param id ID of the annotation
    * @param andnot whether filter is exclusive/inclusive
    * @param criterion1 text to be set in criterion1 field
    * @param criterion2 text to be set in criterion1 field
    * @param criterion3 text to be set in criterion1 field
    * @author Heiko Müller
    * @since 1.0
    */
    public FilterDefaults(String id, boolean andnot, String criterion1, String criterion2, String criterion3){
        this.ID = id;
        this.andnot = andnot;
        setValues1(criterion1);
        setValues2(criterion2);
        setValues3(criterion3);
        //this.criterion1 = criterion1;
        //this.criterion2 = criterion2;
        //this.criterion3 = criterion3;
        //this.operator1 = "";
        //this.operator2 = "";
        //this.operator3 = "";
        this.numeric = false;
        
    }
    
    private void testOperators(){
        if(criterion1.startsWith("<") || criterion1.startsWith("=") || criterion1.startsWith(">")){
            setValues1(criterion1);
        }
        if(criterion2.startsWith("<") || criterion2.startsWith("=") || criterion2.startsWith(">")){
            setValues2(criterion2);
        }
        if(criterion3.startsWith("<") || criterion3.startsWith("=") || criterion3.startsWith(">")){
            setValues3(criterion3);
        }
    }
    
    /**
    * Creates new FilterDefaults.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public FilterDefaults(){
    
    }

    /**
    * Returns filter id.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getID() {
        return ID;
    }

    /**
    * Sets filter id.
    * 
    * @param ID ID of annotation
    * @author Heiko Müller
    * @since 1.0
    */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
    * Returns criterion1.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion1() {
        return criterion1;
    }

    /**
    * Sets criterion1.
    * 
    * @param criterion1 criterion written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setCriterion1(String criterion1) {        
        this.criterion1 = criterion1;
    }

    /**
    * Returns criterion2.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion2() {
        return criterion2;
    }

    /**
    * Sets criterion2.
    * 
    * @param criterion2 criterion written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setCriterion2(String criterion2) {
        this.criterion2 = criterion2;
    }

    /**
    * Returns criterion3.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion3() {
        return criterion3;
    }

    /**
    * Sets criterion3.
    * 
    * @param criterion3 criterion written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setCriterion3(String criterion3) {
        this.criterion3 = criterion3;
    }

    /**
    * Returns operator1.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getOperator1() {
        return operator1;
    }

    /**
    * Sets operator1.
    * 
    * @param operator1 operator written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setOperator1(String operator1) {
        this.operator1 = operator1;
    }

    /**
    * Returns operator2.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getOperator2() {
        return operator2;
    }

    /**
    * Sets operator2.
    * 
    * @param operator2 operator written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setOperator2(String operator2) {
        this.operator2 = operator2;
    }

    /**
    * Returns operator3.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getOperator3() {
        return operator3;
    }

    /**
    * Sets operator3.
    * 
    * @param operator3 operator written in text field
    * @author Heiko Müller
    * @since 1.0
    */
    public void setOperator3(String operator3) {
        this.operator3 = operator3;
    }

    /**
    * Returns the numeric filter indicator.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean isNumeric() {
        return numeric;
    }

    /**
    * Sets the numeric filter indicator.
    * 
    * @param numeric whether annotation is numeric
    * @author Heiko Müller
    * @since 1.0
    */
    public void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }

    /**
    * Returns the and not indicator.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean isAndnot() {
        return andnot;
    }

    /**
    * Sets the and not indicator.
    * 
    * @param andnot whether filter is exclusive/inclusive
    * @author Heiko Müller
    * @since 1.0
    */
    public void setAndnot(boolean andnot) {
        this.andnot = andnot;
    }
    
    /**
    * Sets the criterion and operator variables of this Filter according to Filter f.
    * 
    * @param f Filter
    * @author Heiko Müller
    * @since 1.0
    */
    public void set(Filter f){
        setValues1(f.getCriterion1().getText());
        setValues2(f.getCriterion2().getText());
        setValues3(f.getCriterion3().getText());        
        andnot = f.getAndNot();
    }
    
    /**
    * Sets criterion1 and operator1 as provided by s.
    * 
    * @param s
    * @author Heiko Müller
    * @since 1.0
    */
    private void setValues1(String s){
        if(s.startsWith("<") || s.startsWith("=") || s.startsWith(">")){
            if(s.length() > 1){                
                criterion1 = s.substring(1);
                operator1 = s.substring(0, 1);
            }else{
                criterion1 = "";
                operator1 = "";
            }
        }else{
            criterion1 = s;
            operator1 = "";
        }
    }
    
    /**
    * Sets criterion2 and operator2 as provided by s.
    * 
    * @param s
    * @author Heiko Müller
    * @since 1.0
    */
    private void setValues2(String s){
        if(s.startsWith("<") || s.startsWith("=") || s.startsWith(">")){
            if(s.length() > 1){                
                criterion2 = s.substring(1);
                operator2 = s.substring(0, 1);
            }else{
                criterion2 = "";
                operator2 = "";
            }
        }else{
            criterion2 = s;
            operator2 = "";
        }
    }
    
    /**
    * Sets criterion3 and operator3 as provided by s.
    * 
    * @param s
    * @author Heiko Müller
    * @since 1.0
    */
    private void setValues3(String s){
        if(s.startsWith("<") || s.startsWith("=") || s.startsWith(">")){
            if(s.length() > 1){                
                criterion3 = s.substring(1);
                operator3 = s.substring(0, 1);
            }else{
                criterion3 = "";
                operator3 = "";
            }
        }else{
            criterion3 = s;
            operator3 = "";
        }
    }
    
    /**
    * Returns a String representation of this filter.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String dump(){
        return andnot + " " + operator1 + " " + criterion1 + " " + operator2 + " " + criterion2 +  " " + operator3 + " " + criterion3;
    }
    
}
