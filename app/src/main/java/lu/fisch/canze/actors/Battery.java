/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.actors;

/**
 * Battery
 *
 * This class represents a mathematical representation of the battery and it's charging behavior.
 * Please note that this class has no relation with the real battery in the car. It is ONLY used
 * for prediction and it's SOC, or any parameter, or any calculation is an approximation. For non
 * predictive functions, always use the paramters as they are supplied by the car's LBC.
 *
 */
public class Battery {

    /*
     * Meta code on usage
     *
     * Battery battery;
     * // initialize the values. No need to initialize dcPower
     * battery = new Battery();
     * battery.setTemperature (...);
     * battery.setStateOfCharge (...);
     * battery.setChargerPower (...);
     * for (int t=1; t<=60; t++) {
     *      draw (battery, t); // imaginary method that plots SOC, range, time
     *      battery.iterateCharging (60);
     * }
     *
     * Some rough parameters derived from this document: http://www.cse.anl.gov/us%2Dchina%2Dworkshop%2D2011/pdfs/batteries/LiFePO4%20battery%20performances%20testing%20for%20BMS.pdf
     *
     * Still to implement
     * - state of health
     * - discharge behavior
     *
     */

    private double temperature = 10.0;
    private double stateOfCharge = 11.0;                // watch it: in kWh!!!
    private double chargerPower = 11.0;                 // in kW
    private double capacity = 22.0;                     // in kWh
    private double maxDcPower = 0;                      // in kW. This excludes the max imposed by the external charger
    private double dcPower = 0;                         // in kW This includes the max imposed by the external charger
    private int secondsRunning = 0;                     // seconds in iteration, reset by setStateOfChargePerc
    private double dcPowerUpperLimit = 40.0;            // for R240/R90 use 20
    private double dcPowerLowerLimit = 2.0;             // for R240/R90 use 1
    private double rawCapacity = 22;                    // R90/Q90 use 41

    private void predictMaxDcPower () {

        // if the state of charge (in kW) exceeds the capacity of the battery
        if (stateOfCharge >= capacity) {

            // stop charging
            maxDcPower = 0.0;

        // if there is capacity left to charge
        } else {

            // calculate the SOC in percantage
            double stateOfChargePercentage = stateOfCharge * 100.0 / capacity;

            // get a rounded temperature
            int intTemperature = (int )temperature;

            // now use a model to calculate the DC power, based on SOC and temperature
            maxDcPower = 19.0 + (3.6 * intTemperature) - (0.026 * stateOfChargePercentage * intTemperature) - (0.34 * stateOfChargePercentage);
            //maxDcPower = 27.1 + (0.76 * intTemperature) - (0.27 * stateOfChargePercentage);

            if (maxDcPower > dcPowerUpperLimit) {
                maxDcPower = dcPowerUpperLimit;
            } else if (maxDcPower < dcPowerLowerLimit) {
                maxDcPower = dcPowerLowerLimit;

            }
        }
    }

    private void predictDcPower() {

        // calculate what the battery can take
        predictMaxDcPower();

        // predict the efficiency of the charger (assuming it will run at the cpacity the battery can take)
        double efficiency = 0.80 + maxDcPower * 0.00375;

        // predict what is needed on the AC side to give thabattery what it can take
        double requestedAcPower = maxDcPower / efficiency;

        // if this is more than what the charger can deliver
        if (requestedAcPower > chargerPower) {

            // recalculate the efficiency based on the maximum the charger can deliver
            efficiency = 0.80 + chargerPower * 0.00375;

            // DC is maximum AC corrected for efficiency
            dcPower = chargerPower * efficiency;

        // if this is less than the charger can delever
        } else {

            // DC is what the battery can take
            dcPower = maxDcPower;
        }
    }

    /*
     * iteration is in effect numerical integration of the power function to the SOC, respecting temperature and energy efficiency effects
     */

    public void iterateCharging (int seconds) {
        secondsRunning += seconds;
        predictDcPower ();
        setTemperature (temperature + (seconds * dcPower / 7200)); // assume one degree per 40 kW per 3 minutes (180 seconds)
        setStateOfChargeKw (stateOfCharge + (dcPower * seconds * 0.95) / 3600); // 1kW adds 95% of 1kWh in 60 minutes
    }

    /*
     * Getters and setters
     */

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        setRawCapacity(getRawCapacity());
    }

    public double getStateOfChargeKw() {
        return stateOfCharge;
    }

    public void setStateOfChargeKw(double stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
        if (this.stateOfCharge > this.capacity) this.stateOfCharge = this.capacity;
    }

    public double getStateOfChargePerc() {
        return stateOfCharge * 100 / this.capacity;
    }

    public void setStateOfChargePerc(double stateOfCharge) {
        setStateOfChargeKw(stateOfCharge * this.capacity / 100);
    }

    public double getChargerPower() {
        return chargerPower;
    }

    public void setChargerPower(double chargerPower) {
        this.chargerPower = chargerPower;
        if (this.chargerPower > 43.0) {
            this.chargerPower = 43.0;
        } else if (this.chargerPower < 1.84) {
            this.chargerPower = 1.84;
        }
    }
    public double getMaxDcPower() {
        return maxDcPower;
    }

    public double getDcPower() {
        predictDcPower ();
        return dcPower;
    }

    public int getTimeRunning () { return secondsRunning; }

    public void setTimeRunning (int secondsRunning) {
        this.secondsRunning = secondsRunning;
    }

    public double getDcPowerUpperLimit() {
        return dcPowerUpperLimit;
    }

    public void setDcPowerUpperLimit(double dcPowerUpperLimit) {
        this.dcPowerUpperLimit = dcPowerUpperLimit;
    }

    public double getDcPowerLowerLimit() {
        return dcPowerLowerLimit;
    }

    public void setDcPowerLowerLimit(double dcPowerLowerLimit) {
        this.dcPowerLowerLimit = dcPowerLowerLimit;
    }

    public double getRawCapacity() {
        return rawCapacity;
    }

    public void setRawCapacity(double rawCapacity) {
        this.rawCapacity = rawCapacity;

        // adjust for capacity loss due to temperature differences (system wide)
        if (temperature > 15.0) {
            capacity = rawCapacity;
        } else if (temperature > 0) {
            capacity = 0.9 * rawCapacity + temperature * 2.2 / 15.0;
        } else {
            capacity = 0.9 * rawCapacity + temperature * 4.4 / 15.0;
        }

        // ensure the SOC is refreshed. This is only relevant for a very full battery
        setStateOfChargeKw(getStateOfChargeKw());

    }
}
