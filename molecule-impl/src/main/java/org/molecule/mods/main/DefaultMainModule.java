package org.molecule.mods.main;


import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import lombok.extern.slf4j.Slf4j;
import org.cfg4j.source.ConfigurationSource;
import org.molecule.module.ModuleInfo;
import org.molecule.module.annotations.ModulesInfo;
import org.molecule.system.LifecycleManager;
import org.molecule.system.OnExit;
import org.molecule.system.OnStartup;
import org.molecule.system.annotations.AsyncEventBus;
import org.molecule.system.annotations.EventSink;
import org.molecule.system.annotations.MainArgs;
import org.molecule.system.annotations.SyncEventBus;
import org.molecule.system.services.EventsService;
import org.molecule.system.services.SysLifecycleCallbackService;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.molecule.util.CollectionUtils.KV;
import static org.molecule.util.CollectionUtils.MAP;

@Slf4j
public class DefaultMainModule extends AbstractModule{

    private ModuleInfo systemInfo;
    private ConfigurationSource[] configSources;
    private Class<? extends LifecycleManager> lifecycleManagerClass;
    private Class<? extends OnStartup>[] startupClazzes;
    private Class<? extends OnExit>[] exitClazzes;
    private Object[] eventSinks;
    private String[] mainArgs;

    public DefaultMainModule(ModuleInfo moduleInfo,
                             ConfigurationSource[] configurationSources,
                             Class<? extends LifecycleManager> lifecycleManagerClazz,
                             Object[] eventSinks, String[] mainArgs,
                             Class<? extends OnStartup>[] startupclzes,
                             Class<? extends OnExit>[] onexitClzes) {
        checkArgument(moduleInfo != null,"ModuleInfo cannot be null or empty!");
        checkArgument(lifecycleManagerClazz != null, "Lifecycle Manager class cannot be null or empty!");
        this.systemInfo = moduleInfo;
        configSources = configurationSources;
        lifecycleManagerClass = lifecycleManagerClazz;
        this.eventSinks = eventSinks;
        this.mainArgs = mainArgs;
        this.startupClazzes = startupclzes;
        this.exitClazzes = onexitClzes;
    }

    @Override
    protected void configure() {

        bindMainArgs();

        bindLifecycleManager();

        bindEventSinks();

        bindStartupAndExitInstances();
    }

    private void bindStartupAndExitInstances() {
        Multibinder<OnStartup> onStartupMultibinder = Multibinder.newSetBinder(binder(),OnStartup.class);
        if(startupClazzes != null && startupClazzes.length >0){
            for (Class<? extends OnStartup> startupClazz : startupClazzes) {
                onStartupMultibinder.addBinding().to(startupClazz);
            }

        }

        Multibinder<OnExit> onExitMultibinder = Multibinder.newSetBinder(binder(),OnExit.class);
        if(exitClazzes != null && exitClazzes.length >0){
            for (Class<? extends OnExit> exitClazz : exitClazzes) {
                onExitMultibinder.addBinding().to(exitClazz);
            }

        }
  }

    private void bindMainArgs() {
        OptionalBinder optionalBinder = OptionalBinder.newOptionalBinder(binder(), Key.get(String[].class,MainArgs.class));
        if(mainArgs != null){
            optionalBinder.setBinding().toInstance(mainArgs);
        }
    }

    private void bindEventSinks() {
        Multibinder<Object> multibinder = Multibinder.newSetBinder(binder(), Object.class, EventSink.class);
        if(eventSinks != null && eventSinks.length > 0){
            for (Object eventSink : eventSinks) {
                multibinder.addBinding().toInstance(eventSink);
            }

        }
    }

    private void bindLifecycleManager() {
        log.info("Binding LifecycleManager to {}",lifecycleManagerClass);
        //bind the lifecycle manager class in singleton scope
        bind(LifecycleManager.class).to(lifecycleManagerClass).in(Singleton.class);
    }

    @ProvidesIntoSet
    @ModulesInfo
    public ModuleInfo provideSystemInfo(){
        if(systemInfo != null){
            return systemInfo;
        }else{
            return new ModuleInfo("Main","1.0.0","Molecule",MAP(KV("default",true)));
        }
    }

    @Provides
    @SyncEventBus
    @Singleton
    public EventBus provideSyncEventBus(){
        return new EventBus("sync-eventbus");
    }

    @Provides
    @AsyncEventBus
    @Singleton
    public EventBus provideAsyncEventBus(@Named("asyncbus-executor") ExecutorService executorService){


        return new com.google.common.eventbus.AsyncEventBus("async-eventbus",executorService);
    }


    @Provides
    @Named("asyncbus-executor")
    @Singleton
    public ExecutorService provideExecutorService(){
        return Executors.newFixedThreadPool(1); //for now only one thread is assigned
    }


    @Provides
    @Singleton
    public EventsService provideEventSinkRegistrationService(@SyncEventBus EventBus syncEventBus,
                                                             @AsyncEventBus EventBus asyncEventBus,
                                                             @EventSink Set<Object> eventSinksSet){

        log.info("EventSinkSet {}",eventSinksSet);
        return new EventsServiceImpl(syncEventBus,asyncEventBus,eventSinksSet.toArray());
    }

    @Provides
    @Singleton
    public SysLifecycleCallbackService provideSysLifecycleCallbackService(Set<OnStartup> startups,
                                                                          Set<OnExit> exits,
                                                                          @MainArgs Optional<String[]> mainArgs){

        return new SysLifecycleCallbackServiceImpl(startups,exits,mainArgs);
    }

}
