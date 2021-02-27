package sk.shopking.shopkingapp.model;


import java.text.DecimalFormat;

import sk.shopking.shopkingapp.tools.ShopKingTools;
/**
 * Táto trieda slúži na zobrazenie nákupov uskutočnených zákazníkmi.
 * @author Filip
 *
 */
public class NakupenyTovar extends Tovar{

    private float nakupeneMnozstvo;
    private float nakupenaCena;
    private int noveMnozstvoPoStorne;

    /**
     * Konštruktor pre tovar bez zľavy
     * @param tovarPLU plu tovaru
     * @param tovarName nazov tovaru
     * @param tovarCategory kategoria tovaru
     * @param jednotka jadnotka tovaru
     * @param tovarCode EAN tovaru
     * @param tovarCena jednotkova cena tovaru
     * @param dph DPH tovaru
     * @param mnozstvo mnozstvo tovaru
     */
    public NakupenyTovar(int tovarPLU, String tovarName, Category tovarCategory, JednotkaType jednotka, long tovarCode, float tovarCena, DPHType dph, float mnozstvo) {

        super(tovarPLU,tovarName, tovarCategory, jednotka, tovarCode, tovarCena, dph);

        //this.setMnozstvoProperty("" + new DecimalFormat("#.##").format(mnozstvo));
        this.setNakupeneMnozstvo(mnozstvo);

        if(jednotka.equals(JednotkaType.KS) || jednotka.equals(JednotkaType.KG)) {
            //this.setCenaProperty("" + new DecimalFormat("0.00").format(mnozstvo * tovarCena));
            this.setNakupenaCena(mnozstvo * tovarCena);
            //this.setStornoProperty("" + (int) mnozstvo);
        }
        else if(jednotka.equals(JednotkaType.G)) {
            //this.setCenaProperty("" + new DecimalFormat("0.00").format(ShopKingTools.roundNumber((double)(mnozstvo / 1000) * tovarCena,2)));
            this.setNakupenaCena((float)ShopKingTools.roundNumber((double)(mnozstvo / 1000) * tovarCena,2));
            //this.setStornoProperty("" + 1);
        }

        this.noveMnozstvoPoStorne = 0;
    }

    /**
     * Konštruktor pre tovar s cenovou zľavou
     * @param tovarPLU plu tovaru
     * @param tovarName nazov tovaru
     * @param tovarCategory kategoria tovaru
     * @param jednotka jadnotka tovaru
     * @param tovarCode EAN tovaru
     * @param tovarCena jednotkova cena tovaru
     * @param dph DPH tovaru
     * @param novaCena nova cena tovaru
     */
    public NakupenyTovar(int tovarPLU, String tovarName, Category tovarCategory, JednotkaType jednotka, long tovarCode, float tovarCena, DPHType dph, float mnozstvo, float novaCena) {

        super(tovarPLU,tovarName, tovarCategory, jednotka, tovarCode, novaCena, dph);

        float zlavaVPercentach = 100 - ((novaCena / tovarCena)*100);
        DecimalFormat df = new DecimalFormat("#.#");

        this.setNakupeneMnozstvo(mnozstvo);

        if(jednotka.equals(JednotkaType.KS) || jednotka.equals(JednotkaType.KG)) {
            this.setNakupenaCena(mnozstvo * super.tovarJednotkovaCena);
        }
        else if(jednotka.equals(JednotkaType.G)) {
            this.setNakupenaCena((float) ShopKingTools.roundNumber((double)(mnozstvo / 1000) * super.tovarJednotkovaCena,2));
        }

        this.noveMnozstvoPoStorne = 0;
    }

    /**
     * Konštruktor pre tovar s množstevnou zľavou (len pre tovary s jednotkou množstva v kusoch)
     * @param tovarPLU plu tovaru
     * @param tovarName nazov tovaru
     * @param tovarCategory kategoria tovaru
     * @param jednotka jadnotka tovaru
     * @param tovarCode EAN tovaru
     * @param tovarCena jednotkova cena tovaru
     * @param dph DPH tovaru
     * @param mnozstvo mnozstvo tovaru
     * @param povodneMnozstvo povodne mnozstvo
     * @param noveMnozstvo nove mnozstvo
     * @param minimalneMnozstvo minimalne mnozstvo
     */
    public NakupenyTovar(int tovarPLU, String tovarName, Category tovarCategory, JednotkaType jednotka, long tovarCode, float tovarCena, DPHType dph, float mnozstvo, int povodneMnozstvo, int noveMnozstvo, int minimalneMnozstvo) {
        super(tovarPLU,tovarName, tovarCategory, jednotka, tovarCode, tovarCena, dph);
        if (mnozstvo >= minimalneMnozstvo) {

            this.setNakupeneMnozstvo(mnozstvo);

            if(jednotka.equals(JednotkaType.KS)) {
                int pom = (int)mnozstvo/povodneMnozstvo;
                int upravovaneMnozstvo = pom * noveMnozstvo;
                int ostatneMnozstvo = (int) mnozstvo % povodneMnozstvo;
                int zaplateneMnozstvo = upravovaneMnozstvo + ostatneMnozstvo;

                super.setTovarJednotkovaCena((zaplateneMnozstvo * tovarCena) / mnozstvo);

                this.setNakupenaCena(zaplateneMnozstvo * tovarCena);
            }
        }
        else {
            //this.setMnozstvoProperty(new DecimalFormat("#.##").format(mnozstvo));
            this.setNakupeneMnozstvo(mnozstvo);

            if(jednotka.equals(JednotkaType.KS)) {
                this.setNakupenaCena(mnozstvo * tovarCena);
            }
        }

        this.noveMnozstvoPoStorne = 0;
    }

    public float getNakupeneMnozstvo() {
        return nakupeneMnozstvo;
    }

    public void setNakupeneMnozstvo(float nakupeneMnozstvo) {
        this.nakupeneMnozstvo = nakupeneMnozstvo;
    }

    public float getNakupenaCena() {
        return nakupenaCena;
    }

    public void setNakupenaCena(float nakupenaCena) {
        this.nakupenaCena = nakupenaCena;
    }

    public int getNoveMnozstvoPoStorne() {
        return noveMnozstvoPoStorne;
    }

    public void setNoveMnozstvoPoStorne(int stornovaneMnozstvo) {
        this.noveMnozstvoPoStorne = stornovaneMnozstvo;
    }

}
