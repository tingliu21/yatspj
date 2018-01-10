package net.zjedu.monitor.util;

import javax.servlet.ServletContext;

/** 
 * 用于上传ioc : dao.js  
 * 
 */
public class UploadUtils {
	private ServletContext sc;        
    public String getPath(String path) {           
        return sc.getRealPath(path);       
    }  
}
