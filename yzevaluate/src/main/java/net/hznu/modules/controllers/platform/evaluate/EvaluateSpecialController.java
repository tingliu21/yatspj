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
		parametersMap.put("taskname",taskname.substring(0,2)+"幼儿园");

/*		List<String> schoolnames=null;
		List<Record> list =evaluateRecordsService.getSchoollistbygroup(taskname);
		for(Record rec:list) {
			String schoolname=rec.getString("name");
		}*/

		//Monitor_catalog catalog=
		List<String> eids = evaluateRecordsService.getEvaluateIdsByTaskname(taskname);
		String[] evaluateIds = eids.toArray(new String[eids.size()]);
		String strSql="SELECT evaluateid,";
		String strSchoolSql="";
		for(int i=0;i<8;i++){
			strSchoolSql+=" sum(case when monitor_catalog.location=1 then score_p else 0 end) as index_"+i+",";
		}
		strSql += strSchoolSql;
		strSql +=" sum(score_p)"+
				" FROM evaluate_remark \n" +
				" inner join monitor_index on monitor_index.id = evaluate_remark.indexid\n" +
				" inner join monitor_catalog on monitor_index.catalogid = monitor_catalog.id\n" +
				" where evaluateid in (@evaIds)\n" +
				" group by evaluateid order by evaluateid";

		Sql sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
		List<Record> remarkData = evaluateRecordsService.list(sql);

		//获取专家评估的发展性指标
/*		strSql = "SELECT 10 as no, '发展性指标' as indexname,10 as weights,";
		strSql += strSchoolSql.substring(0,strSchoolSql.length()-1);
		strSql +=" FROM evaluate_custom where evaluateid in (@evaIds)";
		sql = Sqls.create(strSql).setParam("evaIds",evaluateIds);
		List<Record> custom = list(sql);*/

		for (Record rec:remarkData ) {
			Map<String, Object> map=new HashMap<>();
			Map<String, Object> mapDetail=new HashMap<>();
			map.put("school",rec.getString("evaluateid"));
			map.put("YWGL",formatDouble(rec.getDouble("index_1")));//园务管理
			map.put("JYJX",formatDouble(rec.getDouble("index_2")));//教育教学
			map.put("JSFZ",formatDouble(rec.getDouble("index_3")));//教师发展
			map.put("WHXY",formatDouble(rec.getDouble("index_4")));//文化校园
			map.put("AQHQ",formatDouble(rec.getDouble("index_5")));//安全后勤
			map.put("WSBJ",formatDouble(rec.getDouble("index_6")));//卫生保健
			map.put("MYDCP",formatDouble(rec.getDouble("index_7")));//满意度测评
			map.put("TSLD",formatDouble(rec.getDouble("index_8")));//特色亮点
			map.put("custom",formatDouble(rec.getDouble("custom")));//发展性指标（未加到sql语句里）
			map.put("sum",formatDouble(rec.getDouble("sum")));//总分
			map.put("specialLeader",formatDouble(rec.getDouble("specialLeader")));//组长打分（未加到sql语句里）
			map.put("special2",formatDouble(rec.getDouble("special2")));//专家2打分（未加到sql语句里）
			map.put("special3",formatDouble(rec.getDouble("special3")));//专家3打分（未加到sql语句里）
			map.put("special4",formatDouble(rec.getDouble("special4")));//专家4打分（未加到sql语句里）

			table_aggregate.add(map);
		}
		wordDataMap.put("table_aggregate",table_aggregate);
		wordDataMap.put("parametersMap", parametersMap);
		return wordDataMap;
	}
	public String formatDouble(double d) {
		BigDecimal bg = new BigDecimal(d).setScale(1, RoundingMode.UP);
		double num = bg.doubleValue();
		if (Math.round(num) - num == 0) {
			return String.valueOf((long) num);
		}
		return String.valueOf(num);
	}

}
