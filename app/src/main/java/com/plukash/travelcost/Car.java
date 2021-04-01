package com.plukash.travelcost;

import androidx.annotation.NonNull;

public class Car {

    private String MarkName;
    private String Engine;

    private double Gorod;
    private double Trass;
    private double Smesh;
    private String Fuel;

    public double getTrass() {
        return Trass;
    }

    public void setTrass(double trass) {
        Trass = trass;
    }

    public double getSmesh() {
        return Smesh;
    }

    public void setSmesh(double smesh) {
        Smesh = smesh;
    }

    public String getFuel() {
        return Fuel;
    }

    public void setFuel(String fuel) {
        Fuel = fuel;
    }

    public double getGorod() {
        return Gorod;
    }

    public void setGorod(double gorod) {
        Gorod = gorod;
    }

    public String getMarkName() {
        return MarkName;
    }

    public void setMarkName(String markName) {
        MarkName = markName;
    }

    public String getEngine() {
        return Engine;
    }

    public void setEngine(String engine) {
        Engine = engine;
    }


    @NonNull
    @Override
    public String toString() {
        return MarkName + " " + Engine + " " + Gorod;
    }
}
