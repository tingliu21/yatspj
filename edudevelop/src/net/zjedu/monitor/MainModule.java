package net.zjedu.monitor;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

//请注意星号!!不要拷贝少了
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
                    // 这个package下所有带@IocBean注解的类,都会登记上
                                        "*anno", "net.zjedu.monitor",
                                        "*tx", // 事务拦截 aop
                                        "*async"}) // 异步执行aop

@Modules(scanPackage=true)
@Ok("json:full")
@Fail("jsp:jsp.500")
public class MainModule {

}
