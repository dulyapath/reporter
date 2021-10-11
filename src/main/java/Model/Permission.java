package Model;

public class Permission {
    String menuCode;
    boolean create;
    boolean read;
    boolean update;
    boolean delete;

    public Permission() {
        this.menuCode = "";
        this.create = false;
        this.read = false;
        this.update = false;
        this.delete = false;
    }
    
    public boolean isHasCRUD(){
        return this.create || this.read || this.update || this.delete;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
    
    
}
