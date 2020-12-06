public class Hromosoma {
    String gen;
    int fitnes;

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public int getFitnes() {
        return fitnes;
    }

    public void setFitnes(int fitnes) {
        this.fitnes = fitnes;
    }

    @Override
    public String toString() {
        return "Hromosoma{" +
                "gen='" + gen + '\'' +
                ", fitnes=" + fitnes +
                '}'+System.lineSeparator();
    }
}
