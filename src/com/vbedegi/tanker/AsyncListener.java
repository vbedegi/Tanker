package com.vbedegi.tanker;

public abstract class AsyncListener<TResult, TError> {
    public abstract void completed(TResult... result);

    public abstract void failed(TError... result);
}
