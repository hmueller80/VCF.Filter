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

import htsjdk.variant.variantcontext.VariantContext;
import java.io.File;
import java.util.Hashtable;

/**
 * A class for defining the family relationships of an individual (mother, father, male/female).
 * Relationship.java 02 Dec 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Relationship extends javax.swing.JPanel {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * individual
     */
    String individual = "";
    
    /**
     * Mother
     */
    String mother = "None";
    
    /**
     * Father
     */
    String father = "None";
    
    /**
     * male
     */
    boolean male = true;
    
    /**
     * mother variant hash
     */
    Hashtable<String, VariantContext> motherHash;
    
    /**
     * father variant hash
     */
    Hashtable<String, VariantContext> fatherHash;

    /**
     * Creates new form Relationship
     */
    public Relationship() {
        initComponents();
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
    }
    
    /**
     * Creates new form Relationship
     * 
     * @param individual - filename of the individual's VCF file
     * @param unaffected - array of files of unaffected individual's VCF files
     * @param affected - array of files of affected individual's VCF files
     * @author Heiko Müller
     * @since 1.0
     */
    public Relationship(String individual, File[] unaffected, File[] affected) {
        initComponents();
        this.individual = individual;
        jTextField1.setText(this.individual);
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);
        if(unaffected != null && affected != null){
            jComboBox1.addItem("NA");
            jComboBox2.addItem("NA");
            for(File f : unaffected){
                jComboBox1.addItem(f.getName());
                jComboBox2.addItem(f.getName());
            }
            for(File f : affected){
                jComboBox1.addItem(f.getName());
                jComboBox2.addItem(f.getName());
            } 
        }       
    }

    /**
     * Returns the name of the individual, which is identical to the filename of the individual's VCF file.
     * 
     * @return String - filename of the individual's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public String getIndividual() {
        return individual;
    }

    /**
     * Returns the name of the mother, which is identical to the filename of the mother's VCF file.
     * 
     * @return String - filename of the mother's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public String getMother() {
        return mother;
    }

    /**
     * Returns the name of the father, which is identical to the filename of the father's VCF file.
     * 
     * @return String - filename of the father's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public String getFather() {
        return father;
    }

    /**
     * Sets the name of the individual.
     * 
     * @param individual - filename of the individual's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public void setIndividual(String individual) {
        this.individual = individual;
    }

    /**
     * Sets the name of the mother.
     * 
     * @param mother - filename of the mother's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public void setMother(String mother) {
        this.mother = mother;
        jComboBox1.setSelectedItem(mother);
    }

    /**
     * Sets the name of the father.
     * 
     * @param father - filename of the father's VCF file
     * @author Heiko Müller
     * @since 1.0
     */
    public void setFather(String father) {
        this.father = father;
        jComboBox2.setSelectedItem(father);
    }

    /**
     * Sets the sex of the individual.
     * 
     * @param male - true if male, false if female
     * @author Heiko Müller
     * @since 1.0
     */
    public void setMale(boolean male) {
        this.male = male;
        jRadioButton1.setSelected(male);
        jRadioButton2.setSelected(!male);
    }   

    /**
     * Returns the sex of the individual.
     * 
     * @return boolean - true if male, false if female
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean isMale() {
        return male;
    }
    
    /**
     * Returns true if only the mother is defined. False otherwise.
     * 
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean hasOnlyMother(){
        if(!mother.equals("NA") && father.equals("NA")){
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if only the father is defined. False otherwise.
     * 
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean hasOnlyFather(){
        if(mother.equals("NA") && !father.equals("NA")){
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if both mother and father are defined. False otherwise.
     * 
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean hasMotherAndFather(){
        if(!mother.equals("NA") && !father.equals("NA")){
            return true;
        }
        return false;
    }
    
    /**
     * Returns true if neither mother nor father are defined. False otherwise.
     * 
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean hasNoParents(){
        if(mother.equals("NA") && father.equals("NA")){
            return true;
        }
        return false;
    }
    
    /**
     * Tests if a given String is present in jComboBox1 defining the mother's VCF file.
     * 
     * @param s tests if String s is displayed in a ComboBox
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */
    public boolean isValuePresent(String s){
        if(s == null || jComboBox1 == null){
            return false;
        }
        for(int i = 0; i < jComboBox1.getItemCount(); i++){
            String item = (String)jComboBox1.getItemAt(i);
            if(s.equals(item)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a name to both the Mother and the Father JComboBox.
     * 
     * @param s adds String to ComboBoxes for mother and father selection
     * @author Heiko Müller
     * @since 1.0
     */
    public void addSelectableValue(String s){
        jComboBox1.addItem(s);
        jComboBox2.addItem(s);
    }

    public Hashtable<String, VariantContext> getMotherHash() {
        return motherHash;
    }

    public void setMotherHash(Hashtable<String, VariantContext> motherHash) {
        this.motherHash = motherHash;
    }

    public Hashtable<String, VariantContext> getFatherHash() {
        return fatherHash;
    }

    public void setFatherHash(Hashtable<String, VariantContext> fatherHash) {
        this.fatherHash = fatherHash;
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jTextField1 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Individual");

        jLabel2.setText("Mother");

        jLabel3.setText("Father");

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jTextField1.setEditable(false);

        jRadioButton1.setText("male");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });

        jRadioButton2.setText("female");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * jRadioButton1 (male) item state changed event handler.
     * 
     * @param evt
     * @author Heiko Müller
     * @since 1.0
     */
    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        // TODO add your handling code here:
        if(jRadioButton1.isSelected()){
            male = true;
        }else{
            male = false;
        }
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    /**
     * jComboBox1 (Mother) item state changed event handler.
     * 
     * @param evt
     * @author Heiko Müller
     * @since 1.0
     */
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        mother = (String)jComboBox1.getSelectedItem();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    /**
     * jComboBox2 (Father) item state changed event handler.
     * 
     * @param evt
     * @author Heiko Müller
     * @since 1.0
     */
    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        // TODO add your handling code here:
        father = (String)jComboBox2.getSelectedItem();
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    /**
     * jRadioButton2 (male) item state changed event handler.
     * 
     * @param evt
     * @author Heiko Müller
     * @since 1.0
     */
    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        // TODO add your handling code here:
        if(jRadioButton2.isSelected()){
            male = false;
        }else{
            male = true;
        }
    }//GEN-LAST:event_jRadioButton2ItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
