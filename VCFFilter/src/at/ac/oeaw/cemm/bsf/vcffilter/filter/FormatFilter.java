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

import htsjdk.variant.vcf.VCFFormatHeaderLine;

/*

public final class VCFConstants {
    public static final Locale VCF_LOCALE = Locale.US;

    // reserved INFO/FORMAT field keys
    public static final String ANCESTRAL_ALLELE_KEY = "AA";
    public static final String ALLELE_COUNT_KEY = "AC";
    public static final String ALLELE_FREQUENCY_KEY = "AF";
    public static final String ALLELE_NUMBER_KEY = "AN";
    public static final String RMS_BASE_QUALITY_KEY = "BQ";
    public static final String CIGAR_KEY = "CIGAR";
    public static final String DBSNP_KEY = "DB";
    public static final String DEPTH_KEY = "DP";
    public static final String END_KEY = "END";

    public static final String GENOTYPE_FILTER_KEY = "FT";
    public static final String GENOTYPE_KEY = "GT";
    public static final String GENOTYPE_POSTERIORS_KEY = "GP";
    public static final String GENOTYPE_QUALITY_KEY = "GQ";
    public static final String GENOTYPE_ALLELE_DEPTHS = "AD"; //AD isn't reserved, but is specifically handled by VariantContext
    public static final String GENOTYPE_PL_KEY = "PL";   // phred-scaled genotype likelihoods
    public static final String EXPECTED_ALLELE_COUNT_KEY = "EC";
    @Deprecated public static final String GENOTYPE_LIKELIHOODS_KEY = "GL";         // log10 scaled genotype likelihoods

    public static final String HAPMAP2_KEY = "H2";
    public static final String HAPMAP3_KEY = "H3";
    public static final String HAPLOTYPE_QUALITY_KEY = "HQ";
    public static final String RMS_MAPPING_QUALITY_KEY = "MQ";
    public static final String MAPPING_QUALITY_ZERO_KEY = "MQ0";
    public static final String SAMPLE_NUMBER_KEY = "NS";
    public static final String PHASE_QUALITY_KEY = "PQ";
    public static final String PHASE_SET_KEY = "PS";
    public static final String OLD_DEPTH_KEY = "RD";
    public static final String STRAND_BIAS_KEY = "SB";
    public static final String SOMATIC_KEY = "SOMATIC";
    public static final String VALIDATED_KEY = "VALIDATED";
    public static final String THOUSAND_GENOMES_KEY = "1000G";
    
    // reserved INFO for structural variants
    //INFO Type of structural variant 
    public static final String SVTYPE = "SVTYPE";    

    // separators
    public static final String FORMAT_FIELD_SEPARATOR = ":";
    public static final String GENOTYPE_FIELD_SEPARATOR = ":";
    public static final char   GENOTYPE_FIELD_SEPARATOR_CHAR = ':';
    public static final String FIELD_SEPARATOR = "\t";
    public static final char   FIELD_SEPARATOR_CHAR = '\t';
    public static final String FILTER_CODE_SEPARATOR = ";";
    public static final String INFO_FIELD_ARRAY_SEPARATOR = ",";
    public static final char INFO_FIELD_ARRAY_SEPARATOR_CHAR = ',';
    public static final String ID_FIELD_SEPARATOR = ";";
    public static final String INFO_FIELD_SEPARATOR = ";";
    public static final char INFO_FIELD_SEPARATOR_CHAR = ';';
    public static final String UNPHASED = "/";
    public static final String PHASED = "|";
    public static final String PHASED_SWITCH_PROB_v3 = "\\";
    public static final String PHASING_TOKENS = "/|\\";

    // header lines
    public static final String FILTER_HEADER_START = "##FILTER";
    public static final String FORMAT_HEADER_START = "##FORMAT";
    public static final String INFO_HEADER_START = "##INFO";
    public static final String ALT_HEADER_START = "##ALT";
    public static final String CONTIG_HEADER_KEY = "contig";
    public static final String CONTIG_HEADER_START = "##" + CONTIG_HEADER_KEY;

    // old indel alleles
    public static final char DELETION_ALLELE_v3 = 'D';
    public static final char INSERTION_ALLELE_v3 = 'I';

    // special alleles
    public static final char SPANNING_DELETION_ALLELE = '*';
    public static final char NO_CALL_ALLELE = '.';
    public static final char NULL_ALLELE = '-';


    // missing/default values
    public static final String UNFILTERED = ".";
    public static final String PASSES_FILTERS_v3 = "0";
    public static final String PASSES_FILTERS_v4 = "PASS";
    public static final String EMPTY_ID_FIELD = ".";
    public static final String EMPTY_INFO_FIELD = ".";
    public static final String EMPTY_ALTERNATE_ALLELE_FIELD = ".";
    public static final String MISSING_VALUE_v4 = ".";
    public static final String MISSING_QUALITY_v3 = "-1";
    public static final Double MISSING_QUALITY_v3_DOUBLE = Double.valueOf(MISSING_QUALITY_v3);

    public static final String MISSING_GENOTYPE_QUALITY_v3 = "-1";
    public static final String MISSING_HAPLOTYPE_QUALITY_v3 = "-1";
    public static final String MISSING_DEPTH_v3 = "-1";
    public static final String UNBOUNDED_ENCODING_v4 = ".";
    public static final String UNBOUNDED_ENCODING_v3 = "-1";
    public static final String PER_ALTERNATE_COUNT = "A";
    public static final String PER_ALLELE_COUNT = "R";
    public static final String PER_GENOTYPE_COUNT = "G";
    public static final String EMPTY_ALLELE = ".";
    public static final String EMPTY_GENOTYPE = "./.";
    public static final int MAX_GENOTYPE_QUAL = 99;

    public static final Double VCF_ENCODING_EPSILON = 0.00005; // when we consider fields equal(), used in the Qual compare



GT
GQ
DP
PL
AD
FT
PQ

registerStandard(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_KEY,           1,                            VCFHeaderLineType.String,  "Genotype"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_QUALITY_KEY,   1,                            VCFHeaderLineType.Integer, "Genotype Quality"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.DEPTH_KEY,              1,                            VCFHeaderLineType.Integer, "Approximate read depth (reads with MQ=255 or with bad mates are filtered)"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_PL_KEY,        VCFHeaderLineCount.G,         VCFHeaderLineType.Integer, "Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_ALLELE_DEPTHS, VCFHeaderLineCount.R,         VCFHeaderLineType.Integer, "Allelic depths for the ref and alt alleles in the order listed"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_FILTER_KEY,    VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,  "Genotype-level filter"));
registerStandard(new VCFFormatHeaderLine(VCFConstants.PHASE_QUALITY_KEY,      1,                            VCFHeaderLineType.Float,   "Read-backed phasing quality"));

##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=PGT,Number=1,Type=String,Description="Physical phasing haplotype information, describing how the alternate alleles are phased in relation to one another">
##FORMAT=<ID=PID,Number=1,Type=String,Description="Physical phasing ID information, where each unique ID within a given sample (but not across samples) connects records within a phasing group">

##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Approximate read depth (reads with MQ=255 or with bad mates are filtered)">
##FORMAT=<ID=GQ,Number=1,Type=Integer,Description="Genotype Quality">
##FORMAT=<ID=RGQ,Number=1,Type=Integer,Description="Unconditional reference genotype confidence, encoded as a phred quality -10*log10 p(genotype call is wrong)">
##FORMAT=<ID=MIN_DP,Number=1,Type=Integer,Description="Minimum DP observed within the GVCF block">

##FORMAT=<ID=SB,Number=4,Type=Integer,Description="Per-sample component statistics which comprise the Fisher's Exact Test to detect strand bias.">


##FORMAT=<ID=AD,Number=R,Type=Integer,Description="Allelic depths for the ref and alt alleles in the order listed">

##FORMAT=<ID=PL,Number=G,Type=Integer,Description="Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification">

##FORMAT=<ID=CN,Number=1,Type=Integer,Description="Copy number genotype for imprecise events">
##FORMAT=<ID=CNQ,Number=1,Type=Float,Description="Copy number genotype quality for imprecise events">
##FORMAT=<ID=CNL,Number=.,Type=Float,Description="Copy number genotype likelihood for imprecise events">
##FORMAT=<ID=NQ,Number=1,Type=Integer,Description="Phred style probability score that the variant is novel">
##FORMAT=<ID=HAP,Number=1,Type=Integer,Description="Unique haplotype identifier">
##FORMAT=<ID=AHAP,Number=1,Type=Integer,Description="Unique identifier of ancestral haplotype">
##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=GQ,Number=1,Type=Float,Description="Genotype quality">
##FORMAT=<ID=CN,Number=1,Type=Integer,Description="Copy number genotype for imprecise events">
##FORMAT=<ID=CNQ,Number=1,Type=Float,Description="Copy number genotype quality for imprecise events">

*/

/** 
 * abstract Filter for FORMAT fields. Restricts instantiation to VCFFormatHeaderLine header lines.
 * FormatFilter.java 05 APR 2017
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class FormatFilter extends Filter{
    
    /**
    * Initializes new FormatFilter.
    * Restricts this type of Filter to VCFFormatHeaderLine objects, 
    * which require attributes to be extracted from genotypes rather than from VariantContexts.
    *
    * @param header VCF headerline object
    * @author Heiko Müller
    * @since 1.0
    */
    public FormatFilter(VCFFormatHeaderLine header) {
        super(header);
    }    
    
}
