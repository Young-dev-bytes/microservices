package com.young.microservices.mlagenteval.common.event;

import com.google.common.eventbus.AsyncEventBus;
import com.young.microservices.mlagenteval.common.event.handler.JobExecEventHandler;
import com.young.microservices.mlagenteval.common.event.handler.JobStatusEventHandler;
import com.young.microservices.mlagenteval.common.event.handler.JobTrackDetailEventHandler;
import com.young.microservices.mlagenteval.utils.ThreadPoolUtils;

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
        EVENT_BUS.register(new JobTrackDetailEventHandler());
    }

    public JobEventRegisterCenter() {}

    public static void post(Object event) {
        EVENT_BUS.post(event);
    }
}
