package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import net.hznu.modules.services.sys.SysUnitService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IocBean
@At("/platform/evaluate/records")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRecordsController {
	private static final Log log = Logs.get();
	@Inject
	private EvaluateRecordsService evaluateRecordsService;
	@Inject
	private SysUnitService unitService;
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private EvaluateIndexService evaluateIndexService;

	@At("")
	@Ok("beetl:/platform/evaluate/records/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return evaluateRecordsService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/evaluate/records/add.html")
    @RequiresAuthentication
    public void add(HttpServletRequest req) {
		List<Sys_unit> list = unitService.query(Cnd.orderBy().asc("path"));
		List<NutMap> units = new ArrayList<>();
		for (Sys_unit unit : list) {
			NutMap map = new NutMap();

			map.put("id", unit.getId());
			map.put("text", unit.getName());
			map.put("icon", Strings.sBlank(""));
			map.put("parent", "".equals(Strings.sNull(unit.getParentId())) ? "#" : unit.getParentId());
			map.put("data", unit.getUnitcode());
			if ((unit.getPath().length() >=16 || !unit.isHasChildren()) ) {
				map.put("state", NutMap.NEW().addv("selected", true));
			} else {
				map.put("state", NutMap.NEW().addv("selected", false));
			}
			units.add(map);
		}
		req.setAttribute("units", Json.toJson(units));
    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_records", msg = "")
    public Object addDo(@Param("unitIds") String unitIds,@Param("year") int year, HttpServletRequest req) {
		try {
			String[] ids = StringUtils.split(unitIds, ",");
			for (String s : ids) {
				if (!Strings.isEmpty(s)) {
					Evaluate_records records = new Evaluate_records();
					records.setYear(year);

					records.setUnitID(s);


					//插入评估记录
					records = evaluateRecordsService.insert(records);
					List<Monitor_index> monitorIndexs = monitorIndexService.query();

					for (Monitor_index index : monitorIndexs) {
						Evaluate_index evaluate = new Evaluate_index();
						evaluate.setEvaluateId(records.getId());
						evaluate.setIndexId(index.getId());
						evaluate.setCode(index.getCode());

						//插入监测指标记录
						evaluateIndexService.insert(evaluate);
					}
				}
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/records/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {

		return evaluateRecordsService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_records", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_records evaluateRecords, HttpServletRequest req) {
		try {

			evaluateRecords.setOpAt((int) (System.currentTimeMillis() / 1000));
			evaluateRecordsService.updateIgnoreNull(evaluateRecords);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_records", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				evaluateRecordsService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				evaluateRecordsService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/records/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return evaluateRecordsService.fetch(id);

		}
		return null;
    }

	@AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
	@POST
	@At
	@Ok("json")
	@RequiresAuthentication
	//AdaptorErrorContext必须是最后一个参数
	public Object xlsfile(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
		try {
			if (err != null && err.getAdaptorErr() != null) {
				return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
			} else if (tf == null) {
				return Result.error("空文件");
			} else {
				//导入数据库

				return Result.success("上传成功", tf.getSubmittedFileName());
			}
		} catch (Exception e) {
			return Result.error("系统错误");
		} catch (Throwable e) {
			return Result.error("文件格式错误");
		}
	}
}
