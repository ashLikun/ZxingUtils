package com.ashlikun.zxing;

import com.ashlikun.zxing.able.AbleManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author　　: 李坤
 * 创建时间: 2022/5/3 13:37
 * 邮箱　　：496546144@qq.com
 *
 * 功能介绍：
 */

class RespectScalePool extends ThreadPoolExecutor {

    public RespectScalePool(int corePoolSize,
                            int maximumPoolSize,
                            long keepAliveTime,
                            TimeUnit unit,
                            BlockingQueue<? extends Runnable> workQueue,
                            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, (BlockingQueue<Runnable>) workQueue, handler);
    }

    /***
     *     线程池任务拒绝策略：
     *        目前会执行的两个线程
     *               GrayProcessThread ： {@link AbleManager#grayProcessHandler$delegate}
     *              CameraProcessThread ： {@link com.google.android.cameraview.BaseCameraView#cameraHandler}
     *
     */
    public static class RespectScalePolicy extends DiscardOldestPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown())
                return;
            if (!(r instanceof TypeRunnable) ||
                    !(executor instanceof RespectScalePool) ||
                    !(executor.getQueue() instanceof RespectScaleQueue)) {
                super.rejectedExecution(r, executor);
                return;
            }

            TypeRunnable typeRunnable = (TypeRunnable) r;
            RespectScaleQueue<?> respectScaleQueue = (RespectScaleQueue<?>) executor.getQueue();
            //舍弃同类型任务
            respectScaleQueue.poll(typeRunnable.type);
            executor.execute(r);
        }
    }
}
