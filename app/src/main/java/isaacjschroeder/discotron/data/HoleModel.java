package isaacjschroeder.discotron.data;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class HoleModel {

    @Id public long id;

    public ToOne<CourseModel> course;           //One course per hole

    private int number;
    private int par;


    public HoleModel() {}
    public HoleModel(int number, int par) { this.par = par; this.number = number; }


    public int getPar() {
        return par;
    }
    public void setPar(int par) {
        this.par = par;
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
}
