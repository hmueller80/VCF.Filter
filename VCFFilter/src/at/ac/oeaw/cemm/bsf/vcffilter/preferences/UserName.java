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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** 
 * Utility for reading the computer user login name.
 * UserName.java 04 OCT 2016 
 *
 * @author unknown
 * @version 1.0
 * @since 1.0
 */
public class UserName {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
    * Returns the login name of the user.
    * 
    * @return String
    * @author Heiko Müller
    * @since 1.0
    */
    public static String getUserName(){
        String osName = System.getProperty( "os.name" ).toLowerCase();
        String className = null;

        if( osName.contains( "windows" ) ){
            className = "com.sun.security.auth.module.NTSystem";
        }
        else if( osName.contains( "linux" ) ){
            className = "com.sun.security.auth.module.UnixSystem";
        }
        else if( osName.contains( "solaris" ) || osName.contains( "sunos" ) ){
            className = "com.sun.security.auth.module.SolarisSystem";
        }

        if( className != null ){
            try{
                Class<?> c = Class.forName( className );
                Method method = null;
                if(className.endsWith("NTSystem")){
                    method = c.getDeclaredMethod("getName");
                }else{
                    method = c.getDeclaredMethod("getUsername");
                }
                Object o = c.newInstance();
                //System.out.println( method.invoke( o ) );
                return method.invoke(o).toString();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            }catch(InstantiationException e){
                e.printStackTrace();
            }catch(IllegalAccessException e){
                e.printStackTrace();
            }catch(InvocationTargetException e){
                e.printStackTrace();
            }
        }
        return "unknown";
    }
}
