package sk.shopking.shopkingapp.model;


/**
 * Trieda Tovar určuje každý výrobok predávajuci v obchode.
 * @author Filip
 *
 */
public class Tovar {

    protected long tovarEAN;
    protected String tovarName;
    protected Category tovarCategory;
    protected JednotkaType tovarJednotka;
    protected float tovarJednotkovaCena;
    protected DPHType tovarDPH;
    protected int tovarPLU;


    public Tovar(int tovarPLU, String tovarName, Category tovarCategory, JednotkaType jednotka, long tovarEAN, float tovarCena, DPHType dph) {

        this.setTovarPLU(tovarPLU);
        this.setTovarName(tovarName);
        this.setTovarCategory(tovarCategory);
        this.setTovarEAN(tovarEAN);
        this.setTovarJednotkovaCena(tovarCena);
        this.setTovarDPH(dph);
        this.setTovarJednotka(jednotka);
    }

    public String getTovarName() {
        return tovarName;
    }

    public void setTovarName(String tovarName) {
        this.tovarName = tovarName;
    }

    public Category getTovarCategory() {
        return tovarCategory;
    }

    public void setTovarCategory(Category tovarCategory) {
        this.tovarCategory = tovarCategory;
    }

    public float getTovarJednotkovaCena() {
        return tovarJednotkovaCena;
    }

    public void setTovarJednotkovaCena(float tovarCena) {
        this.tovarJednotkovaCena = tovarCena;
    }

    public long getTovarEAN() {
        return tovarEAN;
    }

    public void setTovarEAN(long tovarCode) {
        this.tovarEAN = tovarCode;
    }

    public DPHType getTovarDPH() {
        return tovarDPH;
    }

    public void setTovarDPH(DPHType tovarDPH) {
        this.tovarDPH = tovarDPH;
    }

    public JednotkaType getTovarJednotka() {
        return tovarJednotka;
    }

    public void setTovarJednotka(JednotkaType jednotka) {
        this.tovarJednotka = jednotka;
    }

    public int getTovarPLU() {
        return tovarPLU;
    }

    public void setTovarPLU(int plu) {
        this.tovarPLU = plu;
    }

}


