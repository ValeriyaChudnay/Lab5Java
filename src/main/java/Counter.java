import java.util.*;

public class Counter {
    public static HashMap<String, Integer> getProductSet(int i, List<Check> checks) {
        HashMap<String, Integer> set = new HashMap<>();
        for (Check c : checks) {
            List<String> prd = new ArrayList<>(c.getProducts().keySet());

            for (int counter = 0; counter <= prd.size() - i; counter++) {
                for (int j = counter; j <= prd.size()-i; j++) {
                    String p = prd.get(counter);
                    //for(int t=j+1;t<=i-1;t++){
                    for(int t=1;t<i;t++){
                    p += "," + prd.get(j+1);
                    }
                    set.put(p, 0);
                }
            }
        }
        return set;
    }

    public static List<Check> deleteAllDimension(int i, List<Check> checks,List<Check> mainCheck) {
        HashMap<String, Integer> allExistingProductsChain = Counter.getProductSet(i, checks);
        HashSet<String> toRemove=new HashSet<>();
        for (String productsChain : allExistingProductsChain.keySet()) {
            List<String> items = Arrays.asList(productsChain.split("\\s*,\\s*"));
            for (Check c : mainCheck) {
                boolean isInCheck = true;
                for (String it : items) {
                    if (!c.getProducts().containsKey(it)) {
                        isInCheck = false;
                        break;
                    }
                }
                if (isInCheck) {
                    int v = allExistingProductsChain.get(productsChain);
                    v++;
                    allExistingProductsChain.put(productsChain, v);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : allExistingProductsChain.entrySet()) {
            if (entry.getValue() < Constant.MIN_SUPPORT) {
                toRemove.add(entry.getKey());
            }
        }

        allExistingProductsChain.keySet().removeAll(toRemove);
        Check c=new Check();
        c.setCode("faul");
        List<Check> checksNew=new ArrayList<>();
        HashMap<String,Integer> items = new HashMap<>();
        if(i==Constant.numProdInCheck){
            c.setProducts(allExistingProductsChain);
            checksNew.add(c);
            return checksNew;
        }else{
        for (String productsChain : allExistingProductsChain.keySet()) {
            List<String> item = Arrays.asList(productsChain.split("\\s*,\\s*"));
            for(String s:item){
                items.put(s,0);
            }
        }
        c.setProducts(items);
        checksNew.add(c);
        return checksNew;
        }
    }
}
