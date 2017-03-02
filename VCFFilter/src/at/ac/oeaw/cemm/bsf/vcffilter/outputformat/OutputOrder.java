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

/** 
 * Utility for defining the order of output columns.
 * OutputOrder.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class OutputOrder implements Comparable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /** column id */
    String id;
    
    /** order */
    int order;
    
    /**
    * Creates new OutputOrder.
    * 
    * @param id column header
    * @param order rank
    * @author Heiko Müller
    * @since 1.0
    */
    public OutputOrder(String id, int order){
        this.id = id;
        this.order = order;
    }

    @Override
    public int compareTo(Object o) {
        OutputOrder other = (OutputOrder)o;
        if(other.order > this.order){
            return -1;
        }else if(other.order < this.order){
            return 1;
        }else{
            return 0;
        }
    }

    /**
    * Returns the column id.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getId() {
        return id;
    }

    /**
    * Returns the output order.
    * 
    * @return int
    * @author Heiko Müller
    * @since 1.0
    */
    public int getOrder() {
        return order;
    }
    
    
    
}
