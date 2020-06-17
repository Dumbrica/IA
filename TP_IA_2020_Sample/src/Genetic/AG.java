package Genetic;

import impl.UIConfiguration;
import interf.IPoint;
import performance.Evaluate;
import viewer.PathViewer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AG {

    private int pop_size;
    private int pop_select;
    private  double mutation_rate;
    private int generation_limit;
    private int converence_limit;
    private int cross_limit;
    private int mutation_limit ;
    private int random_limit;
    private UIConfiguration uiConf;
    private double last_fitness_value = 0;
    private int convergence_counter = 0;
    private int iteration_counter = 0;
    Chromosome bestSolution;


    public AG( UIConfiguration uiConf) {
        this.uiConf = uiConf;
    }

    public List<Chromosome> init()
    {
        List<Chromosome> gen = Stream.generate(() -> new Chromosome(uiConf))
                .limit(ConfAG.pop_size)
                .collect(Collectors.toList());

        return gen;
    }


    public List<Chromosome> mutate(List<Chromosome> list) {
        List<Chromosome> mutPop = Stream.generate(() -> getChromosome(list).mutate(ConfAG.mutation_factor))
                .limit(ConfAG.mutation_limit)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return mutPop;
    }

    public Chromosome getChromosome(List<Chromosome> list){
        return list.get(new Random().nextInt(list.size()));
    }

    public List<Chromosome> cross(List<Chromosome> list) {
        List<Chromosome> mutPop = Stream.generate(() -> getChromosome(list).cross(getChromosome(list)))
                .limit(ConfAG.cross_limit)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return mutPop;
    }

    public Chromosome run() throws InterruptedException {
        //referência para a melhor solução encontrada, que será devolvida no final
        //Lista que guarda o fitness da melhor solução de cada geração, para o gráfico
        List<Double> best_fitness = new ArrayList<>();
        //Lista que guarda o fitness médio de cada geração, para o gráfico
        List<Double> avg_fitness = new ArrayList<>();

        //Lista que guarda a geração mais recente
        List<Chromosome> pop;

        //Criar primeira geração
        pop = init();



        Collections.sort(pop);
        bestSolution = pop.get(0);
        best_fitness.add(pop.get(0).getFitness());

        //PathViewer pv = new PathViewer(this.uiConf);
        //pv.paintPath(bestSolutionPoints(bestSolution));
        //int map_id = 1;


        //Evaluate eval = new Evaluate(ConfAG.pop_size,map_id,"omae wa mou shindeiru");

        while (iteration_counter < ConfAG.generation_limit  && convergence_counter < ConfAG.converence_limit) {

            System.out.println("Iteration: " + iteration_counter);

            //selecionar melhores soluções
            List<Chromosome> best = pop.stream().limit(ConfAG.pop_select).collect(Collectors.toList());
            List<Chromosome> filhos = new ArrayList<>();

            //Mutação
            filhos.addAll(mutate(best));
            //Cruzamento
            filhos.addAll(cross(best));

            //aleatórias
            filhos.addAll(Stream.generate(() -> new Chromosome(uiConf))
                    .limit(ConfAG.random_limit)
                    .collect(Collectors.toList()));

            //nova geração é resultado de acrescentar os melhores aos filhos por cruzamento e mutação
            pop = new ArrayList<>();
            pop.addAll(best);
            pop.addAll(filhos);

            Collections.sort(pop);
            best_fitness.add(pop.get(0).getFitness());

            if (last_fitness_value == pop.get(0).getFitness())
                convergence_counter++;
            else {
                convergence_counter = 0;
                last_fitness_value = pop.get(0).getFitness();
                bestSolution = pop.get(0);
            }

            //Thread.sleep(5000);
            //eval.addSolution(bestSolutionPoints(bestSolution),iteration_counter);
            iteration_counter++;
            System.out.println(bestSolution.getFitness());

        }



        /*
        if(eval.submit()){
            System.out.println("submetido");
        }else{
            System.out.println("erro");
        }*/


        //pv.paintPath(bestSolutionPoints(bestSolution));
        return bestSolution;
    }



    public List<IPoint> bestSolutionPoints(Chromosome bestSolutionL){

        List<Gene> geneList = bestSolutionL.getGenes();
        List<IPoint> points = new ArrayList<>();
        for (int i= 0; i < geneList.size(); i++) {
            points.add(geneList.get(i).getPoint());
        }
        return points;
    }

    public List<IPoint> getPoints(){

        List<Gene> geneList = bestSolution.getGenes();
        List<IPoint> points = new ArrayList<>();
        for (int i= 0; i < geneList.size(); i++) {
            points.add(geneList.get(i).getPoint());
        }
        return points;
    }

}
