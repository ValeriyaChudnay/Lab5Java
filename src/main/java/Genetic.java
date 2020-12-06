import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Genetic {
    public static List<String> allCodes;
    public static Random r = new Random();
    public static List<Check> checks;

    static {
        try {
            checks = FileReaderService.readFromExcel("src/main/resources/SmalSet.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> getAllExistingCode(List<Check> checks) {
        Set<String> allCodes = new HashSet<>();
        for (Check c : checks) {
            allCodes.addAll(c.getProducts().keySet());
        }
        return Arrays.asList(allCodes.toArray(new String[0]));
    }

    public static String getRandomProduct(List<String> all) {
        return all.get(r.nextInt(all.size()));
    }

    public static List<String> gen = new ArrayList<>();

    public static String getRandomGen() {
        Set<String> genSet = new HashSet<>();
        if (genSet.size() == 0) {
            for (String s : allCodes) {
                List<String> items = Arrays.asList(s.split("\\s*,\\s*"));
                genSet.addAll(items);
            }
            gen = new ArrayList<>(Arrays.asList(genSet.toArray(new String[0])));
        }
        return gen.get(r.nextInt(allCodes.size()));
    }

    public static List<Hromosoma> generatePopulation(int N) {
        List<Hromosoma> pop = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            String prod = "";
            List<String> all;
            boolean normalGen = false;
            while (!normalGen) {
                prod = "";
                all = new ArrayList<>(allCodes);
                for (int j = 0; j < Constant.numProdInCheck; j++) {
                    String p = getRandomProduct(all);
                    prod += p + ",";
                    all.remove(p);
                }
                prod = prod.substring(0, prod.lastIndexOf(','));
                List<String> items = Arrays.asList(prod.split("\\s*,\\s*"));
                items = items.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
                prod = "";
                for (int j = 0; j < Constant.numProdInCheck; j++) {
                    prod += items.get(j) + ",";
                }
                prod = prod.substring(0, prod.lastIndexOf(','));
                normalGen = true;
                for (Hromosoma h : pop) {
                    if (h.getGen().equals(prod)) {
                        normalGen = false;
                    }
                }
            }
            Hromosoma h = new Hromosoma();
            h.setGen(prod);
            h.setFitnes(getFitnes(prod));
            pop.add(h);
        }
        return pop;
    }

    private static int getFitnes(String prod) {
        int numInCheck = 0;
        List<String> items = Arrays.asList(prod.split("\\s*,\\s*"));
        boolean chekNotFit;
        for (Check c : checks) {
            chekNotFit = true;
            for (String s : items) {
                if (!c.getProducts().containsKey(s)) {
                    chekNotFit = false;
                    break;
                }
            }
            if (chekNotFit) {
                numInCheck++;
            }
        }
        return numInCheck;
    }

    //дерутся 0 со 1 1 со 2 и тд. если не четное кол-во то последняя с предпоследней
    private static List<Hromosoma> FIGHT(List<Hromosoma> population) {
        Set<Hromosoma> winner = new HashSet<>();
        if (population.size() % 2 == 1) {
            population.add(population.get(r.nextInt(population.size())));
        }
        while (population.size() > 0) {
            int i = r.nextInt(population.size());
            int k = r.nextInt(population.size());
            while (i == k) {
                k = r.nextInt(population.size());
            }
            if (population.get(i).getFitnes() > population.get(k).getFitnes()) {
                winner.add(population.get(i));
            } else if (population.get(i).getFitnes() < population.get(k).getFitnes()) {
                winner.add(population.get(k));
            } else {
                if (r.nextInt() % 2 == 0) {
                    winner.add(population.get(i));
                } else {
                    winner.add(population.get(k));
                }
            }
            if (k > i) {
                population.remove(k);
                population.remove(i);
            } else {
                population.remove(i);
                population.remove(k);
            }

        }
        List<Hromosoma> win = new ArrayList<>(Arrays.asList(winner.toArray(new Hromosoma[0])));
         win = win.stream().sorted((x, y) -> y.getFitnes() - x.getFitnes()).collect(Collectors.toList());
        return win;
    }

    private static List<Hromosoma> CROSINGOVER(int n, List<Hromosoma> population) {
        List<Hromosoma> newPopulation = new ArrayList<>();
        if (population.size() % 2 == 1) {
            population.add(population.get(0));
        }
        int pointForDivide = 0;
        pointForDivide = Constant.numProdInCheck / 2;
        while (newPopulation.size() < n) {
            List<String> newHromo = new ArrayList<>();
            int randomToCros1 = r.nextInt(population.size());
            int randomToCros2 = r.nextInt(population.size());
            while (randomToCros1 == randomToCros2) {
                randomToCros2 = r.nextInt(population.size());
            }
            List<String> items1 = new ArrayList<>(Arrays.asList(population.get(randomToCros1).getGen().split("\\s*,\\s*")));
            List<String> items2 = new ArrayList<>(Arrays.asList(population.get(randomToCros2).getGen().split("\\s*,\\s*")));
            String prod = "";

            for (int t = 0; t < pointForDivide; t++) {
                newHromo.add(items1.get(t));
            }
            for (int t = Constant.numProdInCheck - 1; t >= 0; t--) {
                if (!newHromo.contains(items2.get(t))) {
                    newHromo.add(items2.get(t));
                }
            }
            newHromo = newHromo.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            prod = "";
            try {
                newHromo.get(1);
            } catch (IndexOutOfBoundsException e) {
                System.out.println(newHromo);
            }
            for (int j = 0; j < Constant.numProdInCheck; j++) {
                prod += newHromo.get(j) + ",";
            }
            prod = prod.substring(0, prod.lastIndexOf(','));
            Hromosoma h = new Hromosoma();
            h.setGen(prod);
            h.setFitnes(getFitnes(prod));
            newPopulation.add(h);
        }
        newPopulation = newPopulation.stream().sorted((x, y) -> y.getFitnes() - x.getFitnes()).collect(Collectors.toList());
        return newPopulation;
    }

    private static List<Hromosoma> CROSINGOVER_TORNADO(int n, List<Hromosoma> population) {
        List<Hromosoma> newPopulation = new ArrayList<>();
        if (population.size() % 2 == 1) {
            population.add(population.get(0));
        }
        int pointForDivide = 0;
        pointForDivide = Constant.numProdInCheck / 2;
        while (newPopulation.size() < n) {
            List<String> newHromo = new ArrayList<>();
            int randomToCros1 = r.nextInt(population.size());
            int randomToCros2 = r.nextInt(population.size());
            while (randomToCros1 == randomToCros2) {
                randomToCros2 = r.nextInt(population.size());
            }
            List<String> items1 = new ArrayList<>(Arrays.asList(population.get(randomToCros1).getGen().split("\\s*,\\s*")));
            items1.addAll(new ArrayList<>(Arrays.asList(population.get(randomToCros2).getGen().split("\\s*,\\s*"))));

            String prod = "";

            while (newHromo.size() < Constant.numProdInCheck) {
                String nH = items1.get(r.nextInt(items1.size()));
                if (!newHromo.contains(nH)) {
                    newHromo.add(nH);
                }
            }
            newHromo = newHromo.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            prod = "";
            try {
                newHromo.get(1);
            } catch (IndexOutOfBoundsException e) {
                System.out.println(newHromo);
            }
            for (int j = 0; j < Constant.numProdInCheck; j++) {
                prod += newHromo.get(j) + ",";
            }
            prod = prod.substring(0, prod.lastIndexOf(','));
            Hromosoma h = new Hromosoma();
            h.setGen(prod);
            h.setFitnes(getFitnes(prod));
            newPopulation.add(h);
        }
        newPopulation = newPopulation.stream().sorted((x, y) -> y.getFitnes() - x.getFitnes()).collect(Collectors.toList());
        return newPopulation;
    }

    private static List<Hromosoma> MUTATION(List<Hromosoma> population) {
        for (Hromosoma h : population) {
            List<String> items = new ArrayList<>(Arrays.asList(h.getGen().split("\\s*,\\s*")));
            for (int j=0;j<items.size();j++) {
                if (r.nextDouble() <= Constant.mutation) {
                    String newG = getRandomGen();
                    while (items.contains(newG)) {
                        newG = getRandomGen();
                    }
                    items.remove(j);
                    items.add(j, newG);
                }
            }
            items = items.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            String prod = "";
            for (int j = 0; j < Constant.numProdInCheck; j++) {
                prod += items.get(j) + ",";
            }
            prod = prod.substring(0, prod.lastIndexOf(','));
            h.setGen(prod);
            h.setFitnes(getFitnes(prod));
        }
        population = population.stream().sorted((x, y) -> y.getFitnes() - x.getFitnes()).collect(Collectors.toList());
        return population;
    }

    public static void main(String[] args) throws IOException {
        allCodes = getAllExistingCode(checks);

        int N = (int) (checks.size() * Constant.percent);
        List<Hromosoma> population = generatePopulation(N);
        for (int i = 0; i < Constant.numOfGeneration; i++) {
            for (int j = 0; j < N; j++) {
                assert population != null;
                population = FIGHT(population);
                //  population = CROSINGOVER(N, population);
                population = CROSINGOVER_TORNADO(N, population);
            }
            MUTATION(population);
        }
        System.out.println(population);

//        for i=1 to i=Число Поколінь do
//            begin
//        for j=1 to j=N do
//            begin
//        Турнірний Відбір
//        Схрещування
//                end
//        Мутация
//                end

    }


}
