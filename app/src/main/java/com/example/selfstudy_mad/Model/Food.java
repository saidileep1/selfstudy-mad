package com.example.selfstudy_mad.Model;

public class Food {
  private String Name,Image,MenuId,Description,Price;

  public Food(){
        }

    private Food(String name, String image, String menuId, String description, String price) {
        this.Name = name;
        this.Image = image;
        this.MenuId = menuId;
        this.Description = description;
        this.Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
