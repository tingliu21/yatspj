package net.hznu.modules;

import org.junit.runners.model.InitializationError;
import org.nutz.mock.NutTestRunner;

import net.hznu.common.core.Module;

public class NutzWkIocTestRunner extends NutTestRunner {
    public NutzWkIocTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Class<?> getMainModule() {
        return Module.class;
    }
}
