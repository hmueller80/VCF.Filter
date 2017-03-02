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
 * Formats CosmicBeaconHyperlink column output.
 * FormatFieldCosmicBeaconHyperlink.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatFieldCosmicBeaconHyperlink extends FormatFields{
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
    * Creates new FormatFieldCosmicBeaconHyperlink.
    * 
    * @param id VCF header line ID
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFieldCosmicBeaconHyperlink(String id) {
        super(id);
    }
    
    //https://cancer.sanger.ac.uk/cosmic/beacon?dataset=cosmic&ref=39&chrom=7&allele=A&pos=140753336#
    @Override
    public String getOutput(VariantContext vc) {
        StringBuilder sb = new StringBuilder();
        if(vc.getNSamples() == 1){            
            List<Allele> variants = vc.getGenotype(0).getAlleles();
            if(variants != null && variants.size() == 2){
                if(vc.getGenotype(0).isHom()){
                    Allele x = variants.get(0);
                    sb.append("https://cancer.sanger.ac.uk/cosmic/beacon?dataset=cosmic&chrom=" + vc.getContig() + "&allele=" + x.getBaseString() + "&pos=" + vc.getStart() + "# ");
                }else if(vc.getGenotype(0).isHetNonRef()){
                    for (Allele a : variants) {
                        sb.append("https://cancer.sanger.ac.uk/cosmic/beacon?dataset=cosmic&chrom=" + vc.getContig() + "&allele=" + a.getBaseString() + "&pos=" + vc.getStart() + "# ");
                    }
                }else{
                    Allele x = variants.get(1);
                    sb.append("https://cancer.sanger.ac.uk/cosmic/beacon?dataset=cosmic&chrom=" + vc.getContig() + "&allele=" + x.getBaseString() + "&pos=" + vc.getStart() + "# ");
                }
            }
            sb.append("\t");
            return sb.toString();
        }else{
            sb.append("\t");
            return sb.toString();
        }
    }
}
