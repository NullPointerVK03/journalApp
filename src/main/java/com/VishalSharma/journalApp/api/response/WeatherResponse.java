package com.VishalSharma.journalApp.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeatherResponse {


    private Current current;

    @Setter
    @Getter
    public static class Current {

        @JsonProperty("temp_c")
        private double tempCelsius;

        @JsonProperty("temp_f")
        private double tempFahrenheit;

        @JsonProperty("feelslike_c")
        private double feelsLikeCelsius;

        @JsonProperty("feelslike_f")
        private double feelsLikeFahrenheit;

    }

}
