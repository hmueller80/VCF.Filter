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
package at.ac.oeaw.cemm.bsf.vcffilter.outputformat;

import at.ac.oeaw.cemm.bsf.vcffilter.excelparser.VariantRecurrence;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/** 
 * Formats recurrence column output.
 * FormatFieldRecurrence.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */

public class FormatFieldRecurrence extends FormatFields{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
    * Creates new FormatFieldRecurrence.
    * 
    * @param id VCF header line ID
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFieldRecurrence(String id) {
        super(id);
    }
    
    @Override
    public String getOutput(VariantContext vc) {        
        return "\t\t\t";        
    }
    
    @Override
    public String getHeader() {
        return "recurrence total\trecurrence het\trecurrence hom" + "\t";
    }
    
    /**
    * Returns the VariantRecurrence as a String.
    * 
    * @param vc variant
    * @param recurrenceHash recurrence hash
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getOutput(VariantContext vc, Hashtable<String, VariantRecurrence> recurrenceHash) {
        if(recurrenceHash == null){
            return "\t\t\t";
        }
        List<String> keys = getExacVariantFormat(vc);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < keys.size(); i++){
            VariantRecurrence r = recurrenceHash.get(keys.get(i));
            if(r != null){
                sb.append(r.dumpSeparate());
            }
        }  
        sb.append("\t");
        return sb.toString();        
    }
    
    private List<String> getExacVariantFormat(VariantContext vc){
        Genotype g = vc.getGenotype(0);
        List<Allele> al = g.getAlleles();
        ArrayList<String> result = new ArrayList<String>();
        String base1 = al.get(0).getBaseString();
        String base2 = al.get(1).getBaseString();
        String ref = vc.getReference().getBaseString();
        String c = vc.getContig();
        int p = vc.getStart();        
            if(!base1.equals(ref)){
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base1);
            }
            if(!base2.equals(ref) && !base2.equals(base1)){
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base2);
            }
        return result;
    }
}
