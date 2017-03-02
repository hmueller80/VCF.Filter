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

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.List;

/** 
 * Formats ExAC hyperlink output.
 * FormatFieldExacHyperlink.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatFieldExacHyperlink extends FormatFields {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
    * Creates new FormatFieldExacHyperlink.
    * 
    * @param id VCF header line ID
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFieldExacHyperlink(String id) {
        super(id);
    }

    @Override
    public String getOutput(VariantContext vc) {
        StringBuilder sb = new StringBuilder();
        List<String> variants = getExacVariantFormat(vc);
        if(variants != null){
            for (String s : variants) {
                sb.append("http://exac.broadinstitute.org/variant/" + s + " ");
            }
        }
        sb.append("\t");
        return sb.toString();
    }

    /**
    * Returns the variants of the first sample genotype in Exac format.
    * 
    * @param vc variant
    * @return List&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    protected List<String> getExacVariantFormat(VariantContext vc) {
        ArrayList<String> result = new ArrayList<String>();
        if(vc.hasGenotypes() && vc.getSampleNames().size() == 1){
            Genotype g = vc.getGenotype(0);
            List<Allele> al = g.getAlleles();            
            String base1 = al.get(0).getBaseString();
            String base2 = al.get(1).getBaseString();
            String ref = vc.getReference().getBaseString();
            String c = vc.getContig();
            int p = vc.getStart();
            if (!base1.equals(ref)) {
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base1);
            }
            if (!base2.equals(ref) && !base2.equals(base1)) {
                //result.add(c + "-" + p + "-" + ref + "-" + base1);
                result.add(c + "-" + p + "-" + ref + "-" + base2);
            }            
        }else{
            result.add("");
        }
        return result;
    }
}
