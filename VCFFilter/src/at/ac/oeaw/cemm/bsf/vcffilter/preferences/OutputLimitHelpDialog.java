/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.oeaw.cemm.bsf.vcffilter.preferences;

import java.awt.Frame;

/**
 *
 * @author hmueller
 */
public class OutputLimitHelpDialog extends HelpDialog{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    private static final String title = "Output limit help";
    private static final String helpText = "Outputting too many variants can overwhelm the graphics capabilities of your computer. "
            + "Set this number according to your needs but beware that too large values may lead to freezing of the application. "
            ;
    
    /**
     * 
     * @param parent parent frame
     * @param modal whether dialog blocks parent frame until feedback is received
     */
    public OutputLimitHelpDialog(Frame parent, boolean modal){
        super(parent, modal);        
        setDialogTitle(title);
        setHelpText(helpText);
        init();
    
    }
    
}
