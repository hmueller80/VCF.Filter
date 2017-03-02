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
package at.ac.oeaw.cemm.bsf.vcffilter.inheritance;

import at.ac.oeaw.cemm.bsf.vcffilter.outputformat.VariantContextComparator;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements criteria for finding autosomal recessive, autosomal dominant,
 * compound heterozygous, and X-linked variants. Inheritance.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Inheritance {

    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele and identical in all affected individuals.
     *
     * The list of affected individuals is expected to contain
     * Hashtable<String, VariantContext>
     * objects where VariantContext objects are stored using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates
     * @param affected
     * @param relations
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     *
     * private static ArrayList<VariantContext>
     * recessive(ArrayList<VariantContext> candidates,
     * ArrayList<Hashtable<String, VariantContext>> affected, Relationships
     * relations){ ArrayList<VariantContext> result = new
     * ArrayList<VariantContext>(); for(VariantContext vc : candidates) { if
     * (vc.getGenotype(0).isHomVar()) {
     * if(genotypeIsIdenticalInAllIndividuals(vc, affected)){ result.add(vc); }
     * } } return result; }
    *
     */
    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele and identical in all affected individuals as
     * well as absent in homozygous state from all unaffected individuals.
     *
     * When mother and father hash indices are defined, a genotype consistency
     * test is performed and variants failing this test are excluded. The
     * parameters mother and father indicate the index of the corresponding
     * variants Hashtable in the unaffected list.
     *
     * Affected and unaffected lists of hashtables contain all variants that
     * passed the initial filtering for affected and unaffected individuals,
     * respectively.
     *
     * The affectedHashIndex indicates the affected individual whose variants
     * are used for testing.
     *
     * The list of affected and unaffected individuals is expected to contain
     * Hashtable&#60;String, VariantContext&#62; objects where VariantContext
     * objects are stored using keys in the form "contig_start" e.g.
     * "1_123456789"
     *
     * The list of unaffected individuals may be null.
     *
     * @author Heiko Mueller
     * @param affectedHashIndex index of Hashtable holding variants of a given affected individual
     * @param affected list of Hashtables holding variants of affected individuals
     * @param unaffected list of Hashtables holding variants of unaffected individuals
     * @param mother index of Hashtable holding variants of mother
     * @param father index of Hashtable holding variants of father
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> recessive(int affectedHashIndex, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father) {
        ArrayList<VariantContext> al = hashToList(affectedHashIndex, affected);
        return recessive(al, affected, unaffected, mother, father);
    }

    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele and identical in all affected individuals as
     * well as absent in homozygous state from all unaffected individuals.
     *
     * When mother and father hash indices are defined, a genotype consistency
     * test is performed and variants failing this test are excluded. The
     * parameters mother and father indicate the index of the corresponding
     * variants Hashtable in the unaffected list.
     *
     * Affected and unaffected lists of hashtables contain all variants that
     * passed the initial filtering for affected and unaffected individuals,
     * respectively.
     *
     * The list of affected and unaffected individuals is expected to contain
     * Hashtable&#60;String, VariantContext&#62; objects where VariantContext
     * objects are stored using keys in the form "contig_start" e.g.
     * "1_123456789"
     *
     * The list of unaffected individuals may be null.
     *
     * @author Heiko Mueller
     * @param candidates list of candidate variants
     * @param affected list of Hashtables holding variants of affected individuals
     * @param unaffected list of Hashtables holding variants of unaffected individuals
     * @param mother index of Hashtable holding variants of mother
     * @param father index of Hashtable holding variants of father
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> recessive(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father) {
        if (candidates == null) {
            return new ArrayList<VariantContext>();
        }
        if (candidates != null && affected != null && unaffected == null) {
            return recessive(candidates, affected);
        }
        if (candidates != null && affected != null && unaffected != null) {
            return recessive(candidates, affected, unaffected);
            /*
            if ((mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                return recessive(candidates, affected, unaffected);
            } else if (!(mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                ArrayList<VariantContext> temp = recessive(candidates, affected, unaffected);
                return testGenotypeConsistency(temp, unaffected.get(mother), null);
            } else if ((mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                ArrayList<VariantContext> temp = recessive(candidates, affected, unaffected);
                return testGenotypeConsistency(temp, null, unaffected.get(father));
            } else {
                if (mother == father) {
                    return new ArrayList<VariantContext>();
                }
                ArrayList<VariantContext> temp = recessive(candidates, affected, unaffected);
                return testGenotypeConsistency(temp, unaffected.get(mother), unaffected.get(father));
            }
            */

        }
        return new ArrayList<VariantContext>();
    }

    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele and identical in all affected individuals as
     * well as absent in homozygous state from all unaffected individuals.
     *
     * The list of affected and unaffected individuals is expected to contain
     * Hashtable<String, VariantContext>
     * objects where VariantContext objects are stored using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates
     * @param affected
     * @return ArrayList<VariantContext>
     * @since 1.0
     */
    private static ArrayList<VariantContext> recessive(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            if (vc.getGenotype(0).isHomVar()) {
                if (genotypeIsIdenticalInAllIndividuals(vc, affected) && !genotypeIsPresentInIndividuals(vc, unaffected)) {
                    result.add(vc);
                }
            }
        }
        return result;
    }

    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele and identical in all affected individuals.
     *
     * The list of affected individuals is expected to contain
     * Hashtable<String, VariantContext>
     * objects where VariantContext objects are stored using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates
     * @param affected
     * @return ArrayList<VariantContext>
     * @since 1.0
     */
    private static ArrayList<VariantContext> recessive(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            if (vc.getGenotype(0).isHomVar()) {
                if (genotypeIsIdenticalInAllIndividuals(vc, affected)) {
                    result.add(vc);
                }
            }
        }
        return result;
    }

    /**
     * Returns the list of variants from the candidates list that are homozygous
     * for a non-reference allele.
     *
     * @author Heiko Mueller
     * @param candidates
     * @return ArrayList<VariantContext>
     * @since 1.0
     */
    private static ArrayList<VariantContext> recessive(ArrayList<VariantContext> candidates) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            if (vc.getGenotype(0).isHomVar()) {
                result.add(vc);
            }
        }
        return result;
    }

    /**
     * Tests if a given genotype is identical in all individuals. Returns true
     * if all genotype strings are equal as defined by the String class equals
     * operator. False otherwise.
     *
     * The genotype must be called in all individuals as a null call is
     * interpreted as a homozygous reference genotype.
     *
     * The list of individuals is expected to contain
     * Hashtable<String, VariantContext>
     * objects where VariantContext objects are stored using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean genotypeIsIdenticalInAllIndividuals(VariantContext variantcontext, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                if (!test.getGenotype(0).getGenotypeString().equals(gtstring)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if a given genotype is present in at least one individual. Returns
     * true if the variantcontext genotype string is equal as defined by the
     * String class equals operator to the genotype in at least one individual.
     * False otherwise. *
     *
     * The list of individuals is expected to contain
     * Hashtable<String, VariantContext>
     * objects where VariantContext objects are stored using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean genotypeIsPresentInIndividuals(VariantContext variantcontext, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                if (test.getGenotype(0).getGenotypeString().equals(gtstring)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tests if at least one allele of the genotype is present in all
     * individuals.
     *
     * The list of individuals is expected to contain Hashtable&#60;String,
     * VariantContext&#62; objects where VariantContext objects are stored using
     * keys in the form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean alleleIsPresentInAllIndividuals(VariantContext variantcontext, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        List<Allele> candidatealleles = variantcontext.getGenotype(0).getAlleles();
        boolean result = true;
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                List<Allele> testalleles = test.getGenotype(0).getAlleles();
                if (!isSharedAllelePresent(candidatealleles, testalleles)) {
                    result = false;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Tests if allele is present in all individuals.
     *
     * The list of individuals is expected to contain Hashtable&#60;String,
     * VariantContext&#62; objects where VariantContext objects are stored using
     * keys in the form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean alleleIsPresentInAllIndividuals(VariantContext variantcontext, Allele a, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        //String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        //List<Allele> candidatealleles = variantcontext.getGenotype(0).getAlleles();
        boolean result = true;
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                List<Allele> testalleles = test.getGenotype(0).getAlleles();
                if (!isSharedAllelePresent(a, testalleles)) {
                    result = false;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Tests if at least one allele of the genotype is present in at least one
     * individual. * The list of individuals is expected to contain
     * Hashtable&#60;String, VariantContext&#62; objects where VariantContext
     * objects are stored using keys in the form "contig_start" e.g.
     * "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean alleleIsPresentInIndividuals(VariantContext variantcontext, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        List<Allele> candidatealleles = variantcontext.getGenotype(0).getAlleles();
        boolean result = false;
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                List<Allele> testalleles = test.getGenotype(0).getAlleles();
                if (isSharedAllelePresent(candidatealleles, testalleles)) {
                    result = true;
                    return result;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Tests if allele is present in at least one individual. * The list of
     * individuals is expected to contain Hashtable&#60;String,
     * VariantContext&#62; objects where VariantContext objects are stored using
     * keys in the form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext
     * @param individuals
     * @return boolean
     * @since 1.0
     */
    private static boolean alleleIsPresentInIndividuals(VariantContext variantcontext, Allele a, ArrayList<Hashtable<String, VariantContext>> individuals) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        //String gtstring = variantcontext.getGenotype(0).getGenotypeString();
        //List<Allele> candidatealleles = variantcontext.getGenotype(0).getAlleles();
        boolean result = false;
        for (Hashtable<String, VariantContext> ht : individuals) {
            VariantContext test = ht.get(key);
            if (test != null) {
                List<Allele> testalleles = test.getGenotype(0).getAlleles();
                if (isSharedAllele(a, testalleles)) {
                    result = true;
                    return result;
                }
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Tests if at least one allele in the two lists of alleles has identical
     * base strings in the two lists (meaning it's a shared allele).
     *
     *
     * @author Heiko Mueller
     * @param list1
     * @param list2
     * @return boolean
     * @since 1.0
     */
    private static boolean isSharedAllelePresent(List<Allele> list1, List<Allele> list2) {
        boolean result = false;
        for (Allele a : list1) {
            for (Allele b : list2) {
                if (a.getBaseString().equals(b.getBaseString())) {
                    result = true;
                }
                //System.out.println("Is shared allele present a: " + a.getBaseString() + " b: " + b.getBaseString() + result);
            }
        }
        return result;
    }

    /**
     * Tests if at least one allele in the two lists of alleles has identical
     * base strings in the two lists (meaning it's a shared allele).
     *
     *
     * @author Heiko Mueller
     * @param list1
     * @param list2
     * @return boolean
     * @since 1.0
     */
    private static boolean isSharedAllelePresent(Allele a, List<Allele> list2) {
        boolean result = false;
        //for(Allele a : list1){
        for (Allele b : list2) {
            if (a.getBaseString().equals(b.getBaseString())) {
                result = true;
            }
            //System.out.println("Is shared allele present a: " + a.getBaseString() + " b: " + b.getBaseString() + result);
        }
        //}
        return result;
    }

    /**
     * Tests if a given allele has an identical base string as at least one
     * allele in the list being queried (meaning it's a shared allele).
     *
     *
     * @author Heiko Mueller
     * @param a
     * @param list2
     * @return boolean
     * @since 1.0
     */
    private static boolean isSharedAllele(Allele a, List<Allele> list2) {
        boolean result = false;
        for (Allele b : list2) {
            if (a.getBaseString().equals(b.getBaseString())) {
                result = true;
            }
            //System.out.println("Is shared allele a: " + a.getBaseString() + " b: " + b.getBaseString() + result);
        }
        return result;
    }

    /*
     * Tests if a list of variant genotypes is consistent with the parent genotypes.
     * Returns the list of variants where the consistency test succeeds.
     * 
     * When either mother or father data are null, consistency is tested for one parent only.
     * When both mother or father data are null, no test is performed and the candidates list is returned.
     * 
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain 
     * the corresponding VariantContext object,
     * homozygous reference genotype is assumed for that parent.
     * 
     * The Hashtable&#60;String, VariantContext&#62; objects for mother and father are expected to store
     * VariantContext objects using keys in the form "contig_start"
     * e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates
     * @param mother
     * @param father
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
    
     public static ArrayList<VariantContext> testGenotypeConsistency(ArrayList<VariantContext> candidates, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father, Relationships relations){
     ArrayList<VariantContext> result = new ArrayList<VariantContext>();
     if(mother == null && father == null){
     return candidates;
     }else if(mother == null && father != null){
     for(VariantContext vc : candidates){
     if(genotypeIsConsistent(vc, father)){
     result.add(vc);
     }
     }
     }else if(mother != null && father == null){
     for(VariantContext vc : candidates){
     if(genotypeIsConsistent(vc, mother)){
     result.add(vc);
     }
     }
     }else{
     for(VariantContext vc : candidates){
     if(genotypeIsConsistent(vc, mother, father)){
     result.add(vc);
     }
     }
     }
     return result;
     }
     */
    /**
     * Tests if a list of variant genotypes is consistent with the parent
     * genotypes. Returns the list of variants where the consistency test
     * succeeds.
     *
     * When either mother or father data are null, consistency is tested for one
     * parent only. When both mother or father data are null, no test is
     * performed and the candidates list is returned.
     *
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain
     * the corresponding VariantContext object, homozygous reference genotype is
     * assumed for that parent.
     *
     * The Hashtable&#60;String, VariantContext&#62; objects for mother and
     * father are expected to store VariantContext objects using keys in the
     * form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates candidate variants
     * @param mother Hashtable of mother variants
     * @param father Hashtable of mother variants
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> testGenotypeConsistency(ArrayList<VariantContext> candidates, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        if (mother == null && father == null) {
            return candidates;
        } else if (mother == null && father != null) {
            for (VariantContext vc : candidates) {
                if (genotypeIsConsistent(vc, father)) {
                    result.add(vc);
                }
            }
        } else if (mother != null && father == null) {
            for (VariantContext vc : candidates) {
                if (genotypeIsConsistent(vc, mother)) {
                    result.add(vc);
                }
            }
        } else {
            for (VariantContext vc : candidates) {
                if (genotypeIsConsistent(vc, mother, father)) {
                    result.add(vc);
                }
            }
        }
        return result;
    }

    /**
     * Tests if a list of variant genotypes is consistent with the parent
     * genotypes. Returns the list of variants where the consistency test
     * succeeds.
     *
     * When either mother or father data are null, consistency is tested for one
     * parent only. When both mother or father data are null, no test is
     * performed and the candidates list is returned.
     *
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain
     * the corresponding VariantContext object, homozygous reference genotype is
     * assumed for that parent.
     *
     * The Hashtable&#60;String, VariantContext&#62; objects for mother and
     * father are expected to store VariantContext objects using keys in the
     * form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates list of candidate variants
     * @param relation parent child relationsship between individuals
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> testGenotypeConsistency(ArrayList<VariantContext> candidates, Relationship relation) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            boolean pass = true;
            String key = vc.getContig();
            //System.out.println("variant from sample " + vc.getGenotype(0).getSampleName());
            //for (Relationship r : relations.getRelationships()) {
                //System.out.println(r.individual + " " + vc.getContig() + "_" + vc.getStart());
                Hashtable<String, VariantContext> mother = relation.getMotherHash();
                Hashtable<String, VariantContext> father = relation.getFatherHash();
                if (mother == null && father == null) {
                    //System.out.println("do nothing");
                } else if (mother == null && father != null) {                    
                        if (!genotypeIsConsistent(vc, father)) {
                            pass = false;
                            if(relation.male && key.equals("X")){
                                pass = true;
                            }
                        }
                        //System.out.println("father != null " + pass);
                    
                } else if (mother != null && father == null) {
                    if (!genotypeIsConsistent(vc, mother)) {
                        pass = false;
                    }
                    //System.out.println("mother != null " + pass);
                } else {
                    if (!genotypeIsConsistent(vc, mother, father)) {
                        pass = false;
                        if(relation.male && key.equals("X")){
                            if (!genotypeIsConsistent(vc, mother)) {
                                pass = false;
                            }else{
                                pass = true;
                            }                           
                        }
                    }
                    //System.out.println("mother & father != null " + pass);
                }
            
            //System.out.println(pass);
            if (pass) {
                result.add(vc);
            }
        }
        return result;
    }
    
    /**
     * Tests if a list of variant genotypes is consistent with the parent
     * genotypes. Returns the list of variants where the consistency test
     * succeeds.
     *
     * When either mother or father data are null, consistency is tested for one
     * parent only. When both mother or father data are null, no test is
     * performed and the candidates list is returned.
     *
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain
     * the corresponding VariantContext object, homozygous reference genotype is
     * assumed for that parent.
     *
     * The Hashtable&#60;String, VariantContext&#62; objects for mother and
     * father are expected to store VariantContext objects using keys in the
     * form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param candidates list of candidate variants
     * @param relation parent child relationsship between individuals
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> testGenotypeInConsistency(ArrayList<VariantContext> candidates, Relationship relation) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            boolean pass = false;
            String key = vc.getContig().toUpperCase();            
                Hashtable<String, VariantContext> mother = relation.getMotherHash();
                Hashtable<String, VariantContext> father = relation.getFatherHash();
                if (mother == null && father == null) {
                    pass = false;
                } else if (mother == null && father != null) {                    
                        if (!genotypeIsConsistent(vc, father)) {
                            pass = true;  
                            if(relation.male && key.equals("X")){
                                pass = false;
                            }
                        }
                    
                } else if (mother != null && father == null) {
                    if (!genotypeIsConsistent(vc, mother)) {
                        pass = true;
                    }
                } else {
                    if (!genotypeIsConsistent(vc, mother, father)) {
                        pass = true;
                        if(relation.male && key.equals("X")){
                            if (!genotypeIsConsistent(vc, mother)) {
                                pass = true;
                            }else{
                                pass = false;
                            }                           
                        }
                    }
                }
           
            if (pass) {
                result.add(vc);
            }
        }
        return result;
    }

    /**
     * Tests if a given genotype consistent with the parent genotypes. Returns
     * true if one child allele is found in the mother genotype and the other
     * child allele is found in the father genotype. False otherwise.
     *
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain
     * the corresponding VariantContext object, homozygous reference genotype is
     * assumed for that parent.
     *
     * The Hashtable&#60;String, VariantContext&#62; objects for mother and
     * father are expected to store VariantContext objects using keys in the
     * form "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext variant to be tested
     * @param mother Hashtable of mother variants
     * @param father Hashtable of father variants
     * @return boolean
     * @since 1.0
     */
    public static boolean genotypeIsConsistent(VariantContext variantcontext, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        Genotype gt = variantcontext.getGenotype(0);
        //List<Allele> alleles = gt.getAlleles();
        //for(Allele a : alleles){
        //    System.out.println(a.getBaseString());
        //}
        String childallele1 = gt.getAllele(0).getBaseString();
        String childallele2 = gt.getAllele(1).getBaseString();
        VariantContext vcmother = null;
        VariantContext vcfather = null;
        if (mother != null) {
            vcmother = mother.get(key);
        }
        if (father != null) {
            vcfather = father.get(key);
        }
        if (vcmother != null && vcfather != null) {
            Genotype gtmother = vcmother.getGenotype(0);
            String motherallele1 = gtmother.getAllele(0).getBaseString();
            String motherallele2 = gtmother.getAllele(1).getBaseString();
            Genotype gtfather = vcfather.getGenotype(0);
            String fatherallele1 = gtfather.getAllele(0).getBaseString();
            String fatherallele2 = gtfather.getAllele(1).getBaseString();
            //System.out.println(childallele1 + childallele2 + " " + motherallele1 + motherallele2 + " " + fatherallele1 + fatherallele2);
            return genotypeIsConsistent(childallele1, childallele2, motherallele1, motherallele2, fatherallele1, fatherallele2);
        } else if (vcmother != null && vcfather == null) {
            Genotype gtmother = vcmother.getGenotype(0);
            String motherallele1 = gtmother.getAllele(0).getBaseString();
            String motherallele2 = gtmother.getAllele(1).getBaseString();
            String fatherallele1 = variantcontext.getReference().getBaseString();
            String fatherallele2 = variantcontext.getReference().getBaseString();
            return genotypeIsConsistent(childallele1, childallele2, motherallele1, motherallele2, fatherallele1, fatherallele2);

        } else if (vcmother == null && vcfather != null) {
            String motherallele1 = variantcontext.getReference().getBaseString();
            String motherallele2 = variantcontext.getReference().getBaseString();
            Genotype gtfather = vcfather.getGenotype(0);
            String fatherallele1 = gtfather.getAllele(0).getBaseString();
            String fatherallele2 = gtfather.getAllele(1).getBaseString();
            return genotypeIsConsistent(childallele1, childallele2, motherallele1, motherallele2, fatherallele1, fatherallele2);
        } else {
            String motherallele1 = variantcontext.getReference().getBaseString();
            String motherallele2 = variantcontext.getReference().getBaseString();
            String fatherallele1 = variantcontext.getReference().getBaseString();
            String fatherallele2 = variantcontext.getReference().getBaseString();
            return genotypeIsConsistent(childallele1, childallele2, motherallele1, motherallele2, fatherallele1, fatherallele2);
        }

    }

    /**
     * Tests if a given set of child alleles is consistent with the parent
     * genotype. Returns false if both child alleles are different from the
     * parent alleles. True otherwise.
     *
     * If a parent Hashtable&#60;String, VariantContext&#62; does not contain
     * the corresponding VariantContext object, homozygous reference genotype is
     * assumed for that parent.
     *
     * The Hashtable&#60;String, VariantContext&#62; objects for the parent is
     * expected to store VariantContext objects using keys in the form
     * "contig_start" e.g. "1_123456789"
     *
     * @author Heiko Mueller
     * @param variantcontext variant to be tested
     * @param parent Hashtable of variants of parent
     * @return boolean
     * @since 1.0
     */
    public static boolean genotypeIsConsistent(VariantContext variantcontext, Hashtable<String, VariantContext> parent) {
        String key = variantcontext.getContig() + "_" + variantcontext.getStart();
        Genotype gt = variantcontext.getGenotype(0);
        String childallele1 = gt.getAllele(0).getBaseString();
        String childallele2 = gt.getAllele(1).getBaseString();
        VariantContext vcparent = parent.get(key);
        if (vcparent != null) {
            Genotype gtparent = vcparent.getGenotype(0);
            String parentallele1 = gtparent.getAllele(0).getBaseString();
            String parentallele2 = gtparent.getAllele(1).getBaseString();
            return genotypeIsConsistent(childallele1, childallele2, parentallele1, parentallele2);
        } else {
            String parentallele1 = variantcontext.getReference().getBaseString();
            String parentallele2 = variantcontext.getReference().getBaseString();
            return genotypeIsConsistent(childallele1, childallele2, parentallele1, parentallele2);
        }
    }

    /**
     * Tests if a given combination of child alleles is consistent with the
     * parent genotypes. Returns true if one child allele is found in the mother
     * genotype and the other child allele is found in the father genotype.
     * False otherwise.
     *
     *
     * @author Heiko Mueller
     * @param childallele1
     * @param childallele2
     * @param motherallele1
     * @param motherallele2
     * @param fatherallele1
     * @param fatherallele2
     * @return boolean
     * @since 1.0
     */
    private static boolean genotypeIsConsistent(String childallele1, String childallele2, String motherallele1, String motherallele2, String fatherallele1, String fatherallele2) {
        String gt1 = motherallele1 + "/" + fatherallele1;
        String gt2 = motherallele1 + "/" + fatherallele2;
        String gt3 = motherallele2 + "/" + fatherallele1;
        String gt4 = motherallele2 + "/" + fatherallele2;
        String childgt1 = childallele1 + "/" + childallele2;
        String childgt2 = childallele2 + "/" + childallele1;
        if (childgt1.equals(gt1) || childgt1.equals(gt2) || childgt1.equals(gt3) || childgt1.equals(gt4)) {
            return true;
        } else if (childgt2.equals(gt1) || childgt2.equals(gt2) || childgt2.equals(gt3) || childgt2.equals(gt4)) {
            return true;
        }
        return false;
    }

    /**
     * Tests if a given combination of child alleles is consistent with the
     * parent genotype. Returns false if both child alleles are different from
     * the parent alleles. True otherwise.
     *
     *
     * @author Heiko Mueller
     * @param childallele1
     * @param childallele2
     * @param parentallele1
     * @param parentallele2
     * @return boolean
     * @since 1.0
     */
    private static boolean genotypeIsConsistent(String childallele1, String childallele2, String parentallele1, String parentallele2) {
        if (!(childallele1.equals(parentallele1) || childallele1.equals(parentallele2) || childallele2.equals(parentallele1) || childallele2.equals(parentallele2))) {
            return false;
        }
        return true;
    }

    /**
     * Converts VariantContext objects of a Hashtable to a sorted ArrayList of
     * VariantContext objects. The hashindex indicates the Hashtable contained
     * in hashlist to be converted.
     *
     *
     * @author Heiko Mueller
     * @param hashindex index of Hashtable
     * @param hashlist list of Hashtables holding variants
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> hashToList(int hashindex, ArrayList<Hashtable<String, VariantContext>> hashlist) {
        if (hashindex >= 0 && hashindex < hashlist.size()) {
            return hashToList(hashlist.get(hashindex));
        } else {
            return null;
        }
    }

    /**
     * Converts VariantContext objects of a Hashtable to a sorted ArrayList of
     * VariantContext objects.
     *
     *
     * @author Heiko Mueller
     * @param hash Hashtable holding variants
     * @return ArrayList&#60;VariantContext&#62;
     * @since 1.0
     */
    public static ArrayList<VariantContext> hashToList(Hashtable<String, VariantContext> hash) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        if (hash == null) {
            return result;
        }
        Iterator<String> it = hash.keySet().iterator();
        while (it.hasNext()) {
            result.add(hash.get(it.next()));
        }
        result.sort(new VariantContextComparator());
        return result;
    }

    /**
     * Tests for compound heterozygous variants.
     *
     * @param affectedHashIndex
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @param genesymbolfield the gene symbol annotation field id
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     *
     * public static ArrayList<VariantContext> compoundHeterozygous(int
     * affectedHashIndex, ArrayList<Hashtable<String, VariantContext>> affected,
     * ArrayList<Hashtable<String, VariantContext>> unaffected, Relationships
     * relations, String genesymbolfield){ ArrayList<VariantContext> al =
     * hashToList(affectedHashIndex, affected); //return
     * compoundHeterozygous(al, affected, unaffected, mother, father,
     * genesymbolfield); return null; }
     */
    /**
     * Tests for compound heterozygous variants.
     *
     * @param affectedHashIndex index of the Hashtable holding the variants of the affected individual
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @param genesymbolfield the gene symbol annotation field id
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> compoundHeterozygous(int affectedHashIndex, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father, String genesymbolfield) {
        ArrayList<VariantContext> al = hashToList(affectedHashIndex, affected);
        return compoundHeterozygous(al, affected, unaffected, mother, father, genesymbolfield);
    }

    /**
     * Tests for compound heterozygous variants.
     *
     * @param candidates list of candidate variants
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @param genesymbolfield the gene symbol annotation field id
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> compoundHeterozygous(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father, String genesymbolfield) {
        if (candidates == null) {
            return new ArrayList<VariantContext>();
        }
        if (genesymbolfield == null) {
            return new ArrayList<VariantContext>();
        }
        if (candidates != null && affected != null && unaffected == null) {
            return compoundHeterozygous(candidates, affected, genesymbolfield);
        }
        if (candidates != null && affected != null && unaffected != null) {
            if ((mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                return compoundHeterozygous(candidates, affected, unaffected, genesymbolfield);
            } else if (!(mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                candidates = testGenotypeConsistency(candidates, unaffected.get(mother), null);
                candidates = removeNonRefHeterozygotesWhereParentIsWT(candidates, unaffected.get(mother), null);
                return compoundHeterozygous(candidates, affected, unaffected, genesymbolfield);
            } else if ((mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                candidates = testGenotypeConsistency(candidates, null, unaffected.get(father));
                candidates = removeNonRefHeterozygotesWhereParentIsWT(candidates, null, unaffected.get(father));
                return compoundHeterozygous(candidates, affected, unaffected, genesymbolfield);
            } else {
                if (mother == father) {
                    return new ArrayList<VariantContext>();
                }
                candidates = testGenotypeConsistency(candidates, unaffected.get(mother), unaffected.get(father));
                candidates = removeNonRefHeterozygotesWhereParentIsWT(candidates, unaffected.get(mother), unaffected.get(father));
                return compoundHeterozygous(candidates, affected, unaffected, genesymbolfield);
            }

        }
        return new ArrayList<VariantContext>();
    }

    private static ArrayList<VariantContext> compoundHeterozygous(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, String genesymbolfield) {
        ArrayList<VariantContext> result = findHeterozygotesInAllAffectedIndividuals(candidates, affected);
        result = removeSingleHeterozygotesPerGene(result, genesymbolfield);
        return null;
    }

    private static ArrayList<VariantContext> compoundHeterozygous(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, String genesymbolfield) {
        ArrayList<VariantContext> result = findHeterozygotesInAllAffectedIndividuals(candidates, affected);
        result = testSegregationForCompoundHeterozygousCandidates(result, unaffected);
        result = removeSingleHeterozygotesPerGene(result, genesymbolfield);
        result = removeCompoundHeterozygotesWithGenotypesIdenticalToUnaffected(result, unaffected, genesymbolfield);
        return result;
    }

    private static ArrayList<VariantContext> findHeterozygotesInAllAffectedIndividuals(ArrayList<Hashtable<String, VariantContext>> affected) {
        //affectedVariants
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        Hashtable<String, VariantContext> ht = affected.get(0);
        Iterator<String> it = ht.keySet().iterator();

        while (it.hasNext()) {
            boolean pass = true;
            String key = it.next();
            for (Hashtable<String, VariantContext> h : affected) {
                VariantContext vc = h.get(key);
                if (vc != null) {
                    if (!(vc.getGenotype(0).isHet() || vc.getGenotype(0).isHetNonRef())) {
                        pass = false;
                    }
                } else {
                    pass = false;
                }
            }
            if (pass) {
                String genotypeString = affected.get(0).get(key).getGenotype(0).getGenotypeString();
                //test for identity of Genotype Strings
                for (Hashtable<String, VariantContext> h : affected) {
                    String genotypeString2 = h.get(key).getGenotype(0).getGenotypeString();
                    if (!genotypeString.equals(genotypeString2)) {
                        pass = false;
                    }
                }
            }
            if (pass) {
                result.add(affected.get(0).get(key));
            }
        }
        return result;
    }

    private static ArrayList<VariantContext> findHeterozygotesInAllAffectedIndividuals(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected) {
        //affectedVariants
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        //Hashtable<String, VariantContext> ht = affected.get(0);
        //Iterator<String> it = ht.keySet().iterator();
        String key = "";
        for (VariantContext c : candidates) {
            boolean pass = true;
            if (!(c.getGenotype(0).isHet() || c.getGenotype(0).isHetNonRef())) {
                pass = false;
            } else {
                key = c.getContig() + "_" + c.getStart();
                for (Hashtable<String, VariantContext> h : affected) {
                    VariantContext vc = h.get(key);
                    if (vc != null) {
                        if (!(vc.getGenotype(0).isHet() || vc.getGenotype(0).isHetNonRef())) {
                            pass = false;
                        }
                    } else {
                        pass = false;
                    }
                }
            }
            if (pass) {
                String genotypeString = affected.get(0).get(key).getGenotype(0).getGenotypeString();
                //test for identity of Genotype Strings
                for (Hashtable<String, VariantContext> h : affected) {
                    String genotypeString2 = h.get(key).getGenotype(0).getGenotypeString();
                    if (!genotypeString.equals(genotypeString2)) {
                        pass = false;
                    }
                }
            }
            if (pass) {
                result.add(affected.get(0).get(key));
            }
        }
        return result;
    }

    private static ArrayList<VariantContext> testSegregationForCompoundHeterozygousCandidates(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> unaffected) {
        //unaffectedVariants
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext v : candidates) {
            String key = v.getContig() + "_" + v.getStart();
            boolean pass = true;
            for (Hashtable<String, VariantContext> h : unaffected) {
                VariantContext vc = h.get(key);
                if (vc != null) {
                    Genotype g = vc.getGenotype(0);
                    if (g.isHomVar()) {
                        pass = false;
                    }
                } else {
                    pass = true;
                }
            }
            if (pass) {
                result.add(v);
            }
        }
        return result;
    }

    private static ArrayList<VariantContext> removeSingleHeterozygotesPerGene(ArrayList<VariantContext> candidates, String genesymbolfield) {
        if (genesymbolfield == null) {
            return new ArrayList<VariantContext>();
        }
        Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
        for (VariantContext v : candidates) {
            Object o = v.getAttribute(genesymbolfield);
            if (o != null) {
                String gene = o.toString();
                if (!counts.containsKey(gene)) {
                    counts.put(gene, 1);
                } else {
                    int i = counts.get(gene);
                    i++;
                    counts.remove(gene);
                    counts.put(gene, i);
                }
            }
        }
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext v : candidates) {
            Object o = v.getAttribute(genesymbolfield);
            if (o != null) {
                String gene = v.getAttribute(genesymbolfield).toString();
                if (counts.get(gene) > 1) {
                    result.add(v);
                }
            }
        }
        return result;
    }

    private static ArrayList<VariantContext> removeCompoundHeterozygotesWithGenotypesIdenticalToUnaffected(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> unaffected, String genesymbolfield) {
        Hashtable<String, Hashtable<String, VariantContext>> counts = new Hashtable<String, Hashtable<String, VariantContext>>();
        for (VariantContext v : candidates) {
            String gene = v.getAttribute(genesymbolfield).toString();
            if (!counts.containsKey(gene)) {
                Hashtable<String, VariantContext> ht = new Hashtable<String, VariantContext>();
                ht.put(v.getContig() + "_" + v.getStart(), v);
                counts.put(gene, ht);
            } else {
                counts.get(gene).put(v.getContig() + "_" + v.getStart(), v);
            }
        }
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        Set<String> genekeys = counts.keySet();
        for (String s : genekeys) {
            Hashtable<String, VariantContext> ht = counts.get(s);
            Set<String> variantkeys = ht.keySet();
            boolean pass = true;
            StringBuilder gtsb = new StringBuilder();
            for (String var : variantkeys) {
                VariantContext vc = ht.get(var);
                String gt = vc.getGenotype(0).getGenotypeString();
                gtsb.append(gt);

            }
            for (int i = 0; i < unaffected.size(); i++) {
                Hashtable<String, VariantContext> ua = unaffected.get(i);
                StringBuilder gtsbua = new StringBuilder();
                for (String var : variantkeys) {
                    VariantContext vc = ua.get(var);
                    if (vc != null) {
                        String gt = vc.getGenotype(0).getGenotypeString();
                        gtsbua.append(gt);
                    }

                }
                if (gtsbua.toString().equals(gtsb.toString())) {
                    pass = false;
                }
                //ArrayList<Hashtable<String, VariantContext>> unaffectedVariants;
            }
            if (pass) {
                for (String var : variantkeys) {
                    result.add(ht.get(var));
                }
            }
        }
        return result;
    }

    private static ArrayList<VariantContext> removeNonRefHeterozygotesWhereParentIsWT(ArrayList<VariantContext> candidates, Hashtable<String, VariantContext> mum, Hashtable<String, VariantContext> dad) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext v : candidates) {
            String key = v.getContig() + "_" + v.getStart();
            if (v.getGenotype(0).isHetNonRef()) {
                if (mum != null) {
                    if (mum.get(key) == null) {
                        result.add(v);
                    }
                } else if (dad != null && mum == null) {
                    if (dad.get(key) == null) {
                        result.add(v);
                    }
                }

            } else {
                result.add(v);
            }

        }
        return result;
    }

    /**
     * Tests for compound X-linked variants.
     *
     * @param candidates list of candidate variants
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> xLinked(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father) {
        if (candidates == null || affected == null || unaffected == null || mother < 0 || father < 0) {
            return new ArrayList<VariantContext>();
        } else {
            if ((mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                return new ArrayList<VariantContext>();
            } else if (!(mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                return new ArrayList<VariantContext>();
            } else if ((mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                return new ArrayList<VariantContext>();
            } else {
                if (mother == father) {
                    return new ArrayList<VariantContext>();
                }
                candidates = testGenotypeConsistency(candidates, unaffected.get(mother), unaffected.get(father));
                return findXLinkedVariants(candidates, unaffected.get(mother), unaffected.get(father));
            }

        }
    }

    /**
     * Tests for compound X-linked variants.
     *
     * @param candidates list of candidate variants
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother hash of mother's variants
     * @param father hash of father's variants
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> xLinked(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        if (candidates == null || affected == null || unaffected == null || (mother == null && father == null)) {
            /*
            if (candidates == null){
                System.out.println("cannot perform X-linked analysis candidates == null");
            }
            if (affected == null){
                System.out.println("cannot perform X-linked analysis affected == null");
            }
            if (unaffected == null){
                System.out.println("cannot perform X-linked analysis unaffected == null");
            }
            if (mother == null){
                System.out.println("cannot perform X-linked analysis mother == null");
            }
            if (father == null){
                System.out.println("cannot perform X-linked analysis father == null");
            }
            */
            
            return new ArrayList<VariantContext>();
        } else {
            //candidates = testGenotypeConsistency(candidates, mother, father);
            return findXLinkedVariants(candidates, mother, father);
        }
    }

    private static ArrayList<VariantContext> findXLinkedVariants(ArrayList<VariantContext> candidates, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        //affectedVariants
        if(mother == null){
            return new ArrayList<VariantContext>();
        }
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        for (VariantContext vc : candidates) {
            boolean pass = true;
            String key = vc.getContig() + "_" + vc.getStart();
            //System.out.println("testing x linked " + key);
            if (key.startsWith("X") || key.startsWith("x")) {
                if (vc != null) {
                    if (!(vc.getGenotype(0).isHomVar())) {
                        pass = false;
                    }
                } else {
                    pass = false;
                }
                VariantContext vcm = mother.get(key);
                if (vcm != null) {
                    if (!(vcm.getGenotype(0).isHet() || vcm.getGenotype(0).isHetNonRef())) {
                        pass = false;
                    }
                } else {
                    //pass = false;
                }

                if(father != null){
                    VariantContext vcf = father.get(key);
                    if (vcf != null) {
                        if ((vcf.getGenotype(0).getGenotypeString().equals(vc.getGenotype(0).getGenotypeString()))) {
                            pass = false;
                        }
                    } else {
                        //pass = false;
                    }
                }

                if (pass) {
                    result.add(vc);
                }
            }
        }
        return result;
    }

    /**
     * Tests for dominant variants.
     *
     * @param candidates list of candidate variants
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> dominant(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father) {
        if (candidates == null || affected == null || unaffected == null || mother < 0 || father < 0) {
            return new ArrayList<VariantContext>();
        } else {
            if ((mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                return findAutosomalDominantVariants(candidates, affected, unaffected, mother, father);
            } else if (!(mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                //candidates = testGenotypeConsistency(candidates, unaffected.get(mother), null);
                return findAutosomalDominantVariants(candidates, affected, unaffected, mother, father);
            } else if ((mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                //candidates = testGenotypeConsistency(candidates, null, unaffected.get(father));
                return findAutosomalDominantVariants(candidates, affected, unaffected, mother, father);
            } else {
                if (mother == father) {
                    return new ArrayList<VariantContext>();
                }
                //candidates = testGenotypeConsistency(candidates, unaffected.get(mother), unaffected.get(father));
                return findAutosomalDominantVariants(candidates, affected, unaffected, mother, father);
            }

        }
    }

    private static ArrayList<VariantContext> findAutosomalDominantVariants(ArrayList<VariantContext> candidates, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, int mother, int father) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();

        for (VariantContext vc : candidates) {
            boolean pass = true;
            String key = vc.getContig() + "_" + vc.getStart();
            //test for heterozygosity in all affected         
            if (!(vc.getGenotype(0).isHet() && !vc.getGenotype(0).isHetNonRef())) {
                pass = false;
            }
            if (pass) {
                if (genotypeIsPresentInIndividuals(vc, unaffected)) {
                    pass = false;
                }
            }
            if (pass) {
                if (!genotypeIsPresentInIndividuals(vc, affected)) {
                    pass = false;
                }
            }
            if (!(mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                if (genotypeIsConsistent(vc, unaffected.get(mother), unaffected.get(father))) {
                    pass = false;
                }
            } else if ((mother < 0 || mother >= unaffected.size()) && !(father < 0 || father >= unaffected.size())) {
                if (genotypeIsConsistent(vc, null, unaffected.get(father))) {
                    pass = false;
                }
            } else if (!(mother < 0 || mother >= unaffected.size()) && (father < 0 || father >= unaffected.size())) {
                if (genotypeIsConsistent(vc, unaffected.get(mother), null)) {
                    pass = false;
                }
            }
            if (pass) {
                result.add(vc);
            }
        }
        return result;
    }

    /**
     * Tests for dominant variants.
     *
     * @param startsample sample to start with
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> dominant(int startsample, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected) {
        if (affected == null || unaffected == null) {
            return new ArrayList<VariantContext>();
        } else {
            return findAutosomalDominantVariants(startsample, affected, unaffected);
        }
    }

    /**
     * Tests for dominant variants.
     *
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    private static ArrayList<VariantContext> findAutosomalDominantVariants(int startsample, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        ArrayList<VariantContext> candidates = hashToList(startsample, affected);
        //HashSet<VariantContext> hs = new HashSet<VariantContext>();
        for (VariantContext vc : candidates) {
            boolean pass = true;
            String key = vc.getContig() + "_" + vc.getStart();
            //test for heterozygosity in all affected     
            //System.out.println(key);
            if (pass) {
                if (genotypeIsPresentInIndividuals(vc, unaffected)) {
                    pass = false;
                }
                //System.out.println("genotypeIsPresentInUnaffectedIndividuals " + pass);
            }
            Genotype gt = vc.getGenotype(0);
            if (pass) {
                if (gt.isHomRef()) {
                    pass = false;
                }
            }
            if (pass) {
                List<Allele> alleles = gt.getAlleles();
                ArrayList<Boolean> bools = new ArrayList<Boolean>();
                Allele ref = vc.getReference();
                for (Allele a : alleles) {
                    if (!ref.getBaseString().equals(a.getBaseString())) {
                        if (!alleleIsPresentInIndividuals(vc, a, unaffected) && alleleIsPresentInAllIndividuals(vc, a, affected)) {
                            //System.out.println(key + a.getBaseString() + " true");
                            bools.add(new Boolean(true));
                        } else {
                                //System.out.println(key + a.getBaseString() + " false");
                            //bools.add(new Boolean(false));
                        }
                    }
                }
                pass = false;
                if (bools.size() > 0) {
                    pass = true;
                }

            }

            /*
             if(pass){
             if(alleleIsPresentInIndividuals(vc, unaffected)){
             pass = false;
             }
             System.out.println("alleleIsPresentInUnaffectedIndividuals " + pass);
             }
             if(pass){
             if(!alleleIsPresentInAllIndividuals(vc, affected)){
             pass = false;
             }
             System.out.println("!alleleIsPresentInAllAffectedIndividuals " + pass);
             }   
             */
            if (pass) {
                result.add(vc);
            }
        }
        return result;
    }

    /**
     * Tests for denovo variants.
     *
     * @param child hash of variants for child
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> dominantDeNovo(Hashtable<String, VariantContext> child, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        if (affected == null || unaffected == null || (mother == null && father == null)) {
            return new ArrayList<VariantContext>();
        } else {
            return findAutosomalDominantDeNovoVariants(child, affected, unaffected, mother, father);
        }
    }
    
    /**
     * Tests for denovo variants. Child genotype must be inconsistent 
     * with parent genotypes and new variant genotype must be 
     * absent from unaffected individuals.
     *
     * @param child list of variants for child
     * @param relation Family relationship for affected child
     * @param unaffected hash of variants for unaffected individuals
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    public static ArrayList<VariantContext> denovo(ArrayList<VariantContext> child, Relationship relation, ArrayList<Hashtable<String, VariantContext>> unaffected) {  
        ArrayList<VariantContext> candidates = testGenotypeInConsistency(child, relation);  
        ArrayList<VariantContext> results = new ArrayList<VariantContext>();
        for(VariantContext vc : candidates){
            if(!genotypeIsPresentInIndividuals(vc, unaffected)){
                if(!vc.getGenotype(0).isHomRef()){
                    results.add(vc);
                }
            }
        }
        return results;    
    }

    /**
     * Tests for denovo variants.
     *
     * @param child
     * @param affected hash of variants for affected individuals
     * @param unaffected hash of variants for unaffected individuals
     * @param mother index of mother hash in unaffected
     * @param father index of father hash in unaffected
     * @return ArrayList&#60;VariantContext&#62;
     * @author Heiko Müller
     * @since 1.0
     */
    private static ArrayList<VariantContext> findAutosomalDominantDeNovoVariants(Hashtable<String, VariantContext> child, ArrayList<Hashtable<String, VariantContext>> affected, ArrayList<Hashtable<String, VariantContext>> unaffected, Hashtable<String, VariantContext> mother, Hashtable<String, VariantContext> father) {
        ArrayList<VariantContext> result = new ArrayList<VariantContext>();
        ArrayList<VariantContext> candidates = hashToList(child);
        for (VariantContext vc : candidates) {
            boolean pass = true;
            String key = vc.getContig() + "_" + vc.getStart();
            //test for heterozygosity in all affected         
            //if (!(vc.getGenotype(0).isHet() && !vc.getGenotype(0).isHetNonRef())) {
            //    pass = false;
            //}
            //if (pass) {
            //    if (genotypeIsPresentInIndividuals(vc, unaffected)) {
            //        pass = false;
            //    }
            //}
            //if (pass) {
            //    if (!genotypeIsPresentInIndividuals(vc, affected)) {
            //        pass = false;
            //    }
            //}
            if (mother != null && father != null) {
                if (genotypeIsConsistent(vc, mother, father)) {
                    pass = false;
                }
            } else if (father != null) {
                if (genotypeIsConsistent(vc, null, father)) {
                    pass = false;
                }
            } else if (mother != null) {
                if (genotypeIsConsistent(vc, mother, null)) {
                    pass = false;
                }
            }
            if (pass) {
                result.add(vc);
            }
        }
        return result;
    }

}
