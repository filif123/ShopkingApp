package sk.shopking.shopkingapp.model;

/**
 * Trieda Category určuje kategóriu tovaru, do ktorej výrobok patrí.
 * @author Filip
 *
 */
public class Category {
    private int categoryID;
    private String categoryName;
    private boolean categoryPristupnePreMladistvych;

    public Category(int id,String categoryName, boolean pristupnePreMladistvych){

        this.setCategoryID(id);
        this.setCategoryName(categoryName);
        this.setCategoryPristupnePreMladistvych(pristupnePreMladistvych);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean getCategoryPristupnePreMladistvych() {
        return categoryPristupnePreMladistvych;
    }

    public void setCategoryPristupnePreMladistvych(boolean categoryPristupnePreMladistvych) {
        this.categoryPristupnePreMladistvych = categoryPristupnePreMladistvych;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return categoryName;
    }

}

