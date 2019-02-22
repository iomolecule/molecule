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
import com.iomolecule.system.Event;
import com.iomolecule.system.annotations.AsyncEventBus;
import com.iomolecule.system.annotations.NotifyOnEntry;
import com.iomolecule.system.annotations.NotifyOnExit;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Objects;

@Slf4j
class NotificationInterceptor implements MethodInterceptor{

    private Class<? extends Annotation> annotationType;
    private EventBus eventBus;

    NotificationInterceptor(Class<? extends Annotation> annotationType){
        Objects.requireNonNull(annotationType,"Annotation Type");
        this.annotationType = annotationType;
    }

    @Inject
    void setEventBus(@AsyncEventBus EventBus eventBus){
        this.eventBus = eventBus;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        Annotation annotation = methodInvocation.getMethod().getAnnotation(annotationType);

        if(annotation instanceof NotifyOnEntry){
            NotifyOnEntry notifyOnEntry = (NotifyOnEntry)annotation;

            String eventName = notifyOnEntry.value();

            eventBus.post(Event.create(eventName));
        }

        Object response = methodInvocation.proceed();

        if(annotation instanceof NotifyOnExit){
            NotifyOnExit notifyOnExit = (NotifyOnExit)annotation;

            String eventName = notifyOnExit.value();

            eventBus.post(Event.create(eventName));
        }

        return response;
    }
}
