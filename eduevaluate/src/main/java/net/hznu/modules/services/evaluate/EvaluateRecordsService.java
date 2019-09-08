package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.common.chart.MonitorSumValue;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

@IocBean(args = {"refer:dao"})
public class EvaluateRecordsService extends Service<Evaluate_records> {
	private static final Log log = Logs.get();

    public EvaluateRecordsService(Dao dao) {
    	super(dao);
    }
    /**
     * 功能：根据行政区划，获得下辖所有县级行政区划的评估总分
     * xzqh：为行政区划代码，一般为省级或地市级行政区划代码
     * */
    public List<MonitorSumValue> getTotalScore(int year, String xzqhdm){
        String statXZQ = xzqhdm;
        if(xzqhdm.endsWith("00")) {
            statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if (statXZQ.endsWith("00")) {
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }
        }
        Sql sql = Sqls.create("SELECT  unitcode as code, xzqhmc as name,score as value FROM evaluate_record_view where year =@year and unitcode like '"+statXZQ+"%' order by value");
        sql.params().set("year", year);
//        sql.params().set("xzqh", statXZQ);

        Entity<MonitorSumValue> entity = dao().getEntity(MonitorSumValue.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(MonitorSumValue.class);
    }
    /**
     * 功能：获得特殊地区的评估总分
     * colname：为sys_unit的特征字段，如develop、keynote
     * sValue：为对应colname的值
     * */
    public List<MonitorSumValue> getTotalScore(int year, String cloName,Object value){

        Sql sql = Sqls.create("SELECT  b.unitcode as code, b.xzqhmc as name,score as value FROM evaluate_records a inner join sys_unit b on b.id = a.unitid where "+
                cloName + " = @col and  year =@year order by value");
        sql.params().set("col",value);
        sql.params().set("year", year);

        Entity<MonitorSumValue> entity = dao().getEntity(MonitorSumValue.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(MonitorSumValue.class);
    }
    /**
     * 功能：根据行政区划，获得评估总分的平均值
     * xzqh：为行政区划代码，一般为省级或地市级行政区划代码
     * */
    public List<MonitorSumValue> getCityAvgTotalScore(int year, String xzqhdm){
        String statXZQ = xzqhdm;
        if(xzqhdm.endsWith("00")) {
            statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
            if (statXZQ.endsWith("00")) {
                statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
            }
        }
        Sql sql = Sqls.create("SELECT  sys_unit.unitcode as code, sys_unit.xzqhmc as name,cast(avg(score) as numeric(4,2)) as value FROM evaluate_record_view,sys_unit " +
                " where year =@year and evaluate_record_view.unitcode like '"+statXZQ+"%' and sys_unit.unitcode=substring(evaluate_record_view.unitcode,0,5) || '00' group by sys_unit.unitcode,sys_unit.xzqhmc  order by value");
        sql.params().set("year", year);
        sql.params().set("xzqh", xzqhdm);

        Entity<MonitorSumValue> entity = dao().getEntity(MonitorSumValue.class);
        sql.setEntity(entity);
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);

        return sql.getList(MonitorSumValue.class);

    }
    /**
     * 读取Excel数据到数据库中
     * @param path
     * @return
     * @throws Exception
     */
    public void excel2db(InputStream is, int year,boolean bScore) throws Exception {

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
                        for(int c = 2;c<=row.getLastCellNum();c++){
                            Cell cell = row.getCell( c);
                            if(cell == null){
                                continue;
                            }
                            String code = cell.toString().replace(".0", "").trim().toUpperCase();

                            ht.put(c, code);
                        }
                    }else if(j>0){
                        //第2行开始为真正的数据行,第2列为行政区划代码
                        int iNo = 1;

                        Cell valuec = row.getCell(iNo);
                        if(valuec==null || valuec.getCellTypeEnum() == CellType.BLANK){
                            //没有读到行政区划，继续下一行
                            continue;
                        }
                        String xzqhdm = getValue(valuec);
                        xzqhdm = xzqhdm.substring(0,6);
                        System.out.println("xzqhdm"+xzqhdm);

                        //获取行政区划对应的单位
                        Sys_unit unit = dao().fetch(Sys_unit.class,Cnd.where("unitcode","=",xzqhdm));

                        //生成评估记录
                        Evaluate_records records = dao().fetch(Evaluate_records.class,Cnd.where("unitid", "=", unit.getId()).and("year", "=", year));

                        if (records == null) {
                            records = new Evaluate_records();
                            records.setYear(year);
                            records.setUnitID(unit.getId());
                            records = insert(records);
                        }
                        //第三列开始为数据指标值
                        for(int n = 2;n<=row.getLastCellNum();n++){

                            //从第三列开始，每列为一个指标，插入数据库
                            String dt_cd = ht.get(n);
                            if(dt_cd == null){
                                //如果该列没有对应的数据代码，则跳过
                                continue;
                            }

                            //读取该单元格的值
                            Double value = null;
                            valuec = row.getCell(n);
                            if(valuec!=null && valuec.getCellTypeEnum() != CellType.BLANK){
                                try{
                                    value = getNumericValue(valuec);
                                    if(value==null){
                                        //该列没值，则跳过
                                        continue;
                                    }
                                }catch (NumberFormatException ne){

                                    log.error("行政区："+xzqhdm+"的数据指标"+dt_cd+"解析出错："+ne.getMessage());
                                }
                                if(dt_cd.equalsIgnoreCase("total")){
                                    records.setScore(value);
                                    updateIgnoreNull(records);
                                }else {
                                    Evaluate_index data = dao().fetch(Evaluate_index.class, Cnd.where("evaluateid", "=", records.getId()).and("code", "=", dt_cd));
                                    if (data == null) {
                                        data = new Evaluate_index();
                                        data.setEvaluateId(records.getId());
                                        data.setCode(dt_cd);
                                        //获取监测点id
                                        Monitor_index index = dao().fetch(Monitor_index.class, Cnd.where("code", "=", dt_cd).and("year", "=", year));
                                        data.setIndexId(index.getId());
                                        if (bScore) {
                                            data.setScore(value);
                                        } else {
                                            data.setValue(value);
                                        }
                                        dao().insert(data);

                                    } else {
                                        if (bScore) {
                                            data.setScore(value);

                                        } else {
                                            data.setValue(value);
                                        }

                                        dao().updateIgnoreNull(data);
                                    }
                                }
                                log.info("行政区："+xzqhdm+"的数据指标"+dt_cd+"入库成功！");

                            }



                        }
                    }
                }
            }
        }
        is.close();
    }


    /**
     * 执行sql使value转换为svalue
     * @param value
     * @return
     * @throws Exception
     */
    public void UpdateIndexSvalue() {
        Sql sql = Sqls.create("SELECT update_evaluate_svalue()");
        dao().execute(sql);
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
    private Double getNumericValue(Cell cell) throws NumberFormatException{
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


            if(!StringUtils.isNotBlank(strValue)) return null;
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

}

