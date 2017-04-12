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

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import java.util.ArrayList;
import java.util.List;

/** 
 * abstract Filter for FORMAT fields of type array.
 * FormatArrayFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class FormatArrayFilter extends FormatFilter{
     /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * Indicator if array contains reference allele
     */
    boolean arraycontainsref = false;

    /**
     * Indicator if array is genotype array
     */
    boolean isGenotypeArray = false;
      
    
    /**
    * Initializes new FormatArrayFilter.
    *
    * @param header VCF headerline object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatArrayFilter(VCFFormatHeaderLine header) {
        super(header);
    }

    /**
     * Returns the allele index needed for filtering on values stored in arrays
     * since the reference allele has index 0 and the arrays generally list only
     * alt alleles, the allele index must be decremented by 1; the indices
     * returned are decremented by 1 and can be used directly for filtering on
     * array values.
     *
     * @param vc VariantContext
     * @param sampleName sample name
     * @return List&#60;Integer&#62; the allele indices
     * @author Heiko Müller
     * @since 1.0
     */
    protected List<Integer> getAlleleIndices(VariantContext vc, String sampleName) {
        List<Integer> result = new ArrayList<Integer>();
        List<Allele> alleles = vc.getGenotype(sampleName).getAlleles();
        for (Allele a : alleles) {
            int index = vc.getAlleleIndex(a);
            if (index > 0) {
                result.add(index - 1);
            }
        }
        return result;
    }
    
    protected List<Integer> getAlleleIndices(VariantContext vc, Genotype gt) {
        List<Integer> result = new ArrayList<Integer>();
        List<Allele> alleles = gt.getAlleles();
        for (Allele a : alleles) {
            int index = vc.getAlleleIndex(a);
            if (index > 0) {
                result.add(index - 1);
            }
        }
        return result;
    }

    /**
     * Returns the allele index needed for filtering on values stored in arrays
     * since the reference allele has index 0 and the arrays generally list only
     * alt alleles, the allele index must be decremented by 1; the indices
     * returned are decremented by 1 and can be used directly for filtering on
     * array values.
     *
     * @param vc VariantContext
     * @param sampleName sample name
     * @return List&#60;Integer&#62; the allele indices
     * @author Heiko Müller
     * @since 1.0
     */
    protected List<Integer> getAlleleIndicesIncludingRefAllele(VariantContext vc, String sampleName) {
        List<Integer> result = new ArrayList<Integer>();
        List<Allele> alleles = vc.getGenotype(sampleName).getAlleles();
        for (Allele a : alleles) {
            result.add(vc.getAlleleIndex(a));
        }
        return result;
    }
    
    protected List<Integer> getAlleleIndicesIncludingRefAllele(VariantContext vc, Genotype gt) {
        List<Integer> result = new ArrayList<Integer>();
        List<Allele> alleles = gt.getAlleles();
        for (Allele a : alleles) {
            result.add(vc.getAlleleIndex(a));
        }
        return result;
    }

    /**
     * Setter for arraycontainsref variable.
     *
     * @param val true if array contains reference allele
     * @author Heiko Müller
     * @since 1.0
     */
    public void setArrayContainsRefAllele(boolean val) {
        arraycontainsref = val;
    }

    /**
     * Getter for arraycontainsref variable (Type R if true, Type A if false).
     *
     * @return boolean true if array contains reference allele
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean getArrayContainsRefAllele() {
        return arraycontainsref;
    }

    //public boolean isArraycontainsref() {
    //    return arraycontainsref;
    //}
    //public void setArraycontainsref(boolean arraycontainsref) {
    //    this.arraycontainsref = arraycontainsref;
    //}
    /**
     * Getter for isGenotypeArray variable.
     *
     * @return boolean true if array is a genotype array (Type G)
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean isIsGenotypeArray() {
        return isGenotypeArray;
    }

    /**
     * Setter for isGenotypeArray variable.
     *
     * @param isGenotypeArray true if array is a genotype array (Type G), false
     * otherwise.
     * @author Heiko Müller
     * @since 1.0
     */
    public void setIsGenotypeArray(boolean isGenotypeArray) {
        this.isGenotypeArray = isGenotypeArray;
    }
}
