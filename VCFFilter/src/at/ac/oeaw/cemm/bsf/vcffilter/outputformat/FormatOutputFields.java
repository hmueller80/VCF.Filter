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
import at.ac.oeaw.cemm.bsf.vcffilter.preferences.Hyperlink;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.Hashtable;

/** 
 * Formats default column output, i.e. fields with no special requirements.
 * FormatOutputFields.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FormatOutputFields {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /** fields */
    ArrayList<FormatField> fields;
    
    /** recurrence hash */
    Hashtable<String, VariantRecurrence> recurrenceHash;
    
    /** affected variants */
    ArrayList<Hashtable<String, VariantContext>> affectedVariants;
    
    /** unaffected variants */
    ArrayList<Hashtable<String, VariantContext>> unaffectedVariants;
    
    /**
    * Creates new FormatOutputFields.
    * 
    * @param ids desired column headers
    * @param links list of Hyperlinks to be shown
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatOutputFields(ArrayList<String> ids, ArrayList<Hyperlink> links){
        fields = new ArrayList<FormatField>();
        for(String s : ids){
            if(s.equals("CHROM")){
                fields.add(new FormatFieldChrom(s));
            }else if(s.equals("POS")){
                fields.add(new FormatFieldPos(s));
            }else if(s.equals("ID")){
                fields.add(new FormatFieldID(s));
            }else if(s.equals("REF")){
                fields.add(new FormatFieldRef(s));
            }else if(s.equals("ALT") || s.equals("NON_REF")){
                fields.add(new FormatFieldAlt(s));
            }else if(s.equals("QUAL") || s.equals("LowQual")){
                fields.add(new FormatFieldQual(s));
            }else if(s.equals("FILTER")){
                fields.add(new FormatFieldFilter(s));
            }else{
                fields.add(new FormatFields(s));
            }
                    
        } 
        fields.add(new FormatFieldRecurrence("recurrence"));
        fields.add(new FormatFieldGenotype("genotype"));
        fields.add(new FormatFieldExacHyperlink("ExAC"));   
        fields.add(new FormatFieldCosmicBeaconHyperlink("CosmicBeacon"));   
        for(Hyperlink h : links){
            fields.add(h);
        }
    }
    
    /**
    * Creates new FormatOutputFields.
    * 
    * @param ids desired column headers
    * @param affectedVariants affected variants hash
    * @param unaffectedVariants unaffected variants hash
    * @param links list of Hyperlinks to be shown
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatOutputFields(ArrayList<String> ids, ArrayList<Hashtable<String, VariantContext>> affectedVariants, ArrayList<Hashtable<String, VariantContext>> unaffectedVariants, ArrayList<Hyperlink> links){
        this.affectedVariants = affectedVariants;
        this.unaffectedVariants = unaffectedVariants;
        fields = new ArrayList<FormatField>();
        for(String s : ids){
            if(s.equals("CHROM")){
                fields.add(new FormatFieldChrom(s));
            }else if(s.equals("POS")){
                fields.add(new FormatFieldPos(s));
            }else if(s.equals("ID")){
                fields.add(new FormatFieldID(s));
            }else if(s.equals("REF")){
                fields.add(new FormatFieldRef(s));
            }else if(s.equals("ALT") || s.equals("NON_REF")){
                fields.add(new FormatFieldAlt(s));
            }else if(s.equals("QUAL") || s.equals("LowQual")){
                fields.add(new FormatFieldQual(s));
            }else if(s.equals("FILTER")){
                fields.add(new FormatFieldFilter(s));
            }else{
                fields.add(new FormatFields(s));
            }
                    
        } 
        fields.add(new FormatFieldRecurrence("recurrence"));
        for(int i = 0; i < affectedVariants.size(); i++){
            fields.add(new FormatFieldGenotype("genotype"));
        }
        for(int i = 0; i < unaffectedVariants.size(); i++){
            fields.add(new FormatFieldGenotype("genotype"));
        }
        fields.add(new FormatFieldExacHyperlink("ExAC"));   
        for(Hyperlink h : links){
            fields.add(h);
        }
    }
    
    /**
    * Returns the column headers of all selected output fields.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getOutputHeader(){
        StringBuilder sb = new StringBuilder();
        for(FormatField ff : fields){
            sb.append(ff.getHeader());
        }
        return sb.toString();
    }
    
    /**
    * Returns the variant attributes for all selected output fields.
    * 
    * @param vc variant
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String formatOutput(VariantContext vc){
        StringBuilder sb = new StringBuilder();
        for(FormatField ff : fields){
            if(ff instanceof FormatFieldRecurrence){
                FormatFieldRecurrence ffr = (FormatFieldRecurrence)ff;
                sb.append(ffr.getOutput(vc, recurrenceHash));
            }else if(ff instanceof FormatFieldGenotype){
                FormatFieldGenotype ffg = (FormatFieldGenotype)ff;
                sb.append(ffg.getOutput(vc));
            }else{
                sb.append(ff.getOutput(vc));
            }            
        }
        return sb.toString();
    }
    
    /**
    * Returns the variant attributes for all selected output fields and all family genotypes.
    * 
    * @param vc variant
    * @param affectedVariants affected variants hash
    * @param unaffectedVariants unaffected variants hash
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String formatOutput(VariantContext vc, ArrayList<Hashtable<String, VariantContext>> affectedVariants, ArrayList<Hashtable<String, VariantContext>> unaffectedVariants){
        StringBuilder sb = new StringBuilder();
        boolean reportgenotype = true;
        for(FormatField ff : fields){
            if(ff instanceof FormatFieldRecurrence){
                FormatFieldRecurrence ffr = (FormatFieldRecurrence)ff;
                sb.append(ffr.getOutput(vc, recurrenceHash));
            }else if(ff instanceof FormatFieldGenotype){
                if(reportgenotype){
                    FormatFieldGenotype ffg = (FormatFieldGenotype)ff;
                    sb.append(ffg.getAllSamplesGenotypeOutput(vc, affectedVariants, unaffectedVariants));
                    reportgenotype = false;
                }
            }else{
                sb.append(ff.getOutput(vc));
            }            
        }
        return sb.toString();
    }

    /**
    * Sets the reference to the recurrence hash.
    * 
    * @param recurrenceHash recurrence hash
    * @author Heiko Müller
    * @since 1.0
    */
    public void setRecurrenceHash(Hashtable<String, VariantRecurrence> recurrenceHash) {
        this.recurrenceHash = recurrenceHash;
    }
    
    
    
}
