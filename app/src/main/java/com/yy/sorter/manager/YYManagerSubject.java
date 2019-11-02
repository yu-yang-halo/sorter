package com.yy.sorter.manager;

import java.util.ArrayList;

import th.service.core.*;

/**
 * YYManagerSubject
 * 目标类
 */

public class YYManagerSubject {
    private boolean changed = false;
    private final ArrayList<YYMangerObserver> observers;


    public YYManagerSubject() {
        observers = new ArrayList<>();
    }

    public synchronized void addObserver(YYMangerObserver o) {
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
        YYMangerObserver[] arrLocal;

        synchronized (this) {
            if (!hasChanged())
                return;

            arrLocal = observers.toArray(new YYMangerObserver[observers.size()]);
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
