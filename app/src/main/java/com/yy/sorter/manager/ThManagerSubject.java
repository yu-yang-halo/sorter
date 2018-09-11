package com.yy.sorter.manager;

import java.util.ArrayList;

import th.service.core.*;

/**
 * Created by Administrator on 2017/3/23.
 * 目标类
 */

public class ThManagerSubject {
    private boolean changed = false;
    private final ArrayList<ThMangerObserver> observers;


    public ThManagerSubject() {
        observers = new ArrayList<>();
    }

    public synchronized void addObserver(ThMangerObserver o) {
        if (o == null)
            throw new NullPointerException();
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    public synchronized void deleteObserver(ThObserver o) {
        observers.remove(o);
    }
    public void notifyObservers() {
        notifyObservers(null,null);
    }
    public void notifyObservers(Object arg,String message) {
        ThMangerObserver[] arrLocal;

        synchronized (this) {
            if (!hasChanged())
                return;

            arrLocal = observers.toArray(new ThMangerObserver[observers.size()]);
            clearChanged();
        }

        for (int i = arrLocal.length-1; i>=0; i--)
            arrLocal[i].update(arg,message);
    }

    public synchronized void deleteObservers() {
        observers.clear();
    }

    protected synchronized void setChanged() {
        changed = true;
    }

    protected synchronized void clearChanged() {
        changed = false;
    }

    public synchronized boolean hasChanged() {
        return changed;
    }

    public synchronized int countObservers() {
        return observers.size();
    }
}
