package com.gymcrm.storage;

import com.gymcrm.model.IdAccessor;

import java.util.HashMap;

public abstract class TypedStorage<T extends IdAccessor> extends HashMap<Long, T> {
    public abstract Class<T> getType();
}
