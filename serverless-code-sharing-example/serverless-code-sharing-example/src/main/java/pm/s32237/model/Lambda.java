package pm.s32237.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lambda")
public class Lambda {

    @Id
    private String id;

    @Column(name = "code")
    private String code;

    @Column(name = "comment")
    private String comment;

    public Lambda() {
    }

    public Lambda(String id, String code, String comment) {
        this.id = id;
        this.code = code;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
