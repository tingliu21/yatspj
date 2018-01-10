package net.zjedu.monitor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.alibaba.druid.util.StringUtils;

import sun.misc.BASE64Decoder;

public class ImgBase64Util {
	/** 
	 * 解析base64，返回图片所在路径 
	 * @param base64Info 
	 * @return 
	 */  
	public String decodeBase64(String base64Info, File filePath){  
	    if(StringUtils.isEmpty(base64Info)){  
	        return null;  
	    }  
	    BASE64Decoder decoder = new BASE64Decoder();  
	    String[] arr = base64Info.split("base64,");           
	    // 数据中：data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABI4AAAEsCAYAAAClh/jbAAA ...  在"base64,"之后的才是图片信息  
	    String picPath = filePath+ "/"+ UUID.randomUUID().toString() +".png";  
	    try {  
	        byte[] buffer = decoder.decodeBuffer(arr[1]);  
	        OutputStream os = new FileOutputStream(picPath);  
	        os.write(buffer);  
	        os.close();  
	    } catch (IOException e) {  
	        throw new RuntimeException();  
	    }  
	      
	    return picPath;  
	}  
}
