package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.base.Globals;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.common.util.XwpfUtil;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.sys.Sys_role;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateCustomService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateRemarkService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@IocBean
@At("/platform/evaluate/special")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateSpecialController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private EvaluateRemarkService evaluateRemarkService;
	@Inject
	private EvaluateCustomService evaluateCustomService;

	@At("")
	@Ok("re")
	@RequiresPermissions("evaluate.verify.special")
	public String index(@Param("evaluateId") String evaluateId,@Param("unitType") String unitType,HttpServletRequest req) {
		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			Evaluate_records evaluate = evaluateRecordsService.fetch(evaluateId);
			evaluate = evaluateRecordsService.fetchLinks(evaluate,"school");
			req.setAttribute("schoolname",evaluate.getSchool().getName());
			req.setAttribute("evaluateId",evaluateId);
			req.setAttribute("unitType",unitType);

			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();
			Sys_role role = evaluateRecordsService.getRoleInEvaluate(user.getId(),evaluateId);
			if(role!=null) {
				if (role.getCode().equalsIgnoreCase(Globals.CustomRole)) {
					return "beetl:/platform/evaluate/special/index_develop.html";
				} else {
					return "beetl:/platform/evaluate/special/index_basic.html";
				}
			}
		}return "beetl:/platform/evaluate/special/index_basic.html";

	}

	//专家只列出自己负责的指标
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object basicdata(@Param("evaluateId") String evaluateId, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Cnd cnd = Cnd.where("specialid ","=",user.getId());
			cnd.and("evaluateId", "like", "%" + evaluateId + "%").asc("location");

			return evaluateRemarkService.data(length, start, draw, order, columns, cnd, "");
		}
		return null;
	}
	//专家只列出自己负责的指标
	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object developdata(@Param("evaluateId") String evaluateId, @Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns){

		if (!Strings.isBlank(evaluateId) && !"0".equals(evaluateId)) {
			//获取专家id
			Subject subject = SecurityUtils.getSubject();
			Sys_user user = (Sys_user) subject.getPrincipal();

			Cnd cnd = Cnd.where("specialid","=",user.getId());
			cnd.and("evaluateId", "=",   evaluateId ).asc("location");

			return evaluateCustomService.data(length, start, draw, order, columns, cnd, "");
		}
		return null;
	}
	@At
	@Ok("void")
	@RequiresAuthentication
	public Object aggregate(@Param("taskname") String taskname, HttpServletResponse resp) {

		if (!Strings.isBlank(taskname)) {
			Map<String,Object> wordDataMap = packageObject(taskname);
			XwpfUtil xwpfUtil = new XwpfUtil();
			//读入word模板
			InputStream is = getClass().getClassLoader().getResourceAsStream("template/EvaAggregate-yz.docx");
			try {
				String filename = "宁波市鄞州区2018学年幼儿园发展性评估专家评分汇总表.docx";
				filename = URLEncoder.encode(filename, "UTF-8");

				xwpfUtil.exportWord(wordDataMap,is,resp,filename);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * 组装word文档中需要显示数据的集合
	 * @return
	 */
	public Map<String, Object> packageObject(String taskname) {
		Map<String,Object> wordDataMap = new HashMap<String,Object>();
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		List<Map<String, Object>> table_aggregate = new ArrayList<Map<String, Object>>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy 年 MM 月 dd 日");
		parametersMap.put("date",sdf.format(new Date()));
		parametersMap.put("taskname",taskname.substring(0,3)+"幼儿园");

		List<String> eids = evaluateRecordsService.getEvaluateIdsByTaskname(taskname);
		String[] evaluateIds = eids.toArray(new String[eids.size()]);
		//学校信息
		String strSql="select sys_unit.name from sys_unit\n" +
				"JOIN evaluate_records ON schoolid=sys_unit.id\n" +
				"where evaluate_records.id in (@evaIds)" +
				"order by score_p desc ";
		Sql sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
		List<Record> schoolData = evaluateRecordsService.list(sql);
		//基础性指标
		 strSql="SELECT evaluateid,";
		String strSchoolSql="";
		for(int i=1;i<10;i++){//9个基础性二级指标
			strSchoolSql+=" sum(case when monitor_catalog.location="+i+" then evaluate_remark.score_p else 0 end) as index_"+i+",";
		}
		strSql += strSchoolSql;
		strSql +=" sum(evaluate_remark.score_p) as indexsum,"+
				" sum(case when monitor_index.masterrole='37206ff462b24524a467c74fffb83376' then evaluate_remark.score_p else 0 end) as special2,\n" +
				" sum(case when monitor_index.masterrole='5426f51b1e1345f0a4853345b7288f06' then evaluate_remark.score_p else 0 end) as special3,\n" +
				" sum(case when monitor_index.masterrole='e11b437889ef4a31bc184261c5d05f3c' then evaluate_remark.score_p else 0 end) as special4"+
				" FROM evaluate_remark \n" +
				" inner join monitor_index on monitor_index.id = evaluate_remark.indexid\n" +
				" inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id\n" +
				" inner join evaluate_records on evaluate_records.id=evaluate_remark.evaluateid\n"+
				" where evaluateid in (@evaIds)\n" +
				" group by evaluateid ,evaluate_records.score_p order by evaluate_records.score_p desc";

		 sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
		List<Record> remarkData = evaluateRecordsService.list(sql);

		//发展性指标
		strSql = "SELECT evaluateid,COALESCE(sum(evaluate_custom.score_p),0) as customsum";
		strSql +=" FROM evaluate_custom inner join evaluate_records on evaluate_records.id=evaluate_custom.evaluateid \n"+
				" where evaluateid in (@evaIds) group by evaluateid,evaluate_records.score_p order by evaluate_records.score_p desc";
		sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
		List<Record> customData = evaluateRecordsService.list(sql);

		for (int i=0;i<remarkData.size();i++) {
			Map<String, Object> map=new HashMap<>();
			map.put("school",schoolData.get(i).getString("name"));
			if(remarkData.get(i).get("index_1")!=null) {//园务管理
				double index_1=remarkData.get(i).getDouble("index_1");
				map.put("YWGL", formatDouble(index_1));
			}else{
				map.put("YWGL", "");
			}
			if(remarkData.get(i).get("index_2")!=null) {//教育教学
				double index_2 = remarkData.get(i).getDouble("index_2");
				map.put("JYJX", formatDouble(index_2));
			}else {
				map.put("JYJX","");
			}
			if(remarkData.get(i).get("index_3")!=null) {//教师发展
				double index_3= remarkData.get(i).getDouble("index_3");
				map.put("JSFZ", formatDouble(index_3));
			}else {
				map.put("JSFZ", "");
			}
			if(remarkData.get(i).get("index_4")!=null) {//文化校园
				double index_4=remarkData.get(i).getDouble("index_4");
				map.put("WHXY", formatDouble(index_4));
			}else {
				map.put("WHXY", "");
			}
			if(remarkData.get(i).get("index_5")!=null) {//安全后勤
				double index_5 = remarkData.get(i).getDouble("index_5");
				map.put("AQHQ", formatDouble(index_5));
			}else {
				map.put("AQHQ", "");
			}
			if(remarkData.get(i).get("index_6")!=null) {//卫生保健
				double index_6 = remarkData.get(i).getDouble("index_6");
				map.put("WSBJ", formatDouble(index_6));
			}else {
				map.put("WSBJ", "");
			}
			if(remarkData.get(i).get("index_7")!=null) {//满意度测评
				double index_7 = remarkData.get(i).getDouble("index_7");
				map.put("MYDCP", formatDouble(index_7));//满意度测评
			}else {
				map.put("MYDCP", "");
			}
			if(remarkData.get(i).get("index_8")!=null) {//满意度测评
				double index_8 = remarkData.get(i).getDouble("index_8");
				map.put("TSLD", formatDouble(index_8));
			}else {
				map.put("TSLD", "");
			}
			if(remarkData.get(i).get("index_9")!=null) {//倒扣分项目
				double index_9 = remarkData.get(i).getDouble("index_9");
				map.put("DKFXM", formatDouble(index_9));
			}else {
				map.put("DKFXM", "");
			}
			if(customData.get(i).get("customsum")!=null) {//发展性指标
				double customsum = customData.get(i).getDouble("customsum");
				map.put("customsum", formatDouble(customsum));
			}else {
				map.put("customsum", "");
			}
			if(customData.get(i).get("customsum")!=null||remarkData.get(i).get("indexsum")!=null) {//总分
				double sum = customData.get(i).getDouble("customsum") + remarkData.get(i).getDouble("indexsum");
				map.put("sum", formatDouble(sum));
			}else{
				map.put("sum", "");
			}
			if(remarkData.get(i).get("special2")!=null) {//专家2打分
				double special2= remarkData.get(i).getDouble("special2");
				map.put("special2", formatDouble(special2));
			}else{
				map.put("special2", "");
			}
			if(remarkData.get(i).get("special3")!=null) {//专家3打分
				double special3 = remarkData.get(i).getDouble("special3");
				map.put("special3", formatDouble(special3));
			}else {
				map.put("special3", "");
			}
			if(remarkData.get(i).get("special4")!=null) {//专家4打分
				double special4 = remarkData.get(i).getDouble("special4");
				map.put("special4", formatDouble(special4));
			}else {
				map.put("special4", "");
			}
			table_aggregate.add(map);
		}


		wordDataMap.put("table_aggregate",table_aggregate);
		wordDataMap.put("parametersMap", parametersMap);
		return wordDataMap;
	}
	public String formatDouble(double d) {
		BigDecimal bg = new BigDecimal(String.valueOf(d)).setScale(2, RoundingMode.HALF_UP);
		double num = bg.doubleValue();
		if (Math.round(num) - num == 0) {
			return String.valueOf((long) num);
		}
		return String.valueOf(num);
	}

}
