package com.example.backend_breakable_toy_i_todoapp.model;

public class AverageDetails {
    public double highAverage;
    public double mediumAverage;
    public double lowAverage;
    public double totalAverage;

    public AverageDetails(){};
    public AverageDetails(double highAverage, double mediumAverage, double lowAverage, double totalAverage){
        this.highAverage = highAverage;
        this.mediumAverage = mediumAverage;
        this.lowAverage = lowAverage;
        this.totalAverage = totalAverage;
    }
}
