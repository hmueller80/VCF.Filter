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

import htsjdk.variant.vcf.VCFCompoundHeaderLine;

/** 
 * Abstract Filter for number fields.
 * NumberFilter.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class NumberFilter extends Filter{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
      
    /**
    * Parses NumberFilter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public NumberFilter(VCFCompoundHeaderLine header){
        super(header);  
        criterion1.setToolTipText("<1");
        criterion2.setToolTipText(">1");
        criterion3.setToolTipText("=1");
    }    
    
    /**
    * Removes all non-digit characters from the provided String.
    * 
    * @param s String to be cleared from non digits
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    protected String removeNonDigits(String s){
        //System.out.println("remove digits from " + s);
        char[] c = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(Character ch : c){
            if(Character.isDigit(ch) || ch == '.' || ch == ',' || ch == '-'){
                sb.append(ch);
            }
        }
        String result = sb.toString().trim();
        if(result.equals(".") || result.equals(",") || result.equals("-") || result.equals("")){
            result = "0.0";
        }
        //System.out.println("digits removed " + result);
        return result;
    }
    
        
    
    /**
    * Parses provided String as a Number.
    * 
    * @param number String to be parsed as number
    * @return Number
    * @author Heiko Müller
    * @since 1.0
    */
    protected abstract Number parseNumber(String number);
    
}
