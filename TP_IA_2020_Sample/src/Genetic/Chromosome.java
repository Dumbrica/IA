package Genetic;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chromosome implements Comparable<Chromosome> {
    private List<Gene> genes;
    private UIConfiguration uiConf;

    public Chromosome(UIConfiguration uiConf) {
        this.uiConf = uiConf;
        do {
            this.genes = generateGeneList();
        }while (this.genes == null);


    }

    //Deep copy
    public Chromosome(Chromosome other) {
        this.genes = other.genes;
        this.uiConf = other.uiConf;
    }

    public Chromosome(List<Gene> genes, UIConfiguration uiConf) {
        this.genes = genes;
        this.uiConf = uiConf;
    }



    //Mutar cromossoma

    public Chromosome mutate(int mutationRate){

        //Deep Copy

        Gene start = new Gene(uiConf.getStart(), uiConf); //Ponto inicial do Mapa
        Gene end = new Gene(uiConf.getEnd(), uiConf);//Ponto final do Mapa

        //Variavel que determina se o caminho é válido
        List<Gene> caminho = new ArrayList<>();//Lista com o caminho
        caminho.add(start);
        boolean caminhoValido;
        int maxIt = 0;
        do {
            caminho = new ArrayList<>();
            caminho.add(start);
            caminhoValido = false;
            int i = 1;
            do {

                IPoint ponto =  genes.get(i).getPoint();
                int x = 0;
                int y = 0;

                if (new Random().nextDouble() >= 0.5) {

                    x = ponto.getX() -  (ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    y = ponto.getY() - ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate);
                    //System.out.println(ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    //  System.out.println("Old x: " + ponto.getX() + "| New x:" + x);
                }else{
                    x = ponto.getX() + (ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    y = ponto.getY() + ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate);
                }


                IPoint novoPonto = new Point(x, y);
                Gene novoGene = new Gene(novoPonto, uiConf);
                Gene geneAnterior = caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, novoGene)) {
                    caminho.add(novoGene);
                    if (!intersect(novoGene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                        break;
                    }
                }else if(!intersect(geneAnterior,genes.get(i))) {
                    caminho.add(genes.get(i));
                    if (!intersect(end, genes.get(i))) {
                        caminho.add(end);
                        caminhoValido = true;
                        break;
                    }
                }

                i++;
            }while(i < genes.size());// && caminho.size() <= ConfAG.max_pontos !caminhoValido &&

        }while (caminho.size() > ConfAG.max_pontos);

        if (caminho.size() < ConfAG.min_pontos || !caminhoValido) {
            return null;
        } else {
            return new Chromosome(caminho,uiConf);
        }


    }

    /*
    public Chromosome mutate(int mutationRate){

        //Deep Copy

        Gene start = new Gene(uiConf.getStart(), uiConf); //Ponto inicial do Mapa
        Gene end = new Gene(uiConf.getEnd(), uiConf);//Ponto final do Mapa

        //Variavel que determina se o caminho é válido
        List<Gene> caminho = new ArrayList<>();//Lista com o caminho
        caminho.add(start);
        boolean caminhoValido;


            caminho = new ArrayList<>();
            caminho.add(start);
            caminhoValido = false;
            int i = 1;
            do {

                IPoint ponto =  genes.get(i).getPoint();
                int x = 0;
                int y = 0;

                if (new Random().nextDouble() >= 0.5) {

                    x = ponto.getX() +  (ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    y = ponto.getY() - ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate);
                    //System.out.println(ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    //  System.out.println("Old x: " + ponto.getX() + "| New x:" + x);
                }else{
                    x = ponto.getX() - (ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate));
                    y = ponto.getY() + ThreadLocalRandom.current().nextInt(-mutationRate, mutationRate);
                }


                IPoint novoPonto = new Point(x, y);
                Gene novoGene = new Gene(novoPonto, uiConf);
                Gene geneAnterior = caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, novoGene)) {
                    caminho.add(novoGene);
                    if (!intersect(novoGene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }else if(!intersect(geneAnterior,genes.get(i))) {
                    caminho.add(genes.get(i));
                    if (!intersect(end, genes.get(i))) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }
                i++;
            }while(!caminhoValido && i < genes.size());// && caminho.size() <= ConfAG.max_pontos


        if (caminho.size() < ConfAG.min_pontos || !caminhoValido) {
            return null;
        } else {
            return new Chromosome(caminho,uiConf);
        }

    }*/



    public Chromosome cross(Chromosome other){

        //Deep Copy

        Gene start = new Gene(uiConf.getStart(), uiConf); //Ponto inicial do Mapa
        Gene end = new Gene(uiConf.getEnd(), uiConf);//Ponto final do Mapa

        //Variavel que determina se o caminho é válido
        List<Gene> caminho;//Lista com o caminho
        boolean caminhoValido = false;

        int min = Math.min(genes.size(),other.genes.size());
        do {
            caminho = new ArrayList<>();
            caminho.add(start);
            caminhoValido = false;
            int i = 1;
            do {
                IPoint ponto =  genes.get(i).getPoint();
                IPoint otherPonto = other.genes.get(i).getPoint();

                int x = (ponto.getX() + otherPonto.getX())/2;
                int y = (ponto.getY() + otherPonto.getY()) /2;

                //int x = ponto.getX();
                //int y = ponto.getY();

                IPoint novoPonto = new Point(x, y);
                Gene novoGene = new Gene(novoPonto, uiConf);
                Gene geneAnterior = caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, novoGene)) {
                    caminho.add(novoGene);
                    if (!intersect(novoGene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                        break;
                    }
                }else if(!intersect(geneAnterior,genes.get(i))) {
                    caminho.add(genes.get(i));
                    if (!intersect(end, genes.get(i))) {
                        caminho.add(end);
                        caminhoValido = true;
                        break;
                    }
                }
                i++;
            }while ( i < min); //!caminhoValido &&
            //i < genes.size() && i < other.genes.size()
            //caminho.size() <= ConfAG.max_pontos &&
            //System.out.println("yooooo");
        }while (caminho.size() > ConfAG.max_pontos);


        if (caminho.size() < ConfAG.min_pontos || !caminhoValido) {
            return null;
        } else {
            return new Chromosome(caminho,uiConf);
        }

    }

    /*
    public Chromosome cross(Chromosome other){

        //Deep Copy

        Gene start = new Gene(uiConf.getStart(), uiConf); //Ponto inicial do Mapa
        Gene end = new Gene(uiConf.getEnd(), uiConf);//Ponto final do Mapa

        //Variavel que determina se o caminho é válido
        List<Gene> caminho;//Lista com o caminho
        boolean caminhoValido = false;

        int min = Math.min(genes.size(),other.genes.size());


            caminho = new ArrayList<>();
            caminho.add(start);
            caminhoValido = false;
            int i = 1;
            do {
                IPoint ponto =  genes.get(i).getPoint();
                IPoint otherPonto = other.genes.get(i).getPoint();

                int x = (ponto.getX() + otherPonto.getX())/2;
                int y = (ponto.getY() + otherPonto.getY()) /2;

                //int x = ponto.getX();
                //int y = ponto.getY();

                IPoint novoPonto = new Point(x, y);
                Gene novoGene = new Gene(novoPonto, uiConf);
                Gene geneAnterior = caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, novoGene)) {
                    caminho.add(novoGene);
                    if (!intersect(novoGene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }else if(!intersect(geneAnterior,genes.get(i))) {
                    caminho.add(genes.get(i));
                    if (!intersect(end, genes.get(i))) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }
                //System.out.println(i);
                i++;
            }while(!caminhoValido &&  i < min);
            //i < genes.size() && i < other.genes.size()
            //caminho.size() <= ConfAG.max_pontos &&

        if (caminho.size() < ConfAG.min_pontos || !caminhoValido) {
            return null;
        } else {
            return new Chromosome(caminho,uiConf);
        }

    }*/



    public boolean intersect(Gene gene,Gene otherGene){
        IPoint point = gene.getPoint();
        IPoint otherPoint = otherGene.getPoint();

        List<Rectangle> obstacles = uiConf.getObstacles();
        for(int i= 0; i < obstacles.size(); i++){
            Line2D line = new Line2D.Float(point.getX(), point.getY(), otherPoint.getX(), otherPoint.getY());
            if(line.intersects(obstacles.get(i))){
                return true;//retorna true se houver colisão
            }
        }

        return false;//retorna false se não houver colisão
    }

    /*
    public double getFitness(){
        double fitness = 0;
        for(int i = 0; i + 1 < genes.size(); i++){
            Gene gene =  genes.get(i);
            Gene proxGene = genes.get(i+1);

            int startX = gene.getPoint().getX();
            int startY = gene.getPoint().getY();

            int endX = proxGene.getPoint().getX();
            int endY = proxGene.getPoint().getY();

            double caminho = Math.sqrt(Math.pow((endX - startX), 2) + Math.pow((endY - startY), 2));
            //
            fitness = fitness + caminho;
        }
        fitness += (genes.size()*10);
        //fitness = fitness + (it*3);
        return fitness;
    }*/

    public double getFitness(){
        double fitness = 0;
        for(int i = 0; i + 1 < genes.size(); i++){
            Gene gene =  genes.get(i);
            Gene proxGene = genes.get(i+1);

            int startX = gene.getPoint().getX();
            int startY = gene.getPoint().getY();

            int endX = proxGene.getPoint().getX();
            int endY = proxGene.getPoint().getY();

            double caminho = Math.sqrt(Math.pow((endX - startX), 2) + Math.pow((endY - startY), 2));//
            fitness = fitness + caminho;
        }
        //fitness += (genes.size() * 100);

        //fitness = fitness + (it*3);
        return fitness;
    }



    public List<Gene> generateGeneList(){
        List<Gene> caminho = new ArrayList<>();
        Gene start = new Gene(uiConf.getStart(), uiConf);
        Gene end = new Gene(uiConf.getEnd(), uiConf);

        do {
            caminho = new ArrayList<>();
            caminho.add(start);
            boolean caminhoValido = false;
            do {
                Gene gene = new Gene(uiConf);
                Gene geneAnterior =  caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, gene)) {
                    caminho.add(gene);
                    if (!intersect(gene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }

            } while (!caminhoValido && caminho.size() <= ConfAG.max_pontos);

        }while (caminho.size() > ConfAG.max_pontos) ;
        if (caminho.size() < ConfAG.min_pontos) {
            return null;
        } else {
            return caminho;
        }
    }


    /*
    public List<Gene> generateGeneList(){
        List<Gene> caminho = new ArrayList<>();
        Gene start = new Gene(uiConf.getStart(), uiConf);
        Gene end = new Gene(uiConf.getEnd(), uiConf);

        //do {
            caminho = new ArrayList<>();
            caminho.add(start);
            boolean caminhoValido = false;
            do {
                Gene gene = new Gene(uiConf);
                Gene geneAnterior = (Gene) caminho.get(caminho.size() - 1);
                if (!intersect(geneAnterior, gene)) {
                    caminho.add(gene);
                    if (!intersect(gene, end)) {
                        caminho.add(end);
                        caminhoValido = true;
                    }
                }
            } while (!caminhoValido); //&& caminho.size() <= ConfAG.max_pontos

        //}while (caminho.size() > ConfAG.max_pontos);

        if (caminho.size() < ConfAG.min_pontos) {
            System.out.println("here");
            return null;

        } else {
            return caminho;
        }
    }*/



    public List<Gene> getGenes() {
        return this.genes;
    }

    public float getDistance(){

        return 0;
    }

    @Override
    public int compareTo(Chromosome o) {
        if (o.getFitness() < this.getFitness())
            return 1;
        else if (o.getFitness() > this.getFitness())
            return -1;
        else return 0;
    }
}
