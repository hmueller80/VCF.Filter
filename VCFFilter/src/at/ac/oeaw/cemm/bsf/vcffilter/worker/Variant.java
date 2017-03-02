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
package at.ac.oeaw.cemm.bsf.vcffilter.worker;

import java.util.ArrayList;

/**
 * Utility for parsing ExAC coordinates.
 * 
 * Variant.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Variant {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    String chr;
    int start;
    String ref;
    String alt;

    /**
    * Creates new Variant.
    * 
    * @param exac variant in ExAC notation (chr-pos-ref-alt)
    * @author Heiko Müller
    * @since 1.0
    */
    public Variant(String exac) {
        if (exac.length() == 0) {
            return;
        }
        String[] temp = parseExac(exac);
        if (temp != null) {
            chr = temp[0].trim();
            start = Integer.parseInt(temp[1]);
            ref = temp[2].trim();
            alt = temp[3].trim();
        }

    }

    private String[] parseExac(String s) {
        String[] result = s.split("-");
        if (result.length == 4) {
            return result;
        } else {
            return null;
        }
    }
    
    

}
