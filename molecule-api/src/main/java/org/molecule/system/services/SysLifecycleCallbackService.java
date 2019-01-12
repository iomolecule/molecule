package org.molecule.system.services;

public interface SysLifecycleCallbackService {

    public void invokeAllStartupCallbacks();

    public void invokeAllExitCallbacks();


}
