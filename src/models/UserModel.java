package models;

public class UserModel {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public UserModel(String userName, boolean admin) {
        this.userName = userName;
        this.admin = admin;
    }

    public UserModel() {
    }

    private String userName;
    private boolean admin;
}
