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
package at.ac.oeaw.cemm.bsf.vcffilter;

/**
 * NoExampleVCFFileException class of VCFFilter application. 
 * Thrown when an example VCF file cannot be found or read.
 * NoExampleVCFFileException.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class NoExampleVCFFileException extends RuntimeException{
    
    /**
    * Creates a NoExampleVCFFileException  
    *
    * @author Heiko Müller
    * @version 1.0
    * @since 1.0
    */
    public NoExampleVCFFileException(){
        super();
    }
    
    
    /**
    * Creates a NoExampleVCFFileException  
    *
    * @param message a message to be reported
    * @author Heiko Müller
    * @version 1.0
    * @since 1.0
    */
    public NoExampleVCFFileException(String message) {
        super(message);
    }
    
    /**
    * Creates a NoExampleVCFFileException  
    *
    * @param message a message to be reported
    * @param cause the cause of the exception
    * @author Heiko Müller
    * @version 1.0
    * @since 1.0
    */
    public NoExampleVCFFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
    * Creates a NoExampleVCFFileException  
    *
    * @param cause the cause of the exception
    * @author Heiko Müller
    * @version 1.0
    * @since 1.0
    */
    public NoExampleVCFFileException(Throwable cause) {
        super(cause);
    }
    
    /**
    * Creates a NoExampleVCFFileException  
    *
    * @param message a message to be reported
    * @param cause the cause of the exception
    * @param enableSuppression enableSuppression
    * @param writableStackTrace writableStackTrace
    * @author Heiko Müller
    * @version 1.0
    * @since 1.0
    */
    protected NoExampleVCFFileException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
