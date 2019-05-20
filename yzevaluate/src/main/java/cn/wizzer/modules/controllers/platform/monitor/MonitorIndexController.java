package cn.wizzer.modules.controllers.platform.monitor;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import cn.wizzer.modules.models.monitor.Monitor_index;
import cn.wizzer.modules.services.monitor.MonitorCatalogService;
import cn.wizzer.modules.services.monitor.MonitorIndexService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/monitor/index")
@Filters({@By(type = PrivateFilter.class)})
public class MonitorIndexController {
	private static final Log log = Logs.get();
	@Inject
	private MonitorIndexService monitorIndexService;
	@Inject
	private MonitorCatalogService monitorCatalogService;

	@At("")
	@Ok("beetl:/platform/monitor/index/index.html")
	@RequiresAuthentication
	public void index(@Param("unitType") String unitType,HttpServletRequest req) {

		req.setAttribute("unitType",unitType);
	}


	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("catalogId") String catalogId, @Param("name") String name,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
		if (!Strings.isBlank(catalogId) && !"0".equals(catalogId)) {
			cnd.and("catalogId", "like", "%" + catalogId + "%");
		}
		if (!Strings.isBlank(name)) {
			cnd.and("name", "like", "%" + name + "%");
		}
    	return monitorIndexService.data(length, start, draw, order, columns, cnd, "dept");
    }

    @At
    @Ok("beetl:/platform/monitor/index/add.html")
    @RequiresAuthentication
    public void add(@Param("unitType") String unitType,@Param("catalogId") String catalogId,HttpServletRequest req) {
		req.setAttribute("unitType", unitType);
		req.setAttribute("catalog", catalogId != null && !"0".equals(catalogId) ? monitorCatalogService.fetch(catalogId) : null);
//		return Strings.isBlank(catalogId)?null: monitorCatalogService.fetch(catalogId);
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.index.add")
    @SLog(tag = "新建三级指标", msg = "指标名称:${args[0].name}")
    public Object addDo(@Param("..") Monitor_index monitorIndex, HttpServletRequest req) {
		try {
			monitorIndexService.insert(monitorIndex);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/monitor/index/edit.html")
    @RequiresAuthentication
    public Object edit(String id,@Param("unitType") String unitType,HttpServletRequest req) {
		Monitor_index index = monitorIndexService.fetch(id);
		index = monitorIndexService.fetchLinks(index,"dept");
		req.setAttribute("unitType", unitType);
		req.setAttribute("id", id);
		return index;
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.index.edit")
    @SLog(tag = "修改三级指标", msg = "三级指标名称:${args[0].name}")
    public Object editDo(@Param("..") Monitor_index monitorIndex, HttpServletRequest req) {
		try {
			monitorIndex.setOpBy(Strings.sNull(req.getAttribute("uid")));
			monitorIndex.setOpAt((int) (System.currentTimeMillis() / 1000));
			monitorIndexService.updateIgnoreNull(monitorIndex);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
	@RequiresPermissions("monitor.index.delete")
	@SLog(tag = "删除三级指标", msg = "三级指标名称:${args[1].getAttribute('name')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				monitorIndexService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				monitorIndexService.delete(id);
    			req.setAttribute("id", id);
			}
			return null;
//			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/monitor/index/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return monitorIndexService.fetch(id);

		}
		return null;
    }

}
