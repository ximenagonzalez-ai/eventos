package co.edu.unbosque.eventos.dto.ticketmaster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmPriceRangeDTO {

    private Double min;
    private Double max;
    private String currency;

    public Double getMin() { return min; }
    public void setMin(Double min) { this.min = min; }

    public Double getMax() { return max; }
    public void setMax(Double max) { this.max = max; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
