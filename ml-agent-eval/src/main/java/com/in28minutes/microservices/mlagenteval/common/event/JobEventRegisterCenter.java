package com.in28minutes.microservices.mlagenteval.common.event;

import com.google.common.eventbus.AsyncEventBus;
import com.in28minutes.microservices.mlagenteval.common.event.handler.JobStatusEventHandler;
import com.in28minutes.microservices.mlagenteval.common.event.handler.JobExecEventHandler;
import com.in28minutes.microservices.mlagenteval.utils.ThreadPoolUtils;

/***
 * event register center
 * 
 * @author share
 */

public class JobEventRegisterCenter {

    private static final AsyncEventBus EVENT_BUS = new AsyncEventBus(ThreadPoolUtils.getThreadPoolExecutor());

    static {
        EVENT_BUS.register(new JobExecEventHandler());
        EVENT_BUS.register(new JobStatusEventHandler());
    }

    public JobEventRegisterCenter() {}

    public static void post(Object event) {
        EVENT_BUS.post(event);
    }
}