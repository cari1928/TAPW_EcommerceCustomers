package mx.edu.itcelaya.ecommercecustomers;

import java.util.List;

/**
 * Created by Radogan on 2016-12-09.
 */

public class Cupones {
    int id;
    String code;
    String expiry_date;
    String emails;

    public Cupones(int id, String code, String expiry_date, String emails) {
        this.id = id;
        this.code = code;
        this.expiry_date = expiry_date;
        this.emails = emails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }
}
