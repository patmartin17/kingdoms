package com.rivensoftware.hardcoresmp.house.taxes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaxTask 
{
	public static int payTimeHour = 20;
	
	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    volatile boolean isStopIssued;

    public TaxTask() 
    {
    	startExecutionAt(payTimeHour, 0, 0);
    }
    
    public void startExecutionAt(int targetHour, int targetMin, int targetSec)
    {
        Runnable taskWrapper = new Runnable(){

            @Override
            public void run() 
            {
                /*
                 * Put tax system here
                 */
                startExecutionAt(targetHour, targetMin, targetSec);
            }
            
        };
        long delay = computeNextDelay(targetHour, targetMin, targetSec);
        executorService.schedule(taskWrapper, delay, TimeUnit.SECONDS);
    }

    private long computeNextDelay(int targetHour, int targetMin, int targetSec) 
    {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.systemDefault();
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNextTarget = zonedNow.withHour(targetHour).withMinute(targetMin).withSecond(targetSec);
        if(zonedNow.compareTo(zonedNextTarget) > 0)
            zonedNextTarget = zonedNextTarget.plusDays(1);
        
        Duration duration = Duration.between(zonedNow, zonedNextTarget);
        return duration.getSeconds();
    }
    
    public void stop()
    {
        executorService.shutdown();
        try { executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
        }
    }
}
