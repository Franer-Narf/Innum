package nc.instrumentum.innum;

import androidx.annotation.NonNull;

public class ListClass {

    private int idL;
    private String nameL;

    ListClass(int iId, String iTitle) {
        this.idL = iId;
        this.nameL = iTitle;
    }


    public int getIdL() {
        return this.idL;
    }

    public void setIdL(int id) {
        this.idL = id;
    }

    public String getNameL() {
        return this.nameL;
    }

    public void setNameL(String name) {
        this.nameL = name;
    }

    @NonNull
    @Override
    public String toString() {
        return nameL;
    }

    public String toStringComplete() {
        return idL+" "+nameL;
    }
}
