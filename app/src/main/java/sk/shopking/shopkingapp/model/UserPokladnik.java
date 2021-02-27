package sk.shopking.shopkingapp.model;

/**
 * @author Filip
 *
 */
public class UserPokladnik extends User {

    private float sumaPokladnica;

    public UserPokladnik(int id,String meno, String priezvisko, String nick, int hash, float sumaPokladnica){
        super(id,meno, priezvisko, nick, hash);
        this.sumaPokladnica = sumaPokladnica;
    }

    public float getSumaPokladnica() {
        return sumaPokladnica;
    }

    public void setSumaPokladnica(float sumaPokladnica) {
        this.sumaPokladnica = sumaPokladnica;
    }

    @Override
    public void setUsertype() {
        super.userType = UserType.POKLADNIK;

    }
}
