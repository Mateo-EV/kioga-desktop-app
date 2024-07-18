package models;

public class Admin extends TimeStamps implements Identifiable {

    private int id;
    private String name;
    private String email;
    private String password;

    private String image;
    private final static String BASE_IMAGE_URL = "https://ui-avatars.com/api/?name=";

    public Admin() {
    }

    public Admin(int id, String name, String email) {
        this.name = name;
        this.email = email;
        this.image = BASE_IMAGE_URL.concat(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.image = BASE_IMAGE_URL.concat(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
