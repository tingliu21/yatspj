package net.hznu.modules.services.evaluate;

import net.hznu.common.base.Service;
import net.hznu.common.util.StringUtil;
import net.hznu.common.util.WordTemplate;
import net.hznu.common.util.XwpfUtil;
import net.hznu.modules.models.evaluate.Evaluate_custom;
import net.hznu.modules.models.evaluate.Evaluate_records_self;
import net.hznu.modules.models.evaluate.Evaluate_remark;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean(args = {"refer:dao"})
public class EvaluateRecordsSelfService extends Service<Evaluate_records_self> {
	private static final Log log = Logs.get();

    public EvaluateRecordsSelfService(Dao dao) {
    	super(dao);
    }
    /**
     * 级联删除评估记录
     * 2018-12-23 改为通过数据库设置外键级联删除
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void deleteAndChild(String evaluateId) {


        //dao().execute(Sqls.create("delete from evaluate_records where path like @path").setParam("path", catalog.getPath() + "%"));
        //清空该评估记录下的所有观测点评分

        dao().execute(Sqls.create("delete from evaluate_remark where evaluateid=@id ").setParam("id", evaluateId));
        dao().execute(Sqls.create("delete from evaluate_custom where evaluateid=@id").setParam("id",evaluateId));
        dao().execute(Sqls.create("delete from evaluate_summary where evaluateid=@id").setParam("id",evaluateId));
        dao().execute(Sqls.create("delete from evaluate_appendix where evaluateid=@id").setParam("id",evaluateId));

        //清空该评估记录
        delete(evaluateId);
    }
    /**
     * 提交评估记录
     *
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void submit(String evaluateId){

        dao().execute(Sqls.create("update evaluate_records set status_s = true where id=@id ").setParam("id", evaluateId));
    }
    /**
     * 上传评估报告和规划材料
     *
     * @param evaluateId
     */
    @Aop(TransAop.READ_COMMITTED)
    public void upload(String evaluateId,String evapath,String planpath){

        dao().execute(Sqls.create("update evaluate_records set selfevaurl = @evapath, planurl=@planpath where id=@id ").
                setParam("id", evaluateId).setParam("evapath",evapath).setParam("planpath",planpath));
    }
    public List<Record> getUnitInfo(String evalId) {
        return list(Sqls.create("select t1.name as unitname, t1.address as address, t1.website as website, t1.telephone as telephone, t1.email as email from sys_unit t1 join evaluate_records t2 on t1.id=t2.schoolid where t2.id=@id").setParam("id", evalId));

    }

    public List<Record> getBasicEvalData(String evalId) {
        return list(Sqls.create("select t1.qualify as qualify, t2.location from evaluate_qualify t1 join monitor_index t2 on t1.indexid=t2.id where evaluateid=@id order by location").setParam("id", evalId));
    }

    public List<Record> getBasicSummaryData(String evalId) {
        return list(Sqls.create("select t1.summary, t2.location from evaluate_summary t1 join monitor_catalog t2 on t1.catalogid=t2.id where evaluateid=@id order by location").setParam("id", evalId));
    }



    /**
     * 组装word文档中需要显示段落数据的集合
     * @return
     */
    public Map<String, Object> packageParaObject(String evaluateId) {
        if (StringUtils.isNotBlank(evaluateId)) {
            Map<String,Object> wordDataMap = new HashMap<String,Object>();

            Map<String,Object> params = new HashMap<String,Object>();
            //报告生成日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
            params.put("date",sdf.format(new Date()));

            //报告学校信息
    		//String pxzqhdm = xzqhdm.substring(0,4)+"00";
    		//Xzqh pxzqh = dao.fetch(Xzqh.class,pxzqhdm);
            List<Record> recordsUnitInfo = this.getUnitInfo(evaluateId);
            if (recordsUnitInfo.size() > 0) {
                params.put("unitname", recordsUnitInfo.get(0).getString("unitname"));
                params.put("address", recordsUnitInfo.get(0).getString("address"));
                params.put("website", recordsUnitInfo.get(0).getString("website"));
                params.put("telephone", recordsUnitInfo.get(0).getString("telephone"));
                params.put("email", recordsUnitInfo.get(0).getString("email"));
            }

            //表格内容
            List<Record> remarkList = list(Sqls.create("select location,sum(score_s) as score_s, string_agg(remark_s,'\n' order by indexname ) as remark_s,string_agg(id,',') as id FROM evaluate_remark_view where evaluateid=@eid group by location ")
                    .setParam("eid",evaluateId));
            //得分
            for (Record record : remarkList) {
                int location = record.getInt("location");
                double score_s = record.getDouble("score_s");
                //double score_p = record.getDouble("score_p");
                String remark_s = record.getString("remark_s").replaceAll("\n","\r\n");
                String remarkids = record.getString("id");

                String[] remarkIdList = StringUtils.split(remarkids,",");
                String attachement="";
                List<Record> appList = list(Sqls.create("select apname from evaluate_appendix where evaluateid=@eid and remarkid in (@remarkid) order by apname")
                        .setParam("eid",evaluateId).setParam("remarkid",remarkIdList));
                for(Record appendix :appList){
                    attachement +=appendix.getString("apname")+"\r\n";
                }
                params.put("s_i" + location, formatDouble(score_s));
                //params.put("p_i" + location, formatDouble(score_p));
                params.put("r_i" + location, remark_s);
                params.put("att_i"+location,attachement);
            }

            wordDataMap.put("parametersMap", params);
            return wordDataMap;

        }else
            return null;
    }


    public String formatDouble(double d) {
        BigDecimal bg = new BigDecimal(d).setScale(1, RoundingMode.UP);
        double num = bg.doubleValue();
        if (Math.round(num) - num == 0) {
            return String.valueOf((long) num);
        }
        return String.valueOf(num);
    }

    /**
     * 导出word文件

     * @param is

     * @param response
     * @param xwpfUtil
     */
    public void exportWord(Map<String, Object> paramsPara, List<Evaluate_custom>stat, InputStream is,
                           HttpServletResponse response,
                           XwpfUtil xwpfUtil, String filename) {

        try {
            //暂时先用WordTemplate工具类替换word段落中的内容
            WordTemplate wordTemplate = new WordTemplate(is);
            wordTemplate.replaceDocument(paramsPara);

            XWPFDocument doc=wordTemplate.getDocument();
            //xwpfUtil.replaceInPara(doc,paramsPara);
            //替换表格中的内容
            //xwpfUtil.replaceInTable(doc,paramsTable);
            CreateRowInTable(doc,stat);

            OutputStream os = response.getOutputStream();

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition","attachment;filename="+filename);//文件名中文不显示
            //把构造好的文档写入输出流
            doc.write(os);
            //关闭流
            xwpfUtil.close(os);
            xwpfUtil.close(is);
            os.flush();
            os.close();
        } catch (IOException e) {
            //logger.error("文件导出错误");
        }
    }

    /**
     * 循环创建表格行记录
     * @param doc 要替换的文档
     * @param customStatList 发展性指标内容
     */
    public void CreateRowInTable(XWPFDocument doc, List<Evaluate_custom> customStatList) {
        List<XWPFTable> tables = doc.getTables();
        //附件中的最后1个表格为发展性指标表
        for (int i = 0; i < tables.size(); i++) {
            XWPFTable table = tables.get(i);
            if (null != customStatList && 0 < customStatList.size() && i == tables.size()-1){
                insertTable(table,customStatList);
            }
        }

    }
    /**
     * 为表格插入数据，行数不够添加新行
     * @param table 需要插入数据的表格
     * @param xzqList 第四个表格的插入数据
     */
    public static void insertTable(XWPFTable table, List<Evaluate_custom> customStatList) {
        //插入表头下面第一行的数据
        for (int i = 0; i < customStatList.size(); i++) {
            Evaluate_custom stat = customStatList.get(i);
            double weight= stat.getWeights();
            double score_s= stat.getScore_s();
            XWPFTableRow row = table.createRow();
            List<XWPFTableCell> cells = row.getTableCells();
            //第一列序号
            cells.get(0).setText(String.valueOf(i+1));
            //第二列指标名称
            cells.get(1).setText(stat.getIndexname());
            //第三列具体发展性目标（任务）
            cells.get(2).setText(stat.getTaskname());
            //第四列达成标志
            cells.get(3).setText(stat.getTaskdetail());
            //第五列原有基础自我分析
            cells.get(4).setText(stat.getAnalysis_s());
            //第六列权重
            cells.get(5).setText(String.format("%.2f",weight));
            //第七列自评得分
            cells.get(6).setText(String.format("%.2f",score_s));
            cells.get(7).setText(stat.getRemark_s());

        }
    }


}

