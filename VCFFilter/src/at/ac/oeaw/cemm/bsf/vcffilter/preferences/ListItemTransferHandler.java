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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.MOVE;

/** 
 * ListItemTransferHandler. Needed for drag and drop.
 * ListItemTransferHandler.java 04 OCT 2016
 *
 * @author unknown
 * @version 1.0
 * @since 1.0
 */
public class ListItemTransferHandler extends TransferHandler{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    private final DataFlavor localObjectFlavor;
  private Object[] transferedObjects = null;
  
  /**
    * Creates new ListItemTransferHandler.
    * 
    * @since 1.0
    */
  public ListItemTransferHandler() {
    localObjectFlavor = new ActivationDataFlavor(
      Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
  }
  @SuppressWarnings("deprecation")
  @Override protected Transferable createTransferable(JComponent c) {
    JList list = (JList) c;
    indices = list.getSelectedIndices();
    transferedObjects = list.getSelectedValues();
    return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
  }
  @Override public boolean canImport(TransferSupport info) {
    if(!info.isDrop() || !info.isDataFlavorSupported(localObjectFlavor)) {
      return false;
    }
    return true;
  }
  @Override public int getSourceActions(JComponent c) {
    return MOVE; //TransferHandler.COPY_OR_MOVE;
  }
  @SuppressWarnings("unchecked")
  @Override public boolean importData(TransferSupport info) {
    if(!canImport(info)) {
      return false;
    }
    JList target = (JList)info.getComponent();
    JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
    DefaultListModel listModel = (DefaultListModel)target.getModel();
    int index = dl.getIndex();
    int max = listModel.getSize();
    if(index<0 || index>max) {
      index = max;
    }
    addIndex = index;
    try {
      Object[] values = (Object[])info.getTransferable().getTransferData(
          localObjectFlavor);
      addCount = values.length;
      for(int i=0; i<values.length; i++) {
        int idx = index++;
        listModel.add(idx, values[i]);
        target.addSelectionInterval(idx, idx);
      }
      return true;
    } catch(UnsupportedFlavorException ufe) {
      ufe.printStackTrace();
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
    return false;
  }
  @Override protected void exportDone(
      JComponent c, Transferable data, int action) {
    cleanup(c, action == MOVE);
  }
  private void cleanup(JComponent c, boolean remove) {
    if(remove && indices != null) {
      JList source = (JList)c;
      DefaultListModel model = (DefaultListModel)source.getModel();
      if(addCount > 0) {
        //http://java-swing-tips.googlecode.com/svn/trunk/DnDReorderList/src/java/example/MainPanel.java
        for(int i=0; i<indices.length; i++) {
          if(indices[i]>=addIndex) {
            indices[i] += addCount;
          }
        }
      }
      for(int i=indices.length-1; i>=0; i--) {
          //System.out.println(indices[i]);
          if(indices[i] < model.size()){
                model.remove(indices[i]);
          }else if(indices[i] == model.size()){
                model.remove(indices[i] - 1);
          }
      }
    }
    indices  = null;
    addCount = 0;
    addIndex = -1;
  }
  private int[] indices = null;
  private int addIndex  = -1; //Location where items were added
  private int addCount  = 0;  //Number of items added.
}
