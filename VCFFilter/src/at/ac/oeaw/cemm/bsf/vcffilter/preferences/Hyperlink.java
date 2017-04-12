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

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import at.ac.oeaw.cemm.bsf.vcffilter.outputformat.FormatField;

/** 
 * Graphical utility shown in preferences for defining custom hyperlinks.
 * Hyperlink.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Hyperlink extends JPanel implements FormatField, Comparable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    
    /**
     * The ID if the selected annotation
     */
    private String ID;
    
    /**
     * left constant part of hyperlink 
     */
    private String left;
    
    /**
     * search string
     */
    private String search;
    
    /**
     * right constant part of hyperlink 
     */
    private String right;
    
    /**
     * VCFFilterPreferences
     */
    private VCFFilterPreferences preferences;
    
    /**
    * Creates new Hyperlink.
    * 
    * @param id selected annotation id
    * @param left left constant part of hyperlink 
    * @param search search string
    * @param right right constant part of hyperlink 
    * @param preferences VCFFilterPreferences
    * @author Heiko Müller
    * @since 1.0
    */
    public Hyperlink(String id, String left, String search, String right, VCFFilterPreferences preferences) {
        this.ID = id;
        this.left = left;
        this.search = search;
        this.right = right;
        
        initComponents();
        jTextField1.setText(id);
        jTextField2.setText(left);
        jTextField3.setText(right);
        this.preferences = preferences;
        initSearchFields(jComboBox1);
        if(search.length() > 0){
            jComboBox1.setSelectedItem(search);
        }
    }
    
    public String getHeader() {
        return ID + "_hyperlink" + "\t";
    }
    
    public String getOutput(VariantContext vc) {
        Object o = vc.getAttribute((String)jComboBox1.getSelectedItem());
        if (o != null) {
            return left + o.toString() + right + "\t";
        } else {
            String key = (String)jComboBox1.getSelectedItem();
            if(key.equals("ID")){
                String rs = vc.getID();
                if(!rs.startsWith("rs")){
                    return "\t";
                }else{
                    return left + vc.getID() + right + "\t";
                }
            }
            return "\t";
        }
    }
    
    private void initSearchFields(javax.swing.JComboBox jc) {
        ArrayList<String> header = preferences.getOutputColumns();
        for (String s : header) {         
                jc.addItem(s);                
        }
    }
    
    /**
    * Inits combobox with available ids.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void initSearchFields() {
        ArrayList<String> header = preferences.getOutputColumns();
        for (String s : header) {         
                jComboBox1.addItem(s);                
        }
    }
    
    /**
    * Sets the combobox selected item to id.
    * 
    * @param id selected annotation ID
    * @author Heiko Müller
    * @since 1.0
    */
    public void setSelectedItem(String id){
        jComboBox1.setSelectedItem(id);
    }

    /**
    * Returns the id the Hyperlink searches for.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getID() {
        return ID;
    } 

    /**
    * Returns the left part of the hyperlink (left of id).
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getLeft() {
        return left;
    }

    /**
    * Returns the search part of the hyperlink (usually = id).
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getSearch() {
        return search;
    }

    /**
    * Returns the right part of the hyperlink (often empty).
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public String getRight() {
        return right;
    }

    /**
    * Sets the reference to the VCFFilter preferences.
    * 
    * @param preferences VCFFilter preferences
    * @author Heiko Müller
    * @since 1.0
    */
    public void setPreferences(VCFFilterPreferences preferences) {
        this.preferences = preferences;
    }    
    
    
    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {                                      
        // TODO add your handling code here:
        ID = jTextField1.getText();
        preferences.initRemoveHyperlinks();
    }
    
    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {                                      
        // TODO add your handling code here:
        left = jTextField2.getText();
    }
    
    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {                                      
        // TODO add your handling code here:
        right = jTextField3.getText();
    }
    
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {                                            
        ID = (String)jComboBox1.getSelectedItem();
        jTextField1.setText(ID);
        preferences.initRemoveHyperlinks();
    }  
    
    
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField1.setEditable(false);
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();

        //setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("ID");

        jLabel2.setText("Hyperlink left");

        jLabel3.setText("Search field");

        jLabel4.setText("Hyperlink right");
        
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
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
                        .addComponent(jLabel1)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 70, Short.MAX_VALUE))
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public int compareTo(Object other){
        return ID.compareTo(((Hyperlink)other).getID());
    }    
    
}
