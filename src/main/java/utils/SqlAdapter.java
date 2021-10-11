
package utils;

public class SqlAdapter {
    
    public static String search(String[] colSearch, String keyWord) {
        String sqlWhere = "";

        String[] keyWordArr = keyWord.trim().split(" ");

        for (String key : keyWordArr) {
            if (key != "") {   
                sqlWhere += sqlWhere == "" ? "" : " AND ";
                sqlWhere += " (";
                for (int i2 = 0; i2 < colSearch.length; i2++) {
                    sqlWhere += i2 > 0 ? " OR " : "";
                    sqlWhere += " lower(" + colSearch[i2].toLowerCase() + ") LIKE '%" + key.toLowerCase() + "%' ";
                }
                sqlWhere += ") ";
            }
        }
        
        if(!sqlWhere.equals("")){
            sqlWhere = " (" + sqlWhere + ") ";
        }
        return sqlWhere;
    }
}
