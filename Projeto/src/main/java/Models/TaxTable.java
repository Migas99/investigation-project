package Models;

public class TaxTable {

    private String TaxType;
    private String TaxCountryRegion;
    private String TaxCode;
    private String Description;
    private String TaxExpirationDate;
    private String TaxPercentage = "-1";
    private String TaxAmount = "-1";

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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTaxExpirationDate() {
        return TaxExpirationDate;
    }

    public void setTaxExpirationDate(String taxExpirationDate) {
        TaxExpirationDate = taxExpirationDate;
    }

    public String getTaxPercentage() {
        return TaxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        TaxPercentage = taxPercentage;
    }

    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
    }
}
