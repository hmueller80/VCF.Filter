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

/** 
 * Stores Filter settings.
 * FilterSettings.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class FilterSettings {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    String id;
    String criterion1;
    String criterion2;
    String criterion3;
    boolean andnot;
    
    /**
    * Creates new FilterSettings.
    * 
    * @param scenarioFileEntry coded scenario String
    * @author Heiko Müller
    * @since 1.0
    */
    public FilterSettings(String scenarioFileEntry){
        String[] sa = scenarioFileEntry.split("\t");
        if(sa != null && sa.length == 10 && sa[0].equals("Filter id:")){
            this.id = sa[1];
            this.criterion1 = sa[5];
            this.criterion2 = sa[7];
            this.criterion3 = sa[9];
            this.andnot = parseBoolean(sa[3]);
        }else if(sa != null && sa.length == 9 && sa[0].equals("Filter id:")){
            this.id = sa[1];
            this.criterion1 = sa[5];
            this.criterion2 = sa[7];
            this.criterion3 = "";
            this.andnot = parseBoolean(sa[3]);
        }
        //System.out.println(andnot);
    }

    /**
    * Returns the filter id.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getId() {
        return id;
    }

    /**
    * Returns criterion1.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion1() {
        return criterion1;
    }

    /**
    * Returns criterion2.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion2() {
        return criterion2;
    }

    /**
    * Returns criterion3.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getCriterion3() {
        return criterion3;
    }

    /**
    * Returns the and not indicator.
    * 
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    public boolean isAndnot() {
        return andnot;
    }
    
    /**
    * Attempts to parse the provided String as a boolean.
    * 
    * @param s
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean parseBoolean(String s){
        if(s.toUpperCase().equals("TRUE")){
            return true;
        }else{
            return false;
        }
    }
    
    
    
}
