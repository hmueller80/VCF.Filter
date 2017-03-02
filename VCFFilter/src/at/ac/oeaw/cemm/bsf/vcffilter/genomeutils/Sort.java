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
package at.ac.oeaw.cemm.bsf.vcffilter.genomeutils;

import java.io.Serializable;

/** 
 * Implements a number of sort algorithms.
 * Sort.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class Sort implements Serializable{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;

    /** Creates a new instance of Sort 
     * Implementation of shell sort for various types of arrays
     */
    public Sort() {
    }

    /**
     * shell sort for 2D String array of Integers
     * sorting is done by numerical order
     * first index = row index
     * second index = column index
     * user can choose what column to sort on
     * via the column parameter
     *
     * @param data a 2D String array
     * @param column the column index where to sort on
     * @return sorted 2D String array
     */
    public String[][] shellsort(String[][] data, int column) {
        int i, j, h;
        String[] v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && (new Integer(data[j - h - 1][column])).intValue() > (new Integer(v[column])).intValue()) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 2D String array
     * sorting is done as text
     * first index = row index
     * second index = column index
     * user can choose what column to sort on
     * via the column parameter
     *
     * @param data a 2D String array
     * @param column the column index where to sort on
     * @return sorted 2D String array
     */
    public String[][] shellsortText(String[][] data, int column) {
        int i, j, h;
        String[] v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1][column].compareTo(v[column]) > 0) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 2D int array
     * first index = row index
     * second index = column index
     * user can choose what column to sort on
     * via the column parameter
     *
     * @param data a 2D int array
     * @param column the column index where to sort on
     * @return sorted 2D int array
     */
    public int[][] shellsort(int[][] data, int column) {
        int i, j, h;
        int[] v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1][column] > v[column]) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 2D int array
     * first index = row index
     * second index = column index
     * user can choose what column to sort on
     * via the column parameter
     *
     * @param data a 2D int array
     * @param column the column index where to sort on
     * @return sorted 2D int array
     */
    public int[][] shellsortdescending(int[][] data, int column) {
        int i, j, h;
        int[] v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1][column] < v[column]) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 2D double array
     * first index = row index
     * second index = column index
     * user can choose what column to sort on
     * via the column parameter
     *
     * @param data a 2D double array
     * @param column the column index where to sort on
     * @return sorted 2D double array
     */
    public double[][] shellsort(double[][] data, int column) {
        int i, j, h;
        double[] v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1][column] > v[column]) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D double array
     *
     * @param data a 1D double array
     * @return sorted (ascending) 1D double array
     */
    public double[] shellsort(double[] data) {
        int i, j, h;
        double v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1] > v) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D int array
     *
     * @param data a 1D int array
     * @return sorted (ascending) 1D int array
     */
    public int[] shellsort(int[] data) {
        int i, j, h;
        int v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1] > v) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D int array
     *
     * @param data a 1D int array
     * @return sorted (descending) 1D int array
     */
    public int[] shellsortdescending(int[] data) {
        int i, j, h;
        int v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1] < v) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D String array
     *
     * @param data a 1D String array
     * @return sorted (ascending) 1D String array
     */
    public String[] shellsort(String[] data) {
        int i, j, h;
        String v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1].compareTo(v) > 0) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

    /**
     * shell sort for 1D char array
     *
     * @param data a 1D char array
     * @return sorted (ascending) 1D char array
     */
    public char[] shellsort(char[] data) {
        int i, j, h;
        char v;
        int n = data.length;
        h = 1;
        while (h < n) {
            h = 3 * h + 1;
        }
        do {
            h = h / 3;
            for (i = h + 1; i < n + 1; i++) {
                v = data[i - 1];
                j = i;
                while (j > h && data[j - h - 1] > v) {
                    data[j - 1] = data[j - h - 1];
                    j = j - h;
                }
                data[j - 1] = v;
            }

        } while (h > 1);
        return data;
    }

}
