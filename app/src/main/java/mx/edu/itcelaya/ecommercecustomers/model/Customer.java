package mx.edu.itcelaya.ecommercecustomers.model;

/**
 * Created by niluxer on 5/19/16.
 */
public class Customer {
    int id;
    String email;
    String full_name;
    String rol;

    public Customer(int id, String email, String full_name, String username) {
        this.id = id;
        this.email = email;
        this.full_name = full_name;
        this.rol = username;
    }

    public Customer() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return rol;
    }

    public void setUsername(String username) {
        this.rol = username;
    }
}
