package ord.example.LegalManagementSystem.util;

public class SortUtils {
    public static String nextOrder(String currentField, String currentOrder, String column) {
        if (!column.equals(currentField)) {
            return "asc";
        }
        return "asc".equalsIgnoreCase(currentOrder) ? "desc" : "asc";
    }

    public static String sortArrow(String currentField, String currentOrder, String column) {
        if (!column.equals(currentField)) return "";
        return "asc".equalsIgnoreCase(currentOrder) ? "↑" : "↓";
    }
}
