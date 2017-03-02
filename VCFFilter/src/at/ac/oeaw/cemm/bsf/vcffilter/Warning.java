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

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * Renders a graphical modal warning dialog.
 * Warning.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Warning extends JDialog {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
    * Creates a new instance of Warning.
    *
    * @param parent parent dialog
    * @param message message being reported
    * @author Heiko Müller
    * @since 1.0
    */
    public Warning(JDialog parent, String message) {
        super(parent);
        init(message);
        
    }
    
    /**
    * Creates a new instance of Warning.
    *
    * @param parent parent frame
    * @param message message being reported
    * @author Heiko Müller
    * @since 1.0
    */
    public Warning(JFrame parent, String message) {
        super(parent);
        init(message);    
    }
    
    /**
    * Creates a new instance of Warning. Supports setting custom title.
    *
    * @param parent parent frame
    * @param message message being reported
    * @param title Dialog title
    * @author Heiko Müller
    * @since 1.0
    */
    public Warning(JFrame parent, String message, String title) {
        super(parent);        
        setTitle(title);
        init(message);
    }
    
    /**
    * Close action event handler.
    *
    * @param evt close Hint action performed
    * @author Heiko Müller
    * @since 1.0
    */
    public void closeActionPerformed(java.awt.event.ActionEvent evt){
        this.dispose();
    }
    
    /**
    * Centers this frame on the screen according to screen resolution.
    *
    * @author Heiko Mueller
    * @param frame
    * @since 1.0
    */
    private void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
    
    /**
    * Initializes graphical components.
    *
    * @author Heiko Mueller
    * @param message the warning message
    * @since 1.0
    */
    private void init(String message){
        this.setModal(true);
        setTitle("Hint");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JLabel(message), BorderLayout.NORTH);
        JPanel jp = new JPanel();
        JButton ok = new JButton("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        jp.add(ok);
        getContentPane().add(jp, BorderLayout.SOUTH);
        setBounds(100, 100, 300, 100);
        pack();
        centerWindow(this);
        setVisible(true);
    }
    
}
