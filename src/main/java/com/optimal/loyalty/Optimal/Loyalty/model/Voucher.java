package com.optimal.loyalty.Optimal.Loyalty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "Voucher")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "category_id")
    private int categoryId;

    private int points;

    private String title;

    private String image;

    private String description;

    private String terms_and_condition;

    private  boolean is_latest;

    public boolean isIs_latest() {
        return is_latest;
    }

    public void setIs_latest(boolean is_latest) {
        this.is_latest = is_latest;
    }

    public String getTerms_and_condition() {
        return terms_and_condition;
    }

    public void setTerms_and_condition(String terms_and_condition) {
        this.terms_and_condition = terms_and_condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
