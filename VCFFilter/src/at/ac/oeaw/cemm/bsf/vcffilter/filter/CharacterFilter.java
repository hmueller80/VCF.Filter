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
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/** 
 * Filters for character fields.
 * CharacterFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class CharacterFilter extends Filter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    Character predicate1 = null;
    Character predicate2 = null;
    Character predicate3 = null;
    
    /*
    public CharacterFilter(String vcfheaderline){
        super(vcfheaderline);   
        criterion1.setToolTipText("C");
        criterion2.setToolTipText("C");
        criterion3.setToolTipText("C");
    }
    */
    
    /**
     * Creates new CharacterFilter.
     *
     * @param header VCF headerline object
     * @author Heiko Müller
     * @since 1.0
     */
    public CharacterFilter(VCFInfoHeaderLine header){
        super(header);   
        criterion1.setToolTipText("C");
        criterion2.setToolTipText("C");
        criterion3.setToolTipText("C");
    }
    
    public boolean passesFilter(VariantContext vc){
        Object o = vc.getAttribute(ID);
        if(o == null){
            return false;
        }
        Character attribute = o.toString().charAt(0);
        if(testPredicate(attribute, predicate1) || testPredicate(attribute, predicate2) || testPredicate(attribute, predicate3)){
            return true;
        }else{
            return false;
        }        
    }
    
    private boolean testPredicate(Character attribute, Character predicate){
        if(predicate == null){
            return false;
        }
        
        if(attribute.equals(predicate)){
            return true;
        }else{
            return false;
        }
    }
    
    public void setPredicate1(){
        String temp = criterion1.getText();
        if(temp.length() > 0){
            this.predicate1 = criterion1.getText().charAt(0);
        }else{
            this.predicate1 = null;
        }
        //System.out.println(predicate1);
    }
    
    public void setPredicate2(){
        String temp = criterion2.getText();
        if(temp.length() > 0){
            this.predicate2 = criterion2.getText().charAt(0);
        }else{
            this.predicate2 = null;
        }
    }
    
    public void setPredicate3(){
        String temp = criterion3.getText();
        if(temp.length() > 0){
            this.predicate3 = criterion3.getText().charAt(0);
        }else{
            this.predicate3 = null;
        }
    }
    
    /**
     * Returns first Character of input String.
     *
     * @param input input String
     * @return Character
     * @author Heiko Müller
     * @since 1.0
     */
    protected Character parseCharacter(String input) {
        if(input == null || input.length() == 0){
            return null;
        }
        try {
            return input.charAt(0);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return null;
    }
}
