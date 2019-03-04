/*
 * Copyright 2019 Vijayakumar Mohan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iomolecule.mods.main;


import com.google.common.eventbus.EventBus;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import com.iomolecule.aop.matchers.MethodNameMatcher;
import com.iomolecule.config.CompositeConfigurationSource;
import com.iomolecule.config.CompositeMsgConfigSource;
import com.iomolecule.config.ConfigurationSource;
import com.iomolecule.config.MsgConfigSource;
import com.iomolecule.config.annotations.ConfigsSource;
import com.iomolecule.config.annotations.DefaultConfigsSource;
import com.iomolecule.config.annotations.MsgConfigsSource;
import com.iomolecule.module.ModuleInfo;
import com.iomolecule.module.MoleculeModule;
import com.iomolecule.system.*;
import com.iomolecule.system.Param;
import com.iomolecule.system.annotations.*;
import com.iomolecule.system.services.*;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The Main Module implementation in the Molecule Framework.
 *
 */
@Slf4j
public class DefaultMainModule extends MoleculeModule{

    private ModuleInfo systemInfo;
    private ConfigurationSource[] configSources;
    private Class<? extends LifecycleManager> lifecycleManagerClass;
    private Class<? extends OnStartup>[] startupClazzes;
    private Class<? extends OnExit>[] exitClazzes;
    private Object[] eventSinks;
    private String[] mainArgs;
    private OnStartup[] onStartupInstances;
    private OnExit[] onExitInstances;
    private String[] domainDefinitionPaths;
    private List<Class> fnProviderClasses;
    private Map<Class,TypeConverter> typeConverterMap;

    public DefaultMainModule(ModuleInfo moduleInfo,
                             ConfigurationSource[] configurationSources,
                             Class<? extends LifecycleManager> lifecycleManagerClazz,
                             Object[] eventSinks, String[] mainArgs,
                             Class<? extends OnStartup>[] startupclzes,
                             Class<? extends OnExit>[] onexitClzes,OnStartup[] onStartupInstances,OnExit[] onExitInstances,
                             String[] domainDefinitionPaths,List<Class> fnProviderClasses,
                             Map<Class,TypeConverter> typeConverterMap) {
        checkArgument(moduleInfo != null,"ModuleInfo cannot be null or empty!");
        checkArgument(lifecycleManagerClazz != null, "Lifecycle Manager class cannot be null or empty!");
        this.systemInfo = moduleInfo;
        configSources = configurationSources;
        lifecycleManagerClass = lifecycleManagerClazz;
        this.eventSinks = eventSinks;
        this.mainArgs = mainArgs;
        this.startupClazzes = startupclzes;
        this.exitClazzes = onexitClzes;
        this.onStartupInstances = onStartupInstances;
        this.onExitInstances = onExitInstances;
        this.domainDefinitionPaths = domainDefinitionPaths;
        this.fnProviderClasses = fnProviderClasses;
        this.typeConverterMap = typeConverterMap;
    }

    @Override
    protected void configure() {
        super.configure();

        bindMainArgs();

        bindMainLifecycleManager();

        bindLifecycleManager();

        bindEventSinks();

        bindStartupAndExitInstances();

        bindDomainOperations();

        bindConfigsSources();

        bindMsgConfigsSources();

        bindFuns();

        bindFunc();

        bindMethodFnProviders();

        bindInterceptors();

        bindTypeConverters();

        initModule();

        registerFuncs(ListAllFnsFunction.class,
                ListSystemPropertiesFunction.class,
                ListAllDomainsFunction.class,
                ListAllModulesFunction.class);

        registerDomainOperationsForSystem();

        registerFnProviders();
    }

    private void bindTypeConverters() {
        MapBinder<Class,TypeConverter> typeConverterBinder = MapBinder.newMapBinder(binder(),Class.class,TypeConverter.class);

        if(typeConverterMap != null && !typeConverterMap.isEmpty()){
            typeConverterMap.forEach((clz,converter)->{
                typeConverterBinder.addBinding(clz).toInstance(converter);
            });
        }
    }

    private void registerFnProviders() {

        Multibinder<Object> fnProviders = Multibinder.newSetBinder(binder(),Object.class,FnProvider.class);

        if(fnProviderClasses != null && !fnProviderClasses.isEmpty()){
            for (Class fnProviderClass : fnProviderClasses) {
                fnProviders.addBinding().to(fnProviderClass).in(Singleton.class);
            }

        }
    }

    private void registerDomainOperationsForSystem() {
        if(domainDefinitionPaths != null && domainDefinitionPaths.length > 0){
            for (String domainDefinitionPath : domainDefinitionPaths) {
                registerDomainOperationsFromClasspath(domainDefinitionPath);
            }

        }
    }

    private void bindInterceptors() {

        if(hasToPrintLifecycleTime()){
            bindInterceptor(Matchers.subclassesOf(LifecycleManager.class),new MethodNameMatcher("start","stop"),
                    new LifecycleMgrStartupTimeInterceptor());
        }

        if(!isNotificationDisabled()){
            NotificationInterceptor onEntryInterceptor = new NotificationInterceptor(NotifyOnEntry.class);
            NotificationInterceptor onExitInterceptor = new NotificationInterceptor(NotifyOnExit.class);
            binder().requestInjection(onEntryInterceptor);
            binder().requestInjection(onExitInterceptor);
            bindInterceptor(Matchers.any(),Matchers.annotatedWith(NotifyOnEntry.class),onEntryInterceptor);
            bindInterceptor(Matchers.any(),Matchers.annotatedWith(NotifyOnExit.class),onExitInterceptor);
        }

        binder().bind(FnInterceptionService.class).to(FnInterceptionServiceImpl.class).in(Singleton.class);


    }

    private boolean isNotificationDisabled() {
        return systemInfo.getAttributes().containsKey("sys.ignore-notification-annotation");
    }

    private boolean hasToPrintLifecycleTime() {
        return systemInfo.getAttributes().containsKey("sys.print-lifecycle-time");
    }

    private void bindMainLifecycleManager() {
        binder().bind(LifecycleManager.class).annotatedWith(Names.named("main")).to(MainLifecycleManager.class).in(Singleton.class);
    }



    private void bindMsgConfigsSources() {
        Multibinder<ConfigurationSource> msgConfigSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                MsgConfigsSource.class);
    }

    private void bindFunc() {
        Multibinder<Function<Param,Param>> funcSet = Multibinder.newSetBinder(binder(),new TypeLiteral<Function<Param,Param>>(){},
                Func.class);

    }

    private void bindFuns() {
        Multibinder<Fn> funs = Multibinder.newSetBinder(binder(),new TypeLiteral<Fn>(){},
                Fun.class);

        Multibinder<List<Fn>> funsList = Multibinder.newSetBinder(binder(),new TypeLiteral<List<Fn>>(){},
                Funs.class);

    }

    private void bindMethodFnProviders(){
        Multibinder<Object> methodFns = Multibinder.newSetBinder(binder(),Object.class,MethodFnProvider.class);
    }

    private void bindConfigsSources() {
        Multibinder<ConfigurationSource> configsSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                ConfigsSource.class);
        Multibinder<ConfigurationSource> defaultConfigsSources = Multibinder.newSetBinder(binder(),new TypeLiteral<ConfigurationSource>(){},
                DefaultConfigsSource.class);

        if(configSources != null && configSources.length > 0){
            for (ConfigurationSource configSource : configSources) {
                if(configSource != null) {
                    configsSources.addBinding().toInstance(configSource);
                }
            }

        }

    }

    private void bindDomainOperations() {
        Multibinder<List<Operation>> domainOperations = Multibinder.newSetBinder(binder(),new TypeLiteral<List<Operation>>(){},
                DomainOperations.class);
    }

    private void bindStartupAndExitInstances() {
        Multibinder<OnStartup> onStartupMultibinder = Multibinder.newSetBinder(binder(),OnStartup.class);
        if(startupClazzes != null && startupClazzes.length >0){
            for (Class<? extends OnStartup> startupClazz : startupClazzes) {
                onStartupMultibinder.addBinding().to(startupClazz);
            }

        }

        if(onStartupInstances != null && onStartupInstances.length > 0){
            for (OnStartup onStartupInstance : onStartupInstances) {
                onStartupMultibinder.addBinding().toInstance(onStartupInstance);
            }

        }

        Multibinder<OnExit> onExitMultibinder = Multibinder.newSetBinder(binder(),OnExit.class);
        if(exitClazzes != null && exitClazzes.length >0){
            for (Class<? extends OnExit> exitClazz : exitClazzes) {
                onExitMultibinder.addBinding().to(exitClazz);
            }

        }

        if(onExitInstances != null && onExitInstances.length > 0){
            for (OnExit onExitInstance : onExitInstances) {
                onExitMultibinder.addBinding().toInstance(onExitInstance);
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
        log.debug("Binding LifecycleManager to {}",lifecycleManagerClass);
        //bind the lifecycle manager class in singleton scope
        bind(LifecycleManager.class).to(lifecycleManagerClass).in(Singleton.class);
    }

    @Provides
    @Singleton
    public TypeConversionService provideTypeConversionService(Map<Class,TypeConverter> converterMap){
        return new TypeConversionServiceImpl(converterMap);
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

        log.debug("EventSinkSet {}",eventSinksSet);
        return new EventsServiceImpl(syncEventBus,asyncEventBus,eventSinksSet.toArray());
    }

    @Provides
    @Singleton
    public SysLifecycleCallbackService provideSysLifecycleCallbackService(Set<OnStartup> startups,
                                                                          Set<OnExit> exits,
                                                                          @MainArgs Optional<String[]> mainArgs){

        return new SysLifecycleCallbackServiceImpl(startups,exits,mainArgs);
    }

    @Provides
    @Singleton
    public DomainService provideDomain(@DomainOperations Set<List<Operation>> operationsSet){
        log.debug("OperationsSet {}",operationsSet);
        return new DefaultDomainService(operationsSet);
    }

    @Provides
    @Singleton
    public ConfigurationSource provideCompositeConfigSource(
            @DefaultConfigsSource Set<ConfigurationSource> defaultConfigurationSources,
            @ConfigsSource Set<ConfigurationSource> configurationSources){
        return new CompositeConfigurationSource(defaultConfigurationSources,configurationSources);
    }

    @Provides
    @Singleton
    public MsgConfigSource provideMessageConfigSource(@MsgConfigsSource Set<ConfigurationSource> messageConfigSources){
        return new CompositeMsgConfigSource(messageConfigSources);
    }

    @Provides
    @Singleton
    public FnBus provideDefaultFnBus(Injector injector, @Fun Set<Fn> fns, @Funs Set<List<Fn>> fnsList,
                                     @Func Set<Function<Param,Param>> functions,
                                     @AsyncEventBus EventBus eventBus,
                                     @MethodFnProvider Set<Object> methodFnProviders,
                                     @FnProvider Set<Object> fnProviderClasses,
                                     FnInterceptionService fnInterceptionService,
                                     MsgConfigSource msgConfigSource){

        return new DefaultFnBus(injector,fns,fnsList,functions,methodFnProviders,
                fnProviderClasses,eventBus,msgConfigSource,fnInterceptionService);
    }


    @ProvidesIntoSet
    @FnInterceptors
    @Singleton
    public FnInterceptor provideInOutInterceptor(TypeConversionService typeConversionService){
        return new FnInOutInterceptor(typeConversionService);
    }




}
