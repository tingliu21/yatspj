package net.hznu.modules.controllers.platform.basicdata;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.basicdata.Bascidata_selectionstate;
import net.hznu.modules.services.basicdata.BascidataSelectionstateService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/basicdata/selectionstate")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataSelectionstateController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataSelectionstateService bascidataSelectionstateService;

	@At("")
	@Ok("beetl:/platform/basicdata/selectionstate/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataSelectionstateService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/selectionstate/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_selectionstate", msg = "")
    public Object addDo(@Param("..") Bascidata_selectionstate bascidataSelectionstate, HttpServletRequest req) {
		try {
			bascidataSelectionstateService.insert(bascidataSelectionstate);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/selectionstate/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataSelectionstateService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_selectionstate", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_selectionstate bascidataSelectionstate, HttpServletRequest req) {
		try {

			bascidataSelectionstate.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataSelectionstateService.updateIgnoreNull(bascidataSelectionstate);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_selectionstate", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataSelectionstateService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataSelectionstateService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/selectionstate/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataSelectionstateService.fetch(id);

		}
		return null;
    }

}
