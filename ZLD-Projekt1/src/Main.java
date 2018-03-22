public class Main {
    public static void main(String []args)
    {



        int Odbiorcy = 3;
        int Dostawcy = 4;

        int [] IloscWMagazynie = { 20, 20, 20, 10};
        int [] Zapotrzebowanie = { 25, 28, 17};

        float [][] Tab = {  {2.f, 5.f ,4.f},
                            {1.f, 3.f, 6.f},
                            {2.f, 2.f, 7.f}};


        TransportProblem p = new TransportProblem(Dostawcy, Odbiorcy, IloscWMagazynie, Zapotrzebowanie, Tab);
        p.Solve();
        int x = 0;
        System.out.println("Hello World"); // prints Hello World
    }
}
