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
 * Filters for default QUAL field (Phred scaled quality).
 * QualFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class QualFilter extends DoubleNumberFilter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
    * Creates new QualFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public QualFilter(VCFCompoundHeaderLine header){
        super(header);   
        criterion1.setToolTipText(">1000");
        criterion2.setToolTipText("=1000");
        criterion3.setToolTipText("");
    }
    
    public boolean passesFilter(VariantContext vc) {
        Object o = vc.getPhredScaledQual();
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
}
