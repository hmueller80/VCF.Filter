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
package at.ac.oeaw.cemm.bsf.vcffilter.preferences;

import java.awt.Frame;

/** 
 * HyperlinksHelpDialog.
 * HyperlinksHelpDialog.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class HyperlinksHelpDialog extends HelpDialog{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    private static final String title = "Hyperlinks help";
    private static final String helpText = "Here, hyperlinks for searching online resources of interest can be defined for up to five selected fields. "
            + "VCFFilter concatenates the hyperlink parts and the selected field values to generate the hyperlink that is reported in the output in the column with the name defined in the ID field.\r\n\r\n"
            + "For example, in order to generate a hyperlink that permits searching the Entrez gene resource with the gene symbol of a variant, define "
            + "Hyperlink left = \"https://www.ncbi.nlm.nih.gov/gene/?term=\", "
            + "Search field = \"SNPEFF_GENE_NAME\", Hyperlink right = \"[sym]\". "
            + "The hyperlink \"https://www.ncbi.nlm.nih.gov/gene/?term=CCND1[sym]\" will be reported in the output with column name SNPEFF_GENE_NAME_hyperlink for all variants of the CCND1 gene."
            
            ;
    
    /**
    * Creates new HyperlinksHelpDialog.
    * 
    * @param parent parent frame
    * @param modal is dialog modal
    * @author Heiko Müller
    * @since 1.0
    */
    public HyperlinksHelpDialog(Frame parent, boolean modal){
        super(parent, modal);        
        setDialogTitle(title);
        setHelpText(helpText);
        init();
    }
}
