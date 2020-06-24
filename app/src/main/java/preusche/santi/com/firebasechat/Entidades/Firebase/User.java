package preusche.santi.com.firebasechat.Entidades.Firebase;

/**
 * Created by user on 19/02/2018. 19
 */

public class User {

    private String profilePicURL;
    private String name;
    private String email;
    private long birthDate;
    private String sex;



    public User() {
    }

    public User(String profilePicURL, String name, String email, long birthDate, String sex) {
        this.profilePicURL = profilePicURL;
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.sex = sex;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
