package org.molecule.system;

import com.github.krukow.clj_ds.PersistentMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import org.molecule.commons.Constants;

import java.util.*;

public class InOutParam implements Param {

    private PersistentMap<String,Object> delegate;

    public InOutParam(){
        delegate = PersistentHashMap.emptyMap();
    }

    public InOutParam(Map<String,Object> inMap){
        delegate = PersistentHashMap.create(inMap);
    }

    private InOutParam(PersistentMap<String,Object> delg){
        this.delegate = delg;
    }

    @Override
    public Param zero() {
        return new InOutParam(delegate.zero());
    }

    @Override
    public Param plus(String s, Object o) {
        return new InOutParam(delegate.plus(s,o));
    }

    @Override
    public Param minus(String s) {
        return new InOutParam(delegate.minus(s));
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Object get(String key) {
        return delegate.get(key);
    }


    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public Map<String,Object> asMap() {
        return delegate;
    }

    @Override
    public List<ParamDeclaration> outParams() {
        return (List<ParamDeclaration>)delegate.getOrDefault(Constants.OUT_PARAMS, Collections.EMPTY_LIST);
    }

    @Override
    public String toString() {
        return "{" +delegate+"}";
    }
}
