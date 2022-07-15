package com.x.bridge.interfaces;

/**
 * @author AD
 * @date 2022/7/15 9:03
 */
public abstract class Service implements IService {

    private volatile boolean started = false;

    @Override
    public final boolean start() {
        if (!started) {
            try {
                started = true;
                this.onStart();
            } catch (Exception e) {
                e.printStackTrace();
                onStartError(e);
            }
        }
        return started;
    }

    @Override
    public final void stop() {
        if (started) {
            try {
                this.onStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            started = false;
        }
    }

    protected abstract boolean onStart() throws Exception;

    protected abstract boolean onStop() throws Exception;

    protected abstract boolean onStartError(Throwable e);

}
