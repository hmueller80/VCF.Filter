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

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.preferences.VCFFilterPreferences;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/** 
 * Generates Filters from VCFHeaderLines.
 * FilterFactory.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FilterFactory { 
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
    * Reference to the VCFFilter preferences.
    */
    static VCFFilterPreferences PREFERENCES;      
    
    /**
    * Creates new FilterFactory.
    * 
    * @param gui graphical user interface
    * @author Heiko Müller
    * @since 1.0
    */
    public FilterFactory(VCFFilter gui){
        PREFERENCES = gui.getPreferences();
    }
    
    /**
    * Parses VCFHeaderLine.
    * 
    * @param header VCF header line
    * @return FilterType
    * @author Heiko Müller
    * @since 1.0
    */
    private static FilterType parseVCFHeaderLine(String header){
        header = header.substring(6, header.length() - 1);
        String id = header.substring(0, header.indexOf(",Number"));
        String number = header.substring(header.indexOf(",Number") + 1, header.indexOf(",Type"));
        String type = header.substring(header.indexOf(",Type") + 1, header.indexOf(",Description"));
        String description = header.substring(header.indexOf(",Description") + 1);
        
        String ID = id.substring(id.indexOf("=") + 1);        
        String Number = number.substring(number.indexOf("=") + 1);
        String Type = type.substring(type.indexOf("=") + 1);
        String Description = description.substring(description.indexOf("=") + 1); 
        return new FilterType(ID, Number, Type, Description);
    }
    
    /**
     *  Returns the Filter that is appropriate for this VCFInfoHeaderLine line.
     * 
     * If the count is a fixed count, return that.  For example, a field with size of 1 in the header returns 1
     * If the count is of type A, return vc.getNAlleles - 1
    * If the count is of type G, return the expected number of genotypes given the number of alleles in VC and the
    * max ploidy among all samples.  Note that if the max ploidy of the VC is 0 (there's no GT information
    * at all, then implicitly assume diploid samples when computing G values.
    * int ploidy = vc.GetMaxPloidy(2);
    * return GenotypeLikelihoods.numLikelihoods(vc.NAlleles, ploidy);
    * If the count is UNBOUNDED return -1
     * Get the number of values expected for this header field, given the properties of VariantContext vc
     *
     * If the count is a fixed count, return that.  For example, a field with size of 1 in the header returns 1
     * If the count is of type A, return vc.getNAlleles - 1
     * If the count is of type R, return vc.getNAlleles
     * If the count is of type G, return the expected number of genotypes given the number of alleles in VC and the
     *   max ploidy among all samples.  Note that if the max ploidy of the VC is 0 (there's no GT information
     *   at all, then implicitly assume diploid samples when computing G values.
     * If the count is UNBOUNDED return -1
     *
     * @param header VCF header line object
     * @return Filter 
    */
    public static Filter getFilter(VCFInfoHeaderLine header){

        VCFHeaderLineType type = header.getType();
        VCFHeaderLineCount count = header.getCountType();
        //header.
        if(type.equals(VCFHeaderLineType.Integer)){
            if(count.name().equals(VCFHeaderLineCount.A.name())){
                return new IntegerArrayFilter(header);
            }else if(count.name().equals(VCFHeaderLineCount.G.name())){
                IntegerArrayFilter filter = new IntegerArrayFilter(header);
                filter.setIsGenotypeArray(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.INTEGER.name())){
                if(header.getID().equals("POS")){
                    return new PositionFilter(header); 
                }else{
                    return new IntegerNumberFilter(header);
                }
            }else if(count.name().equals(VCFHeaderLineCount.R.name())){
                IntegerArrayFilter filter = new IntegerArrayFilter(header);
                filter.setArrayContainsRefAllele(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.UNBOUNDED.name())){
                return new IntegerNumberFilter(header);
            }
        }else if(type.equals(VCFHeaderLineType.Character)){
            if(count.name().equals(VCFHeaderLineCount.A.name())){
                CharacterArrayFilter filter = new CharacterArrayFilter(header);
                filter.setArrayContainsRefAllele(false);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.G.name())){
                CharacterArrayFilter filter = new CharacterArrayFilter(header);
                filter.setIsGenotypeArray(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.INTEGER.name())){
                return new CharacterFilter(header);
            }else if(count.name().equals(VCFHeaderLineCount.R.name())){
                CharacterArrayFilter filter = new CharacterArrayFilter(header);
                filter.setArrayContainsRefAllele(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.UNBOUNDED.name())){
                return new CharacterFilter(header);
            }
        }else if(type.equals(VCFHeaderLineType.Flag)){
            if(count.name().equals(VCFHeaderLineCount.A.name())){
                return null;
            }else if(count.name().equals(VCFHeaderLineCount.G.name())){
                return null;
            }else if(count.name().equals(VCFHeaderLineCount.INTEGER.name())){
                return new FlagFilter(header);
            }else if(count.name().equals(VCFHeaderLineCount.R.name())){
                return null;
            }else if(count.name().equals(VCFHeaderLineCount.UNBOUNDED.name())){
                return null;
            }
        }else if(type.equals(VCFHeaderLineType.Float)){
            if(count.name().equals(VCFHeaderLineCount.A.name())){
                return new DoubleArrayFilter(header);
            }else if(count.name().equals(VCFHeaderLineCount.G.name())){
                DoubleArrayFilter filter = new DoubleArrayFilter(header);
                filter.setIsGenotypeArray(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.INTEGER.name())){
                if(header.getID().equals("QUAL")){                            
                    QualFilter f = new QualFilter(header);                        
                    return f;
                }else{
                    return new DoubleNumberFilter(header);
                }
            }else if(count.name().equals(VCFHeaderLineCount.R.name())){
                DoubleArrayFilter filter = new DoubleArrayFilter(header);
                filter.setArrayContainsRefAllele(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.UNBOUNDED.name())){
                return new DoubleNumberFilter(header);
            }
        }else if(type.equals(VCFHeaderLineType.String)){
            if(count.name().equals(VCFHeaderLineCount.A.name())){                
                 return new StringArrayFilter(header);                
            }else if(count.name().equals(VCFHeaderLineCount.G.name())){
                StringArrayFilter filter = new StringArrayFilter(header);
                filter.setIsGenotypeArray(true);
                return filter;
            }else if(count.name().equals(VCFHeaderLineCount.INTEGER.name())){
                if(header.getID().equals("CHROM")){                            
                    ChromosomeFilter f = new ChromosomeFilter(header);                        
                    return f;
                }else if(header.getID().equals("FILTER")){                            
                    FilterFilter f = new FilterFilter(header);                        
                    return f;
                }else if(header.getID().equals("ID")){                            
                    IDFilter f = new IDFilter(header);                        
                    return f;
                }else{
                    return new StringFilter(header);
                }
            }else if(count.name().equals(VCFHeaderLineCount.R.name())){                
                StringArrayFilter filter = new StringArrayFilter(header);
                filter.setArrayContainsRefAllele(true);
                return filter;
               
            }else if(count.name().equals(VCFHeaderLineCount.UNBOUNDED.name())){
                if(header.getID().equals("db_snp.CAF")){
                    //exception as this field exports allele frequencies in a String data type field
                    //System.out.println("returning CAF filter");
                    CAFFilter f = new CAFFilter(header);
                    f.setArrayContainsRefAllele(true);
                    return f;
                }else{
                    return new StringFilter(header);
                }
            }
        }
        return null;
    }
    
    /**
    * Returns Filter according to the provided settings.
    * 
    * @param fsc Filter settings
    * @return Filter
    * @author Heiko Müller
    * @since 1.0
    */
    public Filter getFilter(FilterSettings fsc){
        VCFInfoHeaderLine hl = PREFERENCES.getVCFInfoHeaderLineByID(fsc.getId());
        if(hl != null){
            Filter f = FilterFactory.getFilter(hl);
            f.setCriterion1(fsc.getCriterion1());
            f.setCriterion2(fsc.getCriterion2());
            f.setCriterion3(fsc.getCriterion3());
            f.setAndnot(fsc.isAndnot());
            f.setPredicate1();
            f.setPredicate2();
            f.setPredicate3();
            return f;
        }
        return null;
    }
    
    /**
    * Returns Filter according to the provided headerlineString.
    * The headerlineString is used to look up the corresponding VCFInfoHeaderLine in preferences.
    * 
    * @param headerlineString String of header line
    * @param prefs VCFFilterPreferences
    * @return Filter
    * @author Heiko Müller
    * @since 1.0
    */
    public static Filter getFilter(String headerlineString, VCFFilterPreferences prefs){
        String id = headerlineString.substring(headerlineString.indexOf("ID=") + 3, headerlineString.indexOf(",Number"));       
        VCFInfoHeaderLine hl = prefs.getVCFInfoHeaderLineByID(id);
        return getFilter(hl);
    }
    
    
    /**
    * Parses the ID from the provided header line String.
    * 
    * @param header String of header line
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    private static String getID(String header){
        header = header.substring(6, header.length() - 1);
        String id = header.substring(0, header.indexOf(",Number"));        
        String ID = id.substring(id.indexOf("=") + 1);    
        return ID;
    }
}
