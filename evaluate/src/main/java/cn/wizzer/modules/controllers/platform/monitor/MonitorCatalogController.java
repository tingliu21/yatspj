package cn.wizzer.modules.controllers.platform.monitor;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.monitor.Monitor_catalog;
import cn.wizzer.modules.services.monitor.MonitorCatalogService;
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
@At("/platform/monitor/catalog/")
@Filters({@By(type = PrivateFilter.class)})
public class MonitorCatalogController {
	private static final Log log = Logs.get();
	@Inject
	private MonitorCatalogService monitorCatalogService;

	@At("")
	@Ok("beetl:/platform/monitor/catalog/index.html")
	@RequiresAuthentication
	public void index(@Param("unitType") String unitType,HttpServletRequest req) {
		req.setAttribute("unitType",unitType);
		///where (A or B) and C
		req.setAttribute("list", monitorCatalogService.query(Cnd.where(Cnd.exps("parentId", "=", "").or("parentId", "is", null))
						.and("unitType","=",unitType).asc("path")));

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return monitorCatalogService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/monitor/catalog/add.html")
    @RequiresAuthentication
    public Object add(@Param("unitType") String unitType,@Param("pid") String pid, HttpServletRequest req) {
		req.setAttribute("unitType", unitType);
		return Strings.isBlank(pid) ? null : monitorCatalogService.fetch(pid);
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.catalog.add")
    @SLog(tag = "新建指标体系", msg = "指标体系名称：${args[0].name}")
    public Object addDo(@Param("..") Monitor_catalog monitorCatalog, @Param("parentId") String parentId, @Param("unitType") String unitType, HttpServletRequest req) {
		try {
			monitorCatalogService.save(monitorCatalog,parentId,unitType);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/monitor/catalog/edit.html")
    @RequiresAuthentication
    public Object edit(String id, HttpServletRequest req) {
		Monitor_catalog catalog = monitorCatalogService.fetch(id);
		if(catalog!=null) {
			req.setAttribute("parentMenu", monitorCatalogService.fetch(catalog.getParentId()));
			req.setAttribute("unitType",catalog.getUnitType());
		}
		return catalog;
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.catalog.edit")
    @SLog(tag = "修改指标体系", msg = "指标体系名称:${args[0].name}")
    public Object editDo(@Param("..") Monitor_catalog monitorCatalog, HttpServletRequest req) {
		try {
			monitorCatalog.setOpBy(Strings.sNull(req.getAttribute("uid")));
			monitorCatalog.setOpAt((int) (System.currentTimeMillis() / 1000));
			monitorCatalogService.updateIgnoreNull(monitorCatalog);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
	@RequiresPermissions("monitor.catalog.delete")
    @SLog(tag = "删除指标体系", msg = "指标体系名称:${args[1].getAttribute('name')}")
    public Object delete(String id,HttpServletRequest req) {
		try {
			Monitor_catalog catalog = monitorCatalogService.fetch(id);
			req.setAttribute("name",catalog.getName());
			monitorCatalogService.deleteAndChild(catalog);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
//	@At
//	@Ok("json")
//	@RequiresAuthentication
//	public Object tree(@Param("unitType") String unitType,@Param("pid") String pid) {
//		List<Monitor_catalog> list = monitorCatalogService.query(Cnd.where("parentId", "=", Strings.sBlank(pid)).and("unitType","=",unitType).asc("path"));
//		List<Map<String, Object>> tree = new ArrayList<>();
//		for (Monitor_catalog catalog : list) {
//			Map<String, Object> obj = new HashMap<>();
//			obj.put("id", catalog.getId());
//			obj.put("text", catalog.getName());
//			obj.put("children", catalog.isHasChildren());
//			tree.add(obj);
//		}
//		return tree;
//	}
	// Add by Liut 2018-10-29
	@At({"/tree","/tree/?"})
	@Ok("json")
	@RequiresAuthentication
	public Object tree(String evatype,@Param("unitType") String unitType,@Param("pid") String pid) {
		Cnd cnd = Cnd.where("parentId", "=", Strings.sBlank(pid)).and("unitType","=",unitType);
		if("qualify".equals(evatype)){
			cnd = cnd.and("qualify","=",true);
		}else if("remark".equals(evatype)){
			cnd = cnd.and("qualify","=",false);
		}


		List<Monitor_catalog> list = monitorCatalogService.query(cnd.asc("path"));
		List<Map<String, Object>> tree = new ArrayList<>();
		for (Monitor_catalog catalog : list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", catalog.getId());
			obj.put("text", catalog.getName());
			obj.put("children", catalog.isHasChildren());
			tree.add(obj);
		}
		return tree;
	}
	@At("/child/?")
	@Ok("beetl:/platform/monitor/catalog/child.html")
	@RequiresAuthentication
	public Object child(String id) {

		return monitorCatalogService.query(Cnd.where("parentId", "=", id).asc("path"));
	}


	@At("/detail/?")
    @Ok("beetl:/platform/monitor/catalog/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return monitorCatalogService.fetch(id);

		}
		return null;
    }

}
