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
package at.ac.oeaw.cemm.bsf.vcffilter.excelparser;


/**
 * Abstract super class of ExcelParser.java
 * Parser.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class Parser {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The file name of the file to be read and parsed.
     */
    protected String fileName;
    
    /**
     * The raw file content as text.
     */
    protected String rawFileContent;
    
    /**
    * Creates new Parser.
    * 
    * @param filename input file
    * @author Heiko Müller
    * @since 1.0
    */
    public Parser(String filename){
        fileName = filename;
        readFile();
    }
    
    /**
     * Parser constructor
     */
    public Parser(){
        
    }
    
    /**
    * Getter for file name
    *
    * @return String file name
    * @author Heiko Müller
    * @since 1.0
    */
    public String getFileName(){
        return fileName;
    }
    
    /**
    * Getter for file content
    *
    * @return String file content as text
    * @author Heiko Müller
    * @since 1.0
    */
    public String getRawFileContent(){
        return rawFileContent;
    }
    
    /**
    * Abstract method readFile();
    *
    * @author Heiko Müller
    * @since 1.0
    */
    protected abstract void readFile();
    
    /**
    * Abstract method parse();
    *
    * @author Heiko Müller
    * @since 1.0
    */
    protected abstract void parse();
    
}
