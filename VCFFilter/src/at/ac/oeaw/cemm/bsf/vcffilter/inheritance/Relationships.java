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

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;

/**
 * A class for defining the family relationships of all affected individuals.
 * Relationships.java 02 Dec 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Relationships extends javax.swing.JDialog {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * List of all relationships.
     */
    ArrayList<Relationship> relationships;
    
    /**
     * Reference to the GUI.
     */
    VCFFilter gui;

    /**
     * Creates new form Relationships
     * 
     * @param gui graphical user interface
     * @param modal is dialog modal
     */
    public Relationships(VCFFilter gui, boolean modal) {
        super(gui, modal);
        this.gui = gui;
        initComponents();
        relationships = new ArrayList<Relationship>();
        //addRelationship();
        centerWindow(this);
    }    
    
    /**
     * Creates new form Relationships
     * 
     * @param gui graphical user interface
     * @param modal is dialog modal
     * @param affected - VCF files of affected individuals
     * @param unaffected - VCF files of unaffected individuals
     */
    public Relationships(VCFFilter gui, boolean modal, File[] affected, File[] unaffected) {
        super(gui, modal);
        initComponents();
        relationships = new ArrayList<Relationship>();
        for(File f : affected){
            Relationship r = new Relationship(f.getName(), unaffected, affected);
            relationships.add(r);
        }
        addRelationships();
        centerWindow(this);
    }
    
    /**
     * Adds relationships to the layout
     * 
     */
    public void addRelationships(){
        int gap = 10;
        for(Relationship r : relationships){
            addToLayout(r, 10, gap, 10, 10);
            gap = gap + 80;
        }        
    }
    
    /**
     * Adds relationships to the layout
     * 
     * @param r - a relationship to be added to the layout
     * 
     */
    public void addRelationship(Relationship r){
        relationships.add(r);
        int gap = 10;
        for(Relationship rel : relationships){
            addToLayout(rel, 10, gap, 10, 10);
            gap = gap + 80;
        }        
    }
    
    /*     
    public void addRelationship(){
        File[] a1 = new File[2];
        a1[0] = new File("C://Temp/a11");
        a1[1] = new File("C://Temp/a12");
        File[] a2 = new File[2];
        a2[0] = new File("C://Temp/a21");
        a2[1] = new File("C://Temp/a22");
        Relationship r = new Relationship("p1", a1, a2);
        addToLayout(r, 10, 10, 10, 10);
        Relationship r2 = new Relationship("p2", a1, a2);
        addToLayout(r2, 10, 90, 10, 10);
        Relationship r3 = new Relationship("p3", a1, a2);
        addToLayout(r3, 10, 170, 10, 10);
        Relationship r4 = new Relationship("p4", a1, a2);
        addToLayout(r4, 10, 260, 10, 10);
        Relationship r5 = new Relationship("p5", a1, a2);
        addToLayout(r5, 10, 350, 10, 10);
        
    }
    */
    
    
    /**
     * Adds relationships to the layout
     * 
     * @param r - a relationship to be added to the layout
     * @param hgap
     * @param vgap
     * @param hcgap
     * @param vcgap
     * 
     */
    private void addToLayout(Relationship r, int hgap, int vgap, int hcgap, int vcgap) {
            javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanelLayout);
            jPanelLayout.setHorizontalGroup(
                    jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                            .addGap(hgap, hgap, hgap)
                            .addComponent(r)
                            .addContainerGap(hcgap, Short.MAX_VALUE))
            );
            jPanelLayout.setVerticalGroup(
                    jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelLayout.createSequentialGroup()
                            .addGap(vgap, vgap, vgap)
                            .addComponent(r)
                            .addContainerGap(vcgap, Short.MAX_VALUE))
            );
            pack();
    }
       
    /**
    * Centers this frame on the screen according to screen resolution.
    *
    * @author Heiko Müller
    * @param frame the frame to be centered
    * @since 1.0
    */
    public void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    /**
    * Returns the list of defined relationships
    *
    * @return ArrayList&#60;Relationship&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<Relationship> getRelationships() {
        return relationships;
    } 
    
    /**
    * Returns the relationship for a given individual.
    *
    * @param name - the filename of the individuals VCF file
    * @return ArrayList&#60;Relationship&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public Relationship getRelationshipForIndividual(String name){
        for(Relationship r : relationships){
            if(name.equals(r.getIndividual())){
                return r;
            }
        }  
        return null;
    }    
    
    /**
     * Remove an individual from the list of relationships
     * @param name name of the individual to be removed from relationships
     */
    public void removeRelationshipForIndividual(String name){
        for(int i = 0; i < relationships.size(); i++){
            Relationship r = relationships.get(i);
            if(name.equals(r.getIndividual())){
                relationships.remove(i);
                break;
            }
        }  
        int gap = 10;
        for(Relationship rel : relationships){
            addToLayout(rel, 10, gap, 10, 10);
            gap = gap + 80;
        } 
        //return null;
    }    
     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Relationships");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Affected individuals"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 832, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(396, 396, 396))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * jButton1 action performed event handler. Closes the Relationships viewer.
    *
    * @param evt
    * @author Heiko Müller
    * @since 1.0
    */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(gui != null){
            gui.updateRelationships();
        }
        this.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Relationships.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Relationships.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Relationships.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Relationships.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Relationships dialog = new Relationships(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
                */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
