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

import java.util.ArrayList;

/**
 * Test class for generating in-silico matings of all possible genotypes. 
 * GenotypeFactory.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class GenotypeFactory {
    

    static String[][] genotypes = {{"A","A"},{"A","C"},{"A","G"},{"A","T"},{"C","C"},{"C","G"},{"C","T"},{"G","G"},{"G","T"},{"T","T"}};
    static String[] mother = {"A", "C", "G", "T"};
    static String[] father = {"A", "C", "G", "T"};
    
    /**
     * 
     * @return ArrayList&#60;Trio&#62; mated alleles list
     */
    public static ArrayList<Trio> mateAlleles(){
        ArrayList<Trio> result = new ArrayList<Trio>();
        for(String m : mother){
            for(String f : father){
                Trio t = new Trio();
                t.child = m + "/" + f;
                t.mother = m;
                t.father = f;
                result.add(t);
            }
        }
        return result;
    }
    
    /**
     * 
     * @return mated genotypes list
     */
    public static ArrayList<Trio> mateGenotypes(){
        ArrayList<Trio> result = new ArrayList<Trio>();
        for(String[] m : genotypes){
            for(String[] f : genotypes){
                Trio t1 = new Trio();
                Trio t2 = new Trio();
                Trio t3 = new Trio();
                Trio t4 = new Trio();
                if(m[0].compareTo(f[0]) <= 0){
                    t1.child = m[0] + "/" + f[0];
                }else{
                    t1.child = f[0] + "/" + m[0];
                }
                if(m[0].compareTo(f[1]) <= 0){
                    t2.child = m[0] + "/" + f[1];
                }else{
                    t2.child = f[1] + "/" + m[0];
                }
                if(m[1].compareTo(f[0]) <= 0){
                    t3.child = m[1] + "/" + f[0];
                }else{
                    t3.child = f[0] + "/" + m[1];
                }
                if(m[1].compareTo(f[1]) <= 0){
                    t4.child = m[1] + "/" + f[1];
                }else{
                    t4.child = f[1] + "/" + m[1];
                }
                //t2.child = m[0] + "/" + f[1];
                //t3.child = m[1] + "/" + f[0];
                //t4.child = m[1] + "/" + f[1];
                t1.mother = m[0] + "/" + m[1];
                t2.mother = m[0] + "/" + m[1];
                t3.mother = m[0] + "/" + m[1];
                t4.mother = m[0] + "/" + m[1];
                t1.father = f[0] + "/" + f[1];
                t2.father = f[0] + "/" + f[1];
                t3.father = f[0] + "/" + f[1];
                t4.father = f[0] + "/" + f[1];
                result.add(t1);
                result.add(t2);
                result.add(t3);
                result.add(t4);
            }
        }
        return result;
    }
    
    /**
     * 
     * @param a input parameters
     */
    public static void main(String[] a){
        //ArrayList<Trio> trios = mateAlleles();
        ArrayList<Trio> trios = mateGenotypes();
        for(Trio t : trios){
            System.out.println(t.dump());
        }
    }
    
}
