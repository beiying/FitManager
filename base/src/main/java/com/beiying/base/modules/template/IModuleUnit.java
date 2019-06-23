package com.beiying.base.modules.template;


import com.beiying.annotation.ModuleMeta;

import java.util.List;

/**
 * Created by cangwang on 2017/9/4.
 */

public interface IModuleUnit {
    void loadInto(List<ModuleMeta> metaSet);
}
