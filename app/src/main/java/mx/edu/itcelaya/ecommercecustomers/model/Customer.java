package mx.edu.itcelaya.ecommercecustomers.model;

/**
 * Created by niluxer on 5/19/16.
 */
public class Customer {
    int id;
    String email;
    String first_name;
    String last_name;
    String username;
    Address billing_address;
    Address shipping_address;

    public Customer(int id, String email, String first_name,String last_name,String username,Address billing_address, Address shipping_address)
    {
        this.id               = id;
        this.email            = email;
        this.first_name       = first_name;
        this.last_name        = last_name;
        this.username         = username;
        this.billing_address  = billing_address;
        this.shipping_address = shipping_address;
    }

    public Customer() {}

    public Address getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(Address billing_address) {
        this.billing_address = billing_address;
    }

    public Address getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(Address shipping_address) {
        this.shipping_address = shipping_address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
