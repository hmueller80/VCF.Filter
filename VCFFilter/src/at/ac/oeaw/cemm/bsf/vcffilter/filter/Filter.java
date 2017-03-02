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

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The root class of all filters. Implements graphical elements, some common processing, 
 * and abstract methods to be implemented by child classes.
 * 
 * Filter.java 04 OCT 2016
 *
 * 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class Filter extends JPanel{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The equal operator.
     */
    protected static final String equal = "=";
    
    /**
     * The smaller than operator.
     */
    protected static final String smaller = "<";
    
    /**
     * The larger than operator.
     */
    protected static final String larger = ">";
    
    /**
     * HeaderlineType.
     */
    protected String HeaderlineType;
    
    /**
     * VCF headerline as string.
     */
    protected String Headerline;
    
    /**
     * VCF headerline ID.
     */
    protected String ID;
    
    /**
     * VCF headerline Number (Integer, A, R, G, .).
     */
    protected String Number;
    
    /**
     * VCF headerline Type (String, Character, Float, Flag, Integer).
     */
    protected String Type;
    
    /**
     * VCF headerline Description.
     */
    protected String Description;
    
    /**
     * JLabel displaying the Filter ID.
     */
    protected JLabel idlabel;
    
    /**
     * JTextField holding search criterion 1.
     */
    protected JTextField criterion1;
    
    /**
     * JTextField holding search criterion 2.
     */
    protected JTextField criterion2;
    
    /**
     * JTextField holding search criterion 3.
     */
    protected JTextField criterion3;
    
    
    //protected JLabel descriptionlabel;
    
    /**
     * JLabel for the AndNot checkbox.
     */
    protected JLabel andnotlabel;
    
    /**
     * AndNot checkbox.
     */
    protected JCheckBox andnotcheckbox;
    
    /**
     * AndNot variable.
     */
    protected boolean andnot = false;
    
    /**
     * VCFHeaderLine
     */
    protected VCFHeaderLine headerLine;
    
    /**
     * Remove button
     */
    protected JButton remove;
    
    /**
     * Filter index
     */
    protected int index;
    
    /**
     * VCFFilter gui
     */
    protected VCFFilter gui;
    
    /*
    public Filter(String vcfheaderline){
        Headerline = vcfheaderline;
        //INFO=<ID=exac.AN_CONSANGUINEOUS,Number=1,Type=String,Description="Allele number among individuals with F > 0.05">
        if(vcfheaderline.startsWith("INFO=")){
            HeaderlineType = "INFO";
            String strippedHeader = vcfheaderline.substring(6, vcfheaderline.length() - 1);
            parseVCFHeaderLine(strippedHeader);
            setFilterLayout();

            criterion1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion1FocusLost(evt);
                }
            });
            criterion2.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion2FocusLost(evt);
                }
            });
            criterion3.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion3FocusLost(evt);
                }
            });
            andnotcheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                AndnotcheckboxItemStateChanged(evt);
            }
            });
            
        }
        
    }
    */    
   
    /**
    * Initializes new Filter.
    * 
    * @param header VCF header line object
    * @author Heiko Müller
    * @since 1.0
    */
    public Filter(VCFInfoHeaderLine header){
        Headerline = header.toString();
        //INFO=<ID=exac.AN_CONSANGUINEOUS,Number=1,Type=String,Description="Allele number among individuals with F > 0.05">
        if(header.getKey().startsWith("INFO")){
            HeaderlineType = "INFO";
            ID = header.getID();        
            Number = header.getValue();
            Type = header.getType().toString();
            Description = header.getDescription(); 
            
            setFilterLayout();

            criterion1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion1FocusLost(evt);
                }
            });
            criterion2.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion2FocusLost(evt);
                }
            });
            criterion3.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    criterion3FocusLost(evt);
                }
            });
            andnotcheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                AndnotcheckboxItemStateChanged(evt);
            }
            });
            
        }
    }
    
    /**
    * Remove filter action event handler.
    * 
    * @param evt Remove filter action event
    * @author Heiko Müller
    * @since 1.0
    */
    public void removeActionPerformed(java.awt.event.ActionEvent evt){  
        if(gui.getSelectedTabIndex() == 0){
            gui.removeFilter(index);
        }else if(gui.getSelectedTabIndex() == 1){
            gui.removeFamilyFilter(index);
        }
    }
    
    
    /**
     * Sets the graphical Filter layout of type GroupLayout.
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    private void setFilterLayout(){
        idlabel = new javax.swing.JLabel();
        idlabel.setPreferredSize(new Dimension(170, 30));
        idlabel.setFont(new Font("Courier New", Font.PLAIN, 13));
        idlabel.setToolTipText(Description);        
        criterion1 = new javax.swing.JTextField();
        criterion1.setPreferredSize(new Dimension(230, 30));
        JLabel jLabel2 = new javax.swing.JLabel();
        criterion2 = new javax.swing.JTextField();
        criterion2.setPreferredSize(new Dimension(230, 30));
        JLabel jLabel3 = new javax.swing.JLabel();
        criterion3 = new javax.swing.JTextField();
        criterion3.setPreferredSize(new Dimension(230, 30));
        andnotlabel = new javax.swing.JLabel();
        andnotcheckbox = new javax.swing.JCheckBox();
        remove = new javax.swing.JButton();
        remove.setText("Remove");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        String temp = ID + "                                                                       ";
        temp = temp.substring(0, 30);
        temp = temp + ":";
        idlabel.setText(temp);        
        criterion1.setText("");
        jLabel2.setText("or");
        criterion2.setText("");
        jLabel3.setText("or");
        criterion3.setText("");
        andnotlabel.setText("AND NOT");
        andnotcheckbox.setSelected(false);
        andnotcheckbox.setToolTipText("Select to turn on \"AND NOT\" logic");
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(idlabel)
                .addGap(29, 29, 29)
                    .addComponent(andnotcheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                .addComponent(andnotlabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(criterion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(27, 27, 27)
                .addComponent(criterion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(criterion3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addGap(29, 29, 29)
                //.addComponent(andnotlabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                //.addGap(18, 18, 18)
                //.addComponent(andnotcheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idlabel)
                    .addComponent(andnotlabel)
                    .addComponent(andnotcheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(criterion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(criterion2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(criterion3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    //.addComponent(andnotlabel)                    
                    //.addComponent(andnotcheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                 )
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        //this.pack();
    
    }
    
    /**
     * Parses the VCFHeaderLine.
     * 
     * @author Heiko Müller
     * @since 1.0
     
    private void parseVCFHeaderLine(String header){
        String id = header.substring(0, header.indexOf(",Number"));
        String number = header.substring(header.indexOf(",Number") + 1, header.indexOf(",Type"));
        String type = header.substring(header.indexOf(",Type") + 1, header.indexOf(",Description"));
        String description = header.substring(header.indexOf(",Description") + 1);
        ID = id.substring(id.indexOf("=") + 1);
        
        Number = number.substring(number.indexOf("=") + 1);
        Type = type.substring(type.indexOf("=") + 1);
        Description = description.substring(description.indexOf("=") + 1);        
    }
    * 
    */
    
    /**
     * Abstract method.
     * 
     * @param vc VariantContext object to be filtered
     * @return boolean true if pass, false otherwise
     * @author Heiko Müller
     * @since 1.0
     */
    public abstract boolean passesFilter(VariantContext vc);
    
    /**
     * Abstract method. Should copy the typed value in jTextField1 to predicate1 variable depending on data type. 
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    public abstract void setPredicate1();
    
    /**
     * Abstract method. Should copy the typed value in jTextField2 to predicate2 variable depending on data type. 
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    public abstract void setPredicate2();
    
    /**
     * Abstract method. Should copy the typed value in jTextField3 to predicate3 variable depending on data type. 
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    public abstract void setPredicate3();
    
    /**
     * Focus lost event handler for jTextField1. Sets predicate1 upon focus loss.
     * 
     * @param evt Focus lost event
     * @author Heiko Müller
     * @since 1.0
     */    
    protected void criterion1FocusLost(java.awt.event.FocusEvent evt) {                                      
        setPredicate1();
        setIDLabelState();
    }
    
    /**
     * Focus lost event handler for jTextField2. Sets predicate2 upon focus loss.
     * 
     * @param evt Focus lost event
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected void criterion2FocusLost(java.awt.event.FocusEvent evt) {                                      
        setPredicate2();
        setIDLabelState();
    }
    
    /**
     * Focus lost event handler for jTextField3. Sets predicate3 upon focus loss.
     * 
     * @param evt Focus lost event
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected void criterion3FocusLost(java.awt.event.FocusEvent evt) {                                      
        setPredicate3();
        setIDLabelState();
    }
    
    /**
     * Andnot checkbox item state changed event handler. Sets the andnot variable according to user choice.
     * 
     * @param evt Andnot checkbox item state changed event
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected void AndnotcheckboxItemStateChanged(java.awt.event.ItemEvent evt) {                                      
        if(andnotcheckbox.isSelected()){
            andnot = true;
        }else{
            andnot = false;
        }
    }
    
    /**
     * Tests if filter is active, i.e. has non-empty search fields.
     * 
     * @return boolean true if Filter has valid search criteria, false otherwise
     * @author Heiko Müller
     * @since 1.0
     */ 
    public boolean filterIsActive(){
        if(criterion1.getText().trim().equals("") && criterion2.getText().trim().equals("") && criterion3.getText().trim().equals("")){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * Removes brackets [ and ] from a String.
     * 
     * @param arraystring the string enclosed by brackets []
     * @return String String without brackets
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected String parseArrayString(String arraystring) {
        int start = arraystring.lastIndexOf("[");
        int end = arraystring.indexOf("]");
        if(start > -1 && end > -1){
            return arraystring.substring(start + 1, end);
        }
        return arraystring;
    }
    
    
    /*
    protected int[] parseAlleleCount(VariantContext vc){
        
            String ac = vc.getAttribute("AC").toString();
            ac = parseArrayString(ac);
            //System.out.println(ac);
            String[] acarray = ac.split(",");
            int[] result = new int[2];
            int count = 1;
            for(int i = 0; i < acarray.length; i++){
                if(acarray[i].trim().equals("1")){
                    result[count] = (i+1);
                    count--;
                } 
                if(acarray[i].trim().equals("2")){
                    result[0] = (i+1);
                    result[1] = (i+1);
                    break;
                }
            }
            return result;
        }
    */
        
    /**
     * Tests if String s represents an operator (&#62;, &#60; =).
     * 
     * @param s input String
     * @return boolean true if s.equals("&#60;") or s.equals("=") or s.equals("&#62;"), false otherwise.
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected boolean isOperator(String s){
        if(s.equals("<") || s.equals("=") || s.equals(">")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Parses the operator of a predicate.
     * 
     * @param s input String
     * @return String the operator
     * @author Heiko Müller
     * @since 1.0
     */ 
    protected String parseOperator(String s){
        if(s.startsWith("<")){
            return Filter.smaller;
        }else if(s.startsWith("=")){
            return Filter.equal;
        }else if(s.startsWith(">")){
            return Filter.larger;
        }else{
            return "";
        }
    }
    
    //public void setAndNot(boolean b){
    //    andnotcheckbox.setSelected(b);
    //    andnot = b;
    //}

    /**
     * Getter for criterion1.
     * 
     * @return JTextField
     * @author Heiko Müller
     * @since 1.0
     */ 
    public JTextField getCriterion1() {
        return criterion1;
    }

    /**
     * Getter for criterion2.
     * 
     * @return JTextField
     * @author Heiko Müller
     * @since 1.0
     */ 
    public JTextField getCriterion2() {
        return criterion2;
    }

    /**
     * Getter for criterion3.
     * 
     * @return JTextField
     * @author Heiko Müller
     * @since 1.0
     */ 
    public JTextField getCriterion3() {
        return criterion3;
    }

    /**
     * Setter for criterion1.
     * 
     * @param criterion1 Sets criterion1.
     * @author Heiko Müller
     * @since 1.0
     */ 
    public void setCriterion1(String criterion1) {
        this.criterion1.setText(criterion1);
    }

    /**
     * Setter for criterion2.
     * 
     * @param criterion2 Sets criterion2.
     * @author Heiko Müller
     * @since 1.0
     */ 
    public void setCriterion2(String criterion2) {
        this.criterion2.setText(criterion2);
    }

    /**
     * Setter for criterion3.
     * 
     * @param criterion3 Sets criterion2.
     * @author Heiko Müller
     * @since 1.0
     */ 
    public void setCriterion3(String criterion3) {
        this.criterion3.setText(criterion3);
    }    
    
    /**
     * Getter for andnot variable.
     * 
     * @return boolean
     * @author Heiko Müller
     * @since 1.0
     */ 
    public boolean getAndNot(){
        return andnot;
    }

    /**
     * Setter for andnot variable.
     * 
     * @param andnot Sets andnot.
     * @author Heiko Müller
     * @since 1.0
     */ 
    public void setAndnot(boolean andnot) {
        andnotcheckbox.setSelected(andnot);
        this.andnot = andnot;        
    }
    
    /**
     * Getter for HeaderlineType variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getHeaderlineType() {
        return HeaderlineType;
    }

    /**
     * Getter for Headerline variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getHeaderline() {
        return Headerline;
    }

    /**
     * Getter for ID variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getID() {
        return ID;
    }

    /**
     * Getter for Number variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getNumber() {
        return Number;
    }

    /**
     * Getter for Type variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getType() {
        return Type;
    }

    /**
     * Getter for Description variable.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */ 
    public String getDescription() {
        return Description;
    }

    /**
     * Getter for AndNot checkbox.
     * 
     * @return JCheckBox
     * @author Heiko Müller
     * @since 1.0
     */
    public JCheckBox getAndnotcheckbox() {
        return andnotcheckbox;
    }
    
    /**
     * Setter for AndNot checkbox. Sets the selection status of the checkbox according to the boolean value of andnot.
     * 
     * @param andnot Sets andnot.
     * @author Heiko Müller
     * @since 1.0
     */
    public void setAndnotCheckBox(boolean andnot) {        
        this.andnotcheckbox.setSelected(andnot);
    }
    
    /**
     * Reports the settings of this Filter as a String.
     * 
     * @return String
     * @author Heiko Müller
     * @since 1.0
     */
    public String getSettings(){
        return "Filter id:\t" + ID + "\tAND NOT:\t" + andnot + "\tcriterion1:\t" + criterion1.getText() + "\tcriterion2:\t" + criterion2.getText() + "\tcriterion3:\t" + criterion3.getText();
    }

    /**
     * Getter for VCFHeaderLine to be written to an output VCF file. 
     * Contains Filter ID and current search criteria.
     * 
     * @return VCFHeaderLine to be reported in file output
     * @author Heiko Müller
     * @since 1.0
     */
    public VCFHeaderLine getHeaderLine() {
        setHeaderLine();
        return headerLine;
    }
    
    /**
     * Initializes the VCFHeaderLine headerline variable according to current search criteria.
     * This headerline appears in VCF file output.
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    protected void setHeaderLine() {
        StringBuilder sb = new StringBuilder();
        
        if(andnot){
            sb.append("AND NOT:(");
            if(criterion1.getText().length() > 0){
                sb.append(criterion1.getText());
            }
            if(criterion2.getText().length() > 0){
                sb.append(" or " + criterion2.getText());
            }
            if(criterion3.getText().length() > 0){
                sb.append(" or " + criterion3.getText());
            }
            headerLine = new VCFHeaderLine("VCFFilter_" + ID, sb.toString() + ")");
        }else{
            sb.append("AND:(");
            if(criterion1.getText().length() > 0){
                sb.append(criterion1.getText());
            }
            if(criterion2.getText().length() > 0){
                sb.append(" or " + criterion2.getText());
            }
            if(criterion3.getText().length() > 0){
                sb.append(" or " + criterion3.getText());
            }
            headerLine = new VCFHeaderLine("VCFFilter_" + ID, sb.toString() + ")");
        }
    }

    /**
     * Sets the index of this filter on the GUI.
     * 
     * @param index Sets filter index.
     * @author Heiko Müller
     * @since 1.0
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Sets the reference of this filter to the GUI.
     * 
     * @param gui graphical user interface
     * @author Heiko Müller
     * @since 1.0
     */
    public void setGui(VCFFilter gui) {
        this.gui = gui;
    }
    
    /**
     * Sets the filter label state of the filter to enabled/disabled depending on whether the filter is active (meaning it has valid search criteria) or not.
     * 
     * @author Heiko Müller
     * @since 1.0
     */
    public void setIDLabelState(){
        if(filterIsActive()){
            idlabel.setEnabled(true);
        }else{
            idlabel.setEnabled(false);
        }
    }
    
}
