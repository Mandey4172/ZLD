public class Main {
    public static void main(String []args)
    {



//        int Odbiorcy = 3;
//        int Dostawcy = 4;
//
//        int [] IloscWMagazynie = { 20, 20, 20, 10};
//        int [] Zapotrzebowanie = { 25, 28, 17};
//
//        float [][] Tab = {  {2.f, 5.f ,4.f},
//                            {1.f, 3.f, 6.f},
//                            {2.f, 2.f, 7.f}};
//        int Odbiorcy = 3;
//        int Dostawcy = 3;
//
//        int [] IloscWMagazynie = { 20, 30, 20};
//        int [] Zapotrzebowanie = { 25, 28, 17};
//
//        float [][] Tab = {  {2.f, 5.f ,4.f},
//                            {1.f, 3.f, 6.f},
//                            {2.f, 2.f, 7.f}};
        int Odbiorcy = 3;
        int Dostawcy = 3;

        int [] IloscWMagazynie = { 32, 19, 27};
        int [] Zapotrzebowanie = { 20, 40, 40};

        float [][] Tab = {  {1.f, 4.f ,3.f},
                {4.f, 5.f, 1.f},
                {2.f, 6.f, 5.f}};

        TransportProblem p = new TransportProblem(Dostawcy, Odbiorcy, IloscWMagazynie, Zapotrzebowanie, Tab);
        //p.GenerateBase();
        p.Solve();
        int x = 0;
        System.out.println("Hello World"); // prints Hello World
    }
}
