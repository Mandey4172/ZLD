public class Main {
    public static void main(String []args)
    {


        int Dostawcy = 3;
        int Odbiorcy = 3;

        int [] IloscWMagazynie = { 20, 30, 20};
        int [] Zapotrzebowanie = { 25, 28, 17};

        float [][] Tab = {  {2.f, 5.f ,4.f},
                            {1.f, 3.f, 6.f},
                            {2.f, 2.f, 7.f}};
        TransportProblem p = new TransportProblem(Dostawcy, Odbiorcy, IloscWMagazynie, Zapotrzebowanie, Tab);
        p.GenerateBase();
        System.out.println("Hello World"); // prints Hello World
    }
}
