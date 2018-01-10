package net.zjedu.monitor.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import com.csvreader.*;

import net.zjedu.monitor.bean.DataDetail;
import net.zjedu.monitor.bean.DataValue;
import net.zjedu.monitor.bean.MonitorDetail;
import net.zjedu.monitor.bean.MonitorValue;
import net.zjedu.monitor.bean.Xzqh;

@IocBean // 还记得@IocBy吗? 这个跟@IocBy有很大的关系哦
@At("/data")
@Ok("json")
@Fail("http:500")
public class DataValueModule {
	@Inject
	protected Dao dao; // 就这么注入了,有@IocBean它才会生效
	@At
	@Ok("raw:json")
	public Object calc(@Param("year") String  year){
		Map<String, Object> map = new HashMap<>();
        if( year ==null || "".equals(year)){
            map.put( "result", "现代化评估失败!" );
            return map;
        }
		Cnd cnd =  Cnd.where("upload", "=",true);
		List<Xzqh> xzqhlist = dao.query(Xzqh.class, cnd);
		Sql sql = Sqls.create("SELECT insert_monitor_value(@xzqh,@year )");
		for(Xzqh xzqh : xzqhlist){
			sql.params().set("xzqh",xzqh.getXzqhdm());
			sql.params().set("year", Integer.parseInt(year));
			try{
				System.out.println("计算现代化教育监测指标，行政区划："+xzqh.getXzqhdm());
			dao.execute(sql);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		//2017-12-30添加，修改指定县区的评估分
		sql = Sqls.create("SELECT update_monitor_score()");
		dao.execute(sql);
		
		map.put("result", "现代化评估成功!");
		return map;
	}
	@At
	@Ok("raw:json")
	@AdaptBy(type = UploadAdaptor.class,args = { "ioc:myUpload" })
	public Object upload(@Param("Filedata") TempFile  tf,@Param("year") String  year,
            HttpServletRequest req,
            ServletContext context) throws IOException {
		Map<String, Object> map = new HashMap<>();
        if(tf == null || "".equals( tf ) || year ==null || "".equals(year)){
            map.put( "result", "上传失败!" );
            return map;
        }
        try{
	        InputStream is = tf.getInputStream();
//        	InputStream is = req.getInputStream();
	        excel2db(is,Integer.parseInt(year));
	        map.put("result", "上传成功!");
	    } catch (Exception e) {
	        e.printStackTrace();
	        map.put("result", "上传失败!");
	    }
	    return map;
		/*InputStream inputStream = null;
		inputStream = req.getInputStream();
		CsvReader csvr = new CsvReader(inputStream, Charset.forName("UTF8"));
        csvr.readHeaders();
      ///逐条读取记录，直至读完
        while (csvr.readRecord()) {
            //读取一条记录
            System.out.println(csvr.getRawRecord());
            //按列名读取这条记录的值
            System.out.println(csvr.get("A1"));
            System.out.println(csvr.get("A2"));
            System.out.println(csvr.get("A3"));
            System.out.println(csvr.get("A4"));
        }
        csvr.close();*/
		
		 
	}
	
	/**
	 * 读取Excel数据到数据库中
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public void excel2db(InputStream is,int year) throws Exception { 
		
	  
	   Hashtable<Integer,String> ht = new Hashtable<Integer,String>();
	   
	    HSSFWorkbook wb = new HSSFWorkbook(is);
	    
	    //循环遍历excel文件中的所有tab表
	    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
	        Sheet sheet = wb.getSheetAt(i);
	        if (null == sheet) {
	            continue;
	        }
	        //遍历数据表的所有行
	        System.out.println("总行数:"+sheet.getLastRowNum());
	        for (int j = 0; j <= sheet.getLastRowNum(); j++) {
	            Row row = sheet.getRow(j);
	            if (row != null) {
	            	if(j==0){
	            		System.out.println("总列数:"+row.getLastCellNum());
	            		//第一行为代码行
	            		for(int c = 0;c<=row.getLastCellNum();c++){
	            			Cell cell = row.getCell( c);    
	            	          if(cell == null){    
	            	            continue;    
	            	          }   
	            			String code = cell.toString().replace('*', ' ').trim().toUpperCase();
	            			            			
	            			ht.put(c, code);	
	            		}	            		
	            	}else if(j>1){
	            		//第三行开始为真正的数据行
	            		int iNo = 0;
	            		
	            		Cell valuec = row.getCell(iNo);
	            		if(valuec==null || valuec.getCellTypeEnum() == CellType.BLANK){
	            			//没有读到行政区划，继续下一行
	            			continue;
	            		}
	            		String xzqhdm = getValue(valuec);
	            		System.out.println("xzqhdm"+xzqhdm);
	            		
	            		//第三列开始为数据指标值
	            		for(int n = 2;n<=row.getLastCellNum();n++){	  
	            			
	            			//从第三列开始，每列为一个指标，插入数据库
	            			DataValue data =  new DataValue();
	            			data.setXzqhdm(xzqhdm);
	            			String dt_cd = ht.get(n);
	            			if(dt_cd == null){
	            				//如果该列没有对应的数据代码，则跳过
	            				continue;
	            			}
		            		if(dt_cd.equalsIgnoreCase("A9A")||dt_cd.equalsIgnoreCase("A11A")||dt_cd.equalsIgnoreCase("A13A")||dt_cd.equalsIgnoreCase("A15A")||dt_cd.equalsIgnoreCase("A17A")||dt_cd.equalsIgnoreCase("A19A")){
		            			//表示上一年度数据
		            			data.setYear(year-1);
		            			data.setDt_cd(dt_cd.substring(0,dt_cd.length()-1));
		            		}else{
		            			data.setDt_cd(dt_cd);
		            			data.setYear(year);
		            		}
		            		//读取该单元格的值
		            		double value =0.0;
	            			valuec = row.getCell(n);
	            			if(valuec!=null && valuec.getCellTypeEnum() != CellType.BLANK){            			
		            			try{
		            				value = getNumericValue(valuec);
		            			}catch (NumberFormatException ne){
		            				
		            				System.out.println("行政区："+xzqhdm+"的数据指标"+dt_cd+"解析出错："+ne.getMessage());
		            			}
		            			
		            		}
		            		data.setValue(value);
		            		//插入数据库
		            		dao.insert(data);	
		            		//System.out.println("行政区："+xzqhdm+"，"+dt_cd+"："+value+",导入成功！");
		            		
		            		
	            		}
	            	}	               
	            }
	        }
	    }
	    is.close();
	}
	
	private String getValue(Cell cell){    		
	    if(cell.getCellTypeEnum() == CellType.BOOLEAN){    
	      return String.valueOf( cell.getBooleanCellValue());    
	    }else if(cell.getCellTypeEnum() == CellType.NUMERIC){    
	      return String.valueOf( (long)cell.getNumericCellValue());    
	    }else if (cell.getCellTypeEnum() ==CellType.FORMULA){  
	        return String.valueOf(cell.getNumericCellValue());  
	    }else if (cell.getCellTypeEnum() ==CellType.STRING){  
	        return String.valueOf(cell.getStringCellValue());  
	    }else{    
	      return String.valueOf( cell.getStringCellValue());    
	    }    
    }  
	private double getNumericValue(Cell cell) throws NumberFormatException{
		if(cell.getCellTypeEnum() == CellType.NUMERIC){    
			
	      return cell.getNumericCellValue();    
	    }else if (cell.getCellTypeEnum() ==CellType.STRING){ 
	    	
	    	String strValue = cell.getStringCellValue();
	    	if(strValue.contains("%")){
	    		strValue = strValue.replace('%', ' ').trim();
	    		return Double.parseDouble( strValue)/100.0;
	    	}
	    	else if(strValue.contains("﹪")){
	    		strValue = strValue.replace('﹪', ' ').trim();
	    		return Double.parseDouble( strValue)/100.0;
	    	}
	    	
	    	
	    	if(Strings.isBlank(strValue)) return 0.0;
	    	else
	    		return Double.parseDouble( strValue);  
	    	
	    	
	    }else if (cell.getCellTypeEnum() ==CellType.FORMULA){  
	    	
	        return cell.getNumericCellValue();  
	    }
	    else{  
	    	System.out.println("第"+cell.getColumnIndex()+"列的类型为："+cell.getCellTypeEnum());
	      return Double.parseDouble( (cell.getStringCellValue()));    
	    }    
	}
	@At
    public Object get(@Param("xzqhdm")String xzqh, @Param("year") int year) { 
	 	Cnd cnd = Strings.isBlank(xzqh)? null : Cnd.where("xzqhdm", "=", xzqh.trim());
	 	if(year!=0){
        	if(cnd == null) cnd = Cnd.where("dt_year","=", year);
        	else cnd = cnd.and("dt_year","=",year);
        }
    	DataDetail result = new DataDetail();
    	result.year = year;
    	result.xzqhdm = xzqh;
    	
    	@SuppressWarnings("unchecked")
		Class<DataDetail> datatable = (Class<DataDetail>)result.getClass();
    	//获得某年某行政区的所有监测指标值及得分
        List<DataValue> list = dao.query(DataValue.class, cnd,null);  
        
        for(DataValue model : list){  
        	result.xzqhmc = model.getXzqhmc();
            String code = model.getDt_cd();

            try {
            	//利用java反射机制得到类的属性
				Field f_value = datatable.getDeclaredField(code);
				//设置类属性的值
				double v = model.getValue();
				
				f_value.set(result,v);
		
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }  	        
        return result;  
    }
}


