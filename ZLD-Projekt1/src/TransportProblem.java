import com.sun.org.apache.xpath.internal.operations.Bool;

public class TransportProblem {

    public static final int max = Integer.MAX_VALUE;

    public float [][] TransportTable;          //tabela ceny transportu jednej jednostki produktu od danego dostawcy do danego odbiorcy
    public int Providers;                      //ilość dostawcow
    public int Recipients;                     //ilość odbiorców
    public int[] PossessedAmount;              //ilość dobra w magazyniach poszczególnych dostawców
    public int[] Request;                      //zapotrzebowanie na dobro danego odbiorcy

    public float [] A;
    public float [] B;
    public int [][] Base;
    public float [][] Delta;
    //
    public TransportProblem( int Providers, int Recipients, int[] PossessedAmount, int [] Request, float [][] TransportTable)
    {
        this.Providers = Providers;
        this.Recipients = Recipients;
        if(this.Providers < 0 )  this.Providers = 1;
        if(this.Recipients < 0 )  this.Recipients = 1;

        this.A = new float[this.Providers];
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
        this.B = new float[this.Recipients];
        this.Request = new int[this.Recipients];
        for(int i = 0; i < this.Recipients; i++)
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
        this.Base = new int [this.Providers][this.Recipients];
        this.Delta = new float [this.Providers][this.Recipients];

        this.TransportTable = new float [this.Providers][this.Recipients];
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
                this.Base[i][j] = -1;
            }
        }
    }

    void Solve()
    {
        GenerateBase();
        DualVariables();
        CritVariables();
        while(Check())
        {
            DualVariables();
            CritVariables();
        }
    }

    public void GenerateBase()
    {
        int [] TPossessedAmount = this.PossessedAmount.clone();
        int [] TRequest = this.Request.clone();

        int TSum = 0;
        for(int i = 0 ; i < TPossessedAmount.length; i++ )
        {
            TSum += TPossessedAmount[i];
        }
        while (TSum > 0)
        {
            int TProvider = 0;
            int TRecipient = 0;
            for(int i = 0; i < TPossessedAmount.length; i++)
            {
                for (int j = 0; j < TRequest.length; j++)
                {
                    if (this.Base[i][j] == -1)
                    {
                        TProvider = i;
                        TRecipient = j;
                    }
                }
            }
            for(int i = 0; i < this.Providers; i++)
            {
                for(int j = 0; j < this.Recipients; j++)
                {
                    if((this.TransportTable[TProvider][TRecipient] > this.TransportTable[i][j]) && (this.Base[i][j] < 1))
                    {
                        if(TPossessedAmount[i] > 0 && TRequest[j] > 0)
                        {
                            TProvider = i;
                            TRecipient = j;
                        }
                    }
                }
            }
            this.Base[TProvider][TRecipient] = 0;
            int val = TPossessedAmount[TProvider];
            if(TPossessedAmount[TProvider] > TRequest[TRecipient]) val = TRequest[TRecipient];

            TSum -= val;
            TPossessedAmount[TProvider] -= val;
            TRequest[TRecipient] -= val;
            if(val > 0)
                this.Base[TProvider][TRecipient] = val;
        }
        for(int i = 0; i < this.Providers; i++)
        {
            for(int j = 0; j < this.Recipients; j++)
            {
                if(this.Base[i][j] == -2)
                {
                    this.Base[i][j] = -1;
                }
            }
        }

//        for(int j = 0; j < this.Recipients; j++)
//        {
//            Boolean [] used = new Boolean[this.Providers];
//            for(int i = 0; i < this.Providers; i++)
//            {
//                used[i] = false;
//            }
//            int min_index = 0;
//            for(int i = 0; i < this.Providers; i++)
//            {
//                for(int a = 0; a < this.Providers; a++)
//                {
//                    if(!used[a])
//                    {
//                        min_index = a;
//                        break;
//                    }
//                }
//                for(int a = 0; a < this.Providers; a++)
//                {
//                    if ((this.TransportTable[a][j] < this.TransportTable[min_index][j]) && !used[a]) min_index = a;
//                }
//                used[min_index]=true;
//                int minus = TPossessedAmount[min_index];
//                if(minus > TRequest[j]) minus = TRequest[j];
//
//                TPossessedAmount[min_index] -= minus;
//                TRequest[j] -= minus;
//                this.Base[min_index][j] += minus;
//                if(TRequest[j] == 0) break;
//            }
//        }
        int x = 0;
    }

    public void DualVariables()
    {
        Boolean work = true;
        Boolean [] TA = new Boolean[this.Providers];
        for(int i = 0; i < this.Providers; i++)
        {
            TA[i] = false;
        }
        Boolean [] TB = new Boolean[this.Recipients];
        for(int j = 0; j < this.Recipients; j++)
        {
            TB[j] = false;
        }
        this.A[0] = 0;
        TA[0] = true;
        while(work)
        {
            for(int i = 0; i < this.Providers; i++)
            {
                for(int j = 0; j < this.Recipients; j++)
                {
                    if(this.Base[i][j] > 0)
                    {
                        if(!TA[i] && TB[j])
                        {
                            TA[i] = true;
                            this.A[i] = this.TransportTable[i][j] - this.B[j];
                        }
                        else if(TA[i] && !TB[j])
                        {
                            TB[j] = true;
                            this.B[j] = this.TransportTable[i][j] - this.A[i];
                        }
                    }
                }
            }
            work = false;
            for(int i = 0; i < this.Providers; i++)
            {
                if(!TA[i])
                {
                    work = true;
                }
            }
            for(int j = 0; j < this.Recipients; j++)
            {
                if(!TB[j])
                {
                    work = true;
                }
            }
        }
    }

    public void CritVariables()
    {
        for(int i = 0; i < this.Providers; i++)
        {
            for(int j = 0; j < this.Recipients; j++)
            {
                if(this.Base[i][j] < 1)
                {
                    this.Delta[i][j] = this.TransportTable[i][j] - this.A[i] - this.B[j];
                }
            }
        }
    }

    public Boolean Check()
    {
        Boolean work = false;
        int TProvider = -1;
        int TRecipient = -1;

        for(int i = 0; i < this.Providers; i++)
        {
            for(int j = 0; j < this.Recipients; j++)
            {
                if(this.Delta[i][j] < 0)
                {
                    if(TProvider > -1 && TRecipient > -1)
                    {
                        if(this.Delta[i][j] < this.Delta[TProvider][TRecipient])
                        {
                            TProvider = i;
                            TRecipient = j;
                        }
                    }
                    else
                    {
                        TProvider = i;
                        TRecipient = j;
                    }
                }
            }
        }

        if(TProvider > -1 && TRecipient > -1)
        {
            int     TA = -1,
                    TB = -1;
            for(int i = 0; i < this.Providers; i++)
            {
                for(int j = 0; j < this.Recipients; j++)
                {
                    if(i != TProvider && j != TRecipient)
                    {
                        if(this.Delta[i][j] == 0 && this.Delta[TProvider][j] == 0 && this.Delta[i][TRecipient] == 0)
                        {
                            TA = i;
                            TB = j;
                        }
                    }
                }
            }

            if(TA > -1 && TB > -1)
            {
                work = true;
                int val = this.Base[TA][TB];
                if( val > this.Base[TProvider][TB]) val = this.Base[TProvider][TB];
                else if( val > this.Base[TA][TRecipient]) val = this.Base[TA][TRecipient];

                if (this.Base[TProvider][TRecipient] < 1) this.Base[TProvider][TRecipient] = val;
                else this.Base[TProvider][TRecipient] += val;
                this.Base[TA][TB] += val;
                this.Base[TProvider][TB] -= val;
                if(this.Base[TProvider][TB] == 0)
                {
                    this.Base[TProvider][TB] = -1;
                }
                this.Base[TA][TRecipient] -= val;
                if(this.Base[TA][TRecipient] == 0)
                {
                    this.Base[TA][TRecipient] = -1;
                }
            }
        }
        return work;
    }
}
