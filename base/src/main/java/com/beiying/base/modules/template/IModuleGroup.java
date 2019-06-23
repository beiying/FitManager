package com.beiying.base.modules.template;

import com.beiying.annotation.ModuleMeta;

import java.util.Map;

/**
 * Created by cangwang on 2017/8/31.
 */

public interface IModuleGroup {
      void loadInto(Map<String, ModuleMeta> info);
}
