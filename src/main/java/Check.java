import java.util.Map;
import java.util.Objects;

public class Check {
     private String code;
     private Map<String,Integer> products;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    public boolean isTheSameCode(String code){
        return this.code.equals(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Check check = (Check) o;
        return Objects.equals(code, check.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Check{" +
                "code='" + code + '\'' +
                ", products=" + products +
                '}';
    }

}
