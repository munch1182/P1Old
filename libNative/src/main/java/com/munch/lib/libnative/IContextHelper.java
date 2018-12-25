package com.munch.lib.libnative;

import android.content.Context;
import android.support.annotation.Nullable;

/**
 * Created by Munch on 2018/12/25 23:50.
 */
public interface IContextHelper {

    @Nullable
    default Context getViewContext() {
        return null;
    }
}
