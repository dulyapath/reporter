package models;

import java.math.BigDecimal;
import java.util.Date;

public class SaleSummaryLine1 {

    public Date _docDate;
    public BigDecimal _amount;
    public BigDecimal _cost;
    public BigDecimal _profit;
    public BigDecimal _cash;
    public BigDecimal _credit;
    public BigDecimal _purchase;
    
    public Date getDocDate() {
        return this._docDate;
    }

}
