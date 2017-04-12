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

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/** 
 * Formats Genotype output.
 * FormatFieldGenotype.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatFieldGenotype extends FormatFields {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
    * Creates new FormatFieldGenotype.
    * 
    * @param id VCF header line ID
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFieldGenotype(String id) {
        super(id);
    }

    @Override
    public String getHeader() {
        return "sample\tgenotype\talleleIndices\tDP\tGQ\tAD\tPL\t";
    }

    @Override
    public String getOutput(VariantContext vc) {
        StringBuilder sb = new StringBuilder();
        if(vc.getSampleNames().size() == 1){
            if(vc.hasGenotypes()){

                sb.append(vc.getGenotype(0).getSampleName());
                sb.append("\t");
                sb.append(vc.getGenotype(0).getGenotypeString());
                sb.append("\t");

                sb.append(vc.getAlleleIndices(vc.getGenotype(0).getAlleles()));
                sb.append("\t");

                sb.append(vc.getGenotype(0).getDP());
                sb.append("\t");

                sb.append(vc.getGenotype(0).getGQ());
                sb.append("\t");
                
                sb.append(intArrayToString(vc.getGenotype(0).getAD()));
                sb.append("\t");
                    
                sb.append(intArrayToString(vc.getGenotype(0).getPL()));
                sb.append("\t");
                return sb.toString();
            }else{
                sb.append("\t\t\t\t\t\t\t");
                return sb.toString();
            }
        }else if(vc.getSampleNames().size() > 1){
            GenotypesContext gc = vc.getGenotypes();
            Iterator<Genotype> it = gc.iterator();
            StringBuilder samples = new StringBuilder();
            StringBuilder genotypestrings = new StringBuilder();
            StringBuilder alleleindices = new StringBuilder();
            StringBuilder depth = new StringBuilder();
            StringBuilder gq = new StringBuilder();
            StringBuilder ad = new StringBuilder();
            StringBuilder pl = new StringBuilder();
            while(it.hasNext()){
                Genotype gt = it.next();
                if(!gt.getGenotypeString().equals("./.")){
                    samples.append(gt.getSampleName() + ";");
                    genotypestrings.append(gt.getGenotypeString() + ";");
                    alleleindices.append(vc.getAlleleIndices(gt.getAlleles()) + ";");
                    depth.append(gt.getDP() + ";");
                    gq.append(gt.getGQ() + ";");
                    ad.append(intArrayToString(gt.getAD()) + ";");
                    pl.append(intArrayToString(gt.getPL()) + ";");
                }
            }
            sb.append(samples.toString() + "\t");
            sb.append(genotypestrings.toString() + "\t");
            sb.append(alleleindices.toString() + "\t");
            sb.append(depth.toString() + "\t");
            sb.append(gq.toString() + "\t");
            sb.append(ad.toString() + "\t");
            sb.append(pl.toString() + "\t");
            return sb.toString();
        }else{
            sb.append("\t\t\t\t\t\t\t");
            return sb.toString();
        }
    }
    
    /**
    * Returns the genotypes of all family members.
    * 
    * @param vc variant
    * @param affectedVariants affected variants hash
    * @param unaffectedVariants unaffected variants hash
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getAllSamplesGenotypeOutput(VariantContext vc, ArrayList<Hashtable<String, VariantContext>> affectedVariants, ArrayList<Hashtable<String, VariantContext>> unaffectedVariants) {
        StringBuilder sb = new StringBuilder();        
        if(vc.hasGenotypes()){
            String key = vc.getContig() + "_" + vc.getStart();
            for(Hashtable<String, VariantContext> ht : affectedVariants){
                VariantContext v = ht.get(key);
                if(v != null && v.hasGenotypes()){
                    sb.append(v.getGenotype(0).getSampleName());
                    sb.append("\t");
                    sb.append(v.getGenotype(0).getGenotypeString());
                    sb.append("\t");

                    sb.append(v.getAlleleIndices(vc.getGenotype(0).getAlleles()));
                    sb.append("\t");

                    sb.append(v.getGenotype(0).getDP());
                    sb.append("\t");

                    sb.append(v.getGenotype(0).getGQ());
                    sb.append("\t");
                    
                    sb.append(intArrayToString(v.getGenotype(0).getAD()));
                    sb.append("\t");
                    
                    sb.append(intArrayToString(v.getGenotype(0).getPL()));
                    sb.append("\t");
                }else{
                    sb.append("\t\t\t\t\t\t\t");
                }
            }
            for(Hashtable<String, VariantContext> ht : unaffectedVariants){
                VariantContext v = ht.get(key);
                if(v != null && v.hasGenotypes()){
                    sb.append(v.getGenotype(0).getSampleName());
                    sb.append("\t");
                    sb.append(v.getGenotype(0).getGenotypeString());
                    sb.append("\t");

                    sb.append(v.getAlleleIndices(vc.getGenotype(0).getAlleles()));
                    sb.append("\t");

                    sb.append(v.getGenotype(0).getDP());
                    sb.append("\t");

                    sb.append(v.getGenotype(0).getGQ());
                    sb.append("\t");
                    
                    sb.append(intArrayToString(v.getGenotype(0).getAD()));
                    sb.append("\t");
                    
                    sb.append(intArrayToString(v.getGenotype(0).getPL()));
                    sb.append("\t");
                }else{
                    sb.append("\t\t\t\t\t\t\t");
                }
            }
            return sb.toString();
        }else{
            for(Hashtable<String, VariantContext> ht : affectedVariants){                 
                    sb.append("\t\t\t\t\t\t\t");                
            }
            for(Hashtable<String, VariantContext> ht : unaffectedVariants){                
                    sb.append("\t\t\t\t\t\t\t");                
            }
            return sb.toString();
        }
    }
    
    private String intArrayToString(int[] a){
        if(a == null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i : a){
            sb.append("" + i + ",");
        }
        sb.append("]");
        String result = sb.toString();
        result = result.replace(",]", "]");
        return result;
        
    }
}
