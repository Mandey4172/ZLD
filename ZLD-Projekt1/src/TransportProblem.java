public class TransportProblem {

    public static final int max = Integer.MAX_VALUE;

    public float [][] TransportTable;          //tabela ceny transportu jednej jednostki produktu od danego dostawcy do danego odbiorcy
    public int Providers;                      //ilość dostawcow
    public int Recipient;                      //ilość odbiorców
    public int[] PossessedAmount;              //ilość dobra w magazyniach poszczególnych dostawców
    public int[] Request;                      //zapotrzebowanie na dobro danego odbiorcy

    public int [] A;
    public int [] B;
    public int [][] Base;
    public int [][] Delta;
    //
    public TransportProblem( int Providers, int Recipient, int[] PossessedAmount, int [] Request, float [][] TransportTable)
    {
        this.Providers = Providers;
        this.Recipient = Recipient;
        if(this.Providers < 0 )  this.Providers = 1;
        if(this.Recipient < 0 )  this.Recipient = 1;

        this.A = new int[this.Providers];
        this.PossessedAmount = new int[this.Providers];
        for(int i = 0; i < this.Providers; i++)
        {
            this.A[i] = -1;
            if( i < PossessedAmount.length)
            {
                this.PossessedAmount[i] = PossessedAmount[i];
            }
            else
            {
                this.PossessedAmount[i] = 0;
            }
        }
        this.B = new int[this.Recipient];
        this.Request = new int[this.Recipient];
        for(int i = 0; i < this.Recipient; i++)
        {
            this.B[i] = -1;
            if( i < Request.length)
            {
                this.Request[i] = Request[i];
            }
            else
            {
                this.Request[i] = 0;
            }
        }
        this.Base = new int [this.Providers][this.Recipient];
        this.Delta = new int [this.Providers][this.Recipient];

        this.TransportTable = new float [this.Providers][this.Recipient];
        for(int i = 0; i < this.TransportTable.length; i++)
        {
            for(int j = 0; j < this.TransportTable[i].length; j++)
            {
                if((i < TransportTable.length)&&(j < TransportTable[i].length))
                {
                    this.TransportTable[i][j] = TransportTable[i][j];
                }
                else
                {
                    this.TransportTable[i][j] = TransportProblem.max;
                }
            }
        }
    }


    public void GenerateBase()
    {
        int [] TPossessedAmount = this.PossessedAmount.clone();
        int [] TRequest = this.Request.clone();

        for(int j = 0; j < this.Recipient; j++)
        {
            Boolean [] used = new Boolean[this.Providers];
            for(int i = 0; i < this.Providers; i++)
            {
                used[i] = false;
            }
            int min_index = 0;
            for(int i = 0; i < this.Providers; i++)
            {
                for(int a = 0; a < this.Providers; a++)
                {
                    if(!used[a])
                    {
                        min_index = a;
                        break;
                    }
                }
                for(int a = 0; a < this.Providers; a++)
                {
                    if ((this.TransportTable[a][j] < this.TransportTable[min_index][j]) && !used[a]) min_index = a;
                }
                used[min_index]=true;
                int minus = TPossessedAmount[min_index];
                if(minus > TRequest[j]) minus = TRequest[j];

                TPossessedAmount[min_index] -= minus;
                TRequest[j] -= minus;
                this.Base[min_index][j] += minus;
                if(TRequest[j] == 0) break;
            }
        }
    }
}
