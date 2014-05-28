package org.kesler.simplereg.logic.reception.filter;

import java.util.Date;
import java.text.SimpleDateFormat;

import org.kesler.simplereg.logic.Reception;

public class StatusChangeDateReceptionsFilter implements ReceptionsFilter {

    private Date fromDate = null;
    private Date toDate = null;

    public StatusChangeDateReceptionsFilter(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean checkReception(Reception reception) {
        if (reception == null) {
            throw new IllegalArgumentException("Reception argument cannot be null");
        }

        Date statusChangeDate = reception.getStatusChangeDate();

        if (statusChangeDate == null) {// если время принятия не задано - не удовлетворяет
            return false;
        }

        if (fromDate != null && statusChangeDate.before(fromDate)) {
            return false;
        }

        if (toDate != null && statusChangeDate.after(toDate)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String filterString = "По дате смены состояния: ";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        filterString += "(";

        if (fromDate != null) {
            filterString += "с " + dateFormat.format(fromDate);
        }

        if (toDate != null) {
            filterString += " по " + dateFormat.format(toDate);
        }

        filterString += ")";

        return filterString;
    }

}