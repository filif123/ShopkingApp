package sk.shopking.shopkingapp.tools;

import android.util.Log;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sk.shopking.shopkingapp.model.Category;
import sk.shopking.shopkingapp.model.DPHType;
import sk.shopking.shopkingapp.model.JednotkaType;
import sk.shopking.shopkingapp.model.NakupAdapter;
import sk.shopking.shopkingapp.model.NakupenyTovar;
import sk.shopking.shopkingapp.model.Obchod;
import sk.shopking.shopkingapp.model.Pokladnica;
import sk.shopking.shopkingapp.model.Tovar;
import sk.shopking.shopkingapp.model.TovarZlavaCena;
import sk.shopking.shopkingapp.model.TovarZlavaMnozstvo;
import sk.shopking.shopkingapp.model.User;
import sk.shopking.shopkingapp.model.UserAdministrator;
import sk.shopking.shopkingapp.model.UserPokladnik;
import sk.shopking.shopkingapp.model.UserType;

public class Database {

    /**
     * Adresa na server
     */
    public static String addressToServer;

    public static final String DATABASE_NAME = "shopking";

    private static final String LOGIN_NAME = "app";
    private static final String LOGIN_PASSWORD = "BYGYNHckXzN7I4ad";
    private static final String ROOT_PASSWORD = "hesloroot";

    /**
     * Zistí, či sa dá na daný server pripojiť
     * @param url - adresa servera
     * @param name - meno pouivatela
     * @param password - heslo pouzivatela
     * @return <strong>true</strong>, ak je spojenie s databázou úspešné
     */
    public static boolean isServerExists(String url,String name, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.getConnection("jdbc:mysql://" + url, name, password);
        } catch (Exception ex) {
            return false;
        }
        return true;

    }


    /**
     * Zistí, či sa dá na danú databázu pripojiť
     * @return <strong>true</strong>, ak je spojenie s databázou úspešné
     */
    public static boolean isConnectionAvailable() {
        try {
            InetAddress address = InetAddress.getByName(addressToServer);
            if (!address.isReachable(500)){
                return false;
            }
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(1);
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + addressToServer + "/" + DATABASE_NAME + "?zeroDateTimeBehavior=convertToNull", LOGIN_NAME, LOGIN_PASSWORD);
            DriverManager.setLoginTimeout(0);
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }
    /**
     * Vráti komunikáciu so serverom
     * @return komunikáciu
     */
    public static Connection getConnectionToServer() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + addressToServer, LOGIN_NAME, LOGIN_PASSWORD);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("ERROR",ex.toString());
        }
        return con;
    }

    /**
     * Vráti komunikáciu so serverom
     * @param url - adresa servera
     * @param name - meno pouivatela
     * @param password - heslo pouzivatela
     * @return komunikáciu
     */
    public static Connection getConnectionToServer(String url, String name, String password) {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + url, name, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("ERROR",ex.toString());
        }
        return con;
    }

    /**
     * Vráti komunikáciu s databázou
     * @return komunikáciu
     */
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://" + addressToServer + "/" + DATABASE_NAME + "?zeroDateTimeBehavior=convertToNull", LOGIN_NAME, LOGIN_PASSWORD);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("ERROR",ex.toString());
        }
        return con;
    }


    /**
     * Vráti kategóriu z databázy podľa id
     * @param id kategórie
     * @return kategóriu tovarov na zadanom id
     */
    public static Category getSpecificCategory(int id) {
        Category category= null;
        String query = "SELECT * FROM categories WHERE id=?";

        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,id);

            ResultSet rSet = ps.executeQuery();

            while (rSet.next()) {
                String nazov = rSet.getString("nazov");
                boolean pristupnePreMladistvych =  rSet.getBoolean("pristupne_pre_mladistvych");
                boolean deleted = rSet.getBoolean("deleted");
                if (!deleted) {
                    category = new Category(id,nazov, pristupnePreMladistvych);
                }

            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return category;
    }

    /**
     * Vráti všetky tovary, ktoré sa nachádzajú v databáze
     * @return tovary z databázy
     */
    public static List<Tovar> getTovary() {
        List<Tovar> tovaryFromDBList = new ArrayList<>();

        ResultSet rSet;

        try(Connection conn = getConnection()){
            Statement statement = conn.createStatement();
            rSet = statement.executeQuery("SELECT * FROM tovary");
            while (rSet.next()) {
                int plu = rSet.getInt("plu");
                String nazov = rSet.getString("nazov");
                float cena = rSet.getFloat("cena");
                DPHType dphType = DPHType.valueOf(rSet.getString("dph"));
                long ean = Long.parseLong(rSet.getString("ean"));
                JednotkaType jednotkaType = JednotkaType.valueOf(rSet.getString("jednotka"));
                Category category = getSpecificCategory(rSet.getInt("id_category"));
                float zlavaNovaCena = rSet.getFloat("zlava_nova_cena");
                int zlavaPovodneMnozstvo = rSet.getInt("zlava_povodne_mnozstvo");
                int zlavaNoveMnozstvo = rSet.getInt("zlava_nove_mnozstvo");
                int zlavaMinimalneMnozstvo = rSet.getInt("zlava_min_mnozstvo");
                boolean deleted = rSet.getBoolean("deleted");
                if (!deleted) {
                    if (zlavaNovaCena != 0) {
                        TovarZlavaCena tovar = new TovarZlavaCena(plu,nazov,category,jednotkaType,ean,cena,dphType,zlavaNovaCena);
                        tovaryFromDBList.add(tovar);
                    }
                    else if (zlavaPovodneMnozstvo != 0 && zlavaNoveMnozstvo != 0 && zlavaMinimalneMnozstvo != 0) {
                        TovarZlavaMnozstvo tovar = new TovarZlavaMnozstvo(plu,nazov,category,jednotkaType,ean,cena,dphType, zlavaPovodneMnozstvo, zlavaNoveMnozstvo, zlavaMinimalneMnozstvo);
                        tovaryFromDBList.add(tovar);
                    }
                    else {
                        Tovar tovar = new Tovar(plu,nazov,category,jednotkaType,ean,cena,dphType);
                        tovaryFromDBList.add(tovar);
                    }
                }

            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return tovaryFromDBList;
    }

    /**
     * Vráti tovar z databázy podľa EAN kodu
     * @param ean tovaru
     * @return tovar
     */
    public static Tovar getSpecificTovar(long ean) {
        Tovar tovar = null;
        String query = "SELECT * FROM tovary WHERE ean=?";

        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1,ean);

            ResultSet rSet = ps.executeQuery();
            while (rSet.next()) {
                int plu = rSet.getInt("plu");
                String nazov = rSet.getString("nazov");
                float cena = rSet.getFloat("cena");
                DPHType dphType = DPHType.valueOf(rSet.getString("dph"));
                JednotkaType jednotkaType = JednotkaType.valueOf(rSet.getString("jednotka"));
                Category category =  getSpecificCategory(rSet.getInt("id_category"));
                float zlavaNovaCena = rSet.getFloat("zlava_nova_cena");
                int zlavaPovodneMnozstvo = rSet.getInt("zlava_povodne_mnozstvo");
                int zlavaNoveMnozstvo = rSet.getInt("zlava_nove_mnozstvo");
                int zlavaMinimalneMnozstvo = rSet.getInt("zlava_min_mnozstvo");
                boolean deleted = rSet.getBoolean("deleted");
                if (!deleted) {
                    if (zlavaNovaCena != 0) {
                        tovar = new TovarZlavaCena(plu,nazov,category,jednotkaType,ean,cena,dphType,zlavaNovaCena);
                    }
                    else if (zlavaPovodneMnozstvo != 0 && zlavaNoveMnozstvo != 0 && zlavaMinimalneMnozstvo != 0) {
                        tovar = new TovarZlavaMnozstvo(plu,nazov,category,jednotkaType,ean,cena,dphType, zlavaPovodneMnozstvo, zlavaNoveMnozstvo, zlavaMinimalneMnozstvo);
                    }
                    else {
                        tovar = new Tovar(plu,nazov,category,jednotkaType,ean,cena,dphType);
                    }
                }
            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return tovar;
    }


    /**
     * Vráti všetkých používateľov, ktorí sú uložený v databáze
     * @return používateľov z databázy
     */
    public static List<User> getUsers() {
        List<User> usersFromDBList = new ArrayList<User>();
        ResultSet rSet;

        try(Connection conn = getConnection()){
            Statement statement = conn.createStatement();
            rSet = statement.executeQuery("SELECT * FROM users");
            while (rSet.next()) {
                int id = rSet.getInt("id");
                String meno = rSet.getString("meno");
                String priezvisko =  rSet.getString("priezvisko");
                String nickname =  rSet.getString("nickname");
                int hash = rSet.getInt("hash");
                float sumaFloat = rSet.getFloat("suma_v_pokladnici");
                boolean deleted = rSet.getBoolean("deleted");
                UserType userType = UserType.valueOf(rSet.getString("usertype"));
                if(!deleted) {
                    if (userType.equals(UserType.ADMIN)) {
                        usersFromDBList.add(new UserAdministrator(id,meno,priezvisko,nickname,hash));
                    }
                    else {
                        usersFromDBList.add(new UserPokladnik(id,meno,priezvisko,nickname,hash,sumaFloat));
                    }
                }
            }
            rSet.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());

        }
        return usersFromDBList;
    }


    /**
     * Vráti používateľa z databázy podľa id
     * @return používateľa na zadanom id
     */
    public static User getSpecificUser(int id) {
        User user = null;
        String query = "SELECT * FROM users WHERE id=?";

        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,id);

            ResultSet rSet = ps.executeQuery();

            while (rSet.next()) {
                String meno = rSet.getString("meno");
                String priezvisko =  rSet.getString("priezvisko");
                String nickname =  rSet.getString("nickname");
                int hash = rSet.getInt("hash");
                float sumaFloat = rSet.getFloat("suma_v_pokladnici");
                boolean deleted = rSet.getBoolean("deleted");
                UserType userType = UserType.valueOf(rSet.getString("usertype"));
                if(!deleted) {
                    if (userType.equals(UserType.ADMIN)) {
                        user = new UserAdministrator(id,meno,priezvisko,nickname,hash);
                    }
                    else {
                        user = new UserPokladnik(id,meno,priezvisko,nickname,hash,sumaFloat);
                    }
                }
            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return user;
    }

    /**
     * Vráti používateľa z databázy podla jeho použivateľského mena a hesla
     * @param username prihlasovacie meno
     * @param password heslo pouzivatela
     * @return používateľa z databázy, alebo NULL, ak neexistuje zhoda
     */
    public static User loginUser(String username,String password) {
        String query = "SELECT * FROM users WHERE nickname=? AND hash=?";
        int hash = User.hashPassword(password);
        User prihlaseny = null;

        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,username);
            ps.setInt(2,hash);

            ResultSet rSet = ps.executeQuery();

            while (rSet.next()) {
                int id = rSet.getInt("id");
                String meno = rSet.getString("meno");
                String priezvisko =  rSet.getString("priezvisko");
                String nickname =  rSet.getString("nickname");
                float sumaFloat = rSet.getFloat("suma_v_pokladnici");
                boolean deleted = rSet.getBoolean("deleted");
                UserType userType = UserType.valueOf(rSet.getString("usertype"));
                if(!deleted) {
                    if (userType.equals(UserType.ADMIN)) {
                        prihlaseny = new UserAdministrator(id,meno,priezvisko,nickname,hash);
                    }
                    else {
                        prihlaseny = new UserPokladnik(id,meno,priezvisko,nickname,hash,sumaFloat);
                    }
                }
            }
            rSet.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());

        }
        return prihlaseny;
    }


    /**
     * Vráti informácie o obchode (o spoločnosti a prevádzke)
     * @return informácie o obchode
     */
    public static Obchod infoObchod() {
        Obchod obchod = null;
        ResultSet rSet;

        try(Connection conn = getConnection()){
            Statement statement = conn.createStatement();
            rSet = statement.executeQuery("SELECT * FROM obchod");
            while (rSet.next()) {
                String obchodnyNazov = rSet.getString("obchodny_nazov");
                String nazovObchodu = rSet.getString("nazov_obchodu");
                String sidloMesto =  rSet.getString("sidlo_mesto");
                String sidloUlica =  rSet.getString("sidlo_ulica");
                String sidloPSC =  rSet.getString("sidlo_psc");
                String sidloCislo =  rSet.getString("sidlo_cislo");
                String ico =  rSet.getString("ico");
                String dic =  rSet.getString("dic");
                String icDPH =  rSet.getString("icdph");
                String prevadzkaMesto =  rSet.getString("prevadzka_mesto");
                String prevadzkaUlica =  rSet.getString("prevadzka_ulica");
                String prevadzkaPSC =  rSet.getString("prevadzka_psc");
                String prevadzkaCislo =  rSet.getString("prevadzka_cislo");
                String logoPathString =  rSet.getString("logo_path");

                if (logoPathString != null) {
                    File logoFile = new File(logoPathString);
                    obchod = new Obchod(obchodnyNazov, nazovObchodu, sidloMesto, sidloUlica, sidloPSC, sidloCislo, ico, dic, icDPH, prevadzkaMesto, prevadzkaUlica, prevadzkaPSC, prevadzkaCislo,logoFile);
                }
                else {
                    obchod = new Obchod(obchodnyNazov, nazovObchodu, sidloMesto, sidloUlica, sidloPSC, sidloCislo, ico, dic, icDPH, prevadzkaMesto, prevadzkaUlica, prevadzkaPSC, prevadzkaCislo,null);
                }

            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return obchod;
    }


    /**
     * Vráti všetky pokladnice, ktorí sú uložený v databáze
     * @return pokladnice z databázy
     */
    public static List<Pokladnica> getPokladnice() {
        List<Pokladnica> pokladnice = new ArrayList<>();
        ResultSet rSet;
        try(Connection conn = getConnection()){
            Statement statement = conn.createStatement();
            rSet = statement.executeQuery("SELECT * FROM pokladnice");
            while (rSet.next()) {
                int id = rSet.getInt("id");
                String dkp = rSet.getString("dkp");
                boolean isOpen = rSet.getBoolean("otvorena");
                UserPokladnik userPokladnik = (UserPokladnik) getSpecificUser(rSet.getInt("id_pokladnik"));
                String ip = rSet.getString("ip");

                Pokladnica tatoPokladnica = new Pokladnica(id,dkp,isOpen,userPokladnik,ip);
                pokladnice.add(tatoPokladnica);
            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }
        return pokladnice;
    }

    /**
     * Vráti IP adresu pokladnika
     * @return IP adresu pokladnika, ktory je prave pri pokladni, ak nie , vrati NULL
     */
    public static String getIPPokladnikaOnline(int userId) {
        String query = "SELECT otvorena,ip FROM pokladnice WHERE id_pokladnik=?";
        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,userId);

            ResultSet rSet = ps.executeQuery();
            if (rSet.next()) {
                boolean isOpen = rSet.getBoolean("otvorena");
                String ip = rSet.getString("ip");

                if (isOpen){
                    return ip;
                }
                else {
                    return null;
                }
            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());

        }
        return null;
    }

    public static int createNewCustomer() {
        String query = "INSERT INTO zakaznici (`id`) VALUES (NULL)";

        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int)generatedKeys.getLong(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setNakupZakaznika(int idZakaznik, List<NakupenyTovar> nakupeneTovary){
        String query = "INSERT INTO nakup_zakaznikov_online SET tovar_plu=?,zakaznik_id=?,mnozstvo=?";
        for (NakupenyTovar nakupenyTovar : nakupeneTovary){
            try(Connection conn = getConnection()){
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1,nakupenyTovar.getTovarPLU());
                ps.setInt(2,idZakaznik);
                ps.setFloat(3,nakupenyTovar.getNakupeneMnozstvo());

                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR",e.toString());

            }
        }
    }

    public static boolean isNakupZakaznikaUkonceny(int idZakaznik){
        String query = "SELECT ukonceny FROM zakaznici WHERE id=?";
        try(Connection conn = getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,idZakaznik);

            ResultSet rSet = ps.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("ukonceny");
            }
            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());

        }
        return false;
    }

}
