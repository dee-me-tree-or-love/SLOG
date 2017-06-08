package dmitriiorlov.com.slog.data.models;

/**
 * Created by Dmitry on 6/6/2017.
 */

public class User {
    private String name;
    private String email;

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    // required for the database reference
    public User(){}

    public User(String name, String email) {
        this.name=name;
        this.email = email;
    }
}

