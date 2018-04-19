import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;

public class TransportProblem {

    public static final float max = Float.MAX_VALUE;

    public float [][] TransportTable;          //tabela ceny transportu jednej jednostki produktu od danego dostawcy do danego odbiorcy
    public int Providers;                      //ilość dostawcow
    public int Recipients;                     //ilość odbiorców
    public int[] PossessedAmount;              //ilość dobra w magazyniach poszczególnych dostawców
    public int[] Request;                      //zapotrzebowanie na dobro danego odbiorcy

    public int Demand;                          //Popyt
    public int Suply;                           //Podaż

    public float [] A;
    public float [] B;
    public int [][] Base;
    public float [][] Delta;



    //
    public TransportProblem( int Providers, int Recipients, int[] PossessedAmount, int [] Request, float [][] TransportTable)
    {
        this.Providers = Providers;
        this.Recipients = Recipients;

        Demand = 0;
        Suply = 0;
        for(int x : Request)
            Demand += x;
        for(int x : PossessedAmount)
            Suply += x;

        if(Demand > Suply)
        {
            this.Providers++;
        }
        else if (Demand < Suply)
        {
            this.Recipients++;
        }

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
            else //if(Demand > Suply && i < this.Providers)
            {
                this.PossessedAmount[i] = this.Demand - this.Suply;
            }
//            else
//            {
//                this.PossessedAmount[i] = 0;
//            }
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
            else //if(Demand < Suply && i < this.Recipients)
            {
                this.Request[i] = this.Suply - this.Demand;
            }
//            else
//            {
//                this.Request[i] = 0;
//            }
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
                    //this.TransportTable[i][j] = 0;
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
        if(this.Demand > this.Suply)
        {
            TSum = this.Demand;
        }
        else
        {
            TSum = this.Suply;
        }
        while (TSum > 0)
        {
            int TProvider = 0;
            int TRecipient = 0;
            boolean end = false;
            for(int i = 0; i < TPossessedAmount.length && !end; i++)
            {
                for (int j = 0; j < TRequest.length; j++)
                {
                    if (this.Base[i][j] == -1)
                    {
                        TProvider = i;
                        TRecipient = j;
                        end = true;
                        break;
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
                if(this.Base[i][j] < 0)
                {
                    this.Base[i][j] = 0;
                }
                if(this.TransportTable[i][j] == this.max)
                {
                    this.TransportTable[i][j] = 0;
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
                else
                {
                    this.Delta[i][j] = -this.max;
                }
            }
        }
    }

    public Boolean Check() {
        //Poszukiwanie największej ujemnej wartości
        Boolean work = false;
        int TProvider = -1;
        int TRecipient = -1;

        for (int i = 0; i < this.Providers; i++) {
            for (int j = 0; j < this.Recipients; j++) {
                if (this.Delta[i][j] < 0 && this.Delta[i][j] > -this.max) {
                    if (TProvider > -1 && TRecipient > -1) {
                        if (this.Delta[i][j] < this.Delta[TProvider][TRecipient]) {
                            TProvider = i;
                            TRecipient = j;
                        }
                    }
                    else {
                        TProvider = i;
                        TRecipient = j;
                    }
                }
            }
        }
        //Gdy istnieja ujemne wartosci
        if (TProvider > -1 && TRecipient > -1)
        {
            int RowCount[] = new int[this.Providers];
            int ColumnCount[] = new int[ this.Recipients];

            int ChangeMatrix[][] = new int[ this.Providers][this.Recipients];
            for (int i = 0; i < this.Providers; i++)
            {
                RowCount[i] = 0;
                for(int j = 0; j < this.Recipients; j++)
                {
                    ColumnCount[j] = 0;
                    if(this.Base[i][j] > 0)
                        ChangeMatrix[i][j] = 0;
                    else
                        ChangeMatrix[i][j] = 2; //Zablokowane komórki
                }
            }
            ChangeMatrix[TProvider][TRecipient] = 1;

            RowCount[TProvider]++;
            ColumnCount[TRecipient]++;

            boolean loopSearch = true;
            int direction = 0;
            ArrayList<Integer> RootRow = new ArrayList<Integer>();
            ArrayList<Integer> RootColumn = new ArrayList<Integer>();

            RootRow.add(new Integer(TProvider));
            RootColumn.add(new Integer(TRecipient));

            int ActualRow = TProvider;
            int ActualColumn = TRecipient;
            int TryCount = 0;
            int Add = 0;
            while(loopSearch)
            {
                int NewRow = -1;
                int NewColumn = -1;
                if(direction == 0)
                {
                    NewColumn = ActualColumn;
                    for(int i = ActualRow; i < this.Providers; i++)
                    {
                        if((ChangeMatrix[i][NewColumn] == 0) && (RowCount[i] < 2) && (ColumnCount[NewColumn] < 2))
                        {
                            NewRow = i;
                            break;
                        }
                    }
                    direction++;
                }
                else if(direction == 1)
                {
                    NewRow = ActualRow;
                    for(int j = ActualColumn; j < this.Recipients; j++)
                    {
                        if((ChangeMatrix[NewRow][j]) == 0 && (ColumnCount[j] < 2) && (RowCount[NewRow] < 2))
                        {
                            NewColumn = j;
                            break;
                        }

                    }
                    direction++;
                }
                else if(direction == 2)
                {
                    NewColumn = ActualColumn;
                    for(int i = 0; i < ActualRow; i++)
                    {
                        if((ChangeMatrix[i][NewColumn] == 0) && (RowCount[i] < 2) && (ColumnCount[NewColumn] < 2))
                        {
                            NewRow = i;
                            break;
                        }
                    }
                    direction++;
                }
                else
                {
                    NewRow = ActualRow;
                    for(int j = 0; j < ActualColumn ; j++)
                    {
                        if((ChangeMatrix[NewRow][j] == 0) && (ColumnCount[j] < 2) && (RowCount[NewRow] < 2)){
                            NewColumn = j;
                            break;
                        }
                    }
                    direction = 0;
                }
                //Czy znaleziono nowy punkt
                if(NewRow == -1 || NewColumn == -1)
                {
                    direction++;
                    if(direction > 3)
                    {
                        direction = 0;
                    }
                    TryCount++;
                    if((TryCount == 3)&&(RootRow.size() == 1))
                    {
                        direction = 1;
                        Add=1;
                        TryCount = 0;
                    }
                    if(TryCount == 3)
                    {
                        ChangeMatrix[ActualRow][ActualColumn] = 2;
                        RowCount[ActualRow]--;
                        ColumnCount[ActualColumn]--;

                        RootRow.remove(RootRow.size() - 1);
                        RootColumn.remove(RootColumn.size() - 1);

                        if(RootColumn.size() <= 0 && RootColumn.size() <= 0)
                        {
                            break;
                        }

                        ActualRow = RootRow.get(RootRow.size() - 1);
                        ActualColumn = RootColumn.get(RootColumn.size() - 1);
                        direction = (RootRow.size() + 1 + Add) % 2;
                        TryCount = 0;
                    }


                    continue;
                }
                else
                {
                    ChangeMatrix[NewRow][NewColumn] = ChangeMatrix[ActualRow][ActualColumn] * -1;

                    RootRow.add(new Integer(NewRow));
                    RootColumn.add(new Integer(NewColumn));

                    ActualRow = NewRow;
                    ActualColumn = NewColumn;

                    RowCount[NewRow]++;
                    ColumnCount[NewColumn]++;
                    TryCount = 0;
                    int x = 0;
                }
                //Sprawdzanie czy utworzono petle
                loopSearch = false;

                int Sum = 0;
                for (int i = 0; i < this.Providers; i++)
                {
                    Sum += RowCount[i];
                    if(RowCount[i] != 0 && RowCount[i] != 2) loopSearch = true;
                }
                if(Sum < 4)
                {
                    loopSearch = true;
                }

                Sum = 0;
                for (int j = 0; j < this.Recipients; j++)
                {
                    Sum += ColumnCount[j];
                    if(ColumnCount[j] != 0 && ColumnCount[j] != 2) loopSearch = true;
                }
                if(Sum < 4)
                {
                    loopSearch = true;
                }
            }
            if((RootRow.size() >= 4) && (RootColumn.size() >= 4))
            {
                int val = Integer.MAX_VALUE;
                for(int i = 0; i < RootColumn.size(); i++ )
                {
                    int x = RootRow.get(i);
                    int y = RootColumn.get(i);
                    if((this.Base[x][y] > 0)&& (ChangeMatrix[x][y] == -1) &&(this.Base[x][y] < val))
                    {
                        val = this.Base[x][y];
                    }
                }
                for(int i = 0; i < RootColumn.size(); i++ )
                {
                    int x = RootRow.get(i);
                    int y = RootColumn.get(i);
                    this.Base[x][y] += ChangeMatrix[x][y] * val;
                }
                work = true;
            }
        }
        return work;
    }


}




//            int     TA = -1,
//                    TB = -1;
//            for(int i = 0; i < this.Providers; i++)
//            {
//                for(int j = 0; j < this.Recipients; j++)
//                {
//                    if(i != TProvider && j != TRecipient)
//                    {
//                        if(this.Delta[i][j] == -this.max && this.Delta[TProvider][j] == -this.max && this.Delta[i][TRecipient] == -this.max)
//                        {
//                            TA = i;
//                            TB = j;
//                        }
//                    }
//                }
//            }
//            if(TRow > -1 && TColumn > -1)
//            {
//                work = true;
//                int val = this.Base[TRow][TColumn];
//                if( val > this.Base[TProvider][TColumn]) val = this.Base[TProvider][TColumn];
//                else if( val > this.Base[TRow][TRecipient]) val = this.Base[TRow][TRecipient];
//
//                if (this.Base[TProvider][TRecipient] < 1) this.Base[TProvider][TRecipient] = val;
//                else this.Base[TProvider][TRecipient] += val;
//                this.Base[TRow][TColumn] += val;
//                this.Base[TProvider][TColumn] -= val;
//                if(this.Base[TProvider][TColumn] == 0)
//                {
//                    this.Base[TProvider][TColumn] = -1;
//                }
//                this.Base[TRow][TRecipient] -= val;
//                if(this.Base[TRow][TRecipient] == 0)
//                {
//                    this.Base[TRow][TRecipient] = -1;
//                }
//            }