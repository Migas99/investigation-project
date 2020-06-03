package Models;

public class TaxTable {

    private String TaxType;
    private String TaxCountryRegion;
    private String TaxCode;
    private double TaxPercentage = -99;
    private double TaxAmount = -99;

    public String getTaxType() {
        return TaxType;
    }

    public void setTaxType(String taxType) {
        TaxType = taxType;
    }

    public String getTaxCountryRegion() {
        return TaxCountryRegion;
    }

    public void setTaxCountryRegion(String taxCountryRegion) {
        TaxCountryRegion = taxCountryRegion;
    }

    public String getTaxCode() {
        return TaxCode;
    }

    public void setTaxCode(String taxCode) {
        TaxCode = taxCode;
    }

    public double getTaxPercentage() {
        return TaxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        TaxPercentage = taxPercentage;
    }

    public double getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        TaxAmount = taxAmount;
    }
}
