package sk.shopking.shopkingapp.model;

/**
 * Pomocou tejto triedy sa zobrazujú aktuálne informácie o pokladniciach v obchodnej prevádzke.
 * @author Filip
 *
 */
public class Pokladnica {

    private int idPokladnice;
    private String dkpPokladnice;
    private boolean isOpen;
    private String ipAdresa;
    private UserPokladnik pokladnikUser;

    public Pokladnica(int idPokladnice, String dkpPokladnice, boolean isOpen, UserPokladnik pokladnik, String ipAdresa){
        this.idPokladnice = idPokladnice;
        this.isOpen = isOpen;
        this.dkpPokladnice = dkpPokladnice;
        this.pokladnikUser = pokladnik;
        this.ipAdresa = ipAdresa;

    }

    public int getIdPokladnice() {
        return idPokladnice;
    }

    public void setIdPokladnice(int idPokladnice) {
        this.idPokladnice = idPokladnice;
    }

    public String getDkpPokladnice() {
        return dkpPokladnice;
    }

    public void setDkpPokladnice(String dkpPokladnice) {
        this.dkpPokladnice = dkpPokladnice;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public UserPokladnik getPokladnikUser() {
        return pokladnikUser;
    }

    public void setPokladnikUser(UserPokladnik pokladnikUser) {
        this.pokladnikUser = pokladnikUser;
    }

    public String getIPAdresa() {
        return ipAdresa;
    }

    public void setIPAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    @Override
    public String toString() {
        return "Pokladnica " + idPokladnice;

    }
}
