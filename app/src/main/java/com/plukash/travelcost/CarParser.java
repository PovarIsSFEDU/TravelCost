package com.plukash.travelcost;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

import static java.lang.Double.parseDouble;

public class CarParser {
    private ArrayList<Car> cars;

    public CarParser() {
        cars = new ArrayList<>();
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public boolean parse(XmlPullParser xpp) {
        boolean status = true;
        Car currentCar = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("row".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentCar = new Car();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            if ("row".equalsIgnoreCase(tagName)) {
                                cars.add(currentCar);
                                inEntry = false;
                            } else if ("Mark".equalsIgnoreCase(tagName)) {
                                currentCar.setMarkName(textValue);
                            } else if ("Engine".equalsIgnoreCase(tagName)) {
                                currentCar.setEngine(textValue);
                            } else if ("Gorod".equalsIgnoreCase(tagName)) {
                                currentCar.setGorod(parseDouble(textValue));
                            } else if ("Trass".equalsIgnoreCase(tagName)) {
                                currentCar.setTrass(parseDouble(textValue));
                            } else if ("Smesh".equalsIgnoreCase(tagName)) {
                                currentCar.setSmesh(parseDouble(textValue));
                            } else if ("Fuel".equalsIgnoreCase(tagName)) {
                                currentCar.setFuel(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
