package sk.shopking.shopkingapp.model;

import java.util.Objects;

/**
 * Trieda User určuje informácie o používateľovi.
 * @author Filip
 *
 */
public abstract class User{

    private String userMeno;
    private String userPriezvisko;
    private String userNickname;
    private int hashHeslo;
    protected UserType userType;
    private int id;


    public User(int id,String meno, String priezvisko, String nick, int hash) {

        this.setId(id);
        this.setUsertype();
        this.setUserMeno(meno);
        this.setUserPriezvisko(priezvisko);
        this.setUserNickname(nick);
        this.setHashHeslo(hash);
    }

    public static int hashPassword(String heslo) {
        return Objects.hash(heslo);
    }

    public String getUserMeno() {
        return userMeno;
    }

    public void setUserMeno(String meno) {
        this.userMeno = meno;
    }

    public String getUserPriezvisko() {
        return userPriezvisko;
    }

    public void setUserPriezvisko(String priezvisko) {
        this.userPriezvisko = priezvisko;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String nick) {
        this.userNickname = nick;
    }

    public int getHashHeslo() {
        return hashHeslo;
    }

    public void setHashHeslo(int hashHeslo) {
        this.hashHeslo = hashHeslo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void setUsertype();

    @Override
    public String toString() {
        return userMeno + " " + userPriezvisko;
    }

}