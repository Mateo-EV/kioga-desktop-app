package models;

import java.io.File;

public class Brand extends TimeStamps implements Identifiable {

    private int id;
    private String name;
    private String image;
    private File imageFile;
    private String slug;

    public Brand() {
    }

    public Brand(int id, String name, String slug, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.slug = slug;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

}
